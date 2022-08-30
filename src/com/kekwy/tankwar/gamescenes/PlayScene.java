package com.kekwy.tankwar.gamescenes;

import com.kekwy.gameengine.GameEngine;
import com.kekwy.gameengine.GameFrame;
import com.kekwy.gameengine.GameObject;
import com.kekwy.gameengine.GameScene;

import com.kekwy.tankwar.TankWar;
import com.kekwy.tankwar.gamemap.MapTile;
import com.kekwy.tankwar.tank.EnemyTank;
import com.kekwy.tankwar.tank.PlayerTank;
import com.kekwy.tankwar.tank.Tank;
import com.kekwy.tankwar.util.Direction;
import com.kekwy.tankwar.util.TankWarUtil;
import javafx.scene.media.AudioClip;


import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Objects;


/**
 * 游戏进行时的场景
 */
public class PlayScene extends GameScene {
	/**
	 * 当前场景的窗口标题
	 */
	private static final String GAME_TITLE = "坦克大战v2.0 by kekwy - 单人游戏";
	/**
	 * 当前场景的窗口大小
	 */
	private static final int FRAME_WIDTH = 960, FRAME_HEIGHT = 560;


	/**
	 * 游戏进行时用于生成敌方坦克的线程
	 */
	Thread gameThread;

	/**
	 * 游戏背景对象
	 */
	BackGround backGround;

	/**
	 * 游戏时背景音乐
	 */
	public static AudioClip audioClip;

	/**
	 * 判断游戏是否正在进行
	 */
	boolean isActive;

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

		audioClip = new AudioClip(Objects.requireNonNull(PlayScene.class.getResource("/gameBGM1.wav")).toString());

		audioClip.setCycleCount(9999999);

		audioClip.play(0.5);



		// audioClip.loop();
		Tank player = new PlayerTank(this, 200, 400, Direction.DIR_UP, TankWar.PLAYER_NAME);

		setActive();

		TankWar.levels[0].setParent(this);
		TankWar.levels[0].setPlayer(player);
		TankWar.levels[0].start();

	}

	public static final int MAX_ENEMY_COUNT = 10;
	public static final int BORN_ENEMY_INTERVAL = 5000;





	public void levelPass() {

	}
	/**
	 * 用于设置游戏结束的方法
	 */
	public void gameOver() {
		audioClip.stop();

		// audioClip.isPlaying();

		isActive = false;
		try {
			gameThread.join();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		backGround.setActive(false);
		sceneClear();
		MapTile.base = 2;
		EnemyTank.setCount(0);
		OverBackGround overBackGround = new OverBackGround(this);
		addGameObject(overBackGround);
	}


	/**
	 * 关卡类，每一关对应该类的一个对象
	 */
	class level extends Thread {

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
		public static final String OVER_NOTICE = new String("按Enter键继续...");

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
