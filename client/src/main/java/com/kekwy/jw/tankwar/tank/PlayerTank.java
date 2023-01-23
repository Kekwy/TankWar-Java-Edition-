package com.kekwy.jw.tankwar.tank;

import com.kekwy.jw.tankwar.GameScene;
import com.kekwy.jw.tankwar.gamescenes.LocalPlayScene;
import com.kekwy.jw.tankwar.util.Direction;
import com.kekwy.tankwar.io.actions.GameAction;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.LinkedList;
import java.util.List;


public class PlayerTank extends Tank {

//	Thread waiting = null;

	@Override
	public void refresh(GraphicsContext g, long timestamp) {
		super.refresh(g, timestamp);
		g.drawImage(tankImg[getDirection().ordinal()], transform.getX() - getRadius(), transform.getY() - getRadius(),
				2 * getRadius(), 2 * getRadius());
	}

	public static final int DEFAULT_PLAYER_TANK_SPEED = 3;

	public PlayerTank(GameScene parent, double x, double y, Direction direction, String name, int group) {
		super(parent, x, y, direction, name, group);
		setSpeed(DEFAULT_PLAYER_TANK_SPEED);
	}

	public PlayerTank(GameScene parent, double x, double y, Direction direction, String name, int group, String identity) {
		super(parent, x, y, direction, name, group, identity);
		setSpeed(DEFAULT_PLAYER_TANK_SPEED);
	}

	private static final Image[] tankImg;

	static {
		tankImg = new Image[4];
		tankImg[0] = new Image("/p1tankU.gif");
		tankImg[1] = new Image("/p1tankD.gif");
		tankImg[2] = new Image("/p1tankL.gif");
		tankImg[3] = new Image("/p1tankR.gif");
	}

//	@Override
//	protected void destroy() {
//		super.destroy();
//		this.getParent().removeEventHandler(KeyEvent.KEY_PRESSED, this.getParent().getOnKeyPressed());
//		this.getParent().removeEventHandler(KeyEvent.KEY_RELEASED, this.getParent().getOnKeyReleased());
//	}


	@Override
	protected void check(long timestamp) {
		if (getState() == State.STATE_DIE) {
			new Thread(() -> {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
				((LocalPlayScene) getParent()).gameOver();
			}).start();
		}
	}

	transient List<KeyCode> keyStack = new LinkedList<>();

	public void recoveryFromDisk() {
		keyStack = new LinkedList<>();
	}

	boolean isFired = false;

	public static final int MOVE_ACTION = 1;
	public static final int FIRE_ACTION = 2;

	public int keyPressedHandle(KeyEvent keyEvent) {
		int action = 0;
		if (getState().equals(State.STATE_DIE))
			return action;
		KeyCode keyCode = keyEvent.getCode();

		if (keyCode == KeyCode.W || keyCode == KeyCode.S
				|| keyCode == KeyCode.A || keyCode == KeyCode.D) {
			if (!keyStack.contains(keyCode)) {
				if (!getParent().isOnline())
					setMove(keyEvent.getCode());
				keyStack.add(keyCode);
				action |= MOVE_ACTION;
			}
		} else if (keyCode == KeyCode.J && !isFired) {
			if (!getParent().isOnline())
				fire();
			isFired = true;
			action |= FIRE_ACTION;
		}
		return action;
	}

	public void keyReleasedHandle(KeyEvent keyEvent) {
		if (getState().equals(State.STATE_DIE))
			return;
		KeyCode keyCode = keyEvent.getCode();
		if (keyCode == KeyCode.W || keyCode == KeyCode.S
				|| keyCode == KeyCode.A || keyCode == KeyCode.D) {
			keyStack.remove(keyCode);
		} else if (keyCode == KeyCode.J && isFired) {
			isFired = false;
		}

		if (!getParent().isOnline()) return;

		if (keyStack.isEmpty())
			setState(State.STATE_IDLE);
		else {
			setMove(keyStack.get(keyStack.size() - 1));
		}
	}

	private void setMove(KeyCode keyCode) {
		// System.out.println("你干嘛~");
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
