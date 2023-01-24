package com.kekwy.tankwar.server.gamescenes;

import com.kekwy.tankwar.io.actions.*;
import com.kekwy.tankwar.io.handlers.server.*;
import com.kekwy.tankwar.server.GameScene;
import com.kekwy.tankwar.server.ServerCore;
import com.kekwy.tankwar.server.gamemap.GameMap;
import com.kekwy.tankwar.server.tank.Bullet;
import com.kekwy.tankwar.server.tank.EnemyTank;
import com.kekwy.tankwar.server.tank.PlayerTank;
import com.kekwy.tankwar.server.tank.Tank;
import com.kekwy.tankwar.util.Direction;
import com.kekwy.tankwar.util.RandomGen;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class RoomScene extends GameScene {

	ServerCore serverCore = ServerCore.getGlobalServer();

	@SuppressWarnings("SpellCheckingInspection")
	static final String DB_URL = "jdbc:mysql://keekkewy.xicp.net:59203/tank_war";
	static final String USER_NAME = "tankwar";
	@SuppressWarnings("SpellCheckingInspection")
	static final String USER_PASSWD = "tBcuqeJUJkj59Lu";
	private final Statement statement;
	private final Connection connection;

	private static final int SCENE_WIDTH = 1200, SCENE_HEIGHT = 640;

	static final Map<Class<? extends GameAction>, GameHandler> HANDLERS = new HashMap<>(){{
		put(LoginAction.class, null);
		put(JoinGameAction.class, new JoinGameHandler());
		put(PlayerFireAction.class, new PlayerFireHandler());
		put(PlayerMoveAction.class, new PlayerMoveHandler());
		put(ChangeTeamAction.class, new ChangeTeamHandler());
		put(GameStartAction.class, new GameStartHandler());
	}};

	public RoomScene() throws IOException {
		super(SCENE_WIDTH, SCENE_HEIGHT);
		try {
			connection = DriverManager.getConnection(DB_URL, USER_NAME, USER_PASSWD);
			statement = connection.createStatement();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		HANDLERS.put(LoginAction.class, new LoginHandler(statement));

		// 初始化服务器
		serverCore.setUpHandlers(HANDLERS);
		serverCore.setUpGameScene(this);
		serverCore.open(HOST, PORT);
	}

	@Override
	public void start() {
		super.start();
		serverCore.start();
	}

	private static final int PORT = 2727;
	private static final String HOST = "localhost";

	@Override
	public void stop() {
		super.stop();
		serverCore.stop();
		try {
			statement.close();
			connection.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	private final List<List<String>> playerList = new ArrayList<>() {{
		add(new LinkedList<>());
		add(new LinkedList<>());
		add(new LinkedList<>());
		add(new LinkedList<>());
	}};

	public boolean changeTeam(String name, int team, int oldTeam) {
		synchronized (playerList) {
			List<String> list = playerList.get(team);
			if (list.size() >= 2) return false;
			else {
				playerList.get(oldTeam).remove(name);
				list.add(name);
				return true;
			}
		}
	}

	public int playerJoin(String name, String uuid, SocketChannel channel) {

		int team = 0, minCount = playerList.get(0).size();
		synchronized (playerList) {
			for (int i = 1; i < playerList.size(); i++) {
				if (playerList.get(i).size() < minCount) {
					team = i;
					minCount = playerList.get(i).size();
				}
			}
			if (!playerList.get(team).contains(name))
				playerList.get(team).add(name);
		}
		clientMap.put(channel, uuid);
		Tank tank = new PlayerTank(this, uuid, 200, 300, Direction.DIR_UP, name, team);
		tank.setAtk(0);
		addGameObject(tank);
		return team;
	}

	@Override
	public void getActionListAllAsNew(List<GameAction> dst) {
		super.getActionListAllAsNew(dst);
		for (int i = 0; i < playerList.size(); i++) {
			List<String> players = playerList.get(i);
			for (String name : players) {
				dst.add(new EnterRoomAction(name, i));
			}
		}
	}

	@Override
	public void startGame() {
		try {
			new GameMap(new FileInputStream("./maps/gamemap.xlsx")).createGameMap(this);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}

		for (String uuid : clientMap.values()) {
			Tank tank = (Tank) findObject(uuid);
			tank.setAtk(Tank.DEFAULT_ATK);
		}
	}

}
