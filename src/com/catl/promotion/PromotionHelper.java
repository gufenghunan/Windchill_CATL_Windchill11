package com.catl.promotion;

import com.catl.promotion.processor.MagnificationProcessor;
import com.catl.promotion.processor.MaintenancePartSourceProcessor;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.model.NmSimpleOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.netmarkets.util.beans.NmURLFactoryBean;
import com.ptc.netmarkets.util.misc.NetmarketURL;

import java.sql.Timestamp;
import java.util.Vector;

import wt.doc.WTDocument;
import wt.fc.ObjectVector;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.fc.WTObject;
import wt.fc.collections.WTHashSet;
import wt.fc.collections.WTSet;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerRef;
import wt.maturity.MaturityBaseline;
import wt.maturity.MaturityException;
import wt.maturity.MaturityHelper;
import wt.maturity.Promotable;
import wt.maturity.PromotionNotice;
import wt.maturity.PromotionTarget;
import wt.part.WTPart;
import wt.query.ClassAttribute;
import wt.query.ConstantExpression;
import wt.query.QuerySpec;
import wt.query.RelationalExpression;
import wt.query.SearchCondition;
import wt.util.WTAttributeNameIfc;
import wt.util.WTException;
import wt.util.WTInvalidParameterException;
import wt.util.WTPropertyVetoException;
import wt.vc.baseline.BaselineHelper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;

import wt.fc.collections.WTArrayList;
import wt.fc.collections.WTKeyedHashMap;
import wt.log4j.LogR;
import wt.method.RemoteAccess;

public class PromotionHelper implements Serializable, RemoteAccess
{

	private static final Logger logger;
	private static final long serialVersionUID = 1L;
	private static final String CLASSNAME = PromotionHelper.class.getName();
	public static PromotionNotice promotionNotice = null;

	static
	{
		try
		{
			logger = LogR.getLogger(PromotionHelper.class.getName());
		} catch (Exception exception)
		{
			throw new ExceptionInInitializerError(exception);
		}
	}

	/**
	 * 
	 * 
	 * @return
	 * @throws MaturityException
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 * @throws WTInvalidParameterException
	 */
	public static PromotionNotice newPromotionNotice(String promotionName, String promotionDesc, WTContainerRef containerRef) throws Exception
	{
		MaturityBaseline partBaseline = MaturityBaseline.newMaturityBaseline();
		String location = "/Default";
		partBaseline.setContainerReference(containerRef);
		PromotionNotice notice = PromotionNotice.newPromotionNotice(promotionName);

		String description = promotionDesc;
		Timestamp pDate = new Timestamp(System.currentTimeMillis() + 400000000);

		notice.setPromotionDate(pDate);
		notice.setContainer((WTContainer) containerRef.getObject());
		notice.setContainerReference(containerRef);
		notice.setDescription(description);
		partBaseline = (MaturityBaseline) PersistenceHelper.manager.save(partBaseline);
		notice.setConfiguration(partBaseline);
		notice = MaturityHelper.service.savePromotionNotice(notice);
		notice = (PromotionNotice) PersistenceHelper.manager.refresh(notice);
		return notice;
	}

	public static void setPromotionNotice(PromotionNotice pn)
	{
		promotionNotice = pn;
	}

	public static QueryResult getPromotable(PromotionNotice promotionNotice) throws MaturityException, WTException
	{
		return MaturityHelper.service.getPromotionTargets(promotionNotice);
	}

	public static PromotionNotice addPromotable(PromotionNotice promotionNotice, Promotable promotable) throws WTException, WTPropertyVetoException
	{
		WTSet promotableSet = new WTHashSet(1);
		Vector seedVec = new Vector();
		MaturityBaseline partBaseline = null;

		seedVec.add(promotable);

		partBaseline = promotionNotice.getConfiguration();
		partBaseline = (MaturityBaseline) BaselineHelper.service.addToBaseline(seedVec, partBaseline);
		promotionNotice.setConfiguration(partBaseline);
		PersistenceHelper.manager.save(promotionNotice);

		promotableSet.add(promotable);
		MaturityHelper.service.savePromotionTargets(promotionNotice, promotableSet);
		promotionNotice = (PromotionNotice) PersistenceHelper.manager.refresh(promotionNotice);
		return promotionNotice;
	}

	public static PromotionNotice removePromotable(PromotionNotice promotionNotice, Promotable promotable) throws WTException, WTPropertyVetoException
	{
		WTSet promotableSet = new WTHashSet(1);
		Vector seedVec = new Vector();
		MaturityBaseline partBaseline = null;

		seedVec.add(promotable);

		partBaseline = promotionNotice.getConfiguration();
		partBaseline = (MaturityBaseline) BaselineHelper.service.removeFromBaseline(seedVec, partBaseline);
		promotionNotice.setConfiguration(partBaseline);
		PersistenceHelper.manager.save(promotionNotice);

		promotableSet.add(promotable);
		MaturityHelper.service.deletePromotionTargets(promotionNotice, promotableSet);
		promotionNotice = (PromotionNotice) PersistenceHelper.manager.refresh(promotionNotice);
		return promotionNotice;
	}

