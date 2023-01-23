package com.kekwy.tankwar.server.tank;

import com.kekwy.tankwar.server.GameServer;
import com.kekwy.tankwar.util.Direction;
import com.kekwy.tankwar.util.RandomGen;
import com.kekwy.tankwar.server.GameObject;
import com.kekwy.tankwar.server.GameScene;
import com.kekwy.tankwar.server.gamemap.MapTile;
import com.kekwy.tankwar.io.actions.NewObjectAction;
import com.kekwy.tankwar.io.actions.NewTankAction;
import com.kekwy.tankwar.io.actions.UpdateObjectAction;
import com.kekwy.tankwar.io.actions.UpdateTankAction;

import java.util.LinkedList;
import java.util.List;

public abstract class Tank extends GameObject implements Runnable {
	@Override
	public NewObjectAction getNewObjectAction() {
		if (this instanceof PlayerTank)
			return new NewTankAction(getIdentity(), getClass().getName(), 0,
					transform.getX(), transform.getY(), direction.ordinal(),
					name, group, r, g, b);
		else
			return new NewTankAction(getIdentity(), getClass().getName(), 1,
					transform.getX(), transform.getY(), direction.ordinal(),
					name, group, r, g, b);
	}

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

	public String getName() {
		return name;
	}

	private int hp = DEFAULT_HP, atk = DEFAULT_ATK, speed = DEFAULT_SPEED;
	private int maxHp = DEFAULT_HP;

	private Direction direction = DEFAULT_DIR;
	private State state = DEFAULT_STATE;

	public double r, g, b;

	public Tank(GameScene parent, GameServer server) {
		super(parent, server);
		setRadius(TANK_RADIUS);
	}

	public Tank(GameScene parent, GameServer server, int x, int y, Direction direction, String name, int group) {
		super(parent, server);
		setRadius(TANK_RADIUS);
//		setColliderType(ColliderType.COLLIDER_TYPE_RECT);
		initTank(x, y, direction, name, group);
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


	private int group;

	private static final long UPDATE_INTERVAL = 20;

	@SuppressWarnings("BusyWait")
	@Override
	public void run() {
		while (this.isActive()) {
			move();
//			fire();
			try {
				doCollide();
				check(System.currentTimeMillis());
//				sendToClient();
//				System.out.println("17171717");
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
//						hitSound.play();
						this.hp = 0;
						tank.setHp(0);
						tank.setState(State.STATE_DIE);
//						Blast blast = Blast.createBlast(getParent(), this.transform.getX(), this.transform.getY());
//						getParent().addGameObject(blast);
						tank.setHp(0);
					}
				} else if (gameObject instanceof Bullet bullet && bullet.getFrom().group != this.group) {
//					hitSound.play();
					hp -= bullet.getAtk();
					if (hp < 0) hp = 0;
//					Blast blast = Blast.createBlast(getParent(), bullet.transform.getX(), bullet.transform.getY());
//					getParent().addGameObject(blast);
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

	public int getGroup() {
		return group;
	}

	protected void check(long timestamp) {
	}

	@Override
	public void destroy() {
		this.setActive(false);
	}

	protected void initTank(double x, double y, Direction direction, String name, int group) {
		this.transform.setX(x);
		this.transform.setY(y);
		this.group = group;
//		 fireTime = getParent().currentTimeMillis();
		this.direction = direction;
		this.r = RandomGen.getRandomNumber(0, 1);
		this.g = RandomGen.getRandomNumber(0, 1);
		this.b = RandomGen.getRandomNumber(0, 1);
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
		Bullet bullet = Bullet.createBullet(getParent(), atk, r, g, b, bulletX, bulletY, direction, this);
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

	@Override
	public UpdateObjectAction getUpdateObjectAction() {
		return new UpdateTankAction(getIdentity(), transform.getX(), transform.getY(),
				direction.ordinal(), isActive(), hp, state.ordinal());
	}
}
