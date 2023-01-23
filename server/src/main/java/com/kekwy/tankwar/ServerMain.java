package com.kekwy.tankwar;

import com.kekwy.tankwar.server.GameServer;

import java.io.IOException;

public class ServerMain {
	// TODO 从配置文件中读取
	private static final int PORT = 2727;
	private static final String HOST = "localhost";

	public static void main(String[] args) throws IOException {
		new GameServer(HOST, PORT).launch();
	}

}
