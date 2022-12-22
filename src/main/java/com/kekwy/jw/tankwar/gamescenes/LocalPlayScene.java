package com.kekwy.jw.tankwar.gamescenes;

import com.kekwy.jw.tankwar.GameObject;
import com.kekwy.jw.tankwar.GameScene;
import com.kekwy.jw.tankwar.TankWar;
import com.kekwy.jw.tankwar.effect.Blast;
import com.kekwy.jw.tankwar.effect.MusicPlayer;
import com.kekwy.jw.tankwar.level.Level;
import com.kekwy.jw.tankwar.tank.Bullet;
import com.kekwy.jw.tankwar.tank.EnemyTank;
import com.kekwy.jw.tankwar.tank.PlayerTank;
import com.kekwy.jw.tankwar.util.Direction;
import com.kekwy.jw.tankwar.util.TankWarUtil;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

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
		});
		this.setOnKeyReleased((KeyEvent keyEvent) -> {
			if (player != null) {
				player.keyReleasedHandle(keyEvent);
			}
		});
	}

	@Override
	public void start() {
		super.start();
		player = new PlayerTank(this, 200, 400, Direction.DIR_UP, TankWar.PLAYER_NAME);
		play();
	}

	public void play() {
		playing = true;
		TankWar.levels[currentLevel].setParent(this);
		TankWar.levels[currentLevel].setPlayer(player);
		TankWar.levels[currentLevel].start();
	}

	PlayerTank player;


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

	public boolean isPlaying() {
		return playing;
	}

	public void levelPassed() {
		if (!playing)
			return;
		playing = false;
		// waitCurrentLevel();
		System.out.println("Pass!");
		// player.setState(Tank.State.STATE_DIE);
		gameBGM.stop();
		audioPassLevel.play(0.5);
		passNotice.setActive(true);
		addGameObject(passNotice);
	}

	/**
	 * 用于设置游戏结束的方法
	 */
	public void gameOver() {
		if (isWin) {
			audioPassLevel.stop();

		}
		if (!playing)
			return;
		waitCurrentLevel();
		gameBGM.stop();
		// audioClip.isPlaying();
		backGround.setActive(false);
		stop();
		EnemyTank.setCount(0);
		OverBackGround overBackGround = new OverBackGround(this);
		addGameObject(overBackGround);
	}

	int currentLevel = 0;

	int levelCount;

	boolean isWin = false;

	/**
	 * 用于展示过关后关卡底部“黑框”的内部类
	 */
	class PassNotice extends GameObject {

		public PassNotice(GameScene parent) {
			super(parent);
			this.setLayer(2);
			this.setColliderType(ColliderType.COLLIDER_TYPE_RECT);
			this.setRadius(SCENE_WIDTH / 2);
			this.transform.setX(SCENE_WIDTH / 2.0);
			this.transform.setY(top + SCENE_WIDTH / 2.0);
		}


		public static final Font TILE_FONT = new Font("Minecraft 常规", 32);
		public static final Font NOTICE_FONT = new Font("Minecraft 常规", 18);

		static final String TILE = "过关！";
		static final String NOTICE0 = "按ESC返回主菜单";
		static final String NOTICE1 = "按Enter进入下一关";

		int top = SCENE_HEIGHT / 4 * 3;


		@Override
		public void refresh(GraphicsContext g, long timestamp) {
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

		@Override
		public void collide(List<GameObject> gameObjects) {
			if (currentLevel + 1 == levelCount) {
				for (GameObject gameObject : gameObjects) {
					if (gameObject instanceof Bullet bullet) {
						this.setActive(false);
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
							currentLevel += 2;
							TankWar.finalLevel.setParent(LocalPlayScene.this);
							TankWar.finalLevel.setPlayer(player);
							isWin = true;
							playing = true;
							audioPassLevel.stop();
							TankWar.finalLevel.start();
						}).start();
					}
				}
			}
		}

//		@Override
//		public void keyPressedEvent(int keyCode) {
//			if (keyCode == KeyEvent.VK_ESCAPE) {
//				audioPassLevel.stop();
//				LocalPlayScene.this.switchScene(TankWar.MAIN_SCENE);
//				return;
//			}
//
//			if (keyCode != KeyEvent.VK_ENTER)
//				return;
//			this.setActive(false);
//			currentLevel++;
//			if (currentLevel == levelCount) {
//				isWin = true;
//				LocalPlayScene.this.gameOver();
//			} else {
//				audioPassLevel.stop();
//				// itemsClear(MapTile.class);
//				TankWar.levels[currentLevel].setParent(LocalPlayScene.this);
//				TankWar.levels[currentLevel].setPlayer(player);
//				TankWar.levels[currentLevel].start();
//				playing = true;
//			}
//		}
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
					g.drawImage(overImg, SCENE_WIDTH - 3 * imgW / 2,
							(SCENE_HEIGHT - 3 * imgH / 2) - 20, 3 * imgW, 3 * imgH);
					g.setFill(Color.WHITE);
					g.setFont(OVER_FONT);
					g.fillText(OVER_NOTICE, SCENE_WIDTH - 125 >> 1, (SCENE_HEIGHT - 3 * imgH / 2) + 145);
				}
			}
		}

//		@Override
//		public void keyPressedEvent(int keyCode) {
//			if (keyCode == KeyEvent.VK_ENTER && !isWin) {
//				LocalPlayScene.this.switchScene(TankWar.MAIN_SCENE);
//			} else if (keyCode == KeyEvent.VK_ESCAPE && isWin) {
//				LocalPlayScene.this.switchScene(TankWar.MAIN_SCENE);
//				TankWar.endBGM0.stop();
//				TankWar.endBGM1.stop();
//			}
//		}
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
}


// TODO：彩蛋关卡
// TODO：寻路
// TODO：其他游戏场景
// TODO：双人协同作战
// TODO：四人混战
// TODO：简单的寻路AI
