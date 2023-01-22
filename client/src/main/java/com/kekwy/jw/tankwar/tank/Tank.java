package com.kekwy.jw.tankwar.tank;

import com.kekwy.jw.tankwar.GameObject;
import com.kekwy.jw.tankwar.GameScene;
import com.kekwy.jw.tankwar.TankWar;
import com.kekwy.jw.tankwar.effect.Blast;
import com.kekwy.jw.tankwar.gamemap.MapTile;
import com.kekwy.jw.tankwar.gamescenes.OnlinePlayScene;
import com.kekwy.jw.tankwar.util.Direction;
import com.kekwy.jw.tankwar.util.ResourceUtil;
import com.kekwy.jw.tankwar.util.TankWarUtil;
import com.kekwy.tankwar.server.io.FrameUpdate;
import com.kekwy.tankwar.server.io.Protocol;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public abstract class Tank extends GameObject implements Runnable {

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

	public static final int TANK_RADIUS = 20;
	public static final int DEFAULT_SPEED = 3;
	public static final int DEFAULT_HP = 1000;
	public static final int DEFAULT_ATK = 100;
	public static final Direction DEFAULT_DIR = Direction.DIR_DOWN;
	public static final State DEFAULT_STATE = State.STATE_IDLE;


	private int hp = DEFAULT_HP, atk = DEFAULT_ATK, speed = DEFAULT_SPEED;
	private int maxHp = DEFAULT_HP;

	private Direction direction = DEFAULT_DIR;
	private State state = DEFAULT_STATE;

	transient private Color color;

	private final HPBar hpBar = new HPBar();

	public Tank(GameScene parent) {
		super(parent);
		setRadius(TANK_RADIUS);
//		setColliderType(ColliderType.COLLIDER_TYPE_RECT);
		setLayer(1);
		// initTank();
	}

	boolean isOnline = false;

	public Tank(GameScene parent, double x, double y, Direction direction, String name, int group) {
		super(parent);
		setRadius(TANK_RADIUS);
//		setColliderType(ColliderType.COLLIDER_TYPE_RECT);
		initTank(x, y, direction, name, group);
		setLayer(1);
		isOnline = parent instanceof OnlinePlayScene;
	}

	private void move() {
		if (state == State.STATE_MOVE) {
			double x = this.transform.getX();
			double y = this.transform.getY();
			switch (direction) {
				case DIR_UP -> y -= speed;
				case DIR_DOWN -> y += speed;
				case DIR_LEFT -> x -= speed;
				case DIR_RIGHT -> x += speed;
			}
			this.getParent().update(this, x, y, TANK_RADIUS);
		}
	}


	@Override
	public void update(Protocol p) {
		FrameUpdate f = (FrameUpdate) p;
		this.getParent().update(this, f.x, f.y, TANK_RADIUS);
		this.setState(State.values()[f.state]);
		this.setDirection(Direction.values()[f.direction]);

	}

	private int group;

	private static final long UPDATE_INTERVAL = 20;

	@SuppressWarnings("BusyWait")
	@Override
	public void run() {
		if (isOnline) return;
		while (this.isActive()) {
			move();
//			fire();
			try {
				doCollide();
				check(System.currentTimeMillis());
				Thread.sleep(UPDATE_INTERVAL);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private final List<GameObject> collideList = new LinkedList<>();

	private void doCollide() {
		boolean isCovered = false;
		getParent().getObjectAroundTheGridCell(this, collideList);
		for (GameObject gameObject : collideList) {
			if (gameObject == this)
				continue;
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
					if (!this.isActive()) {
						return;
					} else {
						continue;
					}
				}
				if (gameObject instanceof Tank tank) {
					if (tank.group == this.group) {
						// TODO 队友 BUFF
					} else {
						hitSound.play();
						this.hp = 0;
						tank.setHp(0);
						tank.setState(State.STATE_DIE);
						Blast blast = Blast.createBlast(getParent(), this.transform.getX(), this.transform.getY());
						getParent().addGameObject(blast);
						tank.setHp(0);
					}
				} else if (gameObject instanceof Bullet bullet && bullet.getFrom().group != this.group) {
					hitSound.play();
					hp -= bullet.getAtk();
					if (hp < 0) hp = 0;
					Blast blast = Blast.createBlast(getParent(), bullet.transform.getX(), bullet.transform.getY());
					getParent().addGameObject(blast);
					bullet.setActive(false);
				} else if (gameObject instanceof MapTile mapTile) {
					if (mapTile.getType() == MapTile.Type.TYPE_COVER) {
						setVisible(false);
						isCovered = true;
					} else {
						double x1 = transform.getX(), y1 = transform.getY();
						double x2 = mapTile.transform.getX(), y2 = mapTile.transform.getY();
						if (Math.abs(x1 - x2) > Math.abs(y1 - y2)) {
							if (x1 > x2) {
								x1 = x2 + mapTile.getRadius() + this.getRadius();
							} else {
								x1 = x2 - mapTile.getRadius() - this.getRadius();
							}
						} else {
							if (y1 > y2) {
								y1 = y2 + mapTile.getRadius() + this.getRadius();
							} else {
								y1 = y2 - mapTile.getRadius() - this.getRadius();
							}
						}
						this.transform.setX(x1);
						this.transform.setY(y1);
					}
				}
				gameObject.collideLock().unlock();
				this.collideLock().unlock();
			}
		}
		collideList.clear();

		if (hp <= 0) {
			setState(State.STATE_DIE);
		}
		if (!isCovered) {
			setVisible(true);
		}
	}


	protected void check(long timestamp) {}

	@Override
	public void destroy() {
		this.setActive(false);
		super.destroy();
	}

	protected void initTank(double x, double y, Direction direction, String name, int group) {
		this.transform.setX(x);
		this.transform.setY(y);
		this.group = group;
//		 fireTime = getParent().currentTimeMillis();
		this.direction = direction;
		this.color = TankWarUtil.getRandomColor();
		r = color.getRed();
		g = color.getGreen();
		b = color.getBlue();
//		 防止颜色过暗
//		do {
//			this.color = TankWarUtil.getRandomColor();
//		} while (color.getRed() + color.getGreen() + color.getBlue() < 100.0 / 256);
		this.name = name;
		setActive(true);
	}

	public void fire() {
		// if(getParent().currentTimeMillis() - fireTime < FIRE_INTERVAL)
		// return;
		// fireTime = getParent().currentTimeMillis();
		// Position position = getPosition();
		double bulletX = this.transform.getX();
		double bulletY = this.transform.getY();
		switch (direction) {
			case DIR_UP -> bulletY -= getRadius();
			case DIR_DOWN -> bulletY += getRadius();
			case DIR_LEFT -> bulletX -= getRadius();
			case DIR_RIGHT -> bulletX += getRadius();
		}
		Bullet bullet = Bullet.createBullet(getParent(), atk, color, bulletX, bulletY, direction, this);
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
		if (this.hp > hp) {
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

	public Direction getDirection() {
		return direction;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	private String name;
	private static final Font NAME_FONT = Font.loadFont(ResourceUtil.getAsPath("/Fonts/Minecraft.ttf"), 12);

	@Override
	public void refresh(GraphicsContext g, long timestamp) {
		if (visible) {
			hpBar.refresh(g, timestamp);
			g.setFill(color);
			g.setFont(NAME_FONT);
			g.fillText(name, transform.getX() - getRadius(), transform.getY() - getRadius() - 14);
		}
	}

	private static final AudioClip hitSound = TankWar.hitSound;

	private class HPBar implements Serializable {
		public static final int BAR_LENGTH = 50;
		public static final int BAR_HEIGHT = 5;

		public void refresh(GraphicsContext g, long timestamp) {
			double x = Tank.this.transform.getX();
			double y = Tank.this.transform.getY();
			// System.out.println("HPBar render");
			g.setFill(Color.RED);
			g.fillRect(x - Tank.this.getRadius(), y - Tank.this.getRadius() - BAR_HEIGHT * 2,
					hp * BAR_LENGTH / (double) (maxHp), BAR_HEIGHT);
			g.setStroke(Color.WHITE);
			g.strokeRect(x - Tank.this.getRadius(), y - Tank.this.getRadius() - BAR_HEIGHT * 2,
					BAR_LENGTH, BAR_HEIGHT);
		}
	}

	public double r, g, b;



	public void setColor(double r, double g, double b) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.color = Color.color(r, g, b);
	}

	@Override
	public boolean equals(Object o) {
		if (!super.equals(o)) return false;
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Tank tank = (Tank) o;
		return visible == tank.visible && hp == tank.hp && atk == tank.atk && speed == tank.speed && maxHp == tank.maxHp && isOnline == tank.isOnline && group == tank.group && Double.compare(tank.r, r) == 0 && Double.compare(tank.g, g) == 0 && Double.compare(tank.b, b) == 0 && direction == tank.direction && state == tank.state && name.equals(tank.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(visible, hp, atk, speed, maxHp, direction, state, isOnline, group, name, r, g, b);
	}
}
