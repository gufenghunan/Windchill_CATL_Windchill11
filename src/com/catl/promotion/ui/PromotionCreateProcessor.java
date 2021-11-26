package com.catl.promotion.ui;

import java.util.ArrayList;
import java.util.List;

import wt.doc.WTDocument;
import wt.fc.ReferenceFactory;
import wt.fc.WTObject;
import wt.inf.container.WTContainerRef;
import wt.maturity.Promotable;
import wt.maturity.PromotionNotice;
import wt.part.WTPart;
import wt.pdmlink.PDMLinkProduct;
import wt.util.WTException;

import com.catl.promotion.PromotionHelper;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.forms.FormResultAction;
import com.ptc.netmarkets.util.beans.NmCommandBean;

public class PromotionCreateProcessor extends DefaultObjectFormProcessor{
	public FormResult doOperation(NmCommandBean clientData,List<ObjectBean> objectBeans) throws WTException {
		FormResult result = new FormResult();
		try {
			String[] promotionNoticeNameTxt = clientData.getTextParameterValues("inputPromotionName");
			String[] promotionNoticeDescTxt = clientData.getTextParameterValues("inputPromotionDesc");
			String[] promotionNoticeItemTxt = clientData.getTextParameterValues("inputPromotionItem");
			
			String promotionNoticeName = "";
			String promotionNoticeDesc = "";
			String promotionNoticeItem = "";
			
			if( promotionNoticeNameTxt != null && promotionNoticeNameTxt.length > 0 ) promotionNoticeName = promotionNoticeNameTxt[0];
			if( promotionNoticeDescTxt != null && promotionNoticeDescTxt.length > 0 ) promotionNoticeDesc = promotionNoticeDescTxt[0];
			if( promotionNoticeItemTxt != null && promotionNoticeItemTxt.length > 0 ) promotionNoticeItem = promotionNoticeItemTxt[0];
			
			WTContainerRef containerRef = null;
			ReferenceFactory rf = new ReferenceFactory();
			WTObject wtobject = (PDMLinkProduct)rf.getReference(promotionNoticeItem).getObject();
			if(wtobject instanceof WTPart){
				containerRef = ((WTPart)wtobject).getContainerReference();
			}else if(wtobject instanceof WTDocument){
				containerRef = ((WTDocument)wtobject).getContainerReference();
			}

//			PromotionNotice promotion = PromotionHelper.newPromotionNotice(promotionNoticeName,promotionNoticeDesc,containerRef);
			PromotionNotice promotion = (PromotionNotice)clientData.getRequest().getSession().getAttribute("PromotionObject");
			System.out.println(promotion);
			ArrayList selectItems = clientData.getNmOidSelected();
			if(selectItems == null) selectItems = new ArrayList();
			
			for(int i=0; i < selectItems.size(); i++){
				String oid = selectItems.get(i).toString();
				oid = oid.replace("NmOid=VR:", "");
				Object object = rf.getReference(oid).getObject();
				if(object instanceof Promotable){
					promotion = PromotionHelper.addPromotable(promotion, (Promotable)object);
				}
			}
			
			result.setStatus(FormProcessingStatus.SUCCESS);
			result.setNextAction(FormResultAction.NONE);
		} catch (Exception e) {
			// TODO: handle exception
			result.setStatus(FormProcessingStatus.FAILURE);
			result.setNextAction(FormResultAction.NONE);
		}
		
		return result;
	}
}
