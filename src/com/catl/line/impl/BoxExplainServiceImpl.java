package com.catl.line.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import wt.fc.QueryResult;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.part.WTPartMaster;
import wt.part.WTPartUsageLink;
import wt.util.WTException;
import wt.util.WTRuntimeException;

import com.catl.common.util.PartUtil;
import com.catl.line.constant.ConstantLine;
import com.catl.line.constant.GlobalData;
import com.catl.line.helper.BoxExplainHelper;
import com.catl.line.service.BoxExplainService;
import com.catl.line.util.CommonUtil;
import com.catl.line.util.IBAUtil;
import com.catl.line.util.IBAUtility;
import com.catl.line.util.WCUtil;
import com.ptc.netmarkets.model.NmOid;

@Scope("prototype")
@Service("boxExplainService")
public class BoxExplainServiceImpl implements BoxExplainService{
	@Override
	public JSONArray getCustomer() throws Exception {
		BoxExplainHelper.loadBoxCustomerConfig();
		List<String> customers = GlobalData.boxCustomerConfig.get("customer");
		return JSONArray.fromObject(customers);
	}
	
	@Override
	public JSONArray getPackageAsk(String customer) throws Exception {
		BoxExplainHelper.loadBoxCustomerConfig();
		List<String> packageAsk = GlobalData.boxCustomerConfig.get(customer+"_packageAsk");
		return JSONArray.fromObject(conventList(packageAsk));
	}

	@Override
	public JSONArray getRemark(String customer) throws Exception {
		BoxExplainHelper.loadBoxCustomerConfig();
		List<String> remark = GlobalData.boxCustomerConfig.get(customer+"_remark");
		return JSONArray.fromObject(remark);
	}
	
	@Override
	public JSONArray getCustomerInput(String customer,String oid) throws Exception {
		BoxExplainHelper.loadBoxExplainConfig();
		List<String> input = GlobalData.boxExplainConfig.get(customer+"_input");
		List<String> retList = new ArrayList<String>();
		NmOid nmOid = NmOid.newNmOid(oid);
		WTPart part = (WTPart)nmOid.getRefObject();
		String customprojectcode=IBAUtil.getStringIBAValue(part, ConstantLine.box_explain_CustomProjectCode);
		String customcode=IBAUtil.getStringIBAValue(part, ConstantLine.box_explain_CustomCode);
		String productpn=IBAUtil.getStringIBAValue(part, ConstantLine.box_explain_ProductPN);
		String projectname=IBAUtil.getStringIBAValue(part, ConstantLine.box_explain_ProjectName);
		for(String str : input){
			String value = "";
			if(str.equals("客户代码：")){
				if(StringUtils.isEmpty(customcode)){
					value = part.getContainerName();
				}else{
					value=customcode;
				}
			}else if(str.equals("项目名称：")){
				if(StringUtils.isEmpty(projectname)){
					String path = part.getFolderPath();
					int indexStart = 0;
					int indexEnd = 0;
					if(path.contains("(")){
						indexStart = path.indexOf("(");
					}
					if(path.contains(")")){
						indexEnd = path.indexOf(")");
					}
					if(path.contains("（")){
						indexStart = path.indexOf("（");
					}
					if(path.contains("）")){
						indexEnd = path.indexOf("）");
					}
					if(indexStart != 0 && indexEnd != 0)
						value = part.getFolderPath().substring(indexStart+1,indexEnd);
				}else{
					value=projectname;
				}
				
			}else if(str.equals("产品总成PN：")){
				if(StringUtils.isEmpty(productpn)){
					 value = BoxExplainHelper.queryRoots(part, 1);
				}else{
					 value=productpn;
				}
			}else if(str.equals("客户项目代码：")){
				value=customprojectcode;
			}
			str = str+"---"+value;
			retList.add(str);
		}
		return JSONArray.fromObject(retList);
	}

	@Override
	public JSONArray getChildPN(String number) throws Exception {
		
		List<List> list = new ArrayList<List>();
		WTPart part = PartUtil.getLastestWTPartByNumber(number);
		QueryResult qr = WTPartHelper.service.getUsesWTPartMasters(part);
		List originlist=new ArrayList();
		Map linkmap=new HashMap();
		while(qr.hasMoreElements()){
		    WTPartUsageLink link = (WTPartUsageLink)qr.nextElement();
		    WTPartMaster master = link.getUses();
		    if(!master.getNumber().startsWith("55")){
		    	continue;
		    }
		    originlist.add(master.getNumber());
		    linkmap.put(master.getNumber(), link);
		}
		Collections.sort(originlist);
		List childPN;
		for(int i=0;i<originlist.size();i++){
			childPN = new ArrayList();
			String cnumber=(String) originlist.get(i);
			WTPart cpart=CommonUtil.getLatestWTpartByNumber(cnumber);
		    WTPartUsageLink link = (WTPartUsageLink) linkmap.get(cnumber);
		    String quantity=String.valueOf(link.getQuantity().getAmount());
		    if(quantity.endsWith(".0")){
		    	quantity = quantity.substring(0,quantity.length()-2);
		    }
		    childPN.add(cpart.getName());
		    childPN.add(cpart.getNumber());
		    childPN.add(quantity);
		    WTPart childPart = PartUtil.getLastestWTPartByNumber(cpart.getNumber());
		    IBAUtility utility=new IBAUtility(link);
		    childPN.add(utility.getIBAValue(ConstantLine.box_explain_PackageAsk));// link获取包装要求   link上加个包装要求属性
		    IBAUtility utility2=new IBAUtility(childPart);
		    String mtagcontent=utility2.getIBAValue(ConstantLine.var_mtag_content);
		    if(mtagcontent!=null){
		    	childPN.add(mtagcontent);  //部件的主标签内容
		    }else{
		    	childPN.add("");
		    }
		    String remark=utility.getIBAValue(ConstantLine.box_explain_Remark);
		    if(remark!=null){
		    	childPN.add(remark);  //包装要求备注
		    }else{
		    	childPN.add("");
		    }
		    list.add(childPN);
		}
		return JSONArray.fromObject(list);
	}
	
	public List<List<String>> conventList(List<String> list){
		List<List<String>> ret = new ArrayList<List<String>>();
		for(String p : list){
			List<String> temp = new ArrayList<String>();
			temp.add(p);
			ret.add(temp);
		}
		return ret;
	}

	@Override
	public String createBoxExplain(String boxExplainFromPage) throws Exception {
		String ret = BoxExplainHelper.createBoxExplain(boxExplainFromPage);
		return ret;
	}
    
	/**
	 * 获取装箱单的表头信息
	 */
	@Override
	public JSONArray getAsmInfo(String oid) throws WTRuntimeException, WTException {
		Map infomap=new HashMap();
		WTPart part=(WTPart) WCUtil.getWTObject(oid);
		infomap.put(ConstantLine.box_explain_CustomProjectCode, IBAUtil.getStringIBAValue(part, ConstantLine.box_explain_CustomProjectCode));
		infomap.put(ConstantLine.box_explain_CustomCode, IBAUtil.getStringIBAValue(part, ConstantLine.box_explain_CustomCode));
		infomap.put(ConstantLine.box_explain_ProductPN, IBAUtil.getStringIBAValue(part, ConstantLine.box_explain_ProductPN));
		infomap.put(ConstantLine.box_explain_ProjectName, IBAUtil.getStringIBAValue(part, ConstantLine.box_explain_ProjectName));
		return JSONArray.fromObject(part);
	}
}
