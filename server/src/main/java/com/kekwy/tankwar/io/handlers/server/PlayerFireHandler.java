package com.kekwy.tankwar.io.handlers.server;

import com.kekwy.tankwar.server.GameScene;
import com.kekwy.tankwar.server.tank.PlayerTank;
import com.kekwy.tankwar.io.actions.GameAction;
import com.kekwy.tankwar.io.actions.PlayerFireAction;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.logging.Logger;

public class PlayerFireHandler implements GameHandler {
	@Override
	public void handleAction(GameScene scene, GameAction action, SocketChannel channel, ByteBuffer buffer, Logger logger) {
		if (!(action instanceof PlayerFireAction fireAction)) return;
		PlayerTank tank = (PlayerTank) scene.findObject(fireAction.uuid);
		tank.fire();
		logger.info("玩家 " + tank.getName() + "[" + tank.getIdentity() + "] 开火！");
	}
}
