package com.catl.part.classification;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.catl.common.constant.ContainerName;
import com.catl.common.util.CatlConstant;
import com.catl.common.util.IBAUtil;
import com.catl.common.util.PartUtil;
import com.catl.common.util.WorkflowUtil;
import com.catl.integration.PIResultMessage;
import com.catl.integration.PIService;
import com.catl.integration.pi.part.fae.DTZFAECreateResponse;
import com.catl.integration.pi.part.fae.DTZFAECreateResponse.TRETURN;
import com.catl.part.PartConstant;
import com.catl.part.classification.resource.CATLNodeConfigRB;
import com.ptc.core.lwc.common.view.PropertyValueReadView;
import com.ptc.core.lwc.common.view.TypeDefinitionReadView;
import com.ptc.core.lwc.server.LWCStructEnumAttTemplate;
import com.ptc.windchill.csm.client.helpers.CSMTypeDefHelper;

import wt.fc.ObjectReference;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.util.WTMessage;
import wt.vc.wip.WorkInProgressHelper;

public class RefreshFAEStatusUtil {

	public static final String PV_NODE_DISPLAY_NAME = "nodeDisplayName";
	public static final String PV_NODE_NAME = "nodeName";
	public static final String PV_LOCALE_STR = "localeStr";
	public static final String WF_TEMPLATE_NAME = "更新分类的部件FAE状态";
	public static final String RESOURCE = "com.catl.part.classification.resource.CATLNodeConfigRB";
	public static final String SEPARATOR_COMMA = ",";
	public static final String FLAG_SUCCESS = "S";
	public static final String COMPANY = "CATL";
	
	public static void startWF(LWCStructEnumAttTemplate node, String locale) throws WTException{
		if(node != null){
			HashMap<String, Object> variables = new HashMap<String, Object>();
			variables.put(PV_NODE_NAME, node.getName());
			variables.put(PV_NODE_DISPLAY_NAME, getDisplayName(node));
			variables.put(PV_LOCALE_STR, locale);
			WorkflowUtil.startWorkFlow(WF_TEMPLATE_NAME, null, variables);
		}
	}
	
	public static PIResultMessage refreshFaeStatus(String nodeName) {
		PIResultMessage result = new PIResultMessage();
		Set<String> results = new HashSet<String>();
		if(StringUtils.isNotBlank(nodeName)){
			try {
				Set<WTPart> set = PartUtil.getLastedPartByStringIBAValue("", PartConstant.IBA_CLS, nodeName);
				for (WTPart part : set) {
					if(WorkInProgressHelper.isCheckedOut(part)){
						part = (WTPart) WorkInProgressHelper.service.originalCopyOf(part);
					}
					String faeStatus = needRefresh(part);
					if(StringUtils.isNotBlank(faeStatus)){
						Persistable p = IBAUtil.setIBAVaue(part.getMaster(), PartConstant.IBA_CATL_FAEStatus, faeStatus);
						PersistenceHelper.manager.save(p);
						results.add(part.getNumber());
					}
				}
				result.setSuccess(true);
				result.setResultInfo(StringUtils.join(results, ","));
			} catch (WTException e) {
				e.printStackTrace();
				result.setSuccess(false);
				result.setExceptionMsg(e.getLocalizedMessage());
			}
		}
		return result;
	}
	
	public static PIResultMessage sendToERP(String allNumbers, String failNumbers, ObjectReference self, String locale) {
		PIResultMessage result = new PIResultMessage();
		try {
			if(StringUtils.isNotBlank(allNumbers)){
				
				Set<String[]> sendInfos = new HashSet<String[]>();
				Set<String> sends = getSendNumbers(convertStrToSet(allNumbers));
				if(StringUtils.isNotBlank(failNumbers)){
					loadSendInfos(sendInfos, convertStrToSet(failNumbers));
				}
				else {
					loadSendInfos(sendInfos, sends);
				}
				
				PIService service = PIService.getInstance();
				DTZFAECreateResponse response = service.sendFAE(COMPANY, sendInfos);
				Set<String[]> dataSet = new HashSet<String[]>();
				if(isSuccess(response.getEACKNOW().getResult())){
					result.setSuccess(true);
					loadAllRowInfos(dataSet, sends, convertStrToSet(allNumbers));
				}
				else {
					result.setSuccess(false);
					Set<String> fails = new HashSet<String>();
					List<TRETURN> list = response.getTRETURN();
					for (TRETURN tr : list) {
						if(!isSuccess(tr.getSTATUS())){
							String failNumber = tr.getMATNR();
							String fialMsg = tr.getMESSAGE();
							dataSet.add(getRowInfo(failNumber, fialMsg));
							fails.add(failNumber);
						}
					}
					result.setSendFailList(StringUtils.join(fails, SEPARATOR_COMMA));
				}
				result.setResultInfo(getDataListTable(dataSet,locale));
			}
			else {
				result.setSuccess(true);
				result.setResultInfo(getDataListTable(null,locale));
			}
		} catch (WTException e) {
			e.printStackTrace();
			result.setSuccess(false);
			result.setExceptionMsg(e.getLocalizedMessage());
		}
		return result;
	}
	
