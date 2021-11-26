package com.catl.change.mvc;

import java.util.*;

import org.apache.log4j.Logger;

import wt.clients.vc.CheckInOutTaskLogic;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.part.WTPartMaster;
import wt.part.WTPartUsageLink;
import wt.pdmlink.PDMLinkProduct;
import wt.pds.StatementSpec;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTException;
import wt.vc.config.LatestConfigSpec;
import wt.vc.wip.WorkInProgressHelper;

import com.catl.bom.workflow.BomWfUtil;
import com.catl.change.report.ExportBomDataByPart;
import com.catl.common.util.PartUtil;
import com.ptc.core.components.beans.TreeHandlerAdapter;
import com.ptc.netmarkets.util.beans.NmCommandBean;

public class UsagePartTreesHandler extends TreeHandlerAdapter {
    private WTPartMaster dcpartmaster = null;
    private List tdjlist = new ArrayList();
    
    public UsagePartTreesHandler(List tdjlist,WTPartMaster dcpartmaster){
    	this.tdjlist = tdjlist;
    	this.dcpartmaster = dcpartmaster;
    }
    
    public List getRootNodes() throws WTException {
    	List list = new ArrayList<>();
    	NmCommandBean nmCommandBean = getModelContext().getNmCommandBean();
    	Object obj = nmCommandBean.getActionOid().getRefObject();
    	if(obj instanceof WTPart){
    		WTPart part = (WTPart)obj;
        	list.add(part.getMaster());
    	}
    	
        return list;
    }
    
    public Map getNodes(List parents) throws WTException {
        NmCommandBean nmCommandBean = getModelContext().getNmCommandBean();
        if (nmCommandBean == null) {
            return null;
        }
       
        Map result = new HashMap();
        for(Object obj : parents){
        	//1.如果当前对象是顶层,则取它的所有替代件; 2.如果当前对象是替代件,则取它的父项.
        	if(obj instanceof WTPartMaster){
        		WTPartMaster master = (WTPartMaster)obj;
        		List childs = new ArrayList<>(getSubstituteLinks(master));
        		result.put(master, childs);
        	}else if(obj instanceof WTPart){
        		WTPart parent = (WTPart)obj;
        		List<WTPart> listp = getParentPartByChildPart(parent);
        		result.put(parent, listp);
        	}
        }
        
        return result;
    }
    
	//获取特定替换件
    public static Set<WTPart> getSubstituteLinks(WTPartMaster rootMaster) throws WTException {
    	
        Set<WTPart> separt = new HashSet<WTPart>();
        QueryResult qr = WTPartHelper.service.getSubstituteForWTPartUsageLinks(rootMaster);
        while (qr.hasMoreElements()) {
        	WTPartUsageLink link = (WTPartUsageLink) qr.nextElement();
        	WTPartMaster cdms = link.getUses();    //取roleB
        	WTPart fpart = link.getUsedBy();       //取roleA?
        	
        	Set<WTPartMaster> masterSet = new HashSet<WTPartMaster>();
        	//取RoleA的最新大版本的最新小版本(排除工作副本)
        	fpart = ExportBomDataByPart.getPartByNoAndView(fpart.getNumber(),"Design");
			if(!WorkInProgressHelper.isWorkingCopy(fpart)){
				masterSet = ExportBomDataByPart.getUsesWTPartMaster(fpart);
			}else{
				fpart = (WTPart) CheckInOutTaskLogic.getOriginalCopy(fpart);    //取非工作副本的最新版本
				masterSet = ExportBomDataByPart.getUsesWTPartMaster(fpart);
			}
			WTPart childpart = PartUtil.getLastestWTPartByNumber(cdms.getNumber());
			//判断父件最新大版本的最新小版本是否有跟子件关联没有则不加.
			if(masterSet.contains(childpart.getMaster())){
				WTPartUsageLink newlink = getWTPartUsageLink(fpart,(WTPartMaster)childpart.getMaster());
				Set<WTPartMaster> setmaster = getSubWTPartMaster(newlink);
				if(setmaster.contains(rootMaster)){
					separt.add(childpart);
				}
			}
			
        }
        return separt;
    }
    
    //获取特定替换件
    private static Set<WTPart> getSubstituteLinks1(WTPartMaster rootMaster) throws WTException {
    	
        Set<WTPart> separt = new HashSet<WTPart>();
        QueryResult qr = WTPartHelper.service.getSubstituteForWTPartUsageLinks(rootMaster);
        
        while (qr.hasMoreElements()) {
        	WTPartUsageLink link = (WTPartUsageLink) qr.nextElement();
        	WTPartMaster cdms = link.getUses();    //取roleB
        	
			WTPart childpart = PartUtil.getLastestWTPartByNumber(cdms.getNumber());
			separt.add(childpart);
        }
        return separt;
    }
    
    /**
     * 根据usrlink取它的可替代件
     * @throws WTException 
     */
    public static Set<WTPartMaster> getSubWTPartMaster(WTPartUsageLink link) throws WTException{
    	
    	Set<WTPartMaster>  setmaster = new HashSet<WTPartMaster>();
    	QueryResult qrmster = WTPartHelper.service.getSubstitutesWTPartMasters(link);
		while(qrmster.hasMoreElements()){
			WTPartMaster subMs = (WTPartMaster)qrmster.nextElement();
			setmaster.add(subMs);
		}
		return setmaster;
    }
    
    /**
     * 根据部件查询其父件
     */
    public List<WTPart> getParentPartByChildPart(WTPart part) {
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
                    	if(tdjlist.contains(part)){
                    		if(getSubWTPartMaster(getusedLinkByPart(parentPart,(WTPartMaster)part.getMaster())).contains(dcpartmaster)){
                        		parentParts.add(parentPart);
                        	}
                    	}else{
                    		parentParts.add(parentPart);
                    	}
                    }
                }
            }
        } catch (WTException e) {
            e.printStackTrace();
        }
        return parentParts;
    }
    
    //通过父项和子件Master取link
    public static WTPartUsageLink getusedLinkByPart(WTPart wtparent,WTPartMaster master) throws WTException{
    	WTPartUsageLink relink = null;
    	QueryResult qr = WTPartHelper.service.getUsesWTPartMasters(wtparent);
    	
    	while(qr.hasMoreElements()){
    		WTPartUsageLink link = (WTPartUsageLink)qr.nextElement();
    		if(link.getUses().equals(master)){
    			relink = link;
    		}
    	}
    	return relink;
    }
    
	public static WTPartUsageLink getWTPartUsageLink(WTPart part, WTPartMaster master) throws WTException {
		if (part == null || master == null) {
			return null;
		}
		
		WTPartUsageLink link = null;
		QuerySpec qs = new QuerySpec(WTPartUsageLink.class);
		SearchCondition sc = new SearchCondition(WTPartUsageLink.class, "roleAObjectRef.key.id", SearchCondition.EQUAL, PersistenceHelper.getObjectIdentifier(part).getId());
		qs.appendWhere(sc, new int[] { 0 });
		qs.appendAnd();
		sc  = new SearchCondition(WTPartUsageLink.class, "roleBObjectRef.key.id", SearchCondition.EQUAL, PersistenceHelper.getObjectIdentifier(master).getId());
		qs.appendWhere(sc, new int[] { 0 });
		QueryResult qr = PersistenceHelper.manager.find((StatementSpec)qs);
		while (qr.hasMoreElements()) {
			link = (WTPartUsageLink) qr.nextElement();
		}
		return link;
	}
}