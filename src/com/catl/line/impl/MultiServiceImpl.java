package com.catl.line.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import wt.access.AccessControlHelper;
import wt.access.AccessPermission;
import wt.doc.WTDocument;
import wt.fc.PersistenceHelper;
import wt.inf.container.WTContainer;
import wt.org.WTUser;
import wt.part.QuantityUnit;
import wt.part.Source;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.part.WTPartIDSeq;
import wt.part.WTPartMaster;
import wt.pom.Transaction;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.util.WTPropertyVetoException;
import wt.util.WTRuntimeException;

import com.catl.common.util.PartUtil;
import com.catl.common.util.PropertiesUtil;
import com.catl.line.constant.ConstantLine;
import com.catl.line.entity.LayoutComparator;
import com.catl.line.entity.UploadMsg;
import com.catl.line.exception.LineException;
import com.catl.line.queue.DWGToPDFQueue;
import com.catl.line.service.MultiService;
import com.catl.line.util.ClassificationUtil;
import com.catl.line.util.CommonUtil;
import com.catl.line.util.FileUtil;
import com.catl.line.util.IBAUtility;
import com.catl.line.util.NodeUtil;
import com.catl.line.util.PNUtil;
import com.catl.line.util.ParseClfUtil;
import com.catl.line.util.WCUtil;
import com.catl.line.util.WTDocumentUtil;
import com.catl.line.util.WTPartUtil;
import com.catl.line.util.ZipUtil;
import com.catl.line.validator.CreateMultiPartVerify;
import com.catl.part.CatlPartNewNumber;
import com.catl.part.CreateCatlPartProcessor;
import com.catl.part.PartConstant;
import com.catl.part.PartLoadNameSourceUtil;
import com.catl.require.constant.ConstantRequire;
import com.ptc.core.HTMLtemplateutil.server.processors.AttributeKey;
import com.ptc.core.lwc.common.LayoutComponent;
import com.ptc.core.lwc.common.view.AttributeDefinitionReadView;
import com.ptc.core.lwc.common.view.GroupDefinitionReadView;
import com.ptc.core.lwc.common.view.GroupMembershipReadView;
import com.ptc.core.lwc.common.view.LayoutComponentReadView;
import com.ptc.core.lwc.common.view.LayoutDefinitionReadView;
import com.ptc.core.lwc.server.LWCStructEnumAttTemplate;
import com.ptc.core.lwc.server.TypeDefinitionServiceHelper;
import com.ptc.core.meta.common.AttributeIdentifier;
import com.ptc.core.meta.common.AttributeTypeIdentifier;
import com.ptc.jca.mvc.LayoutComponentComparator;

