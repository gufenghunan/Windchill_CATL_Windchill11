package com.catl.part;

import java.io.File;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.catl.bom.workflow.BomWfUtil;
import com.catl.common.util.GenericUtil;
import com.catl.common.util.WCLocationConstants;
import com.catl.ecad.utils.CommonUtil;
import com.catl.ecad.utils.IBAUtility;
import com.catl.loadData.util.ExcelReader;
import com.ptc.windchill.enterprise.part.commands.PartDocServiceCommand;

import wt.doc.WTDocument;
import wt.fc.ObjectReference;
import wt.fc.WTObject;
import wt.fc.collections.WTArrayList;
import wt.part.WTPart;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

public class PartProductPhaseUtil {
	public static Map<String, Integer> phaseMap= new HashMap<>();
	private static String filePathName = WCLocationConstants.WT_CODEBASE+File.separator
			+"config"+File.separator+"custom"+File.separator
			+"config"+File.separator+"PartPhaseConfig.xlsx";
	private static List<String> hasChild = new ArrayList<>();
	private static List<String> hasAttr = new ArrayList<>();
	private static List<String> phase = new ArrayList<>();
	private static List<String> line = new ArrayList<>();
	private static List<String> docClsPhaseMap = new ArrayList<>();
	private static List<String> docCls = new ArrayList<>();
	private static List<String> phaseOrLine = new ArrayList<>();
	private static List<String> phaseweight = new ArrayList<>();

	public static void main(String[] args) throws WTException {
		// TODO Auto-generated method stub
		WTDocument doc = CommonUtil.getLatestWTDocByNumber("SW-PBUS01-00004");
		getRefedPartByDoc(doc);
	}
	
	public static void checkChildPartPhase(WTObject pbo) throws WTException, WTPropertyVetoException, RemoteException{
		StringBuilder sb = new StringBuilder();
		if(pbo instanceof WTDocument){
			WTDocument doc = (WTDocument) pbo;
			//String docSubName = GenericUtil.getObjectAttributeValue(doc, "subCategory") == null ? "" : GenericUtil.getObjectAttributeValue(doc, "subCategory").toString();
			String docTypeName = GenericUtil.getObjectAttributeValue(doc, "CATL_DocType") == null ? "" : GenericUtil.getObjectAttributeValue(doc, "CATL_DocType").toString();
			if (!docTypeName.isEmpty()){
				getConfigMap();
				if(docCls.contains(docTypeName)){
					phaseMap = getPhaseMap();
					List<WTPart> parts = getRefedPartByDoc(doc);
					int clsindx = docCls.indexOf(docTypeName);
					String trans = docClsPhaseMap.get(clsindx);
					if(StringUtils.isNotBlank(trans)){
						if(trans.indexOf("|")>0){
							String[] st = trans.split("\\|");
							String source = st[0];
							String target = st[1];
							if("阶段".equals(phaseOrLine.get(clsindx))){
								sb.append(checkChild(parts, parts, target));
								if(sb.length() > 0){
									throw new WTException(sb.toString());
								}									
							}											
						}
					}
				}				
			}
		}
	}
	
	public static void updatePartPhase(WTObject pbo) throws WTException, WTPropertyVetoException, RemoteException{
		StringBuilder sb = new StringBuilder();
		if(pbo instanceof WTDocument){
			WTDocument doc = (WTDocument) pbo;
			//String docSubName = GenericUtil.getObjectAttributeValue(doc, "subCategory") == null ? "" : GenericUtil.getObjectAttributeValue(doc, "subCategory").toString();
			String docTypeName = GenericUtil.getObjectAttributeValue(doc, "CATL_DocType") == null ? "" : GenericUtil.getObjectAttributeValue(doc, "CATL_DocType").toString();
			if (!docTypeName.isEmpty()){
				getConfigMap();
				if(docCls.contains(docTypeName)){
					phaseMap = getPhaseMap();
					List<WTPart> parts = getRefedPartByDoc(doc);
					int clsindx = docCls.indexOf(docTypeName);
					String trans = docClsPhaseMap.get(clsindx);
					if(StringUtils.isNotBlank(trans)){
						if(trans.indexOf("|")>0){
							String[] st = trans.split("\\|");
							String source = st[0];
							String target = st[1];
							if("阶段".equals(phaseOrLine.get(clsindx))){
								sb.append(checkChild(parts, parts, target));
								if(sb.length() > 0){
									throw new WTException(sb.toString());
								}
								for(int i = 0; i < parts.size(); i ++){
									upgradePhase(parts.get(i), source, target);
									
								}		
							}else{
								for(int i = 0; i < parts.size(); i ++){	
									upgradeProductionLine(parts.get(i), source, target);									
								}			
							}
											
						}
					}
				}				
			}
		}
	}
	
	public static Map<String, Integer> getPhaseMap(){
		Map<String,Integer> map = new HashMap<>();
		for(int i = 0; i < phase.size(); i ++){
			map.put(phase.get(i), Integer.valueOf(phaseweight.get(i)));
		}
		return map;
	}
	
