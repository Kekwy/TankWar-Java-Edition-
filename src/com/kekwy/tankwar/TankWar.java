package com.kekwy.tankwar;

import com.kekwy.gameengine.GameEngine;
import com.kekwy.gameengine.GameEntry;
import com.kekwy.gameengine.GameScene;
import com.kekwy.tankwar.gamescenes.*;
import com.kekwy.tankwar.level.Level;
import com.kekwy.tankwar.util.TankWarUtil;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class TankWar extends GameEntry {


	public static final String PLAYER_NAME;
	public static final String PASSWORD;
	public static final int LEVEL_NUM;
	public static final String[] LEVEL_CONFIG_FILES;
	public static final boolean ENDLESS_MODE;
	public static final String MAP_FILE;
	public static final String SERVER_IP;
	public static final String SERVER_PORT;
	public static final boolean BGM_ENABLE;

	public static final Level[] levels;
	private static final List<Class<? extends GameScene>> gameScenes = new ArrayList<>();

	// 读取配置文件
	static {
		Properties props= TankWarUtil.loadProperties("./game.properties");
		PLAYER_NAME = props.getProperty("id");
		PASSWORD = props.getProperty("password");
		LEVEL_NUM = Integer.parseInt(props.getProperty("level_num"));
		LEVEL_CONFIG_FILES = new String[LEVEL_NUM];
		String contents = props.getProperty("level_config_files");
		int begin = 0;
		for (int i = 0; i < LEVEL_NUM; i++) {
			int ptr = contents.indexOf(".properties", begin) + 11;
			LEVEL_CONFIG_FILES[i] = contents.substring(begin, ptr);
			begin += ptr;
		}
		ENDLESS_MODE = Boolean.parseBoolean(props.getProperty("endless_mode"));
		MAP_FILE = props.getProperty("map_file");
		SERVER_IP = props.getProperty("server_ip");
		SERVER_PORT = props.getProperty("server_port");
		BGM_ENABLE = Boolean.parseBoolean(props.getProperty("bgm_enable"));

		gameScenes.add(MainMenuScene.class);
		gameScenes.add(RoomMenuScene.class);
		gameScenes.add(HelpScene.class);
		gameScenes.add(AboutScene.class);
		gameScenes.add(PlayScene.class);

		levels = new Level[LEVEL_NUM];
		for (int i = 0; i < levels.length; i++) {
			levels[i] = new Level(LEVEL_CONFIG_FILES[i]);
		}

	}

	public static final int INDEX_MAIN_MENU = 0;
	public static final int INDEX_ROOM_MENU = 1;
	public static final int INDEX_HELP = 2;
	public static final int INDEX_ABOUT = 3;
	public static final int INDEX_PLAY = 4;


	public static void main(String[] args) {

		GameEngine gameEngine = new GameEngine(new TankWar());
		gameEngine.start();

	}


	@Override
	public List<Class<? extends GameScene>> getGameScenes() {
		return gameScenes;
	}



}
