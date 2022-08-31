package com.kekwy.tankwar.tank;

import com.kekwy.gameengine.GameObject;
import com.kekwy.gameengine.GameScene;

import com.kekwy.tankwar.TankWar;
import com.kekwy.tankwar.effect.Blast;
import com.kekwy.tankwar.gamemap.MapTile;
import com.kekwy.tankwar.util.Direction;
import com.kekwy.tankwar.util.TankWarUtil;
import javafx.scene.media.AudioClip;

import java.awt.*;
import java.util.List;

public abstract class Tank extends GameObject {

	boolean visible = true;

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public boolean isVisible() {
		return visible;
	}

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
	private int maxHp = DEFAULT_HP;

	private Direction forward = DEFAULT_DIR;
	private State state = DEFAULT_STATE;

	private Color color;

	private final HPBar hpBar = new HPBar();

	public Tank(GameScene parent) {
		super(parent);
		setRadius(DEFAULT_RADIUS);
		setColliderType(ColliderType.COLLIDER_TYPE_RECT);
		setLayer(1);
		// initTank();
	}

	public Tank(GameScene parent, int x, int y, Direction forward, String name) {
		super(parent);
		setRadius(DEFAULT_RADIUS);
		setColliderType(ColliderType.COLLIDER_TYPE_RECT);
		initTank(x, y, forward, name);
		setLayer(1);
	}

	@Override
	public void collide(List<GameObject> gameObjects) {
		boolean isCover = true;

		for (GameObject gameObject : gameObjects) {
			if (gameObject instanceof Bullet bullet) {
				if (!bullet.getFrom().getClass().equals(this.getClass())) {
					hitSound.play();
					hp -= bullet.getAtk();
					Blast blast = Blast.createBlast(getParent(), bullet.position.getX(), bullet.position.getY());
					getParent().addGameObject(blast);
					bullet.setActive(false);
				}
			} else if (gameObject instanceof Tank && !gameObject.getClass().equals(this.getClass())) {
				hitSound.play();
				this.hp = 0;
				((Tank) gameObject).setHp(0);
				((Tank) gameObject).setState(State.STATE_DIE);
				Blast blast = Blast.createBlast(getParent(), this.position.getX(), this.position.getY());
				getParent().addGameObject(blast);
			} else if (gameObject instanceof MapTile mapTile) {
				if (mapTile.getType() == MapTile.Type.TYPE_COVER) {
					setVisible(false);
					isCover = false;
				} else {
					int x1 = position.getX(), y1 = position.getY();
					int x2 = mapTile.position.getX(), y2 = mapTile.position.getY();
					if (Math.abs(x1 - x2) > Math.abs(y1 - y2)) {
						if(x1 > x2) {
							x1 = x2 + mapTile.getRadius() + this.getRadius();
						} else {
							x1 = x2 - mapTile.getRadius() - this.getRadius();
						}
					} else {
						if(y1 > y2) {
							y1 = y2 + mapTile.getRadius() + this.getRadius();
						} else {
							y1 = y2 - mapTile.getRadius() - this.getRadius();
						}
					}
					this.position.setX(x1);
					this.position.setY(y1);
				}
			}
		}

		if (hp <= 0) {
			setState(State.STATE_DIE);
		}

		if (isCover) {
			setVisible(true);
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

	public void setMaxHp(int hp) {
		if(this.hp > hp) {
			this.hp = hp;
		}
		this.maxHp = hp;
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
		// case STATE_DIE -> setActive(false);
		if (state == State.STATE_MOVE) {
			move();
		}
	}

	private class HPBar {
		public static final int BAR_LENGTH = 50;
		public static final int BAR_HEIGHT = 5;

		public void render(Graphics g) {

			int x = Tank.this.position.getX();
			int y = Tank.this.position.getY();
			// System.out.println("HPBar render");
			g.setColor(Color.RED);
			g.fillRect(x - Tank.this.getRadius(), y - Tank.this.getRadius() - BAR_HEIGHT * 2,
					hp * BAR_LENGTH / maxHp, BAR_HEIGHT);
			g.setColor(Color.white);
			g.drawRect(x - Tank.this.getRadius(), y - Tank.this.getRadius() - BAR_HEIGHT * 2,
					BAR_LENGTH, BAR_HEIGHT);
		}
	}

	String name;
	static final Font NAME_FONT = new Font("Minecraft 常规", Font.PLAIN, 14);

	@Override
	public void render(Graphics g) {
		if (!isVisible())
			return;
		hpBar.render(g);
		g.setColor(color);
		g.setFont(NAME_FONT);
		g.drawString(name, position.getX() - getRadius(), position.getY() - getRadius() - 14);

	}


	static AudioClip hitSound = TankWar.hitSound;

}
