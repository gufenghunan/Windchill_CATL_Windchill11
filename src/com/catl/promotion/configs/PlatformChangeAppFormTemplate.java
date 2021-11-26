package com.catl.promotion.configs;

import com.catl.promotion.dbs.PlatformChangeObjectsCollector;
import com.catl.promotion.resource.promotionResource;
import com.catl.promotion.util.PromotionConst;
import com.ptc.xworks.workflow.annotation.AccessPermissionEnum;
import com.ptc.xworks.workflow.annotation.ActivityNodeDef;
import com.ptc.xworks.workflow.annotation.PermissionToCollectedObject;
import com.ptc.xworks.workflow.annotation.ProcessPhaseDef;
import com.ptc.xworks.workflow.annotation.WorkflowTemplateDef;
import com.ptc.xworks.xmlobject.annotation.ResourceBundleKey;

@WorkflowTemplateDef(
		templateId = PromotionConst.platform_change_pn,
		templateName = "CATL_产品线标识变更流程",
		description = "CATL_产品线标识变更流程",		
		processPhaseDefs = {
				@ProcessPhaseDef(
						name = "platform_change_submit",
						description = @ResourceBundleKey(resourceBundle = PromotionConst.RESOURCE, key = promotionResource.SC_CONSTANT_30)
						),
				@ProcessPhaseDef(
						name = "platform_change_audit", 
						description = @ResourceBundleKey(resourceBundle = PromotionConst.RESOURCE, key = promotionResource.SC_CONSTANT_31)
						)
		}
		
)
public interface PlatformChangeAppFormTemplate {
	
	@ActivityNodeDef(
			nodeId = PromotionConst.platform_change_submit,
			name = @ResourceBundleKey,
			description = "调整产品线标识",
			phaseName = "platform_change_submit",
			enableApplicationForm = true,
			applicationFormJspPath = "/netmarkets/jsp/catl/promotion/inlineTaskFormPlatformChange.jsp"
			)
	String platform_change_submit = "platform_change_submit"; // 该常量仅用于承载Annotation,并没有任何实际用途
	
	@ActivityNodeDef(
			nodeId = PromotionConst.platform_change_audit,
			name = @ResourceBundleKey,
			description = "FM审核",
			phaseName = "platform_change_audit",
			permissionToCollectedObject = { @PermissionToCollectedObject(permissionTargetObjectCollector = PlatformChangeObjectsCollector.class, 
				permissions = {AccessPermissionEnum.READ}, role = PermissionToCollectedObject.ANY_ROLE) },
			enableApplicationForm = true,
			applicationFormJspPath = "/netmarkets/jsp/catl/promotion/inlineTaskFormPlatformChange.jsp"
			)
	String platform_change_audit = "platform_change_audit"; // 该常量仅用于承载Annotation,并没有任何实际用途
	

}
