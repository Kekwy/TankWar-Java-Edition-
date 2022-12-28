package com.kekwy.jw.server.game.tank;


import com.kekwy.jw.server.game.GameScene;
import com.kekwy.jw.server.util.Direction;

public class PlayerTank extends Tank {

//	Thread waiting = null;


	public static final int DEFAULT_PLAYER_TANK_SPEED = 3;

	public PlayerTank(GameScene parent, int x, int y, Direction direction, String name) {
		super(parent, x, y, direction, name, 1);
		setSpeed(DEFAULT_PLAYER_TANK_SPEED);
		parent.addGameObject(this);
		// 按键按下时的响应
		// parent.setOnKeyPressed(KeyEvent -> System.out.println("sadsad"));
		// 按键抬起时的响应
		// parent.setOnKeyPressed(this::keyReleasedHandle);
	}


//	@Override
//	protected void destroy() {
//		super.destroy();
//		this.getParent().removeEventHandler(KeyEvent.KEY_PRESSED, this.getParent().getOnKeyPressed());
//		this.getParent().removeEventHandler(KeyEvent.KEY_RELEASED, this.getParent().getOnKeyReleased());
//	}


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


	boolean isFired = false;

//	public void keyPressedHandle(KeyEvent keyEvent) {
////		if (getState().equals(State.STATE_DIE))
////			return;
////		KeyCode keyCode = keyEvent.getCode();
////		setMove(keyEvent.getCode());
////
////		if (keyCode == KeyCode.W || keyCode == KeyCode.S
////				|| keyCode == KeyCode.A || keyCode == KeyCode.D) {
////			if (!keyStack.contains(keyCode))
////				keyStack.add(keyCode);
////		} else if (keyCode == KeyCode.J && !isFired) {
////			fire();
////			isFired = true;
////		}
//	}

//	public void keyReleasedHandle(KeyEvent keyEvent) {
////		if (getState().equals(State.STATE_DIE))
////			return;
////		KeyCode keyCode = keyEvent.getCode();
////		if (keyCode == KeyCode.W || keyCode == KeyCode.S
////				|| keyCode == KeyCode.A || keyCode == KeyCode.D) {
////			keyStack.remove(keyCode);
////		} else if (keyCode == KeyCode.J && isFired) {
////			isFired = false;
////		}
////		if (keyStack.isEmpty())
////			setState(State.STATE_IDLE);
////		else
////			setMove(keyStack.get(keyStack.size() - 1));
//	}

//	private void setMove(KeyCode keyCode) {
//		// System.out.println("你干嘛~");
////		switch (keyCode) {
////			case W -> {
////				setDirection(Direction.DIR_UP);
////				setState(State.STATE_MOVE);
////			}
////			case S -> {
////				setDirection(Direction.DIR_DOWN);
////				setState(State.STATE_MOVE);
////			}
////			case A -> {
////				setDirection(Direction.DIR_LEFT);
////				setState(State.STATE_MOVE);
////			}
////			case D -> {
////				setDirection(Direction.DIR_RIGHT);
////				setState(State.STATE_MOVE);
////			}
////		}
//	}

}
