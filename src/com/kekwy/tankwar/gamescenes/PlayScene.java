package com.kekwy.tankwar.gamescenes;

import com.kekwy.gameengine.GameEngine;
import com.kekwy.gameengine.GameFrame;
import com.kekwy.gameengine.GameObject;
import com.kekwy.gameengine.GameScene;
import com.kekwy.gameengine.util.Position;
import com.kekwy.tankwar.tank.PlayerTank;
import com.kekwy.tankwar.tank.Tank;

import java.awt.*;

public class PlayScene extends GameScene {


	private static final String GAME_TITLE = "坦克大战v1.0.0 by kekwy - 单人游戏";
	private static final int FRAME_WIDTH = 960, FRAME_HEIGHT = 540;

	static class BackGround extends GameObject {

		public BackGround(GameScene parent) {
			super(parent);
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

		addGameObject(new BackGround(this));

		addGameObject(new PlayerTank(this, new Position(200, 400), Tank.Direction.DIR_UP));

		setActive();

		// 定时生成敌人

	}
}
