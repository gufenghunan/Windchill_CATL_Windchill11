package com.catl.change.configs;

import com.catl.change.util.ChangeConst;
import com.catl.change.resource.*; 
import com.catl.common.constant.RoleName;
import com.catl.promotion.dbs.DesignDisabledObjectsCollector;
import com.catl.promotion.util.PromotionConst;
import com.ptc.xworks.xmlobject.annotation.ResourceBundleKey;
import com.ptc.xworks.workflow.annotation.*;


@WorkflowTemplateDef(
		templateId = ChangeConst.changeorder2_temp_dcn,
		templateName = "CATL设计变更通告审批流程",
		description = "CATL设计变更通告审批流程",
		attachmentGroupDefs = {
				@AttachmentGroupDef(groupId = "attachment01", name = @ResourceBundleKey(resourceBundle = ChangeConst.RESOURCE, key = changeActionRB.review_attachments_changetitle)), 
		        @AttachmentGroupDef(groupId = "attachment02", name = @ResourceBundleKey(resourceBundle = ChangeConst.RESOURCE, key = changeActionRB.review_attachments_yzjgtitle)),
		        @AttachmentGroupDef(groupId = "attachment03", name = @ResourceBundleKey(resourceBundle = ChangeConst.RESOURCE, key = changeActionRB.review_attachments_changeexporttitle))
				}, 
		processPhaseDefs = {
				@ProcessPhaseDef(
						name = "changeorder2_temp_submitdcr",
						description = @ResourceBundleKey(resourceBundle = ChangeConst.RESOURCE, key = changeActionRB.PRIVATE_CONSTANT_7)
						),
				@ProcessPhaseDef(
						name = "changeorder2_temp_cpsjgcssh", 
						description = @ResourceBundleKey(resourceBundle = ChangeConst.RESOURCE, key = changeActionRB.PRIVATE_CONSTANT_10)
						),
				@ProcessPhaseDef(
						name = "changeorder2_temp_fmsh", 
						description = @ResourceBundleKey(resourceBundle = ChangeConst.RESOURCE, key = changeActionRB.PRIVATE_CONSTANT_8)
						),
				@ProcessPhaseDef(
						name = "changeorder2_temp_submitdcn",
						description = @ResourceBundleKey(resourceBundle = ChangeConst.RESOURCE, key = changeActionRB.PRIVATE_CONSTANT_9)
						),
				@ProcessPhaseDef(
						name = "changeorder2_temp_sesh", 
						description = @ResourceBundleKey(resourceBundle = ChangeConst.RESOURCE, key = changeActionRB.PRIVATE_CONSTANT_11)
						),
				@ProcessPhaseDef(
						name = "changeorder2_temp_fbsapyccl", 
						description = @ResourceBundleKey(resourceBundle = ChangeConst.RESOURCE, key = changeActionRB.PRIVATE_CONSTANT_12)
						),
				@ProcessPhaseDef(
						name = "changeorder2_temp_qddcalcyccl", 
						description = @ResourceBundleKey(resourceBundle = ChangeConst.RESOURCE, key = changeActionRB.PRIVATE_CONSTANT_13)
						)
			}
		
)
public interface DcnApprovalGrantFromTempalte {
	
	@ActivityNodeDef(
			nodeId = ChangeConst.changeorder2_temp_submitdcr,
			name = @ResourceBundleKey,
			description = "提交DCR",
			phaseName = "changeorder2_temp_submitdcr",
			attachmentGroupTab = true,					//是否显示附件组
			enableApplicationForm = true,				//是否加载JSP
			applicationFormJspPath = "/netmarkets/jsp/catl/changeTask/DcnDatalistFromTemp.jsp",
			operationToAttachmentGroup = {
				@AllowedOperation(
					groupId = "attachment01",
					role = PromotionConst.SUBMITTER, 
					allowedOperations = {UserOperation.REMOVE,UserOperation.VIEW, UserOperation.ADD}),
					
					@AllowedOperation(
						groupId = "attachment02",
						role = PromotionConst.SUBMITTER,
						allowedOperations = {UserOperation.VIEW}),
					@AllowedOperation(
						groupId = "attachment03",
						role = PromotionConst.SUBMITTER, 
						allowedOperations = {UserOperation.VIEW})
					}
			)
	String changeorder2_temp_submitdcr = "changeorder2_temp_submitdcr"; // 该常量仅用于承载Annotation,并没有任何实际用途
	
	//update by szeng 2017-11-06
	
