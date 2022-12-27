package com.kekwy.jw.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

public class GameServer {

	private boolean active = false;
	private final Logger logger = Logger.getLogger(this.getClass().toString());

	private final Selector selector;
	private final ServerSocketChannel serverChannel;

	public GameServer(String host, int port) throws IOException {
		selector = Selector.open();
		serverChannel = ServerSocketChannel.open();
		serverChannel.bind(new InetSocketAddress(host, port));
		serverChannel.configureBlocking(false);
		serverChannel.register(selector, SelectionKey.OP_ACCEPT);

		String logPath = "./logs/" + new SimpleDateFormat("yyyy-MM-dd").format(new Date())+".log";
		FileHandler fileHandler=new FileHandler(logPath,true);
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
	}

}