	private static Set<String> convertStrToSet(String strs){
		Set<String> set = new HashSet<String>();
		if(StringUtils.isNotBlank(strs)){
			for(String str : StringUtils.split(strs, SEPARATOR_COMMA)){
				set.add(str);
			}
		}
		return set;
	}
	
	private static Set<String> getSendNumbers(Set<String> allNumbers) throws WTException{
		Set<String> sends = new HashSet<String>();
		for(String number : allNumbers){
			if(isSendERP(number)){
				sends.add(number);
			}
		}
		return sends;
	}
	
	private static boolean isSendERP(String number) throws WTException{
		WTPartMaster master = PartUtil.getWTPartMaster(number);
		WTPart part = PartUtil.getLatestReleasedPart(master);
		if(part != null){
			return true;
		}
		return false;
	}
	
	private static String[] getRowInfo(String number, String message) throws WTException {
		String[] row = new String[4];
		WTPartMaster master = PartUtil.getWTPartMaster(number);
		row[0] = master.getNumber();
		row[1] = master.getName();
		row[2] = (String) IBAUtil.getIBAValue(master, PartConstant.IBA_CATL_FAEStatus);
		row[3] = message;
		return row;
	}
	
	private static void loadSendInfos(Set<String[]> infos, Set<String> sendNumbers) throws WTException{
		for(String number : sendNumbers){
			String[] row = getRowInfo(number, WTMessage.getLocalizedMessage(RESOURCE, CATLNodeConfigRB.SEND_ERP_Y));
			infos.add(row);
		}
	}
	
	private static void loadAllRowInfos(Set<String[]> infos, Set<String> sendNumbers, Set<String> allNumbers) throws WTException{
		for (String number : allNumbers) {
			String message;
			if(sendNumbers.contains(number)){
				message = WTMessage.getLocalizedMessage(RESOURCE, CATLNodeConfigRB.SEND_ERP_Y);
			}
			else {
				message = WTMessage.getLocalizedMessage(RESOURCE, CATLNodeConfigRB.SEND_ERP_N);
			}
			String[] row = getRowInfo(number, message);
			infos.add(row);
		}
	}
	
