package com.kekwy.tankwar.io.handlers.client;

import com.kekwy.tankwar.client.GameScene;
import com.kekwy.tankwar.io.actions.GameAction;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public interface GameHandler {
	void handleAction(GameScene scene, GameAction action, SocketChannel channel, ByteBuffer buffer);
}
