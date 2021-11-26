package com.catl.promotion.util;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.catl.common.util.ContentUtil;
import com.catl.promotion.resource.promotionResource;
import com.ptc.core.lwc.common.view.TypeDefinitionReadView;
import com.ptc.core.lwc.server.TypeDefinitionServiceHelper;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.core.meta.common.impl.TypeIdentifierUtilityHelper;

import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.fc.Persistable;
import wt.fc.PersistenceServerHelper;
import wt.fc.ReferenceFactory;
import wt.fc.WTObject;
import wt.fc.WTReference;
import wt.folder.Folder;
import wt.inf.container.WTContained;
import wt.inf.container.WTContainerHelper;
import wt.inf.container.WTContainerRef;
import wt.org.DirectoryContextProvider;
import wt.org.OrganizationServicesHelper;
import wt.org.WTOrganization;
import wt.org.WTPrincipalReference;
import wt.project.Role;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.team.Team;
import wt.type.Typed;
import wt.util.WTException;
import wt.util.WTMessage;
import wt.util.WTProperties;
import wt.workflow.definer.WfAssignedActivityTemplate;
import wt.workflow.definer.WfDefinerHelper;
import wt.workflow.definer.WfProcessDefinition;
import wt.workflow.engine.ProcessData;
import wt.workflow.engine.WfEngineHelper;
import wt.workflow.engine.WfProcess;
import wt.workflow.work.WfAssignedActivity;
import wt.workflow.work.WorkItem;

public class WorkflowUtil {

	private static Logger logger = Logger.getLogger(WorkflowUtil.class);
	public static String RESOURCE = "com.catl.promotion.resource.promotionResource";
	
	public static Persistable getObjectByOid(String oid) throws WTException {
		Persistable p = null;
	
		boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
		try {
			ReferenceFactory referencefactory = new ReferenceFactory();
			WTReference wtreference = referencefactory.getReference(oid);
			p = wtreference.getObject();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
		} finally {
			SessionServerHelper.manager.setAccessEnforced(flag);
		}
		
		return p;		
	}
	
	/*
     * Typed 可类型管理的对象，如文档，图纸，部件
     * @return 获得类型的内部名称，如：wt.doc.WTDocument
     */
    public static String getTypeInternalName(Typed typed_object) throws WTException{
    	String name = null;
		try {
			TypeIdentifier type = TypeIdentifierUtilityHelper.service.getTypeIdentifier(typed_object);
			TypeDefinitionReadView trv = TypeDefinitionServiceHelper.service.getTypeDefView(type);
			name = trv.getName();
		} catch (Exception e) {
			e.printStackTrace();
			throw new WTException(e);
		}
		return name;
    }
    
    public static String getActivityTemplateId(Object object) throws WTException {
    	String templateId = null;
    	WfAssignedActivity activity = null;
    	if(object instanceof String) {
    		Persistable p = getObjectByOid(object.toString());
    		if(p instanceof WorkItem) {
    			WorkItem workItem = (WorkItem)p;
    			activity = (WfAssignedActivity)workItem.getSource().getObject();
    		}
    	} else if(object instanceof WorkItem) {
    		WorkItem workItem = (WorkItem)object;
    		activity = (WfAssignedActivity)workItem.getSource().getObject();
    	} else if(object instanceof WfAssignedActivity)
    		activity = (WfAssignedActivity)object;
            if(activity != null) {
            	WfAssignedActivityTemplate template = (WfAssignedActivityTemplate)activity.getTemplate().getObject();
                String desc = template.getDescription();
                templateId = validateTemplateId(desc);
            }
            return templateId;
	}
    
    public static String validateTemplateId(String desc) {
        if(desc == null || "".equals(desc.toString().trim()))
            return null;
        Matcher matcher = TEMPLATE_NODE_ID.matcher(desc);
        if(matcher.find())
            return matcher.group(1);
        else
            return null;
    }
    