	private static boolean isSuccess(String result){
		return StringUtils.equals(result, FLAG_SUCCESS);
	}
	/**
	 * 是否需要更改FAE状态
	 * 1、如果物料的FAE状态为“不需要”，根据5.3.1最新的FAE规则。计算出“需要”，则成熟度1的物料改为“未发起”，成熟度3或6的物料改为“已完成”
	 * 2、如果物料的FAE状态为“未发起”/“已完成”，根据5.3.1最新的FAE规则。计算出“不需要”，则将物料FAE状态改为 “不需要”
	 * @param part
	 * @param nodeName
	 * @return	null表示不需要更改
	 * @throws WTException
	 */
	public static String needRefresh(WTPart part) throws WTException{
		return needRefresh(part, part.getSource().toString());
	}
	/**
	 * 是否需要更改FAE状态
	 * 1、如果物料的FAE状态为“不需要”，根据5.3.1最新的FAE规则。计算出“需要”，则成熟度1的物料改为“未发起”，成熟度3或6的物料改为“已完成”
	 * 2、如果物料的FAE状态为“未发起”/“已完成”，根据5.3.1最新的FAE规则。计算出“不需要”，则将物料FAE状态改为 “不需要”
	 * @param part
	 * @param source 改变后的采购类型
	 * @return	null表示不需要更改
	 * @throws WTException
	 */
	public static String needRefresh(WTPart part,String source) throws WTException{
		String currentMaturity = (String)IBAUtil.getIBAValue(part.getMaster(), PartConstant.IBA_CATL_Maturity);
		String currentFaeStatus = (String)IBAUtil.getIBAValue(part.getMaster(), PartConstant.IBA_CATL_FAEStatus);
		String nodeName = (String)IBAUtil.getIBAValue(part, PartConstant.IBA_CLS);
		String faeStatus = getNewFAEStatusValue(NodeConfigHelper.getNodeConfig(nodeName), part,source);
		
		return needRefresh(currentFaeStatus, faeStatus, currentMaturity);
	}
	/**
	 * 是否需要更改FAE状态
	 * 1、如果物料的FAE状态为“不需要”，根据5.3.1最新的FAE规则。计算出“需要”，则成熟度1的物料改为“未发起”，成熟度3或6的物料改为“已完成”
	 * 2、如果物料的FAE状态为“未发起”/“已完成”，根据5.3.1最新的FAE规则。计算出“不需要”，则将物料FAE状态改为 “不需要”
	 * @param currentFaeStatus 当前FAE状态
	 * @param faeStatus	计算后的FAE状态
	 * @param currentMaturity 物料成熟度
	 * @return	null表示不需要更改
	 * @throws WTException
	 */
	public static String needRefresh(String currentFaeStatus, String faeStatus, String currentMaturity){
		if((currentFaeStatus == null || currentFaeStatus.equals(PartConstant.CATL_FAEStatus_1)) && faeStatus.equals(PartConstant.CATL_FAEStatus_2)){
			if(currentMaturity.equals("1")){
				return faeStatus;
			}else if(currentMaturity.equals("3") || currentMaturity.equals("6")){
				return PartConstant.CATL_FAEStatus_4;
			}
		}else if((currentFaeStatus == null || currentFaeStatus.equals(PartConstant.CATL_FAEStatus_2) || currentFaeStatus.equals(PartConstant.CATL_FAEStatus_4))
					&& faeStatus.equals(PartConstant.CATL_FAEStatus_1)){
			return faeStatus;
		}
		return null;
	}
	
	private static String getNewFAEStatusValue(ClassificationNodeConfig nodeConfig,WTPart part){
		if(nodeConfig !=null && part != null){
			return getNewFAEStatusValue(nodeConfig,part,part.getSource().toString());
		}
		return null;
	}
	/**
	 * 根据FAE规则获取物料的FAE状态
	 * @param nodeConfig
	 * @param part
	 * @param source
	 * @return
	 */
	private static String getNewFAEStatusValue(ClassificationNodeConfig nodeConfig,WTPart part,String source){
		if(nodeConfig !=null && part != null){
			if(nodeConfig.getNeedFae()){
				if(nodeConfig.getAttributeRef().equals(AttributeForFAE.NONE)){
					return PartConstant.CATL_FAEStatus_2;
				}else if(nodeConfig.getAttributeRef().equals(AttributeForFAE.CUSTOMIZED)){
					String customize = (String) IBAUtil.getIBAValue(part, PartConstant.IBA_CATL_Customize);
					if(StringUtils.equals(customize, PartConstant.CATL_Customize_1)){
						return PartConstant.CATL_FAEStatus_2;
					}else if(StringUtils.equals(customize, PartConstant.CATL_Customize_2)){
						return PartConstant.CATL_FAEStatus_1;
					}
				}else if(nodeConfig.getAttributeRef().equals(AttributeForFAE.SOURCE)){
					if(StringUtils.equals(source, CatlConstant.MANUFACTURE_SOURCE_NAME)){
						return nodeConfig.getMakeNeedFae()==true?PartConstant.CATL_FAEStatus_2:PartConstant.CATL_FAEStatus_1;
					}else if(StringUtils.equals(source, CatlConstant.BUY_SOURCE_NAME)){
						return nodeConfig.getBuyNeedFae()==true?PartConstant.CATL_FAEStatus_2:PartConstant.CATL_FAEStatus_1;
					}else if(StringUtils.equals(source, CatlConstant.ASSISIT_SOURCE_NAME)){
						return nodeConfig.getMakeBuyNeedFae()==true?PartConstant.CATL_FAEStatus_2:PartConstant.CATL_FAEStatus_1;
					}else if(StringUtils.equals(source, CatlConstant.VIRTUAL_SOURCE_NAME)){
						return nodeConfig.getVirtualNeedFae()==true?PartConstant.CATL_FAEStatus_2:PartConstant.CATL_FAEStatus_1;
					}else if(StringUtils.equals(source, CatlConstant.CUSTOMER_SOURCE_NAME)){
						return nodeConfig.getCustomerNeedFae()==true?PartConstant.CATL_FAEStatus_2:PartConstant.CATL_FAEStatus_1;
					}
				}
			}else {
				return PartConstant.CATL_FAEStatus_1;
			}
		}
		return null;
	}
	/**
	 * 根据FAE规则获取物料的FAE状态
	 * @param part
	 * @param source	变更后的采购类型
	 * @return
	 * @throws WTException
	 */
	public static String getInitialFAEStatusValueChange(WTPart part,String source) throws WTException{
		if(part != null){
			if(part.getContainer().getName().startsWith(ContainerName.BATTERY_LIBRARY_NAME)){
				return PartConstant.CATL_FAEStatus_1;
			}
			else {
				String cls = (String)IBAUtil.getIBAValue(part, PartConstant.IBA_CLS);
				ClassificationNodeConfig nodeConfig = NodeConfigHelper.getNodeConfig(cls);
				return getNewFAEStatusValue(nodeConfig, part,source);
			}
		}
		return null;
	}
	/**
	 * 根据FAE规则获取物料的FAE状态
	 * @param part
	 * @return
	 * @throws WTException
	 */
	public static String getInitialFAEStatusValue(WTPart part) throws WTException{
		if(part != null){
			if(part.getContainer().getName().startsWith(ContainerName.BATTERY_LIBRARY_NAME)){
				return PartConstant.CATL_FAEStatus_1;
			}
			else {
				String cls = (String)IBAUtil.getIBAValue(part, PartConstant.IBA_CLS);
				ClassificationNodeConfig nodeConfig = NodeConfigHelper.getNodeConfig(cls);
				return getNewFAEStatusValue(nodeConfig, part);
			}
		}
		return null;
	}
	
