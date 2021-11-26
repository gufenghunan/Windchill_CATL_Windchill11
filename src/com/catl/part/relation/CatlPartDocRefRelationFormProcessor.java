package com.catl.part.relation;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.windchill.enterprise.part.forms.PartDocRelationDefaultObjectFormProcessor;
import com.ptc.windchill.enterprise.wip.WIPUtils;

import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.fc.ObjectIdentifier;
import wt.fc.ObjectReference;
import wt.fc.ObjectSetVector;
import wt.fc.PersistInfo;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.fc.collections.WTArrayList;
import wt.fc.collections.WTCollection;
import wt.fc.collections.WTHashSet;
import wt.fc.collections.WTKeyedHashMap;
import wt.fc.collections.WTKeyedMap;
import wt.inf.container.WTContainerHelper;
import wt.org.WTPrincipal;
import wt.part.PartDocHelper;
import wt.part.WTPart;
import wt.part.WTPartDescribeLink;
import wt.part.WTPartReferenceLink;
import wt.pom.Transaction;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.Iterated;
import wt.vc.VersionControlHelper;
import wt.vc.Versioned;

public class CatlPartDocRefRelationFormProcessor extends PartDocRelationDefaultObjectFormProcessor
{

	@Override
	public FormResult doOperation(NmCommandBean clientData, List<ObjectBean> objectList) throws WTException
	{
		WTCollection objects = null;
		Object objOrig = clientData.getPrimaryOid().getWtRef().getObject();
		String action = "relatedPartsReferences";
		FormResult result = new FormResult();
		result.setStatus(FormProcessingStatus.SUCCESS);

		if (objOrig instanceof WTPart)
		{
			WTPart partOrig = (WTPart) objOrig;
			objects = addRelation(clientData, true);

			WTPart part = partOrig;
			for (Iterator it = objects.persistableIterator(); it.hasNext();)
			{
				Object obj = (Object) it.next();
				if (obj instanceof WTPart)
				{
					part = (WTPart) obj;
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

	public static WTCollection addRelation(NmCommandBean nmcommandbean, boolean flag) throws WTException
	{
		Object obj = new WTArrayList();
		Persistable persistable = nmcommandbean.getPrimaryOid().getWtRef().getObject();
		WTCollection wtcollection = CatlPartDocRefDeleteRelationFormProcessor.getDocsForRelation(nmcommandbean, flag);
		if (persistable instanceof WTPart)
		{
			WTPart wtpart = (WTPart) persistable;
			if (wtcollection.size() > 0)
				obj = createRelationship(wtcollection, wtpart, flag);
			else
				((WTCollection) (obj)).add(wtpart);
		}
		return ((WTCollection) (obj));
	}
	
    public static boolean isSiteAdmin(WTPrincipal wtPrincipal) {
        try {
            return WTContainerHelper.service.isAdministrator(WTContainerHelper.service.getExchangeRef(), wtPrincipal);
        } catch (WTException e) {
            e.printStackTrace();
        }
        return false;
    }

	protected static WTCollection createRelationship(WTCollection docs, WTPart part, boolean isRefDoc) throws WTException
	{

		WTCollection objects = new WTArrayList();
		WTCollection links = new WTArrayList();

		part = (WTPart) VersionControlHelper.getLatestIteration((Iterated) part, false);
		 WTPrincipal userPrincipal=null;
			try {
				userPrincipal = SessionHelper.manager.getPrincipal();
			} catch (WTException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(!isSiteAdmin(userPrincipal))
			{
			StringBuffer meBuffer=PartRelationUtil.isDesigner(docs,isRefDoc,part,false);
			if (meBuffer.length() >0) {
				throw new WTException("！"+meBuffer);
			}
			}
		if (isRefDoc)
		{
			links = createPartDocReferenceLinks(part, docs);

		} else
		{
			links = createPartDocDescribeLinks(part, docs);
		}
		// Part to Doc actions
		objects.add(part);
		// add all docs as not added links
		objects.addAll(docs);

		for (Iterator it = links.persistableIterator(); it.hasNext();)
		{
			// remove doc links that were added from the message list
			if (isRefDoc)
			{
				WTPartReferenceLink refLink = (WTPartReferenceLink) it.next();
				WTDocumentMaster docRef = (WTDocumentMaster) refLink.getRoleBObject();
				objects.remove(docRef);
			} else
			{
				WTPartDescribeLink describeLink = (WTPartDescribeLink) it.next();
				WTDocument docRef = (WTDocument) describeLink.getRoleBObject();
				objects.remove(docRef);
			}
		}

		return objects;
	}

	protected static WTCollection createPartDocDescribeLinks(WTPart part, WTCollection documents) throws WTException
	{
		if (!PartDocHelper.isWcPDMMethod())
		{
			documents = removeSmallerVersionForGivenMaster(documents);
		}
		Transaction trx = new Transaction();
		boolean inList = false;
		WTCollection describeLinks = new WTArrayList();
		try
		{
			trx.start();
			for (Iterator<?> it = documents.persistableIterator(); it.hasNext();)
			{
				WTDocument doc = (WTDocument) it.next();
				QueryResult qr = intGetDescribeAssociations(part, (WTDocumentMaster) doc.getMaster());
				if (qr.size() > 0)
				{
					while (qr.hasMoreElements())
					{
						WTPartDescribeLink link = (WTPartDescribeLink) qr.nextElement();
						if (PersistenceHelper.isEquivalent(doc, link.getDescribedBy()))
						{
							inList = true; // Duplicate Link
						} else
						{
							if (!PartDocHelper.isWcPDMMethod())
							{
								//PersistenceHelper.manager.delete(link);
								PersistenceServerHelper.manager.remove(link);
							}
						}
					}
					if (!inList)
					{
						WTPartDescribeLink describelink = WTPartDescribeLink.newWTPartDescribeLink(part, doc);
						PersistenceServerHelper.manager.insert(describelink);
						describelink = (WTPartDescribeLink) PersistenceHelper.manager.refresh(describelink);
						describeLinks.add(describelink);
					}
				} else
				{
					WTPartDescribeLink describelink = WTPartDescribeLink.newWTPartDescribeLink(part, doc);
					PersistenceServerHelper.manager.insert(describelink);
					describelink = (WTPartDescribeLink) PersistenceHelper.manager.refresh(describelink);
					describeLinks.add(describelink);
				}
			}
			trx.commit();
			trx = null;
		} finally
		{
			if (trx != null)
				trx.rollback();
		}
		return describeLinks;
	}

	protected static QueryResult intGetDescribeAssociations(WTPart part, WTDocumentMaster docMaster) throws WTException
	{
		QueryResult result = new QueryResult();

		QuerySpec qs2 = new QuerySpec(WTPartDescribeLink.class);
		qs2.appendClassList(WTDocument.class, true);
		qs2.appendWhere(new SearchCondition(WTPartDescribeLink.class, WTPartDescribeLink.ROLE_AOBJECT_REF + "." + ObjectReference.KEY, SearchCondition.EQUAL,
				PersistenceHelper.getObjectIdentifier(part)), new int[] { 0 });
		qs2.appendAnd();
		qs2.appendWhere(new SearchCondition(WTPartDescribeLink.class, WTPartDescribeLink.ROLE_BOBJECT_REF + "." + ObjectReference.KEY + "." + ObjectIdentifier.ID, WTDocument.class,
				WTDocument.PERSIST_INFO + "." + PersistInfo.OBJECT_IDENTIFIER + "." + ObjectIdentifier.ID), new int[] { 0, 1 });
		qs2.appendAnd();
		qs2.appendWhere(new SearchCondition(WTDocument.class, Iterated.MASTER_REFERENCE + "." + ObjectReference.KEY, SearchCondition.EQUAL,
				PersistenceHelper.getObjectIdentifier(docMaster)), new int[] { 1 });

		QueryResult qr2 = PersistenceHelper.manager.find(qs2);

		Vector v2 = new Vector();
		while (qr2.hasMoreElements())
		{
			Object[] row = (Object[]) qr2.nextElement();
			WTPartDescribeLink link = (WTPartDescribeLink) row[0];
			try
			{
				link.setDescribes(part);
				link.setDescribedBy((WTDocument) row[1]);
			} catch (WTPropertyVetoException wtpve)
			{
				throw new WTException(wtpve);
			}
			v2.add(link);
		}
		result.append(new ObjectSetVector(v2));
		return result;
	}

	private static WTCollection removeSmallerVersionForGivenMaster(WTCollection iterations) throws WTException
	{
		WTKeyedMap masterWithLatestIterationMap = new WTKeyedHashMap();
		Iterator<?> allIterations = iterations.persistableIterator();
		while (allIterations.hasNext())
		{
			Iterated iteration = (Iterated) allIterations.next();
			ObjectReference masterRef = iteration.getMasterReference();
			Iterated putIteration = (Iterated) masterWithLatestIterationMap.get(masterRef);
			if (putIteration == null)
			{
				// This master has come for the first time
				masterWithLatestIterationMap.put(masterRef, iteration);
			} else
			{
				// Need not consider iteration - as the picker lists the latest
				// iteration only for a given branch
				if ((VersionControlHelper.getVersionIdentifier((Versioned) putIteration).getSeries()
						.lessThan(VersionControlHelper.getVersionIdentifier((Versioned) iteration).getSeries())))
				{
					masterWithLatestIterationMap.put(masterRef, iteration);
				}
			}
		}
		return new WTHashSet(masterWithLatestIterationMap.values());
	}

	private static WTCollection createPartDocReferenceLinks(WTPart part, WTCollection documents) throws WTException
	{
		Transaction trx = new Transaction();
		WTCollection referenceLinks = new WTArrayList();
		try
		{
			trx.start();
			for (Iterator it = documents.persistableIterator(); it.hasNext();)
			{
				WTDocumentMaster docMaster = (WTDocumentMaster) it.next();
				QueryResult qr = intGetReferenceAssociations(part, docMaster);
				if (qr.size() > 0)
				{
					// Delete all but the last link.
					for (int i = 0; i < qr.size() - 1; i++)
					{
						PersistenceServerHelper.manager.remove((WTPartReferenceLink) qr.nextElement());
					}
				} else
				{
					WTPartReferenceLink referencelink = WTPartReferenceLink.newWTPartReferenceLink(part, docMaster);
					PersistenceServerHelper.manager.insert(referencelink);
					referencelink = (WTPartReferenceLink) PersistenceHelper.manager.refresh(referencelink);
					referenceLinks.add(referencelink);
				}
			}
			trx.commit();
			trx = null;
		} finally
		{
			if (trx != null)
				trx.rollback();
		}
		return referenceLinks;
	}

	protected static QueryResult intGetReferenceAssociations(WTPart part, WTDocumentMaster docMaster) throws WTException
	{
		QueryResult result = new QueryResult();

		QuerySpec qs1 = new QuerySpec(WTDocumentMaster.class, WTPartReferenceLink.class);
		qs1.appendWhere(new SearchCondition(WTPartReferenceLink.class, WTPartReferenceLink.ROLE_BOBJECT_REF + "." + ObjectReference.KEY, SearchCondition.EQUAL,
				PersistenceHelper.getObjectIdentifier(docMaster)), new int[] { 1 });
		QueryResult qr1 = PersistenceServerHelper.manager.expand(part, WTPartReferenceLink.REFERENCES_ROLE, qs1, false);

		Vector v1 = new Vector();
		while (qr1.hasMoreElements())
		{
			WTPartReferenceLink link = (WTPartReferenceLink) qr1.nextElement();
			v1.add(link);
		}
		result.append(new ObjectSetVector(v1));
		return result;
	}
}
