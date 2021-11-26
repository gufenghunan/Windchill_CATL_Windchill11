package com.catl.change.resource;
import wt.util.resource.*;


@RBUUID("com.catl.change.resource.changeActionRB")
public class changeActionRB extends WTListResourceBundle{
	
	@RBEntry("Customer File")
    public static final String CUSTOMER_FILE_DESCRIPTION="catl.customerFileList.description";

    @RBEntry("Customer File")
    public static final String CUSTOMER_FILE_TITLE="catl.customerFileList.title";
    
    @RBEntry("Customer File")
    public static final String ECN_CUSTOMER_FILE_DESCRIPTION="catl.customerFileOnECN.description";

    @RBEntry("Customer File")
    public static final String ECN_CUSTOMER_FILE_TITLE="catl.customerFileOnECN.title";
    
	//请确认已阅读有关‘非FAE成熟度3升级报告’相关的提示信息
	@RBEntry("请确认已阅读有关‘非FAE成熟度3升级报告’相关的提示信息！")
	public static final String NOT_ISCHECKINFO = "checkInfo";
    
    @RBEntry("Customer File Tab")
    public static final String PRIVATE_CONSTANT_305 = "object.customerFile.description";

	@RBEntry("netmarkets/images/chgnotice_create.gif")
	public static final String PRIVATE_CONSTANT_CREATE_1 = "changeNotice.createdcn.icon";
	@RBEntry("新建设计变更单")
	public static final String PRIVATE_CONSTANT_1 = "changeNotice.createdcn.tooltip";
	@RBEntry("新建设计变更单")
	public static final String PRIVATE_CONSTANT_2 = "changeNotice.createdcn.title";
	@RBEntry("新建设计变更单")
	public static final String PRIVATE_CONSTANT_3 = "changeNotice.createdcn.description";

	@RBEntry("netmarkets/images/edit.png")
	public static final String PRIVATE_CONSTANT_EDIT_2 = "changeNotice.editdcn.icon";
	@RBEntry("编辑设计变更单")
	public static final String PRIVATE_CONSTANT_4 = "changeNotice.editdcn.tooltip";
	@RBEntry("编辑设计变更单")
	public static final String PRIVATE_CONSTANT_5 = "changeNotice.editdcn.title";
	@RBEntry("编辑设计变更单")
	public static final String PRIVATE_CONSTANT_6 = "changeNotice.editdcn.description";
	
	//DCN流程=======================
    @RBEntry("提交DCR")
    public static final String PRIVATE_CONSTANT_7 = "TJDCR";

    @RBEntry("PQM&FM审核")
    public static final String PRIVATE_CONSTANT_8 = "FMSH";
    
    @RBEntry("提交DCN")
    public static final String PRIVATE_CONSTANT_9 = "TJDCN";
    
    @RBEntry("产品数据工程师审核")
    public static final String PRIVATE_CONSTANT_10 = "CPSJGCSSH";
    
    @RBEntry("SE&COST会签")
    public static final String PRIVATE_CONSTANT_11 = "SESH";
	
    @RBEntry("DCN发布SAP异常处理")
    public static final String PRIVATE_CONSTANT_12 = "FBYC";
    
    @RBEntry("启动DCA流程异常处理")
    public static final String PRIVATE_CONSTANT_13 = "QDYC";
    
    @RBEntry("完成更改任务")
    public static final String PRIVATE_CONSTANT_14 = "WCGGRW";
    
    @RBEntry("PQM审核")
    public static final String PRIVATE_CONSTANT_15 = "PQMSH";
    
    //===流程任务页面表格
  	@RBEntry("变更方案")
  	public static final String review_attachments_changetitle = "review_attachments_changetitle";
  	
  	@RBEntry("验证结果")
  	public static final String review_attachments_yzjgtitle = "review_attachments_yzjgtitle";
  	
  	@RBEntry("变更报表")
  	public static final String review_attachments_changeexporttitle = "review_attachments_changeexporttitle";
  	
    @RBEntry("新建设计变更单")
    public static final String DCN_TITLE="dcn_title";
}
