package com.catl.promotion.dbs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import wt.fc.WTObject;
import wt.fc.collections.WTHashSet;
import wt.fc.collections.WTSet;
import wt.log4j.LogR;
import wt.maturity.PromotionNotice;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.session.SessionHelper;
import wt.util.WTException;

import com.catl.bom.workflow.BomWfUtil;
import com.catl.common.constant.TypeName;
import com.catl.promotion.bean.SourceChangeAppFormBean;
import com.catl.promotion.bean.SourceChangeXmlObjectBean;
import com.catl.promotion.util.PromotionUtil;
import com.catl.promotion.util.WorkflowUtil;
import com.catl.promotion.workflow.SourceChangeExprFunction;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.xworks.util.XWorksHelper;
import com.ptc.xworks.xmlobject.BaseXmlObjectLink;
import com.ptc.xworks.xmlobject.BaseXmlObjectRef;
import com.ptc.xworks.xmlobject.XmlObject;
import com.ptc.xworks.xmlobject.XmlObjectIdentifier;
import com.ptc.xworks.xmlobject.XmlObjectLink;
import com.ptc.xworks.xmlobject.store.StoreOptions.LoadOption;
import com.ptc.xworks.xmlobject.store.XmlObjectStoreException;
import com.ptc.xworks.xmlobject.store.XmlObjectStoreManager;

public class SourceChangeXmlObjectUtil {
	public final static String applicationFormOwner = "applicationFormOwner";
	public final static String SourceChangeAppFormBean = "SourceChangeAppFormBean";
	public final static String SourceChangeXmlObjectBean = "SourceChangeXmlObjectBean";

	private static final Logger LOGGER = LogR.getLogger(SourceChangeXmlObjectUtil.class.getName());

	public static void initAppForm(WTObject pbo) throws Exception {
		SourceChangeAppFormBean appFormBean = new SourceChangeAppFormBean();
		XmlObjectStoreManager storeManager = XWorksHelper.getXmlObjectStoreManager();
		storeManager.save(appFormBean);
		BaseXmlObjectRef ownerRef = BaseXmlObjectRef.newBaseXmlObjectRef(pbo);
		BaseXmlObjectRef afbRef = new BaseXmlObjectRef(appFormBean);
		BaseXmlObjectLink link = new BaseXmlObjectLink(ownerRef, applicationFormOwner, afbRef, SourceChangeAppFormBean);
		storeManager.save(link);

		updateAppForm(pbo);
	}

	public static void updateAppForm(WTObject pbo) throws Exception {
		List<SourceChangeAppFormBean> afbs = getSourceChangeXmlObjectList(pbo);
		if (!afbs.isEmpty()) {
			SourceChangeAppFormBean afb = afbs.get(0);

			XmlObjectStoreManager storeManager = XWorksHelper.getXmlObjectStoreManager();
			List<SourceChangeXmlObjectBean> xobs = afb.getSourceChangeXmlObjectList();

			Map<Long, SourceChangeXmlObjectBean> deleted = new HashMap<Long, SourceChangeXmlObjectBean>();
			Set<SourceChangeXmlObjectBean> notExist = new HashSet<SourceChangeXmlObjectBean>();
			for (SourceChangeXmlObjectBean xob : xobs) {
				Long oid = xob.getPartBranchId();
				if (oid != null) {
					deleted.put(oid, xob);
				} else {
					notExist.add(xob);
				}
			}

			PromotionNotice pn = (PromotionNotice) pbo;
			Set<WTPart> pos = BomWfUtil.getTargets(pn);
			for (WTPart part : pos) {
				String type = WorkflowUtil.getTypeInternalName(part);
				Long branchId = Long.valueOf(part.getBranchIdentifier());
				if (deleted.containsKey(branchId)) {
					deleted.remove(branchId);
				} else if (type.equals(TypeName.CATLPart)) {
					SourceChangeXmlObjectBean xob = new SourceChangeXmlObjectBean();
					xob.setPboId(part.getPersistInfo().getObjectIdentifier().getId());
					xob.setPartNumber(part.getNumber());
					xob.setPartName(part.getName());
					xob.setPartCreator(part.getCreatorFullName());
					xob.setPartModifier(part.getModifierFullName());
					xob.setPartBranchId(part.getBranchIdentifier());
					xob.setSourceBefore(part.getSource().toString());
					xob = (SourceChangeXmlObjectBean) storeManager.save(xob);

					BaseXmlObjectRef afbRef = new BaseXmlObjectRef(afb);
					BaseXmlObjectRef xobRef = new BaseXmlObjectRef(xob);
					BaseXmlObjectLink link = new BaseXmlObjectLink(afbRef, XmlObjectLink.ROLE_PARENT, xobRef, SourceChangeXmlObjectBean);
					storeManager.save(link);
				}
			}
			for (Long branchId : deleted.keySet()) {
				storeManager.delete(deleted.get(branchId));
			}
			for (SourceChangeXmlObjectBean xo : notExist) {
				storeManager.delete(xo);
			}
		}
	}

