package com.catl.change.report;

import java.io.File;
import java.io.IOException;
import java.util.*;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Workbook;

import com.catl.bom.workflow.BomWfUtil;
import com.catl.change.mvc.UsagePartTreesHandler;
import com.catl.change.report.privatepart.PartNode;
import com.catl.common.constant.PartState;
import com.catl.common.util.PartUtil;
import com.catl.common.util.WCLocationConstants;

import wt.enterprise.Master;
import wt.enterprise.RevisionControlled;
import wt.epm.EPMDocument;
import wt.epm.structure.EPMDescribeLink;
import wt.fc.ObjectIdentifier;
import wt.fc.ObjectReference;
import wt.fc.ObjectVector;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.log4j.LogR;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.part.WTPartMaster;
import wt.part.WTPartUsageLink;
import wt.pds.StatementSpec;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.series.MultilevelSeries;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.vc.Iterated;
import wt.vc.VersionControlException;
import wt.vc.VersionControlHelper;
import wt.vc.Versioned;
import wt.vc.config.LatestConfigSpec;
import wt.vc.views.View;
import wt.vc.views.ViewHelper;
import wt.vc.views.ViewManageable;
import wt.vc.wip.WorkInProgressException;
import wt.vc.wip.WorkInProgressHelper;
import wt.clients.vc.CheckInOutTaskLogic;

public class ExportBomDataByPart {
    private static final Logger log = LogR.getLogger(ExportBomDataByPart.class.getName());

