package com.catl.promotion.resource;

import wt.util.resource.RBEntry;
import wt.util.resource.RBUUID;
import wt.util.resource.WTListResourceBundle;

@RBUUID("com.catl.promotion.resource.promotionResource")
public final class promotionResource_zh_CN extends WTListResourceBundle {

    
    @RBEntry("添加")
    public static final String PRIVATE_CONSTANT_10 = "CatlPromotion.addPromotables.tooltip";
    @RBEntry("添加")
    public static final String PRIVATE_CONSTANT_11 = "CatlPromotion.addPromotables.description";    
    @RBEntry("add16x16.gif")
    public static final String PRIVATE_CONSTANT_12 = "CatlPromotion.addPromotables.icon";
    
    @RBEntry("移除")
    public static final String PRIVATE_CONSTANT_13 = "CatlPromotion.removePromotables.tooltip";
    @RBEntry("移除")
    public static final String PRIVATE_CONSTANT_14 = "CatlPromotion.removePromotables.description";    
    @RBEntry("remove16x16.gif")
    public static final String PRIVATE_CONSTANT_15 = "CatlPromotion.removePromotables.icon";
    
    @RBEntry("刷新")
    public static final String PRIVATE_CONSTANT_47 = "CatlPromotion.refreshPromotables.tooltip";
    @RBEntry("刷新")
    public static final String PRIVATE_CONSTANT_48 = "CatlPromotion.refreshPromotables.description";    
    @RBEntry("refresh.gif")
    public static final String PRIVATE_CONSTANT_49 = "CatlPromotion.refreshPromotables.icon";

    @RBEntry("编辑")
    public static final String PRIVATE_CONSTANT_17 = "CatlPromotion.promotionModification.tooltip";
    @RBEntry("编辑")
    public static final String PRIVATE_CONSTANT_18 = "CatlPromotion.promotionModification.description";    
    @RBEntry("netmarkets/images/multi_update.gif")
    public static final String PRIVATE_CONSTANT_19 = "CatlPromotion.promotionModification.icon";
    
    //设计禁用流程================================================================================================================================================================================================================
    @RBEntry("pboId")
    public static final String PRIVATE_CONSTANT_20 = "pboId";
    
    @RBEntry("")
    public static final String PRIVATE_CONSTANT_21 = "TypeIcon";
    
    @RBEntry("物料编码")
    public static final String PRIVATE_CONSTANT_22 = "Number";
    
    @RBEntry("物料名称")
    public static final String PRIVATE_CONSTANT_23 = "Name";
    
    @RBEntry("partBranchId")
    public static final String PRIVATE_CONSTANT_24 = "partBranchId";
    
    @RBEntry("*申请人（必填）")
    public static final String PRIVATE_CONSTANT_25 = "Requestor";
    
    @RBEntry("*设计禁用原因（必填）")
    public static final String PRIVATE_CONSTANT_26 = "Reason";
    
    @RBEntry("变更单号")
    public static final String PRIVATE_CONSTANT_27 = "ChangeNo";
    
    @RBEntry("新料号")
    public static final String PRIVATE_CONSTANT_28 = "NewPN";
    
    @RBEntry("备注")
    public static final String PRIVATE_CONSTANT_29 = "Comments";
    
    @RBEntry("设计禁用物料清单")
    public static final String PRIVATE_CONSTANT_30 = "Designed_disable_bom";
    
    @RBEntry("提交设计禁用")
    public static final String PRIVATE_CONSTANT_31 = "TJSJJY";
    
    @RBEntry("确认设计禁用")
    public static final String PRIVATE_CONSTANT_32 = "QRSJJY";
    
    @RBEntry("设计工程师会签")
    public static final String PRIVATE_CONSTANT_33 = "SJGCSHQ";
    
    @RBEntry("PMC会签")
    public static final String PRIVATE_CONSTANT_34 = "PMCHQ";
    
    @RBEntry("其他会签")
    public static final String PRIVATE_CONSTANT_100 = "OTHERHQ";
    
    @RBEntry("SRC会签")
    public static final String PRIVATE_CONSTANT_35 = "SRCHQ";
    
    @RBEntry("处理设计禁用集成异常")
    public static final String PRIVATE_CONSTANT_36 = "CLSJJYJCYC";
    
    @RBEntry("更新电子元器件库")
    public static final String PRIVATE_CONSTANT_37 = "GXDZYQJK";
    
    @RBEntry("存在以下问题，无法提交任务：\n{0}")
	public static final String PRIVATE_CONSTANT_38 = "submit_notes";
    
    @RBEntry("添加")
    public static final String PRIVATE_CONSTANT_39 = "workitem.addDesignDisabledObject.tooltip";
    @RBEntry("添加")
    public static final String PRIVATE_CONSTANT_40 = "workitem.addDesignDisabledObject.description";    
    @RBEntry("netmarkets/images/add16x16.gif")
    public static final String PRIVATE_CONSTANT_41 = "workitem.addDesignDisabledObject.icon";
    