	public static List<SourceChangeXmlObjectBean> addSourceChangeObjects(WTObject pbo, Set<String> soids) throws Exception, WTException {
		List<SourceChangeAppFormBean> afbs = getSourceChangeXmlObjectList(pbo);
		List<SourceChangeXmlObjectBean> xobs = getXmlObjectUtil(pbo);

		List<SourceChangeXmlObjectBean> list = new ArrayList<SourceChangeXmlObjectBean>();
		Set<String> numbers = new HashSet<String>();
		for (SourceChangeXmlObjectBean xob : xobs) {
			String number = xob.getPartNumber();
			numbers.add(number);
		}

		WTSet addTargets = new WTHashSet();
		if (!afbs.isEmpty()) {
			SourceChangeAppFormBean afb = afbs.get(0);
			XmlObjectStoreManager storeManager = XWorksHelper.getXmlObjectStoreManager();

			for (String oid : soids) {
				WTPart part = (WTPart) WorkflowUtil.getObjectByOid(oid);
				
				// 检查部件是否符合添加的要求
				StringBuffer msgs = PromotionUtil.checkSourceChangeObjecs((PromotionNotice)pbo,part,null);
				if (msgs.length() > 0) {
					throw new WTException(msgs.toString());
				}
				
				addTargets.add(part);
				String number = part.getNumber();
				if ((!numbers.isEmpty() && !numbers.contains(number)) || numbers.isEmpty()) {
					String type = WorkflowUtil.getTypeInternalName(part);
					if (type.equals(TypeName.CATLPart)) {
						
						SourceChangeXmlObjectBean xob = new SourceChangeXmlObjectBean();
						xob.setPboId(part.getPersistInfo().getObjectIdentifier().getId());
						xob.setPartNumber(part.getNumber());
						xob.setPartName(part.getName());
						xob.setPartBranchId(part.getBranchIdentifier());
						xob.setPartCreator(part.getCreatorFullName());
						xob.setPartModifier(part.getModifierFullName());
						xob.setSourceBefore(part.getSource().toString());
						xob = (SourceChangeXmlObjectBean) storeManager.save(xob);

						BaseXmlObjectRef afbRef = new BaseXmlObjectRef(afb);
						BaseXmlObjectRef xobRef = new BaseXmlObjectRef(xob);
						BaseXmlObjectLink link = new BaseXmlObjectLink(afbRef, XmlObjectLink.ROLE_PARENT, xobRef, SourceChangeXmlObjectBean);
						storeManager.save(link);
						list.add(xob);
					}
				} else {
					throw new WTException("零部件" + number + "已经加入!");
				}
			}
		}
		BomWfUtil.addToTargets((PromotionNotice) pbo, addTargets);

		return list;
	}

	public static void removeSourceChangeObject(WTObject pbo, Set<String> soids) throws Exception {
		List<SourceChangeXmlObjectBean> afbs = getXmlObjectUtil(pbo);

		WTSet rmTargets = new WTHashSet();
		if (!afbs.isEmpty()) {

			XmlObjectStoreManager storeManager = XWorksHelper.getXmlObjectStoreManager();
			for (SourceChangeXmlObjectBean mafb : afbs) {
				if (mafb != null && soids.contains(mafb.toString())) {
					String id = "VR:wt.part.WTPart:" + mafb.getPartBranchId().toString();
					WTPart part = (WTPart) WorkflowUtil.getObjectByOid(id);
					rmTargets.add(part);
					storeManager.delete(mafb);
				}
			}
		}
		BomWfUtil.removeTargets((PromotionNotice) pbo, rmTargets);
	}

