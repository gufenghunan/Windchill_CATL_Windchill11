package com.catl.doc.maturityUpReport;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.catl.common.constant.ContainerName;
import com.catl.common.constant.DocState;
import com.catl.common.util.CommonUtil;
import com.catl.common.util.DocUtil;
import com.catl.common.util.IBAUtil;
import com.catl.common.util.PartUtil;
import com.catl.common.util.TypeUtil;
import com.catl.doc.maturityUpReport.resource.MaturityUpReportRB;
import com.catl.part.PartConstant;
import com.catl.promotion.util.PromotionUtil;

import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.collections.WTHashSet;
import wt.lifecycle.State;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.part.WTPartSubstituteLink;
import wt.part.WTPartUsageLink;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.util.WTMessage;

public class MaturityUpReportHelper {
	
	private static Logger log = Logger.getLogger(MaturityUpReportHelper.class);
	
	private static final String RESOURCE = "com.catl.doc.maturityUpReport.resource.MaturityUpReportRB";
	public static final String TYPE_NFAEMaturityReport = "com.CATLBattery.NFAEMaturityUp3Report";

	public static Set<NFAEMaturityUp3DocPartLink> getNFAEMaturityUp3DocPartLink(WTDocumentMaster docMaster) throws WTException{
		Set<NFAEMaturityUp3DocPartLink> set = new HashSet<NFAEMaturityUp3DocPartLink>();
		if(docMaster != null){
			QueryResult qr = PersistenceHelper.manager.navigate(docMaster, NFAEMaturityUp3DocPartLink.PART_MASTER_ROLE, NFAEMaturityUp3DocPartLink.class, false);
			while(qr.hasMoreElements()){
				set.add((NFAEMaturityUp3DocPartLink)qr.nextElement());
			}
		}
		return set;
	}
	
	public static Set<String> getAllLinkPartNumbers(WTDocumentMaster docMaster) throws WTException{
		Set<String> numbers = new HashSet<String>();
		Set<NFAEMaturityUp3DocPartLink> links = getNFAEMaturityUp3DocPartLink(docMaster);
		for (NFAEMaturityUp3DocPartLink link : links) {
			numbers.add(link.getPartMaster().getNumber());
		}
		return numbers;
	}
	
	public static NFAEMaturityUp3DocPartLink getNFAEMaturityUp3DocPartLink(WTPartMaster partMaster) throws WTException{
		if(partMaster != null){
			QueryResult qr = PersistenceHelper.manager.navigate(partMaster, NFAEMaturityUp3DocPartLink.DOC_MASTER_ROLE, NFAEMaturityUp3DocPartLink.class, false);
			if(qr.hasMoreElements()){
				return (NFAEMaturityUp3DocPartLink)qr.nextElement();
			}
		}
		return null;
	}
	
	public static Set<NFAEMaturityUp3DocPartLink> getNFAEMaturityUp3DocPartLinks(WTPartMaster partMaster) throws WTException{
		Set<NFAEMaturityUp3DocPartLink> set = new HashSet<NFAEMaturityUp3DocPartLink>();
		if(partMaster != null){
			QueryResult qr = PersistenceHelper.manager.navigate(partMaster, NFAEMaturityUp3DocPartLink.DOC_MASTER_ROLE, NFAEMaturityUp3DocPartLink.class, false);
			while(qr.hasMoreElements()){
				set.add((NFAEMaturityUp3DocPartLink)qr.nextElement());
			}
		}
		return set;
	}
	
	public static WTDocumentMaster getNFAEMaturityUp3DocMaster(WTPartMaster partMaster) throws WTException{
		if(partMaster != null){
			QueryResult qr = PersistenceHelper.manager.navigate(partMaster, NFAEMaturityUp3DocPartLink.DOC_MASTER_ROLE, NFAEMaturityUp3DocPartLink.class, false);
			if(qr.hasMoreElements()){
				NFAEMaturityUp3DocPartLink link = (NFAEMaturityUp3DocPartLink)qr.nextElement();
				return link.getDocMaster();
			}
		}
		return null;
	}
	
