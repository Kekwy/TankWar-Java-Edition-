package com.kekwy.tankwar.server.gamemap;

import com.kekwy.tankwar.io.actions.NewMapTileAction;
import com.kekwy.tankwar.io.actions.NewObjectAction;
import com.kekwy.tankwar.io.actions.UpdateMapTileAction;
import com.kekwy.tankwar.io.actions.UpdateObjectAction;
import com.kekwy.tankwar.server.ServerCore;
import com.kekwy.tankwar.server.GameObject;
import com.kekwy.tankwar.server.GameScene;
import com.kekwy.tankwar.server.tank.Blast;
import com.kekwy.tankwar.server.tank.Bullet;

public class MapTile extends GameObject {
	public static final int TILE_WIDTH = 40;

	public enum Type {
		TYPE_NORMAL, TYPE_HARD, TYPE_COVER, TYPE_BASE,
	}

	int hp = 10;

	public MapTile(GameScene parent, Type type, int x, int y) {
		super(parent);
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
		Blast blast = Blast.createBlast(getParent(), bullet.transform.getX(), bullet.transform.getY());
		getParent().addGameObject(blast);
		bullet.setActive(false);
		if (hp <= 0) {
			setActive(false);
		}
	}

	Type type;

	public Type getType() {
		return type;
	}

	@Override
	public NewObjectAction getNewObjectAction() {
		return new NewMapTileAction(getIdentity(), transform.getX(), transform.getY(), type.ordinal());
	}

	@Override
	public UpdateObjectAction getUpdateObjectAction() {
		return new UpdateMapTileAction(getIdentity(), isActive());
	}
}
