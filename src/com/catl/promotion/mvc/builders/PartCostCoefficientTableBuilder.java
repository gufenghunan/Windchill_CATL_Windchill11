package com.catl.promotion.mvc.builders;

import java.util.ArrayList;
import java.util.List;

import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.workflow.work.WorkItem;

import com.catl.promotion.dbs.SourceChangeXmlObjectUtil;
import com.catl.promotion.resource.promotionResource;
import com.catl.promotion.util.PromotionConst;
import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentConfigFactory;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.TableConfig;
import com.ptc.mvc.util.ClientMessageSource;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.xworks.xmlobject.XmlObject;
import com.ptc.xworks.xmlobject.XmlObjectIdentifier;
import com.ptc.xworks.xmlobject.web.AbstractXmlObjectTableBuilder;
import com.ptc.xworks.xmlobject.web.ObjectGuiComponentBuildContext;

@ComponentBuilder(value = { "com.catl.promotion.mvc.builders.PartCostCoefficientTableBuilder" })
public class PartCostCoefficientTableBuilder extends AbstractXmlObjectTableBuilder{

	private final ClientMessageSource messageSource = getMessageSource("com.catl.promotion.resource.promotionResource");
	
	@Override
	public List<XmlObject> fetchXmlObjects(ComponentConfig config,
			ComponentParams params, ObjectGuiComponentBuildContext buildContext) throws Exception {
		List<XmlObject> result = new ArrayList<XmlObject>();
		boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
		try{

		String applicationFormOid = (String) params.getParameter("applicationFormOid");
		result = getXmlObjectStoreManager().navigate(
				new XmlObjectIdentifier(applicationFormOid), SourceChangeXmlObjectUtil.SourceChangeXmlObjectBean);
		
		}finally{
			SessionServerHelper.manager.setAccessEnforced(flag);
		}
		return result;
	}

	@Override
	public TableConfig buildTableConfig(ComponentParams params, ObjectGuiComponentBuildContext buildContext) throws Exception {
		JcaComponentParams jcaParams = (JcaComponentParams) params;
		jcaParams.getHelperBean().getNmCommandBean().addRequestDataParam("operation", "CREATE", true);
		
		NmCommandBean clientData = jcaParams.getHelperBean().getNmCommandBean();
		Object obj=null;
		if(clientData.getPageOid()!=null) {
			obj = clientData.getPageOid().getRefObject();
		} 
		WorkItem currentWorkItem = null;
		boolean isWorkItemOwner = true;
		if (obj instanceof WorkItem) {
			currentWorkItem = (WorkItem) obj;
			isWorkItemOwner = currentWorkItem.getOwnership().getOwner().getName().equals(SessionHelper.getPrincipal().getName());
		}
		
		ComponentConfigFactory factory = getComponentConfigFactory();
		TableConfig tableConfig = factory.newTableConfig();
		String nodeid = (String) params.getParameter("nodeid");
		String operationType = (String) params.getParameter("operationType");
		String title = messageSource.getMessage(promotionResource.SC_CONSTANT_16);;
		tableConfig.setLabel(title);
		if(PromotionConst.source_change_submit.equals(nodeid) && "EDIT".equals(operationType) && isWorkItemOwner) {
			tableConfig.setActionModel("sourceChange_toolbar");
			tableConfig.setSelectable(true);
		}
		/*if (PromotionConst.source_change_confirm.equals(nodeid) && "EDIT".equals(operationType)) {
			tableConfig.setActionModel("SourceChange_toolbar");//确认设计禁用时，不允许用户增加或移除部件
			tableConfig.setSelectable(true);
		}*/
		//tableConfig.setConfigurable(false);
		//tableConfig.setShowCount(true);
		//tableConfig.setShowCustomViewLink(true);
		
		tableConfig.addComponent(buildColumnConfig(buildContext, "partNumber"));
		tableConfig.addComponent(buildColumnConfig(buildContext, "partName"));
		tableConfig.addComponent(buildColumnConfig(buildContext, "partCreator"));
		tableConfig.addComponent(buildColumnConfig(buildContext, "partModifier"));
		tableConfig.addComponent(buildColumnConfig(buildContext, "sourceBefore"));
		tableConfig.addComponent(buildColumnConfig(buildContext, "sourceAfter"));
		tableConfig.addComponent(buildColumnConfig(buildContext, "cause"));
		
		return tableConfig;
	}
}
