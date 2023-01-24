package com.kekwy.tankwar.io.handlers.server;

import com.kekwy.tankwar.io.actions.ChangeTeamAction;
import com.kekwy.tankwar.io.actions.GameAction;
import com.kekwy.tankwar.server.GameObject;
import com.kekwy.tankwar.server.GameScene;
import com.kekwy.tankwar.server.gamescenes.RoomScene;
import com.kekwy.tankwar.server.tank.PlayerTank;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.logging.Logger;

public class ChangeTeamHandler implements GameHandler {

	static final String[] teamNames = {
			"红队",
			"绿队",
			"蓝队",
			"黄队"
	};

	@Override
	public void handleAction(GameScene scene, GameAction action, SocketChannel channel, ByteBuffer buffer, Logger logger) {
		if (!(scene instanceof RoomScene roomScene)) return; // 若服务器不处于房间场景，则忽略该请求。
		if (!(action instanceof ChangeTeamAction changeAction)) throw new RuntimeException("错误的方法调用");
		logger.info("[INFO] 玩家 %s 请求变更队伍到 %s".formatted(changeAction.name, teamNames[changeAction.team]));
		boolean flag = roomScene.changeTeam(changeAction.name, changeAction.team, changeAction.oldTeam);
		if (!flag) {
			logger.info("[INFO] 玩家 %s 请求变更队伍到 %s 失败，该队伍已满".formatted(changeAction.name, teamNames[changeAction.team]));
			changeAction.stateCode = -1;
		} else {
			logger.info("[INFO] 玩家 %s 请求变更队伍到 %s 成功".formatted(changeAction.name, teamNames[changeAction.team]));
			changeAction.stateCode = 0;
		}
		GameObject object = scene.findObject(changeAction.uuid);
		if (object == null) throw new RuntimeException();
		if (!(object instanceof PlayerTank tank)) throw new RuntimeException();
		tank.setGroup(changeAction.team);
		changeAction.send(channel, buffer);
	}
}
