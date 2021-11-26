package com.catl.part.relation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Logger;

import wt.content.ContentHelper;
import wt.content.ContentRoleType;
import wt.doc.WTDocument;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.collections.WTArrayList;
import wt.fc.collections.WTCollection;
import wt.fc.collections.WTList;
import wt.log4j.LogR;
import wt.part.WTPart;
import wt.part.WTProductInstance2;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.util.WTMessage;

import com.catl.common.constant.Constant;
import com.catl.common.constant.DocState;
import com.catl.common.constant.PartState;
import com.catl.common.constant.TypeName;
import com.catl.doc.relation.DocPartRelationUtil;
import com.catl.integration.DrawingInfo;
import com.catl.integration.DrawingSendERP;
import com.catl.integration.ErpResponse;
import com.catl.integration.PIService;
import com.catl.integration.log.DrawingSendERPLog;
import com.catl.loadData.StrUtils;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DynamicRefreshInfo;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.core.meta.server.TypeIdentifierUtility;
import com.ptc.core.ui.resources.FeedbackType;
import com.ptc.netmarkets.util.utilResource;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.netmarkets.util.misc.NmContext;
import com.ptc.windchill.enterprise.util.PartManagementHelper;
import com.ptc.windchill.enterprise.part.commands.PartDocServiceCommand;
import com.ptc.windchill.enterprise.part.forms.PartDocRelationDefaultObjectFormProcessor;

public class CatlPartDocDescDeleteRelationFormProcessor extends PartDocRelationDefaultObjectFormProcessor {

	private static final Logger LOGGER = LogR.getLogger(CatlPartDocDescRelationFormProcessor.class.getName());
	private static final String RESOURCE = "com.ptc.netmarkets.util.utilResource";

	@Override
	public FormResult doOperation(NmCommandBean clientData, List<ObjectBean> objectList) throws WTException {
		WTCollection objects = null;
		List<Object> objNotPasted = new Vector<Object>();
		Locale locale = SessionHelper.getLocale();
		String action = "relatedPartsDocuments";
		FormResult result = new FormResult();
		result.setStatus(FormProcessingStatus.SUCCESS);

		if (clientData.getSelected().size() == 0) {
			// Nothing selected to delete
			FeedbackMessage msg = new FeedbackMessage(FeedbackType.FAILURE, locale, new WTMessage(RESOURCE, utilResource.NO_OBJECT_SELECTED, null), null, new WTMessage(RESOURCE, utilResource.NO_OBJECT_SELECTED, null));
			result.addFeedbackMessage(msg);
			result.setStatus(FormProcessingStatus.FAILURE);
			return result;
		}

		Object objOrig = clientData.getPrimaryOid().getWtRef().getObject();

		if (objOrig instanceof WTPart) {
			WTPart partOrig = (WTPart) objOrig;
			// objects = PartManagementHelper.deleteRelation(clientData, true);
			Persistable persistable = clientData.getPrimaryOid().getWtRef().getObject();
			WTCollection wtcollection = CatlPartDocRefDeleteRelationFormProcessor.getDocsForRelation(clientData, false);
			if (persistable instanceof WTPart) {
				WTPart wtpart = (WTPart) persistable;
				if (wtcollection.size() > 0)
					objects = CatlPartDocRefDeleteRelationFormProcessor.deleteRelationship(wtcollection, wtpart, false);
				else
					((WTCollection) (objects)).add(wtpart);
			}
			WTPart part = partOrig;
			for (Iterator it = objects.persistableIterator(); it.hasNext();) {
				Object obj = (Object) it.next();
				if (obj instanceof WTPart) {
					part = (WTPart) obj;
				} else {
					objNotPasted.add(obj);
				}
			}
			if (partOrig.equals(part)) {
				redirectURL = null;
			} else {
				redirectURL = getURL(part, action);
			}

			if (part.getLifeCycleState().toString().equals(PartState.RELEASED)) {
				ArrayList<NmContext> list = clientData.getSelected();
				for (NmContext context : list) {
					Object obj = context.getTargetOid().getWtRef().getObject();
					if (obj instanceof WTDocument) {
						WTDocument doc = (WTDocument) obj;
						if (doc.getLifeCycleState().toString().equals(DocState.RELEASED)) {
							DocPartRelationUtil.sendERPDeleteDesc(part, doc);
						}
					}
				}
			}

		}
		super.doOperation(clientData, objectList);
		return result;
	}

	@Override
	protected FormResult setRefreshInfo(FormResult result, NmCommandBean cb, List<ObjectBean> objectBeans) throws WTException {
		DynamicRefreshInfo dynRefrInfo = new DynamicRefreshInfo();
		// old
		dynRefrInfo.setLocation(cb.getActionOid());
		// new
		dynRefrInfo.setOid(cb.getActionOid());
		dynRefrInfo.setAction(cb.DYNAMIC_UPD);
		result.addDynamicRefreshInfo(dynRefrInfo);
		return result;
	}

}
