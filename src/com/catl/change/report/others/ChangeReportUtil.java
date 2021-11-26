package com.catl.change.report.others;

import org.apache.log4j.Logger;

import com.catl.change.mvc.AffectedItemsTableBuilder;
import com.ptc.windchill.enterprise.report.Report;

import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.log4j.LogR;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTException;

public class ChangeReportUtil {

	private static final Logger log = LogR.getLogger(ChangeReportUtil.class.getName());
	
	public static String queryReportOidByName(String name){
		String ret = null;
		try{
			
			QuerySpec qs = new QuerySpec();
			int reportIndex = qs.appendClassList(Report.class, true);
			qs.appendWhere(new SearchCondition(Report.class,Report.NAME,SearchCondition.EQUAL,name),reportIndex);
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(Report.class,Report.READY_FOR_USE,SearchCondition.IS_TRUE),reportIndex);
			log.error("queryReportOidByName qs="+qs.toString());
			QueryResult qr = PersistenceHelper.manager.find(qs);
			while(qr.hasMoreElements()){
				Persistable[] persist=(Persistable[]) qr.nextElement();
				Report report=(Report) persist[0];
				ret = report.getClassInfo().getClassname()+":"+report.getPersistInfo().getObjectIdentifier().getId();
			}
			
		}catch(WTException e){
			log.error("queryReportOidByName fail", e);
		}
		return ret;
	}
}
