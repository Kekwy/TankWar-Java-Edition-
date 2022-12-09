package com.kekwy.jw.tankwar.tank;

import com.kekwy.jw.gameengine.GameScene;
import com.kekwy.jw.tankwar.gamescenes.PlayScene;
import com.kekwy.jw.tankwar.util.Direction;
import com.kekwy.jw.tankwar.util.TankWarUtil;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.LinkedList;
import java.util.List;


public class PlayerTank extends Tank {

	Thread waiting = null;
	@Override
	public void fixedUpdate() {
		super.fixedUpdate();
		if (getState() == State.STATE_DIE) {
			if(waiting == null) {
				waiting = new Thread(() -> {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						throw new RuntimeException(e);
					}
					((PlayScene) getParent()).gameOver();
				});
				waiting.start();
			}
			// setActive(false);
		}

	}

	public static final int DEFAULT_PLAYER_TANK_SPEED = 3;

	public PlayerTank(GameScene parent, int x, int y, Direction forward, String name) {
		super(parent, x, y, forward, name);
		setSpeed(DEFAULT_PLAYER_TANK_SPEED);
		parent.addGameObject(this);
		setActive(true);
	}

	private static final Image[] tankImg;

	// URL url = this.class.getResource("/white.jpg");

	static {
		tankImg = new Image[4];
		tankImg[0] = TankWarUtil.createImage("/p1tankU.gif");
		tankImg[1] = TankWarUtil.createImage("/p1tankD.gif");
		tankImg[2] = TankWarUtil.createImage("/p1tankL.gif");
		tankImg[3] = TankWarUtil.createImage("/p1tankR.gif");
	}


	@Override
	public void render(Graphics g) {
		super.render(g);
		g.drawImage(tankImg[getForward().ordinal()], position.getX() - getRadius(), position.getY() - getRadius(),
				2 * getRadius(), 2 * getRadius(), null);
	}

	List<Integer> keyStack = new LinkedList<>();

	boolean isFired = false;

	@Override
	public void keyPressedEvent(int keyCode) {
		if (getState().equals(State.STATE_DIE))
			return;

		setMove(keyCode);

		if (keyCode == KeyEvent.VK_W || keyCode == KeyEvent.VK_S
				|| keyCode == KeyEvent.VK_A || keyCode == KeyEvent.VK_D) {
			if (!keyStack.contains(keyCode))
				keyStack.add(keyCode);
		} else if (keyCode == KeyEvent.VK_J && !isFired) {
			fire();
			isFired = true;
		}
	}

	@Override
	public void keyReleasedEvent(int keyCode) {
		if (getState().equals(State.STATE_DIE))
			return;

		if (keyCode == KeyEvent.VK_W || keyCode == KeyEvent.VK_S
				|| keyCode == KeyEvent.VK_A || keyCode == KeyEvent.VK_D) {
			if (keyStack.contains(keyCode))
				keyStack.remove(Integer.valueOf(keyCode));
		} else if (keyCode == KeyEvent.VK_J && isFired) {
			isFired = false;
		}

		if (keyStack.isEmpty())
			setState(State.STATE_IDLE);
		else
			setMove(keyStack.get(keyStack.size() - 1));
	}

	private void setMove(int keyCode) {

		switch (keyCode) {
			case KeyEvent.VK_W -> {
				setForward(Direction.DIR_UP);
				setState(State.STATE_MOVE);
			}
			case KeyEvent.VK_S -> {
				setForward(Direction.DIR_DOWN);
				setState(State.STATE_MOVE);
			}
			case KeyEvent.VK_A -> {
				setForward(Direction.DIR_LEFT);
				setState(State.STATE_MOVE);
			}
			case KeyEvent.VK_D -> {
				setForward(Direction.DIR_RIGHT);
				setState(State.STATE_MOVE);
			}
			// case KeyEvent.VK_J -> fire();

		}
	}

}
