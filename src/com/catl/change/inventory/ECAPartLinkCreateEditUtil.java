package com.catl.change.inventory;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import wt.change2.WTChangeActivity2;
import wt.enterprise.Master;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.log4j.LogR;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.vc.VersionControlHelper;

import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.IframeFormProcessorHelper;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;

/**
 * 
 * @author Administrator 2015-10-27
 * 
 * @Description
 */
public class ECAPartLinkCreateEditUtil {// CreateChangeTaskFormProcessor {


	public static Logger logger = LogR.getLogger(ECAPartLinkCreateEditUtil.class.getName());
	private final static String[] attNames = new String[]{"partNumber","partName",ECAPartLink.QUANTITY,ECAPartLink.MATERIAL_STATUS,
			ECAPartLink.DISPOSITION_OPTION,ECAPartLink.OWNER,ECAPartLink.DUE_DAY,ECAPartLink.REMARKS};
	
	
	public ECAPartLinkCreateEditUtil() {
		
	}

	/**
	 * create or update or delete ECAPartLink when edit ECN or ECA.
	 * @param clientData
	 * @param objectBeanList
	 * @throws WTException
	 */
	public static void doOperation(NmCommandBean clientData,
			List<ObjectBean> objectBeanList) throws WTException {
		// WTUser currentUser = (WTUser) SessionHelper.manager.getPrincipal();
		logger.debug("ECAPartLinkCreateEditUtil doOperation in==========");
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);

