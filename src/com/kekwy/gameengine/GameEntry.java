package com.kekwy.gameengine;

import java.util.List;

public abstract class GameEntry {
	// static Map<Class<? extends GameObject>, GameObject> gameObjects;
	public abstract List<Class<? extends GameScene>> getGameScenes();

	// public abstract GameEntry gatGameEntry();

}