	public static PromotionNotice removePromotable(PromotionNotice promotionNotice) throws WTException, WTPropertyVetoException
	{
		WTSet promotableSet = new WTHashSet(1);
		Vector seedVec = new Vector();
		MaturityBaseline partBaseline = null;

		QueryResult promotionqr = MaturityHelper.service.getPromotionTargets(promotionNotice);
		if (promotionqr != null)
		{
			while (promotionqr.hasMoreElements())
			{
				Promotable promotable = (Promotable) promotionqr.nextElement();
				seedVec.add(promotable);
				promotableSet.add(promotable);
			}

			partBaseline = promotionNotice.getConfiguration();
			partBaseline = (MaturityBaseline) BaselineHelper.service.removeFromBaseline(seedVec, partBaseline);
			promotionNotice.setConfiguration(partBaseline);
			// helay by modify
			// PersistenceHelper.manager.save(promotionNotice);

			MaturityHelper.service.deletePromotionTargets(promotionNotice, promotableSet);
		}
		promotionNotice = (PromotionNotice) PersistenceHelper.manager.refresh(promotionNotice);
		return promotionNotice;

	}

	public static PromotionNotice modifyPromotionNotice(PromotionNotice notice, String promotionDesc) throws WTPropertyVetoException, MaturityException, WTException
	{
		String description = promotionDesc;
		Timestamp pDate = new Timestamp(System.currentTimeMillis() + 400000000);

		notice.setPromotionDate(pDate);
		notice.setDescription(description);

		notice = MaturityHelper.service.savePromotionNotice(notice);
		PersistenceHelper.manager.save(notice);
		notice = (PromotionNotice) PersistenceHelper.manager.refresh(notice);
		return notice;
	}

	public static QueryResult getPromotionNoticeByPromotable(WTObject wtobject) throws WTException
	{
		String targetSubNumber = "";
		if (wtobject instanceof WTDocument)
		{
			targetSubNumber = ((WTDocument) wtobject).getNumber();
		} else if (wtobject instanceof WTPart)
		{
			targetSubNumber = ((WTPart) wtobject).getNumber();
		}

		QueryResult result = new QueryResult();
		Persistable object = null;
		Vector vct = new Vector();

		QueryResult rs = PersistenceHelper.manager.find(makeSpecOfPromotionNoticeByPromotable(targetSubNumber, false, true, false));
		while (rs.hasMoreElements())
		{
			Persistable[] oneResult = (Persistable[]) rs.nextElement();
			object = oneResult[0];

			if (object instanceof PromotionNotice)
			{
				if (!vct.contains((PromotionNotice) object))
				{
					PromotionNotice promotion = (PromotionNotice) object;
					vct.add(promotion);
				}
			}
		}
		result.append(new ObjectVector(vct));

		return result;
	}

	public static QueryResult getPromotionNotionByWTObject(WTObject object) throws WTException
	{

		QuerySpec qs = new QuerySpec(PromotionNotice.class, PromotionTarget.class);

		QueryResult result = PersistenceHelper.manager.navigate((Promotable) object, PromotionTarget.PROMOTION_NOTICE_ROLE, qs, true);

		return result;
	}

	private static QuerySpec makeSpecOfPromotionNoticeByPromotable(String targetSubNumber, boolean promotableGetFlag, boolean noticeGetFlag, boolean targetLinkGetFlag)
			throws WTException
	{
		QuerySpec querySpec = new QuerySpec();
		int linkIndex = 0;
		SearchCondition condition = null;
		ClassAttribute classAttribute = null;
		RelationalExpression expression = null;

		if (targetSubNumber == null || targetSubNumber.equals(""))
		{
			return null;
		}
		int classIndex0 = querySpec.appendClassList(Promotable.class, promotableGetFlag);
		int classIndex1 = querySpec.appendClassList(PromotionNotice.class, noticeGetFlag);
		int classIndex2 = querySpec.appendClassList(PromotionTarget.class, targetLinkGetFlag);

		querySpec.appendOpenParen();
		condition = new SearchCondition(Promotable.class, WTAttributeNameIfc.ID_NAME, PromotionTarget.class, WTAttributeNameIfc.ROLEB_OBJECT_ID);
		querySpec.appendWhere(condition, classIndex0, classIndex2);
		querySpec.appendAnd();
		condition = new SearchCondition(PromotionNotice.class, WTAttributeNameIfc.ID_NAME, PromotionTarget.class, WTAttributeNameIfc.ROLEA_OBJECT_ID);
		querySpec.appendWhere(condition, classIndex1, classIndex2);

		querySpec.appendCloseParen();

		querySpec.appendAnd();

		classAttribute = new ClassAttribute(WTPart.class, WTPart.NUMBER);
		expression = ConstantExpression.newExpression(targetSubNumber, classAttribute.getColumnDescriptor().getJavaType());
		condition = new SearchCondition(classAttribute, SearchCondition.EQUAL, expression);
		querySpec.appendWhere(condition, classIndex0);

		return querySpec;
	}

