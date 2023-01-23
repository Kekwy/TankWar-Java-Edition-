package com.kekwy.tankwar.io.handlers.server;

import com.kekwy.tankwar.server.GameScene;
import com.kekwy.tankwar.server.tank.PlayerTank;
import com.kekwy.tankwar.server.tank.Tank;
import com.kekwy.tankwar.util.Direction;
import com.kekwy.tankwar.io.actions.GameAction;
import com.kekwy.tankwar.io.actions.PlayerMoveAction;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.logging.Logger;

public class PlayerMoveHandler implements GameHandler {
	@Override
	public void handleAction(GameScene scene, GameAction action, SocketChannel channel, ByteBuffer buffer, Logger logger) {
		if (!(action instanceof PlayerMoveAction moveAction)) throw new RuntimeException("错误的处理方法调用");
		PlayerTank tank = (PlayerTank) scene.findObject(moveAction.uuid);
		tank.setState(Tank.State.values()[moveAction.state]);
		tank.setDirection(Direction.values()[moveAction.direction]);
		logger.info("玩家 " + tank.getName() + "[" + tank.getIdentity() + "] 状态更新：\n" +
				"状态：" + tank.getState() + "；方向：" + tank.getDirection());
	}
}
