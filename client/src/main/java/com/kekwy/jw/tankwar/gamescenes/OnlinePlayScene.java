package com.kekwy.jw.tankwar.gamescenes;

import com.kekwy.jw.tankwar.GameObject;
import com.kekwy.jw.tankwar.GameScene;
import com.kekwy.jw.tankwar.TankWar;
import com.kekwy.jw.tankwar.effect.MusicPlayer;
import com.kekwy.jw.tankwar.handler.Handler;
import com.kekwy.jw.tankwar.handler.LoginSuccessHandler;
import com.kekwy.jw.tankwar.handler.NewPlayerTankHandler;
import com.kekwy.jw.tankwar.tank.Bullet;
import com.kekwy.tankwar.server.io.*;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class OnlinePlayScene extends GameScene {

	/**
	 * 游戏时背景音乐
	 */
	public final MusicPlayer gameBGM = new MusicPlayer();

	/**
	 * 当前场景的窗口标题
	 */
	private static final String GAME_TITLE = "坦克大战v2.0 by kekwy - 在线游戏";
	/**
	 * 当前场景的窗口大小
	 */
	private static final int SCENE_WIDTH = 960, SCENE_HEIGHT = 560;

	String uuid = null;

	private void send(Protocol protocol) {
		try {
			ByteArrayOutputStream bAos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bAos);
			oos.writeObject(protocol);
			int length = bAos.toByteArray().length;
			ByteBuffer head = ByteBuffer.allocate(4);
			head.putInt(length);
			head.flip();
			ByteBuffer body = ByteBuffer.wrap(bAos.toByteArray());
//			body.flip();
			synchronized (channel) {
				channel.write(head);
				channel.write(body);
			}
			bAos.close();
			oos.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private final Map<String, GameObject> objectMap = new HashMap<>();

	Handler frameUpdateHandler = (protocol -> {
		if (protocol instanceof FrameUpdate p) {
			GameObject object = objectMap.get(p.uuid);
			object.update(p);
		}
	});

	public OnlinePlayScene() {
		super(SCENE_WIDTH, SCENE_HEIGHT, GAME_TITLE);

		setOnKeyPressed((keyEvent -> {
			if (uuid != null) {
				send(new KeyEvent(uuid, keyEvent.getCode().ordinal(), 0));
			}
		}));

		setOnKeyReleased((keyEvent -> {
			if (uuid != null) {
				send(new KeyEvent(uuid, keyEvent.getCode().ordinal(), 1));
			}
		}));

		handlerMap.put(NewPlayerTank.class, new NewPlayerTankHandler(this));
		handlerMap.put(LoginSuccess.class, new LoginSuccessHandler(this));
		handlerMap.put(FrameUpdate.class, frameUpdateHandler);

		addGameObject(new BackGround(this));

		connectToServer();
		send(new LoginProtocol(TankWar.PLAYER_NAME, TankWar.PASSWORD));
		synchronized (this) {
			if (uuid == null) {
				try {
					this.wait();
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		}
		send(new JoinGame(uuid, TankWar.PLAYER_NAME));
	}

	private static final String SERVER_HOST = "127.0.0.1";
	private static final int SERVER_PORT = 2727;
	private SocketChannel channel;
	private Selector selector;

	private void connectToServer() {
		try {
			channel = SocketChannel.open(new InetSocketAddress(SERVER_HOST, SERVER_PORT));
			channel.configureBlocking(false);
			selector = Selector.open();
			channel.register(selector, SelectionKey.OP_READ);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		netWorkThread = new Thread(this::netWork);
		netWorkThread.start();
	}

	private Thread netWorkThread;


	private Protocol recv(SocketChannel channel) {
		try {
			ByteBuffer head = ByteBuffer.allocate(4);
			channel.read(head);
			head.flip();
			int length = head.getInt();
			ByteBuffer body = ByteBuffer.allocate(length);
			do {
				channel.read(body);
			} while (body.position() < length);
			body.flip();
			ByteArrayInputStream bAis = new ByteArrayInputStream(body.array());
			ObjectInputStream ois = new ObjectInputStream(bAis);
			Protocol protocol = (Protocol) ois.readObject();
			bAis.close();
			ois.close();
			return protocol;
		} catch (IOException | ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	private void netWork() {
		try {
			while (selector.select() > 0 && isActive()) {
				Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
				while (iterator.hasNext()) {
					SelectionKey key = iterator.next();
					iterator.remove();
					SocketChannel channel = (SocketChannel) key.channel();
					Protocol protocol = recv(channel);
					handlerMap.get(protocol.getClass()).handle(protocol);
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}



	private final Map<Class<? extends Protocol>, Handler> handlerMap = new HashMap<>();

	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

	public void addGameObject(String uuid, GameObject gameObject) {
		lock.writeLock().lock();
		objectMap.put(uuid, gameObject);
		lock.writeLock().unlock();
		super.addGameObject(gameObject);
	}

	public GameObject getGameObject(String uuid) {
		lock.readLock().lock();
		GameObject object = objectMap.get(uuid);
		lock.readLock().unlock();
		return object;
	}

	public void setUuid(String uuid) {
		synchronized (this) {
			this.uuid = uuid;
			this.notify();
		}
	}

	static class BackGround extends GameObject {

		@Override
		public void refresh(GraphicsContext g, long timestamp) {
			g.setFill(Color.BLACK);
			g.fillRect(0, 0, SCENE_WIDTH, SCENE_HEIGHT);
		}

		public BackGround(GameScene parent) {
			super(parent);
			setLayer(0);
			setActive(true);
			parent.addGameObject(this);
		}
	}

	public void update(GameObject object, double x, double y, int offset) {
		if (object instanceof Bullet) {
			if (x < 0 || x > SCENE_WIDTH || y < 0 || y > SCENE_HEIGHT) {
				object.setActive(false);
				return;
			}
		} else {
			x = Math.max(x, offset);
			x = Math.min(x, SCENE_WIDTH - offset);
			y = Math.max(y, offset);
			y = Math.min(y, SCENE_HEIGHT - offset);
		}
		super.update(object, x, y, offset);
		object.transform.setX(x);
		object.transform.setY(y);
	}

}
