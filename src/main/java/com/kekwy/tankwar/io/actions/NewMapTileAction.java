package com.kekwy.tankwar.io.actions;

import com.kekwy.tankwar.util.ChannelIOUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class NewMapTileAction extends NewObjectAction {

	public int type;

	public NewMapTileAction(String identity, double x, double y, int type) {
		super(identity, "className", x, y);
		this.type = type;
	}

	public NewMapTileAction(SocketChannel channel, ByteBuffer buffer) {
		super(channel, buffer);
		try {
			type = ChannelIOUtil.readInt(channel, buffer);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void send(SocketChannel channel, ByteBuffer buffer) {
		super.send(NEW_MAP_TILE_CODE, buffer);
		try {

			buffer.putInt(type);

			buffer.flip();
			channel.write(buffer);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
