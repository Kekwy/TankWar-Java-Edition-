package com.kekwy.tankwar.server.util;
import com.kekwy.tankwar.server.GameObject;
import com.kekwy.tankwar.server.GameScene;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class ObjectPool {
	public static final int DEFAULT_POOL_SIZE = 200;

	private final int maxSize;

	private final List<GameObject> pool = new LinkedList<>();

	Semaphore mutex_pool = new Semaphore(1);

	Constructor<? extends GameObject> constructor;

	public ObjectPool(Class<? extends GameObject> type, int size) {
		this.maxSize = size;
		initObjectPool(type);
	}


	public ObjectPool(Class<? extends GameObject> type) {
		maxSize = DEFAULT_POOL_SIZE;
		initObjectPool(type);
	}

	private void initObjectPool(Class<? extends GameObject> type) {
		try {
			constructor = type.getDeclaredConstructor(GameScene.class);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
		for (int i = 0; i < maxSize; i++) {
			try {
				pool.add((GameObject)constructor.newInstance((GameScene)null));
			} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public GameObject getObject() {
		GameObject gameObject;

		try {
			mutex_pool.acquire();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		if (pool.size() == 0) {
			try {
				gameObject = (GameObject)constructor.newInstance((GameScene)null);
			} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
				throw new RuntimeException(e);
			}
		} else {
			gameObject = pool.remove(0);
		}

		mutex_pool.release();

		return gameObject;
	}

	public void returnObject(GameObject gameObject) {

		try {
			mutex_pool.acquire();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		// System.out.println("对象池中剩余：" + pool.size());

		if (pool.size() == maxSize) {
			mutex_pool.release();
			return;
		}
		pool.add(gameObject);


		mutex_pool.release();


	}
}
