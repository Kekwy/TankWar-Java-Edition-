package com.kekwy.jw.tankwar.effect;

import com.kekwy.jw.gameengine.GameObject;
import com.kekwy.jw.gameengine.GameScene;
import com.kekwy.jw.tankwar.util.ObjectPool;
import com.kekwy.jw.tankwar.util.TankWarUtil;

import java.awt.*;

public class Blast extends GameObject {

	public static final int EXPLODE_FRAME_COUNT = 14;
	private static final Image[] img;

	static {
		img = new Image[EXPLODE_FRAME_COUNT];
		for (int i = 0; i < EXPLODE_FRAME_COUNT; i++) {
			img[i] = TankWarUtil.createImage("/blast" + String.valueOf((i >> 1) + 1) + ".gif");
		}
	}

	public static Blast createBlast(GameScene parent, int x, int y) {
		Blast blast = (Blast)blastPool.getObject();
		blast.setIndex(0);
		blast.setParent(parent);
		blast.setActive(true);
		blast.position.setX(x);
		blast.position.setY(y);
		blast.setLayer(2);
		return blast;
	}


	// 当前播放的帧的下标
	private int index = 0;

	private static final int DEFAULT_BLAST_RADIUS = 20;

	@Override
	public void setRadius(int radius) {
		this.radius = radius;
	}

	int radius = DEFAULT_BLAST_RADIUS;

	public void setIndex(int index) {
		this.index = index;
	}


	private static final ObjectPool blastPool = new ObjectPool(Blast.class);


	public Blast(GameScene parent) {
		super(parent);
	}

	@Override
	public void render(Graphics g) {
		int x = this.position.getX();
		int y = this.position.getY();
		g.drawImage(img[index], x - radius, y - radius, 2 * radius, 2 * radius, null);
		index++;
		if (index >= EXPLODE_FRAME_COUNT) {
			setActive(false);
			blastPool.returnObject(this);
		}
	}
}
