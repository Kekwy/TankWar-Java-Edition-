package com.kekwy.tankwar.io.actions;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class PlayerFireAction extends PlayerAction {

	public PlayerFireAction(String uuid) {
		super(uuid);
	}

	public PlayerFireAction(SocketChannel channel, ByteBuffer buffer) {
		super(channel, buffer);
	}

	@Override
	public void send(SocketChannel channel, ByteBuffer buffer) {
		super.send(PLAYER_FIRE_CODE, channel, buffer);
		buffer.flip();
		try {
			channel.write(buffer);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
