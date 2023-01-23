package com.kekwy.tankwar.io.actions;

import com.kekwy.tankwar.util.ChannelIOUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class JoinGameAction extends GameAction {

	/**
	 * -1 -- 加入失败
	 * 1 -- 加入成功
	 */
	public int stateCode = -1;
	public String uuid;
	public String name;

	public JoinGameAction(String uuid, String name) {
		this.uuid = uuid;
		this.name = name;
	}

	public JoinGameAction(SocketChannel channel, ByteBuffer buffer) {
		try {
			stateCode = ChannelIOUtil.readInt(channel, buffer);
			uuid = ChannelIOUtil.readString(channel, buffer);
			name = ChannelIOUtil.readString(channel, buffer);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void send(SocketChannel channel, ByteBuffer buffer) {
		buffer.clear();
		buffer.putInt(JOIN_CODE);
		buffer.putInt(stateCode);
		try {
			ChannelIOUtil.writeString(uuid, buffer);
			ChannelIOUtil.writeString(name, buffer);
			buffer.flip();
			channel.write(buffer);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}
}
