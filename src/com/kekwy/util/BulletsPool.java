package com.kekwy.util;

import com.kekwy.game.Bullet;

import java.util.LinkedList;
import java.util.List;

public class BulletsPool {
	public static final int DEFAULT_POOL_SIZE = 300;
	// 用于保存所有子弹的容器
	private static List<Bullet> pool = new LinkedList<>();

	static {
		for (int i = 0; i < DEFAULT_POOL_SIZE; i++) {
			pool.add(new Bullet());
		}
	}

	public static Bullet takeAway() {
		Bullet bullet = null;
		if (pool.size() == 0) {
			bullet = new Bullet();
		} else {
			bullet = pool.remove(0);
		}
		return bullet;
	}

	public static void sendBack(Bullet bullet) {
		if(pool.size() == DEFAULT_POOL_SIZE) {
			return;
		}
		pool.add(bullet);
		// System.out.println("子弹池中剩余：" + pool.size());
	}
}
