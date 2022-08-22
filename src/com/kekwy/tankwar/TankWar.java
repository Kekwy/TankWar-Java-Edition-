package com.kekwy.tankwar;

import com.kekwy.gameengine.GameEntry;
import com.kekwy.gameengine.GameScene;
import com.kekwy.tankwar.gamescenes.*;

public class TankWar extends GameEntry {


	private final Class<?>[] gameScenes = new Class<>[5];

	@Override
	public Class<?>[] getGameScenes() {
		return gameScenes;
	}

	public TankWar() {
		gameScenes[0] = MainMenuScene.class;
		gameScenes[1] = RoomMenuScene.class;
		gameScenes[2] = HelpScene.class;
		gameScenes[3] = AboutScene.class;
		gameScenes[4] = PlayScene.class;
	}
}
