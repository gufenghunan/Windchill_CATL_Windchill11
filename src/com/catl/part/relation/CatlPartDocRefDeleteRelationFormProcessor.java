package com.catl.part.relation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.catl.common.util.DocUtil;
import com.catl.doc.relation.DocPartRelationUtil;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DynamicRefreshInfo;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.core.ui.resources.FeedbackType;
import com.ptc.netmarkets.util.utilResource;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.netmarkets.util.misc.NmContext;
import com.ptc.windchill.enterprise.part.forms.PartDocRelationDefaultObjectFormProcessor;
import com.ptc.windchill.enterprise.util.PartManagementHelper;

import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.fc.ObjectReference;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.fc.collections.WTArrayList;
import wt.fc.collections.WTCollection;
import wt.inf.container.WTContainerHelper;
import wt.introspection.WTIntrospector;
import wt.log4j.LogR;
import wt.org.WTPrincipal;
import wt.part.PartDocHelper;
import wt.part.WTPart;
import wt.part.WTPartDescribeLink;
import wt.part.WTPartReferenceLink;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.util.WTMessage;
import wt.vc.struct.StructHelper;

public class CatlPartDocRefDeleteRelationFormProcessor extends PartDocRelationDefaultObjectFormProcessor
{

	private static final String RESOURCE = "com.ptc.netmarkets.util.utilResource";

	private static final Logger log;
	protected String redirectURL = null;

	static
	{
		try
		{
			log = LogR.getLogger(CatlPartDocRefDeleteRelationFormProcessor.class.getName());
		} catch (Exception e)
		{
			throw new ExceptionInInitializerError(e);
		}
	}

