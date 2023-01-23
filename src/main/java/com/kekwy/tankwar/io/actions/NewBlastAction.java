package com.kekwy.tankwar.io.actions;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class NewBlastAction extends NewObjectAction {

	public NewBlastAction(String identity, double x, double y) {
		super(identity, "className", x, y);
	}

	public NewBlastAction(SocketChannel channel, ByteBuffer buffer) {
		super(channel, buffer);
	}

	@Override
	public void send(SocketChannel channel, ByteBuffer buffer) {
		super.send(NEW_BLAST_CODE, buffer);
		buffer.flip();
		try {
			channel.write(buffer);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
