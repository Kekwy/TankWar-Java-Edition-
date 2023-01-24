package com.kekwy.tankwar.io.actions;

import com.kekwy.tankwar.util.ChannelIOUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class UpdateMapTileAction extends UpdateObjectAction {

	public UpdateMapTileAction(String identity, boolean active) {
		super(identity, active);
	}

	public UpdateMapTileAction(SocketChannel channel, ByteBuffer buffer) {
		super(channel, buffer);
	}

	@Override
	public void send(SocketChannel channel, ByteBuffer buffer) {
		super.send(UPDATE_MAP_TILE_CODE, buffer);
		buffer.flip();
		try {
			channel.write(buffer);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
