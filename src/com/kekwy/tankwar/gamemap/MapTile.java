package com.kekwy.tankwar.gamemap;

import com.kekwy.gameengine.GameObject;
import com.kekwy.gameengine.GameScene;
import com.kekwy.tankwar.util.TankWarUtil;

import java.awt.*;

public class MapTile extends GameObject {
	public static final int TILE_WIDTH = 40;

	public enum Type {
		TYPE_NORMAL, TYPE_HARD,  TYPE_COVER, TYPE_BASE,
	}

	static Image[] tileImg;
	static {
		tileImg = new Image[4];
		tileImg[Type.TYPE_NORMAL.ordinal()] = TankWarUtil.createImage("/walls.gif");
		tileImg[Type.TYPE_BASE.ordinal()] = TankWarUtil.createImage("/baseTank.png");
		tileImg[Type.TYPE_COVER.ordinal()] = TankWarUtil.createImage("/grass.png");
		tileImg[Type.TYPE_HARD.ordinal()] = TankWarUtil.createImage("/steels.gif");
	}

	public MapTile(GameScene parent, Type type, int x, int y) {
		super(parent);
		this.type = type;
		this.position.setX(x);
		this.position.setY(y);
		setActive(true);
		parent.addGameObject(this);
	}

	Type type;

	@SuppressWarnings("SuspiciousNameCombination")
	@Override
	public void render(Graphics g) {
		g.drawImage(tileImg[type.ordinal()], position.getX() - TILE_WIDTH / 2, position.getY() - TILE_WIDTH / 2,
				TILE_WIDTH, TILE_WIDTH, null);
	}
}
