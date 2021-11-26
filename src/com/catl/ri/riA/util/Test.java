package com.catl.ri.riA.util;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import wt.util.WTException;

import com.catl.ri.riB.helper.EncryptHelper;

public class Test {
public static void main(String[] args) {
	int i=1;
	for (int start=25;start < 38; start++) {
		String str=
                 "<tr class=\"materialParams_r3\">"
				+"\n"
				+"   <td class=\"materialParams_c3\"><button class=\"clearbtn\" style=\"background-image: url('netmarkets/jsp/catl/ri/A/image/clear.png');\" onclick=\"clearrow('C"+start+"','Z"+start+"')\"/></button></td>"
				+"\n"
				+"   <td class=\"materialParams_c7\">"+i+"</td>"
				+"\n"
				+"   <td id=\"material_C"+start+"\" class=\"materialParams_c8\" search=\"name\"></td>"
				+"\n"
				+"   <td id=\"material_D"+start+"\" class=\"materialParams_c8\" search=\"number\"></td>"
				+"\n"
				+"   <td id=\"material_E"+start+"\" class=\"materialParams_c8\"></td>"
				+"\n"
				+"   <td id=\"material_F"+start+"\" class=\"materialParams_c8\"></td>"
				+"\n"
				+"   <td id=\"material_G"+start+"\" class=\"materialParams_c8\"></td>"
				+"\n"
				+"   <td id=\"material_H"+start+"\" class=\"materialParams_c8\"></td>"
				+"\n"
				+"   <td id=\"material_I"+start+"\" class=\"materialParams_c8\"></td>"
				+"\n"
				+"   <td id=\"material_J"+start+"\" class=\"materialParams_c8\"></td>"
				+"\n"
				+"   <td id=\"material_K"+start+"\" class=\"materialParams_c8\"></td>"
				+"\n"
				+"   <td id=\"material_L"+start+"\" class=\"materialParams_c8\"></td>"
				+"\n"
				+"   <td id=\"material_M"+start+"\" class=\"materialParams_c8\"></td>"
				+"\n"
				+"   <td id=\"material_N"+start+"\" class=\"materialParams_c8\"></td>"
				+"\n"
				+"   <td id=\"material_O"+start+"\" class=\"materialParams_c8\"></td>"
				+"\n"
				+"   <td id=\"material_P"+start+"\" class=\"materialParams_c8\"></td>"
				+"\n"
			    +"</tr>";
		i++;
		System.out.println(str);
	}
	
  }
	public static void main1(String[] args) throws InvalidFormatException, IOException, GeneralSecurityException, WTException {
		EncryptHelper.getEncryptWorkbook("E://tmp//b.xlsx");
	}
}
