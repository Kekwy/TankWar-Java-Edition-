package com.kekwy.tankwar.io.handlers.client;

import com.kekwy.tankwar.client.GameScene;
import com.kekwy.tankwar.client.gamescenes.RoomScene;
import com.kekwy.tankwar.io.actions.ChangeTeamAction;
import com.kekwy.tankwar.io.actions.GameAction;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class ChangeTeamHandler implements GameHandler {

	@Override
	public void handleAction(GameScene scene, GameAction action, SocketChannel channel, ByteBuffer buffer) {
		if (!(scene instanceof RoomScene roomScene)) return; // 若不处于房间场景，则忽略。
		if (!(action instanceof ChangeTeamAction changeAction)) throw new RuntimeException("错误的方法调用");
		if (changeAction.stateCode == 0) roomScene.changeTeam(changeAction.name, changeAction.team, changeAction.oldTeam);
	}
}
