package com.catl.battery.util;

public class HTMLTemplateTool {
 public static void main(String[] args) {
	 String html=Excel2Html.createHTMLTemplate("E://doc/asm.xls");
	 ToIDHTML.conveter(html,"mechanicalasm");
 }
}
