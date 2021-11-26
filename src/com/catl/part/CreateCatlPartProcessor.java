package com.catl.part;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.RemoteException;
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

import wt.access.NotAuthorizedException;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentRoleType;
import wt.fc.IdentityHelper;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerException;
import wt.inf.library.WTLibrary;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleManaged;
import wt.lifecycle.State;
import wt.log4j.LogR;
import wt.part.Source;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.part.WTPartMasterIdentity;
import wt.pdmlink.PDMLinkProduct;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.util.WTRuntimeException;

import com.catl.battery.constant.ConstantBattery;
import com.catl.common.constant.ClassifyName;
import com.catl.common.constant.ContainerName;
import com.catl.common.constant.IBAInteriorValue;
import com.catl.common.constant.PartState;
import com.catl.common.util.ClassificationUtil;
import com.catl.common.util.GenericUtil;
import com.catl.common.util.IBAUtil;
import com.catl.common.util.PartUtil;
import com.catl.doc.soft.util.CatlSoftDocUtil;
import com.catl.ecad.utils.IBAUtility;
import com.catl.ele.util.CatlEleDocUtil;
import com.catl.ele.util.EleCommonUtil;
import com.catl.line.util.NodeUtil;
import com.catl.part.classification.RefreshFAEStatusUtil;
import com.catl.require.constant.ConstantRequire;
import com.catl.require.constant.GlobalData;
import com.catl.require.helper.CacheHelper;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.FormResult;
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
import com.ptc.windchill.enterprise.part.forms.CreatePartAndCADDocFormProcessor;

public class CreateCatlPartProcessor extends CreatePartAndCADDocFormProcessor {
	private static final Logger log;

	private String cls = "";
	private String defultUnit = "";
	private String location = "";
	private String name = "";
	private String enName = "";
	private String platform = "";
	HashMap textMap = new HashMap();
	HashMap textAreaMap = new HashMap();
	HashMap comboxMap = new HashMap();
	LWCStructEnumAttTemplate clsNodeAttTemplate;

