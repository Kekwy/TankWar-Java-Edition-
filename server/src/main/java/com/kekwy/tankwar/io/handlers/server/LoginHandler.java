package com.kekwy.tankwar.io.handlers.server;

import com.kekwy.jw.server.game.GameScene;
import com.kekwy.tankwar.io.actions.GameAction;
import com.kekwy.tankwar.io.actions.LoginAction;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;
import java.util.logging.Logger;

public class LoginHandler implements GameHandler {

	private static final String SQL_QUERY = "SELECT uuid, passwd FROM players WHERE name=";

	private final Statement statement;

	public LoginHandler(Statement statement) {
		this.statement = statement;
	}

	@Override
	public void handleAction(GameScene scene, GameAction action, SocketChannel channel, ByteBuffer buffer, Logger logger) {
		if (!(action instanceof LoginAction loginAction)) {
			logger.severe("[SEVERE] 处理用户请求的过程中调用了错误的方法或用户请求参数错误！");
			throw new RuntimeException();
		}

		try {
			String name = loginAction.name;
			String passwd = loginAction.passwd;
			ResultSet rs = statement.executeQuery(SQL_QUERY + "'" + name + "'");

			if (rs.next()) {
				if (!Objects.equals(rs.getString("passwd"), passwd)) {
					loginAction.stateCode = 1; // 登录失败
					loginAction.send(channel, ByteBuffer.allocate(1024));
					logger.info("[INFO] 用户登入[name=%s, passwd=%s]，密码错误".formatted(name, passwd));
				} else {
					String uuid = rs.getString("uuid");
					loginAction.stateCode = 0;
					loginAction.userUuid = uuid;
					loginAction.send(channel, ByteBuffer.allocate(1024));
					logger.info("[INFO] 用户登入[name=%s, passwd=%s]，登录成功[uuid=%s]".formatted(name, passwd, uuid));
				}
			} else {
				loginAction.stateCode = 1; // 登录失败
				loginAction.send(channel, ByteBuffer.allocate(1024));
				logger.info("[INFO] 用户登入[name=%s, passwd=%s]，未知用户名".formatted(name, passwd));
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
