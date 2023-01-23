package com.kekwy.jw.server.handler;

import com.kekwy.tankwar.io.actions.GameAction;

import java.nio.channels.SocketChannel;

public interface Handler {
	void handle(GameAction protocol, SocketChannel channel);
}