	public static void checkInitialPart(WTPart part) throws WTException{
		Set<String> docNumbers = new HashSet<String>();
		WTPartMaster master = (WTPartMaster)part.getMaster();
		QueryResult qr = PersistenceHelper.manager.navigate(master, NFAEMaturityUp3DocPartLink.DOC_MASTER_ROLE, NFAEMaturityUp3DocPartLink.class, false);
		while(qr.hasMoreElements()){
			NFAEMaturityUp3DocPartLink link = (NFAEMaturityUp3DocPartLink)qr.nextElement();
			if(link.getInitialPart().equals(part)){
				docNumbers.add(link.getDocMaster().getNumber());
			}
		}
		if(!docNumbers.isEmpty()){
			throw new WTException(WTMessage.getLocalizedMessage(RESOURCE, MaturityUpReportRB.ERROR_DELETE_INITIALPART, new Object[]{docNumbers.toString()}));
		}
	}
	
	public static NFAEMaturityUp3DocPartLink getNFAEMaturityUp3DocPartLink(WTDocumentMaster docMaster, WTPartMaster partMaster) throws WTException{
		QueryResult qr = PersistenceHelper.manager.find(NFAEMaturityUp3DocPartLink.class, docMaster, NFAEMaturityUp3DocPartLink.DOC_MASTER_ROLE, partMaster);
		if(qr.hasMoreElements()){
			return (NFAEMaturityUp3DocPartLink)qr.nextElement();
		}
		return null;
	}
	
	public static void removeAllLinks(WTDocumentMaster docMaster) throws WTException{
		Set<NFAEMaturityUp3DocPartLink> links = getNFAEMaturityUp3DocPartLink(docMaster);
		if(!links.isEmpty()){
			WTHashSet set = new WTHashSet();
			set.addAll(links);
			PersistenceHelper.manager.delete(set);
		}
	}
	
