package com.catl.part.relation;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.catl.common.constant.RoleName;
import com.catl.common.constant.TypeName;
import com.catl.common.util.DocUtil;
import com.catl.doc.processor.CatlDocCreateProcessor;

import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.enterprise.RevisionControlled;
import wt.fc.ObjectIdentifier;
import wt.fc.ObjectReference;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.fc.collections.WTCollection;
import wt.inf.container.WTContainerHelper;
import wt.maturity.PromotionNotice;
import wt.org.WTPrincipal;
import wt.org.WTPrincipalReference;
import wt.part.WTPart;
import wt.part.WTPartDescribeLink;
import wt.part.WTPartReferenceLink;
import wt.pds.StatementSpec;
import wt.project.Role;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionHelper;
import wt.team.Team;
import wt.team.TeamException;
import wt.team.TeamHelper;
import wt.type.TypedUtilityServiceHelper;
import wt.util.WTException;
import wt.util.WTRuntimeException;

public class PartRelationUtil
{
	private static Logger logger=Logger.getLogger(PartRelationUtil.class.getName());
	public void relatePartDoc(WTPart part, List<WTDocument> docList) throws WTException
	{
		if (docList != null)
		{
			for (WTDocument doc : docList)
			{
				if (doc != null)
				{
					String docType = "";
					try
					{
						docType = TypedUtilityServiceHelper.service.getExternalTypeIdentifier(doc);
					} catch (RemoteException e)
					{
						throw new WTException(e);
					}
					logger.debug("docType==="+docType);
					boolean issoft = CatlDocCreateProcessor.isSoftwareDoc(docType);
					if (CatlDocCreateProcessor.isDrawingDoc(docType)||issoft)
					{
						// create describlink
						logger.debug("create describlink=="+part.getNumber()+"---"+doc.getNumber());
						WTPartDescribeLink describelink = WTPartDescribeLink.newWTPartDescribeLink(part, doc);
						PersistenceServerHelper.manager.insert(describelink);
						describelink = (WTPartDescribeLink) PersistenceHelper.manager.refresh(describelink);
						logger.debug("create describlink success!");

					} else{
						WTPartReferenceLink referencelink = WTPartReferenceLink.newWTPartReferenceLink(part, (WTDocumentMaster) doc.getMaster());
						PersistenceServerHelper.manager.insert(referencelink);
						referencelink = (WTPartReferenceLink) PersistenceHelper.manager.refresh(referencelink);
						logger.debug("create ReferenceLink success!");
					}
				}
			}
		}
	}

	
	public static Boolean checkifDesigner(RevisionControlled object2,WTPrincipal user)
	{
		Boolean isdesginerrole=false;
		Role role = Role.toRole(RoleName.DESIGNER);
        Team team2=null;
		try {
			team2 = (Team) TeamHelper.service.getTeam(object2);
			logger.debug("team ==="+team2);
			if (team2!=null) 
			{
            Enumeration enumPrin  = team2.getPrincipalTarget(role);
            logger.debug("design role people==="+enumPrin);
            while (enumPrin.hasMoreElements()) {
            WTPrincipalReference tempPrinRef = (WTPrincipalReference) enumPrin.nextElement();
            WTPrincipal principal2 = tempPrinRef.getPrincipal();
            logger.debug("design role people name==="+principal2.getName());
            if (principal2.getName().equals(user.getName())) {
				isdesginerrole=true;
			}
            }	

			}
		} catch (TeamException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (WTException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
	    return isdesginerrole;
	}
	
	public static StringBuffer isDesigner(WTCollection docs,Boolean isRefDoc,WTPart part,Boolean isdelete) throws WTException
	{
		StringBuffer message=new StringBuffer();
		Boolean isdocdesginerrole=false;
		Boolean ispartdesginerrole=false;
		//get now user
		WTPrincipal userpPrincipal=null;
		try {
			userpPrincipal = SessionHelper.manager.getPrincipal();
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.debug("now user is ==="+userpPrincipal.getName());

 		for (Iterator it = docs.persistableIterator(); it.hasNext();)
 		{
 			WTDocument doc=null;
 			if (isRefDoc) {
 	 			WTDocumentMaster docMaster = (WTDocumentMaster) it.next();
 	 			doc=DocUtil.getLatestWTDocument(docMaster.getNumber());
			}else{
			 doc=(WTDocument) it.next();
			}
 		logger.debug("doc number---"+doc.getNumber());
 		//check doc designer role
		if(!userpPrincipal.getName().endsWith(doc.getCreatorName()))
		{
             isdocdesginerrole=checkifDesigner(doc, userpPrincipal);
	    }else {
			isdocdesginerrole=true;
		}
		if(!isdocdesginerrole){
		//check part desinger role
		if(!userpPrincipal.getName().endsWith(part.getCreatorName()))
		{
             ispartdesginerrole=checkifDesigner(part, userpPrincipal);
	    }else {
			ispartdesginerrole=true;
		}
		}
		if(isdelete)
		 {
          if (!ispartdesginerrole&&!isdocdesginerrole&&!isSiteAdmin(userpPrincipal)) {
			message.append("您不是该文档："+doc.getNumber()+"或部件："+part.getNumber()+"的创建者或设计者，不能进行删除操作！ \n");
		  }
		 }else {
	          if (!ispartdesginerrole&&!isdocdesginerrole&&!isSiteAdmin(userpPrincipal)) {
	  			message.append("您不是该文档："+doc.getNumber()+"或部件："+part.getNumber()+"的创建者或设计者，不能进行添加操作！ \n");
	  		   }
		}
 		}
		return message;
	}
	
    public static boolean isSiteAdmin(WTPrincipal wtPrincipal) {
        try {
            return WTContainerHelper.service.isAdministrator(WTContainerHelper.service.getExchangeRef(), wtPrincipal);
        } catch (WTException e) {
            e.printStackTrace();
        }
        return false;
    }
	
	public void removeRelatePartDoc(WTPart part, List<WTDocument> docList) throws WTException, RemoteException
	{
		if (docList != null)
		{
			for (WTDocument doc : docList)
			{
				if (doc != null)
				{
					String docType = TypedUtilityServiceHelper.service.getExternalTypeIdentifier(doc);
					// check Doc type and remove the link
					if (docType != null && (docType.contains(TypeName.doc_type_autocadDrawing) || docType.contains(TypeName.doc_type_gerberDoc) || docType.contains(TypeName.doc_type_pcbaDrawing)))
					{
						// remove describe link
						WTPartDescribeLink describelink = getPartDescribeLink(part, doc);
						PersistenceServerHelper.manager.remove(describelink);

					} else if (docType != null && docType.indexOf(TypeName.doc_type_technicalDoc) > -1)
					{
						WTPartReferenceLink describelinkOld = null;
						try
						{
							describelinkOld = getPartReferenceLink(part, doc);
						} catch (InvocationTargetException e)
						{
							e.printStackTrace();
						}
						if (describelinkOld != null)
						{
							PersistenceServerHelper.manager.remove(describelinkOld);
						}
					}
				}
			}
		}
	}

	/**
	 * 
	 * Get Describe Link by part and document.
	 * 
	 * @param wtpart
	 *            WTPart : part object
	 * @param wtdocument
	 *            WTDocument : document object
	 * @return WTPartDescribeLink : part and document relationship
	 * @throws WTException
	 *             : exception handling
	 * 
	 * 
	 */
	public WTPartDescribeLink getPartDescribeLink(WTPart wtpart, WTDocument wtdocument) throws WTException
	{
		WTPartDescribeLink partLink = null;
		QueryResult queryresult = PersistenceHelper.manager.find(WTPartDescribeLink.class, wtpart, WTPartDescribeLink.DESCRIBES_ROLE, wtdocument);
		if (queryresult != null && queryresult.size() > 0)
		{
			partLink = (WTPartDescribeLink) queryresult.nextElement();
		}
		return partLink;
	}

	/**
	 * 
	 * Get Reference Link by part and document.
	 * 
	 * @param wtpart
	 * @param wtdocument
	 * @return
	 * @throws WTException
	 * @throws RemoteException
	 * @throws InvocationTargetException
	 */
	public WTPartReferenceLink getPartReferenceLink(WTPart wtpart, WTDocument wtdocument) throws WTException, RemoteException, InvocationTargetException
	{
		int[] index = { 0 };
		QuerySpec querySpec = null;
		querySpec = new QuerySpec(WTPartReferenceLink.class);
		querySpec.appendWhere(new SearchCondition(WTPartReferenceLink.class, WTPartReferenceLink.ROLE_AOBJECT_REF + "." + ObjectReference.KEY, SearchCondition.EQUAL,
				getOid(wtpart)), index);
		querySpec.appendAnd();
		querySpec.appendWhere(new SearchCondition(WTPartReferenceLink.class, WTPartReferenceLink.ROLE_BOBJECT_REF + "." + ObjectReference.KEY, SearchCondition.EQUAL,
				getOid(wtdocument.getMaster())), index);

		QueryResult queryResult = PersistenceHelper.manager.find((StatementSpec) querySpec);
		if (!queryResult.hasMoreElements())
		{
			return null;
		}
		return (WTPartReferenceLink) queryResult.nextElement();
	}

	private ObjectIdentifier getOid(Object object)
	{
		if (object != null && object instanceof Persistable)
		{
			return PersistenceHelper.getObjectIdentifier((Persistable) object);
		} else
		{
			throw new WTRuntimeException("Class not handled: " + object.getClass().getName());
		}
	}

}
