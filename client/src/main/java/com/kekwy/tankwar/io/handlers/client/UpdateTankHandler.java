package com.kekwy.tankwar.io.handlers.client;

import com.kekwy.jw.tankwar.GameObject;
import com.kekwy.jw.tankwar.GameScene;
import com.kekwy.jw.tankwar.tank.Tank;
import com.kekwy.jw.tankwar.util.Direction;
import com.kekwy.tankwar.io.actions.GameAction;
import com.kekwy.tankwar.io.actions.UpdateTankAction;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class UpdateTankHandler implements GameHandler {
	@Override
	public void handleAction(GameScene scene, GameAction action, SocketChannel channel, ByteBuffer buffer) {
		if (!(action instanceof UpdateTankAction updateAction)) throw new RuntimeException("错误的处理方法调用");
		GameObject object = scene.findObject(updateAction.identity);
		if (object == null) throw new RuntimeException("对象不存在");
		if (!(object instanceof Tank tank)) throw new RuntimeException("错误的对象类型");
		tank.setActive(updateAction.active);
		tank.transform.setX(updateAction.x);
		tank.transform.setY(updateAction.y);
		tank.setDirection(Direction.values()[updateAction.direction]);
		tank.setHp(updateAction.hp);
		tank.setState(Tank.State.values()[updateAction.state]);
	}
}
