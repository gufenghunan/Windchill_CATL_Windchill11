package com.catl.promotion.ui;

import java.io.Serializable;
import java.util.ArrayList;

import wt.services.StandardManager;
import wt.util.WTException;

public class StandardPromotionPackService  extends StandardManager implements PromotionPackService, Serializable{
	private static final long serialVersionUID = -8236180948240517231L;
	
	private static final String CLASSNAME = StandardPromotionPackService.class.getName();
	
	public String getConceptualClassname(){
		return CLASSNAME;		
	}
	
	public static StandardPromotionPackService newStandardPromotionPackService() throws WTException {
		StandardPromotionPackService instance = new StandardPromotionPackService();
		instance.initialize();
		return instance;
	}
	
	public ArrayList getPromotionPackItems(ArrayList promotablelist) throws Exception {
		ArrayList result = promotablelist;
		return result;
	}

}
