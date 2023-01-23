package com.kekwy.tankwar.io.actions;

import com.kekwy.tankwar.util.ChannelIOUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class UpdateMapTileAction extends UpdateObjectAction {

	public int hp;

	public UpdateMapTileAction(String identity, boolean active, int hp) {
		super(identity, active);
		this.hp = hp;
	}

	public UpdateMapTileAction(SocketChannel channel, ByteBuffer buffer) {
		super(channel, buffer);
		try {
			hp = ChannelIOUtil.readInt(channel, buffer);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void send(SocketChannel channel, ByteBuffer buffer) {
		super.send(UPDATE_MAP_TILE_CODE, buffer);
		buffer.putInt(hp);
		buffer.flip();
		try {
			channel.write(buffer);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
