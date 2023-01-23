package com.kekwy.jw.tankwar.gamescenes;

import com.kekwy.jw.tankwar.effect.MusicPlayer;
import com.kekwy.jw.tankwar.handler.Handler;
import com.kekwy.jw.tankwar.tank.Bullet;
import com.kekwy.jw.tankwar.GameObject;
import com.kekwy.jw.tankwar.GameScene;
import com.kekwy.jw.tankwar.TankWar;
import com.kekwy.jw.tankwar.tank.PlayerTank;
import com.kekwy.tankwar.io.actions.*;
import com.kekwy.tankwar.io.handlers.client.GameHandler;
import com.kekwy.tankwar.io.handlers.client.LoginHandler;
import com.kekwy.tankwar.io.handlers.client.NewTankHandler;
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

	private final Map<String, GameObject> objectMap = new HashMap<>();

	Handler frameUpdateHandler = (protocol -> {
		if (protocol instanceof UpdatePositionAction p) {
			GameObject object = objectMap.get(p.uuid);
			object.update(p);
		}
	});

	ByteBuffer buffer = ByteBuffer.allocate(1024);

	PlayerTank player = null;

	public OnlinePlayScene() {
		super(SCENE_WIDTH, SCENE_HEIGHT, GAME_TITLE);
		// 设置为在线模式
		setOnline();

		setOnKeyPressed((keyEvent -> {
			if (player != null && player.isActive()) {
				int action = player.keyPressedHandle(keyEvent);
				if ((action & PlayerTank.MOVE_ACTION) != 0) {
					new PlayerMoveAction(player.getIdentity(), player.getDirection().ordinal(),
							player.getState().ordinal()).send(channel, buffer);
				}
				if ((action & PlayerTank.FIRE_ACTION) != 0) {
					new PlayerFireAction(player.getIdentity()).send(channel, buffer);
				}
			}
		}));

		setOnKeyReleased((keyEvent -> {
			if (player != null && player.isActive()) {
				player.keyReleasedHandle(keyEvent);
				new PlayerMoveAction(player.getIdentity(), player.getDirection().ordinal(),
						player.getState().ordinal()).send(channel, buffer);
			}
		}));

		handlerMap.put(NewTankAction.class, new NewTankHandler());
		handlerMap.put(LoginAction.class, new LoginHandler());
//		handlerMap.put(updateAction.class, frameUpdateHandler);

		addGameObject(new BackGround(this));

		connectToServer();
		new LoginAction(TankWar.PLAYER_NAME, TankWar.PASSWORD).send(channel, buffer);

		synchronized (this) {
			if (uuid == null) {
				try {
					this.wait();
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		}
		//
		new JoinGameAction(uuid, TankWar.PLAYER_NAME).send(channel, buffer);
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

	private void netWork() {
		try {
			while (selector.select() > 0 && isActive()) {
				Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
				while (iterator.hasNext()) {
					SelectionKey key = iterator.next();
					iterator.remove();
					SocketChannel channel = (SocketChannel) key.channel();
					GameAction action = GameAction.getInstance(channel, buffer);
					handlerMap.get(action.getClass()).handleAction(this, action, channel, buffer);
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}


	@Override
	public void setPlayerUUid(String s) {
		synchronized (this) {
			uuid = s;
			this.notify();
		}
	}

	private final Map<Class<? extends GameAction>, GameHandler> handlerMap = new HashMap<>();

	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

	@Override
	public void addGameObject(GameObject gameObject) {
		if (player == null && gameObject instanceof PlayerTank) {
			player = (PlayerTank) gameObject;
		}
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
