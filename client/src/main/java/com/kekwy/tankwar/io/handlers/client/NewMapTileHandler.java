package com.kekwy.tankwar.io.handlers.client;

import com.kekwy.tankwar.client.GameScene;
import com.kekwy.tankwar.client.gamemap.MapTile;
import com.kekwy.tankwar.io.actions.GameAction;
import com.kekwy.tankwar.io.actions.NewMapTileAction;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class NewMapTileHandler implements GameHandler {
	@Override
	public void handleAction(GameScene scene, GameAction action, SocketChannel channel, ByteBuffer buffer) {
		if (!(action instanceof NewMapTileAction newAction)) throw new RuntimeException();
		MapTile tile = new MapTile(scene, MapTile.Type.values()[newAction.type], newAction.x, newAction.y, newAction.identity);
	}
}
