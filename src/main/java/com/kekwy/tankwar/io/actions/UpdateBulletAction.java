package com.kekwy.tankwar.io.actions;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class UpdateBulletAction extends UpdatePositionAction {

	public UpdateBulletAction(String uuid, double x, double y, int direction, boolean active) {
		super(uuid, x, y, direction, active);
	}

	public UpdateBulletAction(SocketChannel channel, ByteBuffer buffer) {
		super(channel, buffer);
	}

	@Override
	public void send(SocketChannel channel, ByteBuffer buffer) {
		super.send(UPDATE_BULLET_CODE, buffer);
		buffer.flip();
		try {
			channel.write(buffer);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
