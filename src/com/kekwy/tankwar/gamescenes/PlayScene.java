package com.kekwy.tankwar.gamescenes;

import com.kekwy.gameengine.GameEngine;
import com.kekwy.gameengine.GameFrame;
import com.kekwy.gameengine.GameObject;
import com.kekwy.gameengine.GameScene;

import com.kekwy.tankwar.TankWar;
import com.kekwy.tankwar.effect.Player;
import com.kekwy.tankwar.gamemap.MapTile;
import com.kekwy.tankwar.tank.EnemyTank;
import com.kekwy.tankwar.tank.PlayerTank;
import com.kekwy.tankwar.tank.Tank;
import com.kekwy.tankwar.util.Direction;
import com.kekwy.tankwar.util.TankWarUtil;
import javafx.scene.media.AudioClip;


import java.awt.*;
import java.awt.event.KeyEvent;


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
		TankWar.levels[currentLevel].setActive(false);
		while (TankWar.levels[currentLevel].isAlive()) {
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
		if (!playing)
			return;
		waitCurrentLevel();
		gameBGM.stop();

		// audioClip.isPlaying();

		backGround.setActive(false);
		sceneClear();
		MapTile.base = 2;
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
		}


		public static final Font TILE_FONT = new Font("Minecraft 常规", Font.PLAIN, 32);
		public static final Font NOTICE_FONT = new Font("Minecraft 常规", Font.PLAIN, 18);

		static final String TILE = "过关！";
		static final String NOTICE0 = "按ESC返回主菜单";
		static final String NOTICE1 = "按Enter进入下一关";

		int top = FRAME_HEIGHT / 4 * 3 + getUpBound();
		@Override
		public void render(Graphics g) {
			g.setColor(Color.BLACK);
			g.fillRect(0, top, FRAME_WIDTH, FRAME_HEIGHT / 4);
			g.setColor(Color.WHITE);
			g.fillRect(0, top, FRAME_WIDTH, 3);
			g.fillRect(0, top + 6, FRAME_WIDTH, 3);
			g.setFont(TILE_FONT);
			g.drawString(TILE,440,top + 50);
			g.setFont(NOTICE_FONT);
			g.drawString(NOTICE0,20,top + 110);
			g.drawString(NOTICE1,790,top + 110);
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
		public static final String OVER_NOTICE = "按Enter键继续...";

		public OverBackGround(GameScene parent) {
			super(parent);
			setLayer(2);
			setActive(true);
		}

		@Override
		public void render(Graphics g) {
			if (overImg == null) {
				overImg = TankWarUtil.createImage("/over.gif");
			}
			int imgW = overImg.getWidth(null);
			int imgH = overImg.getHeight(null);
			// TODO 第一次绘制时触发了bug
			if (imgW != -1) {
				// System.out.println(imgH);
				g.drawImage(overImg, FRAME_WIDTH - 3 * imgW >> 1, (FRAME_HEIGHT - 3 * imgH >> 1) - 20, 3 * imgW,
						3 * imgH, null);
				g.setColor(Color.WHITE);
				g.setFont(OVER_FONT);
				g.drawString(OVER_NOTICE, FRAME_WIDTH - 125 >> 1, (FRAME_HEIGHT - 3 * imgH >> 1) + 145);
			}
		}

		@Override
		public void keyReleasedEvent(int keyCode) {
			if (keyCode == KeyEvent.VK_ENTER) {
				PlayScene.this.setInactive(TankWar.INDEX_MAIN_MENU);
			}
		}
	}

}


// TODO：玩家生成坐标
// TODO：彩蛋关卡
// TODO：其他游戏场景
// TODO：双人协同作战
// TODO：四人混战
// TODO：简单的寻路AI
