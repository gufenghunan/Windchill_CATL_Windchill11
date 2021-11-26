package com.catl.battery.util;

public class ExcelToHTMLTool {
 public static void main(String[] args) {
	 String html=Excel2Html.createHTMLTemplate("E:\\tmp\\bak\\work\\aa.xls");
	 ToIDHTML.conveter(html,"bom");
 }
}
