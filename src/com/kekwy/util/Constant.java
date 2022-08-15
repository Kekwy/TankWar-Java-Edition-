package com.kekwy.util;

import java.awt.*;

public class Constant {
	public static final String GAME_TITLE = "坦克大战v0.0 by Kekwy";

	public static final int FRAME_WIDTH = 960;
	public static final int FRAME_HEIGHT = 540;

	public static final int FRAME_X = (Toolkit.getDefaultToolkit().getScreenSize().width - FRAME_WIDTH) >> 1;
	public static final int FRAME_Y = (Toolkit.getDefaultToolkit().getScreenSize().height - FRAME_HEIGHT) >> 1;

	//游戏菜单
	public enum State {
		STATE_MENU,
		STATE_HELP,
		STATE_ABOUT,
		STATE_RUN,
		STATE_OVER,
	}

	public static final String[] MENUS = {
			"开始游戏",
			"继续游戏",
			"游戏帮助",
			"关于游戏",
			"退出游戏",
	};

	public static final Font GAME_FONT = new Font("Minecraft 常规", Font.TRUETYPE_FONT, 24);

	public static final int FLUSH_INTERVAL = 33;

	public static final int MAX_ENEMY_COUNT = 10;
	public static final int BORN_ENEMY_INTERVAL = 5000;
}
