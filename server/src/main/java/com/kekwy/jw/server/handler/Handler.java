package com.kekwy.jw.server.handler;

import com.kekwy.tankwar.server.io.Protocol;

import java.nio.channels.SocketChannel;

public interface Handler {
	void handle(Protocol protocol, SocketChannel channel);
}
