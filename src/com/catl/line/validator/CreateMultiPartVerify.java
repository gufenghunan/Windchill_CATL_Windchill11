package com.catl.line.validator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.catl.common.constant.ClassifyName;
import com.catl.common.constant.ContainerName;
import com.catl.common.constant.IBAInteriorValue;
import com.catl.common.util.ClassificationUtil;
import com.catl.common.util.GenericUtil;
import com.catl.common.util.IBAUtil;
import com.catl.common.util.PartUtil;
import com.catl.part.PartConstant;
import com.catl.part.ValidateSpecification;
import com.catl.part.classification.RefreshFAEStatusUtil;
import com.catl.require.constant.ConstantRequire;
import com.catl.require.constant.GlobalData;
import com.catl.require.helper.CacheHelper;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.lwc.common.AttributeTemplateFlavor;
import com.ptc.core.lwc.common.view.PropertyHolderHelper;
import com.ptc.core.lwc.common.view.PropertyHolderReadView;
import com.ptc.core.lwc.server.LWCAbstractAttributeTemplate;
import com.ptc.core.lwc.server.LWCStructEnumAttTemplate;
import com.ptc.core.lwc.server.PersistableAdapter;
import com.ptc.core.lwc.server.TypeDefinitionServiceHelper;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.core.meta.common.UpdateOperationIdentifier;
import com.ptc.core.meta.server.TypeIdentifierUtility;
import com.ptc.netmarkets.util.beans.NmCommandBean;

import wt.access.NotAuthorizedException;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentRoleType;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerException;
import wt.inf.library.WTLibrary;
import wt.log4j.LogR;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.pdmlink.PDMLinkProduct;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.util.WTRuntimeException;

public class CreateMultiPartVerify{
	private static final Logger log;

	private String cls = "";
	private String defultUnit = "";
	private String location = "";
	private String name = "";
	private String enName = "";
	HashMap textMap = new HashMap();
	HashMap textAreaMap = new HashMap();
	HashMap comboxMap = new HashMap();
	LWCStructEnumAttTemplate clsNodeAttTemplate;
	String platform="";

	static {
		try {
			log = LogR.getLogger(CreateMultiPartVerify.class.getName());
		} catch (Exception e) {
			throw new ExceptionInInitializerError(e);
		}
	}
    private void loadVariable(WTPart part){
    	name=part.getName();
    	platform=(String) IBAUtil.getIBAValue(part.getMaster(), ConstantRequire.iba_CATL_Platform);
    }

	private void preCheckNameLength() throws WTException {
		if (name.length() > 40) {
			throw new WTException("物料名称或者英文描述长度超过了40位。");
		}
	}

	private void preCheckUnit() throws NotAuthorizedException, WTContainerException, WTException {
		String des = "";
		LWCAbstractAttributeTemplate obj = (LWCAbstractAttributeTemplate) clsNodeAttTemplate;
		long id = obj.getPersistInfo().getObjectIdentifier().getId();
		PropertyHolderReadView propertyHolderReadView = TypeDefinitionServiceHelper.service.getTypeDefView(AttributeTemplateFlavor.getAttributeTemplateFlavor(obj), id);
		String propertyName = "description";
		des = PropertyHolderHelper.getPropertyValue(propertyHolderReadView, Locale.ENGLISH, propertyName);
		log.debug("description:" + des);
		if (des == null)
			return;
		if (des.contains("|")) {
			String units[] = des.split("\\|");
			for (int i = 0; i < units.length; i++) {
				String unit = units[i];
				if (unit.equalsIgnoreCase(defultUnit)) {
					return;
				}
			}
		} else {
			if (des.equalsIgnoreCase(defultUnit)) {
				return;
			}
		}
		throw new WTException("零部件单位必须为：" + des + "。  零部件单位必须与该分类属性中定义的一样！请重新修该零部件单位！");
	}

