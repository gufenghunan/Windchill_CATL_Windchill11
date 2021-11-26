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
import com.catl.promotion.bean.DesignDisabledAppFormBean;
import com.catl.promotion.bean.DesignDisabledXmlObjectBean;
import com.catl.promotion.util.WorkflowUtil;
import com.catl.promotion.workflow.DesignDisabledExprFunction;
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

public class DesignDisabledXmlObjectUtil {
	public final static String applicationFormOwner = "applicationFormOwner";
	public final static String DesignDisabledAppFormBean = "DesignDisabledAppFormBean";
	public final static String DesignDisabledXmlObjectBean = "DesignDisabledXmlObjectBean";

	private static final Logger LOGGER = LogR.getLogger(DesignDisabledXmlObjectUtil.class.getName());

	public static void initAppForm(WTObject pbo) throws Exception {
		DesignDisabledAppFormBean appFormBean = new DesignDisabledAppFormBean();
		XmlObjectStoreManager storeManager = XWorksHelper.getXmlObjectStoreManager();
		storeManager.save(appFormBean);
		BaseXmlObjectRef ownerRef = BaseXmlObjectRef.newBaseXmlObjectRef(pbo);
		BaseXmlObjectRef afbRef = new BaseXmlObjectRef(appFormBean);
		BaseXmlObjectLink link = new BaseXmlObjectLink(ownerRef, applicationFormOwner, afbRef, DesignDisabledAppFormBean);
		storeManager.save(link);

		updateAppForm(pbo);
	}

