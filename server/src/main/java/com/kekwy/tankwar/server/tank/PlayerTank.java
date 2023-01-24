package com.kekwy.tankwar.server.tank;

import com.kekwy.tankwar.util.Direction;
import com.kekwy.tankwar.server.GameScene;

public class PlayerTank extends Tank {

	public static final int DEFAULT_PLAYER_TANK_SPEED = 6;

	public PlayerTank(GameScene parent, String uuid, int x, int y, Direction direction, String name, int team) {
		super(parent, x, y, direction, name, team);
		setSpeed(DEFAULT_PLAYER_TANK_SPEED);
		setUuid(uuid);
	}

}
