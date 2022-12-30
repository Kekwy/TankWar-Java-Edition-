package com.kekwy.tankwar.server.io;

public class FrameUpdate extends Protocol {

	public String uuid;
	public double x, y;
	public int direction;
	public int state;

	public FrameUpdate(String uuid, double x, double y, int direction, int state) {
		this.x = x;
		this.y = y;
		this.direction = direction;
		this.state = state;
		this.uuid = uuid;
	}
}
