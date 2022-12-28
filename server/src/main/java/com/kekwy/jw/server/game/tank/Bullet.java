package com.kekwy.jw.server.game.tank;

import com.kekwy.jw.server.game.GameObject;
import com.kekwy.jw.server.game.GameScene;
import com.kekwy.jw.server.game.gamemap.MapTile;
import com.kekwy.jw.server.util.Direction;
import com.kekwy.jw.server.util.ObjectPool;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Bullet extends GameObject implements Runnable {

	private static int BULLET_SPEED = 6;
	public static final int DEFAULT_BULLET_RADIUS = 4;

	private static final ObjectPool bulletPool = new ObjectPool(Bullet.class, 100);
	private int atk;

	public int getSpeed() {
		return speed;
	}

	public static void setSpeed(int speed) {
		BULLET_SPEED = speed;
	}

	private int speed = BULLET_SPEED;


	// private final int radius = DEFAULT_BULLET_RADIUS;
	private Direction direction;
	private double r, g, b;

	private Tank from;

	public static Bullet createBullet(GameScene parent, int atk, double r, double g, double b, double x, double y,
	                                  Direction direction, Tank from) {
		Bullet bullet = (Bullet) bulletPool.getObject();
		// Bullet bullet = new Bullet(null);
		bullet.setParent(parent);
		bullet.setAtk(atk);
		bullet.setColor(r, g, b);
		bullet.transform.setX(x);
		bullet.transform.setY(y);
		bullet.setDirection(direction);
		bullet.setFrom(from);
		bullet.setActive(true);
		bullet.speed = BULLET_SPEED;
		bullet.isExit = false;
		return bullet;
	}

	private void setDirection(Direction direction) {
		this.direction = direction;
	}

	public void setColor(double r, double g, double b) {
		this.r = r;
		this.g = g;
		this.b = b;
	}

	public void setAtk(int atk) {
		this.atk = atk;
	}

	public int getAtk() {
		return atk;
	}

	private static final long UPDATE_INTERVAL = 20;

	boolean isExit = false;

	@SuppressWarnings("BusyWait")
	@Override
	public void run() {
		while (isActive()) {
			double x = this.transform.getX();
			double y = this.transform.getY();
			switch (direction) {
				case DIR_UP -> y -= speed;
				case DIR_DOWN -> y += speed;
				case DIR_LEFT -> x -= speed;
				case DIR_RIGHT -> x += speed;
			}
			getParent().update(this, x, y, 0);
			doCollide();
			try {
				Thread.sleep(UPDATE_INTERVAL);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
		synchronized (this) {
			isExit = true;
			this.notify();
		}
	}

	private final List<GameObject> collideList = new LinkedList<>();

	private void doCollide() {
		getParent().getObjectAroundTheGridCell(this, collideList);
		for (GameObject gameObject : collideList) {
			if (this.isCollide(gameObject)) {
				while (true) {
					if (!this.collideLock().tryLock()) {
						this.collideLock().lock();
					}
					if (!gameObject.collideLock().tryLock()) {
						this.collideLock().unlock();
						gameObject.collideLock().lock();
						gameObject.collideLock().unlock();
						continue;
					}
					break;
				}
				if (!this.isActive() || !gameObject.isActive()) {
					gameObject.collideLock().unlock();
					this.collideLock().unlock();
					if (!this.isActive()) return;
					else continue;
				}
				if (gameObject instanceof MapTile mapTile)
					mapTile.doCollide(this);
				gameObject.collideLock().unlock();
				this.collideLock().unlock();
			}
		}
		collideList.clear();
	}

	private static final ExecutorService SERVICE = Executors.newCachedThreadPool();

	@Override
	public void destroy() {
		// rectangle.intersects(rectangle);
		// javafx.geometry.BoundingBox
		Runnable helper = () -> {
			synchronized (this) {
				if (!isExit) {
					try {
						this.wait();
					} catch (InterruptedException e) {
						throw new RuntimeException(e);
					}
				}
				bulletPool.returnObject(this);
			}
		};

		synchronized (SERVICE) {
			SERVICE.execute(helper);
		}
	}


	/**
	 * 通过反射调用
	 */
	public Bullet(GameScene parent) {
		super(parent);
		setRadius(DEFAULT_BULLET_RADIUS);
		setColliderType(ColliderType.COLLIDER_TYPE_CIRCLE);
	}


	public Tank getFrom() {
		return from;
	}

	public void setFrom(Tank from) {
		this.from = from;
	}

}
