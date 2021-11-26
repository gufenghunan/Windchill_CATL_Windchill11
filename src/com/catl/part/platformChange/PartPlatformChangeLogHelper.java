package com.catl.part.platformChange;

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

public class PartPlatformChangeLogHelper {
	
	private static final Logger log = LogR.getLogger(PartPlatformChangeLogHelper.class.getName());
	
	/**
	 * ADD Part Platform Change History
	 * @throws WTException 
	 */
	public static void addPartPlatformChangeLog(WTPrincipalReference operator, WTPart latestPart, String oldPlatform, String newPlatform,String oldFAE,String newFAE,String cause) throws WTException{
		try {
			PartPlatformChangeLog partlog = PartPlatformChangeLog.newPart((WTPartMaster)latestPart.getMaster());
			partlog.setOperator(operator);
			partlog.setVersion(latestPart.getVersionIdentifier().getValue()+"."+latestPart.getIterationIdentifier().getValue());
			partlog.setOldPlatform(oldPlatform);
			partlog.setNewPlatform(newPlatform);
			partlog.setChangeReason(cause);
			PersistenceHelper.manager.save(partlog);
		} catch (WTPropertyVetoException e) {
			log.debug("保存产品线标识记录异常!");
			e.printStackTrace();
			throw new WTException(e.getLocalizedMessage());
		}
		
	}
	
	/**
	 * GET all PartPlatformChangeLog
	 * @throws WTException 
	 */
	public static List<PartPlatformChangeLog> getPartPlatformChangeLogByPart(WTPartMaster partmaster) throws WTException{
		List<PartPlatformChangeLog> listchangelog = new ArrayList<PartPlatformChangeLog>();
		
		String masteroid = partmaster.toString();
		
		masteroid = masteroid.substring(masteroid.indexOf(":")+1, masteroid.length());
		
		QuerySpec qs = new QuerySpec(PartPlatformChangeLog.class);
		
		SearchCondition sc = new SearchCondition(PartPlatformChangeLog.class,"partMaster.key.id",SearchCondition.EQUAL,Long.parseLong(masteroid));
		qs.appendWhere(sc, new int[] { 0 });
        QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
		while(qr.hasMoreElements()){
			PartPlatformChangeLog changelog = (PartPlatformChangeLog)qr.nextElement();
			listchangelog.add(changelog);
		}
		return listchangelog;
	}
}
