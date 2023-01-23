package com.kekwy.tankwar.client.gamescenes;

import com.kekwy.tankwar.client.effect.Blast;
import com.kekwy.tankwar.client.effect.MusicPlayer;
import com.kekwy.tankwar.client.gamemap.MapTile;
import com.kekwy.tankwar.client.level.Level;
import com.kekwy.tankwar.client.tank.Bullet;
import com.kekwy.tankwar.client.tank.EnemyTank;
import com.kekwy.tankwar.client.tank.PlayerTank;
import com.kekwy.tankwar.client.trigger.Trigger;
import com.kekwy.tankwar.client.trigger.TriggerHandler;
import com.kekwy.tankwar.client.util.Direction;
import com.kekwy.tankwar.client.util.TankWarUtil;
import com.kekwy.tankwar.client.GameObject;
import com.kekwy.tankwar.client.GameScene;
import com.kekwy.tankwar.client.TankWar;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


/**
 * 游戏进行时的场景
 */
public class LocalPlayScene extends GameScene {

	/**
	 * 游戏时背景音乐
	 */
	public final MusicPlayer gameBGM = new MusicPlayer();

	/**
	 * 当前场景的窗口标题
	 */
	private static final String GAME_TITLE = "坦克大战v2.0 by kekwy - 单人游戏";
	/**
	 * 当前场景的窗口大小
	 */
	private static final int SCENE_WIDTH = 960, SCENE_HEIGHT = 560;


	/**
	 * 游戏背景对象
	 */
	BackGround backGround;

	public LocalPlayScene() {
		super(SCENE_WIDTH, SCENE_HEIGHT, GAME_TITLE);
		backGround = new BackGround(this);
		levelCount = TankWar.levels.length;
		this.setOnKeyPressed((KeyEvent keyEvent) -> {
			if (player != null) {
				player.keyPressedHandle(keyEvent);
			}
			if (passNotice.isActive() && passNotice.isVisible()) {
				passNotice.keyPressedHandler(keyEvent);
			}
			if (overBackGround.isActive()) {
				overBackGround.keyPressedHandler(keyEvent);
			}
		});
		this.setOnKeyReleased((KeyEvent keyEvent) -> {
			if (player != null) {
				player.keyReleasedHandle(keyEvent);
			}
		});
		player = new PlayerTank(this, 200, 400, Direction.DIR_UP, TankWar.PLAYER_NAME, 1);
	}

	@Override
	public void start() {
		super.start();
		play();
	}

	public void play() {
		addGameObject(player);
		if (!pass) {
			Level level = getLevel();
			level.setParent(this);
			level.setPlayer(player);
			level.start();
			playing = true;
		} else {
			levelPassed();
		}
	}


	private Level getLevel() {
		Level level;
		if (currentLevel < levelCount) {
			level = TankWar.levels[currentLevel];
		} else {
			level = TankWar.finalLevel;
		}
		return level;
	}

	public PlayerTank player;


	AudioClip audioPassLevel = TankWar.passBGM;

	PassNotice passNotice = new PassNotice(this);

	private void waitCurrentLevel() {
		Level level;
		if (currentLevel < levelCount)
			level = TankWar.levels[currentLevel];
		else
			level = TankWar.finalLevel;
		level.setActive(false);
		while (level.isAlive()) {
			synchronized (EnemyTank.class) {
				EnemyTank.class.notify();
			}
		}
	}

	boolean playing = false;

	boolean pass = false;

	boolean over = false;

	public boolean isPlaying() {
		return playing;
	}

	public void levelPassed() {
		if (!playing)
			return;
		pass = true;
		playing = false;
		// waitCurrentLevel();
		System.out.println("Pass!");
		// player.setState(Tank.State.STATE_DIE);
		// gameBGM.stop();
		audioPassLevel.play(0.5);
		passNotice.show();
	}

	public final OverBackGround overBackGround = new OverBackGround(this);

