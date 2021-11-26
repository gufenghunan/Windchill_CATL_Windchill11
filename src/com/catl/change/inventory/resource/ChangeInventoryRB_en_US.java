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
package com.catl.change.inventory.resource;

import wt.util.resource.RBComment;
import wt.util.resource.RBEntry;
import wt.util.resource.RBUUID;
import wt.util.resource.WTListResourceBundle;

@RBUUID("com.catl.change.inventory.resource.ChangeInventoryRB")
public final class ChangeInventoryRB_en_US extends WTListResourceBundle {
	
    @RBEntry("Material Number") 
    public static final String PRIVATE_CONSTANT_0 = "changeInventory.partNumber";
    @RBEntry("Material Description")
    public static final String PRIVATE_CONSTANT_1 = "changeInventory.partName";
    @RBEntry("Quantity") 
    public static final String PRIVATE_CONSTANT_2 = "changeInventory.quantity";
    @RBEntry("Status") 
    public static final String PRIVATE_CONSTANT_3 = "changeInventory.materialStatus";
    @RBEntry("Handling Suggestion")
    public static final String PRIVATE_CONSTANT_4 = "changeInventory.dispositionOption";
    @RBEntry("Owner")
    public static final String PRIVATE_CONSTANT_5 = "changeInventory.owner";
    @RBEntry("Due Date")
    public static final String PRIVATE_CONSTANT_6 = "changeInventory.dueDay";
    @RBEntry("Remarks")
    public static final String PRIVATE_CONSTANT_7 = "changeInventory.remarks";
    
    @RBEntry("Change Inventory") 
    public static final String PRIVATE_CONSTANT_8 = "changeInventory.tableName";
    
	@RBEntry("Change Inventory")
	public static final String PRIVATE_CONSTANT_9 = "catlInventory.changeInventory.title";
	@RBEntry("Change Inventory")
	public static final String PRIVATE_CONSTANT_10 = "catlInventory.changeInventory.description";
	@RBEntry("netmarkets/images/edit.gif")
	@RBComment("DO NOT TRANSLATE")
	public static final String PRIVATE_CONSTANT_11 = "catlInventory.changeInventory.icon";
	@RBEntry("Change Inventory")
	public static final String PRIVATE_CONSTANT_12 = "catlInventory.changeInventory.tooltip";
	
	@RBEntry("View Change Inventory")
	public static final String PRIVATE_CONSTANT_13 = "catlInventory.viewChangeInventory.title";
	@RBEntry("View Change Inventory")
	public static final String PRIVATE_CONSTANT_14 = "catlInventory.viewChangeInventory.description";
	@RBEntry("View Change Inventory")
	public static final String PRIVATE_CONSTANT_15 = "catlInventory.viewChangeInventory.tooltip";
	
	@RBEntry("设置属性") 
    public static final String PRIVATE_CONSTANT_16 = "changeTask.tabName";
}
