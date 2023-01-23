package com.kekwy.tankwar.io.actions;

import com.kekwy.tankwar.util.ChannelIOUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public abstract class NewObjectAction extends GameAction {

	public String identity;
	public String className;

	public NewObjectAction(String identity, String className) {
		this.identity = identity;
		this.className = className;
	}

	public NewObjectAction(SocketChannel channel, ByteBuffer buffer) {
		try {
			identity = ChannelIOUtil.readString(channel, buffer);
			className = ChannelIOUtil.readString(channel, buffer);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void send(int actionCode, SocketChannel channel, ByteBuffer buffer) {
		buffer.clear();
		buffer.putInt(actionCode);
		try {
			ChannelIOUtil.writeString(identity, buffer);
			ChannelIOUtil.writeString(className, buffer);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}