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

public class PlayScene extends GameScene {


	private static final String GAME_TITLE = "坦克大战v1.0.0 by kekwy - 单人游戏";
	private static final int FRAME_WIDTH = 960, FRAME_HEIGHT = 540;

	static class BackGround extends GameObject {

		public BackGround(GameScene parent) {
			super(parent);
			setLayer(0);
			parent.addGameObject(this);
		}

		@Override
		public void render(Graphics g) {
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, FRAME_WIDTH, FRAME_HEIGHT);
		}
	}


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

		new BackGround(this);

		new PlayerTank(this, 200, 400, Direction.DIR_UP, "Player1");

		setActive();

		// 定时生成敌人
		new Thread(() -> {
			int enemyCount = 0, spawnX;

			while (isActive()) {
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
					spawnX = getLeftBound() + Tank.DEFAULT_RADIUS;
				} else {
					spawnX = getRightBound() - Tank.DEFAULT_RADIUS;
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

}
