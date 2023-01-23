package com.kekwy.tankwar.client.gamemap;

import com.kekwy.tankwar.client.util.TankWarUtil;
import com.kekwy.tankwar.client.GameScene;
import com.kekwy.tankwar.client.gamescenes.LocalPlayScene;

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
		MapTile[][] mapTiles = new MapTile[GAME_MAP_ROW][GAME_MAP_COL];
		MapTile.base = 0;
		for (int i = 0; i < mapData.length; i++) {
			int[] mapDatum = mapData[i];
			for (int j = 0; j < mapDatum.length; j++) {
				int x = j * MapTile.TILE_WIDTH + MapTile.TILE_WIDTH / 2;
				int y = i * MapTile.TILE_WIDTH + MapTile.TILE_WIDTH / 2;
				switch (mapDatum[j]) {
					case 1 -> mapTiles[i][j] = new MapTile(parent, MapTile.Type.TYPE_NORMAL, x, y);
					case 2 -> mapTiles[i][j] = new MapTile(parent, MapTile.Type.TYPE_HARD, x, y);
					case 3 -> mapTiles[i][j] = new MapTile(parent, MapTile.Type.TYPE_COVER, x, y);
					case 4 -> {
						mapTiles[i][j] = new MapTile(parent, MapTile.Type.TYPE_BASE, x, y);
						MapTile.base++;
					}
				}
			}
		}
		((LocalPlayScene)parent).setGameMap(mapTiles);
	}

}
