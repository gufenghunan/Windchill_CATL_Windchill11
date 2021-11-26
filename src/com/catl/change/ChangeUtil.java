package com.catl.change;

import java.rmi.RemoteException;
import java.util.*;

import wt.change2.*;
import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.enterprise.RevisionControlled;
import wt.epm.*;
import wt.fc.*;
import wt.iba.definition.DefinitionLoader;
import wt.iba.definition.litedefinition.AbstractAttributeDefinizerView;
import wt.iba.definition.litedefinition.AttributeDefDefaultView;
import wt.iba.definition.litedefinition.AttributeDefNodeView;
import wt.iba.definition.litedefinition.BooleanDefView;
import wt.iba.definition.litedefinition.StringDefView;
import wt.iba.definition.service.IBADefinitionHelper;
import wt.iba.value.DefaultAttributeContainer;
import wt.iba.value.IBAHolder;
import wt.iba.value.litevalue.AbstractValueView;
import wt.iba.value.litevalue.BooleanValueDefaultView;
import wt.iba.value.litevalue.StringValueDefaultView;
import wt.iba.value.service.IBAValueHelper;
import wt.iba.value.service.StandardIBAValueService;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.pds.StatementSpec;
import wt.pom.PersistenceException;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.type.Typed;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.config.ConfigHelper;
import wt.vc.config.LatestConfigSpec;

import com.catl.cad.BatchDownloadPDFUtil;
import com.catl.change.inventory.ECAPartLink;
import com.catl.change.util.ChangeConst;
import com.catl.common.constant.ChangeState;
import com.catl.common.constant.Constant;
import com.catl.common.constant.TypeName;
import com.catl.common.util.EpmUtil;
import com.catl.common.util.PartUtil;
import com.catl.doc.maturityUpReport.MaturityUpReportHelper;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.lwc.common.view.TypeDefinitionReadView;
import com.ptc.core.lwc.server.PersistableAdapter;
import com.ptc.core.lwc.server.TypeDefinitionServiceHelper;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.core.meta.common.UpdateOperationIdentifier;
import com.ptc.core.meta.common.impl.TypeIdentifierUtilityHelper;
import com.ptc.core.meta.server.TypeIdentifierUtility;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.windchill.enterprise.part.commands.PartDocServiceCommand;

public class ChangeUtil {
	/**
	 * 当前版本：检查受影响对象是否包含或关联 成熟度不为3和6
	 * @param objOrig  ChangeRequest2或ChangeActivity2  可以扩充
	 * @param objectBeanList 
	 * @return
	 * @throws WTException
	 */
	public static StringBuffer checkMaturity(Object objOrig, List<ObjectBean> objectBeanList) throws WTException{
		StringBuffer retMessage = new StringBuffer();
		List<WTPart> retListPart = new ArrayList<WTPart>();
		if (objOrig instanceof ChangeRequest2) {
			ChangeRequest2 ecr = (ChangeRequest2) objOrig;
			QueryResult qr = ChangeHelper2.service.getChangeables(ecr);
			while (qr.hasMoreElements()) {
				Object obj = qr.nextElement();
				checkMaturityType(retMessage, retListPart, obj);
			}
		}else if (objOrig instanceof ChangeActivity2) {
			for(ObjectBean bean : objectBeanList){
				List affectedItems = bean.getAddedItemsByName("changeTask_affectedItems_table");
				for(Object obj : affectedItems){
					NmOid nmOid = (NmOid) obj;
					Object object = nmOid.getLatestIterationObject();
					if (object instanceof RevisionControlled)
						checkMaturityType(retMessage, retListPart, object);
				}
			}
		}
		
		return retMessage;
	}
	
