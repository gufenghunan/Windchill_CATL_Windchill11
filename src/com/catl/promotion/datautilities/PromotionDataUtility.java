package com.catl.promotion.datautilities;

import org.apache.commons.lang.StringUtils;

import wt.change2.WTChangeActivity2;
import wt.maturity.PromotionNotice;
import wt.util.WTException;

import com.catl.change.ChangeUtil;
import com.catl.common.constant.AttributeName;
import com.catl.common.util.GenericUtil;
import com.ptc.core.components.descriptor.ModelContext;
import com.ptc.core.components.factory.dataUtilities.DefaultDataUtility;
import com.ptc.core.components.rendering.guicomponents.UrlDisplayComponent;
import com.ptc.core.components.util.OidHelper;
import com.ptc.core.ui.resources.ComponentMode;
import com.ptc.netmarkets.util.beans.NmURLFactoryBean;
import com.ptc.netmarkets.util.misc.NetmarketURL;

public class PromotionDataUtility extends DefaultDataUtility {

	@Override
	public Object getDataValue(String componentId, Object obj, ModelContext modelContext)
			throws WTException {
		if(modelContext.getDescriptorMode().equals(ComponentMode.VIEW)){
			if(obj instanceof PromotionNotice){
				PromotionNotice pn = (PromotionNotice)obj;
				if(StringUtils.equals(componentId, AttributeName.CATL_CHANGE_TASK_No)){
					
					String caNumber = (String)GenericUtil.getObjectAttributeValue(pn, AttributeName.CATL_CHANGE_TASK_No);
					if(caNumber != null){
						WTChangeActivity2 ca = ChangeUtil.getCAByNumber(caNumber);
						
						NmURLFactoryBean urlFactoryBean = new NmURLFactoryBean();
						urlFactoryBean.setRequestURI(NetmarketURL.BASEURL);
						String url = NetmarketURL.buildURL(urlFactoryBean, "object", "view", OidHelper.getNmOid(ca));
						
						String lable = ca.getNumber()+","+ca.getName();
						UrlDisplayComponent objUrlComp = new UrlDisplayComponent();
			            objUrlComp.setLabel(lable);
			            objUrlComp.setLabelForTheLink(lable);
			            objUrlComp.setLink(url);
			            //objUrlComp.setTarget("_blank");
			            return objUrlComp;
					}
				}
			}
		}
		return super.getDataValue(componentId, obj, modelContext);
	}

}