	/**
	 * 用于设置游戏结束的方法
	 */
	public void gameOver() {
		over = true;

		if (isWin) {
			// waitCurrentLevel();
			audioPassLevel.stop();
			isWin = false;
		} else {
			if (!playing)
				return;
			waitCurrentLevel();
			playing = false;
//			gameBGM.stop();
		}
		// audioClip.isPlaying();
		backGround.setActive(false);
		// stop();
		clear();
		EnemyTank.setCount(0);
		mapClear();
		overBackGround.setActive();
		addGameObject(overBackGround);
	}

	int currentLevel = 0;

	int levelCount;

	boolean isWin = false;

	/**
	 * 用于展示过关后关卡底部“黑框”的内部类
	 */
	class PassNotice extends GameObject implements TriggerHandler {

		public PassNotice(GameScene parent) {
			super(parent);
			setLayer(4);
//			this.setColliderType(ColliderType.COLLIDER_TYPE_RECT);
			setRadius(SCENE_WIDTH / 2);
			this.transform.setX(SCENE_WIDTH / 2.0);
			this.transform.setY(top + SCENE_WIDTH / 2.0);
			setActive(true);
			getParent().addGameObject(this);
			int radius = 20;
			int y = top + radius;
			for (int x = 0; x < SCENE_WIDTH; x += 2 * radius) {
				Trigger trigger = new Trigger(LocalPlayScene.this, this, x, y, radius);
				trigger.setActive(true);
				getParent().addGameObject(trigger);
			}
		}


		public static final Font TILE_FONT = new Font("Minecraft 常规", 32);
		public static final Font NOTICE_FONT = new Font("Minecraft 常规", 18);

		static final String TILE = "过关！";
		static final String NOTICE0 = "按ESC返回主菜单";
		static final String NOTICE1 = "按Enter进入下一关";

		int top = SCENE_HEIGHT / 4 * 3;

		@Override
		public void refresh(GraphicsContext g, long timestamp) {
			if (!visible) return;
			g.setFill(Color.BLACK);
			g.fillRect(0, top, SCENE_WIDTH, SCENE_HEIGHT / 4.0);
			g.setFill(Color.WHITE);
			g.fillRect(0, top, SCENE_WIDTH, 3);
			g.fillRect(0, top + 6, SCENE_WIDTH, 3);
			g.setFont(TILE_FONT);
			g.fillText(TILE, 440, top + 50);
			g.setFont(NOTICE_FONT);
			g.fillText(NOTICE0, 20, top + 110);
			g.fillText(NOTICE1, 790, top + 110);
		}

		private boolean visible = false;

		public boolean isVisible() {
			return visible;
		}

		@Override
		public void setActive(boolean active) {
			synchronized (this) {
				super.setActive(active);
				this.notify();
			}
		}

		public void show() {
			synchronized (this) {
				visible = true;
				this.notify();
			}
		}

		public void hide() {
			synchronized (this) {
				visible = false;
			}
		}

//		private final List<GameObject> collideList = new ArrayList<>();

		private void doCollide(GameObject object) {
//			getParent().getObjectAroundTheGridCell(this, collideList);
//			for (GameObject gameObject : collideList) {
			if (!(object instanceof Bullet bullet))
				return;
			this.hide();
			bullet.setActive(false);
			Blast blast = Blast.createBlast(LocalPlayScene.this,
					bullet.transform.getX(), bullet.transform.getY());
			blast.setRadius(100);
			addGameObject(blast);
			new Thread(() -> {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
				// itemsClear(MapTile.class);
				mapClear();
				currentLevel += 2;
				TankWar.finalLevel.setParent(LocalPlayScene.this);
				TankWar.finalLevel.setPlayer(player);
				isWin = true;
				audioPassLevel.stop();
				TankWar.finalLevel.start();
				playing = true;
			}).start();
//			object.collideLock().unlock();
//			this.collideLock().unlock();
		}
//	}
//			collideList.clear();

