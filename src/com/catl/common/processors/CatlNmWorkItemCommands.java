package com.catl.common.processors;

import java.util.Enumeration;
import java.util.Locale;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import wt.fc.PersistenceHelper;
import wt.log4j.LogR;
import wt.util.WTException;
import wt.workflow.engine.ProcessData;
import wt.workflow.engine.WfEngineHelper;
import wt.workflow.work.WfAssignedActivity;
import wt.workflow.work.WorkItem;

import com.ptc.core.components.forms.FormResult;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.netmarkets.work.NmWorkItemCommands;
import com.ptc.xworks.xmlobject.workflow.ExtendedWorkitemCommands;

public class CatlNmWorkItemCommands extends NmWorkItemCommands {

    private static final String CLASSNAME = CatlNmWorkItemCommands.class.getName();

    private static Logger logger = LogR.getLogger(CLASSNAME);

    public static FormResult complete(NmCommandBean cb) throws WTException {

        Vector<String> eventList = null;
        String comments = null;
        HttpServletRequest request = cb.getRequest();
        Enumeration parameterNames = request.getParameterNames();

        while (parameterNames.hasMoreElements()) { // loop through all the user's form fields
            String plainKey = (String) parameterNames.nextElement();
            String key = NmCommandBean.convert(plainKey);

            if (key.indexOf(ROUTER_EVENT) >= 0 && key.lastIndexOf("old") == -1) {
                String eventValue = null;
                if (key.indexOf(ROUTER_CHECK) >= 0) {
                    eventValue = key.substring(key.indexOf(ROUTER_CHECK) + NmWorkItemCommands.ROUTER_CHECK.length(), key.lastIndexOf("___"));
                } else {
                    eventValue = cb.getTextParameter(plainKey);
                }

                if (eventList == null) {
                    eventList = new Vector<String>();
                }
                eventList.addElement(eventValue);
            } else if (key.indexOf("___" + COMMENTS + "___") >= 0 && !key.endsWith("___old")) {
                comments = cb.getTextParameter(plainKey);
            }
        }

        if (comments == null)
            comments = "";
        comments = comments.trim();

        NmOid nmOid = cb.getPageOid();
        WorkItem workItem = getWorkItem(nmOid);
        WfAssignedActivity wfActivity = getWfActivity(workItem);

        ProcessData processData = workItem.getContext();
        if (processData == null)
            processData = wfActivity.getContext();

        boolean bNeedComment = false;
        if (processData != null && processData.getValue("needComment") != null) {
            String needComment = (String) processData.getValue("needComment");
            if (needComment != null && needComment.equalsIgnoreCase("true")) {
                bNeedComment = true;
            }
        }

        if (bNeedComment && comments.trim().length() <= 0) {
            throw new WTException("请输入备注!");
        }

        boolean rejected = false;
        if (eventList != null && !eventList.isEmpty()) {
            for (int i = 0; i < eventList.size(); i++) {
                String eventName = (String) eventList.get(i);
                if (eventName.startsWith("驳回") || eventName.startsWith("取消") || eventName.startsWith("不通过")) {
                    if (comments.trim().length() <= 0) {
                        throw new WTException("请输入备注!");
                    }
                }
                if ((eventName.startsWith("驳回"))) {
                	rejected = true;
                }
            }
        }


        Locale locale = cb.getLocale();
        if (locale == null) {
            locale = Locale.CHINA;
        }

        wfActivity = (WfAssignedActivity) PersistenceHelper.manager.refresh(wfActivity);


        //FormResult result = NmWorkItemCommands.complete(cb);
        cb.setActionClass("com.ptc.core.components.forms.DefaultObjectFormProcessor");
        FormResult result = ExtendedWorkitemCommands.complete(cb);
        wfActivity = (WfAssignedActivity) PersistenceHelper.manager.refresh(wfActivity);
/*        if (eventList != null && !eventList.isEmpty()) {
            for (int i = 0; i < eventList.size(); i++) {
                String eventName = (String) eventList.get(i);
                if (eventName.startsWith("驳回") || eventName.startsWith("不通过")) {
                //    WfEngineHelper.service.complete(wfActivity, eventList);
                }
            }
        }*/
        return result;
    }



    protected static WorkItem getWorkItem(NmOid nmOid) throws WTException {
        return (WorkItem) nmOid.getRefObject();
    }

    protected static WfAssignedActivity getWfActivity(WorkItem myWorkItem) {
        return (WfAssignedActivity) myWorkItem.getSource().getObject();
    }

}
