package com.kekwy.gameengine;

import com.kekwy.GameSettings;

import java.lang.reflect.InvocationTargetException;


public final class GameMain {


	private static int State;

	private static GameFrame gameFrame;

	private static GameEntry gameEntry;

	public static GameEntry getGameEntry() {
		return gameEntry;
	}

	private static Thread currentThread;
	private static GameScene currentScene;

	public static GameScene getCurrentScene() {
		return currentScene;
	}
/*
		 public static GameScene[] getGameScenes() {
		 return gameScenes;
		 }
		*/

	public static Thread getCurrentThread() {
		return currentThread;
	}

	public static void main(String[] args) {

		try {
			gameEntry = (GameEntry) GameSettings.gameEntryClass.getConstructor().newInstance();
		} catch (NoSuchMethodException | IllegalAccessException |
		         InstantiationException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}

		/**
		 * before running
		 */
		Class<?>[] gameScenes = gameEntry.getGameScenes();

		if (gameScenes == null) {
			System.out.println("未配置游戏场景");
			System.exit(-1);
		}

		while (true) {
			try {
				currentScene = (GameScene) gameScenes[0].getConstructor(GameFrame.class).newInstance();
			} catch (InstantiationException | IllegalAccessException | InvocationTargetException |
			         NoSuchMethodException e) {
				throw new RuntimeException(e);
			}

		}
	}
}

