package com.catl.require.util;

import java.util.ArrayList;
import java.util.List;

import wt.part.WTPart;

import com.catl.line.util.CommonUtil;
import com.catl.line.util.IBAUtil;
import com.catl.promotion.bean.PlatformChangeXmlObjectBean;
import com.catl.require.constant.ConstantRequire;
import com.ptc.xworks.util.StringMessage;
import com.ptc.xworks.xmlobject.annotation.EnumerationValue;
import com.ptc.xworks.xmlobject.annotation.EnumerationValueInfo;
import com.ptc.xworks.xmlobject.web.AttributeGuiComponentBuildContext;

public class PlatformXmlObjectUtils {

	public static List<EnumerationValue> getProcessPlatformEnum(AttributeGuiComponentBuildContext buildContext){
		List<EnumerationValue> list = new ArrayList<EnumerationValue>();
		try{
		PlatformChangeXmlObjectBean bean =(PlatformChangeXmlObjectBean) buildContext.getContextObject();
		String partNumber=bean.getPartNumber();
		WTPart part=CommonUtil.getLatestWTpartByNumber(partNumber);
		   String oldplatform=IBAUtil.getIBAStringValue(part, ConstantRequire.iba_CATL_Platform);
		   if(oldplatform.equals("C")){
				list.add(new EnumerationValueInfo("A", new StringMessage("A")));
				list.add(new EnumerationValueInfo("B", new StringMessage("B")));
		   }else if(oldplatform.equals("B")){
			   list.add(new EnumerationValueInfo("A", new StringMessage("A")));
		   }
		}catch(Exception e){
			e.printStackTrace();
		}
		return list;
	}
}