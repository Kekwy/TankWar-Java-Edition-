package com.kekwy.util;

import com.kekwy.game.EnemyTank;
import com.kekwy.game.Tank;

import java.util.LinkedList;
import java.util.List;

public class TankPool {
	public static final int DEFAULT_POOL_SIZE = 10;
	// 用于保存所有子弹的容器
	private static final List<Tank> pool = new LinkedList<>();

	static {
		for (int i = 0; i < DEFAULT_POOL_SIZE; i++) {
			pool.add(new EnemyTank());
		}
	}

	// TODO 刚从对象池中申请的对象就被归还给对象池，但是同时在活动列表。【我是傻逼】
	//  紧接着再次申请时又将其返回，导致活动队列中两个变量指向同一个对象，在每一次循环时对同一个对象进行了两次事件触发，导致产生“超级tank”
	//  考虑使用临界区管理
	public static Tank takeAway() {
		Tank tank;
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
