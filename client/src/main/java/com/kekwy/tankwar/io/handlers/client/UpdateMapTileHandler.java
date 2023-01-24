package com.kekwy.tankwar.io.handlers.client;

import com.kekwy.tankwar.client.GameObject;
import com.kekwy.tankwar.client.GameScene;
import com.kekwy.tankwar.client.gamemap.MapTile;
import com.kekwy.tankwar.io.actions.GameAction;
import com.kekwy.tankwar.io.actions.UpdateMapTileAction;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class UpdateMapTileHandler implements GameHandler {
	@Override
	public void handleAction(GameScene scene, GameAction action, SocketChannel channel, ByteBuffer buffer) {
		if (!(action instanceof UpdateMapTileAction updateAction)) throw new RuntimeException("错误的处理方法调用");
		GameObject object = scene.findObject(updateAction.identity);
		if (object == null) throw new RuntimeException("对象不存在");
		if (!(object instanceof MapTile tile)) throw new RuntimeException("错误的对象类型");
		tile.setActive(updateAction.active);
	}
}
