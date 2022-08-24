package com.kekwy.tankwar.tank;

import com.kekwy.gameengine.GameObject;
import com.kekwy.gameengine.GameScene;
import com.kekwy.gameengine.util.Position;
import com.kekwy.tankwar.util.TankWarUtil;

import java.awt.*;

public abstract class Tank extends GameObject {

	public enum Direction {
		DIR_UP,
		DIR_DOWN,
		DIR_LEFT,
		DIR_RIGHT,
	}

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

	public Tank(GameScene parent, Position position, Direction forward) {
		super(parent);
		initTank(position, forward);
	}

	long fireTime;

	protected void initTank(Position position, Direction forward) {
		setPosition(position);
		fireTime = getParent().currentTimeMillis();
		this.forward = forward;
		this.color = TankWarUtil.getRandomColor();
	}


	public void fire() {

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

	public synchronized void setForward(Direction forward) {
		this.forward = forward;
	}

	public State getState() {
		return state;
	}

	public synchronized void setState(State state) {
		this.state = state;
	}


	public synchronized void move() {

		Position position = getPosition();

		switch (forward) {
			case DIR_UP -> {
				if (position.y > radius + getParent().getUpBound()) {
					position.y -= speed;
				}
			}
			case DIR_DOWN -> {
				if (position.y < getParent().getDownBound() - radius - 2) {
					position.y += speed;
				}
			}
			case DIR_LEFT -> {
				if (position.x > getParent().getLeftBound() + radius + 6) {
					position.x -= speed;
				}
			}
			case DIR_RIGHT -> {
				if (position.x < getParent().getRightBound() - radius - 6) {
					position.x += speed;
				}
			}
		}

		setPosition(position);

	}

	@Override
	public void fixedUpdate() {
		switch (state) {
			case STATE_DIE -> setActive(false);
			case STATE_MOVE -> move();
		}
	}
}
