package com.kekwy.tankwar.io.handlers.client;

import com.kekwy.tankwar.client.GameObject;
import com.kekwy.tankwar.client.GameScene;
import com.kekwy.tankwar.client.tank.Bullet;
import com.kekwy.tankwar.client.util.Direction;
import com.kekwy.tankwar.io.actions.GameAction;
import com.kekwy.tankwar.io.actions.UpdateBulletAction;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class UpdateBulletHandler implements GameHandler {
	@Override
	public void handleAction(GameScene scene, GameAction action, SocketChannel channel, ByteBuffer buffer) {
		if (!(action instanceof UpdateBulletAction updateAction)) throw new RuntimeException("错误的处理方法调用");
		GameObject object = scene.findObject(updateAction.identity);
		if (object == null) throw new RuntimeException("不存在的对象");
		if (!(object instanceof Bullet bullet)) throw new RuntimeException("错误的对象类型");
		bullet.setActive(updateAction.active);
		bullet.transform.setX(updateAction.x);
		bullet.transform.setY(updateAction.y);
		bullet.setDirection(Direction.values()[updateAction.direction]);
	}
}
