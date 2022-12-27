package com.kekwy.tankwar.server.io;

import java.io.Serializable;

public class Protocol implements Serializable {
	public static final int NUMBER_LOGIN = 0x00;
	public static final int NUMBER_LOGIN_FAILED = 0x01;
	public static final int NUMBER_LOGIN_SUCCESS = 0x02;
}
