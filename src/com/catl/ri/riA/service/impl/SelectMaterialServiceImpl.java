package com.catl.ri.riA.service.impl;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import wt.content.ApplicationData;
import wt.doc.WTDocument;
import wt.fc.QueryResult;
import wt.part.WTPart;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.util.WTRuntimeException;

import com.catl.ri.entity.MaterialAttr;
import com.catl.ri.entity.MaterialDB;
import com.catl.ri.constant.ConstantRI;
import com.catl.ri.riA.constant.GlobalData;
import com.catl.ri.riA.helper.CacheHelper;
import com.catl.ri.riA.helper.ClassificationHelper;
import com.catl.ri.riA.helper.CommonHelper;
import com.catl.ri.riA.service.SelectMaterialService;
import com.catl.ri.riA.util.CommonUtil;
import com.catl.ri.riA.util.ExcelUtil;
import com.catl.ri.riA.util.WTDocumentUtil;
import com.catl.ri.riA.util.WTPartUtil;
import com.catl.line.util.FileUtil;
import com.catl.line.util.IBAUtil;
import com.catl.line.util.NodeUtil;
import com.catl.line.util.WCUtil;
import com.ptc.core.lwc.server.LWCStructEnumAttTemplate;

@Scope("prototype")
@Service("riASelectMaterialService")
public class SelectMaterialServiceImpl implements SelectMaterialService {
	private static final Logger logger = Logger.getLogger(SelectMaterialServiceImpl.class.getName());  
	private static String wt_home = "";
	static {
		try {
			wt_home = WTProperties.getLocalProperties().getProperty("wt.home",
					"UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@Override
	public JSONArray getRecipeJson(String containerOid,String recipenumber) throws WTException, IOException, PropertyVetoException {
		WTDocument doc=WTDocumentUtil.getRecipeDoc(containerOid);
		ApplicationData appdata=WTDocumentUtil.downloadDocPrimary(doc,wt_home+ConstantRI.base_ri_pathA);
		List dbs=new ArrayList();
		List types=new ArrayList();
		if(appdata!=null){
    		String tempfilepath=wt_home+ConstantRI.base_ri_pathA+appdata.getFileName();
    		File tempfile=new File(tempfilepath);
    		String[][] result =ExcelUtil.getData(0, null, tempfile, 0, false);
    		List<Map<String, String>> maps=getSheetInfo(result, ConstantRI.config_recipenumber, recipenumber);
    		FileUtil.deleteFile(tempfilepath);
    		if(maps.size()==0){
    			throw new WTException("配方文件中找不到此配方号!");
    		}else{
    			StringBuffer nomaterialerror=new StringBuffer();
    			for (int i = 0; i < maps.size(); i++) {
					Map recipemap=maps.get(i);
					String pn=(String) recipemap.get(ConstantRI.config_materialpn);
					String loadding=(String) recipemap.get(ConstantRI.config_loadding);
					if(!StringUtils.isEmpty(pn)){
						QueryResult parts=CommonUtil.getPartsByPrefix(pn);
						while(parts.hasMoreElements()){
							WTPart part=(WTPart) parts.nextElement();
							LWCStructEnumAttTemplate clf=NodeUtil.getLWCStructEnumAttTemplateByPart(part);
						    if(clf==null){
						    	throw new WTException(part.getNumber()+"分类为空!");
						    }
							String clfname=clf.getName();
							String type=clfname;
							List<Map<String, String>> typematerials=CommonHelper.getTypeMaterial(type);
							MaterialDB db=new MaterialDB();
							db.setClf(clfname);
							db.setLoadding(loadding);
							db.setMaterialname(part.getName());
							db.setRecipenumber(recipenumber);
							db.setPn(part.getNumber());
							int index=CommonHelper.getTypesIndex(types,type);
							db.setIsPhantom("否");
							db.setSpeicalkey(IBAUtil.getIBAStringValue(part, ConstantRI.config_ri_iba_speicalkey));
							List<MaterialAttr> attrs=CommonHelper.getTypeAttrs(typematerials,part,index,db);
							types.add(type);
							db.setAttr(attrs);
							dbs.add(db);
						}
					}else{
						throw new WTException("配方文件中的PN号未填写!");
					}
				}
    			dbs.add(nomaterialerror.toString());
    			return JSONArray.fromObject(dbs);
    		}
    		
    	}
		throw new WTException("配置文件出错");
	}
	 
	/**
	 * 获取excel信息
	 * @param result
	 * @return
	 */
	public static List<Map<String, String>> getSheetInfo(String[][] result,String contentName,String content) {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		int rowLength = result.length;
		List headers = new ArrayList();
		for (int i = 0; i < rowLength; i++) {
			Map rowmap = new HashMap();
			for (int j = 0; j < result[i].length; j++) {
				if (i == 0) {
					headers.add(result[i][j]);
				} else {
					if (headers.size() > j && !headers.get(j).toString().trim().equals("")) {
							String value=result[i][j];
							rowmap.put(headers.get(j).toString().trim(), value);
					}

				}
			}
			if (!rowmap.isEmpty()) {
				if(rowmap.get(contentName).equals(content)){
					list.add(rowmap);
				}
			}
		}
		return list;
	}

	@Override
	public JSONArray getSearchJson(String type) throws WTException {
		String[] searchclfs=ConstantRI.config_searchclfsA.split(",");
		Map<String, List<Map<String, String>>> searchmap=new HashMap<String, List<Map<String, String>>>();
		Set<String> partForLibary=new HashSet<String>();
		for (int i = 0; i < searchclfs.length; i++) {
			QueryResult qr=ClassificationHelper.getAllPartsByLikeNodeName(searchclfs[i]);
			List<Map<String,String>> partlist=new ArrayList<Map<String,String>>();
			while (qr.hasMoreElements()) {
				WTPart part = (WTPart) qr.nextElement();
				if(part.getContainerName().equals(ConstantRI.config_libary_rimaterial)){
					Map<String,String> map=new HashMap<String,String>();
					String number=part.getNumber();
					String name=part.getName();
					if(type.equals("name")){
						String suffix="";
						if(number.startsWith("RD")){
							suffix="RD";
						}else if(number.startsWith("SP")){
							suffix="SP";
						}else if(number.startsWith("MD")){
							suffix="MD";
						}
						if(!suffix.isEmpty()){
							name=name+"("+suffix+")";
						}
						map.put("title", name);
						map.put("oid", WCUtil.getOid(part));
					}else if(type.equals("number")){
						map.put("title", number);
						map.put("oid", WCUtil.getOid(part));
					}
					partForLibary.add(name);
					partlist.add(map);
				}
			}
			String filePath=wt_home+ConstantRI.config_path_material_xlsxA;
			ExcelUtil.getPartForExcel(filePath, type, searchclfs[i], partForLibary, partlist);
			searchmap.put(searchclfs[i], partlist);
		}
		return JSONArray.fromObject(searchmap);
	}

	@Override
	public JSONArray getPartRowJson(String oid) throws WTRuntimeException, WTException, FileNotFoundException, IOException {
		MaterialDB db=new MaterialDB();
		if(oid.contains("wt.part.WTPart")){
    		WTPart part= (WTPart) WCUtil.getWTObject(oid);
            String pn=part.getNumber();
    		LWCStructEnumAttTemplate clf=NodeUtil.getLWCStructEnumAttTemplateByPart(part);
    	    if(clf==null){
    	    	throw new WTException(pn+"分类为空!");
    	    }
    		String clfname=clf.getName();
    		String type=clfname;
    		List<Map<String, String>> typematerials=CommonHelper.getTypeMaterial(type);
    		db.setClf(clfname);
    		db.setLoadding("");
    		db.setSpeicalkey(IBAUtil.getIBAStringValue(part, ConstantRI.config_ri_iba_speicalkey));
    		db.setMaterialname(part.getName());
    		db.setRecipenumber("");
    		db.setPn(pn);
    		//只获取到列位置信息
    		db.setIsPhantom("否");
    		List<MaterialAttr> attrs=CommonHelper.getEngTypeAttrs(typematerials,part,db);
    		db.setAttr(attrs);
        }else{
        	String filePath=wt_home+ConstantRI.config_path_material_xlsxA;
        	ExcelUtil.getMaterialByName(filePath, oid, db);
        }
		return JSONArray.fromObject(db);
	}

	@Override
	public JSONArray getRecipenumbers(String containerOid,String materialNumber) throws Exception {
		// TODO Auto-generated method stub
		List<List<String>> list=new ArrayList<List<String>>();
		WTDocument doc=WTDocumentUtil.getRecipeDoc(containerOid);
		ApplicationData appdata=WTDocumentUtil.downloadDocPrimary(doc,wt_home+ConstantRI.base_ri_pathA);
		if(appdata!=null){
			String tempFilePath=wt_home+ConstantRI.base_ri_pathA+appdata.getFileName();
			File tempFile=new File(tempFilePath);
			String[][] result =ExcelUtil.getData(0, null, tempFile, 0, false);
    		List<Map<String, String>> maps=getSheetInfo(result, ConstantRI.config_materialpn, materialNumber);
    		FileUtil.deleteFile(tempFilePath);
    		if(maps.size()==0){
    			throw new WTException("配方文件中找不到此材料PN号!");
    		}else{
    			for (int i = 0; i < maps.size(); i++) {
					Map sheetInfo=maps.get(i);
					String recipenumber=(String) sheetInfo.get(ConstantRI.config_recipenumber);
					if(!StringUtils.isEmpty(recipenumber)){
						List<String> results=new ArrayList<String>();
						results.add(recipenumber);
						list.add(results);
					}
				}
    			if(list.size()==0){
    				throw new WTException("配方文件中的配方号未填写!");
    			}
    		}
		}
		return JSONArray.fromObject(list);
	}

	@Override
	public void createPhantom(String jsonStr) throws Exception {
		// TODO Auto-generated method stub
		@SuppressWarnings("unchecked")
		Map<String,String> map=JSONObject.fromObject(jsonStr);
		Map<String,String> newMap=new HashMap<String, String>();
		CacheHelper.loadExcelConfig();
		String[][] result =GlobalData.materical_config_result;
		for(Map.Entry<String, String> entry:map.entrySet()){
			String key=entry.getKey();
			String value=entry.getValue().trim();
			if(key.equals("cls")){
				newMap.put(key, value);
			}
			
			List<Map<String, String>> maps=getSheetInfo(result, ConstantRI.config_material_region, key);
			for(int i=0;i<maps.size();i++){
				Map rowInfo=maps.get(i);
				String clfattr=(String) rowInfo.get(ConstantRI.config_material_name);
				if(clfattr.equals("name")){
					WTPart part=WTPartUtil.getLastestWTPartByName(value);
					if(part!=null){
						throw new Exception("系统中已存在材料["+value+"]");
					}
				}
				newMap.put(clfattr, value);
			}
		}
		String filePath=wt_home+ConstantRI.config_path_material_xlsxA;
		ExcelUtil.saveMaterialInfo(newMap, filePath);
	}

	@Override
	public JSONArray getMaterialPNJson(String containerOid) throws FileNotFoundException, IOException, WTException, PropertyVetoException {
		Map map=new HashMap();
		WTDocument doc=WTDocumentUtil.getRecipeDoc(containerOid);
		ApplicationData appdata=WTDocumentUtil.downloadDocPrimary(doc,wt_home+ConstantRI.base_ri_pathA);
		if(appdata!=null){
			String tempFilePath=wt_home+ConstantRI.base_ri_pathA+appdata.getFileName();
			File tempFile=new File(tempFilePath);
			String[][] result =ExcelUtil.getData(0, null, tempFile, 0, false);
    		List list1=CacheHelper.getRecipeString(result, ConstantRI.config_materialpn);
    		List list2=CacheHelper.getRecipeString(result, ConstantRI.config_recipenumber);
    		map.put("pn", list1);
    		map.put("recipenumber", list2);
    		FileUtil.deleteFile(tempFilePath);
		}
		return JSONArray.fromObject(map);
	}

	@Override
	public String getRemarkByNumber(String number) throws WTException {
		WTPart part=CommonUtil.getLatestWTpartByNumber(number);
		if(part!=null){
			String speicalkey=IBAUtil.getIBAStringValue(part, ConstantRI.config_ri_iba_speicalkey);
	        return speicalkey;
		}
		return "";
	}
}
