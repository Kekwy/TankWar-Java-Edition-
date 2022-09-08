package com.kekwy.tankwar.util;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

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

	public static int[][] readWorkBook(String filepath, int rowFrom, int colFrom, int rowN, int colN, int sheetIndex) {
		int[][] content = new int[rowN][colN];

		Workbook workbook = null;
		String suffix = filepath.substring(filepath.lastIndexOf('.'));
		try {
			if (suffix.equals(".xlsx")) {

				workbook = new XSSFWorkbook(new FileInputStream(filepath));//Excel 2007
			} else if (suffix.equals(".xls")) {
				workbook = new HSSFWorkbook(new FileInputStream(filepath));//Excel 2003
			} else {
				// System.out.println("目标文件不是合法的Excel文件");
				System.exit(-2);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		Sheet sheet = workbook.getSheetAt(sheetIndex);
		for (int i = 0; i < rowN; i++) {
			Row row = sheet.getRow(i+ rowFrom);
			for (int j = 0; j < colN; j++) {
				Cell cell = row.getCell(j + colFrom);
				content[i][j] = Double.valueOf(cell.getNumericCellValue()).intValue();
			}
		}

		return content;
	}

	public static Properties loadProperties(InputStream fileInputStream) {
		BufferedInputStream bis = new BufferedInputStream(fileInputStream);
		Properties props = new Properties();
		try {
			props.load(bis);
		} catch (IOException e) {
			System.out.println("配置文件载入失败");
			throw new RuntimeException(e);
		}
		return props;
	}

	public static String[] splitString(String src, String spliter, int n) {
		String[] res = new String[n];
		int begin = 0;
		for (int i = 0; i < n; i++) {
			int ptr = src.indexOf(spliter, begin) + spliter.length();
			res[i] = (src.substring(begin, ptr));
			begin += ptr;
		}
		return res;
	}

}
