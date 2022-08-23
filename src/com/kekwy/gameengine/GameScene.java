package com.kekwy.gameengine;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

public abstract class GameScene {


	public boolean isActive() {
		return active;
	}

	boolean active;


	private void render() {
		while (active) {

			gameFrame.repaint();
			try {
				mutex_doUpdate.acquire();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}

			for (GameObject gameObject : doUpdate) {
				gameObject.update();
			}

			mutex_doUpdate.release();

			try {
				Thread.sleep(33);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}

	}

	private void fixUpdate() {
		while (active) {
			gameTime += 20;
			try {
				mutex_doFixedUpdate.acquire();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}

			for (GameObject gameObject : doFixedUpdate) {
				gameObject.fixedUpdate();
			}

			mutex_doFixedUpdate.release();

			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private void collide() {
		while (active) {

			try {
				mutex_doCollide.acquire();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}

			for (GameObject gameObject : doCollide) {
				// TODO 碰撞检测
			}

			mutex_doCollide.release();

			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public void start() {

		gameTime = 0;
		gameEngine.setNextScene(-1);

		Thread renderThread = new Thread(this::render);
		Thread fixUpdateThread = new Thread(this::fixUpdate);
		Thread collideThread = new Thread(this::collide);

		renderThread.start();
		fixUpdateThread.start();
		collideThread.start();

		try {
			renderThread.join();
			fixUpdateThread.join();
			collideThread.join();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

	}

	/**
	 * 当前游戏场景被开启后经过的时间
	 */
	private static long gameTime;

	/**
	 * 场景所在的窗口
	 */
	private GameFrame gameFrame;
	private final GameEngine gameEngine;

	/**
	 * runtime
	 */


	private final List<GameObject> doUpdate = new LinkedList<>();
	private final List<GameObject> doFixedUpdate = new LinkedList<>();
	private final List<GameObject> doCollide = new LinkedList<>();

	Semaphore mutex_doUpdate = new Semaphore(1);
	Semaphore mutex_doFixedUpdate = new Semaphore(1);
	Semaphore mutex_doCollide = new Semaphore(1);





	/**
	 * 向当前场景中添加游戏对象
	 * @param gameObject 待添加的游戏对象
	 */
	public void addGameObject(GameObject gameObject) {

		if((gameObject.getAttribute() & GameObject.RELOAD_keyReleasedEvent) != 0) {
			gameFrame.addKeyReleasedEvent(gameObject);
		}
		if((gameObject.getAttribute() & GameObject.RELOAD_keyPressedEvent) != 0) {
			gameFrame.addKeyPressedEvent(gameObject);
		}
		if((gameObject.getAttribute() & GameObject.RELOAD_collide) != 0) {
			try {
				mutex_doCollide.acquire();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
			doCollide.add(gameObject);
			mutex_doCollide.release();
		}
		if((gameObject.getAttribute() & GameObject.RELOAD_fixUpdate) != 0) {
			try {
				mutex_doFixedUpdate.acquire();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
			doFixedUpdate.add(gameObject);
			mutex_doFixedUpdate.release();
		}
		if((gameObject.getAttribute() & GameObject.RELOAD_update) != 0) {
			try {
				mutex_doUpdate.acquire();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
			doUpdate.add(gameObject);
			mutex_doUpdate.release();
		}

		// System.out.println("加入render");
		gameFrame.addRender(gameObject);

	}




	public GameScene(GameFrame gameFrame, GameEngine gameEngine) {
		this.gameFrame = gameFrame;
		this.gameEngine = gameEngine;
		// gameEngine.setNextScene(-1);
	}

	public GameFrame getGameFrame() {
		return gameFrame;
	}


	protected enum FrameType {
		FRAME_TYPE_PUBLIC,
		FRAME_TYPE_PRIVATE,
	}

	private FrameType frameType;

	/**
	 * 设置场景所在的窗口
	 *
	 * @param gameFrame 搭载场景的窗口
	 */
	public void setGameFrame(GameFrame gameFrame, FrameType frameType) {
		this.gameFrame = gameFrame;
		this.frameType = frameType;
		this.gameFrame.setGameScene(this);

	}

	/**
	 * 场景窗口的宽高
	 */
	private int frameWidth, frameHeight;

	/**
	 * 设置场景窗口标题
	 *
	 * @param title 窗口标题
	 */
	protected void setTitle(String title) {
		gameFrame.setTitle(title);
	}

	/**
	 * 设置场景窗口大小
	 *
	 * @param width  窗口宽
	 * @param height 窗口高
	 */
	protected void setSize(int width, int height) {
		frameWidth = width;
		frameHeight = height;
		gameFrame.setSize(width, height);
	}

	/**
	 * 窗口在屏幕居中
	 */
	protected void setLocation() {
		gameFrame.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width - frameWidth) >> 1,
				(Toolkit.getDefaultToolkit().getScreenSize().height - frameHeight) >> 1);
	}

	/**
	 * @param x 窗口在屏幕上的x坐标
	 * @param y 窗口在屏幕上的y坐标
	 */
	protected void setLocation(int x, int y) {
		gameFrame.setLocation(x, y);
	}

	/**
	 * 设置场景活动状态
	 */
	protected void setActive() {
		active = true;
		if (!gameFrame.isVisible()) {
			gameFrame.setVisible(true);
		}
	}


	public void setNextScene(int nextScene) {
		gameEngine.setNextScene(nextScene);
	}

	public void setInactive() {
		active = false;
		if (frameType == FrameType.FRAME_TYPE_PRIVATE)
			gameFrame.setVisible(false);
	}

	protected void setInactive(int nextScene) {
		gameEngine.setNextScene(nextScene);
		active = false;
		if (frameType == FrameType.FRAME_TYPE_PRIVATE)
			gameFrame.setVisible(false);
	}

	/**
	 * 设置窗口大小是否可变
	 *
	 * @param b true表示窗口大小可变
	 */
	protected void setResizable(boolean b) {
		gameFrame.setResizable(b);
	}


}
