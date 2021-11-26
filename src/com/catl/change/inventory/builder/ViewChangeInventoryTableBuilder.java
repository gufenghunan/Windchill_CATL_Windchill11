/* bcwti
 *
 * Copyright (c) 2010 Parametric Technology Corporation (PTC). All Rights Reserved.
 *
 * This software is the confidential and proprietary information of PTC
 * and is subject to the terms of a software license agreement. You shall
 * not disclose such confidential information and shall use it only in accordance
 * with the terms of the license agreement.
 *
 * ecwti
 */
package com.catl.change.inventory.builder;

import wt.change2.WTChangeActivity2;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.util.WTException;
import wt.util.WTMessage;

import com.catl.change.inventory.ECAPartLink;
import com.ptc.jca.mvc.components.JcaTableConfig;
import com.ptc.mvc.components.AbstractComponentBuilder;
import com.ptc.mvc.components.ColumnConfig;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentConfigFactory;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.ds.DataSourceMode;

/**
 * Builder for the Change Task's change inventory records
 * 
 */
@ComponentBuilder(value = "changeTask.viewChangeInventory")
public class ViewChangeInventoryTableBuilder extends AbstractComponentBuilder  {  
    
    private static final String RESOURCE = "com.catl.change.inventory.resource.ChangeInventoryRB";
    
	@Override
    public Object buildComponentData(ComponentConfig config, ComponentParams params) throws WTException {
        WTChangeActivity2 eca = (WTChangeActivity2)params.getContextObject();
        QueryResult queryResult = PersistenceHelper.manager.navigate(eca,ECAPartLink.PART_ROLE, ECAPartLink.class,false);
		return queryResult;
    }
	
	@Override
    public ComponentConfig buildComponentConfig(ComponentParams params) throws WTException {
        
		ComponentConfigFactory factory = getComponentConfigFactory();
		JcaTableConfig table = (JcaTableConfig) factory.newTableConfig();
		table.setLabel(WTMessage.getLocalizedMessage(RESOURCE,"changeInventory.tableName"));
		table.setSelectable(true);
		table.setRowBasedObjectHandle(true);
		table.setShowCount(true);
		table.setShowCustomViewLink(false);
		table.setId("CatlViewChangeInventory");
	    table.setType("com.catl.change.inventory.ECAPartLink");
        table.setDataSourceMode(DataSourceMode.SYNCHRONOUS);

		ColumnConfig partNumber = factory.newColumnConfig("partNumber", true);
		partNumber.setLabel(WTMessage.getLocalizedMessage(RESOURCE,"changeInventory.partNumber"));
		partNumber.setDataUtilityId("CatlViewChangeInventoryUtility");
		table.addComponent(partNumber);
		
		ColumnConfig partName = factory.newColumnConfig("partName", true);
		partName.setLabel(WTMessage.getLocalizedMessage(RESOURCE,"changeInventory.partName"));
		partName.setDataUtilityId("CatlViewChangeInventoryUtility");
		table.addComponent(partName);
		
		ColumnConfig quantity = factory.newColumnConfig(ECAPartLink.QUANTITY, true);
		quantity.setLabel(WTMessage.getLocalizedMessage(RESOURCE,"changeInventory.quantity"));
		quantity.setDataUtilityId("CatlViewChangeInventoryUtility");
		table.addComponent(quantity);
        
        ColumnConfig materialStatus = factory.newColumnConfig(ECAPartLink.MATERIAL_STATUS, true);
        materialStatus.setLabel(WTMessage.getLocalizedMessage(RESOURCE,"changeInventory.materialStatus"));
        materialStatus.setDataUtilityId("CatlViewChangeInventoryUtility");
        table.addComponent(materialStatus);
        
        ColumnConfig dispositionOption = factory.newColumnConfig(ECAPartLink.DISPOSITION_OPTION, true);
        dispositionOption.setLabel(WTMessage.getLocalizedMessage(RESOURCE,"changeInventory.dispositionOption"));
        dispositionOption.setDataUtilityId("CatlViewChangeInventoryUtility");
        table.addComponent(dispositionOption);
        
		
		ColumnConfig owner = factory.newColumnConfig(ECAPartLink.OWNER, true);
		owner.setLabel(WTMessage.getLocalizedMessage(RESOURCE,"changeInventory.owner"));
		owner.setDataUtilityId("CatlViewChangeInventoryUtility");
		table.addComponent(owner);
        
		ColumnConfig dueDay = factory.newColumnConfig(ECAPartLink.DUE_DAY, true);
		dueDay.setLabel(WTMessage.getLocalizedMessage(RESOURCE,"changeInventory.dueDay"));
        table.addComponent(dueDay);
        
		ColumnConfig remarks = factory.newColumnConfig(ECAPartLink.REMARKS, true);
		remarks.setLabel(WTMessage.getLocalizedMessage(RESOURCE,"changeInventory.remarks"));
        table.addComponent(remarks);
		
        System.out.println("viewchangeinventory 88888888888888----------------");
		
        return table;
    }
	
}