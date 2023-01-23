package com.kekwy.tankwar.io.actions;

import com.kekwy.tankwar.util.ChannelIOUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public abstract class NewObjectAction extends GameAction {

	public String identity;
	public String className;
	public double x, y;
	public NewObjectAction(String identity, String className, double x, double y) {
		this.identity = identity;
		this.className = className;
		this.x = x;
		this.y = y;
	}

	public NewObjectAction(SocketChannel channel, ByteBuffer buffer) {
		try {
			identity = ChannelIOUtil.readString(channel, buffer);
			className = ChannelIOUtil.readString(channel, buffer);
			x = ChannelIOUtil.readDouble(channel, buffer);
			y = ChannelIOUtil.readDouble(channel, buffer);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void send(int actionCode, ByteBuffer buffer) {
		buffer.clear();
		buffer.putInt(actionCode);
		try {
			ChannelIOUtil.writeString(identity, buffer);
			ChannelIOUtil.writeString(className, buffer);
			buffer.putDouble(x);
			buffer.putDouble(y);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
