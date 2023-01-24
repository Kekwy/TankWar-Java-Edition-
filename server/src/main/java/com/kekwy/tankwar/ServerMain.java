package com.kekwy.tankwar;

import com.kekwy.tankwar.server.ServerCore;
import com.kekwy.tankwar.server.gamescenes.RoomScene;

import java.io.IOException;

public class ServerMain {
	// TODO 从配置文件中读取
	private static final int PORT = 2727;
	private static final String HOST = "localhost";

	public static void main(String[] args) throws IOException {
		new RoomScene().start();
//		new ServerCore(HOST, PORT).launch();
	}

}
