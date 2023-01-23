package com.kekwy.tankwar.util;

public class RandomGen {
	/**
	 * 得到指定区间的随机数
	 *
	 * @param min 区间最小值，包含
	 * @param max 区间最大值，不包含
	 * @return 随机数
	 */
	public static double getRandomNumber(double min, double max) {
		return Math.random() * (max - min) + min;
	}
}