	public static String getDataListTable(Set<String[]> results, String localeStr) {
		StringBuilder dataListTabe = new StringBuilder("<table border=\"1\" cellspacing=\"0\" style=\"border-collapse:collapse;\">");
		Locale locale = null;
		if(StringUtils.equals("zh_CN", localeStr)){
			locale = Locale.CHINA;
		}
		String column1 = WTMessage.getLocalizedMessage(RESOURCE, CATLNodeConfigRB.REFRESH_DATA_COLUMN_1,null,locale);
		String column2 = WTMessage.getLocalizedMessage(RESOURCE, CATLNodeConfigRB.REFRESH_DATA_COLUMN_2,null,locale);
		String column3 = WTMessage.getLocalizedMessage(RESOURCE, CATLNodeConfigRB.REFRESH_DATA_COLUMN_3,null,locale);
		String column4 = WTMessage.getLocalizedMessage(RESOURCE, CATLNodeConfigRB.REFRESH_DATA_COLUMN_4,null,locale);
		String header = WTMessage.formatLocalizedMessage("<thead><tr><td><b>{0}</b></td><td><b>{1}</b></td><td><b>{2}</b></td><td><b>{3}</b></td></tr></thead>", new Object[]{column1,column2,column3,column4});
		dataListTabe.append(header).append("<tbody>");
		if(results != null){
			for (String[] rowInfo : results) {
				dataListTabe.append(getDataListRow(rowInfo));
			}
		}
		dataListTabe.append("</tbody></table>");
		System.out.println(dataListTabe.toString());
		return dataListTabe.toString();
	}
	
	private static String getDataListRow(String[] rowInfo){
		StringBuilder row = new StringBuilder("<tr>");
		row.append("<td>").append(rowInfo[0]).append("</td>");
		row.append("<td>").append(rowInfo[1]).append("</td>");
		row.append("<td>").append(rowInfo[2]).append("</td>");
		row.append("<td>").append(rowInfo[3]).append("</td>");
		row.append("</tr>");
		return row.toString();
	}
	
	private static String getDisplayName(LWCStructEnumAttTemplate node) throws WTException{
		if(node != null){
			TypeDefinitionReadView defRV = CSMTypeDefHelper.getRV(node);
			PropertyValueReadView proRead = defRV.getPropertyValueByName("displayName");
			return proRead.getValueAsString(SessionHelper.getLocale(),true);
		}
		return "";
	}
}