	@Override
	public FormResult doOperation(NmCommandBean clientData, List<ObjectBean> objectList) throws WTException
	{
		PartRelationUtil partUtil = new PartRelationUtil();
		WTCollection objects = null;
		List<Object> objNotPasted = new Vector<Object>();
		Locale locale = SessionHelper.getLocale();
		String action = "relatedPartsReferences";
		FormResult result = new FormResult();
		result.setStatus(FormProcessingStatus.SUCCESS);

		if (clientData.getSelected().size() == 0)
		{
			// Nothing selected to delete
			FeedbackMessage msg = new FeedbackMessage(FeedbackType.FAILURE, locale, new WTMessage(RESOURCE, utilResource.NO_OBJECT_SELECTED, null), null,
					new WTMessage(RESOURCE, utilResource.NO_OBJECT_SELECTED, null));
			result.addFeedbackMessage(msg);
			result.setStatus(FormProcessingStatus.FAILURE);
			return result;
		}

		Object objOrig = clientData.getPrimaryOid().getWtRef().getObject();

		if (objOrig instanceof WTPart)
		{
			WTPart partOrig = (WTPart) objOrig;
			// objects = PartManagementHelper.deleteRelation(clientData, true);
			Persistable persistable = clientData.getPrimaryOid().getWtRef().getObject();
			WTCollection wtcollection = getDocsForRelation(clientData, true);
			if (persistable instanceof WTPart)
			{
				WTPart wtpart = (WTPart) persistable;
				if (wtcollection.size() > 0)
					objects = deleteRelationship(wtcollection, wtpart, true);
				else
					((WTCollection) (objects)).add(wtpart);
			}
			WTPart part = partOrig;
			for (Iterator it = objects.persistableIterator(); it.hasNext();)
			{
				Object obj = (Object) it.next();
				if (obj instanceof WTPart)
				{
					part = (WTPart) obj;
				} else
				{
					objNotPasted.add(obj);
				}
			}
			if (partOrig.equals(part))
			{
				redirectURL = null;
			} else
			{
				redirectURL = getURL(part, action);
			}
		}
		super.doOperation(clientData, objectList);
		return result;
	}
    public static boolean isSiteAdmin(WTPrincipal wtPrincipal) {
        try {
            return WTContainerHelper.service.isAdministrator(WTContainerHelper.service.getExchangeRef(), wtPrincipal);
        } catch (WTException e) {
            e.printStackTrace();
        }
        return false;
    }
	public static WTCollection deleteRelationship(WTCollection docs, WTPart part, boolean isRefDoc) throws WTException
	{
		log.debug("start to delete relationship=-->");
		WTCollection objects = new WTArrayList();
		objects.addAll(docs);
		 WTPrincipal userPrincipal=null;
		try {
			userPrincipal = SessionHelper.manager.getPrincipal();
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(!isSiteAdmin(userPrincipal)){
			StringBuffer meBuffer=PartRelationUtil.isDesigner(docs,isRefDoc,part,true);
			meBuffer.append(DocPartRelationUtil.partCheckDatasheet(part, docs,isRefDoc));//PN端移除Datasheet文件校验
			if (meBuffer.length() >0) {
				throw new WTException("！"+meBuffer);
			}
		}
		
		for (Iterator it = docs.persistableIterator(); it.hasNext();)
		{
			if (isRefDoc)
			{
				WTDocumentMaster docMaster = (WTDocumentMaster) it.next();


				String REFERENCES_ROLE_OID = ((WTIntrospector.getLinkInfo(WTPartReferenceLink.class).isRoleA(WTPartReferenceLink.REFERENCES_ROLE))
						? WTPartReferenceLink.ROLE_AOBJECT_REF : WTPartReferenceLink.ROLE_BOBJECT_REF) + "." + ObjectReference.KEY;
				QuerySpec qs = new QuerySpec(WTDocumentMaster.class, WTPartReferenceLink.class);
				qs.appendWhere(new SearchCondition(WTPartReferenceLink.class, REFERENCES_ROLE_OID, SearchCondition.EQUAL, PersistenceHelper.getObjectIdentifier(docMaster)), 1, 1);

				QueryResult qr = PersistenceServerHelper.manager.expand(part, WTPartReferenceLink.REFERENCES_ROLE, qs, false);
				if (qr.size() > 0)
				{
					while (qr.hasMoreElements())
					{
						// delete reference links between parts and docs
						WTPartReferenceLink refLink = (WTPartReferenceLink) qr.nextElement();
						WTDocumentMaster docRef = (WTDocumentMaster) refLink.getRoleBObject();
						objects.remove(docRef);
						PersistenceServerHelper.manager.remove(refLink);
					}
				}
			} else
			{
				WTDocument doc = (WTDocument) it.next();
				// Navigate describe links
				QueryResult qr = StructHelper.service.navigateDescribedBy(part, WTPartDescribeLink.class, false);
				if (qr.size() > 0)
				{
					while (qr.hasMoreElements())
					{
						// delete describe links between parts and docs
						WTPartDescribeLink link = (WTPartDescribeLink) qr.nextElement();
						if (PersistenceHelper.isEquivalent(doc, link.getDescribedBy()))
						{
							WTDocument docRef = (WTDocument) link.getRoleBObject();
							objects.remove(docRef);
							PersistenceServerHelper.manager.remove(link);
							break;
						}
					}
				}
			}
		}
		// Part to Doc actions
		objects.add(part);
		return objects;
	}

	public static WTCollection getDocsForRelation(NmCommandBean nmcommandbean, boolean flag) throws WTException
	{
		WTArrayList wtarraylist = new WTArrayList();
		ArrayList arraylist = nmcommandbean.getSelected();
		boolean flag1 = PartManagementHelper.getWcPDMMethodPref();
		if (arraylist != null && arraylist.size() > 0)
		{
			for (int i = 0; i < arraylist.size(); i++)
			{
				NmContext nmcontext = (NmContext) arraylist.get(i);
				Persistable persistable = nmcontext.getTargetOid().getWtRef().getObject();
				if (persistable == null)
					continue;
				if (persistable instanceof WTDocumentMaster)
				{
					WTDocumentMaster wtdocumentmaster = (WTDocumentMaster) persistable;
					wtarraylist.add(wtdocumentmaster);
					continue;
				}
				if (!(persistable instanceof WTDocument))
					continue;
				WTDocument wtdocument = (WTDocument) persistable;
				if (flag1)
				{
					if (flag)
					{
						WTDocumentMaster wtdocumentmaster1 = (WTDocumentMaster) wtdocument.getMaster();
						wtarraylist.add(wtdocumentmaster1);
					} else
					{
						wtarraylist.add(wtdocument);
					}
					continue;
				}
				if (flag)
				{
					WTDocumentMaster wtdocumentmaster2 = (WTDocumentMaster) wtdocument.getMaster();
					flag = true;
					wtarraylist.add(wtdocumentmaster2);
					continue;
				}
				if (!PartDocHelper.isReferenceDocument(wtdocument))
					wtarraylist.add(wtdocument);
			}
		}
		return wtarraylist;
	}

	@Override
	protected FormResult setRefreshInfo(FormResult result, NmCommandBean cb, List<ObjectBean> objectBeans) throws WTException
	{
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
