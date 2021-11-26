package com.catl.promotion.processor;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import wt.fc.QueryResult;
import wt.fc.collections.WTHashSet;
import wt.fc.collections.WTSet;
import wt.log4j.LogR;
import wt.maturity.MaturityBaseline;
import wt.maturity.MaturityHelper;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.part.WTPartMaster;
import wt.part.WTPartUsageLink;
import wt.util.WTException;
import wt.vc.baseline.BaselineHelper;


import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormResult;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.netmarkets.util.misc.NmContext;


import com.catl.bom.workflow.BomWfUtil;

/**
 * Remove Promotable
 * 
 * 
 * 
 */
public class RemovePromotableFormProcessor extends DefaultObjectFormProcessor {
    private static final String CLASSNAME = RemovePromotableFormProcessor.class.getName();

    private static final Logger log = LogR.getLogger(CLASSNAME);

    @Override
    public FormResult doOperation(NmCommandBean clientData, List<ObjectBean> objectBeans) 
            throws WTException {
           log.trace("Enter Remove Promotable ");
        FormResult formResult = super.doOperation(clientData, objectBeans);

            Object obj = (Object) clientData.getPrimaryOid().getWtRef().getObject();
        
            ArrayList<NmContext> addItemOids = clientData.getSelected();
       
            wt.maturity.PromotionNotice promotion = (wt.maturity.PromotionNotice) obj;
            MaturityBaseline baseline = promotion.getConfiguration();
            WTSet selectedSet = new WTHashSet();

             if (addItemOids != null && addItemOids.size() > 0) {
                        for (int i = 0; i < addItemOids.size(); i++) {
                            NmContext ctext = (NmContext) addItemOids.get(i);
                            obj = ctext.getTargetOid().getWtRef().getObject();
                            selectedSet.add(obj);
                        }
               }
               MaturityHelper.service.deletePromotionTargets(promotion, selectedSet);
               MaturityHelper.service.deletePromotionSeeds(promotion, selectedSet);
               BaselineHelper.service.removeFromBaseline(selectedSet, baseline);
       log.trace("Exit remove promotable");
        return formResult;
    }  
    

}

