package com.catl.require.util;

public class Test {
	public static void main(String[] args) {
		String val="[A]";
		System.out.println(val.toString().replace("[]", ""));
	   int step=1;
		switch (step) {
		case 1:
			String a="11";
			int i=0;
			while(i==0){
				System.out.println("标识只能是A、B、C,请重新输入");
				if(va("11")){return;};
			}
			if(va(a)){return;};
			System.out.println("222");
		break;
		}
	}

	private static boolean va(String a) {
		if(a.endsWith("11")){
			return true;
		}
		return false;
		
	}
}
