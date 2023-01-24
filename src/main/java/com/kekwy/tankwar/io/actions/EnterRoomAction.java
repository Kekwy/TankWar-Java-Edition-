package com.kekwy.tankwar.io.actions;

import com.kekwy.tankwar.util.ChannelIOUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class EnterRoomAction extends GameAction {

	public String name;
	public int team;

	public EnterRoomAction(String name, int team) {
		this.name = name;
		this.team = team;
	}

	public EnterRoomAction(SocketChannel channel, ByteBuffer buffer) {
		try {
			name = ChannelIOUtil.readString(channel, buffer);
			team = ChannelIOUtil.readInt(channel, buffer);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void send(SocketChannel channel, ByteBuffer buffer) {
		try {
			buffer.clear();
			buffer.putInt(ENTER_ROOM_CODE);
			ChannelIOUtil.writeString(name, buffer);
			buffer.putInt(team);
			buffer.flip();
			channel.write(buffer);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}
}
