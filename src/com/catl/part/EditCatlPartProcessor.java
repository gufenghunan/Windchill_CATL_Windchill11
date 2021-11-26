package com.catl.part;

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

import com.catl.battery.constant.ConstantBattery;
import com.catl.common.constant.ClassifyName;
import com.catl.common.constant.IBAInteriorValue;
import com.catl.common.util.ClassificationUtil;
import com.catl.common.util.GenericUtil;
import com.catl.common.util.PartUtil;
import com.catl.line.util.NodeUtil;
import com.catl.require.constant.ConstantRequire;
import com.catl.require.constant.GlobalData;
import com.catl.require.helper.CacheHelper;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.EditWorkableFormProcessor;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.forms.FormResultAction;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.core.lwc.common.AttributeTemplateFlavor;
import com.ptc.core.lwc.common.view.PropertyHolderHelper;
import com.ptc.core.lwc.common.view.PropertyHolderReadView;
import com.ptc.core.lwc.server.LWCAbstractAttributeTemplate;
import com.ptc.core.lwc.server.LWCStructEnumAttTemplate;
import com.ptc.core.lwc.server.PersistableAdapter;
import com.ptc.core.lwc.server.TypeDefinitionServiceHelper;
import com.ptc.core.ui.resources.FeedbackType;
import com.ptc.netmarkets.util.beans.NmCommandBean;

import wt.access.NotAuthorizedException;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentRoleType;
import wt.fc.IdentityHelper;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerException;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.State;
import wt.log4j.LogR;
import wt.part.QuantityUnit;
import wt.part.Source;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.part.WTPartMasterIdentity;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.util.WTInvalidParameterException;
import wt.util.WTPropertyVetoException;
import wt.util.WTRuntimeException;
import wt.vc.wip.WorkInProgressHelper;

public class EditCatlPartProcessor extends EditWorkableFormProcessor {
	private static final Logger log;
	private String cls = "";
	private String defultUnit = "";
	private String platform="";
	private String location = "";
	private String name = "";
	private String enName = "";
	HashMap textMap = new HashMap();
	HashMap textAreaMap = new HashMap();
	HashMap comboxMap = new HashMap();
	LWCStructEnumAttTemplate clsNodeAttTemplate;