	/**
	 * 检查对象关联的物料
	 * @param message
	 * @param appendedPart
	 * @param obj
	 * @throws WTException
	 * @throws PersistenceException
	 */
	public static void checkMaturityType(StringBuffer message, List<WTPart> appendedPart, Object obj) throws WTException, PersistenceException {
		if (obj instanceof WTPart) {//零部件
			WTPart part = (WTPart) obj;
			checkMaturityMessage(message, part,"",appendedPart);
		}else if(obj instanceof EPMDocument){
			EPMDocument epmdoc = (EPMDocument) obj;
			if(!BatchDownloadPDFUtil.isMiddleware(epmdoc)){
				if (epmdoc.getDocType().toString().equals("CADDRAWING")) {//CAD图纸
					WTPart part = PartUtil.getRelationPartBy2D(epmdoc);
					if(part != null){
						checkMaturityMessage(message, part,"CAD图纸"+epmdoc.getNumber()+"相关联的",appendedPart);
					}else
						message.append("CAD图纸"+epmdoc.getNumber()+"没有关联部件\n");
				}else{//Catia模型
					WTPart part = PartUtil.getRelationPartBy3D(epmdoc);
					if(part != null){
						checkMaturityMessage(message, part,"Catia模型"+epmdoc.getNumber()+"相关联的",appendedPart);
					}else
						message.append("Catia模型"+epmdoc.getNumber()+"没有关联部件\n");
				}
			}
		}else if(obj instanceof WTDocument){
			WTDocument doc = (WTDocument) obj;
			TypeIdentifier ti = TypeIdentifierUtility.getTypeIdentifier(doc);
			String type = ti.getTypename();
			String typeMsg="";
			if (type.endsWith(TypeName.doc_type_autocadDrawing))//线束AUTOCAD图纸
				typeMsg="线束AUTOCAD图纸";
			else if(type.contains(TypeName.doc_type_pcbaDrawing))//PCBA装配图
				typeMsg="PCBA装配图";
			else if(type.contains(TypeName.doc_type_gerberDoc))//GERBER文件
				typeMsg="GERBER文件";
			if(!typeMsg.equals("")){
				WTPart part = PartUtil.getRelationPartByDescDoc(doc);
				if(part != null)
					checkMaturityMessage(message, part,typeMsg+doc.getNumber()+"相关联的",appendedPart);
				else
					message.append(typeMsg+doc.getNumber()+"没有关联部件\n");
			}
		}
	}
	/**
	 * 成熟度是否为3和6
	 * @param message
	 * @param part
	 * @param messagePrefix
	 * @throws WTException
	 */
	public static void checkMaturityMessage(StringBuffer message, WTPart part,String messagePrefix,List<WTPart> appendedPart) throws WTException {
		PersistableAdapter genericObj = new PersistableAdapter(part.getMaster(), null, null, new UpdateOperationIdentifier());
		genericObj.load("CATL_Maturity");
		String maturity = (String)genericObj.get("CATL_Maturity");
		if(maturity != null && !maturity.equals(Constant.MATURITY_THREE) && !maturity.equals(Constant.MATURITY_SIX)){
			message.append(messagePrefix+"零部件"+part.getNumber()+"的成熟度必须为3或6\n");
			appendedPart.add(part);
		}
	}
	
	/**
	 * param str 要分割的字符串
	 * return ary[ary.length-1] 返回最后一个元数
	 * @author zyw2
	 */
	public static String getStrSplit(Persistable p) {
		
		String str = TypeIdentifierUtility.getTypeIdentifier(p).getTypename();
		
		if (str != null) {
			return str.substring(str.lastIndexOf("|") + 1, str.length());
		}
		return "";
	}
	
