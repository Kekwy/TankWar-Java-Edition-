package com.kekwy.tankwar.io.actions;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;

public abstract class GameAction {

	public static final int LOGIN_CODE = 0x00;
	public static final int JOIN_CODE = 0x01;
	public static final int PLAYER_FIRE_CODE = 0x02;
	public static final int PLAYER_MOVE_CODE = 0x03;

	public static final int NEW_Tank_CODE = 0x10;
	public static final int NEW_BULLET_CODE = 0x11;
	public static final int NEW_BLAST_CODE = 0x12;
	public static final int NEW_MAP_TILE_CODE = 0x13;


	public static final int UPDATE_TANK_CODE = 0x20;
	public static final int UPDATE_BULLET_CODE = 0x21;
	public static final int UPDATE_MAP_TILE_CODE = 0x22;

	public static final int ENTER_ROOM_CODE = 0x30;
	public static final int LEAVE_ROOM_ACTION = 0x31;
	public static final int CHANGE_TEAM_CODE = 0x32;
	public static final int GAME_START_CODE = 0x33;

	static final Map<Integer, Class<? extends GameAction>> ACTION_MAP = new HashMap<>();

	static {
		ACTION_MAP.put(LOGIN_CODE, LoginAction.class);
		ACTION_MAP.put(JOIN_CODE, JoinGameAction.class);
		ACTION_MAP.put(PLAYER_FIRE_CODE, PlayerFireAction.class);
		ACTION_MAP.put(PLAYER_MOVE_CODE, PlayerMoveAction.class);

		ACTION_MAP.put(NEW_Tank_CODE, NewTankAction.class);
		ACTION_MAP.put(NEW_BULLET_CODE, NewBulletAction.class);
		ACTION_MAP.put(NEW_BLAST_CODE, NewBlastAction.class);
		ACTION_MAP.put(NEW_MAP_TILE_CODE, NewMapTileAction.class);

		ACTION_MAP.put(UPDATE_TANK_CODE, UpdateTankAction.class);
		ACTION_MAP.put(UPDATE_BULLET_CODE, UpdateBulletAction.class);
		ACTION_MAP.put(UPDATE_MAP_TILE_CODE, UpdateMapTileAction.class);

		ACTION_MAP.put(ENTER_ROOM_CODE, EnterRoomAction.class);
		ACTION_MAP.put(CHANGE_TEAM_CODE, ChangeTeamAction.class);
		ACTION_MAP.put(GAME_START_CODE, GameStartAction.class);

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
