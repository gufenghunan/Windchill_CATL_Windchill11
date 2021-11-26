package com.catl.battery.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.apache.poi.ss.usermodel.Workbook;

public class ExportTest {
	public static void main(String[] args) throws Exception {
		Workbook workbook=ExcelUtil.exportIfConf("E://tmp//test_098776_tre_XXX_MD_C组###000000-00000066.xlsx", "E://tmp//battery_config.xls");
		OutputStream out=new FileOutputStream(new File("E://tmp//battery_config1.xls"));
		workbook.write(out);
		out.flush();
		out.close();
		workbook.close();
	}
}
