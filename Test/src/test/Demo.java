package test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class Demo {

	static WebDriver driver = null;

	public static void main(String[] args) throws IOException, ParseException {

		initalizeDriver();
		List<WebElement> mTableRows = driver.findElements(
				By.xpath("/html/body/document/type/sequence/filename/description/text/div[215]/div/table/tbody/tr"));

		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("Table");
		Font fontRedSuperscript = workbook.createFont();
		fontRedSuperscript.setColor(Font.COLOR_NORMAL);
		fontRedSuperscript.setTypeOffset(Font.SS_SUPER);
		int r = 0;
		for (int i = 4; i <= mTableRows.size(); i++) {
			Row row = sheet.createRow(r);

			List<WebElement> columns = driver.findElements(
					By.xpath("/html/body/document/type/sequence/filename/description/text/div[215]/div/table/tbody/tr["
							+ i + "]/td"));
			System.out.println("");
			String val = "";
			for (int j = 1; j <= columns.size(); j++) {
				Cell cell = row.createCell(j);
				XSSFCellStyle style = workbook.createCellStyle();
				XSSFDataFormat format = workbook.createDataFormat();
				if (i == 4) {
					style.setFillForegroundColor(IndexedColors.BLUE.getIndex());
					style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
					cell.setCellStyle(style);
				}
				String value = driver.findElement(By
						.xpath("/html/body/document/type/sequence/filename/description/text/div[215]/div/table/tbody/tr["
								+ i + "]/td[" + j + "]"))
						.getText().trim();

				if (!value.equals("")) {
					val = value.replace(",", "");
					if (value.equals(")")) {
						value = "";
					} else if (value.equals("(A") || value.equals("(B")) {
						value = value + ")";
					} else if (StringUtils.isNumeric(val)) {
						style.setDataFormat(format.getFormat("#,##0.00"));
						cell.setCellStyle(style);
						cell.setCellValue(Double.parseDouble(val));
					} else if (val.contains("$")) {
						//System.out.println(val);
						//style.setDataFormat(format.getFormat("$#,##0.00"));
						//cell.setCellStyle(style);
						cell.setCellValue(val);
					} else if(value.contains("/")&&value.length()==10){
						Date date=new SimpleDateFormat("MM/dd/yyyy").parse(value);
						CreationHelper createHelper = workbook.getCreationHelper();
						style.setDataFormat(
							    createHelper.createDataFormat().getFormat("m/d/yy"));
						cell.setCellStyle(style);
						cell.setCellValue(date);
					}else{
						cell.setCellValue(value);
					}
				}

				if (j == 48) {
					int k=j-1;
					String oldValue = driver.findElement(By
							.xpath("/html/body/document/type/sequence/filename/description/text/div[215]/div/table/tbody/tr["
									+ i + "]/td[" + k + "]"))
							.getText().trim();
					if (!value.equals("")) {
						RichTextString richString = new XSSFRichTextString(oldValue + value);
						richString.applyFont(oldValue.length(), oldValue.length() + 1, fontRedSuperscript);
						cell.setCellValue(richString);
					}else{
						cell.setCellValue(oldValue);
					}
				}
				// System.out.print(value);
			}
			if (val.equals("")) {
				sheet.removeRow(row);
			} else {
				r++;
			}
		}
		for (int i = 0; i < sheet.getRow(0).getLastCellNum(); i++) {
			if (i > 8) {
				if (sheet.getRow(0).getCell(i).getStringCellValue().equals(""))
					sheet.setColumnHidden(i, true);
			}
		}

		sheet.setColumnHidden(0, true);
		sheet.setColumnHidden(1, true);
		sheet.setColumnHidden(2, true);
		sheet.setColumnHidden(4, true);
		sheet.setColumnHidden(5, true);
		sheet.setColumnHidden(7, true);
		sheet.setColumnHidden(8, true);
		FileOutputStream out = new FileOutputStream(
				new File("E:" + File.separator + "selenium" + File.separator + "output.xlsx"));

		workbook.write(out);
		out.close();
		workbook.close();
		driver.quit();
	}

	public static void initalizeDriver() {
		System.setProperty("webdriver.chrome.driver",
				"E:" + File.separator + "selenium" + File.separator + "chromedriver.exe");
		driver = new ChromeDriver();
		driver.manage().window().maximize();
		driver.get(
				"https://www.sec.gov/Archives/edgar/data/1001039/000104746918000180/a2233502zdef14a.htm#di42001_compensation_discussion_and_analysis");
	}
}
