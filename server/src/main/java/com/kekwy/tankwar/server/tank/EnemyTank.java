package com.kekwy.tankwar.server.tank;

import com.kekwy.tankwar.server.GameServer;
import com.kekwy.tankwar.util.Direction;
import com.kekwy.tankwar.server.util.ObjectPool;
import com.kekwy.tankwar.util.RandomGen;
import com.kekwy.tankwar.server.GameScene;

import java.util.concurrent.Semaphore;

public class EnemyTank extends Tank {

	long lastChangTime = 0;

	static int count = 0;
	static Semaphore mutex_count = new Semaphore(10);

	public EnemyTank(GameScene parent, GameServer server) {
		super(parent, server);
	}

	long fireTime = 0;

	public static final long FIRE_INTERVAL = 500;

	private int changeInterval = (int) RandomGen.getRandomNumber(1000, 2000);

	public static void setCount(int i) {
		count = i;
	}

	@Override
	public void check(long timestamp) {
		if(getState() == State.STATE_DIE) {
			try {
				mutex_count.acquire();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
			count--;
			if (count == 0) {
				synchronized (EnemyTank.class) {
					EnemyTank.class.notify();
				}
			}
			mutex_count.release();
			setActive(false);
			tankPool.returnObject(this);
		}
		if (timestamp - lastChangTime > changeInterval) {
			// 随机一个状态
			changeInterval = (int)RandomGen.getRandomNumber(1000, 2000);
			setState(State.values()[(int)RandomGen.getRandomNumber(0, 2)]);
			setDirection(Direction.values()[(int)RandomGen.getRandomNumber(0, 4)]);
			lastChangTime = timestamp;
		}
		if (Math.random() < 0.05 && timestamp - fireTime > FIRE_INTERVAL) {
			fireTime = timestamp;
			fire();
		}
	}

	private static final ObjectPool tankPool = new ObjectPool(EnemyTank.class, 1);

	public static final int ENEMY_TANK_MAX_HP = 500;
	public static Tank createEnemyTank(GameScene parent, double x, double y, String name, int group) {
		Tank tank = (Tank) tankPool.getObject();

		tank.setParent(parent);
		tank.initTank(x, y, Direction.DIR_DOWN, name, group);
		tank.setState(State.STATE_MOVE);

		try {
			mutex_count.acquire();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		count++;
		mutex_count.release();
		tank.setHp(ENEMY_TANK_MAX_HP);
		tank.setMaxHp(ENEMY_TANK_MAX_HP);
		return tank;
	}

	public static int getCount() {
		int count;
		try {
			mutex_count.acquire();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		count = EnemyTank.count;
		mutex_count.release();
		return count;
	}

}
