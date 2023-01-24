package com.kekwy.tankwar.io.actions;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class NewMapTileAction extends NewObjectAction {



	public NewMapTileAction(String identity, String className, double x, double y) {
		super(identity, className, x, y);
	}

	@Override
	public void send(SocketChannel channel, ByteBuffer buffer) {

	}
}