    private static String WT_CODEBASE = "";
    static {
        WTProperties wtproperties;
        try {
            wtproperties = WTProperties.getLocalProperties();
            WT_CODEBASE = wtproperties.getProperty("wt.codebase.location");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 生成要导出的Excel文件
     * 
     * @return Workbook
     * @throws WTException
     */
    public Workbook exportReport(String oid) throws WTException {
        if (oid == null || oid.trim().length() <= 0) {
            throw new WTException("oid传值为空，调用错误!");
        }
        ReferenceFactory rf = new ReferenceFactory();
        WTPart part = null;
        try {
        	part = (WTPart) rf.getReference(oid).getObject();
        } catch (WTException e) {
            log.debug("对象" + oid + "不存在!");
            e.printStackTrace();
            throw new WTException("对象" + oid + "不存在!");
        }
        
        if (part == null) {
            log.debug("对象" + oid + "不存在!");
        }
        
        String filePathName = WCLocationConstants.WT_CODEBASE+File.separator
				+"com"+File.separator+"catl"+File.separator
				+"checkPDFData"+File.separator+"BomData_template.xlsx";
        
        //[1.初始化BOM树结构]
        Map<String, PartNode> allChildPartNodes = initBomData(part);
        
        //[2.检查是否为专用物料]
        checkOnlyPart(part,allChildPartNodes);
        
        //[3.获取专用物料集合(排除A——当前顶层部件)]
        TreeMap<String, WTPart> tmap = getBomData_Part(part,allChildPartNodes);
        List<WTPart> lists = new ArrayList<WTPart>();
        lists.addAll(tmap.values());
        try {
            Excel2007Handler excelHander = new Excel2007Handler(filePathName);
        	for (int i = 0; i < lists.size(); i++) {
        		 int rowNum = i + 2;
                 int iCol = 0;
                 //物料创建者：物料最后大版本的第一个小版本（ControlBranch）的创建者
                 //物料修改者：物料最后大版本的最后小版本的创建者
                 //物料所在项目文件夹：产品库或存储库名称/部件所在文件名称/，例如：电子电器件库/项目A/部件
                 excelHander.setStringValue(rowNum,iCol++,lists.get(i).getNumber());
                 excelHander.setStringValue(rowNum,iCol++,lists.get(i).getName());
                 excelHander.setStringValue(rowNum,iCol++,lists.get(i).getLifeCycleState().getDisplay(Locale.CHINA));
                 excelHander.setStringValue(rowNum,iCol++,getFirstAndLastSmallVersion(lists.get(i)).getCreatorFullName());
                 excelHander.setStringValue(rowNum,iCol++,lists.get(i).getCreatorFullName());
                 String loc = lists.get(i).getLocation();
                 if(loc.contains("Default")){
                	 loc = loc.replace("Default",lists.get(i).getContainerName());
                 }
                 
                 excelHander.setStringValue(rowNum,iCol++,loc);
        	}
        	return excelHander.getWorkbook();
        } catch (Exception e) {
            e.printStackTrace();
            throw new WTException(e.getLocalizedMessage());
        }
    }
    
    /**
     * 生成报表数据
     */
    private static TreeMap<String,WTPart> getBomData_Part(WTPart rootpart,Map<String, PartNode> allChildPartNodes){
    	TreeMap<String,WTPart> treemap = new TreeMap<String,WTPart>();
    	
    	//[遍历partnode为true的集合,排除顶层件]
    	for(PartNode node : allChildPartNodes.values()){
    		if(node.isPrivatePart()){
    			if(!rootpart.getNumber().equals(node.getPartNumber())){
    				treemap.put(node.getPartNumber(), node.getPart());
    			}
    		}
    	}
    	
    	return treemap;
    }
    
    /**
     * 1.初始化BOM结构数据
     * 以用户点击菜单项的部件A为起始点，向下逐层递归地遍历BOM结构，查询WTPartUsageLink，遍历时总是取部件的最后大版本的最后小版本（需排除工作副本），
     * 并检查每个WTPartUsageLink是否存在局部替代料的WTPartSubstituteLink，如果存在，则同样需要将WTPartSubstituteLink的Role B端的WTPartMaster对应的最后大版本的最后小版本作为部件的子件。对每一个部件X，
     * 如果根据partNumber可以在allChildPartNodes查找到对应生成对应的PartNode，则无需继续遍历X的下层结构中，而是直接将从allChildPartNodes取得的PartNode加入到X的上层部件的childs中；
     * @throws WTException 
     */
    public static Map<String, PartNode> initBomData(WTPart rootpart) throws WTException{
    	Map<String, PartNode> allChildPartNodes = new HashMap<String,PartNode>();
    	
    	//[构建顶层对象 PartNode]
        PartNode rootNode = new PartNode(rootpart.getNumber());
        
        //[初始化顶层部件结构]
        allChildPartNodes.put(rootpart.getNumber(), rootNode);
        
        //[遍历结构]
    	getBomByParentPart(rootNode,allChildPartNodes); 
    	
    	return allChildPartNodes;
    }
    
    /**
     * 遍历取结构数据:
     * 1.取部件的最后大版本的最后小版本（排除工作副本)
     * @throws WTException 
     */
    public static void getBomByParentPart(PartNode parentNode,Map<String,PartNode> allChildPartNodes) throws WTException{
		QueryResult qr = WTPartHelper.service.getUsesWTPartMasters(parentNode.getPart());
		while(qr.hasMoreElements()){
			WTPartUsageLink link = (WTPartUsageLink)qr.nextElement();
			//[处理替代件]
    		Set<WTPartMaster> subset = UsagePartTreesHandler.getSubWTPartMaster(link);
    		for(WTPartMaster master : subset){
    			WTPart subpart = PartUtil.getLastestWTPartByNumber(master.getNumber());
    			if(!allChildPartNodes.keySet().contains(master.getNumber())){
    				//[构造替代件partnode]
					PartNode subpartnode = new PartNode(subpart.getNumber());
					allChildPartNodes.put(subpart.getNumber(), subpartnode);
					parentNode.getChilds().add(subpartnode);
					getBomByParentPart(subpartnode, allChildPartNodes);
    			}
    			else {
    				parentNode.getChilds().add(allChildPartNodes.get(master.getNumber()));
    			}
    		}
    		
    		//[最新版本]
			WTPart part = PartUtil.getLastestWTPartByNumber(link.getUses().getNumber());
			if(!allChildPartNodes.keySet().contains(part.getNumber())){
				//[childPart]
				PartNode childPartNode = new PartNode(part.getNumber());
				allChildPartNodes.put(part.getNumber(), childPartNode);
				parentNode.getChilds().add(childPartNode);
				getBomByParentPart(childPartNode, allChildPartNodes);
			}
			else {
				parentNode.getChilds().add(allChildPartNodes.get(part.getNumber()));
			}
		}
    }
    
    /**
     * 2.检查对象是否为专用物料
     *   2.1:如果需要通过数据库查询来确定部件X是否为专用物料，则查询部件X对应的WTPartMaster被哪些WTPartUsageLink中使用到（X为Link的Role B），
     *       如果Link的Role A端的部件Y的最新大版本的最后小版本（排除工作副本）也使用了X，则认为部件Y为部件X的父件。并且需要查询部件对应的WTPartMaster被哪些WTPartSubstituteLink中使用到（X为Link的Role B），
     *       如果Link的Role A端对应的WTPartUsageLink的Role A对应的WTPart Z的是部件Z的最新大版本的最后小版本，则认为部件Z也是部件X的父件；
     *   2.2:如果部件Y（或者Z）的编码没有在allChildPartNodes的Key集合中，则认为部件X不是专用物料，则将部件X对应的PartNode的privatePart属性设置为false，checkedPrivatePart设置为true，
     *       同时递归的将childs集合中的各层的PartNode的privatePart属性设置为false，checkedPrivatePart设置为true；
     *   2.3:如果部件Y的的编码在allChildPartNodes的Key集合中，则需要递归的检查部件Y的父件，直到找到顶点部件（顶点部件即没有父件的部件）或者找到部件A为止，
     *       如果在整个的遍历过程中，所有经过的父件节点的编码，都出现在了allChildPartNodes的Key集合中，则认为部件X是专用物料，并将X的checkedPrivatePart属性设置为true；
     *       如果在递归遍历父件的过程中，任何一个父件的编码没有出现在allChildPartNodes的Key集合中，则认为部件X不是专用物料，此时应终止递归遍历，并将部件X对应的PartNode的privatePart属性设置为false，checkedPrivatePart设置为true，
     *       同时递归的将childs集合中的各层的PartNode的privatePart属性设置为false，checkedPrivatePart设置为true；
     * @throws WTException 
     * @throws WorkInProgressException 
     * 
     */
    public static void checkOnlyPart(WTPart rootparent,Map<String,PartNode> allChildPartNodes) throws WTException{
    	PartNode rootNode = allChildPartNodes.get(rootparent.getNumber());
    	checkAllChildNodes(rootNode,rootNode.getPartNumber(),allChildPartNodes);
    }
    
    private static void checkAllChildNodes(PartNode parentNode, String roorNumber, Map<String,PartNode> allChildPartNodes) throws WTException{
    	for(PartNode childnode : parentNode.getChilds()){
    		if(!childnode.isCheckedPrivatePart()){
    			if(isPrivatePart(childnode.getPartMaster(), roorNumber, allChildPartNodes.keySet())){
    				checkAllChildNodes(childnode, roorNumber, allChildPartNodes);
    			}
    			else {
    				setPrivatePartFalse(childnode);
    			}
    		}
    	}
    }
    
    private static boolean isPrivatePart(WTPartMaster currentMaster, String rootPartNumber, Set<String> allPartNumbers) throws WTException{
    	List<WTPart> paretList = filterFatherPartList(currentMaster);   //[有效父件]
    	for(WTPart parentpart : paretList){
    		String parentNumber = parentpart.getNumber();
    		if(!StringUtils.equals(parentNumber, rootPartNumber)){
    			if(allPartNumbers.contains(parentNumber)){
    				if(!isPrivatePart((WTPartMaster)parentpart.getMaster(), rootPartNumber, allPartNumbers)){
    					return false;
    				}
    			}
    			else {
    				return false;
    			}
    		}
    	}
    	return true;
    }
    
    /**
     * [递归的检查部件Y的父件，直到找到顶点部件（顶点部件即没有父件的部件）或者找到部件A为止]
     */
    private static void getFatherPart(WTPart rootparent,WTPart parentpart,List<WTPart> rpartlist,Map<String,PartNode> allChildPartNodes){
    	List<WTPart> fpartlist = getParentPartByChildPart(parentpart);
    	
    	if(fpartlist.size()>0 || fpartlist.contains(rootparent)){
    		for(WTPart fpart : fpartlist){
    			
    			getFatherPart(rootparent,fpart,rpartlist,allChildPartNodes);
    		}
    	}
    }
    
    /**
     * 设置PartNode的属性.
     * @param childnode
     */
    private static void setPrivatePartFalse(PartNode childnode){
    	childnode.setPrivatePart(false);
    	childnode.setCheckedPrivatePart(true);
    	List<PartNode> childs = childnode.getChilds();
    	for(PartNode childNode : childs){
    		setPrivatePartFalse(childNode);
    	}
    }
    
    /**
     * 过滤出符合要求的父件
     *    1. 如果Link的Role A端的部件Y的最新大版本的最后小版本（排除工作副本）也使用了X，则认为部件Y为部件X的父件。并且需要查询部件对应的WTPartMaster被哪些WTPartSubstituteLink中使用到（X为Link的Role B），
     *    2. 如果Link的Role A端对应的WTPartUsageLink的Role A对应的WTPart Z的是部件Z的最新大版本的最后小版本，则认为部件Z也是部件X的父件； 
     * @throws WTException 
     * @throws WorkInProgressException 
     */
    public static List<WTPart> filterFatherPartList(WTPartMaster masterchild) throws WorkInProgressException, WTException{
    	List<WTPart> parentlist = new ArrayList<WTPart>();
    	
    	for(WTPart fpart : getParentPartByChildPart(masterchild)){
    		
    		if(!WorkInProgressHelper.isWorkingCopy(fpart)){
    			WTPart newpart = PartUtil.getLastestWTPartByNumber(fpart.getNumber());		 //取最新版本的part
    			
    			if(eqluasPartversion(fpart,newpart)){
    				parentlist.add(fpart);
    			}
    		}else{
    			WTPart oldPart = (WTPart) CheckInOutTaskLogic.getOriginalCopy(fpart);    		 //取非工作副本的最新版本
    			WTPart newpart = PartUtil.getLastestWTPartByNumber(oldPart.getNumber());		 //取最新版本的part
    			
    			if(eqluasPartversion(oldPart,newpart)){
    				parentlist.add(oldPart);
    			}
    		}
    	}
    	
    	return parentlist;
    }
    
    /**
     * 是否最新版本
     * @return
     */
    public static boolean eqluasPartversion(WTPart oldPart,WTPart newPart){
    	
		//取最新版本和当前对象的版本进行比较,相同的表示最新关联
		String oldversionid = oldPart.getVersionIdentifier().getValue()+"."+oldPart.getIterationIdentifier().getValue();
		String newversionid = newPart.getVersionIdentifier().getValue()+"."+newPart.getIterationIdentifier().getValue();
		
		if(oldversionid.equals(newversionid)){
			return true;
		}
		
    	return false;
    }
    
    //获取master对应的特定替换件
    private static Set<WTPart> getSubstituteLinks(WTPartMaster Master) throws WTException {
    	
        Set<WTPart> setfpart = new HashSet<WTPart>();
        QueryResult qr = WTPartHelper.service.getSubstituteForWTPartUsageLinks(Master);
        
        while (qr.hasMoreElements()) {
        	WTPartUsageLink link = (WTPartUsageLink) qr.nextElement();
        	WTPart subFpart = (WTPart)link.getRoleAObject();    //取roleA
        	
			setfpart.add(subFpart);
        }
        return setfpart;
    }
    
    /**
     * 获取零部件parent的第一层子件的WTPartMaster集合
     * @param parent
     * @return
     * @throws WTException
     */
    public static Set<WTPartMaster> getUsesWTPartMaster(WTPart parent) throws WTException{
    	Set<WTPartMaster> usesPart = new HashSet<WTPartMaster>();
    	QueryResult qr = WTPartHelper.service.getUsesWTPartMasters(parent);
    	while(qr.hasMoreElements()){
    		WTPartUsageLink link = (WTPartUsageLink)qr.nextElement();
			usesPart.add(link.getUses());
    	}
    	return usesPart;
    }
    
    /**
     * 获取最新大版本的最新小版本
     * @param partNo
     * @param viewName
     * @return
     * @throws WTException
     */
    public static WTPart getPartByNoAndView(String partNo, String viewName) throws WTException {
        WTPart part = null;
        if(viewName == null || viewName.equals("")){
            viewName = "Design";
        }
        
        if (!"".equals(partNo) && partNo!=null && !"".equals(viewName)) {
            View view = ViewHelper.service.getView(viewName);
            ObjectIdentifier objId = PersistenceHelper.getObjectIdentifier(view);

            QuerySpec qs = new QuerySpec(WTPart.class);
            int iIndex = qs.getFromClause().getPosition(WTPart.class);
            SearchCondition sc = new SearchCondition(WTPart.class, WTPart.NUMBER, SearchCondition.EQUAL, partNo.toUpperCase());
            qs.appendWhere(sc, new int[] { iIndex });
            qs.appendAnd();
            sc = new SearchCondition(WTPart.class, ViewManageable.VIEW + "." + ObjectReference.KEY, SearchCondition.EQUAL, objId);
            qs.appendWhere(sc, new int[] { iIndex });
            QueryResult qr = PersistenceHelper.manager.find(qs);
            if (qr.hasMoreElements()) {
                LatestConfigSpec configSpec = new LatestConfigSpec();
                qr = configSpec.process(qr);
                part = (WTPart) qr.nextElement();
            }
        }
        return part;
    }
	
	/**
	 * 获取对象最新大版本的第一个小版本
	 * @param part
	 * @return
	 * @throws WTException 
	 */
    public static WTPart getFirstAndLastSmallVersion(WTPart part) throws WTException{
    	WTPart firstVsPart = null;
    	
    	Long branchID = part.getBranchIdentifier();
    	
    	QuerySpec qs = new QuerySpec(WTPart.class);
		SearchCondition sc = new SearchCondition(WTPart.class, "iterationInfo.branchId", SearchCondition.EQUAL, branchID);
		qs.appendWhere(sc, new int[] { 0 });
		qs.appendAnd();
		
		sc = new SearchCondition(WTPart.class, "iterationInfo.identifier.iterationId", SearchCondition.EQUAL,"1");
		qs.appendWhere(sc, new int[] { 0 });
		
        QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
        if(qr.hasMoreElements()){
        	firstVsPart = (WTPart)qr.nextElement();
        }
        
		return firstVsPart;
    }
    
    /**
     * 根据部件查询其父件（替代件,ulink）
     * @throws WTException 
     */
    public static List<WTPart> getParentPartByChildPart(WTPartMaster masterchild) throws WTException {
    	List<WTPart> parentParts = new ArrayList<WTPart>();
        
        try {
        	QueryResult qr = WTPartHelper.service.getUsedByWTParts(masterchild);
        	qr = new LatestConfigSpec().process(qr);  //过滤最新
            while (qr != null && qr.hasMoreElements()) {
                Object obj = qr.nextElement();
                if (obj instanceof WTPart) {
                    WTPart parentPart = (WTPart) obj;	
                    parentParts.add(parentPart);		//[link父]
                }
            }	
        } catch (WTException e) {
            e.printStackTrace();
        }
        
        //[替代件父]
        parentParts.addAll(getSubstituteLinks(masterchild));
        
        return parentParts;
    }
    
    
    /**
     * 根据部件查询其父件
     */
    public static List<WTPart> getParentPartByChildPart(WTPart part) {
    	List<WTPart> parentParts = new ArrayList<WTPart>();
        QueryResult qr;
        try {
            qr = WTPartHelper.service.getUsedByWTParts((WTPartMaster) part.getMaster());
            qr = (new LatestConfigSpec()).process(qr);
            while (qr != null && qr.hasMoreElements()) {
                Object obj = qr.nextElement();
                if (obj instanceof WTPart) {
                    WTPart parentPart = (WTPart) obj;
                    if(BomWfUtil.isLastVersion(parentPart)){
                    	parentParts.add(parentPart);
                    }
                }
            }
        } catch (WTException e) {
            e.printStackTrace();
        }
        return parentParts;
    }
    
}
