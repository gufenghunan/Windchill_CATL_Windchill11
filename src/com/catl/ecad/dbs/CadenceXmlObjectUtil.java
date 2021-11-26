package com.catl.ecad.dbs;

import java.util.*;

import org.apache.log4j.Logger;

import wt.fc.ObjectReference;
import wt.fc.WTObject;
import wt.log4j.LogR;
import wt.part.WTPart;
import wt.util.WTException;

import com.catl.ecad.bean.CadenceAttributeBean;
import com.ptc.xworks.util.XWorksHelper;
import com.ptc.xworks.xmlobject.BaseXmlObjectLink;
import com.ptc.xworks.xmlobject.BaseXmlObjectRef;
import com.ptc.xworks.xmlobject.XmlObject;
import com.ptc.xworks.xmlobject.XmlObjectIdentifier;
import com.ptc.xworks.xmlobject.store.XmlObjectStoreException;
import com.ptc.xworks.xmlobject.store.XmlObjectStoreManager;
import com.ptc.xworks.xmlobject.store.StoreOptions.LoadOption;

public class CadenceXmlObjectUtil {
	public final static String doctype = "wt.doc.WTDocument";
	public final static String epmtype = "wt.epm.EPMDocument";
	public final static String FPCPARTATTRBEAN = "FPCAttributeBean";
	public final static String FPCVENDORATTRIBUTEBEAN = "FPCVendorAttributeBean";
	public final static String PURCHASEATTTRIBUTEBEAN = "PurVendorAttributeBean";
	public final static String OpenModelAttributeBean = "OpenModelAttributeBean";
	public final static String PurAttributeBean = "PurAttributeBean";
	
	public final static String applicationFormOwner = "applicationFormOwner";
	
	private static final Logger LOGGER = LogR.getLogger(CadenceXmlObjectUtil.class.getName());
	
	

	//=========================================================CadenceAttributeBean=========================================================
	/**
	 * 根据对象获取对应的bean数据(CadenceAttributeBean)
	 * @param pbo
	 * @return
	 * @throws WTException
	 */
	public static List<CadenceAttributeBean> getCadenceAttributeBeanUtil(WTObject pbo) throws WTException {
		WTPart part=(WTPart) pbo;
		try {
			XmlObjectStoreManager storeManager = XWorksHelper.getXmlObjectStoreManager();
			XmlObjectIdentifier ownerOid = XmlObjectIdentifier.newXmlObjectIdentifier(part.getMaster());
			List<XmlObject> xmlObjs = storeManager.navigate(ownerOid,"CadenceAttributeBean");
			
			List<CadenceAttributeBean> result = new ArrayList<CadenceAttributeBean>();
			for (XmlObject xmlObj : xmlObjs) {
				xmlObj = storeManager.load(xmlObj.getIdentifier(), LoadOption.ALL_CHILDS);
				result.add((CadenceAttributeBean) xmlObj);
			}
			return result;
		} catch (XmlObjectStoreException e) {
			LOGGER.error("XmlObjectStoreException occured when invoking getMadePartXmlObjectUtil", e);
			throw new WTException(e);
		}
	}
	
	/**
	 * pbo与CadenceAttributeBean关联 
	 * @throws WTException 
	 */
	public static void createCadenceAttributeBean(WTObject pbo) throws WTException{
		WTPart part=(WTPart) pbo;
		String oid=ObjectReference.newObjectReference(part).toString();
		CadenceAttributeBean cadenceAttrbean = new CadenceAttributeBean();
		cadenceAttrbean.setPartOid(oid);
		XmlObjectStoreManager storeManager = XWorksHelper.getXmlObjectStoreManager();
		storeManager.save(cadenceAttrbean);
		BaseXmlObjectRef parentRef = BaseXmlObjectRef.newBaseXmlObjectRef(part.getMaster());
		BaseXmlObjectRef childRef = new BaseXmlObjectRef(cadenceAttrbean);
		BaseXmlObjectLink link = new BaseXmlObjectLink(parentRef, CadenceXmlObjectUtil.applicationFormOwner, childRef, "CadenceAttributeBean");
		storeManager.save(link);
	}
	
	
}