	@SuppressWarnings("rawtypes")
	public static List<SourceChangeXmlObjectBean> pasteSourceChangeObject(WTObject pbo, ArrayList list) throws Exception {
		List<SourceChangeAppFormBean> afbs = getSourceChangeXmlObjectList(pbo);
		List<SourceChangeXmlObjectBean> xobs = getXmlObjectUtil(pbo);

		List<SourceChangeXmlObjectBean> lists = new ArrayList<SourceChangeXmlObjectBean>();
		Set<String> numbers = new HashSet<String>();
		for (SourceChangeXmlObjectBean xob : xobs) {
			String number = xob.getPartNumber();
			numbers.add(number);
		}

		WTSet addTargets = new WTHashSet();
		if (!afbs.isEmpty()) {
			SourceChangeAppFormBean afb = afbs.get(0);
			XmlObjectStoreManager storeManager = XWorksHelper.getXmlObjectStoreManager();

			for (Object obj : list) {
				String oid = ((NmOid) obj).toString();
				WTPart part = (WTPart) WorkflowUtil.getObjectByOid(oid);
				
				// 检查部件是否符合添加的要求
				StringBuffer msgs = PromotionUtil.checkSourceChangeObjecs((PromotionNotice)pbo,part,null);
				if (msgs.length() > 0) {
					continue;
				}
				
				addTargets.add(part);
				String number = part.getNumber();
				if ((!numbers.isEmpty() && !numbers.contains(number)) || numbers.isEmpty()) {
					String type = WorkflowUtil.getTypeInternalName(part);
					if (type.equals(TypeName.CATLPart)) {
						SourceChangeXmlObjectBean xob = new SourceChangeXmlObjectBean();
						xob.setPboId(part.getPersistInfo().getObjectIdentifier().getId());
						xob.setPartNumber(part.getNumber());
						xob.setPartName(part.getName());
						xob.setPartBranchId(part.getBranchIdentifier());
						xob.setPartCreator(part.getCreatorFullName());
						xob.setPartModifier(part.getModifierFullName());
						xob.setSourceBefore(part.getSource().toString());
						xob = (SourceChangeXmlObjectBean) storeManager.save(xob);

						BaseXmlObjectRef afbRef = new BaseXmlObjectRef(afb);
						BaseXmlObjectRef xobRef = new BaseXmlObjectRef(xob);
						BaseXmlObjectLink link = new BaseXmlObjectLink(afbRef, XmlObjectLink.ROLE_PARENT, xobRef, SourceChangeXmlObjectBean);
						storeManager.save(link);
						lists.add(xob);
					}
				}
			}
		}
		BomWfUtil.addToTargets((PromotionNotice) pbo, addTargets);

		return lists;
	}

	public static List<SourceChangeAppFormBean> getSourceChangeXmlObjectList(WTObject pbo) throws WTException {
		try {
			XmlObjectStoreManager storeManager = XWorksHelper.getXmlObjectStoreManager();
			XmlObjectIdentifier ownerOid = XmlObjectIdentifier.newXmlObjectIdentifier(pbo);
			List<XmlObject> xmlObjs = storeManager.navigate(ownerOid, SourceChangeAppFormBean);

			List<SourceChangeAppFormBean> result = new ArrayList<SourceChangeAppFormBean>();
			for (XmlObject xmlObj : xmlObjs) {
				xmlObj = storeManager.load(xmlObj.getIdentifier(), LoadOption.ALL_CHILDS);
				result.add((SourceChangeAppFormBean) xmlObj);
			}
			return result;
		} catch (XmlObjectStoreException e) {
			LOGGER.error("XmlObjectStoreException occured when invoking getReagentRawItemReviewAppFormBean", e);
			throw new WTException(e);
		}
	}

	public static List<SourceChangeXmlObjectBean> getXmlObjectUtil(WTObject pbo) throws WTException {
		try {
			XmlObjectStoreManager storeManager = XWorksHelper.getXmlObjectStoreManager();
			XmlObjectIdentifier ownerOid = XmlObjectIdentifier.newXmlObjectIdentifier(pbo);
			List<XmlObject> xmlObjs = storeManager.navigate(ownerOid, SourceChangeAppFormBean);

			List<SourceChangeXmlObjectBean> result = new ArrayList<SourceChangeXmlObjectBean>();
			for (XmlObject xmlObj : xmlObjs) {
				List<XmlObject> Objs = storeManager.navigate(xmlObj, SourceChangeXmlObjectBean);
				for (XmlObject obj : Objs) {
					obj = storeManager.load(obj.getIdentifier(), LoadOption.ALL_CHILDS);
					result.add((SourceChangeXmlObjectBean) obj);
				}
			}
			return result;
		} catch (XmlObjectStoreException e) {
			LOGGER.error("XmlObjectStoreException occured when invoking getReagentRawItemReviewAppFormBean", e);
			throw new WTException(e);
		}
	}
}
