package com.kekwy.tankwar.tank;

import com.kekwy.gameengine.GameScene;

import com.kekwy.tankwar.util.Direction;

import com.kekwy.tankwar.util.TankWarUtil;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.LinkedList;
import java.util.List;

import static com.kekwy.tankwar.tank.Tank.State.*;
import static com.kekwy.tankwar.util.Direction.*;

public class PlayerTank extends Tank {

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
		if (getState().equals(STATE_DIE))
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
		if (getState().equals(STATE_DIE))
			return;

		if (keyCode == KeyEvent.VK_W || keyCode == KeyEvent.VK_S
				|| keyCode == KeyEvent.VK_A || keyCode == KeyEvent.VK_D) {
			if (keyStack.contains(keyCode))
				keyStack.remove(Integer.valueOf(keyCode));
		} else if (keyCode == KeyEvent.VK_J && isFired) {
			isFired = false;
		}

		if (keyStack.isEmpty())
			setState(STATE_IDLE);
		else
			setMove(keyStack.get(keyStack.size() - 1));
	}

	private void setMove(int keyCode) {

		switch (keyCode) {
			case KeyEvent.VK_W -> {
				setForward(DIR_UP);
				setState(STATE_MOVE);
			}
			case KeyEvent.VK_S -> {
				setForward(DIR_DOWN);
				setState(STATE_MOVE);
			}
			case KeyEvent.VK_A -> {
				setForward(DIR_LEFT);
				setState(STATE_MOVE);
			}
			case KeyEvent.VK_D -> {
				setForward(DIR_RIGHT);
				setState(STATE_MOVE);
			}
			// case KeyEvent.VK_J -> fire();

		}
	}

}
