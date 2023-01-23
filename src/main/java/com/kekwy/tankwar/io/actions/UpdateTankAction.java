package com.kekwy.tankwar.io.actions;

import com.kekwy.tankwar.util.ChannelIOUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class UpdateTankAction extends UpdatePositionAction {

	public int hp;
	public int state;

	public UpdateTankAction(String uuid, double x, double y, int direction, boolean active, int hp, int state) {
		super(uuid, x, y, direction, active);
		this.hp = hp;
		this.state = state;
	}

	public UpdateTankAction(SocketChannel channel, ByteBuffer buffer) {
		super(channel, buffer);
		try {
			hp = ChannelIOUtil.readInt(channel, buffer);
			state = ChannelIOUtil.readInt(channel, buffer);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void send(SocketChannel channel, ByteBuffer buffer) {
		super.send(UPDATE_TANK_CODE, buffer);
		buffer.putInt(hp);
		buffer.putInt(state);
		buffer.flip();
		try {
			channel.write(buffer);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}
}
