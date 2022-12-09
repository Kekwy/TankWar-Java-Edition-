package com.kekwy.jw.tankwar.gamescenes;

import com.kekwy.jw.gameengine.GameEngine;
import com.kekwy.jw.gameengine.GameFrame;
import com.kekwy.jw.gameengine.GameObject;
import com.kekwy.jw.gameengine.GameScene;

import java.awt.*;

public class RoomMenuScene extends GameScene {


	private static final String GAME_TITLE = "坦克大战v1.0.0 by kekwy - 主界面";
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

	public RoomMenuScene(GameFrame gameFrame, GameEngine gameEngine) {
		super(gameFrame, gameEngine);
	}
}
