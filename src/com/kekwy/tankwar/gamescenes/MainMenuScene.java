package com.kekwy.tankwar.gamescenes;

import com.kekwy.gameengine.GameFrame;
import com.kekwy.gameengine.GameScene;

public class MainMenuScene extends GameScene {

	private static final String GAME_TITLE = "坦克大战v1.0.0 by kekwy - 主界面";
	private static final int FRAME_WIDTH = 960, FRAME_HEIGHT = 540;
	public MainMenuScene(GameFrame gameFrame) {
		super(gameFrame);
		System.out.println("初始化");

		// 使用默认窗口
		setGameFrame(gameFrame);
		// 新建窗口
		// setGameFrame(new GameFrame());


		setTitle(GAME_TITLE);
		setSize(FRAME_WIDTH, FRAME_HEIGHT);
		setResizable(false);
		setLocation();

		setActive(true);
	}

}
