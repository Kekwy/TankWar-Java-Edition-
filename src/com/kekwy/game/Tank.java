package com.kekwy.game;

import java.awt.*;

import com.kekwy.util.MyUtil;

import static com.kekwy.util.Constant.*;

public class Tank {
	private static Image[] tankImg;
	private static Image[] enemyImg;

	static {
		tankImg = new Image[4];
		tankImg[0] = Toolkit.getDefaultToolkit().createImage("res/p1tankU.gif");
		tankImg[1] = Toolkit.getDefaultToolkit().createImage("res/p1tankD.gif");
		tankImg[2] = Toolkit.getDefaultToolkit().createImage("res/p1tankL.gif");
		tankImg[3] = Toolkit.getDefaultToolkit().createImage("res/p1tankR.gif");

		enemyImg = new Image[4];
		enemyImg[0] = Toolkit.getDefaultToolkit().createImage("res/enemy1U.gif");
		enemyImg[1] = Toolkit.getDefaultToolkit().createImage("res/enemy1D.gif");
		enemyImg[2] = Toolkit.getDefaultToolkit().createImage("res/enemy1L.gif");
		enemyImg[3] = Toolkit.getDefaultToolkit().createImage("res/enemy1R.gif");
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
	private State state = State.STATE_IDLE;

	private int atk = DEFAULT_ATK;
	private int hp = DEFAULT_HP;
	private int speed = DEFAULT_SPEED;
	private Color color;

	public Tank(int x, int y, Direction forward) {
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
				if (y < FRAME_HEIGHT - RADIUS) {
					y += speed;
				}
			}
			case DIR_LEFT -> {
				if (x > RADIUS) {
					x -= speed;
				}
			}
			case DIR_RIGHT -> {
				if (x < FRAME_WIDTH - RADIUS) {
					x += speed;
				}
			}
		}
	}

	public void draw(Graphics g) {
		logic();
		g.drawImage(tankImg[forward.ordinal()], x - RADIUS, y - RADIUS, 2 * RADIUS, 2 * RADIUS, null);
		//g.fillOval(x - RADIUS, y - RADIUS, RADIUS << 1, RADIUS << 1);

	}

	public void setForward(Direction forward) {
		this.forward = forward;
	}

	public void setState(State state) {
		this.state = state;
	}
}
