package com.kekwy.tankwar.io.handlers.server;

import com.kekwy.tankwar.server.GameScene;
import com.kekwy.tankwar.io.actions.GameAction;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.logging.Logger;

public interface GameHandler {
	void handleAction(GameScene scene, GameAction action, SocketChannel channel, ByteBuffer buffer, Logger logger);
}
