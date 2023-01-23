package com.kekwy.jw.tankwar.handler;

import com.kekwy.jw.tankwar.tank.PlayerTank;
import com.kekwy.jw.tankwar.tank.Tank;
import com.kekwy.jw.tankwar.util.Direction;
import com.kekwy.jw.tankwar.gamescenes.OnlinePlayScene;
import com.kekwy.tankwar.io.actions.GameAction;

public class NewPlayerTankHandler implements Handler {

	private final OnlinePlayScene scene;

	public NewPlayerTankHandler(OnlinePlayScene gameScene) {
		this.scene = gameScene;
	}

	@Override
	public void handle(GameAction protocol) {
//		if (!(protocol instanceof NewPlayerTank p)) {
//			return;
//		}
//		Tank tank = new PlayerTank(scene, p.x, p.y, Direction.values()[p.direction], p.name, p.group);
//		tank.setColor(p.r, p.g, p.b);
//		scene.addGameObject(p.uuid, tank);
	}
}
