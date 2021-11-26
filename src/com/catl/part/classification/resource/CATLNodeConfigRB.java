package com.catl.part.classification.resource;

import wt.util.resource.RBEntry;
import wt.util.resource.RBUUID;
import wt.util.resource.WTListResourceBundle;

@RBUUID("com.catl.part.classification.resource.CATLNodeConfigRB")
public class CATLNodeConfigRB extends WTListResourceBundle {

    @RBEntry("Maturity Configuration Info")
    public static final String CATL_NODE_CONFIG_VIEW_DESCRIPTION = "catlnodeconfig.nodeConfigView.description";
    @RBEntry("Maturity Configuration Info")
    public static final String CATL_NODE_CONFIG_VIEW_TOOLTIP = "catlpart.nodeConfigView.tooltip";
    @RBEntry("Maturity Configuration Info")
    public static final String CATL_NODE_CONFIG_VIEW_TITLE = "catlpart.nodeConfigView.title";
    
    @RBEntry("Edit Maturity Configuration")
    public static final String CATL_NODE_CONFIG_EDIT_DESCRIPTION = "catlnodeconfig.nodeConfigEdit.description";
    @RBEntry("Edit Maturity Configuration")
    public static final String CATL_NODE_CONFIG_EDIT_TOOLTIP = "catlpart.nodeConfigEdit.tooltip";
    @RBEntry("Edit Maturity Configuration")
    public static final String CATL_NODE_CONFIG_EDIT_TITLE = "catlpart.nodeConfigEdit.title";
    
    @RBEntry("Refresh Part Fae Status")
    public static final String CATL_REFRESH_PART_FAE_DESCRIPTION = "catlnodeconfig.refreshPartFAEStatus.description";
    @RBEntry("Refresh Part Fae Status")
    public static final String CATL_REFRESH_PART_FAE_TOOLTIP = "catlpart.refreshPartFAEStatus.tooltip";
    @RBEntry("Refresh Part Fae Status")
    public static final String CATL_REFRESH_PART_FAE_TITLE = "catlpart.refreshPartFAEStatus.title";
    
    @RBEntry("Confirm to refreshes the detailed FAE status for this classification's parts?")
    public static final String CATL_CONFIRM_REFRESH_FAE = "CATL_CONFIRM_REFRESH_FAE";
    
    @RBEntry("Maturity Configuration Info")
    public static final String CATL_CONFIG_PANEL_GROUP = "CATL_CONFIG_PANEL_GROUP";
    
    @RBEntry("Edit Maturity Configuration")
    public static final String CATL_CONFIG_EDIT_GROUP = "CATL_CONFIG_EDIT_GROUP";
    
    @RBEntry("Edit successful!")
    public static final String ATTR_UPDATE_SUCCESSFUL = "ATTR_UPDATE_SUCCESSFUL";
    
    @RBEntry("Need FAE")
    public static final String ATTR_NEED_FAE = "ATTR_NEED_FAE";
    
    @RBEntry("Attribute For FAE")
    public static final String ATTR_ATTRIBUTE_REF = "ATTR_ATTRIBUTE_REF";
    
    @RBEntry("Need FAE Report")
    public static final String ATTR_NEED_NON_FAE_REPORTE = "ATTR_NEED_NON_FAE_REPORTE";
    
    @RBEntry("The refresh data workflow is started!")
    public static final String REFRESH_FAE_WF_START_SUCCESSFUL = "WF_START_SUCCESSFUL";
    
    @RBEntry("Classification cannot instantiate!")
    public static final String NODE_INSTANTIABLE_NOT = "NODE_INSTANTIABLE_NOT";
    
    @RBEntry("Number")
    public static final String REFRESH_DATA_COLUMN_1 = "REFRESH_DATA_COLUMN_1";
    
    @RBEntry("Name")
    public static final String REFRESH_DATA_COLUMN_2 = "REFRESH_DATA_COLUMN_2";
    
    @RBEntry("FAE Status")
    public static final String REFRESH_DATA_COLUMN_3 = "REFRESH_DATA_COLUMN_3";
    
    @RBEntry("Result")
    public static final String REFRESH_DATA_COLUMN_4 = "REFRESH_DATA_COLUMN_4";
    
    @RBEntry("Send ERP")
    public static final String SEND_ERP_Y  = "SEND_ERP_Y";
    
    @RBEntry("Did not send ERP")
    public static final String SEND_ERP_N = "SEND_ERP_N";
    
    @RBEntry("Make For FAE")
    public static final String MAKE_NEED_FAE = "MAKE_NEED_FAE";
    
    @RBEntry("Buy For FAE")
    public static final String BUY_NEED_FAE = "BUY_NEED_FAE";
    
    @RBEntry("MakeBuy For FAE")
    public static final String MAKE_BUY_NEED_FAE = "MAKE_BUY_NEED_FAE";
    
    @RBEntry("Customer For FAE")
    public static final String CUSTOMER_NEED_FAE = "CUSTOMER_NEED_FAE";
    
    @RBEntry("Virtual For FAE")
    public static final String VIRTUAL_NEED_FAE = "VIRTUAL_NEED_FAE";
}