	static {
		try {
			log = LogR.getLogger(EditCatlPartProcessor.class.getName());
		} catch (Exception e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	private void initial(NmCommandBean nmcommandBean, List<ObjectBean> list) throws WTException {
		textMap = nmcommandBean.getText();
		textAreaMap = nmcommandBean.getTextArea();
		comboxMap = nmcommandBean.getComboBox();
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
		for(Object key : comboxMap.keySet()){
			if(key instanceof String){
				String k = (String)key;
				if(k.contains("defaultUnit")){
					String val = ((ArrayList)comboxMap.get(k)).get(0).toString();
					String unit = val.substring(val.lastIndexOf(".")+1);
					defultUnit = unit;
				}
			}
		}
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
		// it = comboxMap.entrySet().iterator();
		// while (it.hasNext())
		// {
		// Map.Entry entry = (Map.Entry) it.next();
		// Object key = entry.getKey();
		// Object val = entry.getValue();
		// if (key instanceof String)
		// {
		// String k = (String) key;
		// if (k != null && k.contains("defaultUnit~~"))
		// {
		// defultUnit = val.toString();
		// if (defultUnit.contains("["))
		// defultUnit = defultUnit.substring(1, defultUnit.length() - 1);
		// log.debug("defultUnit~~:" + defultUnit);
		// break;
		// }
		// }
		// }
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

	private String preCheckSpecification(NmCommandBean clientData, List<ObjectBean> objectBeans) throws WTRuntimeException, WTException {
		ValidateSpecification validate = new ValidateSpecification(clientData);
		return validate.preCheckSpecification(objectBeans);
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
		String defaultUnit = defultUnit.equals("ea")?"pcs":defultUnit;
		if (des.contains("|")) {
			String units[] = des.split("\\|");
			for (int i = 0; i < units.length; i++) {
				String unit = units[i];
				if (unit.equalsIgnoreCase(defaultUnit)) {
					return;
				}
			}
		} else {
			if (des.equalsIgnoreCase(defaultUnit)) {
				return;
			}
		}
		throw new WTException("零部件单位必须为：" + des + "。  零部件单位必须与改分类属性中定义的一样！请重新修改零部件单位！");
	}

	@Override
	public FormResult doOperation(NmCommandBean clientData, List<ObjectBean> objectBeans) throws WTException {
		FormResult result = null;// result = new
									// FormResult(FormProcessingStatus.FAILURE);
		for (ObjectBean objBean : objectBeans) {
			Object obj = objBean.getObject();
			log.trace(obj);
			if (obj instanceof WTPart) {
				WTPart part = (WTPart) obj;
				initial(clientData, objectBeans);
				preCheckLocation();
				
				if(defultUnit.equals("")){//默认单位不可编辑取不到值，则去原来的值
					defultUnit = part.getDefaultUnit().getDisplay(Locale.ENGLISH);
				}
				preCheckUnit();
				checkPlatform(part.getContainer());
				setDefaultUnit(part);
				preCheckClsAttr(part);
				preCheckNameLength();
				if (WorkInProgressHelper.isCheckedOut(part)) {
					WTPart originalPart = (WTPart) WorkInProgressHelper.service.originalCopyOf(part);
					log.trace("originalPart" + originalPart);
					PersistableAdapter genericObj = new PersistableAdapter(originalPart, null, null, null);
					genericObj.load("cls");
					String originalcls = (String) genericObj.get("cls");
					log.trace(originalcls);
					if (!originalcls.equals(cls)) {
						result = new FormResult(FormProcessingStatus.FAILURE);
						result.setNextAction(FormResultAction.NONE);
						FeedbackMessage feedbackmessage = new FeedbackMessage(FeedbackType.FAILURE, clientData.getLocale(), "", null, new String[] { "不能修改分类！" });
						feedbackmessage.setShowItemIdAsText(true);
						result.addFeedbackMessage(feedbackmessage);
						return result;
					}
					// set Specification value
					CreateCatlPartProcessor.setSpecification(clientData,part);				
					
				}
			}
		}
		String checkResult = preCheckSpecification(clientData, objectBeans);
		if (!"".equals(checkResult)) {
			throw new WTException(checkResult);
		}
		return super.doOperation(clientData, objectBeans);
	}

	private void setDefaultUnit(WTPart part) {
		try {
			part.setDefaultUnit(QuantityUnit.toQuantityUnit(defultUnit));
		} catch (WTInvalidParameterException e) {
			e.printStackTrace();
		} catch (WTPropertyVetoException e) {
			e.printStackTrace();
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

	@Override
	public FormResult postProcess(NmCommandBean clientData, List<ObjectBean> objectBeans) throws WTException {
		FormResult result = super.postProcess(clientData, objectBeans);
		WTPart part = null;
		for (ObjectBean objBean : objectBeans) {
			Object obj = objBean.getObject();
			if (obj instanceof WTPart) {
				part = (WTPart) obj;
				PartUtil.checkOldPartNumber(part);
				checkAttachment(part);
				updateSource(part);
				renumberPart(objBean, part);
				try {
					CreateCatlPartProcessor.setMatchingCode(part);
				} catch (WTPropertyVetoException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
		return result;
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
	 * @throws WTException
	 */
	public static void renumberPart(ObjectBean objbean, WTPart part) throws WTException{
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
				String number=(String) GenericUtil.getObjectAttributeValue(part, ConstantBattery.iba_attr_pn);
				if(number==null||part.getNumber().startsWith(number)){
					return;
				}
//				WTPart wtpart=PartUtil.getLastestWTPartByNumber(number.toString());
//				if(wtpart!=null){
//					throw new WTException("PN码为["+number.toString()+"]的物料已存在，请重新填写！");
//				}
				
				part=(WTPart) PersistenceHelper.manager.refresh(part);
				WTPartMaster master=part.getMaster();
				WTPartMasterIdentity identity=(WTPartMasterIdentity) master.getIdentificationObject();
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
						identity.setNumber(number+"-"+numberwater);
						IdentityHelper.service.changeIdentity(master, identity);
					}
					objbean.setObject(part);
					
				} catch (WTPropertyVetoException e) {
					throw new WTException(e);
				} catch (Exception e) {
					throw new WTException(e);
				}
			}
		}catch(WTException e){
			throw e;
		}finally{
			SessionServerHelper.manager.setAccessEnforced(enforce);
		}
	
	}
	private void checkPlatform(WTContainer wtContainer) throws WTException {
        if(!ishideplatform(wtContainer.getName())){
        	System.out.println("platform:"+platform);
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
}
