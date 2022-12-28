package com.kekwy.jw.tankwar.handler;

import com.kekwy.jw.tankwar.gamescenes.OnlinePlayScene;
import com.kekwy.tankwar.server.io.LoginSuccess;
import com.kekwy.tankwar.server.io.Protocol;

public class LoginSuccessHandler implements Handler {

	private final OnlinePlayScene scene;

	public LoginSuccessHandler(OnlinePlayScene gameScene) {
		this.scene = gameScene;
	}

	@Override
	public void handle(Protocol protocol) {
		if (!(protocol instanceof LoginSuccess p)) return;
		scene.setUuid(p.uuid);
	}
}