		public void keyPressedHandler(KeyEvent keyEvent) {
			KeyCode keyCode = keyEvent.getCode();
			if (keyCode == KeyCode.ESCAPE) {
				audioPassLevel.stop();
				LocalPlayScene.this.changeScene(TankWar.MAIN_SCENE);
				return;
			}

			if (keyCode != KeyCode.ENTER)
				return;
			hide();
			currentLevel++;
			if (currentLevel == levelCount) {
				isWin = true;
				LocalPlayScene.this.gameOver();
			} else {
				audioPassLevel.stop();
				mapClear();
				TankWar.levels[currentLevel].setParent(LocalPlayScene.this);
				TankWar.levels[currentLevel].setPlayer(player);
				TankWar.levels[currentLevel].start();
				playing = true;
				pass = false;
			}
		}

		@Override
		public void handle(GameObject object) {
			synchronized (this) {
				if (isVisible() && isActive())
					doCollide(object);
			}

		}
	}

	/**
	 * 用于渲染背景的内部类
	 */
	static class BackGround extends GameObject {

		@Override
		public void refresh(GraphicsContext g, long timestamp) {
			g.setFill(Color.BLACK);
			g.fillRect(0, 0, SCENE_WIDTH, SCENE_HEIGHT);
		}

		public BackGround(GameScene parent) {
			super(parent);
			setLayer(0);
			setActive(true);
			parent.addGameObject(this);
		}
	}

	/**
	 * 用于渲染游戏结束画面的内部类
	 */
	class OverBackGround extends GameObject {

		static Image overImg = null;
		public static final Font OVER_FONT = new Font("Minecraft 常规", 18);
		public static final Font NOTICE_FONT = new Font("Minecraft 常规", 26);
		public static final String OVER_NOTICE = "按Enter键继续...";
		public static final String OVER_NOTICE2 = "按ESC键继续...";
		static final String str0 = "恭喜通关";
		static final String str1 = "但好像少了什么？";
		static final String str2 = "被发现了doge";
		static final String str3 = "这就是游戏的全部关卡啦";
		static final String str4 = "感谢你的支持";
		static final String str5 = "后会有期啦~";

		public OverBackGround(GameScene parent) {
			super(parent);
			setLayer(2);
			// setActive(true)
		}

		public void setActive() {
			setActive(true);
			if (LocalPlayScene.this.isWin) {
				if (currentLevel == levelCount) {
					TankWar.endBGM0.play(0.5);
				} else if (currentLevel == levelCount + 1) {
					TankWar.endBGM1.play(0.5);
				}
			}
		}

		@Override
		public void refresh(GraphicsContext g, long timestamp) {
			if (currentLevel >= levelCount) {
				g.setFill(Color.BLACK);
				g.fillRect(0, 0, SCENE_WIDTH, SCENE_HEIGHT);
				g.setFill(Color.WHITE);
				g.setFont(NOTICE_FONT);
				if (currentLevel == levelCount) {
					g.fillText(str0, 400, 200);
					g.fillText(str1, 400, 300);
				} else if (currentLevel == levelCount + 1) {
					g.fillText(str2, 400, 200);
					g.fillText(str3, 400, 300);
					g.fillText(str4, 400, 400);
					g.fillText(str5, 400, 500);
				}
				g.setFont(OVER_FONT);
				g.fillText(OVER_NOTICE2, 20, 550);
			} else {
				if (overImg == null) {
					overImg = TankWarUtil.createImage("/over.gif");
				}
				double imgW = overImg.getWidth();
				double imgH = overImg.getHeight();
				// 第一次绘制时触发了bug
				if (imgW != -1) {
					// System.out.println(imgH);
					g.drawImage(overImg, SCENE_WIDTH / 2.0 - 3 * imgW / 2,
							(SCENE_HEIGHT / 2.0 - 3 * imgH / 2) - 20, 3 * imgW, 3 * imgH);
					g.setFill(Color.WHITE);
					g.setFont(OVER_FONT);
					g.fillText(OVER_NOTICE, SCENE_WIDTH - 125 >> 1, (SCENE_HEIGHT / 2.0 - 3 * imgH / 2) + 145);
				}
			}
		}

