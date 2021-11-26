package com.catl.require.builder;

import wt.fc.QueryResult;
import wt.part.WTPart;
import wt.util.WTException;

import com.catl.ecad.utils.CommonUtil;
import com.ptc.core.components.descriptor.DescriptorConstants.ColumnIdentifiers;
import com.ptc.jca.mvc.components.JcaTableConfig;
import com.ptc.mvc.components.AbstractComponentBuilder;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentConfigFactory;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.ds.DataSourceMode;

/**
 * Builder for the Change Task's change inventory records
 * 
 */
@ComponentBuilder(value = "aftersalebuilder")
public class AfterSaleBuilder extends AbstractComponentBuilder  {  
	@Override
    public Object buildComponentData(ComponentConfig config, ComponentParams params) throws WTException {
		WTPart part = (WTPart)params.getContextObject();
		 QueryResult queryResult = new QueryResult();
		 WTPart rpart=null;
		if(part.getNumber().endsWith("S")&&!part.getNumber().endsWith("-S")){
		 rpart=CommonUtil.getLatestWTpartByNumber(part.getNumber().substring(0,part.getNumber().length()-1));
		}else{
		 rpart=CommonUtil.getLatestWTpartByNumber(part.getNumber()+"S");
		}
		if(rpart!=null){
		      queryResult.getObjectVector().addElement(rpart);
		}
		return queryResult;
    }
	
	@Override
    public ComponentConfig buildComponentConfig(ComponentParams params) throws WTException {
        
		ComponentConfigFactory factory = getComponentConfigFactory();
		JcaTableConfig table = (JcaTableConfig) factory.newTableConfig();
		table.setLabel("售后再利用");
		table.setSelectable(true);
		table.setRowBasedObjectHandle(true);
		table.setShowCount(true);
		table.setShowCustomViewLink(false);
        table.setDataSourceMode(DataSourceMode.SYNCHRONOUS);
        table.addComponent(factory.newColumnConfig(ColumnIdentifiers.ICON, true));
		table.addComponent(factory.newColumnConfig(ColumnIdentifiers.NUMBER, true));
	    table.addComponent(factory.newColumnConfig(ColumnIdentifiers.NAME, true));
	    table.addComponent(factory.newColumnConfig(ColumnIdentifiers.STATE, true));
        table.addComponent(factory.newColumnConfig(ColumnIdentifiers.INFO_ACTION, true));
        table.addComponent(factory.newColumnConfig(ColumnIdentifiers.NM_ACTIONS, true));
        table.addComponent(factory.newColumnConfig(ColumnIdentifiers.LAST_MODIFIED, true));
        return table;
    }
	
}