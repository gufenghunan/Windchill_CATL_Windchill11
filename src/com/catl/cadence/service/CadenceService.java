package com.catl.cadence.service;

import wt.part.WTPart;
import wt.util.WTException;

public interface CadenceService {
	
	public void addTableColumn() throws WTException;
	
	public void createAttrTable() throws WTException;
	
	public void addPartAttribute(WTPart part) throws WTException, Exception;

	public void updatePartState(WTPart part) throws WTException, Exception;
	
}
