package com.catl.promotion.workflow;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import wt.fc.WTObject;
import wt.session.SessionServerHelper;

import com.catl.promotion.dbs.PlatformChangeXmlObjectUtil;

public class PlatformChangeExprFunction {

	private static final Logger LOGGER = Logger.getLogger(PlatformChangeExprFunction.class);


	// 表达式处理
	public static Map<String, String> initForm(WTObject pbo) throws Exception {

		LOGGER.info("pbo:" + pbo.getPersistInfo().toString());
		Map<String, String> message = new HashMap<String, String>();
		  SessionServerHelper.manager.setAccessEnforced(false);
		try{
			PlatformChangeXmlObjectUtil.initAppForm(pbo);
		}catch(Exception e){
			e.printStackTrace();
			SessionServerHelper.manager.setAccessEnforced(true);
		}
		
		return message;
	}
}
