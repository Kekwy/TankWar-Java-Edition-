package com.kekwy.tankwar.io.actions;

import com.kekwy.tankwar.util.ChannelIOUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class GameStartAction extends GameAction {

	public String uuid;

	public GameStartAction(String uuid) {
		this.uuid = uuid;
	}

	public GameStartAction(SocketChannel channel, ByteBuffer buffer) {
		try {
			uuid = ChannelIOUtil.readString(channel, buffer);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void send(SocketChannel channel, ByteBuffer buffer) {
		try {
			buffer.clear();
			buffer.putInt(GAME_START_CODE);
			ChannelIOUtil.writeString(uuid, buffer);
			buffer.flip();
			channel.write(buffer);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