	/**
	 * GET WTChangeActivity2
	 * @param Persiser
	 * @return
	 * @throws WTException 
	 */
	public static WTChangeActivity2 getEcaWithPersiser(Persistable per) throws WTException{
//		WTChangeActivity2 eca = null;
//		
//		try{
//			QuerySpec qs = new QuerySpec(ChangeRecord2.class);
// 			SearchCondition sc = new SearchCondition(ChangeRecord2.class, "roleBObjectRef.key", SearchCondition.EQUAL, per.getPersistInfo().getObjectIdentifier());
//			qs.appendWhere(sc, new int[] { 0 });
//	        QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
//			if(qr.hasMoreElements()){
//				ChangeRecord2 cr2 = (ChangeRecord2)qr.nextElement();
//				eca = (WTChangeActivity2)cr2.getChangeActivity2();
//				
//			}
//		}catch(WTException e){
//			e.printStackTrace();
//		}
//		return eca;
		
		QueryResult ecaResult = ChangeHelper2.service.getAffectingChangeActivities((Changeable2)per);
		while(ecaResult.hasMoreElements()){
			WTChangeActivity2 eca = (WTChangeActivity2)ecaResult.nextElement();
			if (!eca.getState().toString().equalsIgnoreCase(ChangeState.RESOLVED) && !eca.getState().toString().equalsIgnoreCase(ChangeState.CANCELLED)) {
				return eca;
			}
		}
		return null;
	}
	/**
	 * 
	 * @param caNumber
	 * @return
	 * @throws WTException 
	 */
	public static WTChangeActivity2 getCAByNumber(String caNumber) throws WTException {
		QuerySpec qs = new QuerySpec(WTChangeActivity2.class);
		SearchCondition sc = new SearchCondition(WTChangeActivity2.class, WTChangeActivity2.NUMBER, SearchCondition.EQUAL, caNumber);
		qs.appendWhere(sc, new int[] { 0 });
        QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
		if(qr.hasMoreElements()){
			return (WTChangeActivity2)qr.nextElement();
		}
		return null;
	}
	
