package com.catl.promotion.processor;

import java.rmi.RemoteException;
import java.util.List;

import org.apache.log4j.Logger;

import wt.fc.ObjectReference;
import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.fc.collections.WTHashSet;
import wt.fc.collections.WTSet;
import wt.log4j.LogR;
import wt.maturity.MaturityHelper;
import wt.maturity.PromotionNotice;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.vc.Iterated;
import wt.vc.VersionControlHelper;


import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormResult;
import com.ptc.netmarkets.util.beans.NmCommandBean;


import com.catl.bom.workflow.BomWfUtil;

/**
 * 刷新升级对象
 * 
 * 
 * 
 */
public class RefreshPromotableFormProcessor extends DefaultObjectFormProcessor {
    private static final String CLASSNAME = RefreshPromotableFormProcessor.class.getName();

    private static final Logger log = LogR.getLogger(CLASSNAME);

    @Override
    public FormResult doOperation(NmCommandBean clientData, List<ObjectBean> objectBeans) throws WTException {
        log.trace("Enter Refresh Promotable doOperation");
        FormResult formResult = super.doOperation(clientData, objectBeans);
        Object obj = (Object) clientData.getPrimaryOid().getWtRef().getObject();
        if (obj instanceof PromotionNotice) {
            PromotionNotice promotion = (PromotionNotice) obj;
            BomWfUtil.refreshPromotableObject(promotion);
        
        }
       log.trace("Exit Refersh Promotable doOperation");
        return formResult;
    }
    
    public static void refreshPromotableObject(WTObject pbo) throws WTException {
        boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
        try {
            if (pbo instanceof PromotionNotice) {
                PromotionNotice promotion = (PromotionNotice) pbo;

                WTSet addSeeds = new WTHashSet();
                WTSet addTargets = new WTHashSet();

                WTSet rmSeeds = new WTHashSet();
                WTSet rmTargets = new WTHashSet();
                
                WTSet removeSeeds = new WTHashSet();
                QueryResult qrS = MaturityHelper.service.getPromotionSeeds(promotion);
                while (qrS.hasMoreElements()) {
                    Object obj = qrS.nextElement();
                    removeSeeds.add(obj);
                }

                WTSet removeTargets = new WTHashSet();
                QueryResult qrT = MaturityHelper.service.getPromotionTargets(promotion);
                while (qrT.hasMoreElements()) {
                    Object obj = qrT.nextElement();
                    removeTargets.add(obj);
                }

                WTPart part = null;
                WTPart newPart = null;
                Object newObj = null;
                for (Object obj : removeTargets) {
                    ObjectReference objRef = (ObjectReference)obj;
                    obj = objRef.getObject();
                    if (obj instanceof WTPart) {
                        part = (WTPart) obj;
                        newPart = BomWfUtil.getPartByMasterAndView((WTPartMaster) part.getMaster(), part.getViewName());
                        if (removeSeeds.contains(part)) {
                            addSeeds.add(newPart);
                        }
                        addTargets.add(newPart);
                    } else if (obj instanceof Iterated){
                        newObj = VersionControlHelper.getLatestIteration((Iterated) obj);
                        if (removeSeeds.contains(obj)) {
                            addSeeds.add(newObj);
                        }
                        addTargets.add(newObj);
                    }
                }
                if (removeSeeds.size() > 0) {
                    rmSeeds.addAll(removeSeeds);
                    MaturityHelper.service.deletePromotionSeeds(promotion, rmSeeds);
                }
                if (removeTargets.size() > 0) {
                    rmTargets.addAll(removeTargets);
                    BomWfUtil.removeTargets(promotion, rmTargets);
                }
                if (addSeeds.size() > 0) {
                    MaturityHelper.service.savePromotionSeeds(promotion, addSeeds);
                }
                if (addTargets.size() > 0) {
                    BomWfUtil.addToTargets(promotion, addTargets);
                }
            }
        } finally {
            SessionServerHelper.manager.setAccessEnforced(enforce);
        }

    }


}
