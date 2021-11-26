package com.catl.pd.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ToIDHTML {
	public static void conveter(String path,String htmlid) {
		// TODO Auto-generated method stub
		String htmlStr = "";
		String htmString = "";
		try {
			FileInputStream fileInputStream = new FileInputStream(new File(path));
			InputStreamReader streamReader = new InputStreamReader(
					fileInputStream);
			BufferedReader reader = new BufferedReader(streamReader);
			while ((htmlStr = reader.readLine()) != null) {
				if (htmlStr != null) {
					htmString += htmlStr + "\n";
					continue;
				}
			}
			reader.close();
			streamReader.close();
			fileInputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Pattern p = Pattern.compile("<td[^>]+>([A-Z]+[0-9]*)\\S</td>");
		Matcher m = p.matcher(htmString);
		while (m.find()) {
			String td = m.group();
			System.out.println(td);
			Pattern p1 = Pattern.compile(">[A-Z]+[0-9]*<*");
			Matcher m1 = p1.matcher(td);
			while (m1.find()) {
				 String id = m1.group().replace(">", "").replace("<", "");
				System.out.println(id);
				htmString = htmString.replace(td,td.replace("<td", "<td id=\""+htmlid+"_" + id + "\"").replace(">" + id + "<", "><"));
			}
		}
		htmString=htmString.replaceAll("#ccffff", "#FFF")
				.replaceAll("class=\"", "class=\""+htmlid+"_")
				.replaceAll("\\.c", "."+htmlid+"_c")
				.replaceAll("\\.r", "."+htmlid+"_r")
				.replaceAll("\\.b", "."+htmlid+"_b")
				.replaceAll("\\.t", "."+htmlid+"_t");
		try {
			FileOutputStream outputStream = new FileOutputStream(path);
			outputStream.write(htmString.getBytes());
			outputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void conveter(String path) {
		// TODO Auto-generated method stub
		String htmlStr = "";
		String htmString = "";
		try {
			FileInputStream fileInputStream = new FileInputStream(new File(path));
			InputStreamReader streamReader = new InputStreamReader(
					fileInputStream);
			BufferedReader reader = new BufferedReader(streamReader);
			while ((htmlStr = reader.readLine()) != null) {
				if (htmlStr != null) {
					htmString += htmlStr + "\n";
					continue;
				}
			}
			reader.close();
			streamReader.close();
			fileInputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			FileOutputStream outputStream = new FileOutputStream(path);
			outputStream.write(htmString.getBytes());
			outputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
