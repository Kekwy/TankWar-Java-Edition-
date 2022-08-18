package com.kekwy.game;

import com.kekwy.map.MapTile;
import com.kekwy.util.Constant;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TankHouse {
	public static final int HOUSE_X = Constant.FRAME_WIDTH - 3 * MapTile.tileW >> 1;
	public static final int HOUSE_Y = Constant.FRAME_HEIGHT - 2 * MapTile.tileW;

	private List<MapTile> tiles = new ArrayList<>();

	public TankHouse() {
		tiles.add(new MapTile(HOUSE_X, HOUSE_Y));
		tiles.add(new MapTile(HOUSE_X, HOUSE_Y + MapTile.tileW));
		tiles.add(new MapTile(HOUSE_X+MapTile.tileW, HOUSE_Y));
		tiles.add(new MapTile(HOUSE_X+MapTile.tileW*2, HOUSE_Y));
		tiles.add(new MapTile(HOUSE_X+MapTile.tileW*2, HOUSE_Y+MapTile.tileW));
		tiles.add(new MapTile(HOUSE_X+MapTile.tileW, HOUSE_Y+MapTile.tileW));

	}

	public void draw(Graphics g) {
		for(MapTile tile:tiles) {
			tile.draw(g);
		}
	}

	public Collection<? extends MapTile> getTiles() {
		return tiles;
	}
}
