package com.kekwy.game;


import com.kekwy.util.MyUtil;

import java.awt.*;

public class Explode {
	public static final int EXPLODE_FRAME_COUNT = 16;
	private static Image[] img;

	static {
		img = new Image[EXPLODE_FRAME_COUNT];
		for (int i = 0; i < EXPLODE_FRAME_COUNT; i++) {
			img[i] = MyUtil.createImage("res/blast" + String.valueOf(i + 1 >> 1) + ".gif");
		}
	}

	// 爆炸效果的属性
	private int x, y;
	// 当前播放的帧的下标
	private int index = 0;

	private static final int RADIUS = 16;

	private boolean visible = true;

	public Explode(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public Explode() {

	}

	//______________________________________________________________________

	public void draw(Graphics g) {
		if (!visible) return;
		;
		g.drawImage(img[index], x - RADIUS, y - RADIUS, 2 * RADIUS, 2 * RADIUS, null);
		index++;
		if (index >= EXPLODE_FRAME_COUNT) {
			visible = false;
		}
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}
}
