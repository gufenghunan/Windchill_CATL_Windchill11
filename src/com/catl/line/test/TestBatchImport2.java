package com.catl.line.test;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.script.ScriptException;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.drools.core.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import wt.access.AccessControlHelper;
import wt.access.AccessPermission;
import wt.doc.WTDocument;
import wt.fc.PersistenceHelper;
import wt.fc.WTObject;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.State;
import wt.maturity.Promotable;
import wt.maturity.PromotionNotice;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.part.WTPartUsageLink;
import wt.pom.Transaction;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.util.WTPropertyVetoException;
import wt.util.WTRuntimeException;
import wt.vc.wip.WorkInProgressHelper;
import wt.workflow.work.WorkItem;

import com.catl.common.util.PropertiesUtil;
import com.catl.integration.trp.GetAfterTimeParts;
import com.catl.line.constant.ConstantLine;
import com.catl.line.entity.ParentPNAttr;
import com.catl.line.exception.LineException;
import com.catl.line.helper.CadHelper;
import com.catl.line.helper.ExpressHelper;
import com.catl.line.helper.TagHelper;
import com.catl.line.util.BatchDeriveData;
import com.catl.line.util.CommonUtil;
import com.catl.line.util.IBAUtil;
import com.catl.line.util.IBAUtility;
import com.catl.line.util.NodeUtil;
import com.catl.line.util.PNUtil;
import com.catl.line.util.WCUtil;
import com.catl.line.util.WTDocumentUtil;
import com.ptc.core.lwc.server.LWCStructEnumAttTemplate;
import com.ptc.xworks.windchill.util.PromotionNoticeUtils;

