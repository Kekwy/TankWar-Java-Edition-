package com.kekwy.tankwar.level;

import com.kekwy.tankwar.TankWar;
import com.kekwy.tankwar.gamemap.GameMap;
import com.kekwy.tankwar.gamescenes.PlayScene;
import com.kekwy.tankwar.tank.Bullet;
import com.kekwy.tankwar.tank.EnemyTank;
import com.kekwy.tankwar.tank.Tank;
import com.kekwy.tankwar.util.TankWarUtil;
import javafx.scene.media.AudioClip;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Level {

	private final GameMap GAME_MAP;
	private final int MAX_COUNT_SAME_TIME;
	private final int ENEMY_COUNT;
	private final int ENEMY_HP;
	private final int ENEMY_SPEED;
	private final int ENEMY_ATK;
	private final int BORN_ENEMY_INTERVAL;
	private final int PLAYER_HP;
	private final int PLAYER_SPEED;
	private final int PLAYER_ATK;
	private final int BULLET_SPEED;
	private final boolean RECOVER;
	private final boolean isFinalLevel;
	private final List<AudioClip> bgmList = new ArrayList<>();
	private PlayScene parent;

	private Thread currentThread;

	public Level(String levelConfigFile) {
		Properties props = TankWarUtil.loadProperties(levelConfigFile);
		String mapFile = props.getProperty("map_file");
		GAME_MAP = new GameMap(mapFile);
		MAX_COUNT_SAME_TIME = Integer.parseInt(props.getProperty("max_count_same_time"));
		ENEMY_COUNT = Integer.parseInt(props.getProperty("enemy_count"));
		ENEMY_HP = Integer.parseInt(props.getProperty("enemy_hp"));
		ENEMY_SPEED = Integer.parseInt(props.getProperty("enemy_speed"));
		ENEMY_ATK = Integer.parseInt(props.getProperty("enemy_atk"));
		BORN_ENEMY_INTERVAL = Integer.parseInt(props.getProperty("born_enemy_interval"));
		PLAYER_HP = Integer.parseInt(props.getProperty("player_hp"));
		PLAYER_SPEED = Integer.parseInt(props.getProperty("player_speed"));
		PLAYER_ATK = Integer.parseInt(props.getProperty("player_atk"));
		BULLET_SPEED = Integer.parseInt(props.getProperty("bullet_speed"));
		RECOVER = Boolean.parseBoolean(props.getProperty("recover"));
		isFinalLevel = Boolean.parseBoolean(props.getProperty("final_level", "false"));
		int bgmCount = Integer.parseInt(props.getProperty("bgm_count"));
		String contents = props.getProperty("bgm_files");
		String[] filepath = TankWarUtil.splitString(contents, ".mp3", bgmCount);
		for (String s : filepath) {
			try {
				bgmList.add(new AudioClip(new File(s).toURI().toURL().toString()));
			} catch (MalformedURLException e) {
				throw new RuntimeException(e);
			}

		}
	}

	public void setParent(PlayScene parent) {
		this.parent = parent;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	boolean active;

	Tank player;

	public void setPlayer(Tank player) {
		this.player = player;
	}

	public void start() {
		currentThread = new Thread(this::run);
		currentThread.start();
	}

	@SuppressWarnings("BusyWait")
	public void run() {
		active = true;

		if (!bgmList.isEmpty()) {
			parent.gameBGM.load(bgmList);
		} else if (isFinalLevel){
			parent.gameBGM.load(TankWar.bossBGM);
		} else {
			parent.gameBGM.load(TankWar.pve1BGM);
		}
		if (TankWar.BGM_ENABLE)
			parent.gameBGM.play();

		try {
			GAME_MAP.createGameMap(parent);
		} catch (NullPointerException e) {
			System.out.println("关卡未设置部署场景");
			throw new RuntimeException(e);
		}

		try {
			player.setMaxHp(PLAYER_HP);
			player.setSpeed(PLAYER_SPEED);
			player.setAtk(PLAYER_ATK);
			player.setState(Tank.State.STATE_IDLE);
			if (RECOVER) {
				player.setHp(PLAYER_HP);
			}
		} catch (NullPointerException e) {
			System.out.println("关卡未设置游戏玩家");
			throw new RuntimeException(e);
		}

		Bullet.setSpeed(BULLET_SPEED);

		int enemyCount = 1, spawnX;
		// 开始生成敌方坦克
		while (active && enemyCount <= ENEMY_COUNT) {
			try {
				Thread.sleep(BORN_ENEMY_INTERVAL);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}

			if (EnemyTank.getCount() >= MAX_COUNT_SAME_TIME) {
				System.out.println("max");
				continue;
			}

			int number = TankWarUtil.getRandomNumber(0, 2);
			if (number == 0) {
				spawnX = parent.getLeftBound() + Tank.DEFAULT_RADIUS + 6;
			} else {
				spawnX = parent.getRightBound() - Tank.DEFAULT_RADIUS - 6;
			}

			Tank enemyTank = EnemyTank.createEnemyTank(parent, spawnX,
					parent.getUpBound() + Tank.DEFAULT_RADIUS, "Enemy" + enemyCount);

			enemyTank.setMaxHp(ENEMY_HP);
			enemyTank.setHp(ENEMY_HP);
			enemyTank.setSpeed(ENEMY_SPEED);
			enemyTank.setAtk(ENEMY_ATK);

			enemyCount++;
			parent.addGameObject(enemyTank);
		}

		if (active) {
			synchronized (EnemyTank.class) {
				try {
					EnemyTank.class.wait();
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		}

		if (active) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
			parent.levelPassed();
		}

	}

	public boolean isAlive() {
		return currentThread.isAlive();
	}
}