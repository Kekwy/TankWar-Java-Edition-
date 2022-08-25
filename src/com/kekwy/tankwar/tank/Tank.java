package com.kekwy.tankwar.tank;

import com.kekwy.gameengine.GameObject;
import com.kekwy.gameengine.GameScene;

import com.kekwy.tankwar.util.Direction;
import com.kekwy.tankwar.util.TankWarUtil;

import java.awt.*;

public abstract class Tank extends GameObject {



	public enum State {
		STATE_IDLE,
		STATE_MOVE,
		STATE_DIE,
	}

	public static final int DEFAULT_RADIUS = 20;
	public static final int DEFAULT_SPEED = 4;
	public static final int DEFAULT_HP = 1000;
	public static final int DEFAULT_ATK = 100;
	public static final Direction DEFAULT_DIR = Direction.DIR_DOWN;
	public static final State DEFAULT_STATE = State.STATE_IDLE;


	private int hp = DEFAULT_HP, atk = DEFAULT_ATK, speed = DEFAULT_SPEED, radius = DEFAULT_RADIUS;

	private Direction forward = DEFAULT_DIR;
	private State state = DEFAULT_STATE;

	private Color color;

	public Tank(GameScene parent) {
		super(parent);
		// initTank();
	}

	public Tank(GameScene parent, int x, int y, Direction forward) {
		super(parent);
		initTank(x, y, forward);
	}



	protected void initTank(int x, int y, Direction forward) {
		this.position.setX(x);
		this.position.setY(y);
		// fireTime = getParent().currentTimeMillis();
		this.forward = forward;
		this.color = TankWarUtil.getRandomColor();
	}

	private static final long FIRE_INTERVAL = 500;
	// long fireTime;

	public void fire() {
		// if(getParent().currentTimeMillis() - fireTime < FIRE_INTERVAL)
			// return;
		// fireTime = getParent().currentTimeMillis();
		// Position position = getPosition();
		int bulletX = this.position.getX();
		int bulletY = this.position.getY();
		switch (forward) {
			case DIR_UP -> bulletY -= radius;
			case DIR_DOWN -> bulletY += radius;
			case DIR_LEFT -> bulletX -= radius;
			case DIR_RIGHT -> bulletX += radius;
		}
		Bullet bullet = Bullet.createBullet(getParent(), atk, color, bulletX, bulletY, forward, this);
		getParent().addGameObject(bullet);
	}

	public int getHp() {
		return hp;
	}

	public void setHp(int hp) {
		this.hp = hp;
	}

	public int getAtk() {
		return atk;
	}

	public void setAtk(int atk) {
		this.atk = atk;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public int getRadius() {
		return radius;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	public Direction getForward() {
		return forward;
	}

	public void setForward(Direction forward) {
		this.forward = forward;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}


	public synchronized void move() {

		// Position position = getPosition();
		int x = this.position.getX();
		int y = this.position.getY();

		switch (forward) {
			case DIR_UP -> {
				if (y > radius + getParent().getUpBound()) {
					y -= speed;
				}
			}
			case DIR_DOWN -> {
				if (y < getParent().getDownBound() - radius - 6) {
					y += speed;
				}
			}
			case DIR_LEFT -> {
				if (x > getParent().getLeftBound() + radius + 6) {
					x -= speed;
				}
			}
			case DIR_RIGHT -> {
				if (x < getParent().getRightBound() - radius - 6) {
					x += speed;
				}
			}
		}

		// setPosition(position);
		this.position.setX(x);
		this.position.setY(y);

	}

	@Override
	public void fixedUpdate() {
		switch (state) {
			case STATE_DIE -> setActive(false);
			case STATE_MOVE -> move();
		}
	}
}
