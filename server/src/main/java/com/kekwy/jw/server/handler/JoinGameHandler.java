package com.kekwy.jw.server.handler;

import com.kekwy.jw.server.GameServer;
import com.kekwy.jw.server.game.GameScene;
import com.kekwy.jw.server.game.tank.PlayerTank;
import com.kekwy.jw.server.util.Direction;
import com.kekwy.tankwar.server.io.JoinGame;
import com.kekwy.tankwar.server.io.NewPlayerTank;
import com.kekwy.tankwar.server.io.Protocol;

import java.nio.channels.SocketChannel;

public class JoinGameHandler implements Handler {

	GameScene scene;
	GameServer server;

	public JoinGameHandler(GameScene scene, GameServer server) {
		this.scene = scene;
		this.server = server;
	}

	@Override
	public void handle(Protocol protocol, SocketChannel channel) {
		if(!(protocol instanceof JoinGame p)) return;
		PlayerTank tank = new PlayerTank(scene, 200, 300, Direction.DIR_UP, p.name);
		NewPlayerTank newPlayerTank = new NewPlayerTank();
		newPlayerTank.direction = 0;
		newPlayerTank.r = 1;
		newPlayerTank.g = 1;
		newPlayerTank.b = 1;
		newPlayerTank.x = 200;
		newPlayerTank.y = 300;
		newPlayerTank.group = 1;
		newPlayerTank.name = p.name;
		newPlayerTank.uuid = p.uuid;
		server.send(channel, newPlayerTank);
	}
}
