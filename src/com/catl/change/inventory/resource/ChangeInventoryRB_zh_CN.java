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
public final class ChangeInventoryRB_zh_CN extends WTListResourceBundle {
    @RBEntry("物料编号") 
    public static final String PRIVATE_CONSTANT_0 = "changeInventory.partNumber";
    @RBEntry("物料描述")
    public static final String PRIVATE_CONSTANT_1 = "changeInventory.partName";
    @RBEntry("数量") 
    public static final String PRIVATE_CONSTANT_2 = "changeInventory.quantity";
    @RBEntry("物料状态") 
    public static final String PRIVATE_CONSTANT_3 = "changeInventory.materialStatus";
    @RBEntry("处理意见")
    public static final String PRIVATE_CONSTANT_4 = "changeInventory.dispositionOption";
    @RBEntry("责任人")
    public static final String PRIVATE_CONSTANT_5 = "changeInventory.owner";
    @RBEntry("计划完成时间")
    public static final String PRIVATE_CONSTANT_6 = "changeInventory.dueDay";
    @RBEntry("备注")
    public static final String PRIVATE_CONSTANT_7 = "changeInventory.remarks";
    
    @RBEntry("库存变更") 
    public static final String PRIVATE_CONSTANT_8 = "changeInventory.tableName";
    
    @RBEntry("库存变更") 
	public static final String PRIVATE_CONSTANT_9 = "catlInventory.changeInventory.title";
    @RBEntry("库存变更") 
	public static final String PRIVATE_CONSTANT_10 = "catlInventory.changeInventory.description";
	@RBEntry("netmarkets/images/edit.gif")
	@RBComment("DO NOT TRANSLATE")
	public static final String PRIVATE_CONSTANT_11 = "catlInventory.changeInventory.icon";
    @RBEntry("库存变更") 
	public static final String PRIVATE_CONSTANT_12 = "catlInventory.changeInventory.tooltip";
    
	@RBEntry("物料库存处理意见")
	public static final String PRIVATE_CONSTANT_13 = "catlInventory.viewChangeInventory.title";
	@RBEntry("物料库存处理意见")
	public static final String PRIVATE_CONSTANT_14 = "catlInventory.viewChangeInventory.description";
	@RBEntry("物料库存处理意见")
	public static final String PRIVATE_CONSTANT_15 = "catlInventory.viewChangeInventory.tooltip";
	
	@RBEntry("设置属性") 
    public static final String PRIVATE_CONSTANT_16 = "changeTask.tabName";
}
