package com.kekwy.tankwar.util;

import java.awt.*;

public class TankWarUtil {
	private TankWarUtil() {
	}


	/**
	 * 得到指定区间的随机数
	 *
	 * @param min 区间最小值，包含
	 * @param max 区间最大值，不包含
	 * @return 随机数
	 */
	public static int getRandomNumber(int min, int max) {
		return (int) (Math.random() * (max - min) + min);
	}

	/**
	 * 得到随机颜色
	 *
	 * @return 颜色
	xx*/
	public static Color getRandomColor() {
		int red = getRandomNumber(0, 256);
		int green = getRandomNumber(0, 256);
		int blue = getRandomNumber(0, 256);
		return new Color(red, green, blue);
	}

	/**
	 * 判断一个点是否在某一个正方形的内部
	 * @param rectX 正方形的中心点的x坐标
	 * @param rectY 正方形的中心点的y左边
	 * @param radius 正方形边长的一半
	 * @param pointX 点的x坐标
	 * @param pointY 点的y坐标
	 * @return 是否在内部
	 */
	public static boolean isCollide(int rectX, int rectY, int radius, int pointX, int pointY) {
		int disX = Math.abs(rectX - pointX);
		int disY = Math.abs(rectY - pointY);
		return (disX < radius && disY < radius);
	}

	public static Image createImage(String path) {
		return Toolkit.getDefaultToolkit().createImage(TankWarUtil.class.getResource(path));
	}
}
