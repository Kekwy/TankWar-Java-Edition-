package com.kekwy.game;

import java.awt.*;

public class MyTank extends Tank {

	private static int num = 0;
	private static final Image[] tankImg;

	static {
		tankImg = new Image[4];
		tankImg[0] = Toolkit.getDefaultToolkit().createImage("res/p1tankU.gif");
		tankImg[1] = Toolkit.getDefaultToolkit().createImage("res/p1tankD.gif");
		tankImg[2] = Toolkit.getDefaultToolkit().createImage("res/p1tankL.gif");
		tankImg[3] = Toolkit.getDefaultToolkit().createImage("res/p1tankR.gif");
	}

	public MyTank(int x, int y, Direction forward) {
		super(x, y, forward);
		num++;
		setName("Player" + num);
	}

	@Override
	protected void drawImgTank(Graphics g) {
		g.drawImage(tankImg[getForward().ordinal()], getX() - RADIUS, getY() - RADIUS, 2 * RADIUS, 2 * RADIUS, null);
	}
}
