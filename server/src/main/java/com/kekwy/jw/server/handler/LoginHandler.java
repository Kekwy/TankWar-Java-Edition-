package com.kekwy.jw.server.handler;

import com.kekwy.jw.server.GameServer;
import com.kekwy.tankwar.server.io.*;

import java.nio.channels.SocketChannel;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;
import java.util.logging.Logger;

public class LoginHandler implements Handler {

	private static final String SQL_QUERY = "SELECT uuid, passwd FROM players WHERE name=";

	private final Statement statement;
	private final Logger logger;
	GameServer server;

	public LoginHandler(Statement statement, Logger logger, GameServer server) {
		this.statement = statement;
		this.logger = logger;
		this.server = server;
	}

	@Override
	public void handle(Protocol protocol, SocketChannel channel) {
		if (!(protocol instanceof LoginProtocol loginProtocol)) {
			logger.severe("[SEVERE] 处理用户请求的过程中调用了错误的方法或用户请求参数错误！");
			throw new RuntimeException();
		}

		try {
			String name = loginProtocol.getName();
			String passwd = loginProtocol.getPasswd();
			ResultSet rs = statement.executeQuery(SQL_QUERY + "'" + name + "'");

			if (rs.next()) {
				if (!Objects.equals(rs.getString("passwd"), passwd)) {
					server.send(channel, new LoginFailed());
					logger.info("[INFO] 用户登入[name=%s, passwd=%s]，密码错误".formatted(name, passwd));
				} else {
					LoginSuccess success = new LoginSuccess();
					String uuid = rs.getString("uuid");
					success.setUuid(uuid);
					server.send(channel,success);
					logger.info("[INFO] 用户登入[name=%s, passwd=%s]，登录成功[uuid=%s]".formatted(name, passwd, uuid));
				}
			} else {
				server.send(channel, new LoginFailed());
				logger.info("[INFO] 用户登入[name=%s, passwd=%s]，未知用户名".formatted(name, passwd));
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
