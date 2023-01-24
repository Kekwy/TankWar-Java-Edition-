package com.kekwy.tankwar.client.gamescenes;


import com.kekwy.tankwar.client.GameObject;
import com.kekwy.tankwar.client.GameScene;
import com.kekwy.tankwar.client.ServerCore;
import com.kekwy.tankwar.client.TankWar;
import com.kekwy.tankwar.client.tank.PlayerTank;
import com.kekwy.tankwar.client.util.ResourceUtil;
import com.kekwy.tankwar.io.actions.*;
import com.kekwy.tankwar.io.handlers.client.*;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.nio.ByteBuffer;
import java.util.*;

public class RoomScene extends GameScene {

	boolean playing = false;

	private static final String SERVER_HOST = "127.0.0.1";
	private static final int SERVER_PORT = 2727;

	private static final String GAME_TITLE = "坦克大战v2.3.1 by kekwy - 多人游戏房间";
	private static final int SCENE_WIDTH = 1400, SCENE_HEIGHT = 640;

	private final List<List<String>> playerList = new ArrayList<>() {{
		add(new LinkedList<>());
		add(new LinkedList<>());
		add(new LinkedList<>());
		add(new LinkedList<>());
	}};

	ServerCore serverCore = ServerCore.getGlobalServer();

	static final Map<Class<? extends GameAction>, GameHandler> HANDLERS = new HashMap<>() {{
		put(LoginAction.class, new LoginHandler());
		put(EnterRoomAction.class, new EnterRoomHandler());
		put(ChangeTeamAction.class, new ChangeTeamHandler());

		put(NewTankAction.class, new NewTankHandler());
		put(NewBulletAction.class, new NewBulletHandler());
		put(NewBlastAction.class, new NewBlastHandler());
		put(NewMapTileAction.class, new NewMapTileHandler());

		put(UpdateTankAction.class, new UpdateTankHandler());
		put(UpdateBulletAction.class, new UpdateBulletHandler());
		put(UpdateMapTileAction.class, new UpdateMapTileHandler());
	}};

	PlayerTank player;
	int team;

	public void setPlayer(PlayerTank player) {
		this.player = player;
	}


	ByteBuffer buffer = ByteBuffer.allocate(1024);

	public RoomScene() {
		super(SCENE_WIDTH, SCENE_HEIGHT, GAME_TITLE);
		new BackGround(this);
		new SideMenu(this);

		// 设置为在线模式
		setOnline();

		setOnKeyPressed((keyEvent -> {
			if (player != null && player.isActive()) {
				int action = player.keyPressedHandle(keyEvent);
				if ((action & PlayerTank.MOVE_ACTION) != 0) {
					new PlayerMoveAction(player.getIdentity(), player.getDirection().ordinal(),
							player.getState().ordinal()).send(serverCore.getChannel(), buffer);
				}
				if ((action & PlayerTank.FIRE_ACTION) != 0) {
					new PlayerFireAction(player.getIdentity()).send(serverCore.getChannel(), buffer);
				}
			}

			if (!playing) {

				KeyCode keyCode = keyEvent.getCode();

				switch (keyCode) {
					case DIGIT1 -> {
						if (team != 0)
							new ChangeTeamAction(uuid, TankWar.PLAYER_NAME, 0, team).
									send(serverCore.getChannel(), buffer);
					}
					case DIGIT2 -> {
						if (team != 1)
							new ChangeTeamAction(uuid, TankWar.PLAYER_NAME, 1, team).
									send(serverCore.getChannel(), buffer);
					}
					case DIGIT3 -> {
						if (team != 2)
							new ChangeTeamAction(uuid, TankWar.PLAYER_NAME, 2, team).
									send(serverCore.getChannel(), buffer);
					}
					case DIGIT4 -> {
						if (team != 3)
							new ChangeTeamAction(uuid, TankWar.PLAYER_NAME, 3, team).
									send(serverCore.getChannel(), buffer);
					}
					case ENTER -> {
						playing = true;
						new GameStartAction(uuid).send(serverCore.getChannel(), buffer);
					}
				}
			}

		}));

		setOnKeyReleased((keyEvent -> {
			if (player != null && player.isActive()) {
				player.keyReleasedHandle(keyEvent);
				new PlayerMoveAction(player.getIdentity(), player.getDirection().ordinal(),
						player.getState().ordinal()).send(serverCore.getChannel(), buffer);
			}
		}));

		serverCore.open(SERVER_HOST, SERVER_PORT);
		serverCore.setUpHandlers(HANDLERS);
		serverCore.setUpGameScene(this);

	}

