package com.kekwy.gameengine;

import com.kekwy.tankwar.util.TankWarUtil;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.Semaphore;

public abstract class GameScene {


	public boolean isActive() {
		return active;
	}

	boolean active;


	private void render() {
		while (active) {
			System.out.println("render");
			gameFrame.repaint();


			for (int i = 0; i < doUpdate.size(); i++) {
				try {
					mutex_doUpdate.acquire();
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
				GameObject gameObject = doUpdate.get(i);
				mutex_doUpdate.release();

				if (gameObject.isActive()) {
					gameObject.update();
				} else {
					try {
						mutex_doUpdate.acquire();
					} catch (InterruptedException e) {
						throw new RuntimeException(e);
					}
					doUpdate.remove(i);
					mutex_doUpdate.release();
					i--;
					gameObject.setDestroyed(GameObject.RELOAD_update);
				}
			}


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

			System.out.println("fixUpdate");

			for (int i = 0; i < doFixedUpdate.size(); i++) {
				try {
					mutex_doFixedUpdate.acquire();
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
				GameObject gameObject = doFixedUpdate.get(i);
				mutex_doFixedUpdate.release();
				if (gameObject.isActive()) {
					gameObject.fixedUpdate();
				} else {
					try {
						mutex_doFixedUpdate.acquire();
					} catch (InterruptedException e) {
						throw new RuntimeException(e);
					}
					doFixedUpdate.remove(i);
					mutex_doFixedUpdate.release();
					i--;
					gameObject.setDestroyed(GameObject.RELOAD_fixUpdate);
				}
			}


			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
	}


	List<GameObject> colliders = new LinkedList<>();


	private boolean rectRectCollider(int x1, int y1, int radius1, int x2, int y2, int radius2) {
		return false;
	}

	private boolean rectCircleCollider(int x1, int y1, int radius1, int x2, int y2, int radius2) {
		return TankWarUtil.isCollide(x1, y1, radius1, x2 + radius2, y2)
				|| TankWarUtil.isCollide(x1, y1, radius1, x2 - radius2, y2)
				|| TankWarUtil.isCollide(x1, y1, radius1, x2, y2 + radius2)
				|| TankWarUtil.isCollide(x1, y1, radius1, x2, y2 - radius2);
	}

	private void collide() {
		while (active) {


			for (int i = 0; i < doCollide.size(); i++) {
				try {
					mutex_doCollide.acquire();
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
				GameObject gameObject = doCollide.get(i);
				mutex_doCollide.release();

				if (gameObject.isActive()) {
					// 碰撞检测
					int x = gameObject.position.getX();
					int y = gameObject.position.getY();
					int radius = gameObject.getRadius();
					int block = getBlock(x, y);

					List<GameObject> gameObjects = positionMap.get(block);
					for (GameObject object : gameObjects) {
						if(object == gameObject)
							continue;
						int _x = object.position.getX();
						int _y = object.position.getY();
						int _radius = object.getRadius();
						boolean isCollide = false;
						switch (object.getColliderType()) {
							case COLLIDER_TYPE_CIRCLE -> isCollide = rectCircleCollider(x, y, radius, _x, _y, _radius);
							case COLLIDER_TYPE_RECT -> isCollide = rectRectCollider(x, y, radius, _x, _y, _radius);
						}
						if (isCollide) {
							colliders.add(object);
						}
					}
					// mutex_positionMap.release();
					if (!colliders.isEmpty()) {
						gameObject.collide(colliders);
						colliders.clear();
					}
				} else {
					try {
						mutex_doCollide.acquire();
					} catch (InterruptedException e) {
						throw new RuntimeException(e);
					}
					doCollide.remove(i);
					mutex_doCollide.release();
					i--;
					gameObject.setDestroyed(GameObject.RELOAD_collide);
				}
			}


			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public void start() {

		gameTime = 0;
		setNextScene(-1);

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
	 *
	 * @param gameObject 待添加的游戏对象
	 */
	public void addGameObject(GameObject gameObject) {

		if ((gameObject.getAttribute() & GameObject.RELOAD_keyReleasedEvent) != 0) {
			gameFrame.addKeyReleasedEvent(gameObject);
		}
		if ((gameObject.getAttribute() & GameObject.RELOAD_keyPressedEvent) != 0) {
			gameFrame.addKeyPressedEvent(gameObject);
		}
		if ((gameObject.getAttribute() & GameObject.RELOAD_collide) != 0) {
			try {
				mutex_doCollide.acquire();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
			doCollide.add(gameObject);
			mutex_doCollide.release();
		}
		if ((gameObject.getAttribute() & GameObject.RELOAD_fixUpdate) != 0) {
			try {
				mutex_doFixedUpdate.acquire();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
			doFixedUpdate.add(gameObject);
			mutex_doFixedUpdate.release();
		}
		if ((gameObject.getAttribute() & GameObject.RELOAD_update) != 0) {
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

	public long currentTimeMillis() {
		return gameTime;
	}

	public int getUpBound() {
		return gameFrame.getInsets().top;
	}

	public int getDownBound() {
		return frameHeight;
	}

	public int getLeftBound() {
		return 0;
	}

	public int getRightBound() {
		return frameWidth;
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
		gameFrame.reset();
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
		blockX = width / BLOCK_WIDTH + 1;
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
		if (frameType == FrameType.FRAME_TYPE_PRIVATE)
			gameFrame.setVisible(false);
		// gameFrame.reset();
		active = false;
	}

	/**
	 * 设置窗口大小是否可变
	 *
	 * @param b true表示窗口大小可变
	 */
	protected void setResizable(boolean b) {
		gameFrame.setResizable(b);
	}


	/**
	 * Key：区块编号
	 * Value：保存该区块中所有游戏对象的列表
	 */
	Map<Integer, List<GameObject>> positionMap = new HashMap<>();
	Semaphore mutex_positionMap = new Semaphore(1);

	private static final int BLOCK_WIDTH = 20;

	private int blockX;


	private int getBlock(int x, int y) {
		return (x / BLOCK_WIDTH + 1) + blockX * (y / BLOCK_WIDTH);
	}

	public void updatePositionMap(GameObject gameObject, int old_x, int old_y, int x, int y) {

		int old_block = getBlock(old_x, old_y);
		int block = getBlock(x, y);

		if (old_block != block) {
			try {
				mutex_positionMap.acquire();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
			if (positionMap.containsKey(old_block))
				positionMap.get(old_block).remove(gameObject);
			if (!positionMap.containsKey(block))
				positionMap.put(block, new LinkedList<>());
			positionMap.get(block).add(gameObject);

			mutex_positionMap.release();
		}


	}

}
