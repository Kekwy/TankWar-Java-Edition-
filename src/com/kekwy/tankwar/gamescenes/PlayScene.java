package com.kekwy.tankwar.gamescenes;

import com.kekwy.gameengine.GameEngine;
import com.kekwy.gameengine.GameFrame;
import com.kekwy.gameengine.GameObject;
import com.kekwy.gameengine.GameScene;

import com.kekwy.tankwar.tank.EnemyTank;
import com.kekwy.tankwar.tank.PlayerTank;
import com.kekwy.tankwar.tank.Tank;
import com.kekwy.tankwar.util.Direction;
import com.kekwy.tankwar.util.TankWarUtil;

import java.awt.*;
import java.awt.event.KeyEvent;

public class PlayScene extends GameScene {


	private static final String GAME_TITLE = "坦克大战v1.0.0 by kekwy - 单人游戏";
	private static final int FRAME_WIDTH = 960, FRAME_HEIGHT = 540;

	static class BackGround extends GameObject {

		public BackGround(GameScene parent) {
			super(parent);
			setLayer(0);
			setActive(true);
			parent.addGameObject(this);
		}

		@Override
		public void render(Graphics g) {
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, FRAME_WIDTH, FRAME_HEIGHT);
		}
	}


	BackGround backGround;

	boolean gaming = false;
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

		new PlayerTank(this, 200, 400, Direction.DIR_UP, "Player1");

		setActive();

		gaming = true;
		// 定时生成敌人
		new Thread(() -> {
			int enemyCount = 1, spawnX;

			while (gaming) {
				try {
					Thread.sleep(BORN_ENEMY_INTERVAL);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}

				if(EnemyTank.getCount() >= MAX_ENEMY_COUNT)
				{
					continue;
				}

				int number = TankWarUtil.getRandomNumber(0, 2);
				if (number == 0) {
					spawnX = getLeftBound() + Tank.DEFAULT_RADIUS + 6;
				} else {
					spawnX = getRightBound() - Tank.DEFAULT_RADIUS - 6;
				}

				Tank enemyTank = EnemyTank.createEnemyTank(this, spawnX,
						getUpBound() + Tank.DEFAULT_RADIUS, "Enemy" + enemyCount);

				enemyCount++;

				addGameObject(enemyTank);

			}
		}).start();
	}

	public static final int MAX_ENEMY_COUNT = 10;
	public static final int BORN_ENEMY_INTERVAL = 5000;



	class OverBackGround extends GameObject{

		static Image overImg = null;
		public static final Font OVER_FONT = new Font("Minecraft 常规", Font.PLAIN, 18);
		public static final String OVER_NOTICE = new String("按Enter键继续...");
		public OverBackGround(GameScene parent) {
			super(parent);
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
			if(keyCode == KeyEvent.VK_ENTER) {
				PlayScene.this.setInactive(0);
			}
		}
	}

	public void gameOver() {
		gaming = false;
		backGround.setActive(false);
		sceneClear();
		OverBackGround overBackGround = new OverBackGround(this);
		addGameObject(overBackGround);
	}

}