    public void verifyNewPart(WTPart obj,LWCStructEnumAttTemplate node) throws WTException{
    	if (obj instanceof WTPart) {
    		clsNodeAttTemplate=node;
			WTPart part2 = (WTPart) obj;
			defultUnit = part2.getDefaultUnit().getDisplay(Locale.ENGLISH);
			loadVariable(part2);
			preCheckNameLength();
			checkPlatform(obj.getContainer());
			preCheckUnit();
			preCheckClsAttr(part2);
			checkContainer(part2.getContainer());
			checkAttachment(part2);
			PartUtil.checkOldPartNumber(part2);
			setDefaultMaturity(part2);
		}
    }
	private void checkPlatform(WTContainer wtContainer) throws WTException {
        if(!ishideplatform(wtContainer.getName())){
        	if(StringUtils.isEmpty(platform)){
    	    	throw new WTException(wtContainer.getName()+"下部件，产品线标识必填");
    	    }
		}else{
			if(!StringUtils.isEmpty(platform)){
    	    	throw new WTException(wtContainer.getName()+"下部件，产品线标识不填");
    	    }
		}
	    
	}
	public boolean ishideplatform(String containername) throws WTException{
		try {
			CacheHelper.loadExcelConfig();
		} catch (IOException e) {
			throw new WTException("加载隐藏产品线标识配置表出错");
		}
		for (int i = 0; i < GlobalData.config_hide_platform.size(); i++) {
			String name=(String) GlobalData.config_hide_platform.get(i);
			if(!StringUtils.isEmpty(name)&&containername.startsWith(name)){
				return true;
			}
		}
		return false;
	}
	private void setDefaultMaturity(WTPart part) throws WTException{
		if(part != null){
			part = (WTPart)PersistenceHelper.manager.refresh(part);
			WTPartMaster master = (WTPartMaster)part.getMaster();
			Map<String, Object> dataMap = new HashMap<String, Object>();
			dataMap.put(PartConstant.IBA_CATL_Maturity, "1");
			dataMap.put(PartConstant.IBA_CATL_FAEStatus, RefreshFAEStatusUtil.getInitialFAEStatusValue(part));
			PersistenceHelper.manager.save(IBAUtil.setIBAVaue(master, dataMap));
		}
	}

	private void checkAttachment(WTPart part) throws WTException {
		String containerName = part.getContainerName();
		if(containerName != null && !containerName.equals("电子电气件") && !containerName.equals("原材料") && !containerName.equals("AFT-SLS")){
			QueryResult qr = ContentHelper.service.getContentsByRole(part, ContentRoleType.SECONDARY);
	        while(qr.hasMoreElements()){
	        	Object obj = qr.nextElement();
	        	if(obj instanceof ApplicationData){
	        		throw new WTException("按公司项目指导委员会要求，除“电子电气件”、“原材料”、“AFT-SLS”三个库外不再允许直接上传附件，请上传CATIA等二维图纸！");
	        	}
	            
	        }
		}
	}

	private void preCheckClsAttr(WTPart part) throws WTException {
		StringBuffer errorMsg = new StringBuffer("");
		Object productEnergyObj = GenericUtil.getObjectAttributeValue(part, IBAInteriorValue.Product_Energy);// 产品能量
		if (productEnergyObj != null) {
			Pattern p = Pattern.compile("0|^[1-9][0-9]*$|^([1-9][0-9]*|0)\\.[0-9]{1,3}$");
			Matcher m2 = p.matcher(productEnergyObj.toString());
			if (!m2.matches()) {
				errorMsg.append("产品能量填写值不正确，产品能量小数点后不能超过3位\n");
			}
		}
		Object nominal_VoltageObj = GenericUtil.getObjectAttributeValue(part, IBAInteriorValue.Nominal_Voltage);// 标称电压
		if (nominal_VoltageObj != null) {
			Pattern p = Pattern.compile("0|^[1-9][0-9]*$|^([1-9][0-9]*|0)\\.[0-9]{1,3}$");
			Matcher m2 = p.matcher(nominal_VoltageObj.toString());
			if (!m2.matches()) {
				errorMsg.append("标称电压填写值不正确，标称电压小数点后不能超过3位\n");
			}
		}
		if (!part.getNumber().startsWith("8401")) {
			Object cellConnectionModeObj = GenericUtil.getObjectAttributeValue(part, IBAInteriorValue.Cell_Connection_Mode);// 电芯并串联方式
			if (cellConnectionModeObj != null) {
				Pattern p = Pattern.compile("^[1-9][0-9]*(\\*[1-9])?[0-9]*P[1-9][0-9]*S(\\+[1-9][0-9]*(\\*[1-9])?[0-9]*P[1-9][0-9]*S)*$");
				Matcher m2 = p.matcher(cellConnectionModeObj.toString());
				if (!m2.matches()) {
					errorMsg.append("电芯并串联方式填写值不正确，格式为1P2S或者1*2P3S\n");
				}
			}
		}
		Object mpnObj = GenericUtil.getObjectAttributeValue(part, IBAInteriorValue.Manufacturer_Part_Number);// MPN
		if (mpnObj != null) {
			Set<String> isExistMPN = PartUtil.isExistMPN(part.getNumber(), mpnObj.toString());
			if(isExistMPN.size() > 0){
				errorMsg.append("MPN填写值不正确，已存在PN:"+isExistMPN.toString());
			}
		}
		Object moduleQuantityObj = GenericUtil.getObjectAttributeValue(part, IBAInteriorValue.Module_Quantity);// 模组数量(PCS)
		if (moduleQuantityObj != null) {
			Pattern p = Pattern.compile("0|^[1-9][0-9]*$");
			Matcher m2 = p.matcher(moduleQuantityObj.toString());
			if (!m2.matches()) {
				errorMsg.append("模组数量填写值不正确，必须为整数\n");
			}
		}
		if (!errorMsg.toString().equals("")) {
			throw new WTException(errorMsg.toString());
		}
	}

