package com.catl.part.sourceChange;

import java.util.*;

import org.apache.log4j.Logger;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.log4j.LogR;
import wt.org.WTPrincipalReference;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.pds.StatementSpec;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

public class PartSourceChangeLogHelper {
	
	private static final Logger log = LogR.getLogger(PartSourceChangeLogHelper.class.getName());
	
	/**
	 * ADD Part Source Change History
	 * @throws WTException 
	 */
	public static void addPartSourceChangeLog(WTPrincipalReference operator, WTPart latestPart, String oldSource, String newSource,String oldFAE,String newFAE,String cause) throws WTException{
		try {
			PartSourceChangeLog partlog = PartSourceChangeLog.newPart((WTPartMaster)latestPart.getMaster());
			partlog.setOperator(operator);
			partlog.setVersion(latestPart.getVersionIdentifier().getValue()+"."+latestPart.getIterationIdentifier().getValue());
			partlog.setOldSource(oldSource);
			partlog.setNewSource(newSource);
			partlog.setOldFAE(oldFAE);
			partlog.setNewFAE(newFAE);
			partlog.setChangeReason(cause);
			PersistenceHelper.manager.save(partlog);
		} catch (WTPropertyVetoException e) {
			log.debug("保存物料成熟度记录异常!");
			e.printStackTrace();
			throw new WTException(e.getLocalizedMessage());
		}
		
	}
	
	/**
	 * GET all PartSourceChangeLog
	 * @throws WTException 
	 */
	public static List<PartSourceChangeLog> getPartSourceChangeLogByPart(WTPartMaster partmaster) throws WTException{
		List<PartSourceChangeLog> listchangelog = new ArrayList<PartSourceChangeLog>();
		
		String masteroid = partmaster.toString();
		
		masteroid = masteroid.substring(masteroid.indexOf(":")+1, masteroid.length());
		
		QuerySpec qs = new QuerySpec(PartSourceChangeLog.class);
		
		SearchCondition sc = new SearchCondition(PartSourceChangeLog.class,"partMaster.key.id",SearchCondition.EQUAL,Long.parseLong(masteroid));
		qs.appendWhere(sc, new int[] { 0 });
        QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
		while(qr.hasMoreElements()){
			PartSourceChangeLog changelog = (PartSourceChangeLog)qr.nextElement();
			listchangelog.add(changelog);
		}
		return listchangelog;
	}
}
