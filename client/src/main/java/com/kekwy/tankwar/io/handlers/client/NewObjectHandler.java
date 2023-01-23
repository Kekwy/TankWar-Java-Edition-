package com.kekwy.tankwar.io.handlers.client;

import com.kekwy.jw.tankwar.GameScene;
import com.kekwy.jw.tankwar.tank.EnemyTank;
import com.kekwy.jw.tankwar.tank.PlayerTank;
import com.kekwy.jw.tankwar.util.Direction;
import com.kekwy.tankwar.io.actions.GameAction;
import com.kekwy.tankwar.io.actions.NewObjectAction;
import com.kekwy.tankwar.io.actions.NewTankAction;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class NewObjectHandler implements GameHandler {
	@Override
	public void handleAction(GameScene scene, GameAction action, SocketChannel channel, ByteBuffer buffer) {
		if (!(action instanceof NewObjectAction)) return;
		if (action instanceof NewTankAction newTankAction) {
			if (newTankAction.typeCode == 0) {
				scene.addGameObject(new PlayerTank(scene, newTankAction.x, newTankAction.y,
						Direction.values()[newTankAction.direction], newTankAction.name,
						newTankAction.group, newTankAction.identity));
			} else if (newTankAction.typeCode == 1) {
//				scene.addGameObject(new EnemyTank(scene));
			} else throw new RuntimeException("???");
		} else {
			// TODO
		}
	}
}