	@Override
	public void start() {
		super.start();
		serverCore.start();
		new LoginAction(TankWar.PLAYER_NAME, TankWar.PASSWORD).
				send(serverCore.getChannel(), ByteBuffer.allocate(1024));
	}

	public void addPlayer(String name, int team) {
		playerList.get(team).add(name);

		if (Objects.equals(name, TankWar.PLAYER_NAME))
			this.team = team;
	}

	public void changeTeam(String name, int team, int oldTeam) {
		playerList.get(oldTeam).remove(name);
		playerList.get(team).add(name);
		this.team = team;
	}

	@Override
	public void stop() {
		super.stop();
		serverCore.stop();
	}

	String uuid;

	@Override
	public void setPlayerUUid(String s) {
		uuid = s;

	}

	class SideMenu extends GameObject {

		public SideMenu(GameScene parent) {
			super(parent);
			setActive(true);
			parent.addGameObject(this);
		}

		static final int MENU_WIDTH = 200;

		private static final Font FONT = Font.loadFont(ResourceUtil.getAsPath("/Fonts/Minecraft.ttf"), 12);
		private static final Font EN_FONT = Font.loadFont(ResourceUtil.getAsPath("/Fonts/Minecraft.ttf"), 20);
		private static final Font CH_FONT = Font.loadFont(ResourceUtil.getAsPath("/Fonts/IPix.ttf"), 20);
		private static final Font NAME_FONT = Font.loadFont(ResourceUtil.getAsPath("/Fonts/Minecraft.ttf"), 16);


//		private static final

		private static final String[] teamName = new String[]{
				"红队",
				"绿队",
				"蓝队",
				"黄队"
		};

		private static final Color[] teamColor = new Color[]{
				Color.RED,
				Color.GREEN,
				Color.BLUE,
				Color.YELLOW
		};

		@Override
		public void refresh(GraphicsContext g, long timestamp) {
			// 绘制分割线
			for (int i = 0; i < SCENE_HEIGHT + 20; i += 13) {
				g.setFont(FONT);
				g.setFill(Color.WHITE);
				g.fillText("||", SCENE_WIDTH - MENU_WIDTH, i);
			}

			int lineY = 36;
			int stepLength = SCENE_HEIGHT / 20;
			// 绘制玩家列表
			for (int i = 0; i < 4; i++) {
				g.setFill(teamColor[i]);
				g.setFont(EN_FONT);
				g.fillText("No." + (i + 1), SCENE_WIDTH - MENU_WIDTH + 20, lineY);
				g.setFont(CH_FONT);
				g.fillText(teamName[i], SCENE_WIDTH - MENU_WIDTH + 80, lineY);

				g.setFont(NAME_FONT);
				g.setFill(Color.WHITE);

				for (int j = 0; j < playerList.get(i).size(); j++) {
					g.fillText(playerList.get(i).get(j),
							SCENE_WIDTH - MENU_WIDTH + 30, lineY + (j + 1) * stepLength);
				}

				lineY += stepLength * 4;

			}


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

	@Override
	public void addGameObject(GameObject gameObject) {
		super.addGameObject(gameObject);
		if (gameObject instanceof PlayerTank tank) {
			if (Objects.equals(tank.getIdentity(), uuid)) {
				player = tank;
			}
		}
	}
}
