package com.catl.loadData;

public class StrUtils {

	public static boolean isEmpty(String str){
		if(str == null || str.trim().equals("") || str.equals("null"))
			return true;
		return false;
	}
}
