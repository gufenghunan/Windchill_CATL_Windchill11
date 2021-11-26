package com.catl.require.resource;

import wt.util.resource.RBEntry;
import wt.util.resource.RBUUID;
import wt.util.resource.WTListResourceBundle;

@RBUUID("com.catl.require.resource.requireRB")
public final class requireRB_zh_CN extends WTListResourceBundle {
	@RBEntry("修改产品线标识")
	public static final String PRIVATE_CONSTANT_0 = "require.changeplatform.description";
	@RBEntry("编号")
	public static final String partNumber = "partNumber";
	@RBEntry("名称")
	public static final String partName = "partName";
	@RBEntry("创建者")
	public static final String partCreator = "partCreator";
	@RBEntry("修改者")
	public static final String partModifier = "partModifier";
	@RBEntry("产品线旧标识")
	public static final String platformBefore = "platformBefore";
	@RBEntry("*产品线新标识")
	public static final String platformAfter = "platformAfter";
	@RBEntry("变更原因")
	public static final String cause = "cause";
	@RBEntry("售后再利用")
	public static final String aftersale= "aftersale";
	@RBEntry("售后再利用")
	public static final String PRIVATE_CONSTANT_AFTERSALE = "catl.aftersale.description";
	@RBEntry("part.gif")
	public static final String PRIVATE_CONSTANT_1 = "line.derivePN.icon";
	@RBEntry("新建售后再利用件")
	public static final String PRIVATE_CONSTANT_NEWAFTERSALE = "require.newaftersale.description";
	@RBEntry("part.gif")
	public static final String PRIVATE_CONSTANT_NEWAFTERSALE_ICON = "require.newaftersale.icon";
	@RBEntry("新建售后再利用件")
	public static final String PRIVATE_CONSTANT_NEWAFTERSALE_TOOLTIP = "require.newaftersale.tooltip"; 
	@RBEntry("新建")
	public static final String PRIVATE_CONSTANT_NEW = "require.newaftersalefirststep.description";
	@RBEntry("添加")
	public static final String PRIVATE_CONSTANT_ADD = "require.related_add_aftersale_part.description";
	@RBEntry("add16x16.gif")
	public static final String PRIVATE_CONSTANT_ADD_ICON = "require.related_add_aftersale_part.icon";
	@RBEntry("添加")
	public static final String PRIVATE_CONSTANT_ADD_TOOLTIP = "require.related_add_aftersale_part.tooltip";
	@RBEntry("移除")
	public static final String PRIVATE_CONSTANT_DELETE = "require.related_delete_aftersale_part.description";
	@RBEntry("remove16x16.gif")
	public static final String PRIVATE_CONSTANT_DELETE_ICON = "require.related_delete_aftersale_part.icon";
	@RBEntry("移除")
	public static final String PRIVATE_CONSTANT_DELETE_TOOLTIP = "require.related_delete_aftersale_part.tooltip";
	@RBEntry("粘贴")
	public static final String PRIVATE_CONSTANT_PASTE = "require.related_paste_aftersale_part.description";
	@RBEntry("paste.gif")
	public static final String PRIVATE_CONSTANT_PASTE_ICON = "require.related_paste_aftersale_part.icon";
	@RBEntry("粘贴")
	public static final String PRIVATE_CONSTANT_PASTE_TOOLTIP = "require.related_paste_aftersale_part.tooltip";
	
	@RBEntry("智能设计配置")
	public static final String PRIVATE_CONSTANT_DESIGNCONFIGNAV="navigation.designconfig.description";
	@RBEntry("import.gif")
	public static final String PRIVATE_CONSTANT_DESIGNCONFIGICON="navigation.designconfig.icon";
	@RBEntry("智能设计配置")
	public static final String PRIVATE_CONSTANT_DESIGNCONFIGTOOLTIP="navigation.designconfig.tooltip";
	
	@RBEntry("RI系统配置")
	public static final String PRIVATE_CONSTANT_RICONFIG="confignav.riconfig.description";
	@RBEntry("RI系统配置")
	public static final String PRIVATE_CONSTANT_RICONFIGTOOLTIP="confignav.riconfig.tooltip";

	@RBEntry("EVC系统配置")
	public static final String PRIVATE_CONSTANT_EVCCONFIG="confignav.evcconfig.description";
	@RBEntry("EVC系统配置")
	public static final String PRIVATE_CONSTANT_EVCCONFIGTOOLTIP="confignav.evcconfig.tooltip";
	
	@RBEntry("PD系统配置")
	public static final String PRIVATE_CONSTANT_PDCONFIG="confignav.pdconfig.description";
	@RBEntry("PD系统配置")
	public static final String PRIVATE_CONSTANT_PDCONFIGTOOLTIP="confignav.pdconfig.tooltip";
}