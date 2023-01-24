package com.kekwy.tankwar.server;

import com.kekwy.tankwar.io.actions.*;
import com.kekwy.tankwar.io.handlers.server.GameHandler;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

public class ServerCore {

	private Map<Class<? extends GameAction>, GameHandler> handlerMap;

	public void setUpHandlers(Map<Class<? extends GameAction>, GameHandler> handlerMap) {
		this.handlerMap = handlerMap;
	}

	private GameScene scene;

	public void setUpGameScene(GameScene scene) {
		this.scene = scene;
	}

	private static ServerCore serverCore = null;

	private ServerCore() {
	}

	public static ServerCore getGlobalServer() {
		if (serverCore == null) serverCore = new ServerCore();
		return serverCore;
	}

	private final Logger logger = Logger.getLogger(this.getClass().toString());

	private boolean active = false;

	private Selector selector;
	private ServerSocketChannel serverChannel;

	public void open(String host, int port) throws IOException {

		selector = Selector.open();
		serverChannel = ServerSocketChannel.open();
		serverChannel.bind(new InetSocketAddress(host, port));
		serverChannel.configureBlocking(false);
		serverChannel.register(selector, SelectionKey.OP_ACCEPT);

		String logPath = "./logs/" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ".log";
		FileHandler fileHandler = new FileHandler(logPath, true);
		logger.addHandler(fileHandler);
	}

	private Thread listenThread;
	private Thread forwardThread;

	public void start() {
		active = true;
		listenThread = new Thread(this::listen);
		listenThread.start();
		forwardThread = new Thread(this::forward);
		forwardThread.start();
		logger.info("[SERVER] done!");

	}

	public void stop() {
		active = false;
		selector.wakeup();
		try {
			listenThread.join();
			forwardThread.join();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		logger.info("[SERVER] 服务器挂起");
	}

	public void listen() {
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
			close();
			throw new RuntimeException(e);
		}
	}

	final List<GameAction> forwardActionList = new ArrayList<>();

	static final long FORWARD_INTERVAL = 20;

	private void forward() {

		while (active) {
			long lastForwardTimestamp = System.currentTimeMillis();

			forwardActionList.clear();
			scene.getActionList(forwardActionList);

			for (GameAction action : forwardActionList) {
				for (SelectionKey key : selector.keys()) {
					if (key.channel() instanceof ServerSocketChannel) continue;
					action.send((SocketChannel) key.channel(), forwardBuffer);
				}
			}

			long timestamp = System.currentTimeMillis();

			if (timestamp - lastForwardTimestamp < FORWARD_INTERVAL) {
				try {
					//noinspection BusyWait
					Thread.sleep(FORWARD_INTERVAL - (timestamp - lastForwardTimestamp));
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		}

	}


	ByteBuffer listenBuffer = ByteBuffer.allocate(1024);
	ByteBuffer forwardBuffer = ByteBuffer.allocate(1024);

	private void handle(SelectionKey selKey) {
		SocketChannel channel = (SocketChannel) selKey.channel();
		try {
			GameAction p = GameAction.getInstance(channel, listenBuffer);
			GameHandler handler = handlerMap.get(p.getClass());
			if(handler != null) {
				handler.handleAction(scene, p, channel, listenBuffer, logger);
			}
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

	final List<GameAction> listenActionList = new ArrayList<>();

	private void accept(SelectionKey selKey) throws IOException {
		SocketChannel channel = ((ServerSocketChannel) selKey.channel()).accept();

		listenActionList.clear();
		scene.getActionListAllAsNew(listenActionList);

		for (GameAction action : listenActionList) {
			action.send(channel, listenBuffer);
		}

		channel.configureBlocking(false);
		channel.register(selector, SelectionKey.OP_READ);
		SocketAddress remoteAddr = channel.socket().getRemoteSocketAddress();
		logger.info("[INFO] 新客户端接入：" + remoteAddr);



	}

	private void close() {
		try {
			active = false;
			listenThread.join();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		try {
			selector.close();
			serverChannel.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
