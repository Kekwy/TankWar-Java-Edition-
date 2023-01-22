package com.kekwy.jw.tankwar.handler;

import com.kekwy.tankwar.server.io.Protocol;

public interface Handler {
	void handle(Protocol protocol);
}
