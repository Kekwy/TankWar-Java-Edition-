package com.kekwy.tankwar.io.handlers.server;

import com.kekwy.tankwar.io.actions.GameAction;
import com.kekwy.tankwar.io.actions.GameStartAction;
import com.kekwy.tankwar.io.actions.LoginAction;
import com.kekwy.tankwar.server.GameScene;
import com.kekwy.tankwar.server.gamescenes.RoomScene;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Objects;
import java.util.logging.Logger;

public class GameStartHandler implements GameHandler {
	@Override
	public void handleAction(GameScene scene, GameAction action, SocketChannel channel, ByteBuffer buffer, Logger logger) {
		if (!(scene instanceof RoomScene roomScene)) return; // 若服务器不处于房间场景，则忽略该请求.
		if (!(action instanceof GameStartAction startAction)) throw new RuntimeException();
		if (!Objects.equals(startAction.uuid, "1f1a778b-65f8-497e-a25b-c1d11123aecf")) return;
		scene.startGame();
	}
}
