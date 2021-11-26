package com.catl.common.util.change;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.catl.change.util.ChangeConst;
import com.catl.common.constant.ChangeState;
import com.ptc.core.meta.server.TypeIdentifierUtility;
import com.ptc.windchill.enterprise.change2.commands.RelatedChangesQueryCommands;

import wt.change2.ChangeHelper2;
import wt.change2.WTChangeActivity2;
import wt.change2.WTChangeOrder2;
import wt.enterprise.RevisionControlled;
import wt.fc.ObjectReference;
import wt.fc.QueryResult;
import wt.fc.collections.WTCollection;
import wt.util.WTException;

public class ECAUtil {

	private static Logger log = Logger.getLogger(ECAUtil.class.getName());
	
	public static Boolean checkExistECA(RevisionControlled reControlled) {
		Boolean isworkECA = false;
		try {
			WTCollection collection = RelatedChangesQueryCommands.getRelatedResultingChangeNotices(reControlled);
			if (!collection.isEmpty()) {
				Iterator iterator = collection.iterator();
				while (iterator.hasNext()) {
					ObjectReference objReference = (ObjectReference) iterator.next();
					WTChangeOrder2 eco = (WTChangeOrder2) objReference.getObject();
					log.debug("eco number==" + eco.getNumber());
					QueryResult ecnqr = ChangeHelper2.service.getChangeActivities(eco);
					while (ecnqr.hasMoreElements()) {
						WTChangeActivity2 eca = (WTChangeActivity2) ecnqr.nextElement();
						String ecaType = TypeIdentifierUtility.getTypeIdentifier(eca).getTypename();
						log.debug("eca number===" + eca.getNumber() + "state=" + eca.getState().getState().toString());
						if (ecaType.contains(ChangeConst.CHANGETASK_TYPE_ECA) && 
								!eca.getState().toString().equalsIgnoreCase(ChangeState.RESOLVED) && 
								!eca.getState().toString().equalsIgnoreCase(ChangeState.CANCELLED)) {
							isworkECA = true;
						}
					}
				}
			}
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return isworkECA;
	}
}