    @SuppressWarnings("rawtypes")
	public static Set<Object> validateNeedRoles(WfAssignedActivity wfaa, String validateRoles) throws WTException {
     
        Set<Object> noUsers = new HashSet<Object>();
        WfProcess process = wfaa.getParentProcess();
        WTPrincipalReference creator = process.getCreator();
		Team team = (Team) process.getTeamId().getObject();
        Enumeration roles = process.getRoles();
        Enumeration enumPrin = null;
        while (roles.hasMoreElements()) {
            Role role = (Role) roles.nextElement();
            String roleName = role.toString();
            if(validateRoles.equals(roleName)){
                enumPrin = team.getPrincipalTarget(role);
                while (enumPrin.hasMoreElements()) {
                	Object obj = enumPrin.nextElement();
                	noUsers.add(obj);
                	//if (!obj.equals(creator)) { //不再要求必须不能是流程启动者
                		//noUsers.add(obj);
                	//}                	
                }   
            }
        }
        return noUsers;
    }
    
    public static String joinSetMsg(Collection<String> sets){
		if(sets.size()>0)
			return StringUtils.join(sets, ",");
		else
			return "";
	}
    
	/**
	 * 启动工作流
	 */
    @SuppressWarnings("rawtypes")
	public static WfProcess startWorkFlow(String workFlowName, WTObject pbo, HashMap variables) {
        long WORKFLOW_PRIORITY = 1;
        boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
        try {
            WTContainerRef containerRef = WTContainerHelper.service.getExchangeRef();
            if (pbo instanceof WTContained) {
                WTContained contained = (WTContained) pbo;
                containerRef = contained.getContainerReference();
            }
            WTProperties wtproperties = WTProperties.getLocalProperties();
            WORKFLOW_PRIORITY = Long.parseLong(wtproperties.getProperty("wt.lifecycle.defaultWfProcessPriority", "1"));
            WfProcessDefinition wfprocessdefinition = WfDefinerHelper.service.getProcessDefinition(workFlowName, containerRef);
            if (wfprocessdefinition == null) {
            	logger.debug("Error to getWrokFlowTemplate," + workFlowName + " is null");
            	Locale locale = SessionHelper.getLocale();
            	String msg = WTMessage.getLocalizedMessage(RESOURCE, promotionResource.no_workflow, null, locale);
            	throw new WTException(msg);
            }

            WfProcess wfprocess = WfEngineHelper.service.createProcess(wfprocessdefinition, pbo, containerRef);
            ProcessData processData = wfprocess.getContext();
            processData.setValue(WfDefinerHelper.PRIMARY_BUSINESS_OBJECT, pbo);

            if (variables != null && !variables.isEmpty()) {
                Iterator keys = variables.keySet().iterator();
                while (keys.hasNext()) {
                    String key = (String) keys.next();
                    processData.setValue(key, variables.get(key));
                }
            }

            wfprocess = WfEngineHelper.service.startProcessImmediate(wfprocess, processData, WORKFLOW_PRIORITY);
            return wfprocess;
        } catch (WTException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
        	SessionServerHelper.manager.setAccessEnforced(flag);
        }
        return null;
    }
    
    /**
     * 启动工作流，无PBO
     */
    public static WfProcess startWorkFlowNullPBO(String workFlowName, String OrgName) {
        long WORKFLOW_PRIORITY = 1;
        boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
        try {
        	DirectoryContextProvider dcp = WTContainerHelper.service.getExchangeContainer().getContextProvider();
        	WTOrganization org = OrganizationServicesHelper.manager.getOrganization(OrgName, dcp);
        	if (org != null) {
        		WTContainerRef containerRef = WTContainerHelper.service.getOrgContainerRef(org);
        		WTProperties wtproperties = WTProperties.getLocalProperties();
                WORKFLOW_PRIORITY = Long.parseLong(wtproperties.getProperty("wt.lifecycle.defaultWfProcessPriority", "1"));
                WfProcessDefinition wfprocessdefinition = WfDefinerHelper.service.getProcessDefinition(workFlowName, containerRef);
                if (wfprocessdefinition == null) {
                	logger.debug("Error to getWrokFlowTemplate," + workFlowName + " is null");
                	Locale locale = SessionHelper.getLocale();
                	String msg = WTMessage.getLocalizedMessage(RESOURCE, promotionResource.no_workflow, null, locale);
                	throw new WTException(msg);
                }

                WfProcess wfprocess = WfEngineHelper.service.createProcess(wfprocessdefinition, null, containerRef);
                ProcessData processData = wfprocess.getContext();

                wfprocess = WfEngineHelper.service.startProcessImmediate(wfprocess, processData, WORKFLOW_PRIORITY);
                return wfprocess;
        	}
        	
        } catch (WTException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
        	SessionServerHelper.manager.setAccessEnforced(flag);
        }
        return null;
    }
    
