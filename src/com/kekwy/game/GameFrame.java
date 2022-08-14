package com.kekwy.game;

// import com.kekwy.util.Constant;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

import static com.kekwy.util.Constant.*;
import static com.kekwy.util.Constant.State;

public class GameFrame extends Frame implements Runnable {

	// 双缓冲技术解决屏幕闪烁，定义一张与屏幕大小相同的“图片”
	private BufferedImage bufImg = new BufferedImage(FRAME_WIDTH, FRAME_HEIGHT, BufferedImage.TYPE_4BYTE_ABGR);
	public static State gameState;
	public static int titleBarH;
	private static int menuIndex;
	private Tank myTank;

	@Override
	public void run() {
		while(true) {
			repaint();
			try {
				Thread.sleep(FLUSH_INTERVAL);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public GameFrame() {
		initEventListener();
		initGame();
		initFrame();
		titleBarH = getInsets().top;
		new Thread(this).start();
	}

	private void initGame() {
		gameState = State.STATE_MENU;
		menuIndex = 0;
	}
	private void initFrame() {
		super.setTitle(GAME_TITLE); // 可以把常量写在配置文件中
		super.setSize(FRAME_WIDTH, FRAME_HEIGHT);
		super.setLocation(FRAME_X, FRAME_Y);
		super.setResizable(false);
		super.setVisible(true);
	}
	// 不能主动调用，需要通过repaint回调该方法
	public void update(Graphics g1) {

		Graphics g = bufImg.getGraphics();

		switch (gameState) {
			case STATE_MENU -> drawMenu(g);
			case STATE_HELP -> drawHelp(g);
			case STATE_ABOUT -> drawAbout(g);
			case STATE_RUN -> drawRun(g);
			case STATE_OVER -> drawOver(g);
		}
		// drawMenu(g);
		g1.drawImage(bufImg,0,0,null);
	}

	private void initEventListener() {
		// 注册监听事件
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) { // 按键按下调用
				// 被按下的键的键码
				int keyCode = e.getKeyCode();
				switch (gameState) {
					case STATE_MENU -> keyEventMenu(keyCode);
					case STATE_HELP -> keyEventHelp(keyCode);
					case STATE_ABOUT -> keyEventAbout(keyCode);
					case STATE_RUN -> keyEventRun(keyCode);
					case STATE_OVER -> keyEventOver(keyCode);
				}
			}
			@Override
			public void keyReleased(KeyEvent e) { // 按键松开调用
				int keyCode = e.getKeyCode();
				if(gameState == State.STATE_RUN) {
					switch (keyCode){
						case KeyEvent.VK_W:
						case KeyEvent.VK_S:
						case KeyEvent.VK_A:
						case KeyEvent.VK_D:
							myTank.setState(Tank.State.STATE_IDLE);
							break;
					}
				}
			}
		});
	}

	private void keyEventOver(int keyCode) {

	}

	private void keyEventRun(int keyCode) {
		switch (keyCode) {
			case KeyEvent.VK_W -> myTank.setForward(Tank.Direction.DIR_UP);
			case KeyEvent.VK_S -> myTank.setForward(Tank.Direction.DIR_DOWN);
			case KeyEvent.VK_A -> myTank.setForward(Tank.Direction.DIR_LEFT);
			case KeyEvent.VK_D -> myTank.setForward(Tank.Direction.DIR_RIGHT);
			default ->{ return; }
		}
		myTank.setState(Tank.State.STATE_MOVE);
	}

	private void keyEventAbout(int keyCode) {

	}

	private void keyEventHelp(int keyCode) {

	}

	private void keyEventMenu(int keyCode) {
		switch (keyCode) {
			case KeyEvent.VK_W -> menuIndex = (menuIndex + 4) % 5;
			case KeyEvent.VK_S -> menuIndex = (menuIndex + 1) % 5;
			case KeyEvent.VK_J -> {
				if (menuIndex == 0) {
					newGame();
				}
			}
		}
	}

	private void newGame() {
		gameState = State.STATE_RUN;
		myTank = new Tank(200, 400, Tank.Direction.DIR_UP);
	}

	private void drawMenu(Graphics g) {
		g.setColor(Color.BLACK);
		g.fillRect(0,0,FRAME_WIDTH,FRAME_HEIGHT);

		g.setFont(GAME_FONT);
		final int STR_WIDTH = 70;
		final int DIS = 50;
		int x = (FRAME_WIDTH - STR_WIDTH) >> 1;
		int y = FRAME_HEIGHT / 3;

		for (int i = 0; i < State.values().length; i++) {
			if(i == menuIndex) {
				g.setColor(Color.RED);
			}
			else {
				g.setColor(Color.WHITE);
			}
			g.drawString(MENUS[i], x, y + DIS * i);
		}
	}

	private void drawOver(Graphics g) {
	}

	private void drawRun(Graphics g) {
		g.setColor(Color.BLACK);
		g.fillRect(0,0,FRAME_WIDTH,FRAME_HEIGHT);
		myTank.draw(g);
	}

	private void drawAbout(Graphics g) {

	}

	private void drawHelp(Graphics g) {

	}

}
