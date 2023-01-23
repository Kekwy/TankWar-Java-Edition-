package com.kekwy.tankwar.io.actions;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class NewTankAction extends NewObjectAction {

	//
	//
	/**
	 * 0 - playerTank<br/>
	 * 1 - enemyTank<br/>
	 */
	public int typeCode;

	public double x, y;
	public int direction;
	public String name;
	public int group;
	public double r, g, b;

	public NewTankAction(String identity, String className, int typeCode, double x, double y,
	                     int direction, String name, int group, double r, double g, double b) {
		super(identity, className);
		this.typeCode = typeCode;
		this.x = x;
		this.y = y;
		this.direction = direction;
		this.name = name;
		this.group = group;
		this.r = r;
		this.g = g;
		this.b = b;
	}

	@Override
	public void send(SocketChannel channel, ByteBuffer buffer) {
		super.send(channel, buffer);

	}
}
