package com.kekwy.tankwar.io.handlers.client;

import com.kekwy.tankwar.client.GameScene;
import com.kekwy.tankwar.client.gamescenes.RoomScene;
import com.kekwy.tankwar.io.actions.EnterRoomAction;
import com.kekwy.tankwar.io.actions.GameAction;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class EnterRoomHandler implements GameHandler {
	@Override
	public void handleAction(GameScene scene, GameAction action, SocketChannel channel, ByteBuffer buffer) {
		if (!(scene instanceof RoomScene roomScene)) return; // 若服务器不处于房间场景，则忽略该请求。
		if (!(action instanceof EnterRoomAction enterAction)) throw new RuntimeException("错误的方法调用");
		roomScene.addPlayer(enterAction.name, enterAction.team);
	}
}
