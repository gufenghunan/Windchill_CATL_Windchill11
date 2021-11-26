package com.catl.promotion.mvc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import oracle.net.aso.e;

import org.apache.log4j.Logger;

import wt.doc.WTDocument;
import wt.enterprise.RevisionControlled;
import wt.epm.EPMDocument;
import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.fc.collections.WTArrayList;
import wt.folder.SubFolder;
import wt.maturity.Promotable;
import wt.maturity.PromotionNotice;
import wt.org.WTPrincipal;
import wt.org.WTPrincipalReference;
import wt.part.WTPart;
import wt.project.Role;
import wt.session.SessionHelper;
import wt.team.Team;
import wt.team.TeamException;
import wt.team.TeamHelper;
import wt.util.WTException;
import wt.util.WTMessage;
import wt.vc.wip.WorkInProgressHelper;

import com.catl.bom.workflow.BomWfUtil;
import com.catl.common.constant.PartState;
import com.catl.common.constant.RoleName;
import com.catl.common.constant.TypeName;
import com.catl.common.util.EpmUtil;
import com.catl.common.util.PartUtil;
import com.catl.common.util.WorkflowUtil;
import com.ptc.core.components.descriptor.DescriptorConstants;
import com.ptc.core.components.util.JcaInitialRowsHelper;
import com.ptc.core.htmlcomp.components.ConfigurableTableBuilder;
import com.ptc.core.htmlcomp.tableview.ConfigurableTable;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.core.meta.server.TypeIdentifierUtility;
import com.ptc.core.ui.resources.ComponentMode;
import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.jca.mvc.components.JcaTableConfig;
import com.ptc.mvc.client.feedback.ClientFeedback;
import com.ptc.mvc.components.ColumnConfig;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentConfigFactory;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.ComponentResultProcessor;
import com.ptc.mvc.components.TableConfig;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.windchill.enterprise.maturity.PromotionRequestDescriptorConstants;
import com.ptc.windchill.enterprise.maturity.PromotionRequestHelper;
import com.ptc.windchill.enterprise.maturity.PromotionTableDataHelper;
import com.ptc.windchill.enterprise.maturity.beans.PromotionObjectBean;
import com.ptc.windchill.enterprise.maturity.commands.PromotionItemQueryCommands;
import com.ptc.windchill.enterprise.maturity.mvc.AsyncPromotionObjectsWizardTableBuilder;
import com.ptc.windchill.enterprise.maturity.search.PromotionObjectsPickerConfig;
import com.ptc.windchill.enterprise.maturity.tableViews.PromotionObjectsWizardTableView;
import com.ptc.windchill.enterprise.part.commands.PartDocServiceCommand;

