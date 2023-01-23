package com.kekwy.tankwar.client;

import com.kekwy.tankwar.client.level.Level;
import com.kekwy.tankwar.client.util.TankWarUtil;
import com.kekwy.tankwar.client.gamescenes.LocalPlayScene;
import com.kekwy.tankwar.client.gamescenes.MainScene;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

public class TankWar {

	public static final Media gameBGM0 =
			new Media(Objects.requireNonNull(TankWar.class.getResource("/music/gameBGM0.mp3")).toString());
	public static final Media gameBGM1 =
			new Media(Objects.requireNonNull(TankWar.class.getResource("/music/gameBGM1.mp3")).toString());
	public static final Media gameBGM2 =
			new Media(Objects.requireNonNull(TankWar.class.getResource("/music/gameBGM2.mp3")).toString());
	public static final Media gameBGM3 =
			new Media(Objects.requireNonNull(TankWar.class.getResource("/music/gameBGM3.mp3")).toString());
	public static final Media gameBGM4 =
			new Media(Objects.requireNonNull(TankWar.class.getResource("/music/gameBGM4.mp3")).toString());
	public static final AudioClip passBGM =
			new AudioClip(Objects.requireNonNull(TankWar.class.getResource("/music/passBGM.mp3")).toString());
	public static final AudioClip endBGM0 =
			new AudioClip(Objects.requireNonNull(TankWar.class.getResource("/music/endBGM0.mp3")).toString());
	public static final AudioClip endBGM1 =
			new AudioClip(Objects.requireNonNull(TankWar.class.getResource("/music/endBGM1.mp3")).toString());
	public static final AudioClip hitSound =
			new AudioClip(Objects.requireNonNull(TankWar.class.getResource("/sound/hit.wav")).toString());

	public static final List<Media> pve1BGM = new ArrayList<>();
	public static final List<Media> pve2BGM = new ArrayList<>();
	public static final List<Media> pvpBGM = new ArrayList<>();
	public static final List<Media> bossBGM = new ArrayList<>();

	static {
		pve1BGM.add(gameBGM0);
		pve2BGM.add(gameBGM3);
		pvpBGM.add(gameBGM4);
		bossBGM.add(gameBGM1);
		bossBGM.add(gameBGM2);
	}

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
	public static final Level finalLevel;
	private static final List<Class<? extends GameScene>> gameScenes = new ArrayList<>();

	// 读取配置文件
	static {
		Properties props= null;
		try {
			props = TankWarUtil.loadProperties(new FileInputStream("./game.properties"));
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
		PLAYER_NAME = props.getProperty("id");
		PASSWORD = props.getProperty("password");
		LEVEL_NUM = Integer.parseInt(props.getProperty("level_num"));
		// LEVEL_CONFIG_FILES = new String[LEVEL_NUM];
		String contents = props.getProperty("level_config_files");
		LEVEL_CONFIG_FILES = TankWarUtil.splitString(contents, ".properties", LEVEL_NUM);
		ENDLESS_MODE = Boolean.parseBoolean(props.getProperty("endless_mode"));
		MAP_FILE = props.getProperty("map_file");
		SERVER_IP = props.getProperty("server_ip");
		SERVER_PORT = props.getProperty("server_port");
		BGM_ENABLE = Boolean.parseBoolean(props.getProperty("bgm_enable"));

//		gameScenes.add(MainMenuScene.class);
//		gameScenes.add(RoomMenuScene.class);
//		gameScenes.add(HelpScene.class);
//		gameScenes.add(AboutScene.class);
//		gameScenes.add(PlayScene.class);

		levels = new Level[LEVEL_NUM];
		for (int i = 0; i < levels.length; i++) {
			try {
				levels[i] = new Level(new FileInputStream(LEVEL_CONFIG_FILES[i]));
			} catch (FileNotFoundException e) {
				throw new RuntimeException(e);
			}
		}
		finalLevel = new Level(TankWar.class.getResourceAsStream("/levels/config/final.properties"));
	}

	public static final GameScene MAIN_SCENE = new MainScene();
	public static final GameScene LOCAL_PLAY_SCENE = new LocalPlayScene();

	public static final int INDEX_MAIN_MENU = 0;
	public static final int INDEX_ROOM_MENU = 1;
	public static final int INDEX_HELP = 2;
	public static final int INDEX_ABOUT = 3;
	public static final int INDEX_PLAY = 4;


}