	@ActivityNodeDef(
			nodeId = ChangeConst.changeorder2_temp_submitdcr_equip,
			name = @ResourceBundleKey,
			description = "提交DCR",
			phaseName = "changeorder2_temp_submitdcr",
			attachmentGroupTab = true,					//是否显示附件组
			enableApplicationForm = true,				//是否加载JSP
			applicationFormJspPath = "/netmarkets/jsp/catl/changeTask/DcnDatalistFromTemp.jsp",
			operationToAttachmentGroup = {
				@AllowedOperation(
					groupId = "attachment01",
					role = PromotionConst.SUBMITTER, 
					allowedOperations = {UserOperation.REMOVE,UserOperation.VIEW, UserOperation.ADD}),
					
					@AllowedOperation(
						groupId = "attachment02",
						role = PromotionConst.SUBMITTER,
						allowedOperations = {UserOperation.VIEW}),
					@AllowedOperation(
						groupId = "attachment03",
						role = PromotionConst.SUBMITTER, 
						allowedOperations = {UserOperation.VIEW})
					}
			)
	String changeorder2_temp_submitdcr_equip = "changeorder2_temp_submitdcr_equip"; // 该常量仅用于承载Annotation,并没有任何实际用途
	
	@ActivityNodeDef(
			nodeId = ChangeConst.changeorder2_temp_fmsh_equip,
			name = @ResourceBundleKey,
			description = "FM审核",
			phaseName = "changeorder2_temp_fmsh",
			attachmentGroupTab = true,
			operationToAttachmentGroup = {
			    @AllowedOperation(
			    	groupId = "attachment01",
			    	role = PromotionConst.QUALITY_REPRESENTATIVE, 
			    	allowedOperations = {UserOperation.VIEW}),
			
				@AllowedOperation(
					groupId = "attachment02",
					role = PromotionConst.QUALITY_REPRESENTATIVE,
					allowedOperations = {UserOperation.VIEW}),
				@AllowedOperation(
					groupId = "attachment03",
					role = PromotionConst.QUALITY_REPRESENTATIVE, 
					allowedOperations = {UserOperation.VIEW})
				}
			)
	String changeorder2_temp_fmsh_equip = "changeorder2_temp_fmsh_equip"; // 该常量仅用于承载Annotation,并没有任何实际用途
	
	@ActivityNodeDef(
			nodeId = ChangeConst.changeorder2_temp_hq,
			name = @ResourceBundleKey,
			description = "会签",
			phaseName = "changeorder2_temp_fmsh",
			attachmentGroupTab = true,
			operationToAttachmentGroup = {
			    @AllowedOperation(
			    	groupId = "attachment01",
			    	role = PromotionConst.QUALITY_REPRESENTATIVE, 
			    	allowedOperations = {UserOperation.VIEW}),
			
				@AllowedOperation(
					groupId = "attachment02",
					role = PromotionConst.QUALITY_REPRESENTATIVE,
					allowedOperations = {UserOperation.VIEW}),
				@AllowedOperation(
					groupId = "attachment03",
					role = PromotionConst.QUALITY_REPRESENTATIVE, 
					allowedOperations = {UserOperation.VIEW})
				}
			)
	String changeorder2_temp_hq = "changeorder2_temp_hq"; // 该常量仅用于承载Annotation,并没有任何实际用途
	
	//update by szeng 2017-11-06 end
	
	@ActivityNodeDef(
			nodeId = ChangeConst.changeorder2_temp_pqmsh,
			name = @ResourceBundleKey,
			description = "PQM审核",
			phaseName = "changeorder2_temp_pqmsh",
			attachmentGroupTab = true,
			operationToAttachmentGroup = {
			    @AllowedOperation(
			    	groupId = "attachment01",
			    	role = PromotionConst.QUALITY_REPRESENTATIVE, 
			    	allowedOperations = {UserOperation.VIEW}),
			
				@AllowedOperation(
					groupId = "attachment02",
					role = PromotionConst.QUALITY_REPRESENTATIVE,
					allowedOperations = {UserOperation.VIEW}),
				@AllowedOperation(
					groupId = "attachment03",
					role = PromotionConst.QUALITY_REPRESENTATIVE, 
					allowedOperations = {UserOperation.VIEW})
				}
			)
	String changeorder2_temp_pqmsh = "changeorder2_temp_pqmsh"; // 该常量仅用于承载Annotation,并没有任何实际用途
	
