package com.kekwy.jw.server.handler;

import com.kekwy.jw.server.GameServer;
import com.kekwy.tankwar.server.io.*;
import com.kekwy.tankwar.server.io.Package;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
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

	public LoginHandler(Statement statement, Logger logger) {
		this.statement = statement;
		this.logger = logger;
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

			ByteArrayOutputStream bAos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bAos);
			Package p = new Package();
			if (rs.next()) {
				if (!Objects.equals(rs.getString("passwd"), passwd)) {
					p.init(Protocol.NUMBER_LOGIN_FAILED, new LoginFailedProtocol());
					logger.info("[INFO] 用户登入[name=%s, passwd=%s]，密码错误".formatted(name, passwd));
				} else {
					LoginSuccessProtocol success = new LoginSuccessProtocol();
					String uuid = rs.getString("uuid");
					success.setUuid(uuid);
					p.init(Protocol.NUMBER_LOGIN_FAILED, success);
					logger.info("[INFO] 用户登入[name=%s, passwd=%s]，登录成功[uuid=%s]".formatted(name, passwd, uuid));
				}
			} else {
				p.init(Protocol.NUMBER_LOGIN_FAILED, new LoginFailedProtocol());
				logger.info("[INFO] 用户登入[name=%s, passwd=%s]，未知用户名".formatted(name, passwd));
			}
			oos.writeObject(p);
			ByteBuffer buffer = ByteBuffer.wrap(bAos.toByteArray());
			channel.write(buffer);
			bAos.close();
			oos.close();
		} catch (SQLException | IOException e) {
			throw new RuntimeException(e);
		}
	}
}
