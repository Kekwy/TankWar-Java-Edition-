package com.kekwy.tankwar.io.handlers.server;

import com.kekwy.tankwar.server.GameScene;
import com.kekwy.tankwar.server.tank.PlayerTank;
import com.kekwy.tankwar.util.Direction;
import com.kekwy.tankwar.io.actions.GameAction;
import com.kekwy.tankwar.io.actions.JoinGameAction;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.logging.Logger;

public class JoinGameHandler implements GameHandler {
	@Override
	public void handleAction(GameScene scene, GameAction action, SocketChannel channel, ByteBuffer buffer, Logger logger) {
		if(!(action instanceof JoinGameAction p)) return;
//		PlayerTank tank = new PlayerTank(scene, null, p.uuid, 200, 300, Direction.DIR_UP, p.name);
//		scene.addGameObject(tank);
	}
}