public class TestBatchImport2 implements RemoteAccess{
	private static String wt_home;
	static {
		try {
			wt_home = WTProperties.getLocalProperties().getProperty("wt.home",
					"UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取所有已发布成熟度为3以上的母PN
	 */
	public JSONArray getParentPNs() throws Exception {
		List parentpns = new ArrayList();
		List<WTPart> pns = PNUtil.getParentPNs();
		for (int i = 0; i < pns.size(); i++) {
			WTPart part = pns.get(i);
			String isParentPN = IBAUtil.getStringIBAValue(part,
					ConstantLine.var_parentPN);
			if (!part.getLifeCycleState().getDisplay(Locale.CHINA)
					.equals(ConstantLine.config_released)) {// 不是已发布状态跳过
				continue;
			}
			// 不是母PN跳过
			if (StringUtils.isEmpty(isParentPN)
					|| !isParentPN.equals(ConstantLine.judgeparentPN)) {
				continue;
			}
			List row = new ArrayList();
			int count = 0;
			String var_lconnector = IBAUtil.getStringIBAValue(part,
					ConstantLine.var_lconnector);
			if (!StringUtils.isEmpty(var_lconnector)) {
				count++;
			}
			String var_rconnector = IBAUtil.getStringIBAValue(part,
					ConstantLine.var_rconnector);
			if (!StringUtils.isEmpty(var_rconnector)) {
				count++;
			}
			String var_ldconnector = IBAUtil.getStringIBAValue(part,
					ConstantLine.var_ldconnector);
			if (!StringUtils.isEmpty(var_ldconnector)) {
				count++;
			}
			String var_rdconnector = IBAUtil.getStringIBAValue(part,
					ConstantLine.var_rdconnector);
			if (!StringUtils.isEmpty(var_rdconnector)) {
				count++;
			}
			if (count == 2 || count == 3) {
				row.add(WCUtil.getOid(part));
				row.add(part.getNumber());
				row.add(part.getName());
				row.add(IBAUtil.getStringIBAValue(part,
						ConstantLine.var_linetype));
				row.add(var_lconnector);
				row.add(var_rconnector);
				row.add(var_ldconnector);
				row.add(var_rdconnector);
				row.add(IBAUtil.getStringIBAValue(part,
						ConstantLine.var_maxcablesection));
				row.add(IBAUtil.getIntegerIBAValue(part,
						ConstantLine.var_cablecount));
				row.add(IBAUtil.getIntegerIBAValue(part,
						ConstantLine.var_undercablecount));
				row.add(count);
				row.add(IBAUtil.getStringIBAValue(part,ConstantLine.var_HeatShrinkableCasing_color));
				parentpns.add(row);
			}

		}

		return JSONArray.fromObject(parentpns);
	}

	
	/**
	 * 验证线束总成PN
	 */
	public String getAsmPN(String number) throws WTException {
		if (!number.startsWith(PropertiesUtil
				.getValueByKey("create_box_explain_group"))) {
			return ConstantLine.exception_lineasmnumber;
		}
		WTPart part = CommonUtil.getPartByNumber(number);
		if (part == null) {
			return ConstantLine.exception_partnotfound;
		} else {
			if (!PropertiesUtil
					.getValueByKey("config_filter_asmpn_state")
					.contains(part.getLifeCycleState().getDisplay(Locale.CHINA))) {
				return ConstantLine.exception_partstateeror;
			}
			WTUser user = (WTUser) SessionHelper.manager.getPrincipal();
			SessionServerHelper.manager.setAccessEnforced(true);
			boolean haveaccess = AccessControlHelper.manager.hasAccess(user,
					part, AccessPermission.MODIFY);
			SessionServerHelper.manager.setAccessEnforced(false);
			if (haveaccess) {
				return "";
			} else {
				return ConstantLine.exception_notmodifyaccess;
			}
		}
	}

	
	/**
	 * 获取PDF预览路径
	 */
	public String getViewPDF(String number) throws WTException, IOException,
			PropertyVetoException {
		WTDocument doc = CommonUtil.getWTDocumentByNumber(number);
		if (doc == null) {
			throw new LineException(ConstantLine.exception_docnotfound);
		}
		String url = WTDocumentUtil.getViewAttachURL(doc);
		return url;
	}

	
	/**
	 * 获取标签的下拉值
	 */
	public JSONArray getTagBoxDesc(String coltype, String linetype, String key,
			String value) throws FileNotFoundException, IOException {
		List<String> descs = TagHelper.getTagBoxDesc(coltype, linetype, key,
				value);
		return JSONArray.fromObject(descs);
	}

	
	/**
	 * 获取所有衍生PN信息
	 */
	public JSONArray getChildPNs(String number) throws Exception {
		List parentpns = new ArrayList();
		List<WTPart> pns = PNUtil.getChildPNs(number,
				ConstantLine.limit_childpn);
		for (int i = 0; i < pns.size(); i++) {
			WTPart part = pns.get(i);
			WTUser cuser=(WTUser) SessionHelper.manager.getPrincipal();
			String cusername = cuser.getName();
			
			if (!part.getCreator().getName().equals(cusername)) {
				continue;
			}
			String isParentPN = IBAUtil.getStringIBAValue(part,
					ConstantLine.var_parentPN);
			if (!StringUtils.isEmpty(isParentPN) && isParentPN.equals(number)) {
				if(WorkInProgressHelper.isCheckedOut(part)){//有工作副本的非副本部件不通过
					if(!WorkInProgressHelper.isWorkingCopy(part)){
						String cpartnumber=part.getNumber();
						continue;
					}
				}
				List row = new ArrayList();
				row.add(WCUtil.getOid(part));
				row.add(part.getNumber());
				row.add(part.getName());
				// 正式-getIntegerIBAValue
				IBAUtility iba = new IBAUtility(part);
				row.add(iba.getIBAValue(ConstantLine.var_L1));
				row.add(iba.getIBAValue(ConstantLine.var_L2));
				row.add(iba.getIBAValue(ConstantLine.var_L3));
				row.add(iba.getIBAValue(ConstantLine.var_mtag_content));
				row.add(iba.getIBAValue(ConstantLine.var_llbenchmark));
				row.add(iba.getIBAValue(ConstantLine.var_ltagbox));
				row.add(iba.getIBAValue(ConstantLine.var_rtagbox));
				row.add(iba.getIBAValue(ConstantLine.var_ltagdesc));
				row.add(iba.getIBAValue(ConstantLine.var_rtagdesc));
				row.add(iba.getIBAValue(ConstantLine.var_dtagdesc));
				row.add(iba.getIBAValue(ConstantLine.var_pointa));
				row.add(iba.getIBAValue(ConstantLine.var_pointb));
				row.add(iba.getIBAValue(ConstantLine.var_pointc));
				row.add(iba.getIBAValue(ConstantLine.var_pointd));
				row.add(part.getCreatorFullName());
				Timestamp date=new Timestamp(part.getCreateTimestamp().getTime()+28800000);
				String str=date.toString();
				row.add(str.substring(0, str.lastIndexOf(".")));
				String checkinfo="已检入";
				if(WorkInProgressHelper.isCheckedOut(part)){
				    String owner=part.getOwnership().getOwner().getFullName();
					if(cuser.getName().equals(part.getOwnership().getOwner().getName())){
						checkinfo="已检出";
					}else{
						checkinfo="由"+owner+"检出";
					}
				}
				row.add(checkinfo);
				parentpns.add(row);
			}

		}

		return JSONArray.fromObject(parentpns);
	}

	
	/**
	 * 验证衍生PN规则是否唯一
	 */
	public String validateChildPN(String number, String values)
			throws WTException {
		JSONObject jo = JSONObject.fromObject(values);
		List<WTPart> pns = PNUtil.getChildPNs(number);
		Map<String, Object> valuesmap = jo;
		for (Entry<String, Object> entry : valuesmap.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue().toString();
			pns = filterByIBAValue(pns, key, value);//过滤掉此属性PN
			if (pns.size() == 0) {
				return "";
			}
		}
		String samepart = "";
		for (int i = 0; i < pns.size(); i++) {
			samepart = samepart + pns.get(i).getNumber() + ",";
		}
		samepart = samepart.substring(0, samepart.length() - 1);
		return samepart;
	}
  
	/**
	 * 过滤掉不等于value的部件 获取到相同value的部件集合
	 * @param pns
	 * @param key
	 * @param value
	 * @return
	 */
	private List<WTPart> filterByIBAValue(List<WTPart> pns, String key,
			String value) {
		List samelist = new ArrayList();
		for (int i = 0; i < pns.size(); i++) {
			WTPart part = pns.get(i);
			if (key.equals(ConstantLine.var_L)) {
				key = ConstantLine.var_L1;
			}
			if (ConstantLine.type_integer_attr.indexOf(key) > -1) {
				int val = (int) IBAUtil.getIntegerIBAValue(part, key);
				if (val != 0) {
					String cval = String.valueOf(val);
					if (cval.equals(value)) {
						samelist.add(part);
					}
				} else if (StringUtils.isEmpty(value)) {// 查询出来是0 新值是''没填
					samelist.add(part);
				}
			} else if (ConstantLine.type_double_attr.indexOf(key) > -1) {
				int val = (int) IBAUtil.getIntegerIBAValue(part, key);
				if (val != 0) {
					String cval = String.valueOf(val);
					if (cval.equals(value)) {
						samelist.add(part);
					}
				} else if (StringUtils.isEmpty(value)) {// 查询出来是0 新值是''没填
					samelist.add(part);
				}
			} else {
				String val = IBAUtil.getStringIBAValue(part, key);
				if (StringUtils.isEmpty(value) && StringUtils.isEmpty(val)) {
					samelist.add(part);
				} else if (val != null && value.equals(val)) {
					samelist.add(part);
				}
			}
		}
		return samelist;
	}

	/**
	 * 创建衍生PN
	 * uikeyvalue 为配置表所需的显示名和属性值
	 */
	
	public String createChildPN(String asmnumber,String number, String values,
			String containeroid, String folderoid) throws Exception {
		validateBox(asmnumber,number,"",values);
		JSONObject jo = JSONObject.fromObject(values);
		WTPart mpn = CommonUtil.getPartByNumber(number);
		String newnum = PNUtil.getNextPNNum(number);
		Transaction trx = null;
		try {
			trx = new Transaction();
			trx.start();
			//将母PN另存为为新的衍生PN
			CommonUtil.PartSaveAs(mpn, newnum, containeroid, folderoid);
			WTPart part = CommonUtil.getPartByNumber(newnum);
			if (part == null) {
				throw new LineException("创建衍生PN失败");
			}
			Map<String, Object> valuesmap = jo;
			String var_dtagdesc = (String) valuesmap
					.get(ConstantLine.var_dtagdesc);
			IBAUtility iba = new IBAUtility(part);
			for (Entry<String, Object> entry : valuesmap.entrySet()) {
				String key = entry.getKey();
				if (key.equals(ConstantLine.var_L)) {
					key = ConstantLine.var_L1;
				}
				String value = entry.getValue().toString();
				if (!StringUtils.isEmpty(value)) {
					iba.setIBAValue(key, value);
					String displayname = iba.getIBADisplayName(key);
				}
			}
			iba.setIBAValue(ConstantLine.var_parentPN, number);
			iba.updateAttributeContainer(part);
			iba.updateIBAHolder(part);
			CommonUtil.reassign(part);// 重新分配生命周期
			LifeCycleHelper.service.setLifeCycleState(part,
					State.toState(ConstantLine.var_childpn_initstate), true);
			Map<String, Object> uikeyvalue = NodeUtil
					.getClassificationAttr(part);
			uikeyvalue.put(ConstantLine.var_PN, newnum);
			changeChildPNBom(uikeyvalue, part, var_dtagdesc, false);// 修改用量
																	// 波纹管数量也数量加载到uikeyvalue
			Map dwgmap = new HashMap();
			if (StringUtils.isEmpty(var_dtagdesc)) {// 获取需要填写到dwg中的数据
				dwgmap = ExpressHelper.getExpress(
						ConstantLine.config_2connector_sheetname, uikeyvalue);
			} else {
				dwgmap = ExpressHelper.getExpress(
						ConstantLine.config_3connector_sheetname, uikeyvalue);
			}
			WTDocument doc = CommonUtil.getLatestWTDocByNumber(number);
			if (doc == null) {
				throw new LineException("找不到编码为" + number + "文档");
			}
			boolean delete = PNUtil.deleteDescriptionLink(doc, part);
			if (!delete) {
				throw new LineException("删除PN和线束autocad图纸关联失败");
			}
			CommonUtil.createDocDependsPart(newnum, newnum, part,
					ConstantLine.var_doctype_autocadDrawing,
					PropertiesUtil.getValueByKey("config_autocaddoc_folder"));// 创建新文档
			System.out.println(dwgmap);
			String mtag_content=(String) dwgmap.get("主标签内容");
		    PNUtil.updateIBA(newnum, ConstantLine.var_mtag_content, mtag_content);
			String specification=NodeUtil.getspecificationAttr(part);
		    PNUtil.updateIBA(newnum,"specification", specification);
			CadHelper.updateDocDwgAndPDF(doc, dwgmap, newnum);// 更新dwg和pdf
			WTDocument newdoc = CommonUtil.getLatestWTDocByNumber(newnum);
			boolean iscreate = PNUtil.createDescriptionLink(newdoc, part);
			if (!iscreate) {
				throw new LineException("创建衍生PN和线束autocad图纸关联失败!");
			}
			
			trx.commit();
		} catch (Exception e) {
			trx.rollback();
			throw e;
		}
		return newnum;
	}

	/**
	 * 更改衍生PN用量
	 * 
	 * @param uikeyvalue
	 * @param part
	 * @param var_dtagdesc
	 * @throws WTPropertyVetoException
	 * @throws WTException
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ScriptException
	 */
	public static void changeChildPNBom(Map<String, Object> uikeyvalue,
			WTPart part, String var_dtagdesc, boolean checkflag)
			throws WTPropertyVetoException, WTException, FileNotFoundException,
			IOException, ScriptException {
		if (checkflag) {
			part = (WTPart) WCUtil.getWorkableByPersistable(part);
		}
		Map<WTPartUsageLink, Integer> link_count = PNUtil.getPnBom(part);
		System.out.println(link_count.size());
		Set links_set = link_count.keySet();
		Iterator links_ite = (Iterator) links_set.iterator();
		while (links_ite.hasNext()) {
			WTPartUsageLink link = (WTPartUsageLink) links_ite.next();
			WTPartMaster cpartmaster = (WTPartMaster) link.getRoleBObject();
			WTPart cpart = (WTPart) CommonUtil.getLatestVersionOf(cpartmaster);
			LWCStructEnumAttTemplate lwc = NodeUtil
					.getLWCStructEnumAttTemplateByPart(cpart);
			if (lwc != null) {
				uikeyvalue.put(ConstantLine.config_clf, lwc.getName());
				uikeyvalue
						.put(ConstantLine.config_mpnuse, link_count.get(link));
				if (StringUtils.isEmpty(var_dtagdesc)) {// 获取部件的用量
					double amount = ExpressHelper.getUse(
							ConstantLine.config_2use_sheetname, uikeyvalue);
					if (lwc.getName().startsWith(ConstantLine.config_bom_type1)
							|| lwc.getName().startsWith(
									ConstantLine.config_bom_type2)) {
						link.getQuantity().setAmount(amount / 1000.0);
					}else{
						link.getQuantity().setAmount(amount);
					}
				} else {
					if (lwc.getName().startsWith(ConstantLine.config_bom_type1)) {
						boolean isBigBellows = PNUtil.isBigBellows(part, cpart);// 判断波纹管大小传入配置文件
						if (isBigBellows) {
							uikeyvalue.put(ConstantLine.config_bellows,
									ConstantLine.config_bom_type1_name1);
						} else {
							uikeyvalue.put(ConstantLine.config_bellows,
									ConstantLine.config_bom_type1_name2);
						}
					} else {
						uikeyvalue.put(ConstantLine.config_bellows, "");
					}
					double amount = ExpressHelper.getUse(
							ConstantLine.config_3use_sheetname, uikeyvalue);
					// 波纹管和导线单位换算成m
					if (lwc.getName().startsWith(ConstantLine.config_bom_type1)
							|| lwc.getName().startsWith(
									ConstantLine.config_bom_type2)) {
						link.getQuantity().setAmount(amount / 1000.0);
					}else{
						link.getQuantity().setAmount(amount);
					}
					String key = (String) uikeyvalue
							.get(ConstantLine.config_bellows);
					// 加载bom数量信息到uikeyvalue
					if (!StringUtils.isEmpty(key)) {
						uikeyvalue.put(key, amount);
					}

				}
				PersistenceHelper.manager.modify(link);
			}
		}
		if (checkflag) {
			WCUtil.checkin(part);
		}
	}

	/**
	 * 更改衍生PN用量
	 * 
	 * @param uikeyvalue
	 * @param part
	 * @param var_dtagdesc
	 * @throws WTPropertyVetoException
	 * @throws WTException
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ScriptException
	 */
	public static void changeChildPNBomByParent(Map<String, Object> uikeyvalue,
			WTPart mpart, WTPart part, String var_dtagdesc, boolean checkflag)
			throws WTPropertyVetoException, WTException, FileNotFoundException,
			IOException, ScriptException {
		if (checkflag) {
			part = (WTPart) WCUtil.getWorkableByPersistable(part);
		}
		Map<String, Integer> part_count = PNUtil.getMpnBom(mpart);// 母PN和下层部件数量关系
		Map<WTPartUsageLink, Integer> link_count = PNUtil.getPnBom(part);
		Set links_set = link_count.keySet();
		Iterator links_ite = (Iterator) links_set.iterator();
		while (links_ite.hasNext()) {
			WTPartUsageLink link = (WTPartUsageLink) links_ite.next();
			WTPartMaster cpartmaster = (WTPartMaster) link.getRoleBObject();
			WTPart cpart = (WTPart) CommonUtil.getLatestVersionOf(cpartmaster);
			LWCStructEnumAttTemplate lwc = NodeUtil
					.getLWCStructEnumAttTemplateByPart(cpart);
			if (lwc != null && part_count.containsKey(cpart.getNumber())) {
				uikeyvalue.put(ConstantLine.config_clf, lwc.getName());
				uikeyvalue.put(ConstantLine.config_mpnuse,
						part_count.get(cpart.getNumber()));
				if (StringUtils.isEmpty(var_dtagdesc)) {
					double amount = ExpressHelper.getUse(
							ConstantLine.config_2use_sheetname, uikeyvalue);
					if (lwc.getName().startsWith(ConstantLine.config_bom_type1)
							|| lwc.getName().startsWith(
									ConstantLine.config_bom_type2)) {
						link.getQuantity().setAmount(amount / 1000.0);
					}else{
						link.getQuantity().setAmount(amount);
					}
				} else {
					if (lwc.getName().startsWith(ConstantLine.config_bom_type1)) {
						boolean isBigBellows = PNUtil.isBigBellows(part, cpart);// 判断波纹管大小传入配置文件
						if (isBigBellows) {
							uikeyvalue.put(ConstantLine.config_bellows,
									ConstantLine.config_bom_type1_name1);
						} else {
							uikeyvalue.put(ConstantLine.config_bellows,
									ConstantLine.config_bom_type1_name2);
						}
					} else {
						uikeyvalue.put(ConstantLine.config_bellows, "");
					}
					double amount = ExpressHelper.getUse(
							ConstantLine.config_3use_sheetname, uikeyvalue);
					// 波纹管和导线单位换算成m
					if (lwc.getName().startsWith(ConstantLine.config_bom_type1)
							|| lwc.getName().startsWith(
									ConstantLine.config_bom_type2)) {
						link.getQuantity().setAmount(amount / 1000.0);
					}else{
						link.getQuantity().setAmount(amount);
					}
					// 加载bom数量信息到uikeyvalue
					String key = (String) uikeyvalue
							.get(ConstantLine.config_bellows);
					if (!StringUtils.isEmpty(key)) {
						uikeyvalue.put(key, amount);
					}
				}
				PersistenceHelper.manager.modify(link);
			}
		}
		if (checkflag) {
			WCUtil.checkin(part);
		}
	}

	
	/**
	 *衍生PN 关联线束总成PN
	 */
	public void relativePN(String parentnumber, String childnumber)
			throws WTException {
		if(!StringUtils.isEmpty(parentnumber)){
			CommonUtil.buildPartLink(parentnumber, childnumber);
		}
		
	}

	/**
	 * 获取dwg下载路径
	 */
	
	public String downloadDwg(String number) throws WTException, IOException,
			PropertyVetoException {
		WTDocument doc = CommonUtil.getWTDocumentByNumber(number);
		if (doc == null) {
			throw new LineException(ConstantLine.exception_docnotfound);
		}
		String url = WTDocumentUtil.getViewPrimaryURL(doc);
		return url;
	}

	/**
	 * 上传dwg文件并更新文档
	 */
	
	public void uploaddwg(MultipartFile file, String number) throws Exception {
		String wt_home = WTProperties.getLocalProperties().getProperty(
				"wt.home", "UTF-8");
		String dwgname = number + ".dwg";
		File dwgfile = new File(wt_home
				+ PropertiesUtil.getValueByKey("dwg_temp_path") + dwgname);
		file.transferTo(dwgfile);
		CadHelper.updateDocDwgAndPDF(number, dwgfile);
	}

	/**
	 * 获取衍生PN属性信息
	 */
	
	public JSONArray getChildPN(String oid) throws WTRuntimeException,
			WTException {
		WTPart part = (WTPart) WCUtil.getWTObject(oid);
		Map<String, Object> name_attr = NodeUtil
				.getClassificationNameAttr(part);
		int count = 0;
		String var_lconnector = (String) name_attr
				.get(ConstantLine.var_lconnector);
		if (!StringUtils.isEmpty(var_lconnector)) {
			count++;
		}
		String var_rconnector = (String) name_attr
				.get(ConstantLine.var_rconnector);
		if (!StringUtils.isEmpty(var_rconnector)) {
			count++;
		}
		String var_ldconnector = (String) name_attr
				.get(ConstantLine.var_ldconnector);
		if (!StringUtils.isEmpty(var_ldconnector)) {
			count++;
		}
		String var_rdconnector = (String) name_attr
				.get(ConstantLine.var_rdconnector);
		if (!StringUtils.isEmpty(var_rdconnector)) {
			count++;
		}
		name_attr.put(ConstantLine.config_connectorcount, count);
		return JSONArray.fromObject(name_attr);
	}

	/**
	 * 获取衍生PN属性信息（流程填写母PN属性时使用）
	 */
	
	public JSONArray getMPN(String oid) throws WTRuntimeException, WTException {
		List list = new ArrayList();
		WorkItem workitem = (WorkItem) WCUtil.getWTObject(oid);
		WTObject promotionnotice = (WTObject) workitem
				.getPrimaryBusinessObject().getObject();
		if (promotionnotice instanceof PromotionNotice) {
			Collection<Promotable> objs = PromotionNoticeUtils
					.getPromotionNoticeItems((PromotionNotice) promotionnotice);
			Iterator<Promotable> objiter = objs.iterator();
			while (objiter.hasNext()) {
				Promotable promotable = objiter.next();
				if (promotable instanceof WTPart) {
					List<ParentPNAttr> name_attr = NodeUtil
							.getParentPNAttrs((WTPart) promotable);
					list.add(name_attr);
				}
			}
			return JSONArray.fromObject(list);
		} else {
			return null;
		}

	}

	/**
	 * 更新衍生属性，并更新衍生PN dwg、pdf和用量
	 * uikeyvalue 可能写到dwg所需的值
	 */
	
	public String updateChildPN(String asmnumber,String oid, String values) throws Exception {
		JSONObject jo = JSONObject.fromObject(values);
		WTPart part = (WTPart) WCUtil.getWTObject(oid);
		if (!(part.getLifeCycleState().toString().equals(ConstantLine.state_design)||part.getLifeCycleState().toString().equals(ConstantLine.state_desginmodify))){
			throw new LineException("只能更新设计或设计修改状态的衍生PN");
		}
		String number = part.getNumber();
		Transaction trx = null;
		try {
			trx = new Transaction();
			trx.start();
			Map<String, Object> valuesmap = jo;
			String var_dtagdesc = (String) valuesmap
					.get(ConstantLine.var_dtagdesc);
			//更新衍生PN属性
			for (Entry<String, Object> entry : valuesmap.entrySet()) {
				String key = entry.getKey();
				if (key.equals(ConstantLine.var_L)) {
					key = ConstantLine.var_L1;
				}
				String value = entry.getValue().toString();
				if (!StringUtils.isEmpty(value)) {
					IBAUtility iba = new IBAUtility(part);
					iba.setIBAValue(key, value);
					iba.updateAttributeContainer(part);
					iba.updateIBAHolder(part);
					String displayname = iba.getIBADisplayName(key);
				}else{
					IBAUtility iba = new IBAUtility(part);
					iba.deleteIBAValueByLogical(key);
					iba.updateAttributeContainer(part);
					iba.updateIBAHolder(part);
				}
			}
			
			part=CommonUtil.getLatestWTpartByNumber(number);
			IBAUtility iba = new IBAUtility(part);
			String mpnnumber = iba.getIBAValue(ConstantLine.var_parentPN);
			validateBox(asmnumber,mpnnumber,number,values);//验证标签填写
			WTPart mpart = CommonUtil.getPartByNumber(mpnnumber);
			Map<String, Object> uikeyvalue = NodeUtil.getClassificationAttr(part);//所有分类显示名称-属性值注入到uikeyvalue供配置表使用
			uikeyvalue.put(ConstantLine.var_PN, number);
			//修改薄码用量
			changeChildPNBomByParent(uikeyvalue, mpart, part, var_dtagdesc,
					true);
			Map dwgmap = new HashMap();
			if (StringUtils.isEmpty(var_dtagdesc)) {// 获取需要填写到dwg中的数据
				dwgmap = ExpressHelper.getExpress(
						ConstantLine.config_2connector_sheetname, uikeyvalue);
			} else {
				dwgmap = ExpressHelper.getExpress(
						ConstantLine.config_3connector_sheetname, uikeyvalue);
			}
			String mtag_content=(String) dwgmap.get("主标签内容");
			  PNUtil.updateIBA(number, ConstantLine.var_mtag_content, mtag_content);
			  String specification=NodeUtil.getspecificationAttr(part);
			  PNUtil.updateIBA(number,"specification", specification);
			CadHelper.updateDocDwgAndPDF(number, dwgmap);
			trx.commit();
		} catch (Exception e) {
			trx.rollback();
			throw e;
		}
		return number;
	}

	
	/**
	 * 填写母PN属性值的更新
	 */
	public void updateibavalue(String partNumber, String name, String value)
			throws WTRuntimeException, WTException, WTPropertyVetoException,
			RemoteException {
		WTPart part = CommonUtil.getLatestWTpartByNumber(partNumber);
		IBAUtility iba = new IBAUtility(part);
		if (!StringUtils.isEmpty(value)) {
			iba.setIBAValue(name, value);
			iba.updateAttributeContainer(part);
			iba.updateIBAHolder(part);
		} else {
			iba.deleteIBAValueByLogical(name);
			iba.updateAttributeContainer(part);
			iba.updateIBAHolder(part);
		}

	}
	
	/**
	 * 验证同一线束总成同一母PN下是否有相同的箱体+描述 
	 * 热缩套管颜色
	 * @param asmnumber
	 * @param mpnnumber
	 * @param number
	 * @param values
	 * @throws WTPropertyVetoException
	 * @throws WTException
	 */
	private static void validateBox(String asmnumber,String mpnnumber,String number, String values) throws WTPropertyVetoException, WTException{
		JSONObject jo = JSONObject.fromObject(values);
		Map<String, Object> valuesmap = jo;
		String ldesc=(String) valuesmap.get(ConstantLine.var_ltagdesc);
		String rdesc=(String) valuesmap.get(ConstantLine.var_rtagdesc);
		WTPart mpnpart=CommonUtil.getLatestWTpartByNumber(mpnnumber);
		String rstgcolor=IBAUtil.getStringIBAValue(mpnpart, ConstantLine.var_HeatShrinkableCasing_color);
		colorConfigValidate(rstgcolor,ldesc,rdesc);
		if(!StringUtils.isEmpty(asmnumber)){
			WTPart asmpart=CommonUtil.getLatestWTpartByNumber(asmnumber);
			List childs=CommonUtil.getChildParts(asmpart, asmpart.getViewName());
			String box_desc1=valuesmap.get(ConstantLine.var_ltagbox)+""+valuesmap.get(ConstantLine.var_ltagdesc);
			String box_desc2=valuesmap.get(ConstantLine.var_rtagbox)+""+valuesmap.get(ConstantLine.var_rtagdesc);
		
			for (int i = 0; i < childs.size(); i++) {
				WTPart cpart=(WTPart) childs.get(i);
				if(number.equals(cpart.getNumber())||!cpart.getNumber().startsWith(mpnnumber)){//部件本身或者和非同一个母PN的部件
					continue;
				}
				String var_ltagbox=IBAUtil.getStringIBAValue(cpart, ConstantLine.var_ltagbox);
				String var_rtagbox=IBAUtil.getStringIBAValue(cpart, ConstantLine.var_rtagbox);
				String var_ltagdesc=IBAUtil.getStringIBAValue(cpart, ConstantLine.var_ltagdesc);
				String var_rtagdesc=IBAUtil.getStringIBAValue(cpart, ConstantLine.var_rtagdesc);
				String cbox_desc1=var_ltagbox+var_ltagdesc;
				String cbox_desc2=var_rtagbox+var_rtagdesc;
				if(box_desc1.equals(cbox_desc1)){
					throw new LineException("线束总成"+asmnumber+"中已经存在左箱体描述“"+box_desc1+"”的物料"+cpart.getNumber());
				}
				if(box_desc2.equals(cbox_desc2)){
					throw new LineException("线束总成"+asmnumber+"中已经存在右箱体描述“"+box_desc2+"”的物料"+cpart.getNumber());
				}
			}
		}
	}
	
	/**
	 * 热缩套管颜色对标签填写的限制
	 * @param color
	 * @param ldesc
	 * @param rdesc
	 */
	private static void colorConfigValidate(String color,String ldesc,String rdesc){
		System.out.println(color+"---"+ldesc+"----"+rdesc);
		String config=ConstantLine.config_validate_rstgcolor;
		String[] colorconfig=config.split("\\|");
		for (int i = 0; i < colorconfig.length; i++) {
			String ccolorconfig=colorconfig[i];
			String[] cconfigs=ccolorconfig.split(":");
			if(cconfigs.length!=3){
				throw new LineException("热缩管颜色配置文件出错");
			}
			HashMap cmap=new HashMap();
			if(cconfigs[0].equals(color)){
				System.out.println(cconfigs[0]+"---"+cconfigs[1]+"--"+cconfigs[2]);
				 if(!StringUtils.isEmpty(cconfigs[1])&&!Arrays.asList(cconfigs[1].split("、")).contains(ldesc)){
					 throw new LineException("左标签内容必须在"+cconfigs[1]+"中");
				 }
				 if(!StringUtils.isEmpty(cconfigs[2])&&!Arrays.asList(cconfigs[2].split("、")).contains(rdesc)){
					 throw new LineException("右标签内容必须在"+cconfigs[2]+"中");
				 }
				 break;
			}
		}
	}
	
	/**
	 * 删除衍生PN(已废弃)
	 */
	public void deletePN(String number) throws Exception {
		Transaction trx = null;
		try {
			trx = new Transaction();
			trx.start();
			WTDocument doc = CommonUtil.getLatestWTDocByNumber(number);
			if (doc.getLifeCycleState().toString()
					.equals(ConstantLine.var_childpn_initstate)) {
				SessionServerHelper.manager.setAccessEnforced(true);
				boolean access = AccessControlHelper.manager.hasAccess(
						SessionHelper.getPrincipal(), doc,
						AccessPermission.DELETE);
				SessionServerHelper.manager.setAccessEnforced(false);
				if (access) {
					PersistenceHelper.manager.delete(doc);
				} else {
					throw new LineException("没有权限删除关联文档");
				}
			} else {
				throw new LineException("不能删除当前部件关联的非"
						+ State.toState(ConstantLine.var_childpn_initstate)
								.getDisplay(Locale.CHINA) + "状态文档");
			}
			WTPart part = CommonUtil.getLatestWTpartByNumber(number);
			if (part.getLifeCycleState().toString()
					.equals(ConstantLine.var_childpn_initstate)) {
				SessionServerHelper.manager.setAccessEnforced(true);
				boolean access = AccessControlHelper.manager.hasAccess(
						SessionHelper.getPrincipal(), part,
						AccessPermission.DELETE);
				SessionServerHelper.manager.setAccessEnforced(false);
				if (access) {
					PNUtil.removePartUse(part);
					PersistenceHelper.manager.delete(part);
				} else {
					throw new LineException("没有权限删除部件");
				}
			} else {
				throw new LineException("不能删除非"
						+ State.toState(ConstantLine.var_childpn_initstate)
								.getDisplay(Locale.CHINA) + "状态部件");
			}
			trx.commit();
		} catch (Exception e) {
			trx.rollback();
			throw e;
		}

	}

	
	/**
	 * 获取推荐线长
	 */
	public JSONArray retireRecommendL(String minl, String maxl, String currentl) throws FileNotFoundException, IOException {
		// TODO Auto-generated method stub
		return JSONArray.fromObject(TagHelper.getLengthRecommend(minl, maxl, currentl));
	}
	
	public void batchCreateOrUpdateChildPN(String containeroid,String folderoid,String asmnumber) throws Exception{
		File localfile = new File("/ptc/tt.xls");
		if(!StringUtils.isEmpty(asmnumber)){
			String msg=getAsmPN(asmnumber);
			if(!StringUtils.isEmpty(msg)){
				throw new LineException(msg);
			}
		}
		try{
		List<Map> maps=BatchDeriveData.getData("/ptc/tt.xls");
		for (int i = 0; i < maps.size(); i++) {
			Map map=maps.get(i);
			String mpn=(String) map.get("母PN");
			String number=(String) map.get("衍生PN");
			if(mpn==null){
				throw new LineException("找不到编号为"+mpn+"的母PN");
			}
			String values=BatchDeriveData.getJSONStr(map);
			String existpart=validateChildPN(mpn, values);
			if(StringUtils.isEmpty(number)){//无PN号创建
				if(!StringUtils.isEmpty(existpart)&&!StringUtils.isEmpty(asmnumber)){
					relativePN(asmnumber, existpart);
				}else if(StringUtils.isEmpty(existpart)){//系统中不存在相同线束
					try{
				    WTPart mpnpart = CommonUtil.getPartByNumber(mpn);
				    if(mpnpart==null){
				    	throw new LineException("找不到编号为"+mpn+"的母PN");
				    }
					String newnum=createChildPN(asmnumber, mpn, values, containeroid, folderoid);
					if(!StringUtils.isEmpty(newnum)&&!StringUtils.isEmpty(asmnumber)){
					relativePN(asmnumber,newnum);
					}
					}catch(Exception e){
						e.printStackTrace();
						throw new LineException("序号"+(i+1)+"行创建PN时出错,"+e.getLocalizedMessage());
					}
				}
			}else{//有PN号更新
				try{
					WTPart part=CommonUtil.getLatestWTpartByNumber(number);
					if(part==null){
						throw new LineException("系统中找不到编号"+number+"的PN");
					}
				updateChildPN(asmnumber, WCUtil.getOid(part), values);
				}catch(Exception e){
					e.printStackTrace();
					throw new LineException("序号"+(i+1)+"行创建PN时出错"+e.getLocalizedMessage());
				}
			}
		}
		}catch(Exception e){
			if(localfile.exists()){
				localfile.delete();
			}
			e.printStackTrace();
			throw e;
		}
		
	}
	public static void test() throws Exception {
		SessionServerHelper.manager.setAccessEnforced(false);
		TestBatchImport2 im=new TestBatchImport2();
		try{
		im.batchCreateOrUpdateChildPN("OR:wt.pdmlink.PDMLinkProduct:114865","OR:wt.folder.SubFolder:104340115", "991400-01001");
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}finally{
			 SessionServerHelper.manager.setAccessEnforced(true);
		}
	}
	public static void main(String[] args) throws Exception {
		RemoteMethodServer rms = RemoteMethodServer.getDefault();
		rms.setUserName("60010715");
		rms.setPassword("Bb123456");
		rms.invoke("test", TestBatchImport2.class.getName(), null, null, null);
	}
}
