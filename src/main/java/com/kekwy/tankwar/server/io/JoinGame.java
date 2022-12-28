package com.kekwy.tankwar.server.io;

public class JoinGame extends Protocol {
	/**
	 * -1 -- 加入失败
	 *  1 -- 加入成功
	 */
	int code = -1;
	public String uuid;
	public String name;

	public JoinGame(String uuid, String name) {
		this.uuid = uuid;
		this.name = name;
	}

	public void init(String uuid, String name) {
		this.uuid = uuid;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String getUuid() {
		return uuid;
	}
}
