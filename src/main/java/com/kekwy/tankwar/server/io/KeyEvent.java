package com.kekwy.tankwar.server.io;

public class KeyEvent extends Protocol {
	public String uuid;
	public int keyCode;

	private final int type;

	public KeyEvent(String uuid, int keyCode, int type) {
		this.uuid = uuid;
		this.keyCode = keyCode;
		this.type = type;
	}

	public boolean isPressEvent() {
		return type == 0;
	}

	public boolean isReleaseEvent() {
		return type == 1;
	}

}
