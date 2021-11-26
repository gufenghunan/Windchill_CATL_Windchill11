
package com.catl.process.mvc.builder;

import java.util.ArrayList;
import java.util.List;

import wt.lifecycle.lifecycleResource;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.workflow.engine.WfProcess;
import wt.workflow.work.WfAssignedActivity;
import wt.workflow.work.WorkItem;

import com.catl.process.bean.ProcessStatusBean;
import com.catl.process.util.WFReviewList;
import com.ptc.core.components.descriptor.DescriptorConstants;
import com.ptc.core.ui.resources.ComponentMode;
import com.ptc.jca.mvc.components.AbstractJcaComponentConfig;
import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.jca.mvc.components.JcaTableConfig;
import com.ptc.mvc.components.AbstractComponentBuilder;
import com.ptc.mvc.components.ColumnConfig;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentConfigFactory;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.TableConfig;
import com.ptc.mvc.util.ClientMessageSource;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.netmarkets.util.beans.NmHelperBean;
import com.ptc.netmarkets.workflow.workflowResource;

@ComponentBuilder("com.catl.process.mvc.builder.ProcessStatusTableBuilder")
public class ProcessStatusTableBuilder extends AbstractComponentBuilder {
    private static final String LIFE_CYCLE_RESOURCE = "wt.lifecycle.lifecycleResource";

    private static final String WORK_FLOW_RESOURCE = "com.ptc.netmarkets.workflow.workflowResource";

    ClientMessageSource messageLifeCycleSource = getMessageSource(LIFE_CYCLE_RESOURCE);

    ClientMessageSource messageWorkFlowSource = getMessageSource(WORK_FLOW_RESOURCE);

    private static final org.apache.log4j.Logger log = wt.log4j.LogR.getLogger(ProcessStatusTableBuilder.class.getName());

    @Override
    public Object buildComponentData(ComponentConfig config, ComponentParams params) throws WTException {

        NmHelperBean helper = ((JcaComponentParams) params).getHelperBean();
        NmCommandBean commandBean = helper.getNmCommandBean();
        List<Object> result = new ArrayList<Object>();

            WorkItem workItem = (WorkItem) params.getContextObject();
            WfAssignedActivity activity = (WfAssignedActivity) workItem.getSource().getObject();
            WfProcess wfProcess = activity.getParentProcess();
           List<ProcessStatusBean> userCommentList = WFReviewList.getProcessInfo(wfProcess);
            log.debug("userCommentList.size()===>>" + userCommentList.size());
            for (int i = 0; i < userCommentList.size(); i++) {
                ProcessStatusBean pStatusBean = userCommentList.get(i);
                result.add(pStatusBean);
            }
       
       
        return result;
    }

    @Override
    public ComponentConfig buildComponentConfig(ComponentParams params) throws WTException {
        ComponentConfigFactory factory = getComponentConfigFactory();
        TableConfig table = (JcaTableConfig) factory.newTableConfig();
        
        //Fix for - Defect: Table rows are getting overlapped in X20 UI
        //((AbstractJcaComponentConfig) table).setDescriptorProperty(DescriptorConstants.TableProperties.VARIABLE_ROW_HEIGHTS, true);

        table.setLabel("审核列表");
        //table.setHelpContext("processStatusHelp");
        table.setShowCount(true);
        table.setId("com.catl.mvc.builder.ProcessStatusTableBuilder");


        ColumnConfig colWorkState = factory.newColumnConfig("workState", false);
        colWorkState.setComponentMode(ComponentMode.VIEW);
        colWorkState.setDataUtilityId("ProcessStatusDataUtility");
        colWorkState.setLabel("");
        table.addComponent(colWorkState);

        ColumnConfig colWorkIcon = factory.newColumnConfig("workIcon", false);
        colWorkIcon.setComponentMode(ComponentMode.VIEW);
        colWorkIcon.setDataUtilityId("ProcessStatusDataUtility");
        colWorkIcon.setLabel("");
        table.addComponent(colWorkIcon);

        ColumnConfig colActivityName = factory.newColumnConfig("activityName", false);
        colActivityName.setComponentMode(ComponentMode.VIEW);
        colActivityName.setLabel(messageWorkFlowSource.getMessage(workflowResource.ACTIVITY_NAME));
        colActivityName.setDataUtilityId("ProcessStatusDataUtility");
        table.addComponent(colActivityName);

        ColumnConfig colAssignee = factory.newColumnConfig("assignee", false);
        colAssignee.setComponentMode(ComponentMode.VIEW);
        colAssignee.setLabel(messageWorkFlowSource.getMessage(workflowResource.ASSIGNEE_NAME));
        colAssignee.setDataUtilityId("ProcessStatusDataUtility");
        table.addComponent(colAssignee);

        ColumnConfig colRoleName = factory.newColumnConfig("roleName", false);
        colRoleName.setComponentMode(ComponentMode.VIEW);
        colRoleName.setLabel(messageWorkFlowSource.getMessage(workflowResource.USER_ROLE));
        colRoleName.setDataUtilityId("ProcessStatusDataUtility");
        table.addComponent(colRoleName);

        ColumnConfig colWorkVote = factory.newColumnConfig("workVote", false);
        colWorkVote.setComponentMode(ComponentMode.VIEW);
        colWorkVote.setLabel(messageWorkFlowSource.getMessage(workflowResource.VOTE));
        colWorkVote.setDataUtilityId("ProcessStatusDataUtility");
        table.addComponent(colWorkVote);

        ColumnConfig colWorkComments = factory.newColumnConfig("workComments", false);
        colWorkComments.setComponentMode(ComponentMode.VIEW);
        colWorkComments.setLabel(messageLifeCycleSource.getMessage(lifecycleResource.COMMENTS_COLUMN_LABEL));
        colWorkComments.setDataUtilityId("ProcessStatusDataUtility");
        table.addComponent(colWorkComments);
        
        ColumnConfig colCompletedBy = factory.newColumnConfig("completedBy", false);
        colCompletedBy.setComponentMode(ComponentMode.VIEW);
        colCompletedBy.setLabel(messageWorkFlowSource.getMessage(workflowResource.COMPLETED_BY));
        colCompletedBy.setDataUtilityId("ProcessStatusDataUtility");
        table.addComponent(colCompletedBy);

        ColumnConfig colDeadline = factory.newColumnConfig("deadline", false);
        colDeadline.setComponentMode(ComponentMode.VIEW);
        colDeadline.setLabel(messageWorkFlowSource.getMessage(workflowResource.DEADLINE));
        colDeadline.setDataUtilityId("ProcessStatusDataUtility");
        table.addComponent(colDeadline);

        ColumnConfig colCompletedDate = factory.newColumnConfig("completedDate", false);
        colCompletedDate.setComponentMode(ComponentMode.VIEW);
        colCompletedDate.setLabel(messageWorkFlowSource.getMessage(workflowResource.DATE_COMPLETED));
        colCompletedDate.setDataUtilityId("ProcessStatusDataUtility");
        //colCompletedDate.setDefaultSort(true);
        table.addComponent(colCompletedDate);
       
        return table;
    }

}
