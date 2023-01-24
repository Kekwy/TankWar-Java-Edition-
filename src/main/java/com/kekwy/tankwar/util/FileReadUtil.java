package com.kekwy.tankwar.util;

import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class FileReadUtil {
	/**
	 * 读取 Excel 文件，并返回包含表格指定范围内容的二维数组
	 * @param file Excel 文件输入流
	 * @param rowFrom 读取的起始行
	 * @param colFrom 读取的起始列
	 * @param rowN 期望读取的行数
	 * @param colN 期望读取的列数
	 * @param sheetIndex 期望读取的工作簿索引号
	 * @return 包含表格指定范围内容的二维数组
	 */
	public static int[][] readWorkBook(InputStream file, int rowFrom, int colFrom, int rowN, int colN, int sheetIndex) {
		int[][] content = new int[rowN][colN];

		Workbook workbook = null;
		try {
			workbook = new XSSFWorkbook(file);//Excel 2007
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
}
