package com.kekwy.jw.tankwar.handler;

import com.kekwy.jw.tankwar.gamescenes.OnlinePlayScene;
import com.kekwy.tankwar.io.actions.GameAction;

public class LoginSuccessHandler implements Handler {

	private final OnlinePlayScene scene;

	public LoginSuccessHandler(OnlinePlayScene gameScene) {
		this.scene = gameScene;
	}

	@Override
	public void handle(GameAction protocol) {
//		if (!(protocol instanceof LoginSuccess p)) return;
//		scene.setUuid(p.uuid);
	}
}
