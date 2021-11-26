package com.catl.doc.resource;

import wt.util.resource.RBEntry;
import wt.util.resource.RBUUID;
import wt.util.resource.WTListResourceBundle;

@RBUUID("com.catl.doc.resource.CATLDocRB")
public class CATLDocRB extends WTListResourceBundle
{
	@RBEntry("关联部件")
	public static final String SELECT_PART_STEP_DESCRIPTION = "catldoc.selectPartStep.description";
	@RBEntry("关联部件")
	public static final String SELECT_PART_STEP_TOOLTIP = "catldoc.selectPartStep.tooltip";
	@RBEntry("关联部件")
	public static final String SELECT_PART_STEP_TITLE = "catldoc.selectPartStep.title";

	@RBEntry("确认要进行文档失效操作吗？")
    public static final String CATL_DOC_VOID_MSG = "CATL_DOC_VOID_MSG";
	@RBEntry("netmarkets/images/chgnotice_create.gif")
	public static final String DOCVOID_CREATEDOCVOID_ICON = "docVoid.createDocVoid.icon";
	@RBEntry("新建文档失效流程")
	public static final String DOCVOID_CREATEDOCVOID_TOOLTIP = "docVoid.createDocVoid.tooltip";
	@RBEntry("新建文档失效流程")
	public static final String DOCVOID_CREATEDOCVOID_TITLE = "docVoid.createDocVoid.title";
	@RBEntry("新建文档失效流程")
	public static final String DOCVOID_CREATEDOCVOID_DESCRIPTION = "docVoid.createDocVoid.description";
	
	@RBEntry("设置软件包")
	public static final String ATTACHMENTS_SOFT_DESCRIPTION = "attachments.attachments_soft_step.description";
	
	@RBEntry("添加软件包")
	public static final String ADD_ATTACHMENTS_SOFT_DESCRIPTION = "attachments.addFileAttachmentSoft.description";	
	
	@RBEntry("netmarkets/images/content-file-generic_attach.gif")
	public static final String ADD_ATTACHMENTS_SOFT_ICON = "attachments.addFileAttachmentSoft.icon";
	
	@RBEntry("添加软件包")
	public static final String ADD_ATTACHMENTS_SOFT_TOOLTIP = "attachments.addFileAttachmentSoft.tooltip";
	
	@RBEntry("软件包")
	public static final String SOFT_ATTACHMENTS_TABLE_DESCRIPTION = "attachments.softAttachmentsTable.description";
	
	@RBEntry("编辑软件包")
	public static final String EDIT_ATTACHMENTS_SOFT_DESCRIPTION = "attachments.edit_soft_attachments.description";	
	
	@RBEntry("编辑软件包")
	public static final String EDIT_ATTACHMENTS_SOFT_TOOLTIP = "attachments.edit_soft_attachments.tooltip";	
	
	@RBEntry("netmarkets/images/content-file-generic_attach.gif")
	public static final String EDIT_ATTACHMENTS_SOFT_ICON = "attachments.edit_soft_attachments.icon";
}
