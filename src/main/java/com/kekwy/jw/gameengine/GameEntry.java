package com.kekwy.jw.gameengine;

import java.util.List;

@Deprecated
public abstract class GameEntry {
	// static Map<Class<? extends GameObject>, GameObject> gameObjects;
	public abstract List<Class<? extends GameScene>> getGameScenes();

	// public abstract GameEntry gatGameEntry();

}
