package com.kekwy.tankwar.io.actions;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;

public abstract class GameAction {

	public static final int LOGIN_CODE = 0;
	public static final int JOIN_CODE = 1;
	public static final int NEW_OBJECT_CODE = 2;

	static final Map<Integer, Class<? extends GameAction>> ACTION_MAP = new HashMap<>();

	static {
		ACTION_MAP.put(LOGIN_CODE, LoginAction.class);
		ACTION_MAP.put(JOIN_CODE, JoinGameAction.class);
		ACTION_MAP.put(NEW_OBJECT_CODE, NewObjectAction.class);
	}

	public GameAction() {
	}

	public GameAction(SocketChannel channel, ByteBuffer buffer) {
	}

	public static GameAction getInstance(SocketChannel channel, ByteBuffer buffer) throws IOException {
		buffer.clear();
		buffer.limit(4);
		GameAction gameAction;
		try {
			// 读取游戏行为编号
			channel.read(buffer);
			buffer.flip();
			// 查询对应的行为类，通过反射机制获取其构造方法，并调用。
			gameAction = ACTION_MAP.get(buffer.getInt()).
					getDeclaredConstructor(SocketChannel.class, ByteBuffer.class).
					newInstance(channel, buffer);
		} catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
		         InvocationTargetException e) {
			throw new RuntimeException(e);
		}
		return gameAction;
	}

	public abstract void send(SocketChannel channel, ByteBuffer buffer);

}
