package com.catl.part.relation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import org.apache.log4j.Logger;

import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.doc.WTDocument;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.collections.WTCollection;
import wt.lifecycle.State;
import wt.log4j.LogR;
import wt.part.WTPart;
import wt.part.WTProductInstance2;
import wt.session.SessionHelper;
import wt.util.WTException;

import com.catl.common.constant.Constant;
import com.catl.common.constant.DocState;
import com.catl.common.constant.PartState;
import com.catl.common.constant.TypeName;
import com.catl.doc.relation.DocPartRelationUtil;
import com.catl.integration.DrawingInfo;
import com.catl.integration.DrawingSendERP;
import com.catl.integration.ErpResponse;
import com.catl.integration.Message;
import com.catl.integration.PIService;
import com.catl.integration.ReleaseUtil;
import com.catl.integration.log.DrawingSendERPLog;
import com.catl.loadData.StrUtils;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.core.meta.server.TypeIdentifierUtility;
import com.ptc.core.ui.resources.FeedbackType;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.netmarkets.util.misc.NmContext;
import com.ptc.windchill.enterprise.util.PartManagementHelper;
import com.ptc.windchill.enterprise.part.commands.PartDocServiceCommand;
import com.ptc.windchill.enterprise.part.forms.PartDocRelationDefaultObjectFormProcessor;

public class CatlPartDocDescRelationFormProcessor extends PartDocRelationDefaultObjectFormProcessor {

	private static final Logger LOGGER = LogR.getLogger(CatlPartDocDescRelationFormProcessor.class.getName());

	@Override
	public FormResult doOperation(NmCommandBean clientData, List<ObjectBean> objectList) throws WTException {
		WTCollection objects = null;
		Object objOrig = clientData.getPrimaryOid().getWtRef().getObject();
		String action = "relatedPartsDocuments";
		FormResult result = new FormResult();
		result.setStatus(FormProcessingStatus.SUCCESS);

		if (objOrig instanceof WTPart) {
			WTPart partOrig = (WTPart) objOrig;
			objects = CatlPartDocRefRelationFormProcessor.addRelation(clientData, false);
			WTPart part = partOrig;

			for (Iterator it = objects.persistableIterator(); it.hasNext();) {
				Object obj = (Object) it.next();
				if (obj instanceof WTPart) {
					part = (WTPart) obj;
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
							DocPartRelationUtil.sendERPAddDesc(part, doc);
						}
					}
				}
			}
		}

		super.doOperation(clientData, objectList);
		return result;
	}

}
