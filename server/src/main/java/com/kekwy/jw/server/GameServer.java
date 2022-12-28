package com.kekwy.jw.server;

import com.kekwy.jw.server.game.GameObject;
import com.kekwy.jw.server.game.GameScene;
import com.kekwy.jw.server.handler.Handler;
import com.kekwy.jw.server.handler.JoinGameHandler;
import com.kekwy.jw.server.handler.LoginHandler;
import com.kekwy.tankwar.server.io.JoinGame;
import com.kekwy.tankwar.server.io.LoginProtocol;
import com.kekwy.tankwar.server.io.Package;
import com.kekwy.tankwar.server.io.Protocol;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.*;
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
	private final Connection connection;

	private final Logger logger = Logger.getLogger(this.getClass().toString());

	final Map<Class<? extends Protocol>, Handler> HANDLER_MAP = new HashMap<>();


	private boolean active = false;

	private final Selector selector;
	private final ServerSocketChannel serverChannel;

	public GameServer(String host, int port) throws IOException {

		try {
			connection = DriverManager.getConnection(DB_URL, USER_NAME, USER_PASSWD);
			statement = connection.createStatement();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		HANDLER_MAP.put(LoginProtocol.class, new LoginHandler(statement, logger, this));
		HANDLER_MAP.put(JoinGame.class, new JoinGameHandler(new GameScene(960, 560), this));

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
	private Thread forwardThread;

	public void launch() {
		active = true;
		workThread = new Thread(this::run);
		workThread.start();
//		forwardThread = new Thread(this::forward);
//		forwardThread.start();
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
			shutdown();
			throw new RuntimeException(e);
		}
	}

	private Protocol recv(SocketChannel channel) throws IOException {
		try {
			ByteBuffer head = ByteBuffer.allocate(4);
			channel.read(head);
			head.flip();
			int length = head.getInt();
			ByteBuffer body = ByteBuffer.allocate(length);
			channel.read(body);
			body.flip();
			ByteArrayInputStream bAis = new ByteArrayInputStream(body.array());
			ObjectInputStream ois = new ObjectInputStream(bAis);
			Protocol protocol = (Protocol) ois.readObject();
			bAis.close();
			ois.close();
			return protocol;
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public void send(SocketChannel channel, Protocol protocol) {
		try {
			ByteArrayOutputStream bAos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bAos);
			oos.writeObject(protocol);
			int length = bAos.toByteArray().length;
			ByteBuffer head = ByteBuffer.allocate(4);
			head.putInt(length);
			head.flip();
			ByteBuffer body = ByteBuffer.wrap(bAos.toByteArray());
			channel.write(head);
			channel.write(body);
			bAos.close();
			oos.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void handle(SelectionKey selKey) {
		SocketChannel channel = (SocketChannel) selKey.channel();
		try {
			Protocol p = recv(channel);
			HANDLER_MAP.get(p.getClass()).handle(p, channel);
		} catch (IOException e) {
			try {
				SocketAddress remoteAddr = channel.socket().getRemoteSocketAddress();
				logger.info("[INFO] 客户端离线：" + remoteAddr);
				selKey.cancel();
				channel.close();
			} catch (IOException ex) {
				throw new RuntimeException(ex);
			}
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
//			forwardThread.join();
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
			connection.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	private final List<GameObject> objectList = new LinkedList<>();

//	@SuppressWarnings("BusyWait")
//	private void forward() {
//		while(active) {
//
//			for (SelectionKey key : selector.keys()) {
//				if (!(key.channel() instanceof SocketChannel channel)) continue;
//
//			}
//
//			try {
//				Thread.sleep(20);
//			} catch (InterruptedException e) {
//				throw new RuntimeException(e);
//			}
//		}
//	}

}
