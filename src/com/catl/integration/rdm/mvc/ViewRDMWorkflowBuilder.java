package com.catl.integration.rdm.mvc;

import java.util.List;

import wt.util.WTException;


import com.catl.integration.rdm.RdmIntegrationHelper;
import com.catl.integration.rdm.bean.RDMWorkflowBean;
import com.catl.integration.rdm.resource.CatlRDMResource;
import com.ptc.mvc.components.AbstractComponentBuilder;
import com.ptc.mvc.components.ColumnConfig;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentConfigFactory;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.TableConfig;
import com.ptc.mvc.util.ClientMessageSource;

/**
 * 
 */
@ComponentBuilder("rdm.workflow.list")
public class ViewRDMWorkflowBuilder extends AbstractComponentBuilder {
	private static final String RESOURCE = "com.catl.integration.rdm.resource.CatlRDMResource";
    public final static String ID = "ReportTableDataUtility";
	@Override
	public Object buildComponentData(ComponentConfig arg0,
			ComponentParams params) throws Exception {
		List<RDMWorkflowBean> list = RdmIntegrationHelper.getRDMWorkflows();
		
		return list;
	}

	@Override
	public ComponentConfig buildComponentConfig(ComponentParams arg0)
			throws WTException {
		ClientMessageSource messageSource = getMessageSource(RESOURCE);

		ComponentConfigFactory confgFactory = getComponentConfigFactory();
		TableConfig table = confgFactory.newTableConfig();
		table.setLabel(messageSource.getMessage(CatlRDMResource.TABLE_RDMWF_TITLE));
		table.setSelectable(false);

		ColumnConfig number = confgFactory.newColumnConfig(
				RDMWorkflowBean.COLUMN_ID_NAME, true);
		number.setLabel(messageSource
				.getMessage(CatlRDMResource.TABLE_RDMTASKNAME_LABEL));
		number.setDataUtilityId(ID);
		table.addComponent(number);

		ColumnConfig project = confgFactory.newColumnConfig(
				RDMWorkflowBean.COLUNM_ID_PROJECT, true);
		project.setLabel(messageSource.getMessage(CatlRDMResource.TABLE_RDMWFPROJECT_LABEL));
		table.addComponent(project);
		
		ColumnConfig typeName = confgFactory.newColumnConfig(
				RDMWorkflowBean.COLUMN_ID_TYPENAME, true);
		typeName.setLabel(messageSource.getMessage(CatlRDMResource.TABLE_RDMWFTYPENAME_LABEL));
		table.addComponent(typeName);
		
		ColumnConfig state = confgFactory.newColumnConfig(
				RDMWorkflowBean.COLUMN_ID_STATE, true);
		state.setLabel(messageSource.getMessage(CatlRDMResource.TABLE_RDMWFSTATE_LABEL));
		table.addComponent(state);
		
		ColumnConfig code = confgFactory.newColumnConfig(
				RDMWorkflowBean.COLUNM_ID_CODE, true);
		code.setLabel(messageSource.getMessage(CatlRDMResource.TABLE_RDMWFCODE_LABEL));
		table.addComponent(code);
		
		ColumnConfig department = confgFactory.newColumnConfig(
				RDMWorkflowBean.COLUNM_ID_DEPARTMENT, true);
		department.setLabel(messageSource.getMessage(CatlRDMResource.TABLE_RDMWFDEP_LABEL));
		table.addComponent(department);
		
		return table;
	}

}
