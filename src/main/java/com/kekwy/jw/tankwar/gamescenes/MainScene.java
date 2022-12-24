package com.kekwy.jw.tankwar.gamescenes;


import com.kekwy.jw.tankwar.GameObject;
import com.kekwy.jw.tankwar.GameScene;
import com.kekwy.jw.tankwar.util.ResourceUtil;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;


public class MainScene extends GameScene {

	private static final Font GAME_FONT = Font.loadFont(ResourceUtil.getAsPath("/Fonts/IPix.ttf"), 24);
	private static final String SCENE_TITLE = "坦克大战v2.0.0 by kekwy - 主界面";
	private static final int SCENE_WIDTH = 960, SCENE_HEIGHT = 560;

	public MainScene() {
		super(SCENE_WIDTH, SCENE_HEIGHT, SCENE_TITLE);
		new BackGround(this);
	}


	/**
	 * 主菜单背景对象
	 */
	private class BackGround extends GameObject {

		int menuIndex = 0;

		private static final String[] MENUS = {"单人游戏", "多人游戏", "游戏帮助", "关于游戏", "退出游戏",};

		public BackGround(GameScene parent) {
			super(parent);
			setLayer(0);
			parent.addGameObject(this);
			setActive(true);
			MainScene.this.setOnKeyPressed(keyEvent -> {
				KeyCode keyCode = keyEvent.getCode();
				switch (keyCode) {
					case W -> menuIndex = (menuIndex + MENUS.length - 1) % MENUS.length;
					case S -> menuIndex = (menuIndex + 1) % MENUS.length;
					case J -> {
						switch (menuIndex) {
							case 0 -> changeScene(new LocalPlayScene());
							// case 1 -> setInactive(TankWar.INDEX_ROOM_MENU);
							// case 2 -> setInactive(TankWar.INDEX_HELP);
//							case 3 -> setInactive(TankWar.INDEX_ABOUT);
							case 4 -> System.exit(0);
						}
					}
				}
			});
		}

		@Override
		public void refresh(GraphicsContext g, long timestamp) {
			g.setFill(Color.BLACK);
			g.fillRect(0, 0, SCENE_WIDTH, SCENE_HEIGHT);

			g.setFont(GAME_FONT);
			final int STR_WIDTH = 70;
			final int DIS = 50;
			int x = (SCENE_WIDTH - STR_WIDTH) >> 1;
			int y = SCENE_HEIGHT / 3;

			for (int i = 0; i < MENUS.length; i++) {
				if (i == menuIndex) {
					g.setFill(Color.RED);
				} else {
					g.setFill(Color.WHITE);
				}
				g.fillText(MENUS[i], x, y + DIS * i);
			}
		}
	}

}
