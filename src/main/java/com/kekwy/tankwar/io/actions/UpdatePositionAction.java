package com.kekwy.tankwar.io.actions;

import com.kekwy.tankwar.util.ChannelIOUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public abstract class UpdatePositionAction extends UpdateObjectAction {

	public double x, y;
	public int direction;

	public UpdatePositionAction(String identity, double x, double y, int direction, boolean active) {
		super(identity, active);
		this.x = x;
		this.y = y;
		this.direction = direction;
	}

	public UpdatePositionAction(SocketChannel channel, ByteBuffer buffer) {
		super(channel, buffer);
		try {
			x = ChannelIOUtil.readDouble(channel, buffer);
			y = ChannelIOUtil.readDouble(channel, buffer);
			direction = ChannelIOUtil.readInt(channel, buffer);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}


	@Override
	public void send(int actionCode, ByteBuffer buffer) {
		super.send(actionCode, buffer);
		buffer.putDouble(x);
		buffer.putDouble(y);
		buffer.putInt(direction);
	}

}