		try {
			List<NmOid> addeditems = new ArrayList<NmOid>();
			List<NmOid> removeditems = new ArrayList<NmOid>();
			List<String> addedoids = new ArrayList<String>();
			List<String> removedoids = new ArrayList<String>();
			Map<String,String> valueMap = new HashMap<String,String>();
			Map<String, Map<String,String>> addmap = new HashMap<String, Map<String,String>>();
			Map<String, Map<String,String>> updatemap = new HashMap<String, Map<String,String>>();
			
			List<String> keyList= IframeFormProcessorHelper.getIframeKeyList(clientData);
			
			/*
			 * process request from ECN
			 */
			if(keyList!=null && keyList.size()>0){
				for(ObjectBean bean: objectBeanList){
					addeditems.addAll(bean.getAddedItemsByName("CatlChangeInventory"));
					removeditems.addAll(bean.getRemovedItemsByName("CatlChangeInventory"));
				}

				for(NmOid oid:removeditems){
					removedoids.add(oid.getHTMLId()); 
				}
				for(NmOid oid:addeditems){
					addedoids.add(oid.getHTMLId());
				}
				for(String key :keyList){
					String[] value = (String[]) clientData.getRequestData().getParameterMap().get(key); //"1_popCreateWizard_defaultChangeTask"
					logger.debug("====>key::: "+key);
					logger.debug("====>value from clientData::: "+value[0]);
					getPageDataFromECN(value[0], addmap, updatemap, addedoids, removedoids);
				}
				logger.debug("---addmap:::"+addmap);
				logger.debug("---updatemap:::"+updatemap);

				//to add link
				for(ObjectBean bean: objectBeanList){
					WTChangeActivity2 eca = (WTChangeActivity2) bean.getObject();
					List<NmOid> addeditems1 = bean.getAddedItemsByName("CatlChangeInventory");
					for(NmOid oid: addeditems1){
						valueMap = addmap.get(oid.getHTMLId());
						createEcaPartLink(eca, valueMap);
					}
				}
			}else{
				/*
				 * process request from ECA
				 */
				addeditems.addAll(clientData.getAddedItemsByName("CatlChangeInventory"));
				removeditems.addAll(clientData.getRemovedItemsByName("CatlChangeInventory"));

				for(NmOid oid:removeditems){
					removedoids.add(oid.getHTMLId()); 
				}
				for(NmOid oid:addeditems){
					addedoids.add(oid.getHTMLId());
				}
				Map<String, Object> clientParams = clientData.getRequestData().getParameterMap();
				getPageDataFromECA(clientParams, addmap, updatemap, addedoids, removedoids);
				
				//to add link
				ObjectBean bean = objectBeanList.get(0);
				WTChangeActivity2 eca = (WTChangeActivity2) bean.getObject();
				for(String oid: addedoids){
					valueMap = addmap.get(oid);
					createEcaPartLink(eca, valueMap);
				}
			}

			//to modify link
			Object[] updatedKeys = (Object[]) updatemap.keySet().toArray();
			for(Object updatedKey:updatedKeys){
				valueMap = updatemap.get(updatedKey);
				updateEcaPartLink((String)updatedKey, valueMap);
			}
			// to remove link
			ReferenceFactory rf = new ReferenceFactory();
			for(String oid:removedoids){
				ECAPartLink link = (ECAPartLink) rf.getReference(oid).getObject();
				PersistenceHelper.manager.delete(link);
			}
			
		}catch(Exception e){
			logger.error(e);
			e.printStackTrace();
			throw new WTException(e, "Edit ECAPartLink Failed...");
		}finally {
			// SessionHelper.manager.setPrincipal(currentUser.getName());
			SessionServerHelper.manager.setAccessEnforced(enforce);
			logger.debug("ECAPartLinkCreateEditUtil doOperation out==========");
		}
	}
	
	/**
	 * create ECAPartLink
	 * @param eca
	 * @param valueMap
	 * @return
	 * @throws Exception
	 */
	public static ECAPartLink createEcaPartLink(WTChangeActivity2 eca,Map<String,String> valueMap) throws WTException{
		try{
			ECAPartLink link = new ECAPartLink();
			link.setEca(eca);
			link.setPart(getPartByNumber(valueMap.get("partNumber")));
			link = setLinkAttributes(link, valueMap);
			link = (ECAPartLink) PersistenceHelper.manager.store(link);
			return link;
		}catch(Exception e){
			System.out.println("----------------");
			throw new WTException(e);
		}
	}
	
	/**
	 * update the ECAPartLink
	 * @param updatedKey
	 * @param valueMap
	 * @return
	 * @throws Exception
	 */
	public static ECAPartLink updateEcaPartLink(String updatedKey, Map<String,String> valueMap) throws Exception{
		ECAPartLink link = getEcaPartLinkByOid(updatedKey);//getEcaPartLink(eca, part);
		link = setLinkAttributes(link, valueMap);
		link = (ECAPartLink) PersistenceHelper.manager.save(link);
		return link;
	}
	
	/**
	 * set ECAPartLink attributes besides two roles.
	 * @param link
	 * @param valueMap
	 * @return
	 * @throws Exception
	 */
	public static ECAPartLink setLinkAttributes(ECAPartLink link,Map<String,String> valueMap) throws Exception{
		link.setQuantity(Double.valueOf(valueMap.get(ECAPartLink.QUANTITY)));
		String value = valueMap.get(ECAPartLink.MATERIAL_STATUS);
		String internal = value.substring(value.lastIndexOf(".")+1,value.length());
		link.setMaterialStatus(MaterialStatus.toMaterialStatus(internal));
		
		value = valueMap.get(ECAPartLink.DISPOSITION_OPTION);
		internal = value.substring(value.lastIndexOf(".")+1,value.length());
		link.setDispositionOption(DispositionOption.toDispositionOption(internal));
		
		link.setOwner(valueMap.get(ECAPartLink.OWNER));

		String formatString = "yyyy/MM/dd";
        SimpleDateFormat parseSDF = new SimpleDateFormat(formatString);
        Date date = parseSDF.parse(valueMap.get(ECAPartLink.DUE_DAY));
		link.setDueDay(new Timestamp(date.getTime()));
		
		link.setRemarks(valueMap.get(ECAPartLink.REMARKS));
		
		return link;
	}
	
	/**
	 * get ECAPartLink by roleA and roleB
	 * @param eca
	 * @param part
	 * @return
	 * @throws WTException
	 */
	public static ECAPartLink getEcaPartLink(WTChangeActivity2 eca, WTPart part ) throws WTException{
		QueryResult queryResult = PersistenceHelper.manager.navigate(eca,ECAPartLink.PART_ROLE, ECAPartLink.class,false);
		if(queryResult!=null){
			Object obj = null;
			while(queryResult.hasMoreElements()){
				obj = queryResult.nextElement();
				if(obj instanceof ECAPartLink){
					ECAPartLink link = (ECAPartLink)obj;
					WTPart part2 = link.getPart();
					if(part2.getIdentity().equalsIgnoreCase(part.getIdentity())){
						return link;
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * get ECAPartLink by oid
	 * @param oid
	 * @return
	 * @throws WTException
	 */
	public static ECAPartLink getEcaPartLinkByOid(String oid) throws WTException{
		ReferenceFactory rf = new ReferenceFactory();
		ECAPartLink link = (ECAPartLink) rf.getReference(oid).getObject();
		return link;
	}
	
	/**
	 * prepare the data(ECAPartLink) which need to be added or updated when editing ECA
	 * @param clientParams
	 * @param addmap
	 * @param updatemap
	 * @param addedoids
	 * @param removedoids
	 * @throws UnsupportedEncodingException 
	 */
	public static void getPageDataFromECA(Map<String, Object> clientParams,  
			Map<String, Map<String,String>> addmap, Map<String, Map<String,String>> updatemap,
			List<String> addedoids, List<String> removedoids) throws UnsupportedEncodingException{
		logger.debug("getPageDataFromECA addmap in :::"+addmap);
		logger.debug("getPageDataFromECA updatemap in :::"+updatemap);
		Map<String, String> params = new HashMap<String,String>();
		Object[] keys = (Object[])(clientParams.keySet().toArray());
		
		List<String> updatedoids = new ArrayList<String>();
		
		
		Map<String,String> newRowObjLines = new HashMap<String, String>();
		Map<String,String> ecaPartLinkLines = new HashMap<String, String>();
		
		
		for(Object keyObj: keys){
			//get added data from page request
			String key = (String)keyObj;
			logger.debug("clientParams Key:::"+key);
			logger.debug("clientParams value:::"+((String[])clientParams.get(key))[0]);
			if(key.contains("newRowObj")){
				newRowObjLines.put(key, ((String[])clientParams.get(key))[0]);
				params.put(key, ((String[])clientParams.get(key))[0]);
			}else if(key.contains("ECAPartLink")){
				//get updated data from page request
				String linkOid = key.split("~")[2];
				linkOid = linkOid.replace("%3A", ":");
				if(!removedoids.contains(linkOid)){
					ecaPartLinkLines.put(key, ((String[])clientParams.get(key))[0]);
					if(!updatedoids.contains(linkOid)){
						updatedoids.add(linkOid);
					}
				}
			}
		}
		
		getAddUpdateData(addmap, newRowObjLines, addedoids);
		getAddUpdateData(updatemap, ecaPartLinkLines, updatedoids);
		
		logger.debug("getPageDataFromECA addmap out :::"+addmap);
		logger.debug("getPageDataFromECA updatemap out :::"+updatemap);
	}
	
	/**
	 * prepare the data(ECAPartLink) which need to be added or updated when editing ECN
	 * @param paramStr
	 * @param addmap
	 * @param updatemap
	 * @param addedoids
	 * @param removedoids
	 * @throws UnsupportedEncodingException 
	 */
	public static void getPageDataFromECN(String paramStr, 
			Map<String, Map<String,String>> addmap, Map<String, Map<String,String>> updatemap,
			List<String> addedoids, List<String> removedoids) throws UnsupportedEncodingException{
		logger.debug("getPageDataFromECN addmap in :::"+addmap);
		logger.debug("getPageDataFromECN updatemap in :::"+updatemap);
		String[] lines = paramStr.split("&");
		
		List<String> updatedoids = new ArrayList<String>();
		
		Map<String,String> newRowObjLines = new HashMap<String, String>();
		Map<String,String> ecaPartLinkLines = new HashMap<String, String>();
		
		for(String line: lines){
			//get added data from page request
			//logger.debug("line:::"+line);
			if(line.contains("newRowObj") && line.contains("~")){
				putStringIntoMap(line,newRowObjLines);
			}else if(line.contains("ECAPartLink")){
				//get updated data from page request
				String[] linkOidArr = line.split("~");
				if(linkOidArr.length>2){
					String linkOid = linkOidArr[2];
					linkOid = linkOid.replace("%3A", ":");
					if(!removedoids.contains(linkOid)){
						putStringIntoMap(line,ecaPartLinkLines);
						if(!updatedoids.contains(linkOid)){
							updatedoids.add(linkOid);
						}
					}
				}
			}
		}
		
		List<String> newAddedOids = new ArrayList<String>();
		for(String addedOid : addedoids){
			if(paramStr.contains(addedOid)){
				newAddedOids.add(addedOid);
			}
		}
		logger.debug("getPageDataFromECN updatedoids :::"+updatedoids);
		getAddUpdateData(addmap, newRowObjLines, newAddedOids);
		getAddUpdateData(updatemap, ecaPartLinkLines, updatedoids);
		
		logger.debug("getPageDataFromECN addmap out :::"+addmap);
		logger.debug("getPageDataFromECN updatemap out :::"+updatemap);
	}
	
	/**
	 * split the string by "=" and put them as key-value into map
	 * @param line
	 * @param map
	 */
	public static void putStringIntoMap(String line, Map<String,String> map ){
		String[] lineArr = line.split("=");
		String key = lineArr[0];
		String value = "";
		if(lineArr.length>1){
			value = lineArr[1];
		}
		map.put(key, value);
	}
	
	/**
	 * prepare the data(ECAPartLink) which need to be added or updated
	 * @param objects
	 * @param subParams
	 * @param oids
	 * @throws UnsupportedEncodingException 
	 */
	public static void getAddUpdateData(Map<String, Map<String,String>> objects, 
			Map<String,String> subParams, List<String> oids) throws UnsupportedEncodingException{
		Map<String,String> object = null;
		Object[] subParamsKeys = (Object[])(subParams.keySet().toArray());
		for(String oid: oids){
			object =  new HashMap<String,String>();
			for(Object subParamsKeyObj: subParamsKeys){
				String subParamsKey = (String)subParamsKeyObj;
				String keyCopy = subParamsKey.toString();
				if(keyCopy.contains("%3A")){
					keyCopy = keyCopy.replace("%3A", ":");
				}
				if(keyCopy.contains(oid)){
					for(String attName:attNames){
						if(attName.equals("partName")){
							if(subParamsKey.contains(attName)){
								object.put(attName,subParams.get(subParamsKey));
							}
						}else if(attName.endsWith(ECAPartLink.DUE_DAY)){
							if(subParamsKey.contains(attName) && !subParamsKey.contains("___old")){
								System.out.println("ECAPartLinkCreateEditUtil,getAddUpdateData,MayerLogForDate:::"+subParams.get(subParamsKey));
								String date = subParams.get(subParamsKey);
								date = date.replace("%2F", "/");
								date = date.replace("-", "/");
								object.put(attName,date);
							}
						}else if(attName.contains(ECAPartLink.OWNER) || attName.contains(ECAPartLink.REMARKS)){
							if(subParamsKey.contains(attName) && !subParamsKey.contains("___old")){
								String val = subParams.get(subParamsKey);
								val = URLDecoder.decode(val, "UTF-8");
								//val = java.net.URLEncoder.encode(val,"gb2312");
								object.put(attName,val);
							}
						}else{
							if(subParamsKey.contains(attName) && !subParamsKey.contains("___old")){
								object.put(attName,subParams.get(subParamsKey));
							}
						}
					}
				}
			}
			objects.put(oid, object);
		}
	}
	
	/**
	 * get part by number
	 * @param partNumber
	 * @return
	 */
	public static WTPart getPartByNumber(String partNumber){
		WTPart part = null;
		try{
			WTPartMaster partMaster = getPartMasterByNumber(partNumber);
			QueryResult qr = VersionControlHelper.service.allIterationsOf((Master)partMaster);
			if (qr.hasMoreElements()){
				part = (WTPart)qr.nextElement();
			}
		}catch(Exception ex){
			part = null;
			System.out.println("TAPart.class Method=getPartByNumber Exception Message = " + ex.getMessage()); //Debug
		}
		return part;
	}
	
	
	/**
	 * get part master by number
	 * @param partNumber
	 * @return
	 */
	public static WTPartMaster getPartMasterByNumber(String partNumber){
		WTPartMaster partMaster = null;
		try{						
			QuerySpec qs = new QuerySpec(WTPartMaster.class);			
			SearchCondition sc = new
					SearchCondition(WTPartMaster.class, WTPartMaster.NUMBER, SearchCondition.EQUAL,partNumber,false);
			qs.appendSearchCondition(sc);
			QueryResult qr = PersistenceHelper.manager.find(qs);
			if (qr.hasMoreElements()){				
				partMaster = (WTPartMaster)qr.nextElement();
			}
		}catch(Exception ex){
			partMaster = null;
			System.out.println("CSCPart.class Method=getPartMasterByNumber Exception Message = " + ex.getMessage()); //Debug
		}
		return partMaster;
	}

}
