package com.kekwy.game;

import java.awt.*;

public class Bullet {
	public static final int DEFAULT_SPEED = Tank.DEFAULT_SPEED * 2;
	public static final int RADIUS = 4;

	private int x, y;
	private int speed = DEFAULT_SPEED;
	private Tank.Direction forward;
	private int atk;
	private Color color;

	public Bullet(int x, int y, Tank.Direction forward, int atk, Color color) {
		this.x = x;
		this.y = y;
		this.forward = forward;
		this.atk = atk;
		this.color = color;
	}
	public void draw(Graphics g) {
		logic();
		g.setColor(color);
		g.fillOval(x - RADIUS, y - RADIUS, RADIUS << 1, RADIUS << 1);
	}

	private void logic() {
		move();
	}

	private void move() {
		switch (forward) {
			case DIR_UP -> y -= speed;
			case DIR_DOWN -> y += speed;
			case DIR_LEFT -> x -= speed;
			case DIR_RIGHT -> x += speed;
		}
	}

}