    @RBEntry("移除")
    public static final String PRIVATE_CONSTANT_42 = "workitem.removeDesignDisabledObject.tooltip";
    @RBEntry("移除")
    public static final String PRIVATE_CONSTANT_43 = "workitem.removeDesignDisabledObject.description";    
    @RBEntry("netmarkets/images/remove16x16.gif")
    public static final String PRIVATE_CONSTANT_44 = "workitem.removeDesignDisabledObject.icon";
    
    @RBEntry("粘贴")
    public static final String PRIVATE_CONSTANT_45 = "workitem.pasteDesignDisabledObject.tooltip";
    @RBEntry("粘贴")
    public static final String PRIVATE_CONSTANT_46 = "workitem.pasteDesignDisabledObject.description";    
    @RBEntry("netmarkets/images/paste.gif")
    public static final String PRIVATE_CONSTANT_50 = "workitem.pasteDesignDisabledObject.icon";
    
    @RBEntry("搜索")
    public static final String search_derivePart_title = "search_derivePart_title";
    
    @RBEntry("没有选择对象")
    public static final String NO_CHOICE = "noChoice.description";
    
    @RBEntry("没有相关的工作流!")
    public static final String no_workflow = "no_workflow";
    
    @RBEntry("无有效上层BOM的物料报表已开始在后台输出，请在‘我的任务’中查看报表结果。")
    public static final String CREATE_SUCCEED_MESSAGE_1 = "CREATE_SUCCEED_MESSAGE_1";
    
    @RBEntry("启动无有效上层BOM的物料报表流程启动失败，请联系管理员排除问题后再启动!")
    public static final String CREATE_SUCCEED_MESSAGE_2 = "CREATE_SUCCEED_MESSAGE_2";
    
    @RBEntry("启动无有效上层BOM的物料报表流程")
    public static final String CREATE_SUCCEED_MESSAGE_3 = "CREATE_SUCCEED_MESSAGE_3";
    
    //************************************采购类型变更流程 start******************************************/
    @RBEntry("调整采购类型")
    public static final String SC_CONSTANT_1 = "TZCGLX";
    @RBEntry("SRC会签")
    public static final String SC_CONSTANT_2 = "SC_SRCHQ";
    @RBEntry("PMC会签")
    public static final String SC_CONSTANT_3 = "SC_PMCHQ";
    @RBEntry("IE会签")
    public static final String SC_CONSTANT_4 = "SC_SIEHQ";
    @RBEntry("发送采购类型到ERP异常处理")
    public static final String SC_CONSTANT_5 = "EXCEPTION_HANDLE";
    
    @RBEntry("pboId")
    public static final String SC_CONSTANT_6 = "sc_pboId";
    @RBEntry("")
    public static final String SC_CONSTANT_7 = "sc_TypeIcon";
    @RBEntry("物料编码")
    public static final String SC_CONSTANT_8 = "sc_Number";
    @RBEntry("物料名称")
    public static final String SC_CONSTANT_9 = "sc_Name";
    @RBEntry("partBranchId")
    public static final String SC_CONSTANT_10 = "sc_partBranchId";
    @RBEntry("创建者")
    public static final String SC_CONSTANT_11 = "partCreator";
    @RBEntry("修改者")
    public static final String SC_CONSTANT_12 = "partModifier";
    @RBEntry("采购类型修改前")
    public static final String SC_CONSTANT_13 = "sourceBefore";
    @RBEntry("*采购类型修改后")
    public static final String SC_CONSTANT_14 = "SourceAfter";
    @RBEntry("*变更原因")
    public static final String SC_CONSTANT_15 = "Cause";
    
    @RBEntry("采购类型更改单")
    public static final String PROMOTION_CONSTANT_13 = "SourceChangePNTitle";
    
    @RBEntry("采购类型物料清单")
    public static final String SC_CONSTANT_16 = "Source_Change_bom";
    
    @RBEntry("添加")
    public static final String SC_CONSTANT_17 = "workitem.addSourceChangeObject.tooltip";
    @RBEntry("添加")
    public static final String SC_CONSTANT_18 = "workitem.addSourceChangeObject.description";    
    @RBEntry("netmarkets/images/add16x16.gif")
    public static final String SC_CONSTANT_19 = "workitem.addSourceChangeObject.icon";
    
    @RBEntry("移除")
    public static final String SC_CONSTANT_20 = "workitem.removeSourceChangeObject.tooltip";
    @RBEntry("移除")
    public static final String SC_CONSTANT_21 = "workitem.removeSourceChangeObject.description";    
    @RBEntry("netmarkets/images/remove16x16.gif")
    public static final String SC_CONSTANT_22 = "workitem.removeSourceChangeObject.icon";
    
