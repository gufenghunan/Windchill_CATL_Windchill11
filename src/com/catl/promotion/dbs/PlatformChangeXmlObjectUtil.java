package com.catl.promotion.dbs;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.drools.core.util.StringUtils;

import wt.fc.WTObject;
import wt.fc.collections.WTHashSet;
import wt.fc.collections.WTSet;
import wt.log4j.LogR;
import wt.maturity.PromotionNotice;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.catl.bom.workflow.BomWfUtil;
import com.catl.common.constant.TypeName;
import com.catl.ecad.utils.CommonUtil;
import com.catl.line.util.IBAUtil;
import com.catl.line.util.IBAUtility;
import com.catl.line.util.WCUtil;
import com.catl.promotion.bean.PlatformChangeAppFormBean;
import com.catl.promotion.bean.PlatformChangeXmlObjectBean;
import com.catl.promotion.util.PromotionUtil;
import com.catl.promotion.util.WorkflowUtil;
import com.catl.require.constant.ConstantRequire;
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

public class PlatformChangeXmlObjectUtil implements RemoteAccess{
	public final static String applicationFormOwner = "applicationFormOwner";
	public final static String PlatformChangeAppFormBean = "PlatformChangeAppFormBean";
	public final static String PlatformChangeXmlObjectBean = "PlatformChangeXmlObjectBean";

	private static final Logger LOGGER = LogR.getLogger(PlatformChangeXmlObjectUtil.class.getName());

