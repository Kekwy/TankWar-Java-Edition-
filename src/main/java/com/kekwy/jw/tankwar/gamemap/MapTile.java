package com.kekwy.jw.tankwar.gamemap;

import com.kekwy.jw.gameengine.GameObject;
import com.kekwy.jw.gameengine.GameScene;
import com.kekwy.jw.tankwar.effect.Blast;
import com.kekwy.jw.tankwar.gamescenes.PlayScene;
import com.kekwy.jw.tankwar.tank.Bullet;
import com.kekwy.jw.tankwar.util.TankWarUtil;

import java.awt.*;
import java.util.List;

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
		this.position.setX(x);
		this.position.setY(y);
		if (type == Type.TYPE_COVER)
			setLayer(2);
		else
			setLayer(0);
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
	public void collide(List<GameObject> gameObjects) {
		if (type == Type.TYPE_COVER)
			return;
		for (GameObject gameObject : gameObjects) {
			if (gameObject instanceof Bullet bullet) {
				if (type != Type.TYPE_HARD)
					hp -= bullet.getAtk();
				Blast blast = Blast.createBlast(getParent(), bullet.position.getX(), bullet.position.getY());
				getParent().addGameObject(blast);
				bullet.setActive(false);
			}
		}
		if (hp <= 0) {
			setActive(false);
			if (type == Type.TYPE_BASE)
				base--;
			if (base == 0 && getParent() instanceof PlayScene playScene && playScene.isPlaying()) {
				new Thread(() -> {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						throw new RuntimeException(e);
					}
					((PlayScene) getParent()).gameOver();
				}).start();
			}
		}
	}

	Type type;


	@SuppressWarnings("SuspiciousNameCombination")
	@Override
	public void render(Graphics g) {
		g.drawImage(tileImg[type.ordinal()], position.getX() - TILE_WIDTH / 2, position.getY() - TILE_WIDTH / 2,
				TILE_WIDTH, TILE_WIDTH, null);
	}

	public Type getType() {
		return type;
	}
}