	public static ArrayList getPromotionNotices(NmCommandBean nmcommandbean) throws WTException
	{

		ArrayList list = new ArrayList();
		Promotable promotable = null;
		System.out.println(CLASSNAME + "--> getPromotionNotices : nmcommandbean is '" + nmcommandbean + "'");
		System.out.println(CLASSNAME + "--> getPromotionNotices : nmcommandbean.getActionOid() is '" + nmcommandbean.getActionOid() + "'");
		if (nmcommandbean != null && nmcommandbean.getActionOid() != null)
		{
			NmOid nmoid = nmcommandbean.getActionOid();
			System.out.println(CLASSNAME + "--> getPromotionNotices : (nmoid instanceof NmSimpleOid) is '" + (nmoid instanceof NmSimpleOid) + "'");
			Object obj = nmoid.getRefObject();
			System.out.println(CLASSNAME + "--> getPromotionNotices : nmoid.getRefObject() is '" + obj + "'");
			if (obj == null || !(obj instanceof Promotable))
			{
				return list;
			}
			promotable = (Promotable) obj;
		}

		try
		{
			WTHashSet wthashset = new WTHashSet();
			wthashset.addElement(promotable);
			WTKeyedHashMap wtkeyedhashmap = MaturityHelper.service.getTargetPromotionNotices(wthashset);
			Iterator iterator = wtkeyedhashmap.wtKeySet().persistableIterator();
			Promotable promotableTemp = null;
			while (iterator.hasNext())
			{
				promotableTemp = (Promotable) iterator.next();
				WTArrayList wtarraylist = (WTArrayList) wtkeyedhashmap.get(promotableTemp);
				Iterator iterator1 = wtarraylist.persistableIterator();
				PromotionNotice promotionNoticeTemp = null;
				if (iterator1.hasNext())
				{
					PromotionTarget promotiontarget = (PromotionTarget) iterator1.next();
					promotionNoticeTemp = (PromotionNotice) promotiontarget.getRoleAObject();
				}
				iterator1 = null;
				wtarraylist = null;
				if (promotionNoticeTemp != null)
				{
					list.add(promotionNoticeTemp);
				}
			}
			iterator = null;
			wtkeyedhashmap = null;
			wthashset = null;
		} catch (WTException e)
		{
			e.printStackTrace();
			throw e;
		}
		return list;
	}

	public static void main(String[] args) throws Exception
	{
		String oid = "VR:wt.part.WTPart:58997";
		ReferenceFactory rf = new ReferenceFactory();

		WTObject object = (WTObject) rf.getReference(oid).getObject();
		System.out.println("object==" + object);
		QueryResult rs = getPromotionNotionByWTObject(object);
		while (rs.hasMoreElements())
		{
			PromotionNotice pn = (PromotionNotice) rs.nextElement();
			System.out.println("pn=" + pn.getName());
		}
	}
	
	public static String buildMaintenancePartSourceUrl(WTObject obj) {
		try {
			if(obj instanceof PromotionNotice){
				PromotionNotice pn = (PromotionNotice)obj;
				NmURLFactoryBean urlFactoryBean = new NmURLFactoryBean();
		        urlFactoryBean.setRequestURI(NetmarketURL.BASEURL);
		         
		        HashMap<String, Object> params = new HashMap<String, Object>();
		        params.put("wizardActionClass", MaintenancePartSourceProcessor.class.getName());
		        params.put("wizardActionMethod", "execute");
		        params.put("actionName", "MaintenancePartSource");
		        params.put("portlet", "poppedup");
		        String url = NetmarketURL.buildURL(urlFactoryBean, "CatlPromotion", "MaintenancePartSource", new NmOid(pn),
		                    params, true);
		        return "<a href=\"" + url + "\" target=\"_blank\"><font class=\"wizardlabel\"  size='4'><b>维护源信息</b></font></a><br>";
			}
        } catch (WTException e) {
        	logger.error(e);
        }
        return "";
    }
	
	public static String buildMagnificationUrl(WTObject obj) {
		try {
			if(obj instanceof PromotionNotice){
				PromotionNotice pn = (PromotionNotice)obj;
				NmURLFactoryBean urlFactoryBean = new NmURLFactoryBean();
		        urlFactoryBean.setRequestURI(NetmarketURL.BASEURL);
		         
		        HashMap<String, Object> params = new HashMap<String, Object>();
		        params.put("wizardActionClass", MagnificationProcessor.class.getName());
		        params.put("wizardActionMethod", "execute");
		        params.put("actionName", "Magnification");
		        params.put("portlet", "poppedup");
		        String url = NetmarketURL.buildURL(urlFactoryBean, "CatlPromotion", "Magnification", new NmOid(pn),
		                    params, true);
		        return "<a href=\"" + url + "\" target=\"_blank\"><font class=\"wizardlabel\"  size='4'><b>维护放大倍数</b></font></a><br>";
			}
        } catch (WTException e) {
        	logger.error(e);
        }
        return "";
    }
}
