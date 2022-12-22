package com.kekwy.jw.tankwar.gamemap;

import com.kekwy.jw.tankwar.GameScene;
import com.kekwy.jw.tankwar.util.TankWarUtil;

import java.io.InputStream;

public class GameMap {

	int[][] mapData;

	public GameMap(InputStream file) {
		mapData = TankWarUtil.readWorkBook(file, 1, 1,
				GAME_MAP_ROW, GAME_MAP_COL, 0);
	}

	public static final int GAME_MAP_ROW = 14;
	public static final int GAME_MAP_COL = 24;


	public void createGameMap(GameScene parent) {

		MapTile.base = 0;

		for (int i = 0; i < mapData.length; i++) {
			int[] mapDatum = mapData[i];
			for (int i1 = 0; i1 < mapDatum.length; i1++) {
				int x = i1 * MapTile.TILE_WIDTH + MapTile.TILE_WIDTH / 2;
				int y = i * MapTile.TILE_WIDTH + MapTile.TILE_WIDTH / 2;
				switch (mapDatum[i1]) {
					case 1 -> new MapTile(parent, MapTile.Type.TYPE_NORMAL, x, y);
					case 2 -> new MapTile(parent, MapTile.Type.TYPE_HARD, x, y);
					case 3 -> new MapTile(parent, MapTile.Type.TYPE_COVER, x, y);
					case 4 -> {
						new MapTile(parent, MapTile.Type.TYPE_BASE, x, y);
						MapTile.base++;
					}
				}
			}
		}

	}

}
