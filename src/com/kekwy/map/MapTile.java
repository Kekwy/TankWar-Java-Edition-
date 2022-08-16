package com.kekwy.map;

import com.kekwy.util.MyUtil;

import java.awt.*;

public class MapTile {
	private static Image tileImg;
	public static int tileW;
	static {
		tileImg = MyUtil.createImage("res/walls.gif");
		if(tileW <=0) {
			tileW = tileImg.getWidth(null);
		}
	}
	private int x, y;

	public MapTile(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public void draw(Graphics g) {
		g.drawImage(tileImg, x, y, null);
	}
}
