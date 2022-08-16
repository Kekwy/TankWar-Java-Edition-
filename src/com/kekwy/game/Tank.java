package com.kekwy.game;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;

import com.kekwy.util.*;

import static com.kekwy.util.Constant.*;

public abstract class Tank {


	protected void setHP(int defaultHp) {
		hp = defaultHp;
	}

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

	public static final int RADIUS = 20;
	public static final int DEFAULT_SPEED = 4;
	public static final int DEFAULT_HP = 1000;
	public static final int DEFAULT_ATK = 100;

	private int x, y;
	private Direction forward;
	protected State state = State.STATE_IDLE;

	private int atk = DEFAULT_ATK;
	private int hp = DEFAULT_HP;
	private int speed = DEFAULT_SPEED;
	private Color color;
	private List<Bullet> bullets = new LinkedList<>();

	protected boolean isEnemy = false;

	/**
	 * 对象池思想：
	 * 提前创建好若干个子类对象，放到一个容器中。
	 * 需要的时候从该对象池中拿出来一个使用，
	 * 被销毁时再放回原对象池中。
	 */

	private List<Explode> explodes = new LinkedList<>();

	private String name;

	private HPBar bar = new HPBar();
	//_____________________________________________________________________________________________
	public void bulletsReturn() {
		for (Bullet bullet : bullets) {
			BulletsPool.sendBack(bullet);
		}
		bullets.clear();
		for (Explode explode : explodes) {
			explode.setVisible(false);
			ExplodesPool.sendBack(explode);
		}
		explodes.clear();
	}

	public Tank() {
		// this.color = MyUtil.getRandomColor();
	}

	public Tank(int x, int y, Direction forward) {
		initTank(x, y, forward);
	}

	protected void initTank(int x, int y, Direction forward) {
		this.x = x;
		this.y = y;
		this.forward = forward;
		this.color = MyUtil.getRandomColor();
	}

	private void logic() {
		switch (state) {
			case STATE_MOVE -> move();
		}
	}

	private void move() {
		switch (forward) {
			case DIR_UP -> {
				if (y > RADIUS + GameFrame.titleBarH) {
					y -= speed;
				}
			}
			case DIR_DOWN -> {
				if (y < FRAME_HEIGHT - RADIUS - 2) {
					y += speed;
				}
			}
			case DIR_LEFT -> {
				if (x > RADIUS + 6) {
					x -= speed;
				}
			}
			case DIR_RIGHT -> {
				if (x < FRAME_WIDTH - RADIUS - 6) {
					x += speed;
				}
			}
		}
	}

	public void draw(Graphics g) {
		logic();
		drawBullets(g);
		drawImgTank(g);
		//g.fillOval(x - RADIUS, y - RADIUS, RADIUS << 1, RADIUS << 1);
		drawName(g);
		bar.draw(g);
	}

	private void drawName(Graphics g) {
		g.setColor(color);
		g.setFont(NAME_FONT);
		g.drawString(name, x - RADIUS, y - RADIUS - 14);
	}

	protected abstract void drawImgTank(Graphics g);

	public void setForward(Direction forward) {
		this.forward = forward;
	}

	public void setState(State state) {
		this.state = state;
	}

	public void fire() {
		int bulletx = x;
		int bullety = y;
		switch (forward) {
			case DIR_UP -> bullety -= RADIUS;
			case DIR_DOWN -> bullety += RADIUS;
			case DIR_LEFT -> bulletx -= RADIUS;
			case DIR_RIGHT -> bulletx += RADIUS;
		}
		Bullet bullet = BulletsPool.takeAway();
		bullet.setX(bulletx);
		bullet.setY(bullety);
		bullet.setAtk(atk);
		bullet.setColor(color);
		bullet.setForward(forward);
		bullet.setVisible(true);
		bullets.add(bullet);
	}

	/**
	 * 将当前坦克发射的所有子弹全部绘制出来
	 */
	private void drawBullets(Graphics g) {
		for (Bullet bullet : bullets) {
			if (bullet.isVisible())
				bullet.draw(g);
		}
		for (int i = 0; i < bullets.size(); i++) {
			Bullet bullet = bullets.get(i);
			if (!bullet.isVisible()) {
				BulletsPool.sendBack(bullet);
				bullets.remove(i);
				i--;
			}
		}
		// System.out.println("地图上剩余发射出去的子弹：" + bullets.size());
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public Direction getForward() {
		return forward;
	}

	public void collideBullets(List<Bullet> bullets) {
		for (Bullet bullet : bullets) {
			int bulletX = bullet.getX();
			int bulletY = bullet.getY();
			if (MyUtil.isCollide(x, y, RADIUS, bulletX, bulletY)) {
				hp -= bullet.getAtk();
				bullet.setVisible(false);
				Explode explode = ExplodesPool.takeAway();
				explode.setX(bulletX);
				explode.setY(bulletY);
				explode.setIndex(0);
				explode.setVisible(true);
				explodes.add(explode);
				// TODO 多余爆炸效果的修复
				if(hp <= 0) {
					hp = 0;
					die();
				}
			}
		}
	}

	private void die() {
		state = State.STATE_DIE;
		if(isEnemy) {
			this.bulletsReturn();
			TankPool.sendBack(this);
		}
		else {
			// gameOver
			GameFrame.gameState = Constant.State.STATE_OVER;
		}
	}

	public boolean isDie() {
		return hp == 0;
	}
	public List<Bullet> getBullets() {
		return bullets;
	}

	public void drawExplodes(Graphics g) {
		for (Explode explode : explodes) {
			explode.draw(g);
		}
		for (int i = 0; i < explodes.size(); i++) {
			Explode explode = explodes.get(i);
			if (!explode.isVisible()) {
				ExplodesPool.sendBack(explode);
				explodes.remove(i);
				i--;
			}
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	private class HPBar {
		public static final int BAR_LENGTH = 50;
		public static final int BAR_HEIGHT = 5;

		public void draw(Graphics g) {
			g.setColor(Color.RED);
			g.fillRect(x - RADIUS, y - RADIUS - BAR_HEIGHT * 2, hp * BAR_LENGTH / DEFAULT_HP, BAR_HEIGHT);
			g.setColor(Color.white);
			g.drawRect(x - RADIUS, y - RADIUS - BAR_HEIGHT * 2, BAR_LENGTH, BAR_HEIGHT);
		}
	}
}
