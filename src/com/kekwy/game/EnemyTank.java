package com.kekwy.game;

import com.kekwy.util.Constant;
import com.kekwy.util.MyUtil;

import java.awt.*;

import static com.kekwy.util.Constant.FRAME_WIDTH;

public class EnemyTank extends Tank {

	private long aiTime;
	private static Image[] tankImg;

	static {
		tankImg = new Image[4];
		tankImg[0] = Toolkit.getDefaultToolkit().createImage("res/enemy1U.gif");
		tankImg[1] = Toolkit.getDefaultToolkit().createImage("res/enemy1D.gif");
		tankImg[2] = Toolkit.getDefaultToolkit().createImage("res/enemy1L.gif");
		tankImg[3] = Toolkit.getDefaultToolkit().createImage("res/enemy1R.gif");
	}

	public EnemyTank(int x, int y, Direction forward) {
		super(x, y, forward);
		aiTime = System.currentTimeMillis();
	}

	@Override
	protected void drawImgTank(Graphics g) {
		ai();
		g.drawImage(tankImg[getForward().ordinal()], getX() - RADIUS, getY() - RADIUS, 2 * RADIUS, 2 * RADIUS, null);
	}

	//用于创建敌人的坦克
	public static Tank createEnemy() {
		int x = MyUtil.getRandomNumber(0, 2) == 0 ? RADIUS + 6 : FRAME_WIDTH - RADIUS - 6;
		int y = GameFrame.titleBarH + RADIUS;
		Tank enemy = new EnemyTank(x, y, Direction.DIR_DOWN);
		enemy.state = State.STATE_MOVE;
		enemy.isEnemy = true;
		return enemy;
	}

	private void ai() {
		if(System.currentTimeMillis() - aiTime > 3000) {
			// 随机一个状态
			setState(State.values()[MyUtil.getRandomNumber(0, 2)]);
			setForward(Direction.values()[MyUtil.getRandomNumber(0, 4)]);
			aiTime = System.currentTimeMillis();
		}
		if(Math.random() < 0.5) {
			fire();
		}
	}
}
