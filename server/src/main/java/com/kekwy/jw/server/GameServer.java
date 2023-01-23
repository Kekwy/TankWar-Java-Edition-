package com.kekwy.jw.server;

import com.kekwy.jw.server.game.GameObject;
import com.kekwy.jw.server.game.GameScene;
import com.kekwy.tankwar.io.actions.JoinGameAction;
import com.kekwy.tankwar.io.actions.LoginAction;
import com.kekwy.tankwar.io.actions.GameAction;
import com.kekwy.tankwar.io.handlers.server.GameHandler;
import com.kekwy.tankwar.io.handlers.server.JoinGameHandler;
import com.kekwy.tankwar.io.handlers.server.LoginHandler;

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

	final Map<Class<? extends GameAction>, GameHandler> HANDLER_MAP = new HashMap<>();


	private boolean active = false;

	private final Selector selector;
	private final ServerSocketChannel serverChannel;

	private final GameScene scene = new GameScene(960, 560);


//	Handler keyEventHandler = (protocol, channel) -> {
//		logger.info("[INFO] 用户键盘事件");
//		if ((!(protocol instanceof KeyEvent p))) return;
//		GameObject object = scene.findObject(p.uuid);
//		object.recvPackage(p);
//	};



	public GameServer(String host, int port) throws IOException {

		try {
			connection = DriverManager.getConnection(DB_URL, USER_NAME, USER_PASSWD);
			statement = connection.createStatement();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		HANDLER_MAP.put(LoginAction.class, new LoginHandler(statement));
		HANDLER_MAP.put(JoinGameAction.class, new JoinGameHandler());
//		HANDLER_MAP.put(KeyEvent.class, keyEventHandler);

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
		scene.start();
//		forwardThread = new Thread(this::forward);
//		forwardThread.start();
	}

	public void run() {
		try {
			while (selector.select() > 0 && active) {
				Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
				while (iterator.hasNext()) {
					SelectionKey selKey = iterator.next();
					iterator.remove();      // 删除已经处理的事件
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

	ByteBuffer buffer = ByteBuffer.allocate(1024);

	private void handle(SelectionKey selKey) {
		SocketChannel channel = (SocketChannel) selKey.channel();
		try {
//			GameAction p = recv(channel);
			GameAction p = GameAction.getInstance(channel, buffer);
			HANDLER_MAP.get(p.getClass()).handleAction(scene, p, channel, buffer, logger);
		} catch (IOException e) {
			try {
				SocketAddress remoteAddr = channel.socket().getRemoteSocketAddress();
				logger.info("[INFO] 客户端离线：" + remoteAddr);
				selKey.cancel();
				channel.close();
			} catch (IOException ex) {
				throw new RuntimeException(ex);
			}
//			throw new RuntimeException(e);
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

	public void forward(GameAction p) {
		try {
			ByteArrayOutputStream bAos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bAos);
			oos.writeObject(p);
			int length = bAos.toByteArray().length;
			ByteBuffer head = ByteBuffer.allocate(4);
			head.putInt(length);
			head.flip();
			ByteBuffer body = ByteBuffer.wrap(bAos.toByteArray());
			bAos.close();
			oos.close();

			for (SelectionKey key : selector.keys()) {
				if (key.channel() instanceof ServerSocketChannel) continue;
				SocketChannel channel = (SocketChannel) key.channel();
				synchronized (channel) {
//					System.out.println("hwsjfhsdkl");
					channel.write(head);
					channel.write(body);
				}
			}

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

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
