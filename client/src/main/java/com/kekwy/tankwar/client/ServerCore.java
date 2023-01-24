package com.kekwy.tankwar.client;

import com.kekwy.tankwar.io.actions.GameAction;
import com.kekwy.tankwar.io.handlers.client.GameHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Map;

public class ServerCore {

	private ServerCore() {}

	private static ServerCore serverCore = null;

	public static ServerCore getGlobalServer() {
		if (serverCore == null) serverCore = new ServerCore();
		return serverCore;
	}

	boolean active = false;


	private SocketChannel channel;
	private Selector selector;


	private Map<Class<? extends GameAction>, GameHandler> handlerMap;

	private Thread listenThread;

	public void open(String host, int port) {
		try {
			channel = SocketChannel.open(new InetSocketAddress(host, port));
			channel.configureBlocking(false);
			selector = Selector.open();
			channel.register(selector, SelectionKey.OP_READ);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void setUpHandlers(Map<Class<? extends GameAction>, GameHandler> handlerMap) {
		this.handlerMap = handlerMap;
	}

	private GameScene scene;

	public void setUpGameScene(GameScene scene) {
		this.scene = scene;
	}

	public void start() {
		active = true;
		listenThread = new Thread(this::listen);
		listenThread.start();
	}

	public void stop() {
		active = false;
		selector.wakeup();
		try {
			listenThread.join();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public void close() {
		stop();
		try {
			selector.close();
			channel.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	ByteBuffer buffer = ByteBuffer.allocate(1024);

	private void listen() {
		try {
			while (selector.select() > 0 && active) {
				Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
				while (iterator.hasNext()) {
					SelectionKey key = iterator.next();
					iterator.remove();
					SocketChannel channel = (SocketChannel) key.channel();
					GameAction action = GameAction.getInstance(channel, buffer);
					GameHandler handler = handlerMap.get(action.getClass());
					if (handler != null) {
						handler.handleAction(scene, action, channel, buffer);
					}
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public SocketChannel getChannel() {
		return channel;
	}

}