	@ActivityNodeDef(
			nodeId = ChangeConst.changeorder2_temp_fmsh,
			name = @ResourceBundleKey,
			description = "FM审核",
			phaseName = "changeorder2_temp_fmsh",
			attachmentGroupTab = true,
			operationToAttachmentGroup = {
			    @AllowedOperation(
			    	groupId = "attachment01",
			    	role = PromotionConst.DEPARTMENT_MANAGER, 
			    	allowedOperations = {UserOperation.VIEW}),
			
				@AllowedOperation(
					groupId = "attachment02",
					role = PromotionConst.DEPARTMENT_MANAGER,
					allowedOperations = {UserOperation.VIEW}),
				@AllowedOperation(
					groupId = "attachment03",
					role = PromotionConst.DEPARTMENT_MANAGER, 
					allowedOperations = {UserOperation.VIEW})
				}
			)
	String changeorder2_temp_fmsh = "changeorder2_temp_fmsh"; // 该常量仅用于承载Annotation,并没有任何实际用途
	
	@ActivityNodeDef(
			nodeId = ChangeConst.changeorder2_temp_submitdcn,
			name = @ResourceBundleKey,
			description = "提交DCN",
			phaseName = "changeorder2_temp_submitdcn",
			attachmentGroupTab = true,
			enableApplicationForm = true,
			applicationFormJspPath = "/netmarkets/jsp/catl/changeTask/DcnDatalistFromTemp.jsp",
			operationToAttachmentGroup = {
			@AllowedOperation(
				groupId = "attachment01",
				role = PromotionConst.SUBMITTER, 
				allowedOperations = {UserOperation.VIEW}),
			
				@AllowedOperation(
					groupId = "attachment02",
					role = PromotionConst.SUBMITTER,
					allowedOperations = {UserOperation.REMOVE,UserOperation.VIEW, UserOperation.ADD}),
				@AllowedOperation(
					groupId = "attachment03",
					role = PromotionConst.SUBMITTER, 
					allowedOperations = {UserOperation.VIEW})
				}
			)
	String changeorder2_temp_submitdcn = "changeorder2_temp_submitdcn"; // 该常量仅用于承载Annotation,并没有任何实际用途
	
	@ActivityNodeDef(
			nodeId = ChangeConst.changeorder2_temp_cpsjgcssh,
			name = @ResourceBundleKey,
			description = "产品数据工程师审核",
			phaseName = "changeorder2_temp_cpsjgcssh",
			attachmentGroupTab = true,
			operationToAttachmentGroup = {
				@AllowedOperation(
				groupId = "attachment01",
				role = PromotionConst.PRODUCT_DATA_ENGINGEER, 
				allowedOperations = {UserOperation.VIEW}),
				
				@AllowedOperation(
					groupId = "attachment02",
					role = PromotionConst.PRODUCT_DATA_ENGINGEER,
					allowedOperations = {UserOperation.VIEW}),
				@AllowedOperation(
					groupId = "attachment03",
					role = PromotionConst.PRODUCT_DATA_ENGINGEER, 
					allowedOperations = {UserOperation.VIEW})
				}
			)
	String changeorder2_temp_cpsjgcssh = "changeorder2_temp_cpsjgcssh"; // 该常量仅用于承载Annotation,并没有任何实际用途
	
	@ActivityNodeDef(
			nodeId = ChangeConst.changeorder2_temp_sesh,
			name = @ResourceBundleKey,
			description = "SE&COST会签",
			phaseName = "changeorder2_temp_sesh",
			attachmentGroupTab = true,
			operationToAttachmentGroup = {
			@AllowedOperation(
				groupId = "attachment01",
				role = PromotionConst.SYSTEM_ENGINEER, 
				allowedOperations = {UserOperation.VIEW}),
			
				@AllowedOperation(
					groupId = "attachment02",
					role = PromotionConst.SYSTEM_ENGINEER,
					allowedOperations = {UserOperation.VIEW}),
				@AllowedOperation(
					groupId = "attachment03",
					role = PromotionConst.SYSTEM_ENGINEER, 
					allowedOperations = {UserOperation.VIEW})
				}
			)
	String changeorder2_temp_sesh = "changeorder2_temp_sesh"; // 该常量仅用于承载Annotation,并没有任何实际用途
	
	@ActivityNodeDef(
			nodeId = ChangeConst.changeorder2_temp_fbsapyccl,
			name = @ResourceBundleKey,
			description = "DCN发布SAP异常处理",
			phaseName = "changeorder2_temp_fbsapyccl"
			)
	String changeorder2_temp_fbsapyccl = "changeorder2_temp_fbsapyccl"; // 该常量仅用于承载Annotation,并没有任何实际用途
	
	@ActivityNodeDef(
			nodeId = ChangeConst.changeorder2_temp_qddcalcyccl,
			name = @ResourceBundleKey,
			description = "启动DCA流程异常处理",
			phaseName = "changeorder2_temp_qddcalcyccl"
			)
	String changeorder2_temp_qddcalcyccl = "changeorder2_temp_qddcalcyccl"; // 该常量仅用于承载Annotation,并没有任何实际用途
}
