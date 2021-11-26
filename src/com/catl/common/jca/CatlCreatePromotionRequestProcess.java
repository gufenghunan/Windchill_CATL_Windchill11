package com.catl.common.jca;

import java.util.List;

import org.apache.log4j.Logger;

import wt.change2.WTChangeActivity2;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.fc.collections.WTHashSet;
import wt.fc.collections.WTSet;
import wt.log4j.LogR;
import wt.maturity.MaturityBaseline;
import wt.maturity.MaturityHelper;
import wt.maturity.PromotionNotice;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.baseline.BaselineHelper;

import com.catl.bom.workflow.BomWfUtil;
import com.catl.common.constant.AttributeName;
import com.catl.common.constant.TypeName;
import com.catl.part.workflow.WfUtil;
import com.catl.promotion.util.PromotionUtil;
import com.catl.promotion.util.WorkflowUtil;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.lwc.server.PersistableAdapter;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.core.meta.server.TypeIdentifierUtility;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.windchill.enterprise.maturity.forms.processors.CreatePromotionRequestFormProcessor;


public class CatlCreatePromotionRequestProcess extends CreatePromotionRequestFormProcessor
{

    public CatlCreatePromotionRequestProcess() {
    
    }

    public FormResult postProcess(NmCommandBean nmcommandbean, List<ObjectBean> list) throws WTException {
    	FormResult formresult = super.postProcess(nmcommandbean, list);
        PromotionNotice pn= (PromotionNotice) list.get(0).getObject();
        
        log.debug("pn numner="+pn.getNumber());
        String pntype= "";
		TypeIdentifier ti = TypeIdentifierUtility.getTypeIdentifier(pn);
		pntype = ti.getTypename();
		
		//part promotion notice
		if (pntype.endsWith(TypeName.partPromotion)) {
	        StringBuffer builder=WfUtil.checkReviewObjecs(pn);
	        if(builder.length()>1) {
	         throw new WTException("错误信息："+builder);
	        } 	
		}
		
		//bom AutoCAD CATIA promotion notice
		if (pntype.endsWith(TypeName.bomPromotion)) {
	        StringBuilder builder=new StringBuilder();
			try {
				builder = BomWfUtil.checkReviewObjects(pn, false);
				updateChangeTaskNo(nmcommandbean, pn);
				
			} catch (Exception e) {
				builder.append(e.getMessage());
				e.printStackTrace();
			}
	        if(builder.length()>1) {
	        	throw new WTException("错误信息："+builder);
	        } 
		}
		
		String workflowName = "";
		//Disabled For DesignPN notice
		if (pntype.endsWith(TypeName.designPromotion)) {
			StringBuffer builder=new StringBuffer();
			try {
				builder = PromotionUtil.checkReviewObjecs(pn);
			} catch (Exception e) {
				e.printStackTrace();
			}
	        if(builder.length()>1) {
	         throw new WTException(builder.toString());
	        }
	        
	        workflowName = "CATL_物料设计禁用单流程";	        
		}
		
		//FAE Materials Maturity PN notice
		if (pntype.endsWith(TypeName.UpgradeFaePartMaturityPN)) {
			
			StringBuffer builder=new StringBuffer();
			try {
				builder = PromotionUtil.checkFAERelatedObjecs(pn);
			} catch (Exception e) {
				e.printStackTrace();
			}
	        if(builder.length()>1) {
	         throw new WTException(builder.toString());
	        }
		}
		
		//FAE Materials Maturity PN notice
		if (pntype.endsWith(TypeName.SourceChangePN)) {
			
			StringBuffer builder=new StringBuffer();
			try {
				builder = PromotionUtil.checkSourceChangeObjecs(pn);
			} catch (Exception e) {
				e.printStackTrace();
			}
	        if(builder.length()>1) {
	         throw new WTException(builder.toString());
	        }
		}
		
		if (pntype.endsWith(TypeName.PlatformChangePN)) {
			StringBuffer builder=new StringBuffer();
			try {
				builder = PromotionUtil.checkPlatformChangeObjecs(pn);
			} catch (Exception e) {
				e.printStackTrace();
			}
	        if(builder.length()>1) {
	         throw new WTException(builder.toString());
	        }
		}
		
        AddPromotionTargets(pn);
        
        if (!workflowName.isEmpty()) {
        	startWorkflow(nmcommandbean, list, workflowName);
        }
        
        return formresult;
    }
    /**
     * 更新 ECA/DCA编号
     * @param nmcommandbean
     * @param pn
     * @throws WTException
     */
	private void updateChangeTaskNo(NmCommandBean nmcommandbean,
			PromotionNotice pn) throws WTException {
		Object obj = nmcommandbean.getPageOid().getRefObject();
		if(obj instanceof WTChangeActivity2){
			WTChangeActivity2 ca = (WTChangeActivity2)obj;
			PersistableAdapter genericObj = new PersistableAdapter(pn,null,null,null);
			genericObj.load(AttributeName.CATL_CHANGE_TASK_No);
			genericObj.set(AttributeName.CATL_CHANGE_TASK_No, ca.getNumber());
			Persistable updatedObject = genericObj.apply();
			PersistenceHelper.manager.save(updatedObject);
		}
	}
	
    public static void AddPromotionTargets(PromotionNotice pn) throws WTException {
        
          log.debug(" Enter Add promotable operation");
            MaturityBaseline baseline = pn.getConfiguration();
            WTSet selectedSet = new WTHashSet();
            WTSet newSet = new WTHashSet();
			QueryResult Promotionbaseline = MaturityHelper.service.getBaselineItems(pn);
			QueryResult resulttargets = MaturityHelper.service.getPromotionTargets(pn);
			if(Promotionbaseline.size()!=resulttargets.size())
			{
			while (resulttargets.hasMoreElements()) {
				WTObject targetpart = (WTObject) resulttargets.nextElement();
				//System.out.println("part number===="+targetpart.getNumber());
				selectedSet.add(targetpart);
			     }
				while (Promotionbaseline.hasMoreElements()) {
					WTObject baselineart = (WTObject) Promotionbaseline.nextElement();
					//System.out.println("part number===="+baselineart.getNumber());
					if (!selectedSet.contains(baselineart)) {
						newSet.add(baselineart);
					}
					
				}
			if (newSet.size()>0) {
                try {
                    MaturityHelper.service.savePromotionTargets(pn, newSet);
                } catch (WTPropertyVetoException e) {
                    e.printStackTrace();
                }
                BaselineHelper.service.addToBaseline(newSet, baseline); 	
			}
  
        }
    }
    
    @SuppressWarnings("rawtypes")
	protected FormResult setRefreshInfo(FormResult formresult, NmCommandBean nmcommandbean, List list)
        throws WTException
    {
        return formresult;
    }
    
    protected void startWorkflow(NmCommandBean ncb, List<ObjectBean> objects, String workflowName) throws WTException {	
		PromotionNotice pn = (PromotionNotice)objects.get(0).getObject();
		WorkflowUtil.startWorkFlow(workflowName, pn, null);
	}

    private static final Logger log = LogR.getLogger(CatlCreatePromotionRequestProcess.class.getName());
}
