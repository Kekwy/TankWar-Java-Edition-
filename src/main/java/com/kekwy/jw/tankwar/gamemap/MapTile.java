package com.kekwy.jw.tankwar.gamemap;

import com.kekwy.jw.tankwar.GameObject;
import com.kekwy.jw.tankwar.GameScene;
import com.kekwy.jw.tankwar.effect.Blast;
import com.kekwy.jw.tankwar.gamescenes.LocalPlayScene;
import com.kekwy.jw.tankwar.tank.Bullet;
import com.kekwy.jw.tankwar.util.TankWarUtil;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class MapTile extends GameObject {
	public static final int TILE_WIDTH = 40;

	public enum Type {
		TYPE_NORMAL, TYPE_HARD, TYPE_COVER, TYPE_BASE,
	}

	static Image[] tileImg;

	static {
		tileImg = new Image[4];
		tileImg[Type.TYPE_NORMAL.ordinal()] = TankWarUtil.createImage("/walls.gif");
		tileImg[Type.TYPE_BASE.ordinal()] = TankWarUtil.createImage("/baseTank.png");
		tileImg[Type.TYPE_COVER.ordinal()] = TankWarUtil.createImage("/grass.png");
		tileImg[Type.TYPE_HARD.ordinal()] = TankWarUtil.createImage("/steels.gif");
	}

	int hp = 10;

	public MapTile(GameScene parent, Type type, int x, int y) {
		super(parent);
		this.setRadius(TILE_WIDTH / 2);
		this.type = type;
		this.transform.setX(x);
		this.transform.setY(y);
		if (type == Type.TYPE_COVER)
			setLayer(3);
		else
			setLayer(1);
		if (type == Type.TYPE_NORMAL)
			hp = 200;
		else if (type == Type.TYPE_BASE)
			hp = 500;
		setColliderType(ColliderType.COLLIDER_TYPE_RECT);
		setActive(true);
		parent.addGameObject(this);
	}

	public static int base = 2;

	@Override
	public void refresh(GraphicsContext g, long timestamp) {
		g.drawImage(tileImg[type.ordinal()], transform.getX() - TILE_WIDTH / 2.0, transform.getY() - TILE_WIDTH / 2.0,
				TILE_WIDTH, TILE_WIDTH);
	}

	public void doCollide(Bullet bullet) {
		if (type == Type.TYPE_COVER)
			return;
		if (type != Type.TYPE_HARD) {
			hp -= bullet.getAtk();
		}
		Blast blast = Blast.createBlast(getParent(), bullet.transform.getX(), bullet.transform.getY());
		getParent().addGameObject(blast);
		bullet.setActive(false);
		if (hp <= 0) {
			setActive(false);
			if (type == Type.TYPE_BASE)
				base--;
			if (base == 0 && getParent() instanceof LocalPlayScene localPlayScene && localPlayScene.isPlaying()) {
				new Thread(() -> {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						throw new RuntimeException(e);
					}
					((LocalPlayScene) getParent()).gameOver();
				}).start();
			}
		}
	}

	Type type;

	public Type getType() {
		return type;
	}
}
