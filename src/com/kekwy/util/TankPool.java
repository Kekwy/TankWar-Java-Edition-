package com.kekwy.util;

import com.kekwy.game.EnemyTank;
import com.kekwy.game.Tank;

import java.util.LinkedList;
import java.util.List;

public class TankPool {
	public static final int DEFAULT_POOL_SIZE = 16;
	// 用于保存所有子弹的容器
	private static final List<Tank> pool = new LinkedList<>();

	static {
		for (int i = 0; i < DEFAULT_POOL_SIZE; i++) {
			pool.add(new EnemyTank());
		}
	}

	public static Tank takeAway() {
		Tank tank = null;
		if (pool.size() == 0) {
			tank = new EnemyTank();
		} else {
			tank = pool.remove(0);
		}
		return tank;
	}

	public static void sendBack(Tank tank) {
		if(pool.size() == DEFAULT_POOL_SIZE) {
			return;
		}
		pool.add(tank);
		// System.out.println("子弹池中剩余：" + pool.size());
	}
}
