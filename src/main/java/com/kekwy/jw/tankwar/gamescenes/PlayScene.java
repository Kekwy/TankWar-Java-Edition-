package com.kekwy.jw.tankwar.gamescenes;

import com.kekwy.jw.gameengine.GameEngine;
import com.kekwy.jw.gameengine.GameFrame;
import com.kekwy.jw.gameengine.GameObject;
import com.kekwy.jw.gameengine.GameScene;
import com.kekwy.jw.tankwar.TankWar;
import com.kekwy.jw.tankwar.effect.Blast;
import com.kekwy.jw.tankwar.effect.Player;
import com.kekwy.jw.tankwar.gamemap.MapTile;
import com.kekwy.jw.tankwar.level.Level;
import com.kekwy.jw.tankwar.tank.Bullet;
import com.kekwy.jw.tankwar.tank.EnemyTank;
import com.kekwy.jw.tankwar.tank.PlayerTank;
import com.kekwy.jw.tankwar.tank.Tank;
import com.kekwy.jw.tankwar.util.Direction;
import com.kekwy.jw.tankwar.util.TankWarUtil;
import javafx.scene.media.AudioClip;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.List;


/**
 * 游戏进行时的场景
 */
public class PlayScene extends GameScene {

	/**
	 * 游戏时背景音乐
	 */
	public final Player gameBGM = new Player();

	/**
	 * 当前场景的窗口标题
	 */
	private static final String GAME_TITLE = "坦克大战v2.0 by kekwy - 单人游戏";
	/**
	 * 当前场景的窗口大小
	 */
	private static final int FRAME_WIDTH = 960, FRAME_HEIGHT = 560;


	/**
	 * 游戏背景对象
	 */
	BackGround backGround;


	public PlayScene(GameFrame gameFrame, GameEngine gameEngine) {
		super(gameFrame, gameEngine);

		// 使用公共窗口
		setGameFrame(gameFrame, FrameType.FRAME_TYPE_PUBLIC);
		// 新建私有窗口
		// setGameFrame(new GameFrame(), FrameType.FRAME_TYPE_PRIVATE);

		// 初始化场景
		setTitle(GAME_TITLE);
		setSize(FRAME_WIDTH, FRAME_HEIGHT);
		setResizable(false);
		setLocation();

		backGround = new BackGround(this);

		// GameMap.createGameMap(this, "./maps/level1.xlsx");


		//TODO 关卡跳转
		// server

		// audioClip.loop();
		player = new PlayerTank(this, 200, 400, Direction.DIR_UP, TankWar.PLAYER_NAME);

		levelCount = TankWar.levels.length;

		setActive();

		playing = true;
		TankWar.levels[currentLevel].setParent(this);
		TankWar.levels[currentLevel].setPlayer(player);
		TankWar.levels[currentLevel].start();

	}

