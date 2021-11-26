package com.catl.loadData;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.catl.common.constant.PartState;
import com.catl.common.util.GenericUtil;
import com.catl.common.util.PartUtil;
import com.catl.common.util.WCLocationConstants;
import com.catl.loadData.util.ExcelReader;

import wt.fc.ObjectReference;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.collections.WTCollection;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.part.Quantity;
import wt.part.QuantityUnit;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.part.WTPartMaster;
import wt.part.WTPartSubstituteLink;
import wt.part.WTPartUsageLink;
import wt.pom.Transaction;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.util.WTMessage;

public class RefreshBOM implements RemoteAccess {
	/**
	 * 当前系统所有在用单位
	 */
	private static final String UNIT_ALL="ea|g|ml|dm2|set|m|";

	public static void main(String[] args) {
		
		RemoteMethodServer ms = RemoteMethodServer.getDefault();
		ms.setUserName("dms");
		ms.setPassword("dms");
		
		String root = WCLocationConstants.WT_HOME + File.separator + "loadFiles"+ File.separator+"com"+ File.separator+"catl"+ File.separator+"dms"+ File.separator+"bom";
		String fileName = "";
		
		if (args != null) {
			for (int i = 0; i < args.length; i += 2) {
				if ("-root".equals(args[i])) {
					root = args[i + 1];
				}
				else if ("-filename".equals(args[i])) {
					fileName += args[i + 1];
				}
			}
		}
		if(StringUtils.isNotBlank(fileName)){
			try {
				SessionHelper.manager.setAuthenticatedPrincipal("dms");			
				ms.invoke("refreshBOM", RefreshBOM.class.getName(), null, new Class[]{String.class, String.class}, new Object[]{fileName, root});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	
	public static void refreshBOM(String fileName, String root){
		StringBuilder successMsg = new StringBuilder();
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		Transaction trs = null;
		try {
			trs = new Transaction();
            trs.start();
			Map<WTPart, Map<WTPart,Object[]>> dataMap = readExcelData(fileName, root);
			WTPart workingCopy = null;
			
			for(WTPart parent : dataMap.keySet()){
				Map<WTPart,Object[]> children = dataMap.get(parent);
				workingCopy = (WTPart)GenericUtil.checkout(parent, "");
				deleteUsagelinks(workingCopy);
				for(WTPart child : children.keySet()){
					Object[] infos = children.get(child);
					WTPartUsageLink usageLink = createUsageLink(workingCopy, child, infos);
					Set<WTPart> substituteParts = (HashSet<WTPart>)infos[2];
					Set<String> substitute = new HashSet<String>();
					if(substituteParts != null){
						for (WTPart substitutePart : substituteParts) {
							substitute.add(substitutePart.getNumber());
							createSubstituteLink(usageLink, (WTPartMaster)substitutePart.getMaster());
						}
					}
					
					successMsg.append(WTMessage.formatLocalizedMessage("父件[{0}]，子件[{1}]，用量[{2}]，单位[{3}]", new Object[]{parent.getNumber(),child.getNumber(),infos[0],infos[1]}));
					if(substitute.size() > 0){
						successMsg.append(WTMessage.formatLocalizedMessage(",替代件编码{0}", new Object[]{substitute.toString()}));
					}
					successMsg.append("\r\n");
				}
				GenericUtil.checkin(workingCopy, "Refresh BOM");
			}
			if(successMsg.length() > 0){
				writeTextFile(root, fileName+"_success.txt", successMsg.toString());
			}
			
			trs.commit();
	        trs = null;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			SessionServerHelper.manager.setAccessEnforced(enforce);
            if (trs != null) {
                trs.rollback();
            }
            
        }
	}
	
	private static void deleteUsagelinks(WTPart part) throws WTException{
		QueryResult qr = PersistenceHelper.manager.navigate(part, WTPartUsageLink.USES_ROLE,
						WTPartUsageLink.class, false);
		while (qr.hasMoreElements()) {
			WTPartUsageLink partusagelink = (WTPartUsageLink) qr.nextElement();
			List<WTPartSubstituteLink> substituteLinks = getAllSubstitutes(partusagelink);
			for (WTPartSubstituteLink substituteLink : substituteLinks) {
				PersistenceHelper.manager.delete(substituteLink);
			}
			System.out.println("==delete link:"+partusagelink.getUsedBy().getIdentity()+"--"+partusagelink.getUses().getIdentity());
			PersistenceHelper.manager.delete(partusagelink);
		}
	}
	
	private static List<WTPartSubstituteLink> getAllSubstitutes(WTPartUsageLink usageLink) throws WTException{
		List<WTPartSubstituteLink> list = new ArrayList<WTPartSubstituteLink>();
		WTCollection collection = WTPartHelper.service.getSubstituteLinks(usageLink);
		for (Object o : collection) {
			WTPartSubstituteLink slink = (WTPartSubstituteLink) (((ObjectReference) o).getObject());
			list.add(slink);
		}
		return list;
	}
	
	private static WTPartUsageLink getPartUsageLink(WTPart parentPart,WTPartMaster childPartMaster) throws WTException{
    	WTPartUsageLink partusagelink = null;
    	if(parentPart != null && childPartMaster != null){
    		QueryResult qr = PersistenceHelper.manager.find(WTPartUsageLink.class, parentPart, WTPartUsageLink.USED_BY_ROLE, childPartMaster);
    		if(qr != null && qr.hasMoreElements()){
    			partusagelink = (WTPartUsageLink)qr.nextElement();
    		}
    	}
    	return partusagelink;
    }
	
	private static WTPartUsageLink createUsageLink(WTPart parent, WTPart child, Object[] infos) throws WTException{
		WTPartUsageLink usageLink = null;
		if (parent != null && child != null) {
			WTPartMaster master = (WTPartMaster) child.getMaster();
			//System.out.println("==link:"+parent.getNumber()+"=="+child.getNumber());
			usageLink = getPartUsageLink(parent, master);
			if(usageLink == null){
				usageLink = WTPartUsageLink.newWTPartUsageLink(parent, master);
				//System.out.println("==infos[0]:"+infos[0]);
				double quantity = Double.valueOf((String)infos[0]);
				String str_unit = (String)infos[1];
				if(StringUtils.equalsIgnoreCase(str_unit, "PCS")){
					str_unit = "ea";
				}
				else {
					str_unit = str_unit.toLowerCase();
				}
				QuantityUnit unit = QuantityUnit.toQuantityUnit(str_unit);
				usageLink.setQuantity(Quantity.newQuantity(quantity, unit));
				
				usageLink = (WTPartUsageLink) PersistenceHelper.manager.save(usageLink);
			}
		}
		return usageLink;
	}
	
	private static void createSubstituteLink(WTPartUsageLink usageLink, WTPartMaster substitutePartMaster) throws WTException{
		WTPartSubstituteLink subLink = WTPartSubstituteLink.newWTPartSubstituteLink(usageLink, substitutePartMaster);
		PersistenceHelper.manager.save(subLink);
	}
	
	private static Map<WTPart, Map<WTPart,Object[]>> readExcelData(String fileName, String root) throws Exception{
		String filePathName = root + File.separator + fileName;
		System.out.println("====filePathName:"+filePathName);
		Map<WTPart, Map<WTPart,Object[]>> map = new HashMap<WTPart, Map<WTPart,Object[]>>();
		StringBuilder errorMsg = new StringBuilder();
		ExcelReader er = new ExcelReader(new File(filePathName));
		try {
			er.open();
			int count = er.getRowCount();
			System.out.println("====RowCount:"+count);
			String[] cells = null;
			WTPart part = null;
			Set<String> checkedNumbers = new HashSet<>();
			Set<WTPart> substituteParts = null;
			for(int i=1; i<=count; i++){
				cells = er.readExcelLine(i);
				if(cells==null || StringUtils.isBlank(cells[0])){
					break;
				}
				
				part = PartUtil.getLastestWTPartByNumber(cells[0]);
				if(part == null){
					if(!checkedNumbers.contains(cells[0])){
						checkedNumbers.add(cells[0]);
						errorMsg.append(WTMessage.formatLocalizedMessage("编码[{0}]在系统中不存在！\r\n", new Object[]{cells[0]}));
					}
				}
				else {
					Map<WTPart,Object[]> childMap = map.get(part);
					if(childMap == null){
						childMap = new HashMap<WTPart, Object[]>();
						map.put(part, childMap);
					}
					part = PartUtil.getLastestWTPartByNumber(cells[3]);
					if(part != null){
						Object[] objs = childMap.get(part);
						if(objs == null){
							objs = new Object[3];
							childMap.put(part, objs);
						}
						if(!checkedNumbers.contains(cells[0]+"="+cells[3])){
							checkedNumbers.add(cells[0]+"="+cells[3]);
							//System.out.println("==child="+part.getNumber()+":"+cells[5]);
							objs[0] = cells[5];//单位用量
							String partUnit = part.getDefaultUnit().toString();
							String unit = cells[4];
							if(StringUtils.equalsIgnoreCase(unit, "PCS")){
								unit = "ea";
							}
							else {
								unit = unit.toLowerCase();
							}
							if(!UNIT_ALL.contains(unit+"|")){
								errorMsg.append(WTMessage.formatLocalizedMessage("编码[{0}]的用量单位[{1}]在系统中不存在！\r\n", new Object[]{cells[3], cells[4]}));
							}
							else if(!StringUtils.equalsIgnoreCase(unit, partUnit)){
								errorMsg.append(WTMessage.formatLocalizedMessage("编码[{0}]的用量单位[{1}]与默认单位不符！\r\n", new Object[]{cells[3], cells[4]}));
							}
							else {
								objs[1] = cells[4];//单位
							}
						}
						
						if(cells.length >7 && StringUtils.isNotBlank(cells[7]) && !checkedNumbers.contains("sub="+cells[0]+"="+cells[3]+"="+cells[7])){
							checkedNumbers.add("sub="+cells[0]+"="+cells[3]+"="+cells[7]);
							System.out.println("sub="+cells[0]+"="+cells[3]+"="+cells[7]);
							part = PartUtil.getLastestWTPartByNumber(cells[7]);
							if(part == null){
								errorMsg.append(WTMessage.formatLocalizedMessage("编码[{0}]在系统中不存在！\r\n", new Object[]{cells[7]}));
							}
							else {
								
								if(!isReleased(part)){
									errorMsg.append(WTMessage.formatLocalizedMessage("替代件[{0}]生命周期状态不是“已发布”，不符合业务规范！\r\n", new Object[]{cells[7]}));
								}
								else {
									if(objs[2] == null){
										objs[2] = new HashSet<WTPart>();
									}
									substituteParts = (HashSet<WTPart>)objs[2];
									substituteParts.add(part);
								}
							}
						}
					}
					else {
						if(!checkedNumbers.contains(cells[3])){
							checkedNumbers.add(cells[3]);
							errorMsg.append(WTMessage.formatLocalizedMessage("编码[{0}]在系统中不存在！\r\n", new Object[]{cells[3]}));
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			errorMsg.append("\r\n \r\n");
			errorMsg.append(e.getLocalizedMessage());
		}
		if(errorMsg.length() > 0){
			map.clear();
			writeTextFile(root, fileName+"_checkResult.txt", errorMsg.toString());
		}
		return map;
	}
	
	private static void writeTextFile(String root, String textFileName, String msg) throws Exception{
		//System.out.println("===msg:"+msg);
		String filePathName = root + File.separator + textFileName;
		FileWriter fw = null;
		try{
			File txtFile = new File(filePathName);
			if(txtFile.exists()){
				txtFile.delete();
			}
			fw = new FileWriter(filePathName, true);
			fw.write(msg);
		}
		finally{
			fw.flush();
			fw.close();
		}
	}
	
	private static boolean isReleased(WTPart part){
		if(part != null){
			String state = part.getState().toString();
			return StringUtils.equals(state, PartState.RELEASED);
		}
		return false;
	}

}
