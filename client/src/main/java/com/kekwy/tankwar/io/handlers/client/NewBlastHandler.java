package com.kekwy.tankwar.io.handlers.client;

import com.kekwy.tankwar.client.GameScene;
import com.kekwy.tankwar.client.effect.Blast;
import com.kekwy.tankwar.io.actions.GameAction;
import com.kekwy.tankwar.io.actions.NewBlastAction;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class NewBlastHandler implements GameHandler {
	@Override
	public void handleAction(GameScene scene, GameAction action, SocketChannel channel, ByteBuffer buffer) {
		if (!(action instanceof NewBlastAction newAction)) return;
		Blast blast = Blast.createBlast(scene, newAction.x, newAction.y);
		blast.setIdentity(newAction.identity);
		scene.addGameObject(blast);
	}
}
