package com.catl.change.util;

public class ChangeConst {
	
	public static final String RESOURCE = "com.catl.change.resource.changeActionRB";   //定义资源路径
	
	//类型属性
	public static final String CHANGEORDER_TYPE_DCN = "com.CATLBattery.CATLDChangeNotice";    //DCN类型
	
	public static final String CHANGEORDER_TYPE_ECN = "com.CATLBattery.CATLChangeNotice";     //ECN类型
	
	public static final String CHANGETASK_TYPE_DCA = "com.CATLBattery.CATLDChangeActivity2";     //DCA类型
	
	public static final String CHANGETASK_TYPE_ECA = "com.CATLBattery.CATLChangeActivity2";     //ECA类型
	
	//流程名称
	public static final String WORKFLOWNAME_DCN = "CATL设计变更通告审批流程";     //DCN对应的流程名称
	public static final String WORKFLOWNAME_DCA = "CATL设计变更任务流程";     //DCA对应的流程名称
	
	//流程变量
	public static final String CATL_NEEDPMC = "CATL_NeedPMC";         	  //需PMC处理旧料
	public static final String CATL_NEED_VERIFY = "CATL_Need_Verify";     //需变更验证
	
	//流程角色,流程节点名称
	public static final String ASSIGNEE = "ROLE_ASSIGNEE";    	//工作负责人
	public static final String FM_SH = "FM审核";    				//FM审核
	public static final String SE_SH = "SE&COST会签";    	//SE&COST会签
	
	//常量
	public static final String YFGGRW = "研发更改任务";      //研发更改任务
	public static final String WKYJHKZGGRW = "物控与计划控制更改任务";      //物控与计划控制更改任务
	public static final String SYXEXCLE = "受影响的物料及产品.xls"; //受影响的物料及产品.xls
	public static final String HTML = ".html"; //.html
	public static final String H = "html";
	public static final String EXCEL = "excel";
	
	public static final String ATTACHMENT_01= "attachment01";     //流程的附件分组1   变更方案
	public static final String ATTACHMENT_03= "attachment03";     //流程的附件分组3   变更报表
	public static final String ATTACHMENT_04= "attachment02";     //流程的附件分组2   验证结果(原变更验证报告)
	
	public static final String BEFORE_PART = "before_part";
    public static final String BEFORE_DOC = "before_doc";
    public static final String BEFORE_EPM = "before_epm";
    
    public static final String AFTRE_PART = "after_part";
    public static final String AFTRE_DOC = "after_doc";
    public static final String AFTRE_EPM = "after_epm";
	
	//属性值
	public static final String DCAYXBJ_B = "B";     	// B - 允许DCA的任务名称为“物控与计划控制更改任务”
	public static final String DCAYXBJ_AB = "A_B";		//A_B  - 允许DCA的任务名称为“研发更改任务”和“物控与计划控制更改任务”
	public static final String DEPARTMENT = "department";     //部门
	
	
	//流程模板属性定义
	public static final String changeorder2_temp_dcn = "changeorder2_temp_dcn";//DCN设计变更单
	public static final String chanActivit2_temp_dca = "chanActivit2_temp_dca";//DCA设计变更单
	
	public static final String changeorder2_temp_submitdcr = "changeorder2_temp_submitdcr";//DCN设计变更单流程节点ID:提交DCR
	public static final String changeorder2_temp_pqmsh = "changeorder2_temp_pqmsh";//DCN设计变更单流程节点ID:PQM审核
	public static final String changeorder2_temp_fmsh = "changeorder2_temp_fmsh";//DCN设计变更单流程节点ID:FM审核
	public static final String changeorder2_temp_submitdcn = "changeorder2_temp_submitdcn";//DCN设计变更单流程节点ID:提交DCN
	public static final String changeorder2_temp_cpsjgcssh = "changeorder2_temp_cpsjgcssh";//DCN设计变更单流程节点ID:产品数据工程师审核
	public static final String changeorder2_temp_sesh = "changeorder2_temp_sesh";//DCN设计变更单流程节点ID:SE审核
	public static final String changeorder2_temp_fbsapyccl = "changeorder2_temp_fbsapyccl";//DCN设计变更单流程节点ID:DCN发布SAP异常处理
	public static final String changeorder2_temp_qddcalcyccl = "changeorder2_temp_qddcalcyccl";//DCN设计变更单流程节点ID:启动DCA流程异常处理
	public static final String chanActivit_temp_wcbgrw = "chanActivit_temp_wcbgrw";//DCA设计变更单流程节点ID:完成更改任务
	
	//update by szeng 20171024 #REQ-64
	public static final String changeorder2_temp_submitdcr_equip = "changeorder2_temp_submitdcr_equip";//DCN设计变更单流程节点ID:提交DCR(设备开发)
	public static final String changeorder2_temp_fmsh_equip = "changeorder2_temp_fmsh_equip";//DCN设计变更单流程节点ID:FM审核(设备开发)
	public static final String changeorder2_temp_hq = "changeorder2_temp_hq";//DCN设计变更单流程节点ID:会签
	
	
	//=======检查信息
	public static final String checkcsd1_masg = ",物料成熟度不为1，不允许添加到设计更改单中!\n";
	public static final String checkfaepg_masg = ",物料的FAE状态是 “评估中”，不允许添加到受影响列表中!\n";
	public static final String checkNoGl_masg = ",没有关联零部件!\n";
	public static final String checkecadata_masg = ",不是已发布的状态，不能添加到受影响列表中！\n";
	public static final String checkCFData_masg = "对象已经在其他状态不为“已解决”或“已取消”的ECR的受影响对象中,不能添加到受影响列表中!\n";
	public static final String checkCFData1_masg = "对象已经在其他状态不为“已解决”或“已取消”的DCA或ECA的受影响对象中,不能添加到受影响列表中!\n";
	public static final String checkSC_masg = ",对象正在采购类型变更单中，不能允许添加到设计变更单中！ \n";
	public static final String checkPC_masg = ",对象正在产品线标识变更单中，不能允许添加到设计变更单中！ \n";
	public static final String checkNFAE_masg = ",对象正在非FAE成熟度报告中，不能允许添加到设计变更单中！ \n";
	public static final String checkDesignDisable_masg = ",对象正在设计禁用单中，不能允许添加到受影响列表中！ \n";
	
	public static final String checkstate_masg = "状态必须为已发布\n";
	
	
}