	public static void createNFAEMaturityUp3DocPartLink(WTDocumentMaster docMaster, Set<WTPart> parts) throws WTException{
		try {
			WTHashSet set = new WTHashSet();
			Set<String> numbers = new HashSet<String>();
			for (WTPart part : parts) {
				//NFAEMaturityUp3DocPartLink link = getNFAEMaturityUp3DocPartLink((WTPartMaster)part.getMaster());
//				if(link != null){
//					if(!link.getDocMaster().equals(docMaster)){
//						numbers.add(part.getNumber());
//					}
//				}
//				else {
				NFAEMaturityUp3DocPartLink link = NFAEMaturityUp3DocPartLink.newLink(docMaster, part);
					set.add(link);
//				}
			}
//			if(!numbers.isEmpty()){
//				throw new WTException(WTMessage.getLocalizedMessage(RESOURCE, MaturityUpReportRB.ERROR_EXIST_OTHER_LINK, new Object[]{numbers.toString()}));
//			}
			if(!set.isEmpty()){
				PersistenceHelper.manager.save(set);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new WTException(e.getLocalizedMessage());
		}
		
	}
	
	public static boolean allowAddPart(WTDocumentMaster docMaster, WTPart part) throws WTException{
		if(part != null){
			if(isBatteryPart(part)){
				return false;
			}
			else if(!State.RELEASED.equals(part.getLifeCycleState())){
				return false;
			}
			else {
				String faeStatus = (String)IBAUtil.getIBAValue(part, PartConstant.IBA_CATL_FAEStatus);
				if(!StringUtils.equals(faeStatus, PartConstant.CATL_FAEStatus_1)){
					return false;
				}
				else {
					String maturity = (String)IBAUtil.getIBAValue(part, PartConstant.IBA_CATL_Maturity);
					if(!StringUtils.equals(maturity, "1")){
						return false;
					}
					else if(hasOtherLink(docMaster, (WTPartMaster)part.getMaster())){
						return false;
					}
				}
				if (PromotionUtil.isECRADUndone(part)) {
					return false;
				}
				if(PromotionUtil.isSourceChangeUndone(part, null) > 0){
					return false;
				}
				if(PromotionUtil.isPlatformChangeUndone(part, null) > 0){
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
	public static void checkPartWhenAdd(WTDocumentMaster docMaster, WTPart part, StringBuilder errorMsg) throws WTException{
		part = PartUtil.getLastestWTPart(part);
		if(isBatteryPart(part)){
			errorMsg.append(WTMessage.formatLocalizedMessage("不允许关联电芯件[{0}]！\n", new Object[]{part.getNumber()}));
		}
		else if(!State.RELEASED.equals(part.getLifeCycleState())){
			errorMsg.append(WTMessage.formatLocalizedMessage("物料[{0}]的最新版本状态不是“已发布”！\n", new Object[]{part.getNumber()}));
		}
		else {
			String faeStatus = (String)IBAUtil.getIBAValue(part, PartConstant.IBA_CATL_FAEStatus);
			if (!StringUtils.equals(faeStatus, PartConstant.CATL_FAEStatus_1)) {
				errorMsg.append(WTMessage.formatLocalizedMessage(
						"物料[{0}]的FAE状态属性值不是“不需要”！\n",
						new Object[] { part.getNumber() }));
			} 
//			else {
//				String maturity = (String) IBAUtil.getIBAValue(part,
//						PartConstant.IBA_CATL_Maturity);
//				if (!StringUtils.equals(maturity, "1")) {
//					errorMsg.append(WTMessage.formatLocalizedMessage(
//							"物料[{0}]的成熟度属性值不是“1”！\n",
//							new Object[] { part.getNumber() }));
//				}
//				 else if(hasOtherLink(docMaster,
//				 (WTPartMaster)part.getMaster())){
//				 errorMsg.append(WTMessage.formatLocalizedMessage("物料[{0}]与其它非FAE物料成熟度3升级报告已存在关联！\n",
//				 new Object[]{part.getNumber()}));
//				 }
//			}
			if (PromotionUtil.isECRADUndone(part)) {
				errorMsg.append(WTMessage.formatLocalizedMessage("物料[{0}]正在变更中，不能关联非FAE物料成熟度3升级报告！ \n", new Object[]{part.getNumber()}));
			}
			if(PromotionUtil.isSourceChangeUndone(part, null) > 0){
				errorMsg.append(WTMessage.formatLocalizedMessage("物料[{0}]正在采购类型更改单中，不能关联非FAE物料成熟度3升级报告！\n", new Object[]{part.getNumber()}));
			}
			if(PromotionUtil.isPlatformChangeUndone(part, null) > 0){
				errorMsg.append(WTMessage.formatLocalizedMessage("物料[{0}]正在产品线标识更改单中，不能关联非FAE物料成熟度3升级报告！\n", new Object[]{part.getNumber()}));
			}
			if(PromotionUtil.isexsitPromotion(part) > 0){
				errorMsg.append(WTMessage.formatLocalizedMessage("物料[{0}]正在设计禁用单中，不能关联非FAE物料成熟度3升级报告！\n", new Object[]{part.getNumber()}));
			}
		}
	}
	
	public static void checkPartWhenRemove(WTDocument doc, WTPart part, StringBuilder errorMsg) throws WTException{
		String maturity = (String)IBAUtil.getIBAValue(part, PartConstant.IBA_CATL_Maturity);
		if(!CommonUtil.checkifDesigner(doc, SessionHelper.getPrincipal()) && !CommonUtil.checkifDesigner(part, SessionHelper.getPrincipal())){
			errorMsg.append(WTMessage.formatLocalizedMessage("当前用户对物料[{0}]没有移除权限！\n", new Object[]{part.getNumber()}));
		}
//		else if(!StringUtils.equals(maturity, "1")){
//			errorMsg.append(WTMessage.formatLocalizedMessage("物料[{0}]的成熟度属性值不是“1”！\n", new Object[]{part.getNumber()}));
//		}
	}
	
	private static boolean hasOtherLink(WTDocumentMaster docMaster, WTPartMaster partMaster) throws WTException{
		NFAEMaturityUp3DocPartLink link = getNFAEMaturityUp3DocPartLink(partMaster);
		if(link != null && !link.getDocMaster().equals(docMaster)){
			return true;
		}
		return false;
	}
	
	public static boolean isNFAEMaturityUp3Report(WTDocument doc) throws WTException{
		if(doc != null){
			return TypeUtil.isSpecifiedType(doc, TYPE_NFAEMaturityReport);
		}
		return false;
	}
	
	public static String getLocalizedMessage(String key){
		return WTMessage.getLocalizedMessage(RESOURCE, key);
	}
	
	public static boolean isBatteryPart(WTPart part){
		if(part != null){
			String containername = part.getContainerName();
			log.info("===containername:"+containername);
			return containername.startsWith(ContainerName.BATTERY_LIBRARY_NAME);
		}
		return false;
	}
	
	public static void checkWTPartUsageLink(WTPartUsageLink link, StringBuffer message) throws WTException{
		WTPart parent = link.getUsedBy();
		String parentMaturity = (String)IBAUtil.getIBAValue(parent.getMaster(), PartConstant.IBA_CATL_Maturity);
		log.info("==checkWTPartUsageLink==parent:"+parent.getNumber()+" ==parentMaturity:"+parentMaturity);
		if(StringUtils.equals(parentMaturity, "3") || StringUtils.equals(parentMaturity, "6")){
			String childMaturity = (String)IBAUtil.getIBAValue(link.getUses(), PartConstant.IBA_CATL_Maturity);
			if(!(StringUtils.equals(childMaturity, "3") || StringUtils.equals(childMaturity, "6"))){
				message.append(WTMessage.formatLocalizedMessage("子零件[{0}]的成熟度必须为3或者6", new Object[]{link.getUses().getNumber()}));
			}
		}
	}
	
	public static void checkWTPartSubstituteLink(WTPartSubstituteLink link, StringBuffer message) throws WTException{
		WTPartUsageLink usageLink = link.getSubstituteFor();
		WTPart parent = usageLink.getUsedBy();
		String parentMaturity = (String)IBAUtil.getIBAValue(parent.getMaster(), PartConstant.IBA_CATL_Maturity);
		if(StringUtils.equals(parentMaturity, "3") || StringUtils.equals(parentMaturity, "6")){
			String maturity = (String)IBAUtil.getIBAValue(link.getSubstitutes(), PartConstant.IBA_CATL_Maturity);
			log.info("==checkWTPartSubstituteLink==part:"+link.getSubstitutes().getNumber()+" ==maturity:"+maturity);
			if(!(StringUtils.equals(maturity, "3") || StringUtils.equals(maturity, "6"))){
				message.append(WTMessage.formatLocalizedMessage("替换件[{0}]的成熟度必须为3或者6", new Object[]{link.getSubstitutes().getNumber()}));
			}
		}
	}
	
	public static boolean checkMaturity(WTPart parent, String childNumber) throws WTException{
		String parentMaturity = (String)IBAUtil.getIBAValue(parent.getMaster(), PartConstant.IBA_CATL_Maturity);
		if(StringUtils.equals(parentMaturity, "3") || StringUtils.equals(parentMaturity, "6")){
			WTPartMaster childMaster = PartUtil.getWTPartMaster(childNumber);
			String childMaturity = (String)IBAUtil.getIBAValue(childMaster, PartConstant.IBA_CATL_Maturity);
			if(!(StringUtils.equals(childMaturity, "3") || StringUtils.equals(childMaturity, "6"))){
				return false;
			}
		}
		return true;
	}
	/**
	 * 是否有关联的正在执行非FAE的流程
	 * @param part
	 * @return
	 * @throws WTException
	 */
	public static boolean isNFAEUndone(WTPart part) throws WTException{
		WTDocumentMaster docMaster = MaturityUpReportHelper.getNFAEMaturityUp3DocMaster((WTPartMaster)part.getMaster());
		if(docMaster != null){
			WTDocument doc = DocUtil.getLatestWTDocument(docMaster.getNumber());
			if(!DocState.RELEASED.equals(doc.getLifeCycleState().toString()) && !DocState.CANCELLED.equals(doc.getLifeCycleState().toString())){
				return true;
			}
		}
		return false;
	}
}
