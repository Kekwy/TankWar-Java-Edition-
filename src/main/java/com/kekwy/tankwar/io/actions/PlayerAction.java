package com.kekwy.tankwar.io.actions;

import com.kekwy.tankwar.util.ChannelIOUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public abstract class PlayerAction extends GameAction {

	public String uuid;
	// more members...


	public PlayerAction(String uuid) {
		this.uuid = uuid;
	}

	public PlayerAction(SocketChannel channel, ByteBuffer buffer) {
		try {
			uuid = ChannelIOUtil.readString(channel, buffer);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void send(int actionCode, SocketChannel channel, ByteBuffer buffer) {
		try {
			buffer.clear();
			buffer.putInt(actionCode);
			ChannelIOUtil.writeString(uuid, buffer);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
