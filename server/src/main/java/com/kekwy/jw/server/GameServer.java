package com.kekwy.jw.server;

import com.kekwy.jw.server.handler.Handler;
import com.kekwy.jw.server.handler.LoginHandler;
import com.kekwy.tankwar.server.io.LoginProtocol;
import com.kekwy.tankwar.server.io.Package;
import com.kekwy.tankwar.server.io.Protocol;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

public class GameServer {

	@SuppressWarnings("SpellCheckingInspection")
	static final String DB_URL = "jdbc:mysql://keekkewy.xicp.net:59203/tank_war";
	static final String USER_NAME = "tankwar";
	@SuppressWarnings("SpellCheckingInspection")
	static final String USER_PASSWD = "tBcuqeJUJkj59Lu";
	private final Statement statement;

	private final Logger logger = Logger.getLogger(this.getClass().toString());

	final Map<Integer, Handler> HANDLER_MAP = new HashMap<>();


	private boolean active = false;

	private final Selector selector;
	private final ServerSocketChannel serverChannel;

	public GameServer(String host, int port) throws IOException {

		try {
			statement = DriverManager.getConnection(DB_URL, USER_NAME, USER_PASSWD).createStatement();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}


		HANDLER_MAP.put(Protocol.NUMBER_LOGIN, new LoginHandler(statement, logger));


		selector = Selector.open();
		serverChannel = ServerSocketChannel.open();
		serverChannel.bind(new InetSocketAddress(host, port));
		serverChannel.configureBlocking(false);
		serverChannel.register(selector, SelectionKey.OP_ACCEPT);

		String logPath = "./logs/" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ".log";
//		System.out.println(logPath);
//		File file = new File(logPath);
//		if (!file.exists() && !file.createNewFile()) {
//			throw new IOException("日志文件创建失败");
//		}
		FileHandler fileHandler = new FileHandler(logPath, true);
		logger.addHandler(fileHandler);
	}

	private Thread workThread;

	public void launch() {
		active = true;
		workThread = new Thread(this::run);
		workThread.start();
	}

	public void run() {
		try {
			while ((selector.select() > 0 && active)) {
				Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
				while (iterator.hasNext()) {
					SelectionKey selKey = iterator.next();
					iterator.remove();      // 删除已经相应的事件
					if (selKey.isAcceptable()) {
						accept(selKey);
					} else if (selKey.isReadable()) {
						handle(selKey);
					}
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void handle(SelectionKey selKey) {
		SocketChannel channel = (SocketChannel) selKey.channel();
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		try {
			long num = channel.read(buffer);
			buffer.flip();
			ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(buffer.array()));
			Package p = (Package) ois.readObject();
			HANDLER_MAP.get(p.getNumber()).handle(p.getPayload(), channel);
//			byte[] bytes = new byte[4];
//			buffer.flip();
//			buffer.get(bytes, 0, 4);
//			int number
		} catch (IOException e) {
			try {
				SocketAddress remoteAddr = channel.socket().getRemoteSocketAddress();
				logger.info("[INFO] 客户端离线：" + remoteAddr);
				selKey.cancel();
				channel.close();
			} catch (IOException ex) {
				throw new RuntimeException(ex);
			}
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	private void accept(SelectionKey selKey) throws IOException {
		SocketChannel channel = ((ServerSocketChannel) selKey.channel()).accept();
		channel.configureBlocking(false);
		channel.register(selector, SelectionKey.OP_READ);

		SocketAddress remoteAddr = channel.socket().getRemoteSocketAddress();
		logger.info("[INFO] 新客户端接入：" + remoteAddr);
	}

	private void shutdown() {
		try {
			active = false;
			workThread.join();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		try {
			selector.close();
			serverChannel.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		try {
			statement.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

}
