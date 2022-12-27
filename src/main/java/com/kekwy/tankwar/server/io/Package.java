package com.kekwy.tankwar.server.io;

import java.io.Serializable;

public class Package implements Serializable {
	private int number;
	private Protocol payload;

	public void init(int number, Protocol payload) {
		this.number = number;
		this.payload = payload;
	}

	public int getNumber() {
		return number;
	}

	public Protocol getPayload() {
		return payload;
	}
}
