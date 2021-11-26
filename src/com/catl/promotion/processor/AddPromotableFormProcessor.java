package com.catl.promotion.processor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.log4j.Logger;

import com.catl.bom.workflow.BomWfUtil;
import com.catl.promotion.mvc.CatlPromotionObjectsWizardTableBuilder;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.forms.FormResultAction;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.netmarkets.util.misc.NmContext;

import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.fc.collections.WTHashSet;
import wt.fc.collections.WTSet;
import wt.log4j.LogR;
import wt.maturity.MaturityBaseline;
import wt.maturity.MaturityHelper;
import wt.maturity.PromotionNotice;
import wt.part.WTPart;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.baseline.BaselineHelper;

/**
 * 添加升级对象
 * 
 * 
 * 
 */
public class AddPromotableFormProcessor extends DefaultObjectFormProcessor
{
	private static final String CLASSNAME = AddPromotableFormProcessor.class.getName();

	private static final Logger log = LogR.getLogger(CLASSNAME);

	@Override
	public FormResult doOperation(NmCommandBean clientData, List<ObjectBean> objectBeans) throws WTException
	{
		log.trace(" Enter Add promotable operation");
		FormResult formResult = super.doOperation(clientData, objectBeans);
		Object obj = clientData.getRequest().getSession().getAttribute("PromotionObject");
		// Object obj = Object)
		// clientData.getPrimaryOid().getWtRef().getObject();

		if (obj instanceof wt.maturity.PromotionNotice)
		{
			wt.maturity.PromotionNotice promotion = (wt.maturity.PromotionNotice) obj;
			MaturityBaseline baseline = promotion.getConfiguration();
	         ArrayList<Long> targetlists =new ArrayList<Long>();
	         targetlists =BomWfUtil.settargetList(promotion);
	         log.debug("targetlist size()===="+targetlists.size());
			ArrayList<NmContext> addItemOids = clientData.getSelected();
			WTSet selectedSet = new WTHashSet();
			if (addItemOids != null && addItemOids.size() > 0)
			{
				for (int i = 0; i < addItemOids.size(); i++)
				{
					NmContext ctext = (NmContext) addItemOids.get(i);
					obj = ctext.getTargetOid().getWtRef().getObject();

					if (obj instanceof WTPart)
					{
						WTPart part = (WTPart) obj;
						selectedSet.add(part);
						try
						{
							List otherList = CatlPromotionObjectsWizardTableBuilder.checkPRchildObjects(part);
						    log.debug("other size()=="+otherList.size());
							for (int j = 0; j < otherList.size(); j++) {
								WTObject object = (WTObject)otherList.get(j);
								if (!targetlists.contains(object.getPersistInfo().getObjectIdentifier().getId())) {
									selectedSet.add(object);
								}
							}
						} catch (Exception e)
						{
							e.printStackTrace();
						}
					}
					if (obj instanceof WTDocument)
					{
						WTDocument doc = (WTDocument) obj;
						selectedSet.add(doc);
					}
					if (obj instanceof EPMDocument)
					{
						EPMDocument epm = (EPMDocument) obj;
						try
						{
							List otherList = CatlPromotionObjectsWizardTableBuilder.checkPRchildObjects(epm);
							for (int j = 0; j < otherList.size(); j++) {
								WTObject object = (WTObject)otherList.get(j);
								if (!targetlists.contains(object.getPersistInfo().getObjectIdentifier().getId())) {
									selectedSet.add(object);
								}
							}
						} catch (Exception e)
						{
							e.printStackTrace();
						}
					}
				}
			}
			try
			{
				MaturityHelper.service.savePromotionTargets(promotion, selectedSet);
			} catch (WTPropertyVetoException e)
			{
				e.printStackTrace();
			}
			BaselineHelper.service.addToBaseline(selectedSet, baseline);

		}
		log.trace(" Exit Add promotable operation");
		return formResult;
	}

	@Override
	public FormResult setResultNextAction(FormResult formresult, NmCommandBean nmcommandbean, List<ObjectBean> list) throws WTException
	{
		String contextPath = nmcommandbean.getRequest().getContextPath();
		String oid = nmcommandbean.getRequest().getParameter("oid");
		String redirectURL = contextPath + "/netmarkets/jsp/catl/promotion/processPromotionInfo.jsp?oid=" + oid;
		System.out.println(redirectURL);
		formresult.setURL(redirectURL);
		formresult.setNextAction(FormResultAction.REFRESH_OPENER_AND_SUBMIT_IFRAMES);
		return formresult;
	}
}
