package com.catl.battery.service.impl;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import wt.doc.WTDocument;
import wt.fc.PersistenceHelper;
import wt.folder.Folder;
import wt.pom.Transaction;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.util.WTRuntimeException;

import com.catl.battery.constant.ConstantBattery;
import com.catl.battery.util.CommonUtil;
import com.catl.line.util.FolderUtil;
import com.catl.line.util.WCUtil;
import com.catl.battery.service.ConfigService;
import com.catl.battery.util.BatteryUtil;
import com.catl.battery.util.WTDocumentUtil;
import com.catl.ri.validator.UserValidator;
@Scope("prototype")
@Service("configService")
public class ConfigServiceImpl implements ConfigService{
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
	public JSONArray getTemplate() throws Exception {
		List<WTDocument> docs=BatteryUtil.getAllTemplate();
		List result=new ArrayList();
		for (int i = 0; i < docs.size(); i++) {
			List<String> attrs=new ArrayList<String>();
			WTDocument doc=docs.get(i);
			attrs.add(WCUtil.getOid(doc));
			attrs.add(doc.getName());
			attrs.add(doc.getCreatorFullName());
			attrs.add(doc.getModifierFullName());
			attrs.add(doc.getDescription()==null?"":doc.getDescription());
			Timestamp date=new Timestamp(doc.getModifyTimestamp().getTime()+28800000);
			String str=date.toString();
			attrs.add(str.substring(0, str.lastIndexOf(".")));
			result.add(attrs);
		}
		return JSONArray.fromObject(result);
	}

	@Override
	public String downloadTemplate(String oids) throws WTRuntimeException, WTException, IOException, PropertyVetoException {
		BatteryUtil.emptyTempDir();
		if(oids.contains(",")){
			String[] oidarray=oids.split(",");
			List<WTDocument> docs=new ArrayList<WTDocument>();
			for (int i = 0; i < oidarray.length; i++) {
				docs.add((WTDocument)WCUtil.getWTObject(oidarray[i]));
			}
			String url=BatteryUtil.downloadDocZip(docs);
			BatteryUtil.emptyTempExcel();
			return url;
		}else{
			return WTDocumentUtil.getViewPrimaryURL((WTDocument)WCUtil.getWTObject(oids));
		}
	}
	
	@Override
	public void deleteTemplate(String oids) throws WTRuntimeException, WTException{
		Transaction trx = null;
		try {
			trx = new Transaction();
			trx.start();
			String[] oidarray=oids.split(",");
			for (int i = 0; i < oidarray.length; i++) {
				WTDocument doc=(WTDocument)WCUtil.getWTObject(oidarray[i]);
				PersistenceHelper.manager.delete(doc);
			}
			trx.commit();
		}catch(Exception e){
			trx.rollback();
			throw e;
		}
	}

	@Override
	public void opTemplate(MultipartFile uploadfile, String oid,String description) throws Exception {
		String realFileName = ConstantBattery.config_batterymath_xls.substring(ConstantBattery.config_batterymath_xls.lastIndexOf("/")+1);
		File localfile = new File(wt_home + ConstantBattery.config_battery_temp
				+ File.separator + realFileName);
		if (localfile.exists()) {
			localfile.delete();
		}
		String newname=realFileName.replace(".xlsx", "");
		uploadfile.transferTo(localfile);
		if(StringUtils.isEmpty(oid)){
			Folder folder=FolderUtil.getFolder(ConstantBattery.config_org_storetemplate, BatteryUtil.getOrg());
			WTDocument doc=CommonUtil.createDoc(newname, ConstantBattery.TEMPLATE_WTDOCUMENT_TYPE, folder, description);
			WTDocumentUtil.replaceDocPrimaryContent(doc, wt_home + ConstantBattery.config_battery_temp
					+ File.separator + realFileName, newname,localfile.length()); 
		}else{
			WTDocument doc=(WTDocument) WCUtil.getWTObject(oid);
			doc=(WTDocument) WCUtil.getWorkableByPersistable(doc);
			WTDocumentUtil.replaceDocPrimaryContent(doc, wt_home + ConstantBattery.config_battery_temp
					+ File.separator + realFileName, newname,localfile.length()); 
			doc.setDescription(description);
			doc=(WTDocument) PersistenceHelper.manager.save(doc);
			WCUtil.checkin(doc);
		}
		
	}

	@Override
	public boolean isvalidAdmin() throws WTException {
		return UserValidator.isvalidAdminUser(ConstantBattery.evc_group_admin);
	}

}
