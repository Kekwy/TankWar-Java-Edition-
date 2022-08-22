package com.kekwy.gameengine;

import java.awt.*;
import java.util.List;
import java.util.Map;

public abstract class GameScene implements Runnable {


	@Override
	public void run() {

	}

	/**
	 * 当前游戏场景被开启后经过的时间
	 */
	private static long gameTime;

	/**
	 * 场景所在的窗口
	 */
	private GameFrame gameFrame;

	/**
	 * runtime
	 */
	private static List<GameObject> doUpdate;
	private static List<GameObject> doFixedUpdate;
	private static List<GameObject> doCollide;
	private static List<GameObject> doRender;
	private static List<GameObject> doGameEvent;

	private static Map<Class<? extends GameObject>, Integer> runTimeClassList;


	public GameScene(GameFrame gameFrame) {
		this.gameFrame = gameFrame;
	}

	public GameFrame getGameFrame() {
		return gameFrame;
	}

	public void setGameFrame(GameFrame gameFrame) {
		this.gameFrame = gameFrame;
	}

	/**
	 * 场景窗口的宽高
	 */
	private int frameWidth, frameHeight;

	/**
	 * 设置场景窗口标题
	 * @param title 窗口标题
	 */
	protected void setTitle(String title) {
		gameFrame.setTitle(title);
	}

	/**
	 * 设置场景窗口大小
	 * @param width 窗口宽
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
	 *
	 * @param x 窗口在屏幕上的x坐标
	 * @param y 窗口在屏幕上的y坐标
	 */
	protected void setLocation(int x, int y) {
		gameFrame.setLocation(x, y);
	}

	/**
	 * 设置场景活动状态
	 * @param b true表示场景处于活动状态
	 */
	protected void setActive(boolean b) {
		gameFrame.setVisible(b);
	}

	/**
	 * 设置窗口大小是否可变
	 * @param b true表示窗口大小可变
	 */
	protected void setResizable(boolean b) {
		gameFrame.setResizable(b);
	}

}
