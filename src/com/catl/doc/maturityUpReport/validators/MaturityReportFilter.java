package com.catl.doc.maturityUpReport.validators;

import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.catl.common.util.CommonUtil;
import com.catl.common.util.DocUtil;
import com.catl.common.util.PartUtil;
import com.catl.doc.maturityUpReport.MaturityUpReportHelper;
import com.catl.doc.maturityUpReport.NFAEMaturityUp3DocPartLink;
import com.ptc.core.ui.validation.DefaultSimpleValidationFilter;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationStatus;

import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.fc.Persistable;
import wt.lifecycle.State;
import wt.org.WTPrincipal;
import wt.part.WTPart;
import wt.session.SessionHelper;
import wt.util.WTException;

public class MaturityReportFilter extends DefaultSimpleValidationFilter {
	private static Logger log = Logger.getLogger(MaturityReportFilter.class);

	@Override
	public UIValidationStatus preValidateAction(UIValidationKey key, UIValidationCriteria criteria) {
		UIValidationStatus status = UIValidationStatus.HIDDEN;
		Persistable persistable = criteria.getContextObject().getObject();
		if(persistable instanceof WTDocument){
			WTDocument doc = (WTDocument)persistable;
			try {
				String action = key.getComponentID();
				if(MaturityUpReportHelper.isNFAEMaturityUp3Report(doc)){
					log.info("==action:"+action);
					if(StringUtils.equals(action, "relatedPartList")){
						return UIValidationStatus.ENABLED;
					}
					if(StringUtils.equals(action, "relatedDocumentsParts")){
						return UIValidationStatus.HIDDEN;
					}
					boolean isLatest = doc.equals(DocUtil.getLastestWTDocument(doc));
					log.info("==isLatest:"+isLatest);
					State docState = doc.getLifeCycleState();
					if(State.RELEASED.equals(docState) && StringUtils.equals(action, "removeParts")){
						boolean isDesigner = checkifDesigner(doc);
						log.info("==isDesigner:"+isDesigner);
						if(isLatest && isDesigner){
							return UIValidationStatus.ENABLED;
						}
					}
					else if(State.toState("WRITING").equals(docState) || State.toState("MODIFICATION").equals(docState)){
						boolean isDesigner = CommonUtil.checkifDesigner(doc, SessionHelper.getPrincipal());
						log.info("==isDesigner:"+isDesigner);
						if(isLatest && isDesigner){
							return UIValidationStatus.ENABLED;
						}
					}
				}
				else {
					if(StringUtils.equals(action, "relatedDocumentsParts")){
						return UIValidationStatus.ENABLED;
					}
					else {
						return UIValidationStatus.HIDDEN;
					}
				}
			} catch (WTException e) {
				e.printStackTrace();
			}
		}
		return status;
	}
	
	private boolean checkifDesigner(WTDocument doc) throws WTException{
		WTPrincipal user = SessionHelper.getPrincipal();
		if(CommonUtil.checkifDesigner(doc, user)){
			return true;
		}
		else {
			Set<NFAEMaturityUp3DocPartLink> links = MaturityUpReportHelper.getNFAEMaturityUp3DocPartLink((WTDocumentMaster)doc.getMaster());
			for (NFAEMaturityUp3DocPartLink link : links) {
				WTPart part = PartUtil.getLastestWTPartByNumber(link.getPartMaster().getNumber());
				if(CommonUtil.checkifDesigner(part, user)){
					return true;
				}
			}
		}
		return false;
	}

}
