package com.catl.promotion.dbs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import wt.access.AdHocControlled;
import wt.maturity.PromotionNotice;
import wt.part.WTPart;
import wt.util.WTException;
import wt.workflow.work.WorkItem;

import com.catl.bom.workflow.BomWfUtil;
import com.ptc.xworks.workflow.collector.PermissionTargetCollectionContext;
import com.ptc.xworks.workflow.collector.PermissionTargetObjectCollector;

public class PlatformChangeObjectsCollector implements PermissionTargetObjectCollector {

	@Override
	public Collection<AdHocControlled> collectObjects(PermissionTargetCollectionContext collectionContext) {

		ArrayList<AdHocControlled> objects = new ArrayList<AdHocControlled>();
		try {
			WorkItem workitem = collectionContext.getWorkItem();
			PromotionNotice pn = (PromotionNotice) workitem.getPrimaryBusinessObject().getObject();
			Set<WTPart> list = BomWfUtil.getTargets(pn);
			objects.addAll(list);
		} catch (WTException e) {
			e.printStackTrace();
		}
		return objects;
	}

}
