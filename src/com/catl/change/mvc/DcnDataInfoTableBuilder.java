package com.catl.change.mvc;

import java.util.*;

import wt.change2.*;
import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.fc.Persistable;
import wt.fc.QueryResult;
import wt.part.WTPart;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.vc.VersionControlHelper;
import wt.vc.config.LatestConfigSpec;
import wt.workflow.work.WorkItem;

import com.ptc.mvc.components.*;
import com.ptc.windchill.enterprise.workitem.WorkItemCommands;
import com.catl.change.ChangeUtil;


@ComponentBuilder("com.catl.change.mvc.DcnDataInfoTableBuilder")
public class DcnDataInfoTableBuilder extends AbstractComponentBuilder {
	
	@Override
	public Object buildComponentData(ComponentConfig arg0, ComponentParams params) throws WTException {
		Map<String,String> tablemp = new HashMap<String,String>();
		Object obj = params.getContextObject();
		if(obj instanceof WorkItem){
			WorkItem currentWorkItem = (WorkItem)obj;
			WorkItemCommands command = new WorkItemCommands();
			Persistable pbo = command.getPBOFromWorkItem(currentWorkItem);
			Map<WTPart,WTDocumentMaster> datamap = ChangeUtil.getdocMasterListByPart(pbo);
			for(WTPart part : datamap.keySet()){
				tablemp.put("partNo", part.getNumber());
				tablemp.put("partName", part.getName());
				tablemp.put("docNo", datamap.get(part).getNumber());
				tablemp.put("docName", datamap.get(part).getName());
				QueryResult qr = VersionControlHelper.service.allVersionsOf(datamap.get(part));
				qr = (new LatestConfigSpec()).process(qr);
				if(qr.hasMoreElements()){
					WTDocument doc = (WTDocument)qr.nextElement();
					tablemp.put("docState",doc.getState().getState().getDisplay(Locale.CHINA));
				}
			}
		}
		return tablemp;
	}
	
    @Override
	public ComponentConfig buildComponentConfig(ComponentParams params) throws WTException {
	       
	 	ComponentConfigFactory factory = getComponentConfigFactory();
	    TableConfig table = factory.newTableConfig();
	    //table.setComponentMode(ComponentMode.VIEW);
	    table.setLabel("部件关联的非FAE成熟度3升级报告");
	    
	    ColumnConfig partnumber = factory.newColumnConfig("partNo", true);
	    partnumber.setLabel("物料编号");
	    table.addComponent(partnumber);
	    
	    ColumnConfig partname = factory.newColumnConfig("partName", true);
	    partname.setLabel("物料名称");
	    table.addComponent(partname);
	    
	    ColumnConfig docnumber = factory.newColumnConfig("docNo", true);
	    docnumber.setLabel("报告编号");
	    table.addComponent(docnumber);
	    
	    ColumnConfig docname = factory.newColumnConfig("docName", true);
	    docname.setLabel("报告名称");
	    table.addComponent(docname);
	    
	    ColumnConfig docstate = factory.newColumnConfig("docState", true);
	    docstate.setLabel("报告状态");
	    table.addComponent(docstate);
	    
        return table;
        
	}
    
    
    
}
