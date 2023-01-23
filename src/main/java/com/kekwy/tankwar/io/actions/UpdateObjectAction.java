package com.kekwy.tankwar.io.actions;

import com.kekwy.tankwar.util.ChannelIOUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public abstract class UpdateObjectAction extends GameAction {

	/**
	 * 0 - false; <br/>
	 * 1 - true; <br/>
	 */
	public String identity;
	public boolean active;

	public UpdateObjectAction(String identity, boolean active) {
		this.identity = identity;
		this.active = active;
	}

	public UpdateObjectAction(SocketChannel channel, ByteBuffer buffer) {
		try {
			identity = ChannelIOUtil.readString(channel, buffer);
			active = ChannelIOUtil.readByte(channel, buffer) != 0;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void send(int actionCode, ByteBuffer buffer) {
		try {
			buffer.clear();
			buffer.putInt(actionCode);
			ChannelIOUtil.writeString(identity, buffer);
			buffer.put((byte) (active ? 1 : 0));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