    @RBEntry("粘贴")
    public static final String SC_CONSTANT_23 = "workitem.pasteSourceChangeObject.tooltip";
    @RBEntry("粘贴")
    public static final String SC_CONSTANT_24 = "workitem.pasteSourceChangeObject.description";    
    @RBEntry("netmarkets/images/paste.gif")
    public static final String SC_CONSTANT_25 = "workitem.pasteSourceChangeObject.icon";
    
    //************************************采购类型变更流程 end******************************************/
    
    //FAE物料成熟度升级流程======================================================================================================================================================================================================
    
    @RBEntry("新建FAE物料成熟度升级单") 
    public static final String PROMOTION_CONSTANT_1 = "CatlPromotion.createFAEMaterialsMaturity.description"; 
    @RBEntry("netmarkets/images/catl/promotereqst_create.gif") 
    public static final String PROMOTION_CONSTANT_2 = "CatlPromotion.createFAEMaterialsMaturity.icon"; 
    @RBEntry("新建FAE物料成熟度升级单") 
    public static final String PROMOTION_CONSTANT_3 = "CatlPromotion.createFAEMaterialsMaturity.tooltip";
    
    @RBEntry("添加")
    public static final String PROMOTION_CONSTANT_4 = "faeMaturityAndDesignDisabledPromotion.addPromotables.tooltip";
    @RBEntry("添加")
    public static final String PROMOTION_CONSTANT_5 = "faeMaturityAndDesignDisabledPromotion.addPromotables.description";    
    @RBEntry("netmarkets/images/add16x16.gif")
    public static final String PROMOTION_CONSTANT_6 = "faeMaturityAndDesignDisabledPromotion.addPromotables.icon";
    
    @RBEntry("选择升级对象")
    public static final String PROMOTION_CONSTANT_7 = "faeMaturityAndDesignDisabledPromotion.promotionObjectsTableStep.tooltip";
    @RBEntry("选择升级对象")
    public static final String PROMOTION_CONSTANT_8 = "faeMaturityAndDesignDisabledPromotion.promotionObjectsTableStep.description"; 
    
    @RBEntry("粘贴")
    public static final String PROMOTION_CONSTANT_9 = "faeMaturityAndDesignDisabledPromotion.catlPaste.tooltip";
    @RBEntry("粘贴")
    public static final String PROMOTION_CONSTANT_10 = "faeMaturityAndDesignDisabledPromotion.catlPaste.description";    
    @RBEntry("netmarkets/images/paste.gif")
    public static final String PROMOTION_CONSTANT_11 = "faeMaturityAndDesignDisabledPromotion.catlPaste.icon";
    
    @RBEntry("FAE物料成熟度升级单")
    public static final String PROMOTION_CONSTANT_12 = "FAEMaterialsMaturityPromotionTitle";
    
    //FAE物料成熟度升级流程======================================================================================================================================================================================================
    
    @RBEntry("零部件\"{0}\"的CATL_非FAE物料成熟度1升级3流程已启动”")
    public static final String CREATE_SUCCEED_MESSAGE_4 = "CREATE_SUCCEED_MESSAGE_4";
    
    @RBEntry("零部件\"{0}\"的CATL_非FAE物料成熟度1升级3流程启动失败，请联系管理员排除问题后再启动!”")
    public static final String CREATE_SUCCEED_MESSAGE_5 = "CREATE_SUCCEED_MESSAGE_5";
    
    //设计禁用==========================================================================================
    @RBEntry("启动设计禁用") 
    public static final String PROMOTION_CONSTANT_14 = "CatlPromotion.createDisabledForDesignPN.description"; 
    @RBEntry("netmarkets/images/catl/promotereqst_create.gif") 
    public static final String PROMOTION_CONSTANT_15 = "CatlPromotion.createDisabledForDesignPN.icon"; 
    @RBEntry("启动设计禁用") 
    public static final String PROMOTION_CONSTANT_16 = "CatlPromotion.createDisabledForDesignPN.tooltip";
    
    @RBEntry("设计禁用单")
    public static final String PROMOTION_CONSTANT_17 = "DisabledForDesignPNTitle";
    
    
    @RBEntry("采购类型更改单")
    public static final String PROMOTION_CONSTANT_18 = "PlatformChangePNTitle";
    
    @RBEntry("调整产品线标识")
    public static final String SC_CONSTANT_30 = "CHANGE_PLATFORM";
    @RBEntry("FM审核")
    public static final String SC_CONSTANT_31= "FMAUDIT";
    @RBEntry("产品线标识物料清单")
    public static final String SC_CONSTANT_32 = "Platform_Change_bom";
}
