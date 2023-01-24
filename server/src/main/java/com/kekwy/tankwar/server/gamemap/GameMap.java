package com.kekwy.tankwar.server.gamemap;


import com.kekwy.tankwar.server.GameScene;
import com.kekwy.tankwar.util.FileReadUtil;

import java.io.InputStream;

public class GameMap {

	int[][] mapData;

	public GameMap(InputStream file) {
		mapData = FileReadUtil.readWorkBook(file, 1, 1,
				GAME_MAP_ROW, GAME_MAP_COL, 0);
	}

	public static final int GAME_MAP_ROW = 16;
	public static final int GAME_MAP_COL = 30;


	public void createGameMap(GameScene parent) {
//		MapTile[][] mapTiles = new MapTile[GAME_MAP_ROW][GAME_MAP_COL];
		MapTile.base = 0;
		for (int i = 0; i < mapData.length; i++) {
			int[] mapDatum = mapData[i];
			for (int j = 0; j < mapDatum.length; j++) {
				int x = j * MapTile.TILE_WIDTH + MapTile.TILE_WIDTH / 2;
				int y = i * MapTile.TILE_WIDTH + MapTile.TILE_WIDTH / 2;
				switch (mapDatum[j]) {
					case 1 -> new MapTile(parent, MapTile.Type.TYPE_NORMAL, x, y);
					case 2 -> new MapTile(parent, MapTile.Type.TYPE_HARD, x, y);
					case 3 -> new MapTile(parent, MapTile.Type.TYPE_COVER, x, y);
					case 4 -> new MapTile(parent, MapTile.Type.TYPE_BASE, x, y);
				}
			}
		}
	}

}
