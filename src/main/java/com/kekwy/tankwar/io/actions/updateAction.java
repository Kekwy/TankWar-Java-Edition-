package com.kekwy.tankwar.io.actions;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class updateAction extends GameAction {

	public String uuid;
	public double x, y;
	public int direction;
	public int state;

	public updateAction(String uuid, double x, double y, int direction, int state) {
		this.x = x;
		this.y = y;
		this.direction = direction;
		this.state = state;
		this.uuid = uuid;
	}

	public updateAction(SocketChannel channel, ByteBuffer buffer) {
	}

	@Override
	public void send(SocketChannel channel, ByteBuffer buffer) {

	}
}
