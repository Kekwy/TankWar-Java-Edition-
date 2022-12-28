package com.kekwy.tankwar.server.io;

public class GameUpdate extends Protocol {
	String uuid;
	String className;

	double x, y;
	int direction;
	String name;
	int group;

	public String getUuid() {
		return uuid;
	}

	public String getClassName() {
		return className;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public int getDirection() {
		return direction;
	}

	public int getGroup() {
		return group;
	}

	public String getName() {
		return name;
	}
}
