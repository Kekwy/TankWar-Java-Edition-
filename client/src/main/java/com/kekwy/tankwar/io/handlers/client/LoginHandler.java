package com.kekwy.tankwar.io.handlers.client;

import com.kekwy.tankwar.client.GameScene;
import com.kekwy.tankwar.io.actions.GameAction;
import com.kekwy.tankwar.io.actions.LoginAction;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class LoginHandler implements GameHandler {
	@Override
	public void handleAction(GameScene scene, GameAction action, SocketChannel channel, ByteBuffer buffer) {
		if (action instanceof LoginAction loginAction) {
			if (loginAction.stateCode == 0)
				scene.setPlayerUUid(loginAction.userUuid);
			else if (loginAction.stateCode == 1)
				throw new RuntimeException("登录失败，用户名或密码错误");
			else
				throw new RuntimeException("???");
		} else {
			throw new RuntimeException("调用了错误的处理方法");
		}
	}
}
