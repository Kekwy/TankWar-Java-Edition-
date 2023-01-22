package com.kekwy.jw.tankwar.tank;

import com.kekwy.jw.tankwar.GameScene;
import com.kekwy.jw.tankwar.util.Direction;
import com.kekwy.jw.tankwar.util.ObjectPool;
import com.kekwy.jw.tankwar.util.TankWarUtil;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.Objects;
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
		// 若当前时间与上次状态改变时间差值大于状态改变间隔
		// （状态改变间隔同样由每次进行状态更新时随机生成），
		// 进行敌人坦克的状态改变。
		if (timestamp - lastChangTime > changeInterval) {
			// 随机生成下一次的状态改变间隔
			changeInterval = (int)TankWarUtil.getRandomNumber(1000, 2000);
			// 随机设置一个状态：闲置或移动
			setState(State.values()[(int)TankWarUtil.getRandomNumber(0, 2)]);
			// 随机设置一个方向：上、下、左、右
			setDirection(Direction.values()[(int)TankWarUtil.getRandomNumber(0, 4)]);
			// 更新上次状态改变时间
			lastChangTime = timestamp;
		}
		// 开火概率为 0.05，并且需要大于开火间隔
		if (Math.random() < 0.05 && timestamp - fireTime > FIRE_INTERVAL) {
			// 更新上次开火时间
			fireTime = timestamp;
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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!super.equals(o)) return false;
		if (o == null || getClass() != o.getClass()) return false;
		EnemyTank enemyTank = (EnemyTank) o;
		return lastChangTime == enemyTank.lastChangTime && fireTime == enemyTank.fireTime && changeInterval == enemyTank.changeInterval;
	}

	@Override
	public int hashCode() {
		return Objects.hash(lastChangTime, fireTime, changeInterval);
	}
}
