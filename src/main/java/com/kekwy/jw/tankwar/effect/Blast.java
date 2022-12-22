package com.kekwy.jw.tankwar.effect;

import com.kekwy.jw.tankwar.GameObject;
import com.kekwy.jw.tankwar.GameScene;
import com.kekwy.jw.tankwar.util.ObjectPool;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;


public class Blast extends GameObject {

	public static final int EXPLODE_FRAME_COUNT = 14;
	private static final Image[] img;

	static {
		img = new Image[EXPLODE_FRAME_COUNT];
		for (int i = 0; i < EXPLODE_FRAME_COUNT; i++) {
			img[i] = new Image("/blast" + String.valueOf((i >> 1) + 1) + ".gif");
		}
	}

	public static Blast createBlast(GameScene parent, double x, double y) {
		Blast blast = (Blast)blastPool.getObject();
		blast.setIndex(0);
		blast.setParent(parent);
		blast.setActive(true);
		blast.transform.setX(x);
		blast.transform.setY(y);
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


	@Override
	public void refresh(GraphicsContext g, long timestamp) {
		double x = this.transform.getX();
		double y = this.transform.getY();
		g.drawImage(img[index], x - radius, y - radius, 2 * radius, 2 * radius);
		index++;
		if (index >= EXPLODE_FRAME_COUNT) {
			setActive(false);
			blastPool.returnObject(this);
		}
	}

	public Blast(GameScene parent) {
		super(parent);
	}

}
