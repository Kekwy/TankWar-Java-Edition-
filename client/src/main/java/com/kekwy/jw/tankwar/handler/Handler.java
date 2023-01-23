package com.kekwy.jw.tankwar.handler;

import com.kekwy.tankwar.io.actions.GameAction;

public interface Handler {
	void handle(GameAction protocol);
}
