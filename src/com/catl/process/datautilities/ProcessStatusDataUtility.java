package com.catl.process.datautilities;

import org.apache.log4j.Logger;

import wt.httpgw.URLFactory;
import wt.log4j.LogR;
import wt.project.Role;
import wt.util.WTException;

import com.catl.process.bean.ProcessStatusBean;
import com.ptc.core.components.descriptor.ModelContext;
import com.ptc.core.components.factory.AbstractDataUtility;
import com.ptc.core.components.rendering.guicomponents.GUIComponentArray;
import com.ptc.core.components.rendering.guicomponents.IconComponent;
import com.ptc.core.components.rendering.guicomponents.TextDisplayComponent;
import com.ptc.netmarkets.model.NmOid;

public class ProcessStatusDataUtility extends AbstractDataUtility {
    private static final String CLASSNAME = ProcessStatusDataUtility.class.getName();

    private static final Logger log = LogR.getLogger(CLASSNAME);
    
    private static final String WORKITEM_COMPLETED = "COMPLETED";
    private static final String WORKITEM_POTENTIAL = "POTENTIAL";

    public Object getDataValue(String component_id, Object datum, ModelContext mc) throws WTException {
        GUIComponentArray gui_array = new GUIComponentArray();

        NmOid nmOid = new NmOid();
        nmOid = mc.getNmCommandBean().getPrimaryOid();
        if (nmOid == null) {
            String strOid = mc.getNmCommandBean().getTextParameter("oid");
            nmOid = NmOid.newNmOid(strOid);
        }
        Object obj = nmOid.getRef();

        ProcessStatusBean pStatusBean = null;

        if (datum instanceof ProcessStatusBean) {
            pStatusBean = (ProcessStatusBean) datum;
        }

        URLFactory url_factory = new URLFactory();

        if (component_id.equals("workState")) {
            IconComponent iconComp = new IconComponent("");
            String imgSrc = "";
            if (pStatusBean.getWorkState().equals(WORKITEM_COMPLETED)) {
                imgSrc = url_factory.getHREF("netmarkets/images/checked.gif", true);
                iconComp.setTooltip("已完成任务");
            }else if (pStatusBean.getWorkState().equals(WORKITEM_POTENTIAL)){
                imgSrc = url_factory.getHREF("netmarkets/images/step_complete.png", true);
                iconComp.setTooltip("正在执行任务");
            }else {
                imgSrc = url_factory.getHREF("netmarkets/images/step_incomplete.png", true);
                iconComp.setTooltip("未完成任务");
            }
            iconComp.setSrc(imgSrc);
            
            gui_array.addGUIComponent(iconComp);
        } else if (component_id.equals("workIcon")) {
            IconComponent iconComp = new IconComponent("");
            String imgSrc = "";
            if (pStatusBean.getWorkState().equals(WORKITEM_COMPLETED)) {
                imgSrc = url_factory.getHREF("netmarkets/images/completedWork.gif", true);
            }else if (pStatusBean.getWorkState().equals(WORKITEM_POTENTIAL)){
                imgSrc = url_factory.getHREF("netmarkets/images/resource_assigned.gif", true);//open_work.gif
            }else {
                imgSrc = url_factory.getHREF("netmarkets/images/activity.png", true);
            }
            iconComp.setSrc(imgSrc);
            gui_array.addGUIComponent(iconComp);
        } else if (component_id.equals("activityName")) {
            String activityName = pStatusBean.getActivityName(); 
            TextDisplayComponent gui = getTextDisplayComponent(activityName, component_id);
            gui_array.addGUIComponent(gui);
        }else if (component_id.equals("assignee")) {
            String assignee = pStatusBean.getAssignee();
            TextDisplayComponent gui = getTextDisplayComponent(assignee, component_id);
            gui_array.addGUIComponent(gui);
        }else if (component_id.equals("roleName")) {
            String roleName = pStatusBean.getRoleName();
            
            if(roleName != null && !roleName.trim().isEmpty()){
        	try{
        			roleName = Role.toRole(roleName).getDisplay();
        		}catch(Exception e){
        			e.printStackTrace();
        		}
            }
            TextDisplayComponent gui = getTextDisplayComponent(roleName, component_id);
            gui_array.addGUIComponent(gui);
        }else if (component_id.equals("workVote")) {
            String workVote =pStatusBean.getWorkVote();
            TextDisplayComponent gui = getTextDisplayComponent(workVote, component_id);
            gui_array.addGUIComponent(gui);
        }else if (component_id.equals("workComments")) {
            String workComments = pStatusBean.getWorkComments();
            TextDisplayComponent gui = getTextDisplayComponent(workComments, component_id);
            gui_array.addGUIComponent(gui);
        }else if (component_id.equals("completedBy")) {
            String completedBy = pStatusBean.getCompletedBy();
            TextDisplayComponent gui = getTextDisplayComponent(completedBy, component_id);
            gui_array.addGUIComponent(gui);
        }else if (component_id.equals("deadline")) {
            String deadline = pStatusBean.getDeadline();
            TextDisplayComponent gui = getTextDisplayComponent(deadline, component_id);
            gui_array.addGUIComponent(gui);
        }else if (component_id.equals("completedDate")) {
            String completedDate=pStatusBean.getCompletedDate();
            TextDisplayComponent gui = getTextDisplayComponent(completedDate, component_id);
            gui_array.addGUIComponent(gui);
        }

        return gui_array;
    }

    private TextDisplayComponent getTextDisplayComponent(String strValue, String strKey) {
        TextDisplayComponent gui = new TextDisplayComponent("");

        gui.setId(strKey);
        gui.setName(strKey);
        gui.setValue(strValue);
        gui.setTruncationLength(80);

        return gui;
    }

}