@Scope("prototype")
@Service("multiservice")
public class MultiServiceImpl implements MultiService {
	private static String wt_home;
	static {
		try {
			wt_home = WTProperties.getLocalProperties().getProperty("wt.home",
					"UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
    
	@Override
	/**
	 * 上传压缩文件
	 */
	public JSONArray uploadzip(MultipartFile file, String type, String allowtype)
			throws IllegalStateException, IOException, WTException {
		String realFileName = file.getOriginalFilename();
		File localfile = new File(wt_home + ConstantLine.base_temp + type
				+ File.separator + realFileName);
		if (localfile.exists()) {
			localfile.delete();
		}
		file.transferTo(localfile);
		List msgs = ZipUtil.unZipFile(localfile, wt_home
				+ ConstantLine.base_temp + type + File.separator, allowtype);
		List results = new ArrayList();
		for (int i = 0; i < msgs.size(); i++) {
			UploadMsg msg = (UploadMsg) msgs.get(i);
			List result = new ArrayList();
			result.add(msg.getNumber());
			result.add(msg.getName());
			result.add(msg.getMsg());
			result.add(msg.isStatus());
			results.add(result);
		}
		return JSONArray.fromObject(results);
	}

	@Override
	/**
	 * 创建文档
	 */
	public void createDoc(String docinfo) throws Exception {
		Transaction trx = null;
		try {
			trx = new Transaction();
			trx.start();
			JSONArray array = JSONArray.fromObject(docinfo);
			System.out.println(array);
			for (int i = 0; i < array.size(); i++) {
				Map<String, String> map = array.getJSONObject(i);
				String number = map.get("number");
				String name = map.get("name");
				WTPart part = CommonUtil.getPartByNumber(number);
				if (part == null) {
					throw new LineException("查询不到编号为" + number + "的部件");
				}
				WTDocument doc = CommonUtil.getLatestWTDocByNumber(number);
				boolean iscreate=false;
				if(doc==null){
					iscreate=true;
					CommonUtil.createDocDependsPart(number, number, part,ConstantLine.var_doctype_autocadDrawing,
							PropertiesUtil.getValueByKey("config_autocaddoc_folder"));
					doc = CommonUtil.getLatestWTDocByNumber(number);
					SessionServerHelper.manager.setAccessEnforced(true);
					WTUser user=(WTUser) SessionHelper.manager.getPrincipal();
					boolean flag = AccessControlHelper.manager.hasAccess(user,doc, AccessPermission.CREATE);
					if(!flag){
						throw new LineException("您没有创建文档"+number+"权限");
					}
					SessionServerHelper.manager.setAccessEnforced(false);
					if (doc == null) {
						throw new LineException("创建" + number + "文档失败");
					}
					PNUtil.createDescriptionLink(doc, part);
				}
				File file = new File(wt_home + ConstantLine.dwg_temp_path+ name);
				if (file.exists()) {
					if(!iscreate){//替换主内容后升版本
						FileInputStream is = new FileInputStream(file);
						doc=(WTDocument) CommonUtil.checkoutObject(doc);
						WTDocumentUtil.replaceDocPrimaryContent(doc, is, name,file.length());
						CommonUtil.checkinObject(doc, "批量更新图档内容");
					}else{
						FileInputStream is = new FileInputStream(file);
						WTDocumentUtil.replaceDocPrimaryContent(doc, is, name,file.length());
						DWGToPDFQueue.executeWfExpression(WCUtil.getOid(doc));
					}
				}
			}
			trx.commit();
		} catch (Exception e) {
			trx.rollback();
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	/**
	 * 上传附件
	 */
	public String uploadattach(MultipartFile file, String type)
			throws IllegalStateException, IOException {
		String realFileName = file.getOriginalFilename();
		String filename = realFileName + "_" + System.currentTimeMillis();// 文件名唯一性
		File localfile = new File(wt_home + ConstantLine.base_temp + type
				+ File.separator + realFileName);
		file.transferTo(localfile);
		return filename;
	}

	@Override
	public boolean validateAttachContainer(String containerOid)
			throws WTRuntimeException, WTException {
		WTContainer container = (WTContainer) WCUtil.getWTObject(containerOid);
		if (container != null) {
			String name = container.getName();
			String[] containernames = ConstantLine.config_attach_containername
					.split(",");
			if (Arrays.asList(containernames).contains(name)) {
				return true;
			}
		}
		return false;
	}

	@Override
	/**
	 * 创建多个部件
	 */
	public void createMultiPart(String jsonstr, String type, String uploadtype,
			String folderOid, String containerOid) throws Exception {
		Transaction trx = null;
		List files=new ArrayList();
		try {
			trx = new Transaction();
			trx.start();
			JSONArray array = JSONArray.fromObject(jsonstr);
			List clfstr = new ArrayList();
			for (int i = 0; i < array.size(); i++) {
				Map<String, String> map = array.getJSONObject(i);
				String name = map.get("name");
				String source = map.get("source");
				String unit = map.get("unit");
				String oldnum = map.get("oldnum");
				String description = map.get("description");
				String clf = map.get("clf");
				String uiforcecreate = map.get("forcecreate");
				String attachname = map.get("attachname");
				String feature = map.get("feature");
				String iscustomer = map.get("iscustomer");
				String platform = map.get("platform");
				String openMould = map.get("openMould");
				
				String number = PersistenceHelper.manager
						.getNextSequence(WTPartIDSeq.class);
				WTPart part = CommonUtil.createPart(number, name, type, source,
						unit, oldnum, uiforcecreate, description, folderOid,
						containerOid);
				WTPartMaster partmaster=part.getMaster();
				IBAUtility ibafeature = new IBAUtility(part);
				IBAUtility ibamaster = new IBAUtility(partmaster);
				if(StringUtils.isNotBlank(feature)){
					ibafeature.setIBAValue(PartConstant.CATL_Feature, feature);
				}
				if(StringUtils.isNotBlank(iscustomer)){
					ibafeature.setIBAValue(PartConstant.Is_Customer, iscustomer);
				}
				if(StringUtils.isNotBlank(platform)){
					ibamaster.setIBAValue(ConstantRequire.iba_CATL_Platform, platform);
				}
				
				if(StringUtils.isNotBlank(openMould)){
					ibafeature.setIBAValue(PartConstant.OpenMould, openMould);
				}
				
				part = (WTPart) ibafeature.updateAttributeContainer(part);
				ibafeature.updateIBAHolder(part);
				
				partmaster=(WTPartMaster) ibamaster.updateAttributeContainer(partmaster);
				ibafeature.updateIBAHolder(partmaster);
				SessionServerHelper.manager.setAccessEnforced(true);
				WTUser user=(WTUser) SessionHelper.manager.getPrincipal();
				boolean flag = AccessControlHelper.manager.hasAccess(user,part, AccessPermission.CREATE);
				if(!flag){
					throw new LineException("您没有创建部件权限");
				}
				SessionServerHelper.manager.setAccessEnforced(false);
				// 上传附件
				if (!StringUtils.isEmpty(attachname)) {
					attachname = attachname.substring(0,
							attachname.lastIndexOf("_"));
					WTPartUtil.updateAttachment(part, wt_home
							+ ConstantLine.base_temp + uploadtype
							+ File.separator + attachname);
					files.add(wt_home+ ConstantLine.base_temp + uploadtype+ File.separator + attachname);
				}
				CreateMultiPartVerify verify = new CreateMultiPartVerify();
				// 添加分类，分类属性 验证部件
				if (!StringUtils.isEmpty(clf)) {
					JSONArray clfattrarray = JSONArray.fromObject(clf);
					LWCStructEnumAttTemplate lwc = null;
					String lwcname = null;
					// 获取分类名称
					IBAUtility iba = new IBAUtility(part);
					for (int j = 0; j < clfattrarray.size(); j++) {
						JSONObject clfattrmap = clfattrarray.getJSONObject(j);
						String ibaname = clfattrmap.getString("name");
						if (ibaname.equals(ConstantLine.var_clf)) {
							lwcname = clfattrmap.getString("value");
							iba.setIBAValue(ibaname, lwcname);
							break;
						}
					}
					lwc = NodeUtil.getClfNodeByName(lwcname);
					StringBuffer specification = new StringBuffer();
					for (int j = 0; j < clfattrarray.size(); j++) {
						JSONObject clfattrmap = clfattrarray.getJSONObject(j);
						System.out.println(clfattrmap);
						String ibaname = clfattrmap.getString("name");
						// String datatype=clfattrmap.getString("type");
						if (!ibaname.equals(ConstantLine.var_clf)) {// 不是分类
							String ibavalue = clfattrmap.getString("value");
							String ibadisplay = IBAUtility
									.getIBADisplayName(ibaname);
							if (specification.length() == 0) {
								specification.append(ibadisplay).append(":")
										.append(ibavalue);
							} else {
								specification.append("_").append(ibadisplay)
										.append(":").append(ibavalue);
							}
							if (!StringUtils.isEmpty(ibavalue)) {
								iba.setIBAValue(ibaname, ibavalue);
							}
						}
						// 修改为新编码
					}
					boolean forcecreate = false;
					if (uiforcecreate.equals("是")) {
						forcecreate = true;
					}
					if (!forcecreate) {
						if (clfstr.contains(clf)) {
							throw new LineException(
									"您创建的部件"
											+ part.getName()
											+ "物料规格与表格中其他部件物料规格重复，请选择规格重复时仍然创建为“是”后再试！\n");
						}
						Set parts = PartUtil.getLastedPartByStringIBAValue(
								part.getNumber(), "specification",
								specification.toString());
						if (parts.size() != 0) {
							Iterator ite = parts.iterator();
							WTPart exisPart = (WTPart) ite.next();
							throw new LineException("您创建的部件" + part.getName()
									+ "物料规格与：\n" + exisPart.getNumber()
									+ "\n物料规格重复，请选择规格重复时仍然创建为“是”后再试！\n");
						}

					}
					clfstr.add(clf);					
					
					if (specification.length() > 0) {
						iba.setIBAValue("specification",
								specification.toString());
					}
					iba.updateAttributeContainer(part);
					iba.updateIBAHolder(part);
					
					CreateCatlPartProcessor.updateSource(part);
					CreateCatlPartProcessor.renamePart(part);
					
					String newNum = CatlPartNewNumber.createPartNewnumber(lwc,
							part.getContainerName());
					WTPartMaster partMaster = (WTPartMaster) part.getMaster();
					try {
						WTPartHelper.service.changeWTPartMasterIdentity(
								partMaster, part.getName(), newNum,
								part.getOrganization());
					} catch (WTPropertyVetoException e) {
						e.printStackTrace();
					}
					verify.verifyNewPart(part, lwc);

				} else {
					throw new LineException("获取不到分类");
				}
			}
			
			trx.commit();
			for (int i = 0; i < files.size(); i++) {
				FileUtil.deleteFile((String) files.get(i));
			}
		} catch (Exception e) {
			trx.rollback();
			throw e;
		}

	}

	@Override
	/**
	 * 解析页面传来的分类json数据
	 */
	public JSONArray parseClfAttributes(String jsonstr, String type,String clfnode,String clfjson,String folderOid,
			String containerOid) throws WTException {
		boolean flag=false;
		Map<String,String> namemap = PartLoadNameSourceUtil.getPartClsNameSource();
		if(type!=null&&type.equals("edit")){
			flag=true;
		}
		LinkedHashMap<AttributeKey, Object> map = ParseClfUtil
				.getClfAttkey_value(jsonstr,flag);
		List list = new ArrayList();
		AttributeTypeIdentifier clfati = null;
		String clfval = "";
		Map tempmap = new HashMap();
		for (Entry<AttributeKey, Object> entry : map.entrySet()) {
			Map rmap = new HashMap();
			AttributeIdentifier attrIdentifier = entry.getKey()
					.getAttributeId();
			AttributeTypeIdentifier ati = (AttributeTypeIdentifier) attrIdentifier
					.getDefinitionIdentifier();
			rmap.put("name", ati.getAttributeName());
			rmap.put("value", entry.getValue());
			rmap.put("type", entry.getKey().getDataType());
			rmap.put("displayname",
					IBAUtility.getIBADisplayName(ati.getAttributeName()));
			if (ati.getAttributeName().equals(ConstantLine.var_clf)) {
				clfati = ati;
				LWCStructEnumAttTemplate clf = NodeUtil
						.getClfNodeByName((String) entry.getValue());
				clfval = (String) entry.getValue();
				rmap.put("clfname", ClassificationUtil.getDisplayName(clf));
				System.out.println(namemap.get(clfval));
				rmap.put("partname", namemap.get(clfval));

			}
			tempmap.put(ati.getAttributeName(), rmap);
		}
		if(flag){
			Map rmap=new HashMap();
			rmap.put("name", ConstantLine.var_clf);
			rmap.put("value",clfnode);
			rmap.put("type", "java.lang.String");
			rmap.put("displayname", IBAUtility.getIBADisplayName(ConstantLine.var_clf));
			LWCStructEnumAttTemplate clf=NodeUtil.getClfNodeByName(clfnode);
			rmap.put("clfname", ClassificationUtil.getDisplayName(clf));
			rmap.put("partname", namemap.get(clfnode));
			System.out.println("flag\t"+namemap.get(clfnode));
			tempmap.put(ConstantLine.var_clf, rmap);
			if (!StringUtils.isEmpty(clfjson)) {
				JSONArray clfattrarray = JSONArray.fromObject(clfjson);
				// 获取分类名称
				for (int j = 0; j < clfattrarray.size(); j++) {
					JSONObject clfattrmap = clfattrarray.getJSONObject(j);
					String name = clfattrmap.getString("name");
					if (tempmap.containsKey(name)) {
						list.add(tempmap.get(name));
					}
				}
			}
			
		}else{
			List<LayoutDefinitionReadView> layouts = new ArrayList<LayoutDefinitionReadView>();
			Set<LayoutDefinitionReadView> nestedLayouts = TypeDefinitionServiceHelper.service
					.getLayoutDefinitions(clfati, new String[] { clfval }, null,null);
			layouts.addAll(nestedLayouts);
			Collections.sort(layouts, new LayoutComparator());
	
			Iterator<LayoutDefinitionReadView> iterator = layouts.iterator();
			while (iterator.hasNext()) {
				LayoutDefinitionReadView layout = iterator.next();
				for (LayoutComponentReadView layComp : layout
						.getAllLayoutComponents()) {
					if (layComp instanceof GroupDefinitionReadView) {
						GroupDefinitionReadView layGrp = (GroupDefinitionReadView) layComp;
						ArrayList<LayoutComponent> attributesList = new ArrayList<LayoutComponent>(
								layGrp.getComponents());
						Collections.sort(attributesList,LayoutComponentComparator.getInstance());
						Iterator<LayoutComponent> iter = attributesList.iterator();
						while (iter.hasNext()) {
							LayoutComponent comp = iter.next();
							if (((GroupMembershipReadView) comp).getMember() instanceof AttributeDefinitionReadView) {
								AttributeDefinitionReadView attrReadView = (AttributeDefinitionReadView) ((GroupMembershipReadView) comp)
										.getMember();
								String name = attrReadView.getName();
								if (tempmap.containsKey(name)) {
									list.add(tempmap.get(name));
								}
							}
						}
					}
				}
			}
		}
		list.add(tempmap.get(ConstantLine.var_clf));
		
		return JSONArray.fromObject(list);
	}

	@Override
	public JSONArray querySources() {
		Source[] set = Source.getSourceSet();
		JSONArray array = new JSONArray();
		for (int i = 0; i < set.length; i++) {
			Source s = set[i];
			if(s.isSelectable()){
				String[] source = new String[] { s.getDisplay(Locale.CHINA)};
				array.add(source);
			}
		}
		return array;
	}

	@Override
	public JSONArray queryUnits() {
		QuantityUnit[] set = QuantityUnit.getQuantityUnitSet();
		JSONArray array = new JSONArray();
		for (int i = 0; i < set.length; i++) {
			QuantityUnit s = set[i];
			if(s.isSelectable()){
				String[] unit = new String[] { s.getDisplay(Locale.CHINA) };
				array.add(unit);
			}
		}
		return array;
	}
}
