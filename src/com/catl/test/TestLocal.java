package com.catl.test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public class TestLocal {

	public static void main(String[] args) throws IOException {
		//String[] cmd = {"Notepad.exe","D:\\1.txt"};
		//Runtime.getRuntime().exec(cmd);
		/*String ss ="A|B";
		String[] a = ss.split("\\|");
		System.out.println(a[0]+"\t"+a[1]);
		String s = "YES#供应商质量管理工程师&产品安全工程师&制造代表&物料控制专员&";
		//String s = "/Default/01Version 1.0/设计图档/CATIA图档";
		System.out.println(s.replaceAll("&amp;", "\\&"));
		if (s.indexOf("YES") > -1 || s.indexOf("OPT") > -1) {
			String noticeGroups = s.substring(s.indexOf("#") + 1, s.length());
			System.out.println(noticeGroups);
			String[] groups = noticeGroups.split("&");
			for (int i = 0; i < groups.length; i++) {
				System.out.println("_"+groups[i]+"_");
			}
		}*/
		//System.out.println(s.subSequence(0, s.lastIndexOf(".")));
		//System.out.println(s.substring(0, s.indexOf("-")));
		//InitialCheckinDataManager
		
		/*String[] ss = s.split("/");
		String path = "";
		System.out.println(ss.length);
		for (int i = 1; i < 3; i++) {
			path =path+"/"+ss[i];
			System.out.println(i+"."+ss[i]);
		}
		path = path+"/"+"零部件";
		System.out.println(path);*/
		String ss="23342-333";
		System.out.println(ss.substring(0, ss.indexOf("-")));
		
		Date dNow = new Date();   //当前时间
		Date dBefore = new Date();

		Calendar calendar = Calendar.getInstance(); //得到日历
		calendar.setTime(dNow);//把当前时间赋给日历
		calendar.add(Calendar.DAY_OF_MONTH, -7);  //设置为前一天
		dBefore = calendar.getTime();   //得到前一天的时间


		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //设置时间格式
		String defaultStartDate = sdf.format(dBefore);    //格式化前一天
		System.out.println(defaultStartDate);
		
		String s0 ="X|6";
		String s1 = "3";
		System.out.println("indeof\t"+s0.indexOf("X"));
		
		xmltest();
		
		
	}
	
	public static void mergeExcel(String path) throws Exception {
		File file = new File(path);
		if (file != null && file.exists()) {
			if (file.isDirectory()) {
				File[] allfile = file.listFiles();
				for (File tmp : allfile) {
					if (tmp.isFile()) {
						
						
					}
				}
			}
		}
	}

	public static void xmltest() throws FileNotFoundException, IOException{
		Element root = new Element("root");
		Document result = new Document(root);

		Element item = new Element("result");
		
		item.addContent(new Element("swPn").setText("111111"));
		item.addContent(new Element("flag").setText("E"));
		item.addContent(new Element("message").setText("333333333"));
		
		root.addContent(item);
		System.out.println(result.toString());
		
		XMLOutputter xmlout = new XMLOutputter(Format.getPrettyFormat());
		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		xmlout.output(result,bo);
		String xmlStr = bo.toString();
		//xmlout.output(result, new FileOutputStream("e:\\test1.xml"));
		System.out.println(xmlStr);
	}
}
