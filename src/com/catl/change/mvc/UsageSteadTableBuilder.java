package com.catl.change.mvc;

import java.util.ArrayList;
import java.util.List;

import wt.fc.Persistable;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.util.WTException;

import com.ptc.core.components.descriptor.DescriptorConstants.ColumnIdentifiers;
import com.ptc.core.components.descriptor.DescriptorConstants.TableTreeProperties;
import com.ptc.mvc.components.*;
import com.catl.change.ChangeUtil;
import com.catl.change.mvc.UsagePartTreesHandler;



@ComponentBuilder("com.catl.change.mvc.UsageSteadTableBuilder")
public class UsageSteadTableBuilder extends AbstractComponentBuilder {
	
	@Override
	public Object buildComponentData(ComponentConfig arg0, ComponentParams params) throws Exception {
		String oid = (String)params.getParameter("oid");
		Persistable per = ChangeUtil.getPersistable(oid);
		List childs = new ArrayList<>();
		WTPartMaster master = null;
		if(per instanceof WTPart){
			WTPart parentpart = (WTPart)per;
			master = (WTPartMaster)parentpart.getMaster();
			childs.addAll(UsagePartTreesHandler.getSubstituteLinks(master));
		}
		
		return new UsagePartTreesHandler(childs,master);
	}
	
    @Override
	public ComponentConfig buildComponentConfig(ComponentParams params) throws WTException {
	    
	 	ComponentConfigFactory factory = getComponentConfigFactory();
        TreeConfig tree = factory.newTreeConfig();
        tree.setSelectable(true);
        tree.setSingleSelect(false);
        tree.setLabel("作为特定替换件的使用情况");
        tree.setActionModel("whereUsedTablePart");
        tree.setExpansionLevel(TableTreeProperties.NO_EXPAND);
        tree.setNodeColumn("number");
	    
	    ColumnConfig colType = factory.newColumnConfig(ColumnIdentifiers.NUMBER, true);
	    colType.setLabel("编号");
	    tree.addComponent(colType);
	    
	    //versionInfo.identifier.versionId
	    ColumnConfig colNumber = factory.newColumnConfig("groupedVersions",true);
	    colNumber.setLabel("版本");
	    tree.addComponent(colNumber);
	    
	    ColumnConfig facolNumber = factory.newColumnConfig(ColumnIdentifiers.NAME,true);
	    facolNumber.setLabel("名称");
	    tree.addComponent(facolNumber);
	    
	    ColumnConfig contextName = factory.newColumnConfig(ColumnIdentifiers.CONTAINER_NAME,true);
	    contextName.setLabel("上下文");
	    tree.addComponent(contextName);
	    
	    return tree;
	}
    
}
