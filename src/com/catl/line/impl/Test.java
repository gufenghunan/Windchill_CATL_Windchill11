package com.catl.line.impl;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashMap;

import org.drools.core.util.StringUtils;

import com.catl.line.constant.ConstantLine;
import com.catl.line.exception.LineException;
import com.itextpdf.text.log.SysoCounter;

public class Test {
	private static void colorConfigValidate(String color,String ldesc,String rdesc){
		String config=ConstantLine.config_validate_rstgcolor;
		String[] colorconfig=config.split("\\|");
		for (int i = 0; i < colorconfig.length; i++) {
			String ccolorconfig=colorconfig[i];
			System.out.println(ccolorconfig);
			String[] cconfigs=ccolorconfig.split(":");
			if(cconfigs.length!=3){
				throw new LineException("热缩管颜色配置文件出错");
			}
			HashMap cmap=new HashMap();
			if(cconfigs[0].equals(color)){
				System.out.println(cconfigs[0]+"---"+cconfigs[1]+"--"+cconfigs[2]);
				 if(!StringUtils.isEmpty(cconfigs[1])&&!Arrays.asList(cconfigs[1].split("、")).contains(ldesc)){
					 throw new LineException("左标签内容必须在"+cconfigs[1]+"中");
				 }
				 if(!StringUtils.isEmpty(cconfigs[2])&&!Arrays.asList(cconfigs[2].split("、")).contains(rdesc)){
					 throw new LineException("右标签内容必须在"+cconfigs[2]+"中");
				 }
				 break;
			}
		}
	}
	public static void main(String[] args) {
		//colorConfigValidate("黑红","负","负");
//		Timestamp date=new Timestamp(System.currentTimeMillis());
//		System.out.println(date.toGMTString());
//		String str=date.toString();
//		System.out.println(str.substring(0, str.lastIndexOf(".")));
		String str="{success:true,msg:'" + 123 + "'}";
		System.out.println(test1());
	}
	
	public static boolean test1(){
		try{
			String a="111";
			a.substring(0,2);
			return true;
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			System.out.println("finally");
		}
		return false;
	}
}

