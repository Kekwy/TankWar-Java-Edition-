package com.kekwy.game;

import com.kekwy.util.Constant;

import java.awt.*;

public class Bullet {
	public static final int DEFAULT_SPEED = Tank.DEFAULT_SPEED * 2;
	public static final int RADIUS = 4;

	private int x, y;
	private int speed = DEFAULT_SPEED;
	private Tank.Direction forward;
	private int atk;
	private Color color;
	private boolean visible = true;

	//______________________________________________________________________________
	//给对象池使用
	public Bullet() {
	}

	public Bullet(int x, int y, Tank.Direction forward, int atk, Color color) {
		this.x = x;
		this.y = y;
		this.forward = forward;
		this.atk = atk;
		this.color = color;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public void setForward(Tank.Direction forward) {
		this.forward = forward;
	}

	public void setAtk(int atk) {
		this.atk = atk;
	}

	public void draw(Graphics g) {
		if (!visible)
			return;
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
		if (x < 0 || x > Constant.FRAME_WIDTH || y < 0 || y > Constant.FRAME_HEIGHT)
			visible = false;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}
}
