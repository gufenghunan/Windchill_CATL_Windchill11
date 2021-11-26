package com.catl.require.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import wt.fc.QueryResult;
import wt.part.WTPart;
import wt.part.WTPartConfigSpec;
import wt.part.WTPartHelper;
import wt.part.WTPartMaster;
import wt.part.WTPartStandardConfigSpec;
import wt.part.WTPartUsageLink;
import wt.util.WTException;
import wt.vc.config.LatestConfigSpec;
import wt.vc.wip.Workable;

import com.catl.bom.workflow.BomWfUtil;
import com.catl.common.util.PropertiesUtil;
import com.catl.ecad.utils.CommonUtil;
import com.catl.line.util.IBAUtil;
import com.catl.require.constant.ConstantRequire;

public class PlatformUtil {
	public static void main(String[] args) {
		String platform="A";
		String rulestr="A,A/B/C|B,B/C|C,C";
		String[] rules=rulestr.split("\\|");
		String rule="";
		for (int i = 0; i < rules.length; i++) {
			String crule=rules[i];
			if(crule.startsWith(platform)){
				rule=crule.split(",")[1];
				break;
			}
		}
		System.out.println(rule.contains("A"));
	}
	/**
     * 根据部件查询其父件
	 * @throws WTException 
     */
	public static String checkPlatform(Workable workable) throws WTException {
		
		WTPart part=(WTPart) workable;
		wt.fc.Persistable apersistable[] = null;
		String platform=IBAUtil.getIBAStringValue(workable.getMaster(), ConstantRequire.iba_CATL_Platform);
		if(StringUtils.isEmpty(platform)){
			return "";
		}
		String verifyplatform=PropertiesUtil.getValueByKey("verifyPlatform");
		if(!StringUtils.isEmpty(verifyplatform)&&verifyplatform.equals("N")){
			return "";
		}
		WTPartStandardConfigSpec stdSpec = WTPartStandardConfigSpec
				.newWTPartStandardConfigSpec();
		WTPartConfigSpec configSpec = WTPartConfigSpec
				.newWTPartConfigSpec(stdSpec);
		QueryResult qr = WTPartHelper.service.getUsesWTParts(part, configSpec);
		String rulestr="A,A|B,A/B|C,A/B/C";//部件下可以挂载那些标识
		String[] rules=rulestr.split("\\|");
		String rule="";
		for (int i = 0; i < rules.length; i++) {
			String crule=rules[i];
			if(crule.startsWith(platform)){
				rule=crule.split(",")[1];
				break;
			}
		}
		StringBuffer buffer=new StringBuffer();
		while (qr.hasMoreElements()) {
			apersistable = (wt.fc.Persistable[]) qr.nextElement();
			WTPart child = null;
			WTPartMaster cpartmaster = null;
			if (apersistable[1] instanceof WTPartMaster) {
				cpartmaster = (WTPartMaster) apersistable[1];
				child= (WTPart)CommonUtil.getLatestVersionOf(cpartmaster);
			} else {
				child = (WTPart) apersistable[1];
			}
		   String cplatform=IBAUtil.getIBAStringValue(child.getMaster(), ConstantRequire.iba_CATL_Platform);
		   if(!rule.contains(cplatform)||StringUtils.isEmpty(cplatform)){
			   buffer.append(child.getNumber()+"产品线标识【"+cplatform+"】不能被产品线标识【"+platform+"】的部件"+part.getNumber()+"使用").append("\n");
		   }
		}
		if(buffer.length()>0){
			throw new WTException(buffer.toString());
		}
		return "";
	}
	
	/**
     * 根据部件更改后的产品线标识是否满足规则
	 * @throws WTException 
     */
	public static void checkChangePlatform(WTPart afterpart,String platform) throws WTException {
		wt.fc.Persistable apersistable[] = null;
		if(StringUtils.isEmpty(platform)){
			return;
		}
		WTPartStandardConfigSpec stdSpec = WTPartStandardConfigSpec
				.newWTPartStandardConfigSpec();
		WTPartConfigSpec configSpec = WTPartConfigSpec
				.newWTPartConfigSpec(stdSpec);
		QueryResult qr = WTPartHelper.service.getUsesWTParts(afterpart, configSpec);
		String rulestr="A,A|B,A/B|C,A/B/C";//部件下可以挂载那些标识
		String[] rules=rulestr.split("\\|");
		String rule="";
		for (int i = 0; i < rules.length; i++) {
			String crule=rules[i];
			if(crule.startsWith(platform)){
				rule=crule.split(",")[1];
				break;
			}
		}
		StringBuffer buffer=new StringBuffer();
		while (qr.hasMoreElements()) {
			apersistable = (wt.fc.Persistable[]) qr.nextElement();
			WTPart child = null;
			WTPartMaster cpartmaster = null;
			if (apersistable[1] instanceof WTPartMaster) {
				cpartmaster = (WTPartMaster) apersistable[1];
				child= (WTPart)CommonUtil.getLatestVersionOf(cpartmaster);
			} else {
				child = (WTPart) apersistable[1];
			}
		   String cplatform=IBAUtil.getIBAStringValue(child.getMaster(), ConstantRequire.iba_CATL_Platform);
		   if(!rule.contains(cplatform)){
			   if(buffer.length()<10){
				   buffer.append(child.getNumber()+"产品线标识"+cplatform+"不能被产品线标识"+platform+"的部件"+afterpart.getNumber()+"使用").append("\n");
			   }else{
				   buffer.append("...");
				   break;
			   }
			   
		   }
		}
		if(buffer.length()>0){
			throw new WTException(buffer.toString());
		}
	}
	
    public static List<WTPart> getParentPartByChildPart(WTPart part) {
    	List<WTPart> parentParts = new ArrayList<WTPart>();
        QueryResult qr;
        try {
            qr = WTPartHelper.service.getUsedByWTParts((WTPartMaster) part.getMaster());
            qr = (new LatestConfigSpec()).process(qr);
            while (qr != null && qr.hasMoreElements()) {
                Object obj = qr.nextElement();
                if (obj instanceof WTPart) {
                    WTPart parentPart = (WTPart) obj;
                    if(BomWfUtil.isLastVersion(parentPart)){
                    	parentParts.add(parentPart);
                    }
                }
            }
        } catch (WTException e) {
            e.printStackTrace();
        }
        return parentParts;
    }
    
  //通过父项和子件Master取link
    public static WTPartUsageLink getusedLinkByPart(WTPart wtparent,WTPartMaster master) throws WTException{
    	WTPartUsageLink relink = null;
    	QueryResult qr = WTPartHelper.service.getUsesWTPartMasters(wtparent);
    	
    	while(qr.hasMoreElements()){
    		WTPartUsageLink link = (WTPartUsageLink)qr.nextElement();
    		if(link.getUses().equals(master)){
    			relink = link;
    		}
    	}
    	return relink;
    }
    
    /**
     * 根据usrlink取它的可替代件
     * @throws WTException 
     */
    public static Set<WTPartMaster> getSubWTPartMaster(WTPartUsageLink link) throws WTException{
    	
    	Set<WTPartMaster>  setmaster = new HashSet<WTPartMaster>();
    	QueryResult qrmster = WTPartHelper.service.getSubstitutesWTPartMasters(link);
		while(qrmster.hasMoreElements()){
			WTPartMaster subMs = (WTPartMaster)qrmster.nextElement();
			setmaster.add(subMs);
		}
		return setmaster;
    }
    
}
