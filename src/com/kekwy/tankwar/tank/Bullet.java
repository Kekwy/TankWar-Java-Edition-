package com.kekwy.tankwar.tank;

import com.kekwy.gameengine.GameObject;
import com.kekwy.gameengine.GameScene;
import com.kekwy.gameengine.util.Position;
import com.kekwy.tankwar.util.Direction;
import com.kekwy.tankwar.util.ObjectPool;

import java.awt.*;

public class Bullet extends GameObject {

	public static final int DEFAULT_BULLET_SPEED = 6;
	public static final int DEFAULT_BULLET_RADIUS = 4;

	private static final ObjectPool bulletPool = new ObjectPool(Bullet.class);
	private int atk;

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	private int speed = DEFAULT_BULLET_SPEED;


	private final int radius = DEFAULT_BULLET_RADIUS;
	private Direction forward;
	private Color color;

	private Tank from;

	public static Bullet createBullet(GameScene parent, int atk, Color color, int x, int y,
	                                  Direction forward, Tank from) {
		Bullet bullet = (Bullet) bulletPool.getObject();
		bullet.setParent(parent);
		bullet.setAtk(atk);
		bullet.setColor(color);
		bullet.position.setX(x);
		bullet.position.setY(y);
		bullet.setForward(forward);
		bullet.setFrom(from);
		return bullet;
	}

	private void setForward(Direction forward) {
		this.forward = forward;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public void setAtk(int atk) {
		this.atk = atk;
	}

	public int getAtk() {
		return atk;
	}

	@Override
	public void render(Graphics g) {
		// Position position = getPosition();
		g.setColor(color);
		g.fillOval(this.position.getX() - radius, this.position.getY() - radius,
				radius << 1, radius << 1);
	}


	@Override
	public void fixedUpdate() {
		// Position position = getPosition();
		int x = this.position.getX();
		int y = this.position.getY();

		if (x < getParent().getLeftBound() || x > getParent().getRightBound()
				|| y < getParent().getUpBound() || y > getParent().getDownBound())
			setActive(false);

		switch (forward) {
			case DIR_UP -> y -= speed;
			case DIR_DOWN -> y += speed;
			case DIR_LEFT -> x -= speed;
			case DIR_RIGHT -> x += speed;
		}

		// setPosition(position);
		this.position.setX(x);
		this.position.setY(y);

	}

	/**
	 * 通过反射调用
	 */
	public Bullet(GameScene parent) {
		super(parent);
	}


	public Tank getFrom() {
		return from;
	}

	public void setFrom(Tank from) {
		this.from = from;
	}
}
