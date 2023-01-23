package com.kekwy.tankwar.io.handlers.client;

import com.kekwy.jw.tankwar.GameObject;
import com.kekwy.jw.tankwar.GameScene;
import com.kekwy.jw.tankwar.tank.Bullet;
import com.kekwy.jw.tankwar.tank.Tank;
import com.kekwy.jw.tankwar.util.Direction;
import com.kekwy.tankwar.io.actions.GameAction;
import com.kekwy.tankwar.io.actions.NewBulletAction;
import javafx.scene.paint.Color;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class NewBulletHandler implements GameHandler {
	@Override
	public void handleAction(GameScene scene, GameAction action, SocketChannel channel, ByteBuffer buffer) {
		if (!(action instanceof NewBulletAction newAction)) return;
		GameObject object = scene.findObject(newAction.fromIdentity);
		if (object == null) throw new RuntimeException("不存在的对象：" + newAction.fromIdentity);
		if (!(object instanceof Tank from)) throw new RuntimeException("错误的对象类型" + object);
		Bullet bullet = Bullet.createBullet(scene, newAction.atk, Color.color(newAction.r, newAction.g, newAction.b),
				newAction.x, newAction.y, Direction.values()[newAction.direction], from);
		bullet.setIdentity(newAction.identity);
		scene.addGameObject(bullet);
	}
}
