package com.catl.promotion.delegates;

import java.util.Iterator;
import java.util.Set;

import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.util.OidHelper;
import com.ptc.windchill.enterprise.maturity.PromotionRequestHelper;
import com.ptc.windchill.enterprise.maturity.forms.delegates.PromotionObjectsFormDelegate;

import wt.fc.WTReference;
import wt.fc.collections.WTArrayList;
import wt.fc.collections.WTCollection;
import wt.fc.collections.WTHashSet;
import wt.fc.collections.WTSet;
import wt.maturity.MaturityHelper;
import wt.maturity.Promotable;
import wt.maturity.PromotionNotice;
import wt.maturity.PromotionSeed;
import wt.util.WTException;

public class CatlPromotionObjectsFormDelegate extends PromotionObjectsFormDelegate {

	private WTCollection promotionBaselineObjects = null;
	private static final String PROMOTION_ITEMS_TABLE_ID = "com.catl.promotion.mvc.builders.CatlPromotionObjects";
	
	@Override
	protected void savePromotionTargets(PromotionNotice pn, ObjectBean bean) throws Exception {
		WTCollection localWTCollection = getPromotionBaselineObjects(bean);
	    Iterator<?> localIterator = localWTCollection.referenceIterator();
	    WTHashSet localWTHashSet = new WTHashSet();
	    while (localIterator.hasNext()) {
	    	WTReference localWTReference = (WTReference)localIterator.next();
	        localWTHashSet.add(localWTReference);
	    }
	    MaturityHelper.service.savePromotionTargets(pn, localWTHashSet);
	}
	
	@Override
	protected WTSet createPromotionSeedLinks(PromotionNotice pn, ObjectBean bean) throws WTException {
		WTCollection localWTCollection = getPromotionBaselineObjects(bean);
	    Iterator<?> persistableIter = localWTCollection.persistableIterator();
	    WTHashSet seedSet = new WTHashSet();
	    while (persistableIter.hasNext()) {
	    	seedSet.add(PromotionSeed.newPromotionSeed(pn, (Promotable)persistableIter.next()));
	    }
	    if(seedSet.isEmpty()){
	    	throw new WTException("没有添加升级对象！");
	    }
		return seedSet;
	}

	@Override
	public WTCollection getPromotionBaselineObjects(ObjectBean paramObjectBean) throws WTException {
		if (this.promotionBaselineObjects == null) {
			Set<?> localSet = PromotionRequestHelper.getPromotionBaselineOids(paramObjectBean, PROMOTION_ITEMS_TABLE_ID);
			this.promotionBaselineObjects = OidHelper.getWTCollection(localSet);
			if (this.promotionBaselineObjects == null) {
				this.promotionBaselineObjects = new WTArrayList();
			}
	    }
	    return this.promotionBaselineObjects;
	}

}
