package com.catl.part.classification.resource;

import wt.util.resource.RBEntry;
import wt.util.resource.RBUUID;
import wt.util.resource.WTListResourceBundle;

@RBUUID("com.catl.part.classification.resource.CATLNodeConfigRB")
public class CATLNodeConfigRB_zh_CN extends WTListResourceBundle {

    @RBEntry("成熟度配置信息")
    public static final String CATL_NODE_CONFIG_VIEW_DESCRIPTION = "catlnodeconfig.nodeConfigView.description";
    @RBEntry("成熟度配置信息")
    public static final String CATL_NODE_CONFIG_VIEW_TOOLTIP = "catlpart.nodeConfigView.tooltip";
    @RBEntry("成熟度配置信息")
    public static final String CATL_NODE_CONFIG_VIEW_TITLE = "catlpart.nodeConfigView.title";
    
    @RBEntry("配置成熟度相关属性")
    public static final String CATL_NODE_CONFIG_EDIT_DESCRIPTION = "catlnodeconfig.nodeConfigEdit.description";
    @RBEntry("配置成熟度相关属性")
    public static final String CATL_NODE_CONFIG_EDIT_TOOLTIP = "catlpart.nodeConfigEdit.tooltip";
    @RBEntry("配置成熟度相关属性")
    public static final String CATL_NODE_CONFIG_EDIT_TITLE = "catlpart.nodeConfigEdit.title";
    
    @RBEntry("更新分类的物料FAE状态")
    public static final String CATL_REFRESH_PART_FAE_DESCRIPTION = "catlnodeconfig.refreshPartFAEStatus.description";
    @RBEntry("更新分类的物料FAE状态")
    public static final String CATL_REFRESH_PART_FAE_TOOLTIP = "catlpart.refreshPartFAEStatus.tooltip";
    @RBEntry("更新分类的物料FAE状态")
    public static final String CATL_REFRESH_PART_FAE_TITLE = "catlpart.refreshPartFAEStatus.title";
    
    @RBEntry("确定更新该分类的部件的FAE状态？")
    public static final String CATL_CONFIRM_REFRESH_FAE = "CATL_CONFIRM_REFRESH_FAE";
    
    @RBEntry("成熟度配置信息")
    public static final String CATL_CONFIG_PANEL_GROUP = "CATL_CONFIG_PANEL_GROUP";
    
    @RBEntry("配置成熟度相关属性")
    public static final String CATL_CONFIG_EDIT_GROUP = "CATL_CONFIG_EDIT_GROUP";
    
    @RBEntry("成熟度相关属性配置成功！")
    public static final String ATTR_UPDATE_SUCCESSFUL = "ATTR_UPDATE_SUCCESSFUL";
    
    @RBEntry("是否需要FAE")
    public static final String ATTR_NEED_FAE = "ATTR_NEED_FAE";
    
    @RBEntry("用于辅助判断FAE的属性")
    public static final String ATTR_ATTRIBUTE_REF = "ATTR_ATTRIBUTE_REF";
    
    @RBEntry("是否需要FAE成熟度报告")
    public static final String ATTR_NEED_NON_FAE_REPORTE = "ATTR_NEED_NON_FAE_REPORTE";
    
    @RBEntry("更新分类的物料FAE状态流程已启动！")
    public static final String REFRESH_FAE_WF_START_SUCCESSFUL = "WF_START_SUCCESSFUL";
    
    @RBEntry("分类不可实例化！")
    public static final String NODE_INSTANTIABLE_NOT = "NODE_INSTANTIABLE_NOT";
    
    @RBEntry("物料编码")
    public static final String REFRESH_DATA_COLUMN_1 = "REFRESH_DATA_COLUMN_1";
    
    @RBEntry("物料名称")
    public static final String REFRESH_DATA_COLUMN_2 = "REFRESH_DATA_COLUMN_2";
    
    @RBEntry("FAE状态")
    public static final String REFRESH_DATA_COLUMN_3 = "REFRESH_DATA_COLUMN_3";
    
    @RBEntry("消息")
    public static final String REFRESH_DATA_COLUMN_4 = "REFRESH_DATA_COLUMN_4";
    
    @RBEntry("发送ERP")
    public static final String SEND_ERP_Y  = "SEND_ERP_Y";
    
    @RBEntry("不发ERP")
    public static final String SEND_ERP_N = "SEND_ERP_N";
    
    @RBEntry("自制是否需要FAE")
    public static final String MAKE_NEED_FAE = "MAKE_NEED_FAE";
    
    @RBEntry("外购是否需要FAE")
    public static final String BUY_NEED_FAE = "BUY_NEED_FAE";
    
    @RBEntry("外协是否需要FAE")
    public static final String MAKE_BUY_NEED_FAE = "MAKE_BUY_NEED_FAE";
    
    @RBEntry("客供是否需要FAE")
    public static final String CUSTOMER_NEED_FAE = "CUSTOMER_NEED_FAE";
    
    @RBEntry("虚拟是否需要FAE")
    public static final String VIRTUAL_NEED_FAE = "VIRTUAL_NEED_FAE";
}
