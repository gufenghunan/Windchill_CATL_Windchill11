package com.catl.promotion.ui;

import java.io.Serializable;
import wt.fc.Persistable;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.fc.WTReference;
import wt.services.StandardManager;
import wt.util.WTException;

public class StandardDocPartQueryService  extends StandardManager implements DocPartQueryService, Serializable{
	private static final long serialVersionUID = 1L;
	
	private static final String CLASSNAME = StandardDocPartQueryService.class.getName();
	public static final String DOC = "WTDocument.class";
	public static final String PART = "WTPart.class";
	
	public String getConceptualClassname(){
		return CLASSNAME;		
	}
	
	public static StandardDocPartQueryService newStandardDocPartQueryService() throws WTException {
		StandardDocPartQueryService instance = new StandardDocPartQueryService();
		instance.initialize();
		return instance;
	}
	
	public QueryResult getDocPartByNumberName(String classType, String number, String name, String context) throws WTException {
		return null;
	}
		
}
