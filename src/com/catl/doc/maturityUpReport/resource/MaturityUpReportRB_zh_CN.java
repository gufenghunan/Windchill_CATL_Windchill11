package com.catl.doc.maturityUpReport.resource;

import wt.util.resource.RBEntry;
import wt.util.resource.RBUUID;
import wt.util.resource.WTListResourceBundle;

@RBUUID("com.catl.doc.maturityUpReport.resource.MaturityUpReportRB")
public class MaturityUpReportRB_zh_CN extends WTListResourceBundle {
	
	@RBEntry("关联的成熟度报告")
    public static final String ACTION_RELATED_REPORT_DESCRIPTION = "maturityReport.relatedReportList.description";
    @RBEntry("关联的成熟度报告")
    public static final String ACTION_RELATED_REPORT_TOOLTIP = "maturityReport.relatedReportList.tooltip";
    @RBEntry("关联的成熟度报告")
    public static final String ACTION_RELATED_REPORT_TITLE = "maturityReport.relatedReportList.title";
    
	@RBEntry("关联的物料")
    public static final String ACTION_RELATED_PARTS_DESCRIPTION = "maturityReport.relatedPartList.description";
    @RBEntry("关联的物料")
    public static final String ACTION_RELATED_PARTS_TOOLTIP = "maturityReport.relatedPartList.tooltip";
    @RBEntry("关联的物料")
    public static final String ACTION_RELATED_PARTS_TITLE = "maturityReport.relatedPartList.title";
	
	@RBEntry("添加部件")
    public static final String ACTION_ADD_PARTS_DESCRIPTION = "maturityReport.addParts.description";
    @RBEntry("添加部件")
    public static final String ACTION_ADD_PARTS_TOOLTIP = "maturityReport.addParts.tooltip";
    @RBEntry("添加部件")
    public static final String ACTION_ADD_PARTS_TITLE = "maturityReport.addParts.title";
    @RBEntry("add16x16.gif")
    public static final String ACTION_ADD_PARTS_ICON = "maturityReport.addParts.icon";
    
    @RBEntry("移除部件")
    public static final String ACTION_REMOVE_PARTS_DESCRIPTION = "maturityReport.removeParts.description";
    @RBEntry("移除部件")
    public static final String ACTION_REMOVE_PARTS_TOOLTIP = "maturityReport.removeParts.tooltip";
    @RBEntry("移除部件")
    public static final String ACTION_REMOVE_PARTS_TITLE = "maturityReport.removeParts.title";
    @RBEntry("remove16x16.gif")
    public static final String ACTION_REMOVE_PARTS_ICON = "maturityReport.removeParts.icon";
    
    @RBEntry("粘贴部件")
    public static final String ACTION_PASTE_PARTS_DESCRIPTION = "maturityReport.pasteParts.description";
    @RBEntry("粘贴部件")
    public static final String ACTION_PASTE_PARTS_TOOLTIP = "maturityReport.pasteParts.tooltip";
    @RBEntry("粘贴部件")
    public static final String ACTION_PASTE_PARTS_TITLE = "maturityReport.pasteParts.title";
    @RBEntry("paste.gif")
    public static final String ACTION_PASTE_PARTS_ICON = "maturityReport.pasteParts.icon";

	@RBEntry("该物料与非FAE物料成熟度3升级报告{0}有关联，请移除关系后再删除！")
	public static final String ERROR_DELETE_INITIALPART = "ERROR_DELETE_INITIALPART";
	
	@RBEntry("物料{0}与其它非FAE物料成熟度3升级报告已存在关联！")
	public static final String ERROR_EXIST_OTHER_LINK = "ERROR_EXIST_OTHER_LINK";
	
	@RBEntry("物料{0}的最新版本状态不是“已发布”")
	public static final String ERROR_NON_RELEASED = "ERROR_NON_RELEASED";
	
	@RBEntry("物料{0}的FAE状态属性值不是“不需要”")
	public static final String ERROR_NONFAESTATUS = "ERROR_NONFAESTATUS";
	
	@RBEntry("物料{0}的成熟度属性值不是“1”")
	public static final String ERROR_MATURITY_NON1 = "ERROR_MATURITY_NON1";
	
	@RBEntry("物料初始关联版本")
	public static final String LABEL_INITIAL_VERSION = "LABEL_INITIAL_VERSION";
	
	@RBEntry("关联物料")
	public static final String TABLE_RELATED_PARTS = "TABLE_RELATED_PARTS";
	
	@RBEntry("成熟度报告")
	public static final String TABLE_MATURITY_REPORTS = "TABLE_MATURITY_REPORTS";

	
}
