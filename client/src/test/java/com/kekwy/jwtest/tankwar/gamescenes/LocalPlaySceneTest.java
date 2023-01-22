package com.kekwy.jwtest.tankwar.gamescenes;

import com.kekwy.jw.Main;
import com.kekwy.jw.tankwar.GameObject;
import com.kekwy.jw.tankwar.gamescenes.LocalPlayScene;
import com.kekwy.jw.tankwar.tank.PlayerTank;
import javafx.application.Application;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class LocalPlaySceneTest {

	@Test
	void saveAndLoad() {

		LocalPlayScene scene;
		try {
			new Thread(() -> Main.main(null)).start();
			Thread.sleep(1000);
			scene = new LocalPlayScene();
			scene.start();
			// 等待游戏运行十秒钟
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		// 获取游戏对象列表
		List<GameObject> objectList = scene.saveToDisk();
		File file = new File("./save/");

		assertTrue(file.exists());
		assertTrue(file.isDirectory());
		assertTrue(file.listFiles().length > 0);

		LocalPlayScene newScene = new LocalPlayScene();
		List<GameObject> newObjectList = newScene.loadFromDisk(file.listFiles()[0]);

		// 比较保存前和恢复后对象的数量。
		assertEquals(objectList.size(), newObjectList.size());

		for (int i = 0; i < newObjectList.size(); i++) {
			assertEquals(objectList.get(i), newObjectList.get(i));
		}

	}
}