@ComponentBuilder("CatlpromotionObjectsWizard")
public class CatlPromotionObjectsWizardTableBuilder extends AsyncPromotionObjectsWizardTableBuilder
    implements ConfigurableTableBuilder
{
    private static final String SELECTED_FOR_PROMOTE_STATUS = PromotionRequestDescriptorConstants.ColumnIdentifiers.SELECTED_FOR_PROMOTE_STATUS;
    public CatlPromotionObjectsWizardTableBuilder()
    {
    }

    public ConfigurableTable buildConfigurableTable(String s)
        throws WTException
    {
        return new PromotionObjectsWizardTableView();
    }
    private static Logger logger=Logger.getLogger(CatlPromotionObjectsWizardTableBuilder.class.getName());
    public ComponentConfig buildComponentConfig(ComponentParams componentparams)
        throws WTException
    {
    	logger.debug("start to config table--------------->");
        ComponentConfigFactory componentconfigfactory = getComponentConfigFactory();
        TableConfig tableconfig = componentconfigfactory.newTableConfig();
        tableconfig.setType(Promotable.class.getName());
        tableconfig.setId("promotionRequest.promotionObjects");
        tableconfig.setSelectable(true);
        tableconfig.setConfigurable(true);
        tableconfig.setTargetObject("origObject");
        tableconfig.setLabel(WTMessage.getLocalizedMessage("com.ptc.windchill.enterprise.maturity.maturityClientResource", "PROMOTION_OBJECTS_TABLEVIEW_LABEL", null, SessionHelper.getLocale()));
        tableconfig.setActionModel("promotionRequest wizard promotionItems table");
        if(tableconfig instanceof JcaTableConfig)
        {
            ((JcaTableConfig)tableconfig).setDescriptorProperty("referenceType", "OR");
            ((JcaTableConfig)tableconfig).setInitialRows(true);
            ((JcaTableConfig)tableconfig).setDescriptorProperty(DescriptorConstants.ENFORCE_TARGET_OBJECT, true);
        }
        tableconfig.setToolbarAutoSuggestPickerConfig(new PromotionObjectsPickerConfig());
        //addColumn("promotionStatus", tableconfig, componentconfigfactory);
        addColumn("statusFamily_General", tableconfig, componentconfigfactory);
        addColumn("statusFamily_Share", tableconfig, componentconfigfactory);
        addColumn("latestStatus", tableconfig, componentconfigfactory);
        //addColumn("promotionMessageStatus", tableconfig, componentconfigfactory);
        addColumn("promotionInitialSelectionStatus", tableconfig, componentconfigfactory);
        addColumn("type_icon", tableconfig, componentconfigfactory);
        addColumn("number", tableconfig, componentconfigfactory);
        addColumn("orgid", tableconfig, componentconfigfactory);
        addColumn("version", tableconfig, componentconfigfactory);
        addColumn("name", tableconfig, componentconfigfactory);
        addColumn("state", tableconfig, componentconfigfactory);
        //addColumn("promotableStates", tableconfig, componentconfigfactory);
        /*
        ColumnConfig columnconfig = componentconfigfactory.newColumnConfig("selectedForPromoteStatus", false);
        columnconfig.setHidden(true);
        columnconfig.setDataStoreOnly(true);
        tableconfig.addComponent(columnconfig);
        ArrayList arraylist = new ArrayList(2);
        */
        ColumnConfig promotableComments = componentconfigfactory.newColumnConfig(PromotionRequestDescriptorConstants.ColumnIdentifiers.PROMOTION_TARGET_COMMENTS, true);
        promotableComments.setVariableHeight(true);
        promotableComments.setTargetObject("");
        promotableComments.setComponentMode(ComponentMode.EDIT);
        tableconfig.addComponent(promotableComments);
        
        // these columns use the PromotionObjectBean as the datum
        addColumn("promotionStatus", tableconfig, componentconfigfactory, "");
        addColumn("promotionMessageStatus", tableconfig, componentconfigfactory, "");
        addColumn("promotableStates", tableconfig, componentconfigfactory, "");
        
        // needs to remain in the builder as it is used by the 'Promotion Candidates' table view
        ColumnConfig config = componentconfigfactory.newColumnConfig(SELECTED_FOR_PROMOTE_STATUS, false);
        config.setTargetObject("");
        config.setHidden(true);
       config.setDataStoreOnly(true);
       tableconfig.addComponent(config);
        
        List<String> plugins = new ArrayList<String>(1);

        plugins.add("promotionTablePlugin");
        //arraylist.add("initiallySelectedPlugin");
        ((JcaTableConfig)tableconfig).setPtypes(plugins);
        /*
        ColumnConfig columnconfig1 = componentconfigfactory.newColumnConfig("promoteMsgTypeData", false);
        columnconfig1.setDataStoreOnly(true);
        tableconfig.addComponent(columnconfig1);
        ColumnConfig columnconfig2 = componentconfigfactory.newColumnConfig("promoteStatesData", false);
        columnconfig2.setDataStoreOnly(true);
        tableconfig.addComponent(columnconfig2);
        ColumnConfig columnconfig3 = componentconfigfactory.newColumnConfig("promotionInitialSelectionType", false);
        columnconfig3.setDataStoreOnly(true);
        tableconfig.addComponent(columnconfig3);
        */
        logger.debug("end to config table--------------->");
        return tableconfig;
    }
    
    @Override
    public void buildComponentData(ComponentResultProcessor processor) throws Exception {
        JcaComponentParams params = (JcaComponentParams) processor.getParams();
        NmCommandBean cb = params.getHelperBean().getNmCommandBean();
        List<Object> promotionItems = PromotionItemQueryCommands.getPromotionItems(cb);

        PromotionTableDataHelper tableHelper = new PromotionTableDataHelper();
       // List<PromotionObjectBean> beans = tableHelper.getPromotionObjectBeans(promotionItems, cb);
        WTObject obj = null;
        logger.debug("ssssssssssssssssssssssssss");
    	List<Object> list =new ArrayList<Object>();
        if (promotionItems.size() >0)
        {
        	logger.debug("select list size()=="+promotionItems.size());
        	for (int i = 0; i < promotionItems.size(); i++) {
				obj=(WTObject)promotionItems.get(i);
				logger.debug("object select=="+obj);
				List listcollect=checkPRchildObjects(obj);
				logger.debug("listcollect:"+listcollect.size()+listcollect.toString());
				list.addAll(listcollect);
				
			}	
        	//cast list object to PromotionObjectBean
        	
        }
        List<PromotionObjectBean> beans = tableHelper.getPromotionObjectBeans(list, cb);

        if (processor.isOpen()) {
            List<ClientFeedback> msgs = tableHelper.getFeedbackMessage(params, beans);
            processor.addFeedback(msgs);
        }
        
        JcaInitialRowsHelper.addInitialRows(processor, beans);
        processor.addElements(beans);
    }

    
   
    public void buildComponentData11(ComponentResultProcessor componentresultprocessor)
            throws Exception
        {
        	logger.debug("ssssssssssssssssssssssssss");
        	List<Object> list =new ArrayList<Object>();
            JcaComponentParams jcacomponentparams = (JcaComponentParams)componentresultprocessor.getParams();
            NmCommandBean nmcommandbean = jcacomponentparams.getHelperBean().getNmCommandBean();
        	NmOid nmoid = nmcommandbean.getActionOid();
        	WTObject obj = null;
        	List selectList = PromotionItemQueryCommands.getPromotionItems(nmcommandbean); 
        	
        	/*
        	if (selectList.size() > 0) {
            	logger.debug("select list size()=="+selectList.size());
            	for (int i = 0; i < selectList.size(); i++) {
    				obj=(WTObject)selectList.get(i);
    				List listcollect=checkPRchildObjects(obj);
    				list.addAll(listcollect);
    				logger.debug("object select=="+obj);
    			}	
			}
			*/
			
            if(componentresultprocessor.isOpen())
            {
                List list1 = InitialSelectionHelper.getFeedbackMessage(jcacomponentparams, list);
                componentresultprocessor.addFeedback(list1);
            }
            logger.debug("lastlist ---------------------------"+list.size()+list.toString());
            componentresultprocessor.addElements(list);
        }
    public Object buildComponentData(ComponentConfig componentconfig, ComponentParams componentparams)
        throws Exception
    {
    	logger.debug("start to build Data--------------------->");
        ArrayList arraylist = new ArrayList();
        List list = null;
        if(componentparams instanceof JcaComponentParams)
        {
            JcaComponentParams jcacomponentparams = (JcaComponentParams)componentparams;
            NmCommandBean nmcommandbean = jcacomponentparams.getHelperBean().getNmCommandBean();
            HashMap hashmap = nmcommandbean.getText();
            nmcommandbean.addToMap(hashmap, "TABLE_ID", componentconfig.getId(), true);
       		list = PromotionItemQueryCommands.getPromotionItems(nmcommandbean);
            boolean flag = Boolean.valueOf(nmcommandbean.getTextParameter("selectPromotionStateUpdate")).booleanValue();
            logger.debug("flag:"+flag);
            if(!flag)
                PromotionRequestHelper.getDataUtilityBean(list);
            logger.debug("list.size()==="+list.size()+list.toString());
            return list;
        } else
        {
            return arraylist;
        }
    }

	public static List checkPRchildObjects(WTObject object) throws Exception
	{
		List list=new ArrayList();
		ArrayList<WTPart> childpartList=new ArrayList<WTPart>();
		ArrayList<EPMDocument> childepmList=new ArrayList<EPMDocument>();

		if (object instanceof WTPart) {

			WTPart part = (WTPart) object;
			list.add(part);
			QueryResult parentdesDocResult =PartDocServiceCommand.getAssociatedDescribeDocuments(part);
			while (parentdesDocResult.hasMoreElements()) {
				WTDocument document = (WTDocument) parentdesDocResult.nextElement();
				TypeIdentifier ti = TypeIdentifierUtility.getTypeIdentifier(document);
				String doctype = ti.getTypename();
				if ((doctype.endsWith(TypeName.doc_type_autocadDrawing)||doctype.endsWith(TypeName.softwareDoc))&&
						document.getState().toString().equalsIgnoreCase(PartState.DESIGN)&&checkifDesigner(document)) {
					list.add(document);
				}
			}		
			if(part.getState().toString().equalsIgnoreCase(PartState.DESIGN))
			{	
			//logger.debug("Visit BOM with part " + part.getNumber() );
			WTArrayList parent = new WTArrayList();
			parent.add(part);
			int level =0;
			BomWfUtil.visitBomTree(parent, childpartList, level);
			//logger.debug("childpart list size="	+ childpartList.size());
			for (int i = 0; i < childpartList.size(); i++)
			{
				WTPart childpart = childpartList.get(i);
				//logger.debug("child part state="+childpart.getNumber()+"=="+childpart.getState().toString());
				if (childpart.getState().toString().equalsIgnoreCase(PartState.DESIGN)&&checkifDesigner(childpart)) 
				{
					if(!list.contains(childpart))
					{
                     list.add(childpart);
					}
				}
				QueryResult desDocResult =PartDocServiceCommand.getAssociatedDescribeDocuments(childpart);
				while (desDocResult.hasMoreElements()) {
					WTDocument document = (WTDocument) desDocResult.nextElement();
					TypeIdentifier ti = TypeIdentifierUtility.getTypeIdentifier(document);
					String doctype = ti.getTypename();
					if ((doctype.endsWith(TypeName.doc_type_autocadDrawing)||doctype.endsWith(TypeName.softwareDoc))
							&&document.getState().toString().equalsIgnoreCase(PartState.DESIGN)&&checkifDesigner(document)) {
						list.add(document);
					}
				}
				
			}
			QueryResult epmqr =PartDocServiceCommand.getAssociatedCADDocuments(part);
			//logger.debug("epm qr size===="+epmqr.size());
			while (epmqr.hasMoreElements()) {
				Object object2 = (Object) epmqr.nextElement();
                if (object2 instanceof EPMDocument) {
                EPMDocument epmdoc = (EPMDocument)object2;
                if (epmdoc.getState().toString().equalsIgnoreCase(PartState.DESIGN)&&checkifDesigner(epmdoc)) {
				if (!list.contains(epmdoc)) {
                	list.add(epmdoc);
				}
				Collection<EPMDocument> parentepmDrawingCollection=EpmUtil.getDrawings(epmdoc);
				if (!parentepmDrawingCollection.isEmpty()) {
					Iterator parentepmiteraIterator = parentepmDrawingCollection.iterator();
					while (parentepmiteraIterator.hasNext()) {
						EPMDocument parentepmDrawingdoc = (EPMDocument) parentepmiteraIterator.next();
						if(WorkInProgressHelper.isWorkingCopy(parentepmDrawingdoc)){
                        	continue;
                        }
						//logger.debug("epmDrawingdoc part state="+parentepmDrawingdoc.getNumber()+"=="+parentepmDrawingdoc.getState().toString());
						if (parentepmDrawingdoc.getState().toString().endsWith(PartState.DESIGN)&&checkifDesigner(parentepmDrawingdoc))
						{
							if(!list.contains(parentepmDrawingdoc))
							{
	                         list.add(parentepmDrawingdoc);
							}
						}
					}
					}
				
				
				
				BomWfUtil.getEpmallchild(epmdoc, childepmList);
				//logger.debug("child epm list size=="+childepmList.size());
				for (int j = 0; j < childepmList.size(); j++) {
					EPMDocument childepmdoc=childepmList.get(j);
					//logger.debug("childepmdoc part state="+childepmdoc.getNumber()+"=="+childepmdoc.getState().toString());
					if (childepmdoc.getState().toString().equalsIgnoreCase(PartState.DESIGN)&&checkifDesigner(childepmdoc)) {
                        if(!list.contains(childepmdoc))
                        {
						list.add(childepmdoc);
                        }
					}
					Collection<EPMDocument> epmDrawingCollection=EpmUtil.getDrawings(childepmdoc);
					if (!epmDrawingCollection.isEmpty()) {
						Iterator epmiteraIterator = epmDrawingCollection.iterator();
						while (epmiteraIterator.hasNext()) {
							EPMDocument epmDrawingdoc = (EPMDocument) epmiteraIterator.next();
							if(WorkInProgressHelper.isWorkingCopy(epmDrawingdoc)){
                            	continue;
                            }
							//logger.debug("epmDrawingdoc part state="+epmDrawingdoc.getNumber()+"=="+epmDrawingdoc.getState().toString());
							if (epmDrawingdoc.getState().toString().endsWith(PartState.DESIGN)&&checkifDesigner(epmDrawingdoc))
							{
								if(!list.contains(epmDrawingdoc))
								{
                                 list.add(epmDrawingdoc);
								}
							}
						}
						}
					}
				}
                }
			}
		}
		}
		if (object instanceof EPMDocument) {
            EPMDocument epmdoc = (EPMDocument)object;
			list.add(epmdoc);
			Collection<EPMDocument> parentepmDrawingCollection=EpmUtil.getDrawings(epmdoc);
			if (!parentepmDrawingCollection.isEmpty()) {
				Iterator parentepmiteraIterator = parentepmDrawingCollection.iterator();
				while (parentepmiteraIterator.hasNext()) {
					EPMDocument parentepmDrawingdoc = (EPMDocument) parentepmiteraIterator.next();
					if(WorkInProgressHelper.isWorkingCopy(parentepmDrawingdoc)){
                    	continue;
                    }
					//logger.debug("epmDrawingdoc part state="+parentepmDrawingdoc.getNumber()+"=="+parentepmDrawingdoc.getState().toString());
					if (parentepmDrawingdoc.getState().toString().endsWith(PartState.DESIGN)&&checkifDesigner(parentepmDrawingdoc))
					{
						if (!list.contains(parentepmDrawingdoc)) {
							list.add(parentepmDrawingdoc);	
						}
                     
					}
				}
				}
			
			BomWfUtil.getEpmallchild(epmdoc, childepmList);
			//logger.debug("child epm list size=="+childepmList.size());
			for (int j = 0; j < childepmList.size(); j++) {
				EPMDocument childepmdoc=childepmList.get(j);
				//logger.debug("childepmdoc part state="+childepmdoc.getNumber()+"=="+childepmdoc.getState().toString());
				if (childepmdoc.getState().toString().equalsIgnoreCase(PartState.DESIGN)&&checkifDesigner(childepmdoc)) {
                   if(!list.contains(childepmdoc))
                   {
					list.add(childepmdoc);
                   }
				}
				Collection<EPMDocument> epmDrawingCollection=EpmUtil.getDrawings(childepmdoc);
				if (!epmDrawingCollection.isEmpty()) {
					Iterator epmiteraIterator = epmDrawingCollection.iterator();
					while (epmiteraIterator.hasNext()) {
						EPMDocument epmDrawingdoc = (EPMDocument) epmiteraIterator.next();
						if(WorkInProgressHelper.isWorkingCopy(epmDrawingdoc)){
                        	continue;
                        }
						//logger.debug("epmDrawingdoc part state="+epmDrawingdoc.getNumber()+"=="+epmDrawingdoc.getState().toString());
						if (epmDrawingdoc.getState().toString().endsWith(PartState.DESIGN)&&checkifDesigner(epmDrawingdoc))
						{
							if(!list.contains(epmDrawingdoc))
							{
                             list.add(epmDrawingdoc);
							}
						}
					}
					}
				}
			
		}if (object instanceof WTDocument) {
			WTDocument document =(WTDocument)object;
			list.add(document);
		}
		
       return list;
}
	
	public static Boolean checkifDesigner(RevisionControlled object2)
	{
     Boolean isdesginerrole=false;
     String creator="";
	try {
		creator = SessionHelper.manager.getPrincipal().getName();
	} catch (WTException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
     Role role = Role.toRole(RoleName.DESIGNER);
	if(!creator.endsWith(object2.getCreatorName()))
	{
    Team team2=null;
	try {
		team2 = (Team) TeamHelper.service.getTeam(object2);
		if (team2!=null) 
		{
        Enumeration enumPrin  = team2.getPrincipalTarget(role);
        logger.debug("design role people==="+enumPrin);
        while (enumPrin.hasMoreElements()) {
        WTPrincipalReference tempPrinRef = (WTPrincipalReference) enumPrin.nextElement();
        WTPrincipal principal = tempPrinRef.getPrincipal();
        logger.debug("design role people name==="+principal.getName());
        if (principal.getName().equals(creator)) {
			isdesginerrole=true;
		}
        }					
		}
	} catch (TeamException e2) {
		// TODO Auto-generated catch block
		e2.printStackTrace();
	} catch (WTException e2) {
		// TODO Auto-generated catch block
		e2.printStackTrace();
	}
    }else {
		isdesginerrole = true;
	}
	return isdesginerrole;
	}
    private void addColumn(String s, TableConfig tableconfig, ComponentConfigFactory componentconfigfactory)
    {
        ColumnConfig columnconfig = componentconfigfactory.newColumnConfig(s, true);
        tableconfig.addComponent(columnconfig);
    }
    
    private void addColumn(String s, TableConfig tableconfig, ComponentConfigFactory componentconfigfactory,String target)
    {
        ColumnConfig columnconfig = componentconfigfactory.newColumnConfig(s, true);
        columnconfig.setTargetObject(target);
        tableconfig.addComponent(columnconfig);
    }

     
}

