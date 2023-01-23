package com.kekwy.tankwar.io.actions;

import com.kekwy.tankwar.util.ChannelIOUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class PlayerMoveAction extends PlayerAction {

	public int direction;
	public int state;

	public PlayerMoveAction(String uuid, int direction, int state) {
		super(uuid);
		this.direction = direction;
		this.state = state;
	}

	public PlayerMoveAction(SocketChannel channel, ByteBuffer buffer) {
		super(channel, buffer);
		try {
			direction = ChannelIOUtil.readInt(channel, buffer);
			state = ChannelIOUtil.readInt(channel, buffer);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void send(SocketChannel channel, ByteBuffer buffer) {
		super.send(PLAYER_MOVE_CODE, channel, buffer);
		buffer.putInt(direction);
		buffer.putInt(state);
		buffer.flip();

		try {
			channel.write(buffer);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
