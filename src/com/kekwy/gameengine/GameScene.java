package com.kekwy.gameengine;

import java.util.List;
import java.util.Map;

public abstract class GameScene implements Runnable{

	/**
	 * 当前游戏场景被开启后经过的时间
	 */
	private static long gameTime;

	private GameFrame gameFrame;

	/**
	 * runtime
	 */
	private static List<GameObject> doUpdate;
	private static List<GameObject> doFixedUpdate;
	private static List<GameObject> doCollide;
	private static List<GameObject> doRender;
	private static List<GameObject> doGameEvent;

	private static Map<Class<? extends GameObject>, Integer> runTimeClassList;


	public GameFrame getGameFrame() {
		return gameFrame;
	}

	public void setGameFrame(GameFrame gameFrame) {
		this.gameFrame = gameFrame;
	}


}
