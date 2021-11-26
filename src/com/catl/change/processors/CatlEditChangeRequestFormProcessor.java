package com.catl.change.processors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import wt.change2.ChangeHelper2;
import wt.change2.ChangeRequest2;
import wt.change2.WTChangeOrder2;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.epm.EPMDocumentMaster;
import wt.epm.structure.EPMReferenceLink;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.collections.WTList;
import wt.part.WTPart;
import wt.util.WTException;
import wt.vc.config.ConfigHelper;
import wt.vc.config.LatestConfigSpec;

import com.catl.change.ChangeUtil;
import com.catl.common.constant.Constant;
import com.catl.common.constant.PartState;
import com.catl.common.constant.TypeName;
import com.catl.common.util.EpmUtil;
import com.catl.common.util.IBAUtil;
import com.catl.doc.maturityUpReport.MaturityUpReportHelper;
import com.catl.integration.DrawingInfo;
import com.catl.part.PartConstant;
import com.catl.promotion.util.PromotionUtil;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.lwc.server.PersistableAdapter;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.core.meta.common.UpdateOperationIdentifier;
import com.ptc.core.meta.server.TypeIdentifierUtility;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.netmarkets.util.misc.NmContext;
import com.ptc.windchill.enterprise.change2.forms.ChangeManagementFormProcessorHelper;
import com.ptc.windchill.enterprise.change2.forms.processors.EditChangeRequestFormProcessor;
import com.ptc.windchill.enterprise.part.commands.PartDocServiceCommand;

public class CatlEditChangeRequestFormProcessor extends EditChangeRequestFormProcessor {
	
	@Override
	public FormResult postProcess(NmCommandBean cmdBean, List<ObjectBean> objectList) throws WTException {
		FormResult formResult = super.postProcess(cmdBean, objectList);
		Object objOrig= objectList.get(0).getObject();
		StringBuffer message = ChangeUtil.checkMaturity(objOrig,objectList);
		if (objOrig instanceof ChangeRequest2) {
			ChangeRequest2 ecr = (ChangeRequest2) objOrig;
			QueryResult qr = ChangeHelper2.service.getChangeables(ecr);
			while (qr.hasMoreElements()) {
				Object obj = qr.nextElement();
				if (obj instanceof WTPart) {//零部件
					WTPart part = (WTPart) obj;
					if(PromotionUtil.isSourceChangeUndone(part, null) > 0){
						message.append(part.getNumber() + ",对象正在采购类型更改单中，不能启动工程变更！ \n");
					}
					if(PromotionUtil.isPlatformChangeUndone(part, null) > 0){
						message.append(part.getNumber() + ",对象正在产品线标识更改单中，不能启动工程变更！ \n");
					}
					if(PromotionUtil.isexsitPromotion(part) > 0){
						message.append(part.getNumber() + ",对象正在设计禁用单中，不能启动工程变更！ \n");
					}
					String faestate = (String)IBAUtil.getIBAValue(part, PartConstant.IBA_CATL_FAEStatus);
					if(PartConstant.CATL_FAEStatus_3.equals(faestate)){
						message.append(part.getNumber() + ",对象正在FAE流程单中，不能启动工程变更！ \n");
					}
					if(MaturityUpReportHelper.isNFAEUndone(part)){
						message.append(part.getNumber() + ",对象正在非FAE流程单中，不能工程变更！ \n");
					}
				}
			}
		}
		if(message.length() > 0){
			throw new WTException(message.toString());
		}
		return formResult;
	}


	

}