	public static void updateAppForm(WTObject pbo) throws Exception {
		List<DesignDisabledAppFormBean> afbs = getDesignDisabledXmlObjectUtil(pbo);
		if (!afbs.isEmpty()) {
			DesignDisabledAppFormBean afb = afbs.get(0);

			XmlObjectStoreManager storeManager = XWorksHelper.getXmlObjectStoreManager();
			List<DesignDisabledXmlObjectBean> xobs = afb.getDesignDisabledXmlObjectBean();

			Map<Long, DesignDisabledXmlObjectBean> deleted = new HashMap<Long, DesignDisabledXmlObjectBean>();
			Set<DesignDisabledXmlObjectBean> notExist = new HashSet<DesignDisabledXmlObjectBean>();
			for (DesignDisabledXmlObjectBean xob : xobs) {
				Long oid = xob.getpartBranchId();
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
					DesignDisabledXmlObjectBean xob = new DesignDisabledXmlObjectBean();
					xob.setpboId(part.getPersistInfo().getObjectIdentifier().getId());
					xob.setpartNumber(part.getNumber());
					xob.setpartName(part.getName());
					xob.setrequestor(pn.getCreatorFullName());
					xob.setpartBranchId(part.getBranchIdentifier());
					xob = (DesignDisabledXmlObjectBean) storeManager.save(xob);

					BaseXmlObjectRef afbRef = new BaseXmlObjectRef(afb);
					BaseXmlObjectRef xobRef = new BaseXmlObjectRef(xob);
					BaseXmlObjectLink link = new BaseXmlObjectLink(afbRef, XmlObjectLink.ROLE_PARENT, xobRef, DesignDisabledXmlObjectBean);
					storeManager.save(link);
				}
			}
			for (Long branchId : deleted.keySet()) {
				storeManager.delete(deleted.get(branchId));
			}
			for (DesignDisabledXmlObjectBean xo : notExist) {
				storeManager.delete(xo);
			}
		}
	}

	public static List<DesignDisabledXmlObjectBean> addDesignDisabledObjects(WTObject pbo, Set<String> soids) throws Exception, WTException {
		List<DesignDisabledAppFormBean> afbs = getDesignDisabledXmlObjectUtil(pbo);
		List<DesignDisabledXmlObjectBean> xobs = getXmlObjectUtil(pbo);

		List<DesignDisabledXmlObjectBean> list = new ArrayList<DesignDisabledXmlObjectBean>();
		Set<String> numbers = new HashSet<String>();
		for (DesignDisabledXmlObjectBean xob : xobs) {
			String number = xob.getpartNumber();
			numbers.add(number);
		}

		WTSet addTargets = new WTHashSet();
		if (!afbs.isEmpty()) {
			DesignDisabledAppFormBean afb = afbs.get(0);
			XmlObjectStoreManager storeManager = XWorksHelper.getXmlObjectStoreManager();

			for (String oid : soids) {
				WTPart part = (WTPart) WorkflowUtil.getObjectByOid(oid);
				
				// 检查部件是否符合添加的要求
				Set<String> msgs = DesignDisabledExprFunction.checkPartForDisable(part);
				if (!msgs.isEmpty()) {
					StringBuilder sb = new StringBuilder();
					for (String msg : msgs) {
						sb.append(msg + "\r\n");
					}
					throw new WTException(sb.toString());
				}

				
				addTargets.add(part);
				String number = part.getNumber();
				if ((!numbers.isEmpty() && !numbers.contains(number)) || numbers.isEmpty()) {
					String type = WorkflowUtil.getTypeInternalName(part);
					if (type.equals(TypeName.CATLPart)) {
						
						DesignDisabledXmlObjectBean xob = new DesignDisabledXmlObjectBean();
						xob.setpboId(part.getPersistInfo().getObjectIdentifier().getId());
						xob.setpartNumber(part.getNumber());
						xob.setpartName(part.getName());
						xob.setpartBranchId(part.getBranchIdentifier());
						xob.setrequestor(((WTUser)SessionHelper.getPrincipal()).getFullName());
						;
						xob = (DesignDisabledXmlObjectBean) storeManager.save(xob);

						BaseXmlObjectRef afbRef = new BaseXmlObjectRef(afb);
						BaseXmlObjectRef xobRef = new BaseXmlObjectRef(xob);
						BaseXmlObjectLink link = new BaseXmlObjectLink(afbRef, XmlObjectLink.ROLE_PARENT, xobRef, DesignDisabledXmlObjectBean);
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

	public static void removeDesignDisabledObject(WTObject pbo, Set<String> soids) throws Exception {
		List<DesignDisabledXmlObjectBean> afbs = getXmlObjectUtil(pbo);

		WTSet rmTargets = new WTHashSet();
		if (!afbs.isEmpty()) {

			XmlObjectStoreManager storeManager = XWorksHelper.getXmlObjectStoreManager();
			for (DesignDisabledXmlObjectBean mafb : afbs) {
				if (mafb != null && soids.contains(mafb.toString())) {
					String id = "VR:wt.part.WTPart:" + mafb.getpartBranchId().toString();
					WTPart part = (WTPart) WorkflowUtil.getObjectByOid(id);
					rmTargets.add(part);
					storeManager.delete(mafb);
				}
			}
		}
		BomWfUtil.removeTargets((PromotionNotice) pbo, rmTargets);
	}

	@SuppressWarnings("rawtypes")
	public static List<DesignDisabledXmlObjectBean> pasteDesignDisabledObject(WTObject pbo, ArrayList list) throws Exception {
		List<DesignDisabledAppFormBean> afbs = getDesignDisabledXmlObjectUtil(pbo);
		List<DesignDisabledXmlObjectBean> xobs = getXmlObjectUtil(pbo);

		List<DesignDisabledXmlObjectBean> lists = new ArrayList<DesignDisabledXmlObjectBean>();
		Set<String> numbers = new HashSet<String>();
		for (DesignDisabledXmlObjectBean xob : xobs) {
			String number = xob.getpartNumber();
			numbers.add(number);
		}

		WTSet addTargets = new WTHashSet();
		if (!afbs.isEmpty()) {
			DesignDisabledAppFormBean afb = afbs.get(0);
			XmlObjectStoreManager storeManager = XWorksHelper.getXmlObjectStoreManager();

			for (Object obj : list) {
				String oid = ((NmOid) obj).toString();
				WTPart part = (WTPart) WorkflowUtil.getObjectByOid(oid);
				
				// 检查部件是否符合添加的要求
				Set<String> msgs = DesignDisabledExprFunction.checkPartForDisable(part);
				if (!msgs.isEmpty()) {
					continue;
				}
				
				addTargets.add(part);
				String number = part.getNumber();
				if ((!numbers.isEmpty() && !numbers.contains(number)) || numbers.isEmpty()) {
					String type = WorkflowUtil.getTypeInternalName(part);
					if (type.equals(TypeName.CATLPart)) {
						DesignDisabledXmlObjectBean xob = new DesignDisabledXmlObjectBean();
						xob.setpboId(part.getPersistInfo().getObjectIdentifier().getId());
						xob.setpartNumber(part.getNumber());
						xob.setpartName(part.getName());
						xob.setpartBranchId(part.getBranchIdentifier());
						;
						xob = (DesignDisabledXmlObjectBean) storeManager.save(xob);

						BaseXmlObjectRef afbRef = new BaseXmlObjectRef(afb);
						BaseXmlObjectRef xobRef = new BaseXmlObjectRef(xob);
						BaseXmlObjectLink link = new BaseXmlObjectLink(afbRef, XmlObjectLink.ROLE_PARENT, xobRef, DesignDisabledXmlObjectBean);
						storeManager.save(link);
						lists.add(xob);
					}
				}
			}
		}
		BomWfUtil.addToTargets((PromotionNotice) pbo, addTargets);

		return lists;
	}

	public static List<DesignDisabledAppFormBean> getDesignDisabledXmlObjectUtil(WTObject pbo) throws WTException {
		try {
			XmlObjectStoreManager storeManager = XWorksHelper.getXmlObjectStoreManager();
			XmlObjectIdentifier ownerOid = XmlObjectIdentifier.newXmlObjectIdentifier(pbo);
			List<XmlObject> xmlObjs = storeManager.navigate(ownerOid, DesignDisabledAppFormBean);

			List<DesignDisabledAppFormBean> result = new ArrayList<DesignDisabledAppFormBean>();
			for (XmlObject xmlObj : xmlObjs) {
				xmlObj = storeManager.load(xmlObj.getIdentifier(), LoadOption.ALL_CHILDS);
				result.add((DesignDisabledAppFormBean) xmlObj);
			}
			return result;
		} catch (XmlObjectStoreException e) {
			LOGGER.error("XmlObjectStoreException occured when invoking getReagentRawItemReviewAppFormBean", e);
			throw new WTException(e);
		}
	}

	public static List<DesignDisabledXmlObjectBean> getXmlObjectUtil(WTObject pbo) throws WTException {
		try {
			XmlObjectStoreManager storeManager = XWorksHelper.getXmlObjectStoreManager();
			XmlObjectIdentifier ownerOid = XmlObjectIdentifier.newXmlObjectIdentifier(pbo);
			List<XmlObject> xmlObjs = storeManager.navigate(ownerOid, DesignDisabledAppFormBean);

			List<DesignDisabledXmlObjectBean> result = new ArrayList<DesignDisabledXmlObjectBean>();
			for (XmlObject xmlObj : xmlObjs) {
				List<XmlObject> Objs = storeManager.navigate(xmlObj, DesignDisabledXmlObjectBean);
				for (XmlObject obj : Objs) {
					obj = storeManager.load(obj.getIdentifier(), LoadOption.ALL_CHILDS);
					result.add((DesignDisabledXmlObjectBean) obj);
				}
			}
			return result;
		} catch (XmlObjectStoreException e) {
			LOGGER.error("XmlObjectStoreException occured when invoking getReagentRawItemReviewAppFormBean", e);
			throw new WTException(e);
		}
	}
}
