package com.catl.common.util;

import com.ptc.prolog.pub.RunTimeException;

import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.maturity.MaturityException;
import wt.maturity.MaturityHelper;
import wt.maturity.PromotionNotice;
import wt.part.WTPart;
import wt.part.WTPartConfigSpec;
import wt.part.WTPartHelper;
import wt.part.WTPartMaster;
import wt.part.WTPartStandardConfigSpec;
import wt.util.WTException;
import wt.vc.wip.Workable;

public class RDUtil {
	/**
	 * 判断是否是测试或者研发物料组
	 * @param pbo
	 * @return
	 * @throws MaturityException
	 * @throws WTException
	 */
	public static boolean isTestOrRD(WTObject pbo) throws MaturityException, WTException{
		PromotionNotice notice = (PromotionNotice) pbo;
		QueryResult qr = MaturityHelper.service.getPromotionTargets(notice);
		boolean result = false;
		while (qr.hasMoreElements()) {
			Object obj = qr.nextElement();
			if(obj instanceof WTPart){
				WTPart part = (WTPart) obj;
				if(isTestOrRDPartNum(part.getNumber())){
					result = true;
					return result;
				}
			}
		}
		return result;
	}

	private static boolean isTestOrRDPartNum(String number) {
		String config_speical_clf_group=PropertiesUtil.getValueByKey("config_speical_clf_group");
		String[] clfgroups=config_speical_clf_group.split(",");
		for (int i = 0; i < clfgroups.length; i++) {
		    String[] splits=number.split("-");//区分电芯研发物料
			if(number.startsWith(clfgroups[i])&&splits.length==2){
				return true;
			}
		}
		
		return false;
	}
	private static String getTestOrRDGroup(String number) {
		String config_speical_clf_group=PropertiesUtil.getValueByKey("config_speical_clf_group");
		String[] clfgroups=config_speical_clf_group.split(",");
		for (int i = 0; i < clfgroups.length; i++) {
			if(number.startsWith(clfgroups[i])){
				return clfgroups[i];
			}
		}
		
		return null;
	}
	
	/**
	 * 非特殊物料组下面不能挂载 特殊物料组子件
	 * 
	 * @param workable
	 * @throws WTException 
	 * @throws RunTimeException 
	 */
	public static String checkSpeicalStruct(Workable workable) throws WTException, RunTimeException {
		WTPart part=(WTPart) workable;
		wt.fc.Persistable apersistable[] = null;
		WTPartStandardConfigSpec stdSpec = WTPartStandardConfigSpec
				.newWTPartStandardConfigSpec();
		WTPartConfigSpec configSpec = WTPartConfigSpec
				.newWTPartConfigSpec(stdSpec);
		QueryResult qr = null;
		String pnumber=part.getNumber();
		if(pnumber.startsWith("A")){
			qr = WTPartHelper.service.getUsesWTParts(part, configSpec);
			while (qr.hasMoreElements()) {
				apersistable = (wt.fc.Persistable[]) qr.nextElement();
				WTPart cpart = null;
				WTPartMaster cpartmaster = null;
				String number=null;
				if (apersistable[1] instanceof WTPartMaster) {
					cpartmaster = (WTPartMaster) apersistable[1];
					number=cpartmaster.getNumber();
				} else {
					cpart = (WTPart) apersistable[1];
					number=cpart.getNumber();
				}
				if(number.startsWith("A")){
					return "成套电箱半成品BOM中，不能再出现成套电箱半成品物料";
				}
			}
		}
		if(isTestOrRDPartNum(pnumber)){//特殊物料组下不能挂非相同物料组的子件
			qr = WTPartHelper.service.getUsesWTParts(part, configSpec);
			String pgroup=getTestOrRDGroup(pnumber);
			while (qr.hasMoreElements()) {
				apersistable = (wt.fc.Persistable[]) qr.nextElement();
				WTPart cpart = null;
				WTPartMaster cpartmaster = null;
				String number=null;
				if (apersistable[1] instanceof WTPartMaster) {
					cpartmaster = (WTPartMaster) apersistable[1];
					number=cpartmaster.getNumber();
				} else {
					cpart = (WTPart) apersistable[1];
					number=cpart.getNumber();
				}
			   if(!number.startsWith(pgroup)){//非它本身的物料组
				   if(pnumber.startsWith("A")){
					   if(number.startsWith("TM")){//非RD的特殊物料组
						   return "测试物料"+number+"只能使用在测试物料组的BOM中，不能使用在其它类型物料组BOM中";
					   }else if(number.startsWith("RD")){
						   return "研发物料"+number+"只能使用在研发物料组的BOM中，不能使用在其它类型物料组BOM中";
					   }
				   }else if(pnumber.startsWith(pgroup)){
					   if(isTestOrRDPartNum(number)){
					    return pgroup+"物料BOM下只能使用"+pgroup+"物料和正常物料";
					   }
				   }
				}

			}
		}
		if(!isTestOrRDPartNum(pnumber)){//非特殊物料组下不能挂载特殊物料组的子件
			qr = WTPartHelper.service.getUsesWTParts(part, configSpec);
			while (qr.hasMoreElements()) {
				apersistable = (wt.fc.Persistable[]) qr.nextElement();
				WTPart cpart = null;
				WTPartMaster cpartmaster = null;
				String number=null;
				if (apersistable[1] instanceof WTPartMaster) {
					cpartmaster = (WTPartMaster) apersistable[1];
					number=cpartmaster.getNumber();
				} else {
					cpart = (WTPart) apersistable[1];
					number=cpart.getNumber();
				}
				if(isTestOrRDPartNum(number)){
					String config_speical_clf_group=PropertiesUtil.getValueByKey("config_speical_clf_group");
					return config_speical_clf_group+"的物料组部件不能挂载在其他物料组下";
				}

			}
		}
		return "";
		
	
	}
}
