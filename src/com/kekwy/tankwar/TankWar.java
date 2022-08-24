package com.kekwy.tankwar;

import com.kekwy.gameengine.GameEngine;
import com.kekwy.gameengine.GameEntry;
import com.kekwy.gameengine.GameScene;
import com.kekwy.tankwar.gamescenes.*;

import java.util.ArrayList;
import java.util.List;

public class TankWar extends GameEntry {


	private final List<Class<? extends GameScene>> gameScenes = new ArrayList<>();

	@Override
	public List<Class<? extends GameScene>> getGameScenes() {
		return gameScenes;
	}

	public static final int INDEX_MAIN_MENU = 0;
	public static final int INDEX_ROOM_MENU = 1;
	public static final int INDEX_HELP = 2;
	public static final int INDEX_ABOUT = 3;
	public static final int INDEX_PLAY = 4;


	public TankWar() {
		gameScenes.add(MainMenuScene.class);
		gameScenes.add(RoomMenuScene.class);
		gameScenes.add(HelpScene.class);
		gameScenes.add(AboutScene.class);
		gameScenes.add(PlayScene.class);
	}

	public static void main(String[] args) {

		GameEngine gameEngine = new GameEngine(new TankWar());

	}

}
