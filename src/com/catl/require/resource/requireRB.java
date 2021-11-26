package com.catl.require.resource;

import wt.util.resource.RBEntry;
import wt.util.resource.RBUUID;
import wt.util.resource.WTListResourceBundle;

@RBUUID("com.catl.require.resource.requireRB")
public final class requireRB extends WTListResourceBundle {
	@RBEntry("change platform")
	public static final String PRIVATE_CONSTANT_0 = "require.changeplatform.description";
	@RBEntry("partNumber")
	public static final String partNumber = "partNumber";
	@RBEntry("partName")
	public static final String partName = "partName";
	@RBEntry("partCreator")
	public static final String partCreator = "partCreator";
	@RBEntry("partModifier")
	public static final String partModifier = "partModifier";
	@RBEntry("platformBefore")
	public static final String platformBefore = "platformBefore";
	@RBEntry("platformAfter")
	public static final String platformAfter = "platformAfter";
	@RBEntry("cause")
	public static final String cause = "cause";
	@RBEntry("aftersale")
	public static final String aftersale= "aftersale";
	@RBEntry("after sale")
	public static final String PRIVATE_CONSTANT_AFTERSALE = "catl.aftersale.description";
	@RBEntry("new after sale")
	public static final String PRIVATE_CONSTANT_NEWAFTERSALE = "require.newaftersale.description";
	@RBEntry("part.gif")
	public static final String PRIVATE_CONSTANT_NEWAFTERSALE_ICON = "require.newaftersale.icon";
	@RBEntry("new after sale")
	public static final String PRIVATE_CONSTANT_NEWAFTERSALE_TOOLTIP = "require.newaftersale.tooltip";
	@RBEntry("new after sale first step")
	public static final String PRIVATE_CONSTANT_NEW = "require.newaftersalefirststep.description";
	
	@RBEntry("add")
	public static final String PRIVATE_CONSTANT_ADD = "require.related_add_aftersale_part.description";
	@RBEntry("add16x16.gif")
	public static final String PRIVATE_CONSTANT_ADD_ICON = "require.related_add_aftersale_part.icon";
	@RBEntry("add")
	public static final String PRIVATE_CONSTANT_ADD_TOOLTIP = "require.related_add_aftersale_part.tooltip";
	@RBEntry("remove")
	public static final String PRIVATE_CONSTANT_DELETE = "require.related_delete_aftersale_part.description";
	@RBEntry("remove16x16.gif")
	public static final String PRIVATE_CONSTANT_DELETE_ICON = "require.related_delete_aftersale_part.icon";
	@RBEntry("remove")
	public static final String PRIVATE_CONSTANT_DELETE_TOOLTIP = "require.related_delete_aftersale_part.tooltip";
	@RBEntry("paste")
	public static final String PRIVATE_CONSTANT_PASTE = "require.related_paste_aftersale_part.description";
	@RBEntry("paste.gif")
	public static final String PRIVATE_CONSTANT_PASTE_ICON = "require.related_paste_aftersale_part.icon";
	@RBEntry("paste")
	public static final String PRIVATE_CONSTANT_PASTE_TOOLTIP = "require.related_paste_aftersale_part.tooltip";

	@RBEntry("designconfig navigation")
	public static final String PRIVATE_CONSTANT_DESIGNCONFIGNAV="navigation.designconfig.description";
	@RBEntry("import.gif")
	public static final String PRIVATE_CONSTANT_DESIGNCONFIGICON="navigation.designconfig.icon";
	@RBEntry("designconfig navigation")
	public static final String PRIVATE_CONSTANT_DESIGNCONFIGTOOLTIP="navigation.designconfig.tooltip";
	
	@RBEntry("ri config")
	public static final String PRIVATE_CONSTANT_RICONFIG="confignav.riconfig.description";
	@RBEntry("ri config")
	public static final String PRIVATE_CONSTANT_RICONFIGTOOLTIP="confignav.riconfig.tooltip";
	
	@RBEntry("evc config")
	public static final String PRIVATE_CONSTANT_EVCCONFIG="confignav.evcconfig.description";
	@RBEntry("evc config")
	public static final String PRIVATE_CONSTANT_EVCCONFIGTOOLTIP="confignav.evcconfig.tooltip";
	
	@RBEntry("pd config")
	public static final String PRIVATE_CONSTANT_PDCONFIG="confignav.pdconfig.description";
	@RBEntry("pd config")
	public static final String PRIVATE_CONSTANT_PDCONFIGTOOLTIP="confignav.pdconfig.tooltip";

}