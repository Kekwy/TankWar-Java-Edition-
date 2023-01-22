package com.kekwy.jw.tankwar.level;

import com.kekwy.jw.tankwar.TankWar;
import com.kekwy.jw.tankwar.gamemap.GameMap;
import com.kekwy.jw.tankwar.gamemap.MapTile;
import com.kekwy.jw.tankwar.gamescenes.LocalPlayScene;
import com.kekwy.jw.tankwar.tank.Bullet;
import com.kekwy.jw.tankwar.tank.EnemyTank;
import com.kekwy.jw.tankwar.tank.Tank;
import com.kekwy.jw.tankwar.util.TankWarUtil;
import javafx.scene.media.Media;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	private final List<Media> bgmList = new ArrayList<>();
	private final int SPAWN_X, SPAWN_Y;
	private LocalPlayScene parent;

	private Thread currentThread;

	private static final Pattern spawnPattern = Pattern.compile("([A-Z]+)(\\d+)");

	public Level(InputStream levelConfigFile) {
		Properties props = TankWarUtil.loadProperties(levelConfigFile);
		isFinalLevel = Boolean.parseBoolean(props.getProperty("final_level", "false"));
		InputStream mapFile;
		if (isFinalLevel) {
			mapFile = Level.class.getResourceAsStream("/levels/map/finalMap.xlsx");
			// mapFile = mapFile.substring(mapFile.indexOf("file:") + 6);
		} else {
			try {
				mapFile = new FileInputStream(props.getProperty("map_file"));
			} catch (FileNotFoundException e) {
				throw new RuntimeException(e);
			}
		}
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

		int bgmCount = Integer.parseInt(props.getProperty("bgm_count"));
		String contents = props.getProperty("bgm_files");
		String[] filepath = TankWarUtil.splitString(contents, ".mp3", bgmCount);
		for (String s : filepath) {
			try {
				bgmList.add(new Media(new File(s).toURI().toURL().toString()));
			} catch (MalformedURLException e) {
				throw new RuntimeException(e);
			}

		}
		String spawnPosition = props.getProperty("spawn");
		Matcher matcher = spawnPattern.matcher(spawnPosition);
		if (matcher.find()) {
			SPAWN_X = (matcher.group(1).charAt(0) - 'A' - 1) * MapTile.TILE_WIDTH + MapTile.TILE_WIDTH / 2;
			SPAWN_Y = (Integer.parseInt(matcher.group(2)) - 2) * MapTile.TILE_WIDTH + MapTile.TILE_WIDTH / 2;
			// System.out.println(matcher.group(1).charAt(0) + ", " + Integer.parseInt(matcher.group(2)));
		} else {
			SPAWN_X = 8 * MapTile.TILE_WIDTH + MapTile.TILE_WIDTH / 2;
			SPAWN_Y = 13 * MapTile.TILE_WIDTH + MapTile.TILE_WIDTH / 2;
		}
	}

	public void setParent(LocalPlayScene parent) {
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
		active = true;

		if (!bgmList.isEmpty()) {
			parent.gameBGM.load(bgmList);
		} else if (isFinalLevel) {
			parent.gameBGM.load(TankWar.bossBGM);
		} else {
			parent.gameBGM.load(TankWar.pve1BGM);
		}
		if (TankWar.BGM_ENABLE)
			parent.gameBGM.play();

		if (!parent.isPlaying()) {
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
				player.transform.setX(SPAWN_X);
				player.transform.setY(SPAWN_Y - 6);
				if (RECOVER) {
					player.setHp(PLAYER_HP);
				}
			} catch (NullPointerException e) {
				System.out.println("关卡未设置游戏玩家");
				throw new RuntimeException(e);
			}

			Bullet.setSpeed(BULLET_SPEED);

		}
		currentThread = new Thread(this::run);
		currentThread.start();
	}

	public int getEnemyCount() {
		return enemyCount;
	}

	public void setEnemyCount(int enemyCount) {
		this.enemyCount = enemyCount;
	}

	private int enemyCount = 1;

	@SuppressWarnings("BusyWait")
	public void run() {

		double spawnX;
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

			int number = (int) TankWarUtil.getRandomNumber(0, 2);
			if (number == 0) {
				spawnX = Tank.TANK_RADIUS + 6;
			} else {
				spawnX = parent.getWidth() - Tank.TANK_RADIUS - 6;
			}

			Tank enemyTank = EnemyTank.createEnemyTank(parent, spawnX,
					Tank.TANK_RADIUS, "Enemy" + enemyCount, 2);

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
			if (player.getState() != Tank.State.STATE_DIE) {
				parent.levelPassed();
			}
		}
		parent.gameBGM.stop();
	}

	public boolean isAlive() {
		return currentThread.isAlive();
	}
}
