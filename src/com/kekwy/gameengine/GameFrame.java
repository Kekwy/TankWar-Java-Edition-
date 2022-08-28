package com.kekwy.gameengine;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;

public final class GameFrame extends Frame {

	GameScene gameScene;

	private final List<GameObject> doRender0 = new LinkedList<>();
	private final List<GameObject> doRender1 = new LinkedList<>();
	private final List<GameObject> doRender2 = new LinkedList<>();

	private BufferedImage bufImg; //


	@Override
	public void setSize(int width, int height) {
		super.setSize(width, height);
		bufImg = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
	}

	// 不能主动调用，需要通过repaint回调该方法
	@Override
	public void update(Graphics g) {
		// super.update(g);
		Graphics g1 = bufImg.getGraphics();

		try {

			mutex_doRender.acquire();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		for (int i = 0; i < doRender0.size(); i++) {
			GameObject gameObject = doRender0.get(i);
			if(gameObject.isActive()) {
				gameObject.render(g1);
			}
			else {
				doRender0.remove(i);
				i--;
				gameObject.setDestroyed(GameObject.RELOAD_render);
			}
		}

		for (int i = 0; i < doRender1.size(); i++) {
			GameObject gameObject = doRender1.get(i);
			if(gameObject.isActive()) {
				gameObject.render(g1);
			}
			else {
				doRender1.remove(i);
				i--;
				gameObject.setDestroyed(GameObject.RELOAD_render);
			}
		}

		for (int i = 0; i < doRender2.size(); i++) {
			GameObject gameObject = doRender2.get(i);
			if(gameObject.isActive()) {
				gameObject.render(g1);
			}
			else {
				doRender2.remove(i);
				i--;
				gameObject.setDestroyed(GameObject.RELOAD_render);
			}
		}

		mutex_doRender.release();

		g.drawImage(bufImg, 0, 0, null);
	}

	/**
	 * 游戏开始时遍历一次进行事件的注册
	 */

	private final List<GameObject> doKeyPressedEvent = new ArrayList<>();
	private final List<GameObject> doKeyReleasedEvent = new ArrayList<>();


	public void addKeyPressedEvent(GameObject gameObject) {
		try {
			mutex_doKeyPressedEvent.acquire();
		} catch (InterruptedException ex) {
			throw new RuntimeException(ex);
		}
		doKeyPressedEvent.add(gameObject);
		mutex_doKeyPressedEvent.release();
	}

	public void addKeyReleasedEvent(GameObject gameObject) {
		try {
			mutex_doKeyReleasedEvent.acquire();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		doKeyReleasedEvent.add(gameObject);
		mutex_doKeyReleasedEvent.release();
	}

	public void addRender(GameObject gameObject) {
		try {
			mutex_doRender.acquire();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		switch (gameObject.getLayer()) {
			case 0 -> doRender0.add(gameObject);
			case 1 -> doRender1.add(gameObject);
			case 2 -> doRender2.add(gameObject);
		}
		mutex_doRender.release();
	}

	public void reset() {

		try {
			mutex_doKeyPressedEvent.acquire();
			doKeyPressedEvent.clear();
			mutex_doKeyPressedEvent.release();


			mutex_doKeyReleasedEvent.acquire();
			doKeyReleasedEvent.clear();
			mutex_doKeyReleasedEvent.release();


			mutex_doRender.acquire();
			doRender0.clear();
			doRender1.clear();
			doRender2.clear();
			mutex_doRender.release();

		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

	}

	/**
	 * 线程安全
	 */
	Semaphore mutex_doKeyPressedEvent = new Semaphore(1);
	Semaphore mutex_doKeyReleasedEvent = new Semaphore(1);
	Semaphore mutex_doRender = new Semaphore(1);

	public GameFrame() {
		initListener();
	}


	public GameScene getGameScene() {


		return gameScene;
	}

	public void setGameScene(GameScene gameScene) {
		this.gameScene = gameScene;
	}


	public void initListener() {

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				gameScene.setInactive();
				// gameScene.setNextScene(-1);
				reset();
				setVisible(false);
				gameScene = null;
			}
		});

		addKeyListener(new KeyAdapter() {

			/**
			 * 按下按键被调用
			 */
			@Override
			public void keyPressed(KeyEvent e) {
				// 被按下的键的键码
				int keyCode = e.getKeyCode();

				try {
					mutex_doKeyPressedEvent.acquire();
				} catch (InterruptedException ex) {
					throw new RuntimeException(ex);
				}
				for (int i = 0; i < doKeyPressedEvent.size(); i++) {
					GameObject gameObject = doKeyPressedEvent.get(i);
					if(gameObject.isActive())
						gameObject.keyPressedEvent(keyCode);
					else {
						doKeyPressedEvent.remove(i);
						i--;
						gameObject.setDestroyed(GameObject.RELOAD_keyPressedEvent);
					}
				}
				mutex_doKeyPressedEvent.release();
			}

			/**
			 * 抬起按键被调用
			 */
			@Override
			public void keyReleased(KeyEvent e) {
				// 被按下的键的键码
				int keyCode = e.getKeyCode();

				try {
					mutex_doKeyReleasedEvent.acquire();
				} catch (InterruptedException ex) {
					throw new RuntimeException(ex);
				}
				for (int i = 0; i < doKeyReleasedEvent.size(); i++) {
					GameObject gameObject = doKeyReleasedEvent.get(i);
					if(gameObject.isActive())
						gameObject.keyReleasedEvent(keyCode);
					else {
						doKeyReleasedEvent.remove(i);
						i--;
						gameObject.setDestroyed(GameObject.RELOAD_keyReleasedEvent);
					}
				}
				mutex_doKeyReleasedEvent.release();
			}
		});
	}
}