	public static void initAppForm(WTObject pbo) throws Exception {
		PlatformChangeAppFormBean appFormBean = new PlatformChangeAppFormBean();
		XmlObjectStoreManager storeManager = XWorksHelper.getXmlObjectStoreManager();
		storeManager.save(appFormBean);
		BaseXmlObjectRef ownerRef = BaseXmlObjectRef.newBaseXmlObjectRef(pbo);
		BaseXmlObjectRef afbRef = new BaseXmlObjectRef(appFormBean);
		BaseXmlObjectLink link = new BaseXmlObjectLink(ownerRef, applicationFormOwner, afbRef, PlatformChangeAppFormBean);
		storeManager.save(link);
		updateAppForm(pbo);
	}
	public static void submitAppForm(WTObject pbo) throws Exception {
		List<PlatformChangeXmlObjectBean> beans = PlatformChangeXmlObjectUtil.getXmlObjectUtil(pbo);
	 	for (int i = 0; i < beans.size(); i++) {
    		PlatformChangeXmlObjectBean bean=beans.get(i);
    		String number=bean.getPartNumber();
    		WTPart part=CommonUtil.getPartByNumber(number);
    		String value=bean.getPlatformAfter();
    		setPlatform(part, value);
		}
	}
	public static void setPlatform(WTPart part, String value) throws WTException, WTPropertyVetoException, RemoteException{
		    WTPartMaster partmaster = part.getMaster();
			IBAUtility iba = new IBAUtility(partmaster);
			iba.setIBAValue(ConstantRequire.iba_CATL_Platform, value);
			iba.updateAttributeContainer(partmaster);
			iba.updateIBAHolder(partmaster);
	}
	public static void updateAppForm(WTObject pbo) throws Exception {
		List<PlatformChangeAppFormBean> afbs = getPlatformChangeXmlObjectList(pbo);
		if (!afbs.isEmpty()) {
			PlatformChangeAppFormBean afb = afbs.get(0);
			XmlObjectStoreManager storeManager = XWorksHelper.getXmlObjectStoreManager();
			List<PlatformChangeXmlObjectBean> xobs = afb.getPlatformChangeXmlObjectList();
			Map<Long, PlatformChangeXmlObjectBean> deleted = new HashMap<Long, PlatformChangeXmlObjectBean>();
			Set<PlatformChangeXmlObjectBean> notExist = new HashSet<PlatformChangeXmlObjectBean>();
			for (PlatformChangeXmlObjectBean xob : xobs) {
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
					PlatformChangeXmlObjectBean xob = new PlatformChangeXmlObjectBean();
					xob.setPboId(part.getPersistInfo().getObjectIdentifier().getId());
					xob.setPartNumber(part.getNumber());
					xob.setPartName(part.getName());
					xob.setPartCreator(part.getCreatorFullName());
					xob.setPartModifier(part.getModifierFullName());
					xob.setPartBranchId(part.getBranchIdentifier());
					xob.setPlatformBefore(IBAUtil.getIBAStringValue(part.getMaster(), ConstantRequire.iba_CATL_Platform));
					xob = (com.catl.promotion.bean.PlatformChangeXmlObjectBean) storeManager.save(xob);

					BaseXmlObjectRef afbRef = new BaseXmlObjectRef(afb);
					BaseXmlObjectRef xobRef = new BaseXmlObjectRef(xob);
					BaseXmlObjectLink link = new BaseXmlObjectLink(afbRef, XmlObjectLink.ROLE_PARENT, xobRef, PlatformChangeXmlObjectBean);
					storeManager.save(link);
				}
			}
			for (Long branchId : deleted.keySet()) {
				storeManager.delete(deleted.get(branchId));
			}
			for (PlatformChangeXmlObjectBean xo : notExist) {
				storeManager.delete(xo);
			}
		}
	}

	public static List<PlatformChangeXmlObjectBean> addPlatformChangeObjects(WTObject pbo, Set<String> soids) throws Exception, WTException {
		List<PlatformChangeAppFormBean> afbs = getPlatformChangeXmlObjectList(pbo);
		List<PlatformChangeXmlObjectBean> xobs = getXmlObjectUtil(pbo);

		List<PlatformChangeXmlObjectBean> list = new ArrayList<PlatformChangeXmlObjectBean>();
		Set<String> numbers = new HashSet<String>();
		for (PlatformChangeXmlObjectBean xob : xobs) {
			String number = xob.getPartNumber();
			numbers.add(number);
		}

		WTSet addTargets = new WTHashSet();
		if (!afbs.isEmpty()) {
			PlatformChangeAppFormBean afb = afbs.get(0);
			XmlObjectStoreManager storeManager = XWorksHelper.getXmlObjectStoreManager();

			for (String oid : soids) {
				WTPart part = (WTPart) WorkflowUtil.getObjectByOid(oid);
				
				// 检查部件是否符合添加的要求
				StringBuffer msgs = PromotionUtil.checkPlatformChangeObjecs((PromotionNotice)pbo,part);
				if (msgs.length() > 0) {
					throw new WTException(msgs.toString());
				}
				
				addTargets.add(part);
				String number = part.getNumber();
				if ((!numbers.isEmpty() && !numbers.contains(number)) || numbers.isEmpty()) {
					String type = WorkflowUtil.getTypeInternalName(part);
					if (type.equals(TypeName.CATLPart)) {
						
						PlatformChangeXmlObjectBean xob = new PlatformChangeXmlObjectBean();
						xob.setPboId(part.getPersistInfo().getObjectIdentifier().getId());
						xob.setPartNumber(part.getNumber());
						xob.setPartName(part.getName());
						xob.setPartBranchId(part.getBranchIdentifier());
						xob.setPartCreator(part.getCreatorFullName());
						xob.setPartModifier(part.getModifierFullName());
						xob.setPlatformBefore(IBAUtil.getIBAStringValue(part.getMaster(), ConstantRequire.iba_CATL_Platform));
						xob = (PlatformChangeXmlObjectBean) storeManager.save(xob);

						BaseXmlObjectRef afbRef = new BaseXmlObjectRef(afb);
						BaseXmlObjectRef xobRef = new BaseXmlObjectRef(xob);
						BaseXmlObjectLink link = new BaseXmlObjectLink(afbRef, XmlObjectLink.ROLE_PARENT, xobRef, PlatformChangeXmlObjectBean);
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

	public static void removePlatformChangeObject(WTObject pbo, Set<String> soids) throws Exception {
		List<PlatformChangeXmlObjectBean> afbs = getXmlObjectUtil(pbo);

		WTSet rmTargets = new WTHashSet();
		if (!afbs.isEmpty()) {

			XmlObjectStoreManager storeManager = XWorksHelper.getXmlObjectStoreManager();
			for (PlatformChangeXmlObjectBean mafb : afbs) {
				if (mafb != null && soids.contains(mafb.toString())) {
					String id = "VR:wt.part.WTPart:" + mafb.getPartBranchId();
					WTPart part = (WTPart) WorkflowUtil.getObjectByOid(id);
					rmTargets.add(part);
					storeManager.delete(mafb);
				}
			}
		}
		BomWfUtil.removeTargets((PromotionNotice) pbo, rmTargets);
	}

	@SuppressWarnings("rawtypes")
	public static List<PlatformChangeXmlObjectBean> pastePlatformChangeObject(WTObject pbo, ArrayList list) throws Exception {
		List<PlatformChangeAppFormBean> afbs = getPlatformChangeXmlObjectList(pbo);
		List<PlatformChangeXmlObjectBean> xobs = getXmlObjectUtil(pbo);

		List<PlatformChangeXmlObjectBean> lists = new ArrayList<PlatformChangeXmlObjectBean>();
		Set<String> numbers = new HashSet<String>();
		for (PlatformChangeXmlObjectBean xob : xobs) {
			String number = xob.getPartNumber();
			numbers.add(number);
		}

		WTSet addTargets = new WTHashSet();
		if (!afbs.isEmpty()) {
			PlatformChangeAppFormBean afb = afbs.get(0);
			XmlObjectStoreManager storeManager = XWorksHelper.getXmlObjectStoreManager();

			for (Object obj : list) {
				String oid = ((NmOid) obj).toString();
				WTPart part = (WTPart) WorkflowUtil.getObjectByOid(oid);
				
				// 检查部件是否符合添加的要求
				StringBuffer msgs = PromotionUtil.checkPlatformChangeObjecs((PromotionNotice)pbo,part);
				if (msgs.length() > 0) {
					continue;
				}
				
				addTargets.add(part);
				String number = part.getNumber();
				if ((!numbers.isEmpty() && !numbers.contains(number)) || numbers.isEmpty()) {
					String type = WorkflowUtil.getTypeInternalName(part);
					if (type.equals(TypeName.CATLPart)) {
						PlatformChangeXmlObjectBean xob = new PlatformChangeXmlObjectBean();
						xob.setPboId(part.getPersistInfo().getObjectIdentifier().getId());
						xob.setPartNumber(part.getNumber());
						xob.setPartName(part.getName());
						xob.setPartBranchId(part.getBranchIdentifier());
						xob.setPartCreator(part.getCreatorFullName());
						xob.setPartModifier(part.getModifierFullName());
						xob.setPlatformBefore(IBAUtil.getIBAStringValue(part.getMaster(), ConstantRequire.iba_CATL_Platform));
						xob = (PlatformChangeXmlObjectBean) storeManager.save(xob);

						BaseXmlObjectRef afbRef = new BaseXmlObjectRef(afb);
						BaseXmlObjectRef xobRef = new BaseXmlObjectRef(xob);
						BaseXmlObjectLink link = new BaseXmlObjectLink(afbRef, XmlObjectLink.ROLE_PARENT, xobRef, PlatformChangeXmlObjectBean);
						storeManager.save(link);
						lists.add(xob);
					}
				}
			}
		}
		BomWfUtil.addToTargets((PromotionNotice) pbo, addTargets);

		return lists;
	}

	public static List<PlatformChangeAppFormBean> getPlatformChangeXmlObjectList(WTObject pbo) throws WTException {
		try {
			XmlObjectStoreManager storeManager = XWorksHelper.getXmlObjectStoreManager();
			XmlObjectIdentifier ownerOid = XmlObjectIdentifier.newXmlObjectIdentifier(pbo);
			List<XmlObject> xmlObjs = storeManager.navigate(ownerOid, PlatformChangeAppFormBean);
			System.out.println("#########"+xmlObjs.size());
			List<PlatformChangeAppFormBean> result = new ArrayList<PlatformChangeAppFormBean>();
			for (XmlObject xmlObj : xmlObjs) {
				xmlObj = storeManager.load(xmlObj.getIdentifier(), LoadOption.ALL_CHILDS);
				result.add((PlatformChangeAppFormBean) xmlObj);
			}
			System.out.println("----"+result.size());
			return result;
		} catch (XmlObjectStoreException e){
			LOGGER.error("XmlObjectStoreException occured when invoking getReagentRawItemReviewAppFormBean", e);
			throw new WTException(e);
		}
	}

	public static List<PlatformChangeXmlObjectBean> getXmlObjectUtil(WTObject pbo) throws WTException {
		try {
			XmlObjectStoreManager storeManager = XWorksHelper.getXmlObjectStoreManager();
			XmlObjectIdentifier ownerOid = XmlObjectIdentifier.newXmlObjectIdentifier(pbo);
			List<XmlObject> xmlObjs = storeManager.navigate(ownerOid, PlatformChangeAppFormBean);

			List<PlatformChangeXmlObjectBean> result = new ArrayList<PlatformChangeXmlObjectBean>();
			for (XmlObject xmlObj : xmlObjs) {
				List<XmlObject> Objs = storeManager.navigate(xmlObj, PlatformChangeXmlObjectBean);
				for (XmlObject obj : Objs) {
					obj = storeManager.load(obj.getIdentifier(), LoadOption.ALL_CHILDS);
					result.add((PlatformChangeXmlObjectBean) obj);
				}
			}
			return result;
		} catch (XmlObjectStoreException e) {
			LOGGER.error("XmlObjectStoreException occured when invoking getReagentRawItemReviewAppFormBean", e);
			throw new WTException(e);
		}
	}
	public static void main(String[] args) throws Exception {
		RemoteMethodServer rms = RemoteMethodServer.getDefault();
		rms.setUserName("wcadmin");
		rms.setPassword("wcadmin");
		rms.invoke("test", PlatformChangeXmlObjectUtil.class.getName(), null, null, null);
	}
	public static void test() throws Exception{
		PromotionNotice pn=(PromotionNotice) WCUtil.getWTObject("OR:wt.maturity.PromotionNotice:180238819");
		submitAppForm(pn);
	}
}
