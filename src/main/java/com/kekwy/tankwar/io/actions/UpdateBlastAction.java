package com.kekwy.tankwar.io.actions;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class UpdateBlastAction extends UpdateObjectAction {

	public UpdateBlastAction(String identity, boolean active) {
		super(identity, active);
	}

	public UpdateBlastAction(SocketChannel channel, ByteBuffer buffer) {
		super(channel, buffer);
	}

	@Override
	public void send(SocketChannel channel, ByteBuffer buffer) {

	}
}
