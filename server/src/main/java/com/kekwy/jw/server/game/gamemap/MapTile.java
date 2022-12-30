package com.kekwy.jw.server.game.gamemap;

import com.kekwy.jw.server.GameServer;
import com.kekwy.jw.server.game.GameObject;
import com.kekwy.jw.server.game.GameScene;
import com.kekwy.jw.server.game.tank.Bullet;

public class MapTile extends GameObject {
	public static final int TILE_WIDTH = 40;

	public enum Type {
		TYPE_NORMAL, TYPE_HARD, TYPE_COVER, TYPE_BASE,
	}

	int hp = 10;

	public MapTile(GameScene parent, GameServer server, Type type, int x, int y) {
		super(parent, server);
		this.setRadius(TILE_WIDTH / 2);
		this.type = type;
		this.transform.setX(x);
		this.transform.setY(y);
		if (type == Type.TYPE_NORMAL)
			hp = 200;
		else if (type == Type.TYPE_BASE)
			hp = 500;
		setColliderType(ColliderType.COLLIDER_TYPE_RECT);
		setActive(true);
		parent.addGameObject(this);
	}

	public static int base = 2;


	public void doCollide(Bullet bullet) {
		if (type == Type.TYPE_COVER)
			return;
		if (type != Type.TYPE_HARD) {
			hp -= bullet.getAtk();
		}
//		Blast blast = Blast.createBlast(getParent(), bullet.transform.getX(), bullet.transform.getY());
//		getParent().addGameObject(blast);
		bullet.setActive(false);
		if (hp <= 0) {
			setActive(false);
//			if (type == Type.TYPE_BASE)
//				base--;
//			if (base == 0 && getParent() instanceof LocalPlayScene localPlayScene && localPlayScene.isPlaying()) {
//				new Thread(() -> {
//					try {
//						Thread.sleep(1000);
//					} catch (InterruptedException e) {
//						throw new RuntimeException(e);
//					}
//					((LocalPlayScene) getParent()).gameOver();
//				}).start();
//			}
		}
	}

	Type type;

	public Type getType() {
		return type;
	}
}
