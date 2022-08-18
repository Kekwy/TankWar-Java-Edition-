package com.kekwy.util;

import com.kekwy.game.Explode;
import com.kekwy.map.MapTile;

import java.util.LinkedList;
import java.util.List;

public class MapTilePool {
	public static final int DEFAULT_POOL_SIZE = 100;
	private static List<MapTile> pool = new LinkedList<>();

	static {
		for (int i = 0; i < DEFAULT_POOL_SIZE; i++) {
			pool.add(new MapTile());
		}
	}

	public static MapTile takeAway() {
		MapTile mapTile;
		if (pool.size() == 0) {
			mapTile = new MapTile();
		} else {
			mapTile = pool.remove(0);
		}
		return mapTile;
	}

	public static void sendBack(MapTile mapTile) {
		if(pool.size() == DEFAULT_POOL_SIZE) {
			return;
		}
		pool.add(mapTile);
	}
}
