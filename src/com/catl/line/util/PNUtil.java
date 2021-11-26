package com.catl.line.util;

import java.rmi.RemoteException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.inf.container.WTContainer;
import wt.inf.library.WTLibrary;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.part.WTPartConfigSpec;
import wt.part.WTPartDescribeLink;
import wt.part.WTPartHelper;
import wt.part.WTPartMaster;
import wt.part.WTPartStandardConfigSpec;
import wt.part.WTPartUsageLink;
import wt.pds.StatementSpec;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTAttributeNameIfc;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.struct.StructHelper;

import com.catl.line.constant.ConstantLine;
import com.catl.line.exception.LineException;
import com.ptc.core.lwc.server.LWCStructEnumAttTemplate;

public class PNUtil implements RemoteAccess {
	/**
	 * 获取所有的母PN部件
	 * 
	 * @return
	 * @throws Exception
	 */
	public static List<WTPart> getParentPNs() throws Exception {
		List<WTPart> pns = new ArrayList<WTPart>();
		QuerySpec qs = new QuerySpec();
		qs.setAdvancedQueryEnabled(true);
		int partIndex = qs.appendClassList(WTPart.class, true);
		int libIndex = qs.addClassList(WTLibrary.class, false);

		SearchCondition sc = new SearchCondition(WTPart.class,
				WTAttributeNameIfc.LATEST_ITERATION, SearchCondition.IS_TRUE);
		qs.appendWhere(sc, new int[] { partIndex });

		qs.appendAnd();
		qs.appendWhere(new SearchCondition(WTPart.class, WTPart.CONTAINER_ID,
				WTContainer.class, WTAttributeNameIfc.ID_NAME), new int[] {
				partIndex, libIndex });

		qs.appendAnd();
		qs.appendWhere(new SearchCondition(WTLibrary.class, WTContainer.NAME,
				"=", ConstantLine.libary_lineparentpn), new int[] { libIndex });
		QueryResult qr = PersistenceHelper.manager.find(qs);
		while (qr.hasMoreElements()) {
			Persistable[] persistable = (Persistable[]) qr.nextElement();
			WTPart part = (WTPart) persistable[0];
			String val = IBAUtil.getStringIBAValue(part.getMaster(),
					ConstantLine.var_maturity);
			int flag = -1;
			if (!StringUtils.isEmpty(val)) {
				try {
					flag = Integer.valueOf(val);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			System.out.println(flag);
			if (flag >= ConstantLine.var_basicmaturitylevel) {
				pns.add(part);
			}
		}
		return pns;
	}

	public static void main(String[] args) throws Exception {
		RemoteMethodServer rms = RemoteMethodServer.getDefault();
		rms.setUserName("wcadmin");
		rms.setPassword("wcadmin");
		rms.invoke("test", PNUtil.class.getName(), null, null, null);
	}

	public static void test() throws Exception {
		WTPart part = (WTPart) WCUtil.getWTObject("OR:wt.part.WTPart:193432");
		WTPart currentpart = (WTPart) WCUtil
				.getWTObject("OR:wt.part.WTPart:193381");
		boolean flag = isBigBellows(part, currentpart);
		System.out.println(flag);
	}

	/**
	 * 根据母PN，获取衍生PN
	 * 
	 * @param number
	 * @param limit_childpn
	 *            衍生PN查询出来个数限制
	 * @return
	 * @throws WTException
	 */
	public static List<WTPart> getChildPNs(String number, int limit_childpn)
			throws WTException {
		List<WTPart> pns = new ArrayList<WTPart>();
		QuerySpec qs = new QuerySpec(WTPart.class);
		qs.setAdvancedQueryEnabled(true);
		SearchCondition sc1 = new SearchCondition(WTPart.class, WTPart.NUMBER,
				SearchCondition.LIKE, number + "%");
		qs.appendWhere(sc1);
		qs.appendAnd();
		SearchCondition sc2 = new SearchCondition(WTPart.class,
				WTAttributeNameIfc.LATEST_ITERATION, SearchCondition.IS_TRUE);
		qs.appendWhere(sc2);
		qs.appendOrderBy(new OrderBy(new ClassAttribute(WTPart.class,
				WTPart.NUMBER), true), new int[] { 0 });// 按编号倒序排列
		System.out.println(qs);
		QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
		while (qr.hasMoreElements()) {
			WTPart wt = (WTPart) qr.nextElement();
			if (pns.size() == limit_childpn) {
				break;
			} else {
				if (!wt.getNumber().equals(number)) {
					pns.add(wt);
				}
			}

		}
		return pns;
	}

	/**
	 * 根据母PN，获取衍生PN
	 * 
	 * @param number
	 * @return
	 * @throws WTException
	 */
	public static List<WTPart> getChildPNs(String number) throws WTException {
		List<WTPart> pns = new ArrayList<WTPart>();
		QuerySpec qs = new QuerySpec(WTPart.class);
		qs.setAdvancedQueryEnabled(true);
		SearchCondition sc1 = new SearchCondition(WTPart.class, WTPart.NUMBER,
				SearchCondition.LIKE, number + "%");
		qs.appendWhere(sc1);
		qs.appendAnd();
		SearchCondition sc2 = new SearchCondition(WTPart.class,
				WTAttributeNameIfc.LATEST_ITERATION, SearchCondition.IS_TRUE);
		qs.appendWhere(sc2);
		qs.appendOrderBy(new OrderBy(new ClassAttribute(WTPart.class,
				WTPart.NUMBER), true), new int[] { 0 });
		System.out.println(qs);
		QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
		while (qr.hasMoreElements()) {
			WTPart wt = (WTPart) qr.nextElement();
			if (!wt.getNumber().equals(number)) {
				pns.add(wt);
			}
		}
		return pns;
	}

	/**
	 * 获取衍生PN下一个流水码
	 * 
	 * @param number
	 * @return
	 * @throws WTException
	 */
	public static String getNextPNNum(String number) throws WTException {
		QuerySpec qs = new QuerySpec(WTPart.class);
		qs.setAdvancedQueryEnabled(true);
		SearchCondition sc1 = new SearchCondition(WTPart.class, WTPart.NUMBER,
				SearchCondition.LIKE, number + "%");
		qs.appendWhere(sc1);
		qs.appendAnd();
		SearchCondition sc2 = new SearchCondition(WTPart.class,
				WTAttributeNameIfc.LATEST_ITERATION, SearchCondition.IS_TRUE);
		qs.appendWhere(sc2);
		qs.appendOrderBy(new OrderBy(new ClassAttribute(WTPart.class,
				WTPart.NUMBER), true), new int[] { 0 });
		QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
		if (qr.hasMoreElements()) {
			WTPart wt = (WTPart) qr.nextElement();
			if (!wt.getNumber().equals(number)) {
				String code = wt.getNumber().substring(number.length(),
						wt.getNumber().length());
				DecimalFormat f = new DecimalFormat("0000");
				String numbersuffix = f.format(Integer.valueOf(Integer
						.valueOf(code) + 1));
				return number + numbersuffix;
			} else {
				return number + "0001";
			}
		}
		return number + "0001";
	}

	/**
	 * 获取母PN下一层部件结构,包含波纹管、导线、哑银标签
	 * 
	 * @param part
	 * @return
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public static Map<WTPartUsageLink, Integer> getPnBom(WTPart part)
			throws WTException, WTPropertyVetoException {
		Map<WTPartUsageLink, Integer> result = new HashMap<WTPartUsageLink, Integer>();
		Persistable[] apersistable = (Persistable[]) null;
		WTPartStandardConfigSpec stdSpec = WTPartStandardConfigSpec
				.newWTPartStandardConfigSpec();
		WTPartConfigSpec configSpec = WTPartConfigSpec
				.newWTPartConfigSpec(stdSpec);
		QueryResult qr = WTPartHelper.service.getUsesWTParts(part, configSpec);
		if (qr == null || qr.size() == 0) {
			return new HashMap();
		}
		do {
			apersistable = (Persistable[]) qr.nextElement();
			WTPartUsageLink link = (WTPartUsageLink) apersistable[0];
			double count = link.getQuantity().getAmount();
			WTPartMaster cpartmaster = (WTPartMaster) link.getRoleBObject();
			WTPart cpart = (WTPart) CommonUtil.getLatestVersionOf(cpartmaster);
			LWCStructEnumAttTemplate lwc = NodeUtil
					.getLWCStructEnumAttTemplateByPart(cpart);
			if (lwc != null
					&& (lwc.getName().startsWith(ConstantLine.config_bom_type1)
							|| lwc.getName().startsWith(
									ConstantLine.config_bom_type2) || lwc
							.getName()
							.startsWith(ConstantLine.config_bom_type3))) {
				result.put(link, (int) count);
			}
		} while (qr.hasMoreElements());
		return result;
	}
	/**
	 * 获取母PN下一层部件结构,包含波纹管、导线、哑银标签
	 * 
	 * @param part
	 * @return
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public static Map<String, Integer> getMpnBom(WTPart part)
			throws WTException, WTPropertyVetoException {
		Map<String, Integer> result = new HashMap<String, Integer>();
		Persistable[] apersistable = (Persistable[]) null;
		WTPartStandardConfigSpec stdSpec = WTPartStandardConfigSpec
				.newWTPartStandardConfigSpec();
		WTPartConfigSpec configSpec = WTPartConfigSpec
				.newWTPartConfigSpec(stdSpec);
		QueryResult qr = WTPartHelper.service.getUsesWTParts(part, configSpec);
		if (qr == null || qr.size() == 0) {
			return new HashMap();
		}
		do {
			apersistable = (Persistable[]) qr.nextElement();
			WTPartUsageLink link = (WTPartUsageLink) apersistable[0];
			double count = link.getQuantity().getAmount();
			WTPartMaster cpartmaster = (WTPartMaster) link.getRoleBObject();
			WTPart cpart = (WTPart) CommonUtil.getLatestVersionOf(cpartmaster);
			LWCStructEnumAttTemplate lwc = NodeUtil
					.getLWCStructEnumAttTemplateByPart(cpart);
			if (lwc != null
					&& (lwc.getName().startsWith(ConstantLine.config_bom_type1)
							|| lwc.getName().startsWith(
									ConstantLine.config_bom_type2) || lwc
							.getName()
							.startsWith(ConstantLine.config_bom_type3))) {
				WTPartMaster currentpart=(WTPartMaster) link.getRoleBObject();
				result.put(currentpart.getNumber(), (int) count);
			}
		} while (qr.hasMoreElements());
		return result;
	}


	/**
	 * 判断波纹管规格
	 * 
	 * @param part
	 * @param currentpart
	 * @return
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public static boolean isBigBellows(WTPart part, WTPart currentpart)
			throws WTException, WTPropertyVetoException {
		Map<String, String> result = new HashMap<String, String>();
		double probellowsval = 0.0;
		Persistable[] apersistable = (Persistable[]) null;
		WTPartStandardConfigSpec stdSpec = WTPartStandardConfigSpec
				.newWTPartStandardConfigSpec();
		WTPartConfigSpec configSpec = WTPartConfigSpec
				.newWTPartConfigSpec(stdSpec);
		QueryResult qr = WTPartHelper.service.getUsesWTParts(part, configSpec);
		do {
			apersistable = (Persistable[]) qr.nextElement();
			WTPartUsageLink link = (WTPartUsageLink) apersistable[0];
			WTPartMaster cpartmaster = (WTPartMaster) link.getRoleBObject();
			WTPart cpart = (WTPart) CommonUtil.getLatestVersionOf(cpartmaster);
			LWCStructEnumAttTemplate lwc = NodeUtil
					.getLWCStructEnumAttTemplateByPart(cpart);
			if (lwc != null) {
				if (lwc.getName().startsWith(ConstantLine.config_bom_type1)) {
					IBAUtility iba = new IBAUtility(cpart);
					String inner_Diameter_Str = iba.getIBAValue(ConstantLine.var_inner_diameter);
					double bellowsval = 0.0;
					if (inner_Diameter_Str != null) {
						inner_Diameter_Str = inner_Diameter_Str.split(" ")[0];
						bellowsval = Double.valueOf(inner_Diameter_Str);
					}
					if (probellowsval == 0.0) {
						result.put(ConstantLine.config_bom_type1_name,cpart.getNumber());
						probellowsval = bellowsval;
					} else {
						String prenumber = result.get(ConstantLine.config_bom_type1_name);
						if (result.containsKey(ConstantLine.config_bom_type1_name1)) {
							throw new LineException(ConstantLine.exception_bellowscounterror);
						}
						if (bellowsval > probellowsval) {
							result.put(ConstantLine.config_bom_type1_name1,
									cpart.getNumber());
							result.put(ConstantLine.config_bom_type1_name2,
									prenumber);
						} else {
							result.put(ConstantLine.config_bom_type1_name1,
									prenumber);
							result.put(ConstantLine.config_bom_type1_name2,
									cpart.getNumber());
						}
					}

				}
			}
		} while (qr.hasMoreElements());
		System.out.println(result.size());
		if (result.size() != 3) {
			throw new LineException("找不到波纹管或波纹管数目少于两个!");
		} else if (result.get(ConstantLine.config_bom_type1_name1).equals(
				currentpart.getNumber())) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * 删除说明文档关联
	 * @param document
	 * @param part
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public static boolean deleteDescriptionLink(WTDocument document, WTPart part)
			throws WTException, WTPropertyVetoException {
		WTDocumentMaster wtdocumentmaster = (WTDocumentMaster) document
				.getMaster();
		List<WTPartDescribeLink> links = getPartDescriptionLink(part, wtdocumentmaster);
		for (int i = 0; i < links.size(); i++) {
			WTPartDescribeLink wtPartDescribeLinklink=links.get(i);
			if (wtPartDescribeLinklink != null) {
				PersistenceServerHelper.manager.remove(wtPartDescribeLinklink);
			}
		}
		List<WTPartDescribeLink> rlinks = getPartDescriptionLink(part, wtdocumentmaster);
		if(rlinks.size()==0){
			return true;
		}else{
			return false;
		}
	}

	public static List<WTPartDescribeLink> getPartDescriptionLink(WTPart wtpart,
			WTDocumentMaster wtdocumentmaster) throws WTException {
		 List<WTPartDescribeLink>  links=new ArrayList<WTPartDescribeLink>();
		QueryResult qr = PersistenceHelper.manager.navigate(wtpart,
				WTPartDescribeLink.DESCRIBED_BY_ROLE, WTPartDescribeLink.class,
				false);
		while (qr.hasMoreElements()) {
			WTPartDescribeLink link = (WTPartDescribeLink) qr.nextElement();
			links.add(link);
		}
		return links;
	}
	
	public static void removePartUseageLink(WTPart wtpart,
			WTPartMaster master) throws WTException {
		QueryResult qr = PersistenceHelper.manager.find(WTPartUsageLink.class, wtpart, WTPartUsageLink.USED_BY_ROLE, master);
		while (qr.hasMoreElements()) {
			WTPartUsageLink link = (WTPartUsageLink) qr.nextElement();
			PersistenceServerHelper.manager.remove(link);
		}
	}
	public static boolean hasPartUseageLink(WTPart wtpart,WTPartMaster master) throws WTException {
		QueryResult qr = PersistenceHelper.manager.find(WTPartUsageLink.class, wtpart, WTPartUsageLink.USED_BY_ROLE, master);
		while (qr.hasMoreElements()) {
			return true;
		}
		return false;
	}
	public static void removePartUse(WTPart part) throws WTException{
		QueryResult pparts=StructHelper.service.navigateUsedBy(part.getMaster());
		while(pparts.hasMoreElements()){
			WTPart wtpart=(WTPart) pparts.nextElement();
			removePartUseageLink(wtpart,part.getMaster());
		}
	}

	/**
	 * 创建文档和部件说明关系
	 * 
	 * @param document
	 * @param part
	 * @throws WTException
	 */
	public static boolean createDescriptionLink(WTDocument document, WTPart part)
			throws WTException {
		WTDocumentMaster wtdocumentmaster = (WTDocumentMaster) document
				.getMaster();
		List<WTPartDescribeLink> links = getPartDescriptionLink(
				part, wtdocumentmaster);
		if (links.size()==0) {
			WTPartDescribeLink wtPartDescribeLinklink1 = WTPartDescribeLink
					.newWTPartDescribeLink(part, document);
			PersistenceServerHelper.manager.insert(wtPartDescribeLinklink1);
			wtPartDescribeLinklink1 = (WTPartDescribeLink) PersistenceHelper.manager
					.refresh(wtPartDescribeLinklink1);
		}
		List<WTPartDescribeLink> rlinks = getPartDescriptionLink(
				part, wtdocumentmaster);
		if(rlinks.size()==0){
			return false;
		}else{
			return true;
		}
	}
	
	public static WTPart updateIBA(String number,String ibaname,String value)
			throws WTException, WTPropertyVetoException, RemoteException {
		WTPart part = CommonUtil.getPartByNumber(number);
		IBAUtility partiba = new IBAUtility(part);
		if(!StringUtils.isEmpty(value)){
			partiba.setIBAValue(ibaname, value);
		}
		String specification=NodeUtil.getspecificationAttr(part);
		partiba.setIBAValue("specification",specification);
		partiba.updateAttributeContainer(part);
		partiba.updateIBAHolder(part);
		return CommonUtil.getPartByNumber(number);
	}
}
