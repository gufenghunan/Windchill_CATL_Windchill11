package com.catl.part.maturity;

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

public class PartMaturityChangeLogHelper {
	
	private static final Logger log = LogR.getLogger(PartMaturityChangeLogHelper.class.getName());
	
	/**
	 * ADD Part FAE History
	 * @param operator
	 * @param latestPart
	 * @param oldMaturity
	 * @param newMaturity
	 * @throws WTPropertyVetoException 
	 * @throws WTException 
	 */
	public static void addPartMaturityChangeLog(WTPrincipalReference operator, WTPart latestPart, String oldMaturity, String newMaturity) throws WTException{
		try {
			PartMaturityChangeLog partlog = PartMaturityChangeLog.newPart((WTPartMaster)latestPart.getMaster());
			partlog.setOperator(operator);
			partlog.setVersion(latestPart.getVersionIdentifier().getValue()+"."+latestPart.getIterationIdentifier().getValue());
			partlog.setOldMaturity(oldMaturity);
			partlog.setNewMaturity(newMaturity);
			PersistenceHelper.manager.save(partlog);
		} catch (WTPropertyVetoException e) {
			log.debug("保存物料成熟度记录异常!");
			e.printStackTrace();
			throw new WTException(e.getLocalizedMessage());
		}
		
	}
	
	/**
	 * GET all PartMaturityChangeLog
	 * @throws WTException 
	 * @author zyw2
	 */
	public static List<PartMaturityChangeLog> getPartMaturityChangeLogByPart(WTPartMaster partmaster) throws WTException{
		List<PartMaturityChangeLog> listchangelog = new ArrayList<PartMaturityChangeLog>();
		
		String masteroid = partmaster.toString();
		
		masteroid = masteroid.substring(masteroid.indexOf(":")+1, masteroid.length());
		
		QuerySpec qs = new QuerySpec(PartMaturityChangeLog.class);
		
		SearchCondition sc = new SearchCondition(PartMaturityChangeLog.class,"partMaster.key.id",SearchCondition.EQUAL,Long.parseLong(masteroid));
		qs.appendWhere(sc, new int[] { 0 });
        QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
		while(qr.hasMoreElements()){
			PartMaturityChangeLog changelog = (PartMaturityChangeLog)qr.nextElement();
			listchangelog.add(changelog);
		}
		return listchangelog;
	}
}
