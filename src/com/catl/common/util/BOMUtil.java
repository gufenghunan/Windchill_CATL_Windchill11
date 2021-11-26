package com.catl.common.util;

import java.util.HashSet;
import java.util.Set;

import wt.fc.QueryResult;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.part.WTPartMaster;
import wt.part.WTPartUsageLink;
import wt.util.WTException;

public class BOMUtil {

	/**
	 * 获取WTPartUsageLink的所有替代物料
	 * @param usageLink
	 * @return
	 * @throws WTException
	 */
	public static Set<WTPart> getSubstitutes(WTPartUsageLink usageLink) throws WTException{
		Set<WTPart> substitutes = new HashSet<WTPart>();
		QueryResult qrmster = WTPartHelper.service.getSubstitutesWTPartMasters(usageLink);
		while(qrmster.hasMoreElements()){
			WTPartMaster substitute = (WTPartMaster)qrmster.nextElement();
			substitutes.add(PartUtil.getLastestWTPartByNumber(substitute.getNumber()));
		}
		return substitutes;
	}
}
