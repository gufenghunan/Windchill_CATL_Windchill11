package com.catl.integration.rdm.mvc;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import wt.util.WTException;


import com.catl.integration.rdm.RdmIntegrationHelper;
import com.catl.integration.rdm.bean.RDMTaskBean;
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
@ComponentBuilder("rdm.assignments.list")
public class ViewRDMTaskBuilder extends AbstractComponentBuilder {
	private static final String RESOURCE = "com.catl.integration.rdm.resource.CatlRDMResource";
    public final static String ID = "ReportTableDataUtility";
	@Override
    public Object buildComponentData(ComponentConfig arg0, ComponentParams params) throws Exception {
        List<RDMTaskBean> list = RdmIntegrationHelper.getRDMToDoTasks();
        if (list != null && list.size() > 0) {
            sortRDMTaskBeans(list);
            sortRDMTaskBeansByTime(list);
        }
        return list;
    }

	@Override
	public ComponentConfig buildComponentConfig(ComponentParams arg0)
			throws WTException {
		ClientMessageSource messageSource = getMessageSource(RESOURCE);

		ComponentConfigFactory confgFactory = getComponentConfigFactory();
		TableConfig table = confgFactory.newTableConfig();
		table.setLabel(messageSource.getMessage(CatlRDMResource.TABLE_RDMTASK_TITLE));
		table.setSelectable(false);

		ColumnConfig number = confgFactory.newColumnConfig(
				RDMTaskBean.COLUMN_ID_NAME, true);
		number.setLabel(messageSource
				.getMessage(CatlRDMResource.TABLE_RDMTASKNAME_LABEL));
		number.setDataUtilityId(ID);
		table.addComponent(number);

        ColumnConfig object = confgFactory.newColumnConfig(RDMTaskBean.COLUNM_ID_OBJECT, true);
        object.setLabel(messageSource.getMessage(CatlRDMResource.TABLE_RDMTASKPROJECT_LABEL));
        table.addComponent(object);
              
		ColumnConfig state = confgFactory.newColumnConfig(
				RDMTaskBean.COLUMN_ID_STATE, true);
		state.setLabel(messageSource.getMessage(CatlRDMResource.TABLE_RDMTASKSTATE_LABEL));
		table.addComponent(state);
		
		ColumnConfig planStartDate = confgFactory.newColumnConfig(
				RDMTaskBean.COLUMN_ID_SDATE, true);
		planStartDate.setLabel(messageSource.getMessage(CatlRDMResource.TABLE_RDMTASKPLANSDATE_LABEL));
		table.addComponent(planStartDate);
		
		ColumnConfig planEndDate = confgFactory.newColumnConfig(
				RDMTaskBean.COLUNM_ID_EDATE, true);
		planEndDate.setLabel(messageSource.getMessage(CatlRDMResource.TABLE_RDMTASKPLANEDATE_LABEL));
		table.addComponent(planEndDate);
		
		return table;
	}

    @SuppressWarnings("unchecked")
    public static void sortRDMTaskBeans(List<RDMTaskBean> list) {
        Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
                RDMTaskBean b1 = (RDMTaskBean) o1;
                RDMTaskBean b2 = (RDMTaskBean) o2;

                String t1 = b1.getObject();
                String t2 = b2.getObject();
                if (t1.compareTo(t2) > 0) {
                    return 1;
                } else if (t1.compareTo(t2) < 0) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });
    }
    
    @SuppressWarnings("unchecked")
    public static void sortRDMTaskBeansByTime(List<RDMTaskBean> list) {
        Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
                RDMTaskBean b1 = (RDMTaskBean) o1;
                RDMTaskBean b2 = (RDMTaskBean) o2;

                String t1 = b1.getPlanStartDate();
                String t2 = b2.getPlanStartDate();
                if (t1.compareTo(t2) > 0) {
                    return 1;
                } else if (t1.compareTo(t2) < 0) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });
    }
}