    /**
     * 启动工作流，无PBO
     */
    public static WfProcess startWorkFlowNullPBO(String workFlowName, WTContainerRef containerRef) {
        long WORKFLOW_PRIORITY = 1;
        boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
        try {
        	
        	WTProperties wtproperties = WTProperties.getLocalProperties();
            WORKFLOW_PRIORITY = Long.parseLong(wtproperties.getProperty("wt.lifecycle.defaultWfProcessPriority", "1"));
            WfProcessDefinition wfprocessdefinition = WfDefinerHelper.service.getProcessDefinition(workFlowName, containerRef);
            if (wfprocessdefinition == null) {
            	logger.debug("Error to getWrokFlowTemplate," + workFlowName + " is null");
            	Locale locale = SessionHelper.getLocale();
            	String msg = WTMessage.getLocalizedMessage(RESOURCE, promotionResource.no_workflow, null, locale);
            	throw new WTException(msg);
            }

            WfProcess wfprocess = WfEngineHelper.service.createProcess(wfprocessdefinition, null, containerRef);
            ProcessData processData = wfprocess.getContext();

            wfprocess = WfEngineHelper.service.startProcessImmediate(wfprocess, processData, WORKFLOW_PRIORITY);
            return wfprocess;
        	
        } catch (WTException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
        	SessionServerHelper.manager.setAccessEnforced(flag);
        }
        return null;
    }
    
    /**
     * 设置附件 
     * @param wtdoc
     * @param contentFileName
     * @param contentInputStream
     * @param contentDescription
     * @param checkOutFolder
     * @param checkOutDescription
     * @param checkInDescription
     * @return
     * @throws WTException
     * @throws IOException
     * @throws PropertyVetoException
     */
    public static String replaceSecondaryContentWithNoCheckOut(ContentHolder wtdoc, String contentFileName, InputStream contentInputStream, String contentDescription, Folder checkOutFolder,
            String checkOutDescription, String checkInDescription) throws WTException, IOException, PropertyVetoException {
        boolean accessEnforced = SessionServerHelper.manager.isAccessEnforced();

        try {
            SessionServerHelper.manager.setAccessEnforced(false);
            ContentHolder contentHolder = (ContentHolder) ContentHelper.service.getContents(wtdoc);
            ApplicationData applicationData = ApplicationData.newApplicationData(contentHolder);
            applicationData.setRole(ContentRoleType.SECONDARY);
            applicationData.setDescription(contentDescription);
            applicationData.setFileName(contentFileName);
            applicationData = ContentServerHelper.service.updateContent(wtdoc, applicationData, contentInputStream);

            PersistenceServerHelper.manager.update(applicationData);
            PersistenceServerHelper.manager.restore(wtdoc);

            ContentHolder ch = (ContentHolder) ContentHelper.service.getContents(wtdoc);
            String url = ContentUtil.getDownloadUrl(ch, applicationData);
            
            return url;
        } finally {
            SessionServerHelper.manager.setAccessEnforced(accessEnforced);
        }
    }
    
    private static final Pattern TEMPLATE_NODE_ID = Pattern.compile("<!--[ ]*//(?:TEMPLATE_ID|NODE_ID)=([\\w\\-\\.]+)[ ]*-->");
}
