package com.kekwy.map;

import com.kekwy.game.Bullet;
import com.kekwy.game.Tank;
import com.kekwy.util.BulletsPool;
import com.kekwy.util.MyUtil;

import java.awt.*;
import java.util.List;

public class MapTile {

	private boolean isVisible = true;
	private static Image tileImg;
	public static int tileW;

	static {
		tileImg = MyUtil.createImage("res/walls.gif");
		if (tileW <= 0) {
			tileW = 2 * Tank.RADIUS;
		}
	}

	private int x, y;

	public MapTile(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public MapTile() {

	}

	public void draw(Graphics g) {
		if(!isVisible) return;
		g.drawImage(tileImg, x, y, tileW, tileW, null);

		if(name != null) {
			g.setColor(Color.WHITE);
			g.drawString(name, x + tileW / 2, y + tileW);
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

	public boolean isCollideBullet(List<Bullet> bullets) {
		if(!isVisible) return false;
		for (Bullet bullet : bullets) {
			int bulletX = bullet.getX();
			int bulletY = bullet.getY();
			if (MyUtil.isCollide(x + Tank.RADIUS, y + Tank.RADIUS, Tank.RADIUS, bulletX, bulletY)) {
				bullet.setVisible(false);
				BulletsPool.sendBack(bullet);
				isVisible = false;
				return true;
			}
		}
		return false;
	}

	public boolean isVisible() {
		return isVisible;
	}

	public void setVisible(boolean visible) {
		isVisible = visible;
	}


	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
