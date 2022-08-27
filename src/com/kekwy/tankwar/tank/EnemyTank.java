package com.kekwy.tankwar.tank;

import com.kekwy.gameengine.GameScene;
import com.kekwy.tankwar.util.Direction;
import com.kekwy.tankwar.util.ObjectPool;
import com.kekwy.tankwar.util.TankWarUtil;

import java.awt.*;
import java.util.concurrent.Semaphore;

public class EnemyTank extends Tank {

	long lastChangTime = 0;

	static int count = 0;
	static Semaphore mutex_count = new Semaphore(1);

	public EnemyTank(GameScene parent) {
		super(parent);
	}

	long fireTime = 0;

	public static final long FIRE_INTERVAL = 500;

	private int changeInterval = TankWarUtil.getRandomNumber(1000, 2000);

	@Override
	public void fixedUpdate() {
		super.fixedUpdate();
		if(getState() == State.STATE_DIE) {
			try {
				mutex_count.acquire();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
			count--;
			mutex_count.release();
			setActive(false);
		}
		if (getParent().currentTimeMillis() - lastChangTime > changeInterval) {
			// 随机一个状态
			changeInterval = TankWarUtil.getRandomNumber(1000, 2000);
			setState(State.values()[TankWarUtil.getRandomNumber(0, 2)]);
			setForward(Direction.values()[TankWarUtil.getRandomNumber(0, 4)]);
			lastChangTime = getParent().currentTimeMillis();
		}
		if (Math.random() < 0.05 && getParent().currentTimeMillis() - fireTime > FIRE_INTERVAL) {
			fireTime = getParent().currentTimeMillis();
			fire();
		}
	}

	private static final Image[] tankImg;

	static {
		tankImg = new Image[4];
		tankImg[0] = TankWarUtil.createImage("/enemy1U.gif");
		tankImg[1] = TankWarUtil.createImage("/enemy1D.gif");
		tankImg[2] = TankWarUtil.createImage("/enemy1L.gif");
		tankImg[3] = TankWarUtil.createImage("/enemy1R.gif");
	}

	@Override
	public void render(Graphics g) {
		super.render(g);
		g.drawImage(tankImg[getForward().ordinal()], position.getX() - getRadius(), position.getY() - getRadius(),
				2 * getRadius(), 2 * getRadius(), null);
	}

	private static final ObjectPool tankPool = new ObjectPool(EnemyTank.class, 10);

	public static Tank createEnemyTank(GameScene parent, int x, int y, String name) {
		Tank tank = (Tank) tankPool.getObject();

		tank.setParent(parent);
		tank.initTank(x, y, Direction.DIR_DOWN, name);
		tank.setState(State.STATE_MOVE);

		try {
			mutex_count.acquire();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		count++;
		mutex_count.release();
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
