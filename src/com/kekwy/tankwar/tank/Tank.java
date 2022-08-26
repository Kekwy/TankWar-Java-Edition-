package com.kekwy.tankwar.tank;

import com.kekwy.gameengine.GameObject;
import com.kekwy.gameengine.GameScene;

import com.kekwy.tankwar.util.Direction;
import com.kekwy.tankwar.util.TankWarUtil;

import java.awt.*;
import java.util.List;

public abstract class Tank extends GameObject {



	public enum State {
		STATE_IDLE,
		STATE_MOVE,
		STATE_DIE,
	}

	public static final int DEFAULT_RADIUS = 20;
	public static final int DEFAULT_SPEED = 3;
	public static final int DEFAULT_HP = 1000;
	public static final int DEFAULT_ATK = 100;
	public static final Direction DEFAULT_DIR = Direction.DIR_DOWN;
	public static final State DEFAULT_STATE = State.STATE_IDLE;


	private int hp = DEFAULT_HP, atk = DEFAULT_ATK, speed = DEFAULT_SPEED;

	private Direction forward = DEFAULT_DIR;
	private State state = DEFAULT_STATE;

	private Color color;

	private final HPBar hpBar;

	public Tank(GameScene parent) {
		super(parent);
		hpBar = new HPBar(parent);
		setRadius(DEFAULT_RADIUS);
		setColliderType(ColliderType.COLLIDER_TYPE_RECT);
		setLayer(1);
		// initTank();
	}

	public Tank(GameScene parent, int x, int y, Direction forward, String name) {
		super(parent);
		hpBar = new HPBar(parent);
		setRadius(DEFAULT_RADIUS);
		setColliderType(ColliderType.COLLIDER_TYPE_RECT);
		initTank(x, y, forward, name);
		setLayer(1);
	}

	@Override
	public void collide(List<GameObject> gameObjects) {
		for (GameObject gameObject : gameObjects) {
			if(gameObject.getClass().equals(Bullet.class)) {
				Bullet bullet = (Bullet)gameObject;
				if(!bullet.getFrom().getClass().equals(this.getClass())) {
					hp -= bullet.getAtk();
					bullet.setActive(false);
				}
			}
		}
	}

	protected void initTank(int x, int y, Direction forward, String name) {
		this.position.setX(x);
		this.position.setY(y);
		// fireTime = getParent().currentTimeMillis();
		this.forward = forward;

		// 防止颜色过暗
		do {
			this.color = TankWarUtil.getRandomColor();
		} while (color.getRed() + color.getGreen() + color.getBlue() < 100);
		this.name = name;
		setActive(true);
	}


	public void fire() {
		// if(getParent().currentTimeMillis() - fireTime < FIRE_INTERVAL)
			// return;
		// fireTime = getParent().currentTimeMillis();
		// Position position = getPosition();
		int bulletX = this.position.getX();
		int bulletY = this.position.getY();
		switch (forward) {
			case DIR_UP -> bulletY -= getRadius();
			case DIR_DOWN -> bulletY += getRadius();
			case DIR_LEFT -> bulletX -= getRadius();
			case DIR_RIGHT -> bulletX += getRadius();
		}
		Bullet bullet = Bullet.createBullet(getParent(), atk, color, bulletX, bulletY, forward, this);
		getParent().addGameObject(bullet);
		// System.out.println("fire");
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
				if (y > getRadius() + getParent().getUpBound()) {
					y -= speed;
				}
			}
			case DIR_DOWN -> {
				if (y < getParent().getDownBound() - getRadius() - 6) {
					y += speed;
				}
			}
			case DIR_LEFT -> {
				if (x > getParent().getLeftBound() + getRadius() + 6) {
					x -= speed;
				}
			}
			case DIR_RIGHT -> {
				if (x < getParent().getRightBound() - getRadius() - 6) {
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

	private class HPBar extends GameObject{
		public static final int BAR_LENGTH = 50;
		public static final int BAR_HEIGHT = 5;

		public HPBar(GameScene parent) {
			super(parent);
			setLayer(1);
			if(parent != null)
				parent.addGameObject(this);
		}

		public void render(Graphics g) {
			int x = Tank.this.position.getX();
			int y = Tank.this.position.getY();
			// System.out.println("HPBar render");
			g.setColor(Color.RED);
			g.fillRect(x - Tank.this.getRadius(), y - Tank.this.getRadius() - BAR_HEIGHT * 2,
					hp * BAR_LENGTH / DEFAULT_HP, BAR_HEIGHT);
			g.setColor(Color.white);
			g.drawRect(x - Tank.this.getRadius(), y - Tank.this.getRadius() - BAR_HEIGHT * 2,
					BAR_LENGTH, BAR_HEIGHT);
		}

		@Override
		public boolean isActive() {
			return Tank.this.isActive();
		}
	}

	String name;
	static final Font NAME_FONT = new Font("Minecraft 常规", Font.PLAIN, 14);
	@Override
	public void render(Graphics g) {
		g.setColor(color);
		g.setFont(NAME_FONT);
		g.drawString(name, position.getX() - getRadius(), position.getY() - getRadius() - 14);
	}

	@Override
	public void setParent(GameScene parent) {
		super.setParent(parent);
		hpBar.setParent(parent);
		parent.addGameObject(hpBar);
	}
}