		public void keyPressedHandler(KeyEvent keyEvent) {
			if (!this.isActive()) {
				return;
			}
			KeyCode keyCode = keyEvent.getCode();
			if (keyCode == KeyCode.ENTER && !isWin) {
				LocalPlayScene.this.changeScene(new MainScene());
			} else if (keyCode == KeyCode.ESCAPE && isWin) {
				LocalPlayScene.this.changeScene(new MainScene());
				TankWar.endBGM0.stop();
				TankWar.endBGM1.stop();
			}
		}

	}


	@Override
	public void update(GameObject object, double x, double y, int offset) {
		if (object instanceof Bullet) {
			if (x < 0 || x > SCENE_WIDTH || y < 0 || y > SCENE_HEIGHT) {
				object.setActive(false);
				return;
			}
		} else {
			x = Math.max(x, offset);
			x = Math.min(x, SCENE_WIDTH - offset);
			y = Math.max(y, offset);
			y = Math.min(y, SCENE_HEIGHT - offset);
		}
		super.update(object, x, y, offset);
		object.transform.setX(x);
		object.transform.setY(y);
	}

	private final List<MapTile> mapTileList = new ArrayList<>();

	public void setGameMap(MapTile[][] gameMap) {
		mapTileList.clear();
		for (MapTile[] tiles : gameMap) {
			for (MapTile tile : tiles) {
				if (tile != null) {
					mapTileList.add(tile);
				}
			}
		}
	}

	private void mapClear() {
		for (MapTile tile : mapTileList) {
			tile.setActive(false);
		}
	}

	// 单人游戏进度保存
	public List<GameObject> saveToDisk() {
		this.stop();
		// for test
		List<GameObject> list = new ArrayList<>();
		if (over) {
			return null;
		}
		try {
			// 创建临时文件
			File saveFile = File.createTempFile("save", ".tmp", new File("./save/"));
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(saveFile));
			// 对象写入顺序：currentLevel, isWin, playing, level中的EnemyCount变量, 场景中的游戏对象
			oos.writeObject(currentLevel);
			oos.writeObject(isWin);
			oos.writeObject(playing);
			oos.writeObject(getLevel().getEnemyCount());
			for (GameObject object : super.objectList) {
				// 跳过一些不需要保存的对象
				if (object instanceof BackGround ||
						object instanceof OverBackGround ||
						object instanceof PassNotice ||
						object instanceof Trigger) {
					continue;
				}
				if (object instanceof Runnable) {
					object.waitFor();
				}
				object.setActive(true);
				list.add(object);
				oos.writeObject(object);
			}
			oos.writeObject(null);
			oos.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return list;
	}

	public List<GameObject> loadFromDisk(File file) {
		List<GameObject> list = new ArrayList<>();
		ObjectInputStream ois;
		try {
			ois = new ObjectInputStream(new FileInputStream(file));
		} catch (IOException e) {
//			System.out.println("找不到文件：" + file);
			throw new RuntimeException(e);
		}

		try {
			currentLevel = (int) ois.readObject();
			isWin = (boolean) ois.readObject();
			playing = (boolean) ois.readObject();
			getLevel().setEnemyCount((Integer) ois.readObject());

			GameObject object;

			int enemyCount = 0;

			while ((object = (GameObject) ois.readObject()) != null) {
				list.add(object);
				if (object instanceof PlayerTank tank) {
					player = tank;
					player.setColor(player.r, player.g, player.b);
					object.setParent(this);
					tank.recoveryFromDisk();
					continue;
				} else if (object instanceof MapTile tile) {
					mapTileList.add(tile);
				} else if (object instanceof EnemyTank enemy) {
					enemyCount++;
					enemy.setColor(enemy.r, enemy.g, enemy.b);
				} else if (object instanceof Bullet bullet) {
					bullet.setColor(bullet.r, bullet.g, bullet.b);
				}
				object.setParent(this);
				addGameObject(object);
			}

			EnemyTank.setCount(enemyCount);

		} catch (IOException e) {
			System.out.println("目标文件损坏（格式异常）");
			throw new RuntimeException(e);
		} catch (ClassNotFoundException e) {
			System.out.println("找不到指定的类");
			throw new RuntimeException(e);
		}

		try {
			ois.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return list;
	}

}


// TODO：其他游戏场景
// TODO：多人混战
// TODO：简单的寻路AI