	public static String checkChild(List<WTPart> parent,List<WTPart> sourceList,String targetPhase) throws WTException{
		Map<Object, List> allChildren = BomWfUtil.getChildrenParts(parent);
		StringBuilder sb = new StringBuilder();		
		//getConfigMap();
		
		
		for (Iterator i = allChildren.keySet().iterator(); i.hasNext();) {

			WTPart parentPart = (WTPart) i.next();
			String parentNum = parentPart.getNumber();
			IBAUtility iba = new IBAUtility(parentPart);
			String parentpp = iba.getIBAValue("ProductPhase");
			boolean haschildcls = false;
			for(int j = 0; j< hasChild.size(); j ++){
				if(parentNum.startsWith(hasChild.get(j))){
					haschildcls = true;
					break;
				}
			}
			if(haschildcls){
				List childParts = allChildren.get(parentPart);
				/*List<WTPart> childParts = new ArrayList<>();
				for (int index = 0; index < childPartORs.size(); index++) {
					ObjectReference o = (ObjectReference) childPartORs.get(index);
					childParts.add((WTPart) o.getObject());
				}*/
				
				for (int index = 0; index < childParts.size(); index++) {
					Object o = childParts.get(index);
					if (o instanceof WTPart) {
						WTPart childPart = (WTPart) o;
						String childNum = childPart.getNumber();
						boolean hasPhaseCls = false;
						for(int j = 0; j< hasAttr.size(); j ++){
							if(childNum.startsWith(hasAttr.get(j))){
								hasPhaseCls = true;
								break;
							}
						}
						if(hasPhaseCls){
							IBAUtility childiba = new IBAUtility(childPart);
							String childpp = childiba.getIBAValue("ProductPhase");
							if(StringUtils.isBlank(childpp)){
								childpp = "需求分析";
							}
							if(phaseMap.get(targetPhase) > phaseMap.get(childpp) && !sourceList.contains(childPart)){
								sb.append(parentNum).append("的子件").append(childNum).append("产品阶段还是").append(childpp).append("\n");
							}
						
						}//  end if child is WTPart

					}// end if child is WTPart

				}// end for child loop
				sb.append(checkChild(childParts,sourceList,targetPhase));
			} // end if parent Number is Satisfied
		}// end for parent part loop
		return sb.toString();
	}
	
	public static List<WTPart> getRefedPartByDoc(WTDocument doc) throws WTException{
		List<WTPart> result = new ArrayList();
		WTArrayList parts = PartDocServiceCommand.getAssociatedRefParts(doc);
		for(int i = 0; i<parts.size(); i ++){
			ObjectReference or = (ObjectReference) parts.get(i);
			WTPart part = (WTPart) or.getObject();
			result.add(part);
			System.out.println(part.getNumber());
		}		
		return result;		
	}

	/**
	 * 阶段与生产拉线升级
	 * @param part
	 * @param source
	 * @param target
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 * @throws RemoteException
	 */
	public static void upgradePhase(WTPart part,String source,String target) throws WTException, WTPropertyVetoException, RemoteException{
		IBAUtility iba =  new IBAUtility(part);
		String currentPhase = iba.getIBAValue("ProductPhase");
		//String currentPL = iba.getIBAValue("ProductionLine");
		if(source.equals(currentPhase)){
			iba.setIBAValue("ProductPhase", target);
			int i = phaseMap.get(target);
			//int j = phaseMap.get(source);

			//if(line.get(j).equals(currentPL)){
				iba.setIBAValue("ProductionLine", line.get(i));
			//}
			//}
			part = (WTPart) iba.updateAttributeContainer(part);
			iba.updateIBAHolder(part);
		}
	}
	
	/**
	 * PTO拉转量产拉
	 * @param part
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 * @throws RemoteException
	 */
	public static void upgradeProductionLine(WTPart part,String source,String target) throws WTException, WTPropertyVetoException, RemoteException{
		IBAUtility iba =  new IBAUtility(part);
		String currentPL = iba.getIBAValue("ProductionLine");

		if(source.equals(currentPL)){
			iba.setIBAValue("ProductionLine", target);
			part = (WTPart) iba.updateAttributeContainer(part);
			iba.updateIBAHolder(part);
		}		
	}
	
	public static void getConfigMap(){		
		hasChild = new ArrayList<>();
		hasAttr = new ArrayList<>();
		phase = new ArrayList<>();
		line = new ArrayList<>();
		docClsPhaseMap = new ArrayList<>();
		docCls = new ArrayList<>();
		phaseOrLine = new ArrayList<>();
		phaseweight = new ArrayList<>();
		
		ExcelReader er = new ExcelReader(new File(filePathName));
		er.setSheetNum(0);
		try {
			er.open();
			int count = er.getRowCount();
			String[] cells = null;
			
			for(int i=1; i<=count; i++){
				cells = er.readExcelLine(i);
				if(cells==null || (StringUtils.isBlank(cells[0])&& StringUtils.isBlank(cells[1])
						&& StringUtils.isBlank(cells[2])&& StringUtils.isBlank(cells[3]) && StringUtils.isBlank(cells[4]) 
						&& StringUtils.isBlank(cells[5]) && StringUtils.isBlank(cells[6]) && StringUtils.isBlank(cells[7]))){
					break;
				}
				if(!StringUtils.isBlank(cells[0])){
					hasChild.add(cells[0]);
				}
				
				if(!StringUtils.isBlank(cells[1])){
					hasAttr.add(cells[1]);
				}
				
				if(!StringUtils.isBlank(cells[2])){
					phase.add(cells[2]);
				}
				
				if(!StringUtils.isBlank(cells[3])){
					phaseweight.add(cells[3]);
				}
				
				if(!StringUtils.isBlank(cells[4])){
					line.add(cells[4]);
				}
				
				if(!StringUtils.isBlank(cells[5])){
					docCls.add(cells[5]);
				}
				
				if(!StringUtils.isBlank(cells[6])){
					phaseOrLine.add(cells[6]);
				}
				
				if(!StringUtils.isBlank(cells[7])){
					docClsPhaseMap.add(cells[7]);
				}				
				
			}
						
			System.out.println("====RowCount:"+count);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