	/**
	 * 通过ECO查找关联的ECA
	 * 
	 * @param eco
	 */
	public static List<WTChangeActivity2> getChangeActiveities(
			WTChangeOrder2 eco) {
		List<WTChangeActivity2> listEca = new ArrayList<WTChangeActivity2>();
		try {
			QueryResult qs = ChangeHelper2.service.getChangeActivities(eco);
			while (qs.hasMoreElements()) {
				WTChangeActivity2 eca = (WTChangeActivity2) qs.nextElement();
				listEca.add(eca);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listEca;
	}
	
	// 获取受影响对象列表
    public static Map<String, List<Object>> getBeforeData(WTChangeOrder2 eco) throws WTException {
        Map<String, List<Object>> map = new HashMap<String, List<Object>>();
        List<Object> listPart = new ArrayList<Object>();
        List<Object> listDoc = new ArrayList<Object>();
        List<Object> listEPM = new ArrayList<Object>();
        if (eco != null) {
            QueryResult qs = ChangeHelper2.service.getChangeActivities(eco);
            while (qs.hasMoreElements()) {
                WTChangeActivity2 activity = (WTChangeActivity2) qs.nextElement();

                QueryResult qsafter = ChangeHelper2.service.getChangeablesBefore(activity);
                while (qsafter.hasMoreElements()) {
                    Object obj = qsafter.nextElement();
                    if (obj instanceof WTPart) {
                        listPart.add((WTPart) obj);
                    } else if (obj instanceof WTDocument) {
                        listDoc.add((WTDocument) obj);
                    } else if (obj instanceof EPMDocument) {
                        listEPM.add((EPMDocument) obj);
                    }
                }
            }
        }
        map.put(ChangeConst.BEFORE_PART, listPart);
        map.put(ChangeConst.BEFORE_DOC, listDoc);
        map.put(ChangeConst.BEFORE_EPM, listEPM);
        return map;
    }
    
	// 获取产生对象列表
    public static List<Object> getAfterData(WTChangeActivity2 dca) throws WTException {
        List<Object> listAll = new ArrayList<Object>();
        QueryResult qsafter = ChangeHelper2.service.getChangeablesAfter(dca);
        while (qsafter.hasMoreElements()) {
            Object obj = qsafter.nextElement();
            if (obj instanceof WTPart) {
            	listAll.add(obj);
            } else if (obj instanceof WTDocument) {
            	listAll.add(obj);
            } else if (obj instanceof EPMDocument) {
            	listAll.add(obj);
            }
        }
        return listAll;
    }
    
    /**
     * 通过dca获取受影响对象
     * @throws WTException 
     * @throws ChangeException2 
     */
    public static Map<String,List<Object>> getBeforeDataByDca(WTChangeActivity2 activity) throws WTException{
    	Map<String,List<Object>> map = new HashMap<String,List<Object>>();
    	List<Object> listPart = new ArrayList<Object>();
    	List<Object> listDoc = new ArrayList<Object>();
    	List<Object> listEPM = new ArrayList<Object>();
    	QueryResult qsafter = ChangeHelper2.service.getChangeablesBefore(activity);
    	while (qsafter.hasMoreElements()) {
    		Object obj = qsafter.nextElement();
    		if (obj instanceof WTPart) {
    			listPart.add((WTPart) obj);
    		} else if (obj instanceof WTDocument) {
    			listDoc.add((WTDocument) obj);
    		} else if (obj instanceof EPMDocument) {
    			listEPM.add((EPMDocument) obj);
    		}
    	}
    	map.put(ChangeConst.BEFORE_PART, listPart);
    	map.put(ChangeConst.BEFORE_DOC, listDoc);
    	map.put(ChangeConst.BEFORE_EPM, listEPM);
    	return map;
    }
	
    /**
     * 通过dca获取产生的对象
     * @throws WTException 
     * @throws ChangeException2 
     */
    public static Map<String,List<Object>> getAfterDataByDca(WTChangeActivity2 activity) throws WTException{
    	Map<String,List<Object>> map = new HashMap<String,List<Object>>();
    	List<Object> listPart = new ArrayList<Object>();
    	List<Object> listDoc = new ArrayList<Object>();
    	List<Object> listEPM = new ArrayList<Object>();
    	QueryResult qsafter = ChangeHelper2.service.getChangeablesAfter(activity);
    	while (qsafter.hasMoreElements()) {
    		Object obj = qsafter.nextElement();
    		if (obj instanceof WTPart) {
    			listPart.add((WTPart) obj);
    		} else if (obj instanceof WTDocument) {
    			listDoc.add((WTDocument) obj);
    		} else if (obj instanceof EPMDocument) {
    			listEPM.add((EPMDocument) obj);
    		}
    	}
    	map.put(ChangeConst.AFTRE_PART, listPart);
    	map.put(ChangeConst.AFTRE_DOC, listDoc);
    	map.put(ChangeConst.AFTRE_EPM, listEPM);
    	return map;
    }
    
    /**
     * 检查DCN/DCA中受影响的零部件是否有关联的“非FAE成熟度3升级报告
     * @throws WTException 
     */
    public static String isHasdocLinkByDcn(Object pbo) throws WTException{
    	if(getdocMasterListByPart(pbo).size()>0){
    		return "true";
    	}
    	return "false";
    }

    /**
     * get docmaster by part
     * @param pbo
     * @return
     * @throws WTException
     */
    public static Map<WTPart,WTDocumentMaster> getdocMasterListByPart(Object pbo) throws WTException{
    	Map<WTPart,WTDocumentMaster> mp = new HashMap<WTPart,WTDocumentMaster>();
    	List<Object> datalist = new ArrayList<Object>();
    	
    	if(pbo instanceof WTChangeOrder2){
    		datalist = ChangeUtil.getBeforeData((WTChangeOrder2)pbo).get(ChangeConst.BEFORE_PART);
    	}else if(pbo instanceof WTChangeActivity2){
    		datalist = ChangeUtil.getBeforeDataByDca((WTChangeActivity2)pbo).get(ChangeConst.BEFORE_PART);
    	}
    	
    	for(Object obj : datalist){
    		if(obj instanceof WTPart){
    			WTPart befpart = (WTPart)obj;
    			WTDocumentMaster docmaster = MaturityUpReportHelper.getNFAEMaturityUp3DocMaster((WTPartMaster)befpart.getMaster());
    			if(docmaster!=null){
    				mp.put(befpart, docmaster);
    			}
    		}
    	}
    	
    	return mp;
    }
    
    /**
     * list转换map
     */
    public static Map<String,Object> convertListToMap(List<Object> list){
    	Map<String,Object> mp = new HashMap<String,Object>();
    	for(Object obj : list){
    		if (obj instanceof WTPart) {
    			mp.put(((WTPart)obj).getNumber(), obj);
    		} else if (obj instanceof WTDocument) {
    			mp.put(((WTDocument)obj).getNumber(), obj);
    		} else if (obj instanceof EPMDocument) {
    			mp.put(((EPMDocument)obj).getNumber(), obj);
    		}
    	}
    	return mp;
    }
    
    /**
     * Set iba attribute value
     * @param obj         object
     * @param ibaName     attribute name
     * @param newValue     attribute value
     * @return void
     * @exception WTException
     */
    public static void setIBAStringValue(WTObject obj, String ibaName, String newValue) throws WTException {
        String ibaClass = "wt.iba.definition.StringDefinition";
        //   System.out.println("ENTER..." + ibaName + "..." + newValue);
        try {
            if (obj instanceof IBAHolder) {
                IBAHolder ibaHolder = (IBAHolder) obj;
                DefaultAttributeContainer defaultattributecontainer = getContainer(ibaHolder);
                if (defaultattributecontainer == null) {
                    defaultattributecontainer = new DefaultAttributeContainer();
                    ibaHolder.setAttributeContainer(defaultattributecontainer);
                }
                StringValueDefaultView abstractvaluedefaultview = (StringValueDefaultView) getIBAValueView(defaultattributecontainer, ibaName, ibaClass);
                if (abstractvaluedefaultview != null) {
                    abstractvaluedefaultview.setValue(newValue);
                    defaultattributecontainer.updateAttributeValue(abstractvaluedefaultview);
                } else {
                    AttributeDefDefaultView attributedefdefaultview = getAttributeDefinition(ibaName, false);
                    StringValueDefaultView abstractvaluedefaultview1 = new StringValueDefaultView((StringDefView) attributedefdefaultview, newValue);
                    defaultattributecontainer.addAttributeValue(abstractvaluedefaultview1);
                }
                ibaHolder.setAttributeContainer(defaultattributecontainer);
                StandardIBAValueService.theIBAValueDBService.updateAttributeContainer(ibaHolder, null, null, null);
                ibaHolder = IBAValueHelper.service.refreshAttributeContainer(ibaHolder, "CSM", null, null);
                //wt.iba.value.service.LoadValue.applySoftAttributes(ibaHolder);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    
    /**
     * Set IBA Boolean Value
     *
     * @param obj
     * @param ibaName: iba name
     * @param new Value: TRUE or FALSE
     * @exception WTException
     */
    public static void setIBABooleanValue(WTObject obj, String ibaName, boolean newValue) throws WTException {
        String ibaClass = "wt.iba.definition.BooleanDefinition";
        try {
            if (obj instanceof IBAHolder) {
                IBAHolder ibaHolder = (IBAHolder) obj;
                DefaultAttributeContainer defaultattributecontainer = getContainer(ibaHolder);
                if (defaultattributecontainer == null) {
                    defaultattributecontainer = new DefaultAttributeContainer();
                    ibaHolder.setAttributeContainer(defaultattributecontainer);
                }
                BooleanValueDefaultView abstractvaluedefaultview = (BooleanValueDefaultView) getIBAValueView(defaultattributecontainer, ibaName, ibaClass);
                if (abstractvaluedefaultview != null) {
                    abstractvaluedefaultview.setValue(newValue);
                    defaultattributecontainer.updateAttributeValue(abstractvaluedefaultview);
                } else {
                    AttributeDefDefaultView attributedefdefaultview = getAttributeDefinition(ibaName, false);
                    BooleanValueDefaultView abstractvaluedefaultview1 = new BooleanValueDefaultView((BooleanDefView) attributedefdefaultview, newValue);
                    defaultattributecontainer.addAttributeValue(abstractvaluedefaultview1);
                }
                ibaHolder.setAttributeContainer(defaultattributecontainer);
                StandardIBAValueService.theIBAValueDBService.updateAttributeContainer(ibaHolder, null, null, null);
                ibaHolder = IBAValueHelper.service.refreshAttributeContainer(ibaHolder, "CSM", null, null);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

    }
    
    /**
     * Get attribute definition
     *
     * @param s
     * @param flag
     * @return AttributeDefDefaultView
     */
    public static AttributeDefDefaultView getAttributeDefinition(String s, boolean flag) {
        AttributeDefDefaultView attributedefdefaultview = null;
        try {
            attributedefdefaultview = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath(s);
            if (attributedefdefaultview == null) {
                AbstractAttributeDefinizerView abstractattributedefinizerview = DefinitionLoader.getAttributeDefinition(s);
                if (abstractattributedefinizerview != null) {
                    attributedefdefaultview = IBADefinitionHelper.service.getAttributeDefDefaultView((AttributeDefNodeView) abstractattributedefinizerview);
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return attributedefdefaultview;
    }
    
    public static DefaultAttributeContainer getContainer(IBAHolder ibaHolder) throws WTException, RemoteException {
        ibaHolder = IBAValueHelper.service.refreshAttributeContainerWithoutConstraints(ibaHolder);
        DefaultAttributeContainer defaultattributecontainer = (DefaultAttributeContainer) ibaHolder.getAttributeContainer();
        return defaultattributecontainer;
    }
    
    public static AbstractValueView getIBAValueView(DefaultAttributeContainer dac, String ibaName, String ibaClass) throws WTException {
        AbstractValueView aabstractvalueview[] = null;
        AbstractValueView avv = null;
        aabstractvalueview = dac.getAttributeValues();
        for (int j = 0; j < aabstractvalueview.length; j++) {
            String thisIBAName = aabstractvalueview[j].getDefinition().getName();
            String thisIBAClass = (aabstractvalueview[j].getDefinition()).getAttributeDefinitionClassName();
            if (thisIBAName.equals(ibaName) && thisIBAClass.equals(ibaClass)) {
                avv = aabstractvalueview[j];
                break;
            }
        }
        return avv;
    }
    
    private static AttributeDefDefaultView getAttributeDefinition(String s) {
        AttributeDefDefaultView attributedefdefaultview = null;
        try {
            attributedefdefaultview = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath(s);
            if (attributedefdefaultview == null) {
                AbstractAttributeDefinizerView abstractattributedefinizerview = DefinitionLoader.getAttributeDefinition(s);
                if (abstractattributedefinizerview != null)
                    attributedefdefaultview = IBADefinitionHelper.service.getAttributeDefDefaultView((AttributeDefNodeView) abstractattributedefinizerview);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return attributedefdefaultview;
    }
    
	/**
	 * 通过oid获取相应的对象
	 * 
	 */
	public static Persistable getPersistable(String oid)  {
		Persistable p = null;
		try {
			ReferenceFactory rf = new ReferenceFactory();
			WTReference ref = rf.getReference(oid);
			p = ref.getObject();
		} catch (WTException e) {
			e.printStackTrace();
			return null;
		}
		return p;
	}
	
	/**
	 * 获取对象的显示名称.(部件,文档,EPM)
	 * @param typed_object
	 * @return
	 * @throws RemoteException
	 * @throws WTException
	 */
    public static String getTypeDisplayName(Typed typed_object) throws RemoteException, WTException{
    	TypeIdentifier type = TypeIdentifierUtilityHelper.service.getTypeIdentifier(typed_object);
    	TypeDefinitionReadView trv = TypeDefinitionServiceHelper.service.getTypeDefView(type);
    	return trv.getDisplayName();
    }
    
}
