package com.catl.ecad.validator;

import java.util.List;

import org.apache.log4j.Logger;

import wt.clients.beans.query.WT;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.fc.Persistable;
import wt.fc.QueryResult;
import wt.inf.container.WTContainer;
import wt.log4j.LogR;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.vc.wip.WorkInProgressHelper;
import wt.workflow.engine.WfProcess;
import wt.workflow.engine.WfState;

import com.catl.ecad.bean.CadenceAttributeBean;
import com.catl.ecad.dbs.CadenceXmlObjectUtil;
import com.catl.ecad.utils.ECADConst;
import com.catl.ecad.utils.ECADutil;
import com.catl.ecad.utils.WorkflowHelper;
import com.ptc.core.ui.validation.DefaultSimpleValidationFilter;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationStatus;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.windchill.enterprise.workflow.WorkflowCommands;

public class StartWFFilter extends DefaultSimpleValidationFilter {

    private static final String CLASSNAME = StartWFFilter.class.getName();

    private static final Logger log = LogR.getLogger(CLASSNAME);

    public UIValidationStatus preValidateAction(UIValidationKey uivalidationkey,
            UIValidationCriteria uivalidationcriteria) {

    	uivalidationkey.getComponentID();
        UIValidationStatus uiValidationStatus = UIValidationStatus.ENABLED;

        try {
            Object obj  = uivalidationcriteria.getContextObject().getObject();
            WTUser user = (WTUser) SessionHelper.manager.getPrincipal();
            if(obj instanceof WTPart){
            	WTPart wtpart=(WTPart) obj;
            	 if (WorkInProgressHelper.isCheckedOut(wtpart) || !wtpart.isLatestIteration()) {
            		 uiValidationStatus = UIValidationStatus.HIDDEN;
                 }
            	 if(!isECAD(wtpart.getContainer(), user)&&!isHardwareEngineer(wtpart.getContainer(), user)){
              		uiValidationStatus = UIValidationStatus.HIDDEN;
                 }
            	 if(!terminateWorkProcess(wtpart)){
            		 uiValidationStatus = UIValidationStatus.HIDDEN;
            	 }
            	 if(!ECADutil.isElectronicPart(wtpart)){
            		 uiValidationStatus = UIValidationStatus.HIDDEN;
            	 }
            }else if(obj instanceof EPMDocument){
            	EPMDocument epm=(EPMDocument) obj;
            	if(!terminateWorkProcess(epm)||!ECADutil.isSCHEPM(epm)){
            		 uiValidationStatus = UIValidationStatus.HIDDEN;
            	}
            	if(!isECAD(epm.getContainer(), user)&&!isHardwareEngineer(epm.getContainer(), user)){
            		 uiValidationStatus = UIValidationStatus.HIDDEN;
            	}
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.debug("CISFilter.ValidationStatus：" + uiValidationStatus);
        return uiValidationStatus;
    }
    
    public static boolean isECAD(WTContainer container, WTUser user) throws WTException{
    	List<WTUser> users = WorkflowHelper.getRoleUsers(container, ECADConst.ECAD);
    	if(users.contains(user)){
    		return true;
    	}
    	
    	return false;
    	
    }
    
    public static boolean isHardwareEngineer(WTContainer container, WTUser user) throws WTException{
    	List<WTUser> users = WorkflowHelper.getRoleUsers(container, ECADConst.HARDWAREENGINEER);
    	if(users.contains(user)){
    		return true;
    	}
    	
    	return false;
    	
    }
    
    /**
     * 检验是否存在流程进程
     * @param npart
     * @throws WTException
     */
    public static Boolean terminateWorkProcess(Persistable object) throws WTException {
    	boolean flag=true;
    	if(object instanceof WTPart){
    		WTPart part=(WTPart) object;
    		QueryResult qr = WorkflowCommands.getRoutingHistory(new NmOid(part), false);
    		if (qr.hasMoreElements()) {
    			WfProcess process = (WfProcess) qr.nextElement();
    			if (process.getState().equals(WfState.OPEN_RUNNING)) {
    				flag=false;
    			}
    		}
    	}else if(object instanceof EPMDocument){
    		EPMDocument epm=(EPMDocument) object;
    		QueryResult qr = WorkflowCommands.getRoutingHistory(new NmOid(epm), false);
    		if (qr.hasMoreElements()) {
    			WfProcess process = (WfProcess) qr.nextElement();
    			if (process.getState().equals(WfState.OPEN_RUNNING)) {
    				flag=false;
    			}
    		}
    	}else if(object instanceof WTDocument) {
    		WTDocument doc=(WTDocument) object;
    		QueryResult qr = WorkflowCommands.getRoutingHistory(new NmOid(doc), false);
    		if (qr.hasMoreElements()) {
    			WfProcess process = (WfProcess) qr.nextElement();
    			if (process.getState().equals(WfState.OPEN_RUNNING)) {
    				flag=false;
    			}
    		}
    	}
    	return flag;
	}
}