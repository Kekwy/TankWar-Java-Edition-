package com.kekwy.util;

import com.kekwy.game.Bullet;
import com.kekwy.game.Explode;

import java.util.LinkedList;
import java.util.List;

public class ExplodesPool {
	public static final int DEFAULT_POOL_SIZE = 100;
	// 用于保存所有子弹的容器
	private static List<Explode> pool = new LinkedList<>();

	static {
		for (int i = 0; i < DEFAULT_POOL_SIZE; i++) {
			pool.add(new Explode());
		}
	}

	public static Explode takeAway() {
		Explode explode = null;
		if (pool.size() == 0) {
			explode = new Explode();
		} else {
			explode = pool.remove(0);
		}
		return explode;
	}

	public static void sendBack(Explode explode) {
		if(pool.size() == DEFAULT_POOL_SIZE) {
			return;
		}
		pool.add(explode);
		// System.out.println("子弹池中剩余：" + pool.size());
	}
}
