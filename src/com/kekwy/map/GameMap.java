package com.kekwy.map;

import com.kekwy.game.GameFrame;
import com.kekwy.game.Tank;
import com.kekwy.game.TankHouse;
import com.kekwy.util.Constant;
import com.kekwy.util.MapTilePool;
import com.kekwy.util.MyUtil;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 游戏地图
 */
public class GameMap {
	public static final int MAP_X = Tank.RADIUS * 3;
	public static final int MAP_Y = Tank.RADIUS * 3 + GameFrame.titleBarH;
	public static final int MAP_WIDTH = Constant.FRAME_WIDTH - Tank.RADIUS * 6;
	public static final int MAP_HEIGHT = Constant.FRAME_HEIGHT - Tank.RADIUS * 8 - GameFrame.titleBarH;

	private int x, y;
	private int width, height;

	// 游戏地图元素块的容器
	private List<MapTile> tiles = new ArrayList<>();

	public GameMap() {
		initMap();
	}

	/**
	 * 初始化地图元素块
	 */
	private TankHouse tankHouse;
	private void initMap() {
		final int COUNT = 20;
		for (int i = 0; i < COUNT; i++) {
			MapTile tile = MapTilePool.takeAway();
			int x = MyUtil.getRandomNumber(MAP_X, MAP_X + MAP_WIDTH - MapTile.tileW);
			int y = MyUtil.getRandomNumber(MAP_Y, MAP_Y + MAP_HEIGHT - MapTile.tileW);
			tile.setX(x);
			tile.setY(y);
			tiles.add(tile);
		}
		tankHouse = new TankHouse();
		tiles.addAll(tankHouse.getTiles());
	}
	public void draw(Graphics g) {
		for (MapTile tile : tiles) {
			tile.draw(g);
		}
		// tankHouse.draw(g);
	}

	public List<MapTile> getTiles() {
		return tiles;
	}

	public void clearDestroyTile() {
		for (int i = 0; i < tiles.size(); i++) {
			MapTile tile = tiles.get(i);
			if(!tile.isVisible()) {
				tiles.remove(i);
				i--;
			}
		}
	}
}
