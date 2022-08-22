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
