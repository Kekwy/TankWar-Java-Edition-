package com.kekwy.tankwar.server.tank;


import com.kekwy.tankwar.server.GameServer;
import com.kekwy.tankwar.util.Direction;
import com.kekwy.tankwar.server.GameScene;
import com.kekwy.tankwar.io.actions.GameAction;

import java.util.LinkedList;
import java.util.List;

public class PlayerTank extends Tank {

//	Thread waiting = null;


	public static final int DEFAULT_PLAYER_TANK_SPEED = 6;

	public PlayerTank(GameScene parent, GameServer server, String uuid, int x, int y, Direction direction, String name) {
		super(parent, server, x, y, direction, name, 1);
		setSpeed(DEFAULT_PLAYER_TANK_SPEED);
		// parent.addGameObject(this);
		setUuid(uuid);
//		NewPlayerTank newPlayerTank = new NewPlayerTank();
//		newPlayerTank.direction = 0;
//		newPlayerTank.r = 1;
//		newPlayerTank.g = 1;
//		newPlayerTank.b = 1;
//		newPlayerTank.x = 200;
//		newPlayerTank.y = 300;
//		newPlayerTank.group = 1;
//		newPlayerTank.name = name;
//		newPlayerTank.uuid = uuid;
//		forward(newPlayerTank);
	}

	@Override
	public void recvPackage(GameAction protocol) {
//		if (protocol instanceof KeyEvent p) {
//			if (p.isPressEvent()) {
//				keyPressedHandle(p.keyCode);
//			} else {
//				keyReleasedHandle(p.keyCode);
//			}
//		}
	}

	@Override
	protected void check(long timestamp) {
//		if (getState() == State.STATE_DIE) {
//			new Thread(()->{
//				try {
//					Thread.sleep(1000);
//				} catch (InterruptedException e) {
//					throw new RuntimeException(e);
//				}
//				// ((LocalPlayScene)getParent()).gameOver();
//			}).start();
//		}
	}

	List<Integer> keyStack = new LinkedList<>();

	boolean isFired = false;

	public void keyPressedHandle(int keyCode) {
		if (getState().equals(State.STATE_DIE))
			return;
		setMove(keyCode);

		if (keyCode == W || keyCode == S
				|| keyCode == A || keyCode == D) {
			if (!keyStack.contains(keyCode))
				keyStack.add(keyCode);
		} else if (keyCode == J && !isFired) {
			fire();
			isFired = true;
		}
	}

	static final int W = 58;
	static final int S = 54;
	static final int A = 36;
	static final int D = 39;
	static final int J = 45;

	public void keyReleasedHandle(int keyCode) {
		if (getState().equals(State.STATE_DIE))
			return;
		if (keyCode == W || keyCode == S
				|| keyCode == A || keyCode == D) {
			keyStack.remove(Integer.valueOf(keyCode));
		} else if (keyCode == J && isFired) {
			isFired = false;
		}
		if (keyStack.isEmpty())
			setState(State.STATE_IDLE);
		else
			setMove(keyStack.get(keyStack.size() - 1));
	}

	private void setMove(int keyCode) {
		switch (keyCode) {
			case W -> {
				setDirection(Direction.DIR_UP);
				setState(State.STATE_MOVE);
			}
			case S -> {
				setDirection(Direction.DIR_DOWN);
				setState(State.STATE_MOVE);
			}
			case A -> {
				setDirection(Direction.DIR_LEFT);
				setState(State.STATE_MOVE);
			}
			case D -> {
				setDirection(Direction.DIR_RIGHT);
				setState(State.STATE_MOVE);
			}
		}
	}

}
