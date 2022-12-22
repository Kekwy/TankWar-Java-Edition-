package com.kekwy.jw.tankwar.tank;

import com.kekwy.jw.tankwar.GameScene;
import com.kekwy.jw.tankwar.util.Direction;
import com.kekwy.jw.tankwar.util.ObjectPool;
import com.kekwy.jw.tankwar.util.TankWarUtil;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.concurrent.Semaphore;

public class EnemyTank extends Tank {

	long lastChangTime = 0;

	static int count = 0;
	static Semaphore mutex_count = new Semaphore(10);

	public EnemyTank(GameScene parent) {
		super(parent);
	}

	long fireTime = 0;

	public static final long FIRE_INTERVAL = 500;

	private int changeInterval = (int)TankWarUtil.getRandomNumber(1000, 2000);

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
			changeInterval = (int)TankWarUtil.getRandomNumber(1000, 2000);
			setState(State.values()[(int)TankWarUtil.getRandomNumber(0, 2)]);
			setDirection(Direction.values()[(int)TankWarUtil.getRandomNumber(0, 4)]);
			lastChangTime = timestamp;
		}
//		if (Math.random() < 0.05 && getParent().currentTimeMillis() - fireTime > FIRE_INTERVAL) {
//			fireTime = getParent().currentTimeMillis();
//			fire();
//		}
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
	public void refresh(GraphicsContext g, long timestamp) {
		super.refresh(g, timestamp);
		g.drawImage(tankImg[getDirection().ordinal()], transform.getX() - getRadius(), transform.getY() - getRadius(),
				2 * getRadius(), 2 * getRadius());
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
