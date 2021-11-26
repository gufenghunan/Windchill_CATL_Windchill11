package com.catl.change.mvc;

import wt.change2.WTChangeActivity2;
import wt.change2.WTChangeOrder2;
import wt.change2.WTChangeRequest2;
import wt.maturity.PromotionNotice;
import wt.org.WTPrincipal;
import wt.session.SessionHelper;
import wt.util.WTException;

import com.catl.common.util.UserUtil;
import com.ptc.core.components.descriptor.ComponentDescriptor;
import com.ptc.core.htmlcomp.tableview.TableViewDescriptor;
import com.ptc.core.htmlcomp.tableview.TableViewDescriptorHelper;
import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.OverrideComponentBuilder;
import com.ptc.mvc.components.TableConfig;
import com.ptc.netmarkets.util.beans.NmHelperBean;
import com.ptc.windchill.enterprise.team.mvc.builders.CommonTeamTableBuilder;

@OverrideComponentBuilder
@ComponentBuilder("enterpriseUI.team.commonTeam")
public class CATLCommonTeamTableBuilder extends CommonTeamTableBuilder {
	
	@Override
	public ComponentConfig buildComponentConfig(ComponentParams params)
			throws WTException {
		TableConfig tableConfig = (TableConfig) super.buildComponentConfig(params);
		Object obj = params.getContextObject();
		if(isUpdate(obj)){
			tableConfig.setSelectable(false);
			tableConfig.setActionModel(null);
			NmHelperBean helper = ((JcaComponentParams) params).getHelperBean();
			helper.getRequest().setAttribute(CLASS_CONTEXT + "treeActionModel", "");
		}
		
		return tableConfig;
	}
	
	@Override
	public Object buildComponentData(ComponentConfig config,
			ComponentParams params) throws WTException {
		Object obj = super.buildComponentData(config, params);
		ComponentDescriptor descriptor =((JcaComponentParams)params).getDescriptor();
		removeComponet(descriptor.getId(), params.getContextObject());
		return obj;
	}

	private void removeComponet(String tableId,Object object) throws WTException {
        TableViewDescriptor coreHtmlView = TableViewDescriptorHelper.getCurrentActiveView(
                tableId, SessionHelper.getLocale());
        if(isUpdate(object)){
        	coreHtmlView.getTableColumnDefinition().remove(coreHtmlView.getColumnById("adminIconAction"));
        }
    }

	private boolean isUpdate(Object obj){
		try {
			WTPrincipal principal = SessionHelper.getPrincipal();
			if(UserUtil.isOrgAdministator(principal) || UserUtil.isSiteAdmin(principal)){
				return false;
			}
			if(obj instanceof PromotionNotice || obj instanceof WTChangeOrder2 || obj instanceof WTChangeRequest2 || obj instanceof WTChangeActivity2){
				return true;
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return false;
	}
}
