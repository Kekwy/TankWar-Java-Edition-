package com.kekwy.jw.gameengine;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

@Deprecated
public final class GameEngine {

	/**
	 * 游戏主体中总场景数
	 */
	private int sceneNumber;

	/**
	 * 下一个待切换的场景
	 * -1 表示退出游戏，主线程结束虚拟机
	 */
	private int NextScene;

	/**
	 * 仅可在GameScene中被调用
	 *
	 * @param nextScene 下一个场景的序号
	 */
	public void setNextScene(int nextScene) {
		if (nextScene < -1 || nextScene >= sceneNumber) {
			System.out.println("场景跳转时提供了非法编号");
			System.exit(0);
		}
		NextScene = nextScene;
	}


	private GameFrame gameFrame;

	private GameEntry gameEntry;

	public GameEntry getGameEntry() {
		return gameEntry;
	}


	private GameScene currentScene;

	public GameScene getCurrentScene() {
		return currentScene;
	}
/*
		 public static GameScene[] getGameScenes() {
		 return gameScenes;
		 }
		*/


	public GameEngine(GameEntry gameEntry) {
		this.gameEntry = gameEntry;
	}

	public void start() {
		List<Class<? extends GameScene>> gameScenes = gameEntry.getGameScenes();

		if (gameScenes == null) {
			System.out.println("未配置游戏场景");
			System.exit(-1);
		}

		gameFrame = new GameFrame();
		sceneNumber = gameScenes.size();
		NextScene = 0;

		while (true) {

			try {
				currentScene = gameScenes.get(NextScene).
						getConstructor(GameFrame.class, GameEngine.class).newInstance(gameFrame, this);
			} catch (InstantiationException | IllegalAccessException | InvocationTargetException |
			         NoSuchMethodException e) {
				throw new RuntimeException(e);
			}

			currentScene.start();

			if (NextScene == -1)
				System.exit(0);

		}
	}

}

