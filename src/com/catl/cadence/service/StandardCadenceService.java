package com.catl.cadence.service;

import java.io.Serializable;

import wt.part.WTPart;
import wt.services.StandardManager;
import wt.util.WTException;

import com.catl.cadence.util.ConfigTableUtil;
import com.catl.cadence.util.PartReleaseToCadenceUtil;


public class StandardCadenceService extends StandardManager implements
	Serializable,CadenceService {

	/**
	 * 
	 */
	private static final long serialVersionUID = -848852797842020156L;

	public StandardCadenceService(){}
	
	public static StandardCadenceService newStandardCadenceService()
			throws WTException {
		StandardCadenceService standardwtpartservice = new StandardCadenceService();
		standardwtpartservice.initialize();
		return standardwtpartservice;
	}
	
	public void addTableColumn() throws WTException{
		ConfigTableUtil.selectTable();
	}
	
	public void createAttrTable() throws WTException{
		ConfigTableUtil.createAttrTable();
	}
	
	public void addPartAttribute(WTPart part) throws Exception{
		PartReleaseToCadenceUtil.sendPartToCadence(part);
	}
	
	public void updatePartState(WTPart part) throws Exception{
		PartReleaseToCadenceUtil.updateStateToCadence(part);
	}
}