	private String preCheckSpecification(NmCommandBean clientData, List<ObjectBean> objectBeans) throws WTRuntimeException, WTException {
		ValidateSpecification validate = new ValidateSpecification(clientData);
		return validate.preCheckSpecification(objectBeans);
	}

	public static void setSpecification(NmCommandBean clientData, WTPart part) throws WTException {
		if (part != null) {
			String specification = ValidateSpecification.getSpecification(clientData, part);
			log.debug("specification:" + specification);

			try {
				if(specification != null && !specification.trim().equals("") && !specification.equals("null")){
					PersistableAdapter genericObj = new PersistableAdapter(part, null, null, new UpdateOperationIdentifier());
					genericObj.load("specification");
					genericObj.set("specification", specification);
					Persistable updatedObject = genericObj.apply();
					part = (WTPart) PersistenceHelper.manager.save(updatedObject);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void checkContainer(WTContainer container) throws WTException {
		String containername = container.getName();
		log.debug("container name ==" + containername);
		ArrayList<String> parentNameString = new ArrayList<String>();
		ClassificationUtil.getLastNodeName(clsNodeAttTemplate, parentNameString);
		String rootname = parentNameString.get(0).toString();
		if (container instanceof WTLibrary) {
			if (containername.startsWith(ContainerName.BATTERY_LIBRARY_NAME)) {
				if (!rootname.startsWith(ClassifyName.battery_library_classify)) {
					throw new WTException(containername + ",不能够创建该物料分类的零部件!");
				}
			}
			if (containername.startsWith(ContainerName.EE_LIBRARY_NAME)) {
				if (!rootname.startsWith(ClassifyName.ee_library_classify)) {
					throw new WTException(containername + ",不能够创建该物料分类的零部件!");
				}
			}
			if (containername.startsWith(ContainerName.METRIAL_LIBRARY_NAME)) {
				if (!rootname.startsWith(ClassifyName.metrial_library_classify)) {
					throw new WTException(containername + ",不能够创建该物料分类的零部件!");
				}
			}
			if (containername.startsWith(ContainerName.FIX_LIBRARY_NAME)) {
				if (!rootname.startsWith(ClassifyName.fix_library_classify)) {
					throw new WTException(containername + ",不能够创建该物料分类的零部件!");
				}
			}
		} else if (container instanceof PDMLinkProduct) {
			
    		if (rootname.startsWith(ClassifyName.ee_library_classify) || rootname.startsWith(ClassifyName.fix_library_classify) || rootname.startsWith(ClassifyName.battery_library_classify) || rootname.startsWith(ClassifyName.metrial_library_classify)) {
				throw new WTException(containername + ",不能够创建该物料分类的零部件!");
			}
    		
    		if(clsNodeAttTemplate.getName().startsWith("A")&&!containername.equals("成套电箱半成品")){
				throw new WTException("套电箱半成品只能在成套半成品产品库下创建");
			}
			if(clsNodeAttTemplate.getName().startsWith("TM")&&!containername.equals("测试物料")){
				throw new WTException("测试物料只能在测试物料产品库下创建");
			}
		}

	}
	
	public static void updateFAEStatus(WTPart part) throws WTException{
		if(part != null){
			String faeStatus = RefreshFAEStatusUtil.needRefresh(part);
			if(StringUtils.isNotBlank(faeStatus)){
				boolean enforced = SessionServerHelper.manager.setAccessEnforced(false);
				try {
					Persistable p = IBAUtil.setIBAVaue(part.getMaster(), PartConstant.IBA_CATL_FAEStatus, faeStatus);
					PersistenceHelper.manager.save(p);
				} finally {
					SessionServerHelper.manager.setAccessEnforced(enforced);
				}
			}
		}
	}
}