	Tank player;


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
		sceneClear();
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
			this.setRadius(FRAME_WIDTH / 2);
			this.position.setX(FRAME_WIDTH / 2);
			this.position.setY(top + FRAME_WIDTH / 2);
		}


		public static final Font TILE_FONT = new Font("Minecraft 常规", Font.PLAIN, 32);
		public static final Font NOTICE_FONT = new Font("Minecraft 常规", Font.PLAIN, 18);

		static final String TILE = "过关！";
		static final String NOTICE0 = "按ESC返回主菜单";
		static final String NOTICE1 = "按Enter进入下一关";

		int top = FRAME_HEIGHT / 4 * 3 + getUpBound();


		@Override
		public void collide(List<GameObject> gameObjects) {
			if (currentLevel + 1 == levelCount) {
				for (GameObject gameObject : gameObjects) {
					if (gameObject instanceof Bullet bullet) {
						this.setActive(false);
						bullet.setActive(false);
						Blast blast = Blast.createBlast(PlayScene.this,
								bullet.position.getX(), bullet.position.getY());
						blast.setRadius(100);
						addGameObject(blast);
						new Thread(() -> {
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								throw new RuntimeException(e);
							}
							itemsClear(MapTile.class);
							currentLevel += 2;
							TankWar.finalLevel.setParent(PlayScene.this);
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

		@Override
		public void render(Graphics g) {
			g.setColor(Color.BLACK);
			g.fillRect(0, top, FRAME_WIDTH, FRAME_HEIGHT / 4);
			g.setColor(Color.WHITE);
			g.fillRect(0, top, FRAME_WIDTH, 3);
			g.fillRect(0, top + 6, FRAME_WIDTH, 3);
			g.setFont(TILE_FONT);
			g.drawString(TILE, 440, top + 50);
			g.setFont(NOTICE_FONT);
			g.drawString(NOTICE0, 20, top + 110);
			g.drawString(NOTICE1, 790, top + 110);
		}


		@Override
		public void keyPressedEvent(int keyCode) {
			if (keyCode == KeyEvent.VK_ESCAPE) {
				audioPassLevel.stop();
				PlayScene.this.setInactive(TankWar.INDEX_MAIN_MENU);
				return;
			}

			if (keyCode != KeyEvent.VK_ENTER)
				return;
			this.setActive(false);
			currentLevel++;
			if (currentLevel == levelCount) {
				isWin = true;
				PlayScene.this.gameOver();
			} else {
				audioPassLevel.stop();
				itemsClear(MapTile.class);
				TankWar.levels[currentLevel].setParent(PlayScene.this);
				TankWar.levels[currentLevel].setPlayer(player);
				TankWar.levels[currentLevel].start();
				playing = true;
			}
		}
	}

	/**
	 * 用于渲染背景的内部类
	 */
	class BackGround extends GameObject {

		public BackGround(GameScene parent) {
			super(parent);
			setLayer(0);
			setActive(true);
			parent.addGameObject(this);
		}

		@Override
		public void render(Graphics g) {
			g.setColor(Color.BLACK);
			g.fillRect(0, getUpBound(), FRAME_WIDTH, FRAME_HEIGHT);
		}
	}

	/**
	 * 用于渲染游戏结束画面的内部类
	 */
	class OverBackGround extends GameObject {

		static Image overImg = null;
		public static final Font OVER_FONT = new Font("Minecraft 常规", Font.PLAIN, 18);
		public static final Font NOTICE_FONT = new Font("Minecraft 常规", Font.PLAIN, 26);
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
			if (PlayScene.this.isWin) {
				if (currentLevel == levelCount) {
					TankWar.endBGM0.play(0.5);
				} else if (currentLevel == levelCount + 1) {
					TankWar.endBGM1.play(0.5);
				}
			}
		}

		@Override
		public void render(Graphics g) {
			if (currentLevel >= levelCount) {
				g.setColor(Color.BLACK);
				g.fillRect(0, getUpBound(), FRAME_WIDTH, FRAME_HEIGHT);
				g.setColor(Color.WHITE);
				g.setFont(NOTICE_FONT);
				if (currentLevel == levelCount) {
					g.drawString(str0, 400, 200);
					g.drawString(str1, 400, 300);
				} else if (currentLevel == levelCount + 1) {
					g.drawString(str2, 400, 200);
					g.drawString(str3, 400, 300);
					g.drawString(str4, 400, 400);
					g.drawString(str5, 400, 500);
				}
				g.setFont(OVER_FONT);
				g.drawString(OVER_NOTICE2, 20, 550);
			} else {
				if (overImg == null) {
					overImg = TankWarUtil.createImage("/over.gif");
				}
				int imgW = overImg.getWidth(null);
				int imgH = overImg.getHeight(null);
				// 第一次绘制时触发了bug
				if (imgW != -1) {
					// System.out.println(imgH);
					g.drawImage(overImg, FRAME_WIDTH - 3 * imgW >> 1, (FRAME_HEIGHT - 3 * imgH >> 1) - 20, 3 * imgW,
							3 * imgH, null);
					g.setColor(Color.WHITE);
					g.setFont(OVER_FONT);
					g.drawString(OVER_NOTICE, FRAME_WIDTH - 125 >> 1, (FRAME_HEIGHT - 3 * imgH >> 1) + 145);
				}
			}
		}

		@Override
		public void keyPressedEvent(int keyCode) {
			if (keyCode == KeyEvent.VK_ENTER && !isWin) {
				PlayScene.this.setInactive(TankWar.INDEX_MAIN_MENU);
			} else if (keyCode == KeyEvent.VK_ESCAPE && isWin) {
				PlayScene.this.setInactive(TankWar.INDEX_MAIN_MENU);
				TankWar.endBGM0.stop();
				TankWar.endBGM1.stop();
			}
		}
	}

}


// TODO：彩蛋关卡
// TODO：寻路
// TODO：其他游戏场景
// TODO：双人协同作战
// TODO：四人混战
// TODO：简单的寻路AI
