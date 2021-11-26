package com.catl.promotion.populator;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.apache.log4j.Logger;

import wt.maturity.PromotionNotice;
import wt.util.WTException;

import com.ptc.windchill.enterprise.maturity.PromotionRequestHelper;
import com.ptc.windchill.enterprise.maturity.forms.populators.PromotionNameAttributePopulator;

public class SCPromotionNameAttributePopulator extends PromotionNameAttributePopulator {

	private static final Logger LOGGER = Logger.getLogger(SCPromotionNameAttributePopulator.class);
	
	@Override
	protected String getName(Object arg0) {

		return "采购类型更改单_";
	}
	
	@Override
	protected String calculateNameAttribute(Object obj) {
		
		String s = "";
		try{
            String s1 = getName(obj);
            int i = PromotionRequestHelper.getAttributeMaxLength(PromotionNotice.class, "name");
            if(s1.length() < i) {
            	LOGGER.debug("Setting promotable object name.");
                StringBuffer stringbuffer = new StringBuffer(i);
                stringbuffer.append(s1);                
                if(stringbuffer.length() < i) {
                    SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
                    df.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
                    String s3 = df.format(new Date());
                    stringbuffer.append(s3);
                }
                if(stringbuffer.length() < i) {
                    s = stringbuffer.toString();
                }
            }else {
            	LOGGER.debug("Truncating promotable object name to promotion notice name max length.");
                s = s1.substring(0, i);
            }
        }catch(WTException wtexception){
        	LOGGER.trace("PromotionNameAttributePopulator#calculateNameAttribute: Unable to calculate name ", wtexception);
        }
		return s;
	}
}
