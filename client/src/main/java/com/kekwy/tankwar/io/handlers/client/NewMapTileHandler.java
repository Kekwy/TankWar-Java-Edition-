package com.kekwy.tankwar.io.handlers.client;

import com.kekwy.tankwar.client.GameScene;
import com.kekwy.tankwar.io.actions.GameAction;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class NewMapTileHandler implements GameHandler {
	@Override
	public void handleAction(GameScene scene, GameAction action, SocketChannel channel, ByteBuffer buffer) {

	}
}
