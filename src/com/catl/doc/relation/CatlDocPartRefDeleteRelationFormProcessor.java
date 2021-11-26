package com.catl.doc.relation;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.fc.Persistable;
import wt.fc.PersistenceServerHelper;
import wt.org.WTPrincipal;
import wt.part.WTPart;
import wt.part.WTPartReferenceLink;
import wt.pom.Transaction;
import wt.services.applicationcontext.implementation.ServiceProperties;
import wt.session.SessionHelper;
import wt.util.WTException;

import com.catl.common.constant.PartState;
import com.catl.common.constant.TypeName;
import com.catl.common.util.CommonUtil;
import com.catl.common.util.PropertiesUtil;
import com.catl.doc.EDatasheetDocUtil;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.meta.server.TypeIdentifierUtility;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.netmarkets.util.misc.NmContext;
import com.ptc.windchill.enterprise.part.forms.DocPartRefAddRelationFormProcessor;
import com.ptc.windchill.enterprise.part.forms.DocPartRefDeleteRelationFormProcessor;

public class CatlDocPartRefDeleteRelationFormProcessor extends DocPartRefDeleteRelationFormProcessor {

	@Override
	public FormResult doOperation(NmCommandBean paramNmCommandBean, List<ObjectBean> paramList) throws WTException {

		FormResult result = new FormResult();
		result.setStatus(FormProcessingStatus.SUCCESS);
		
		Object objOrig = paramNmCommandBean.getPrimaryOid().getWtRef().getObject();
		ArrayList<NmContext> selects = paramNmCommandBean.getSelected();
		ArrayList<WTPart> list = new ArrayList<WTPart>();
		for(NmContext nmContext : selects){
			Persistable persistable = nmContext.getTargetOid().getWtRef().getObject();
			list.add((WTPart)persistable);
		}

		if (objOrig instanceof WTDocument) {
			WTDocument doc = (WTDocument) objOrig;
			DocPartRelationUtil.checkPermission(doc, list);
			
			StringBuffer buffer = DocPartRelationUtil.docCheckDatasheet(doc, list);//Datasheet文件中移除PN校验
			if(buffer.length() > 0)
				throw new WTException(buffer.toString());
			
			DocPartRelationUtil.deleteRef(doc, list);
		}

		return result;
	}

}
