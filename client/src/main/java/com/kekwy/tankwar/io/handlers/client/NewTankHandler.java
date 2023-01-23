package com.kekwy.tankwar.io.handlers.client;

import com.kekwy.tankwar.client.GameScene;
import com.kekwy.tankwar.client.tank.PlayerTank;
import com.kekwy.tankwar.client.util.Direction;
import com.kekwy.tankwar.io.actions.GameAction;
import com.kekwy.tankwar.io.actions.NewTankAction;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class NewTankHandler implements GameHandler {
	@Override
	public void handleAction(GameScene scene, GameAction action, SocketChannel channel, ByteBuffer buffer) {
		if (!(action instanceof NewTankAction newTankAction)) return;
		if (newTankAction.typeCode == 0) {
			scene.addGameObject(new PlayerTank(scene, newTankAction.x, newTankAction.y,
					Direction.values()[newTankAction.direction], newTankAction.name,
					newTankAction.group, newTankAction.identity));
		} else if (newTankAction.typeCode == 1) {
//				scene.addGameObject(new EnemyTank(scene));
		} else throw new RuntimeException("???");
	}
}
