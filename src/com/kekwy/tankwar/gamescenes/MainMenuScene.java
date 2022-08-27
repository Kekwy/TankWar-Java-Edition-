package com.kekwy.tankwar.gamescenes;

import com.kekwy.gameengine.GameEngine;
import com.kekwy.gameengine.GameFrame;
import com.kekwy.gameengine.GameObject;
import com.kekwy.gameengine.GameScene;
import com.kekwy.tankwar.TankWar;

import java.awt.*;
import java.awt.event.KeyEvent;


public class MainMenuScene extends GameScene {


	private static final Font GAME_FONT = new Font("Minecraft 常规", Font.PLAIN, 24);
	private static final String GAME_TITLE = "坦克大战v1.0.0 by kekwy - 主界面";
	private static final int FRAME_WIDTH = 960, FRAME_HEIGHT = 540;


	/**
	 * 主菜单背景对象
	 */
	class BackGround extends GameObject {

		int menuIndex;

		private static final String[] MENUS = {
				"单人游戏",
				"多人游戏",
				"游戏帮助",
				"关于游戏",
				"退出游戏",
		};

		public BackGround(GameScene parent) {
			super(parent);
			setLayer(0);
			parent.addGameObject(this);
			setActive(true);
		}

		/**
		 * 背景的
		 * @param g 底层提供的画笔对象
		 */
		@Override
		public void render(Graphics g) {
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, FRAME_WIDTH, FRAME_HEIGHT);

			g.setFont(GAME_FONT);
			final int STR_WIDTH = 70;
			final int DIS = 50;
			int x = (FRAME_WIDTH - STR_WIDTH) >> 1;
			int y = FRAME_HEIGHT / 3;

			for (int i = 0; i < MENUS.length; i++) {
				if (i == menuIndex) {
					g.setColor(Color.RED);
				} else {
					g.setColor(Color.WHITE);
				}
				g.drawString(MENUS[i], x, y + DIS * i);
			}
		}

		/**
		 * 注册键盘监听事件
		 * @param keyCode 底层提供的键码
		 */
		@Override
		public void keyPressedEvent(int keyCode) {
			switch (keyCode) {
				case KeyEvent.VK_W -> menuIndex = (menuIndex + MENUS.length - 1) % MENUS.length;
				case KeyEvent.VK_S -> menuIndex = (menuIndex + 1) % MENUS.length;
				case KeyEvent.VK_J -> {
					switch (menuIndex) {
						case 0->setInactive(TankWar.INDEX_PLAY);
						case 1->setInactive(TankWar.INDEX_ROOM_MENU);
						case 2->setInactive(TankWar.INDEX_HELP);
						case 3->setInactive(TankWar.INDEX_ABOUT);
						case 4->setInactive(-1);
					}
				}
			}
		}
	}


	public MainMenuScene(GameFrame gameFrame, GameEngine gameEngine) {
		super(gameFrame, gameEngine);
		// System.out.println("初始化");

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

		setActive();
	}

}
