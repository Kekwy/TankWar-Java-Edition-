package com.kekwy.jw.server.handler;

import com.kekwy.jw.server.GameServer;
import com.kekwy.jw.server.game.GameScene;
import com.kekwy.jw.server.game.tank.PlayerTank;
import com.kekwy.jw.server.util.Direction;
import com.kekwy.tankwar.io.actions.JoinGameAction;
import com.kekwy.tankwar.io.actions.GameAction;

import java.nio.channels.SocketChannel;

public class JoinGameHandler implements Handler {

	GameScene scene;
	GameServer server;

	public JoinGameHandler(GameScene scene, GameServer server) {
		this.scene = scene;
		this.server = server;
	}

	@Override
	public void handle(GameAction protocol, SocketChannel channel) {
		if(!(protocol instanceof JoinGameAction p)) return;
		PlayerTank tank = new PlayerTank(scene, server, p.uuid, 200, 300, Direction.DIR_UP, p.name);
		scene.addGameObject(tank);
	}
}
