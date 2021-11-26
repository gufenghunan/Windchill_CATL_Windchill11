package com.catl.pd.util;

public class ExcelToHTMLTool {
 public static void main(String[] args) {
	 String html=Excel2Html.createHTMLTemplate("E://tmp//aaa.xls");
	 ToIDHTML.conveter(html);
 }
}