	static {
		try {
			log = LogR.getLogger(CreateCatlPartProcessor.class.getName());
		} catch (Exception e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	@Override
	public FormResult preProcess(NmCommandBean nmcommandBean, List<ObjectBean> list) throws WTException {
		initial(nmcommandBean, list);
		if(clsNodeAttTemplate == null)
			return super.preProcess(nmcommandBean, list);
		// preCheckLocation();
		checkPlatform(nmcommandBean.getContainer());
		preCheckNameLength();
		checkContainer(nmcommandBean.getContainer());
		return super.preProcess(nmcommandBean, list);
	}

	private void preCheckNameLength() throws WTException {
		if (name.length() > 40 || enName.length() > 40) {
			throw new WTException("物料名称或者英文描述长度超过了40位。");
		}
	}

	private void preCheckLocation() throws WTException {
		if (location != null) {
			if (location.split("/").length == 2) {
				throw new WTException("不能在根目录创建零部件！");
			}
		}
	}

	private void initial(NmCommandBean nmcommandBean, List<ObjectBean> list) throws WTException {
		textMap = nmcommandBean.getText();
		textAreaMap = nmcommandBean.getTextArea();
		Iterator it = textMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			Object key = entry.getKey();
			Object val = entry.getValue();
			if (key instanceof String) {
				String k = (String) key;
				if (k != null && k.contains("cls~~")) {
					cls = val.toString();
					log.debug("cls~~:" + cls);
				}
				if (key.toString().contains("Location")) {
					location = val.toString();
					log.debug("Location:" + location);
				}
				if (key.toString().contains("englishName~~")) {
					enName = val.toString();
				}
			}
		}
		comboxMap = nmcommandBean.getComboBox();
		Iterator cit = comboxMap.entrySet().iterator();
		while (cit.hasNext()) {
			Map.Entry entry = (Map.Entry) cit.next();
			Object key = entry.getKey();
			Object val = entry.getValue();
			if (key instanceof String) {
				if (key.toString().contains("CATL_Platform~~")) {
					platform = val.toString().replace("[]", "");
				}
			}
		}
		clsNodeAttTemplate = ClassificationUtil.getLWCStructEnumAttTemplateByName(cls);
		log.debug("clsNodeAttTemplate:" + clsNodeAttTemplate);
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

	@Override
	/**
	 * 修改验证请同时修改类com.catl.line.validator。CreateMultiPartVerify类
	 */
	public FormResult postProcess(NmCommandBean clientData, java.util.List<ObjectBean> objectBeans) throws WTException {
		FormResult result = super.postProcess(clientData, objectBeans);

		TypeIdentifier typeIdentifier = null;
		WTPart part2 = null;
		for (ObjectBean objBean : objectBeans) {
			Object obj = objBean.getObject();
			if (obj instanceof WTPart) {
				typeIdentifier = TypeIdentifierUtility.getTypeIdentifier(obj);
				part2 = (WTPart) obj;
				defultUnit = part2.getDefaultUnit().getDisplay(Locale.ENGLISH);
				preCheckUnit();
				preCheckClsAttr(part2);
				checkContainer(part2.getContainer());
				checkAttachment(part2);
				PartUtil.checkOldPartNumber(part2);
				log.debug("typeIdentifier:" + typeIdentifier);
				String checkResult = preCheckSpecification(clientData, objectBeans);
				if (!"".equals(checkResult)) {
					throw new WTException(checkResult);
				}
				// set Specification value
				setSpecification(clientData, part2);
				
				//update by szeng 20171009
				try {
					renamePart(part2);
					setMatchingCode(part2);
				} catch (WTPropertyVetoException e) {
					log.error("组合物料名称出错。");
					e.printStackTrace();
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				
				setDefaultMaturity(part2);
				
				updateSource(part2);
				
				//update by fzp 2017-11-23
				boolean isbattery=renumberPart(objBean,part2);//设置了电芯编码
				if(!isbattery){
					if(part2.getNumber().startsWith("6")||part2.getNumber().startsWith("C")||PartUtil.isSWPart(part2)){
						boolean enforced = SessionServerHelper.manager.setAccessEnforced(false);
						try {
						if(part2.getLifeCycleState().equals(State.toState(PartState.WRITING))){
							LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged) part2, State.toState(PartState.DESIGN));
						}
						} catch (Exception e) {
							e.printStackTrace();
						}finally{
							SessionServerHelper.manager.setAccessEnforced(enforced);
						}
					}	
				}
				
				//update by szeng 2018-04-09
				try {
					CatlSoftDocUtil.createSoftDocByPart(part2);
					System.out.println("______"+EleCommonUtil.isPBUSPart(part2));
					if(EleCommonUtil.isPBUSPart(part2)) {
						CatlEleDocUtil.createEleDocByWTPart(part2);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return result;
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

	public static void setDefaultMaturity(WTPart part) throws WTException{
		if(part != null){
			part = (WTPart)PersistenceHelper.manager.refresh(part);
			WTPartMaster master = (WTPartMaster)part.getMaster();
			Map<String, Object> dataMap = new HashMap<String, Object>();
			if (master.getNumber().indexOf("560220")!=-1) {
				dataMap.put(PartConstant.IBA_CATL_Maturity, "3");
			}else{
				dataMap.put(PartConstant.IBA_CATL_Maturity, "1");
			}
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
		
		String riskObj = (String) GenericUtil.getObjectAttributeValue(part, IBAInteriorValue.riskPoint);// 风险点
		String riskRelease = (String) GenericUtil.getObjectAttributeValue(part, IBAInteriorValue.riskRelease);// 是否风险放行
		if (StringUtils.isNotBlank(riskRelease)) {
			if(riskRelease.equals("是")){
				if(StringUtils.isBlank(riskObj)){
					errorMsg.append("当是否风险放行选【是】的时候，软件风险点必填！\n");
				}
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
			if (containername.startsWith("设备开发标准件")) {
				if (!rootname.startsWith("6")) {
					throw new WTException(containername + ",不能够创建该物料分类的零部件!");
				}
			}
		} else if (container instanceof PDMLinkProduct) {
			
    		if (rootname.startsWith(ClassifyName.ee_library_classify) || rootname.startsWith(ClassifyName.fix_library_classify) 
    				|| rootname.startsWith(ClassifyName.battery_library_classify) 
    				|| rootname.startsWith(ClassifyName.metrial_library_classify)
    				|| clsNodeAttTemplate.getName().startsWith("6")) {
					throw new WTException(containername + ",不能够创建该物料分类的零部件!");
			}
    		
			if(clsNodeAttTemplate.getName().startsWith("A")&&!containername.equals("成套电箱半成品")){
				throw new WTException("成套电箱半成品只能在成套半成品产品库下创建");
			}
			if(clsNodeAttTemplate.getName().startsWith("TM")&&!containername.equals("测试物料")){
				throw new WTException("测试物料只能在测试物料产品库下创建");
			}
			if(clsNodeAttTemplate.getName().startsWith("C")&&!containername.equals("设备开发产品库")){
				throw new WTException("设备开发零部件只能在设备开发产品库下创建");
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
	
	/**
	 * 组合物料名称
	 * @param part
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public static void renamePart(WTPart part) throws WTException, WTPropertyVetoException{
		if(part != null){
			WTPartMaster master=(WTPartMaster) part.getMaster();
			WTPartMasterIdentity identity=(WTPartMasterIdentity) master.getIdentificationObject();
			String feature = (String) GenericUtil.getObjectAttributeValue(part, PartConstant.CATL_Feature);
			String name = part.getName();
			if(StringUtils.isNotBlank(feature)){
				name = name+"_"+feature;
				identity.setName(name);
				master =(WTPartMaster) IdentityHelper.service.changeIdentity(master,identity);
			}
		}
	}
	
	public static void updateSource(WTPart part) throws WTException{
		if(part != null){
			Object isCustomer = GenericUtil.getObjectAttributeValue(part, PartConstant.Is_Customer);// 是否客供
			if(isCustomer != null){
				if("是".equals(isCustomer)){
					part.setSource(Source.toSource("customer"));
					PersistenceHelper.manager.save(part);
				}
			}
		}
	}
	
	/**
	 * 电芯物料设置手填PN码
	 * @param obj 
	 * @param part
	 * @throws Exception 
	 */
	public static boolean renumberPart(ObjectBean objbean, WTPart part) throws WTException{
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		try{
			com.catl.ecad.utils.IBAUtil ibaUtil=new com.catl.ecad.utils.IBAUtil(part);
			LWCStructEnumAttTemplate attTemplate=ClassificationUtil.getLWCStructEnumAttTemplateByName(ibaUtil.getIBAValue("cls"));
			if(attTemplate==null){
				throw new WTException("当前物料分类不存在！");
			}
			ArrayList<String> parentNameString = new ArrayList<String>();
			ClassificationUtil.getLastNodeName(attTemplate, parentNameString);
			String rootname = parentNameString.get(0).toString();
			
			if(rootname.equals(ClassifyName.battery_design_classify)&&!attTemplate.getName().equals(ClassifyName.battery_batteryasm_classify)){
				Object number=GenericUtil.getObjectAttributeValue(part, ConstantBattery.iba_attr_pn);
//				WTPart wtpart=PartUtil.getLastestWTPartByNumber(number.toString());
//				if(wtpart!=null){
//					throw new WTException("PN码为["+number.toString()+"]的物料已存在，请重新填写！");
//				}
				
				part=(WTPart) PersistenceHelper.manager.refresh(part);
				WTPartMaster master=part.getMaster();
				WTPartMasterIdentity identity=(WTPartMasterIdentity) master.getIdentificationObject();
				LifeCycleHelper.service.setLifeCycleState(part, State.toState("RELEASED"));
				try {
					String numberwater=CatlPartNewNumber.queryMaxBatteryMaterialNumberWatercode(number+"-");
					System.out.println("获取到流水码"+numberwater);
					if(StringUtils.isEmpty(numberwater)){
						numberwater=ConstantRequire.battery_watercode.charAt(0)+"";
					}else{
						if(!ConstantRequire.battery_watercode.contains(numberwater)){
							throw new WTException("系统中存在不符合编码规则的部件"+number+"-"+numberwater);
						}else{
							int index=ConstantRequire.battery_watercode.indexOf(numberwater);
							if(ConstantRequire.battery_watercode.endsWith(numberwater)){
								throw new WTException("电芯流水码已用尽，请联系管理员");
							}else{
								numberwater=ConstantRequire.battery_watercode.charAt(index+1)+"";
							}
						}
					}
					identity.setNumber(number+"-"+numberwater);
					IdentityHelper.service.changeIdentity(master, identity);
					objbean.setObject(part);
				} catch (WTPropertyVetoException e) {
					throw new WTException(e);
				} catch (Exception e) {
					throw new WTException(e);
				}
				return true;
			}
		}catch(WTException e){
			throw e;
		}finally{
			SessionServerHelper.manager.setAccessEnforced(enforce);
		}
		return false;
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
	
	public static WTPart setMatchingCode(WTPart part) throws WTException, WTPropertyVetoException, RemoteException{
		if(PartUtil.checkSW_HWPart(part)){
			IBAUtility iba = new IBAUtility(part);
			String componentType = iba.getIBAValue(PartConstant.CATL_ComponentType);
			String swVersion = iba.getIBAValue(PartConstant.Software_Version);
			
			Map<String,PartMatchingCodeBean> partMatchingCode = PartMatchingCodeUtil.getAllPartMatchingCode();
			String matchcode = "";
			for(String key:partMatchingCode.keySet()){
				if("BMU".equalsIgnoreCase(componentType)){
					PartMatchingCodeBean pmc = partMatchingCode.get(key);
					List<String> bmulist = pmc.getBmuVersion();
					if(bmulist.contains(swVersion)){
						matchcode = key;
					}
				}else if("CSC".equalsIgnoreCase(componentType)){
					PartMatchingCodeBean pmc = partMatchingCode.get(key);
					List<String> csclist = pmc.getCscVersion();
					if(csclist.contains(swVersion)){
						matchcode = key;
					}
				}else if("HVB".equalsIgnoreCase(componentType)){
					PartMatchingCodeBean pmc = partMatchingCode.get(key);
					List<String> hvblist = pmc.getHvbVersion();
					if(hvblist.contains(swVersion)){
						matchcode = key;
					}
				}
			}
			
			if(StringUtils.isNotBlank(matchcode)){
				iba.setIBAValue(PartConstant.MatchingCode, matchcode);
				part = (WTPart) iba.updateAttributeContainer(part);
				iba.updateIBAHolder(part);			
			}
		}		
		return part;
	}
}
