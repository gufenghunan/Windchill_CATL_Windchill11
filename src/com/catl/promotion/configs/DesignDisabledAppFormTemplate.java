package com.catl.promotion.configs;

import com.catl.promotion.dbs.DesignDisabledObjectsCollector;
import com.catl.promotion.resource.promotionResource;
import com.catl.promotion.util.PromotionConst;
import com.ptc.xworks.workflow.annotation.AccessPermissionEnum;
import com.ptc.xworks.workflow.annotation.ActivityNodeDef;
import com.ptc.xworks.workflow.annotation.PermissionToCollectedObject;
import com.ptc.xworks.workflow.annotation.ProcessPhaseDef;
import com.ptc.xworks.workflow.annotation.WorkflowTemplateDef;
import com.ptc.xworks.xmlobject.annotation.ResourceBundleKey;

@WorkflowTemplateDef(
		templateId = PromotionConst.design_disable_pn,
		templateName = "CATL_物料设计禁用单流程",
		description = "CATL_物料设计禁用单流程",		
		processPhaseDefs = {
				@ProcessPhaseDef(
						name = "design_disable_submit",
						description = @ResourceBundleKey(resourceBundle = PromotionConst.RESOURCE, key = promotionResource.PRIVATE_CONSTANT_31)
						),
				@ProcessPhaseDef(
						name = "design_disable_confirm", 
						description = @ResourceBundleKey(resourceBundle = PromotionConst.RESOURCE, key = promotionResource.PRIVATE_CONSTANT_32)
						),
				@ProcessPhaseDef(
						name = "design_disable_engineer",
						description = @ResourceBundleKey(resourceBundle = PromotionConst.RESOURCE, key = promotionResource.PRIVATE_CONSTANT_33)
						),
				@ProcessPhaseDef(
						name = "design_disable_pmc", 
						description = @ResourceBundleKey(resourceBundle = PromotionConst.RESOURCE, key = promotionResource.PRIVATE_CONSTANT_34)
						),
				@ProcessPhaseDef(
						name = "design_disable_src", 
						description = @ResourceBundleKey(resourceBundle = PromotionConst.RESOURCE, key = promotionResource.PRIVATE_CONSTANT_35)
						),
				@ProcessPhaseDef(
						name = "design_disable_other", 
						description = @ResourceBundleKey(resourceBundle = PromotionConst.RESOURCE, key = promotionResource.PRIVATE_CONSTANT_100)
						),
				@ProcessPhaseDef(
						name = "design_disable_exception",
						description = @ResourceBundleKey(resourceBundle = PromotionConst.RESOURCE, key = promotionResource.PRIVATE_CONSTANT_36)
						),
				@ProcessPhaseDef(
						name = "design_disable_update", 
						description = @ResourceBundleKey(resourceBundle = PromotionConst.RESOURCE, key = promotionResource.PRIVATE_CONSTANT_37)
						)
			}
		
)
public interface DesignDisabledAppFormTemplate {
	
	@ActivityNodeDef(
			nodeId = PromotionConst.design_disable_submit,
			name = @ResourceBundleKey,
			description = "提交设计禁用",
			phaseName = "design_disable_submit",
			enableApplicationForm = true,
			applicationFormJspPath = "/netmarkets/jsp/catl/promotion/inlineTaskFormDesignDisabled.jsp"
			)
	String design_disable_submit = "design_disable_submit"; // 该常量仅用于承载Annotation,并没有任何实际用途
	
	@ActivityNodeDef(
			nodeId = PromotionConst.design_disable_confirm,
			name = @ResourceBundleKey,
			description = "确认设计禁用",
			phaseName = "design_disable_confirm",
			enableApplicationForm = true,
			applicationFormJspPath = "/netmarkets/jsp/catl/promotion/inlineTaskFormDesignDisabled.jsp"
			)
	String design_disable_confirm = "design_disable_confirm"; // 该常量仅用于承载Annotation,并没有任何实际用途
	
	@ActivityNodeDef(
			nodeId = PromotionConst.design_disable_engineer,
			name = @ResourceBundleKey,
			description = "设计工程师会签",
			phaseName = "design_disable_engineer",
			permissionToCollectedObject = { @PermissionToCollectedObject(permissionTargetObjectCollector = DesignDisabledObjectsCollector.class, 
				permissions = {AccessPermissionEnum.READ}, role = PermissionToCollectedObject.ANY_ROLE) },
			enableApplicationForm = true,
			applicationFormJspPath = "/netmarkets/jsp/catl/promotion/inlineTaskFormDesignDisabled.jsp"
			)
	String design_disable_engineer = "design_disable_engineer"; // 该常量仅用于承载Annotation,并没有任何实际用途
	
	@ActivityNodeDef(
			nodeId = PromotionConst.design_disable_pmc,
			name = @ResourceBundleKey,
			description = "PMC会签",
			phaseName = "design_disable_pmc",
			permissionToCollectedObject = { @PermissionToCollectedObject(permissionTargetObjectCollector = DesignDisabledObjectsCollector.class, 
				permissions = {AccessPermissionEnum.READ}, role = PermissionToCollectedObject.ANY_ROLE) },
			enableApplicationForm = true,
			applicationFormJspPath = "/netmarkets/jsp/catl/promotion/inlineTaskFormDesignDisabled.jsp"
			)
	String design_disable_pmc = "design_disable_pmc"; // 该常量仅用于承载Annotation,并没有任何实际用途
	
	@ActivityNodeDef(
			nodeId = PromotionConst.design_disable_src,
			name = @ResourceBundleKey,
			description = "SRC会签",
			phaseName = "design_disable_src",
			permissionToCollectedObject = { @PermissionToCollectedObject(permissionTargetObjectCollector = DesignDisabledObjectsCollector.class, 
				permissions = {AccessPermissionEnum.READ}, role = PermissionToCollectedObject.ANY_ROLE) },
			enableApplicationForm = true,
			applicationFormJspPath = "/netmarkets/jsp/catl/promotion/inlineTaskFormDesignDisabled.jsp"
			)
	String design_disable_src = "design_disable_src"; // 该常量仅用于承载Annotation,并没有任何实际用途
	
	@ActivityNodeDef(
			nodeId = PromotionConst.design_disable_other,
			name = @ResourceBundleKey,
			description = "其他会签",
			phaseName = "design_disable_other",
			permissionToCollectedObject = { @PermissionToCollectedObject(permissionTargetObjectCollector = DesignDisabledObjectsCollector.class, 
				permissions = {AccessPermissionEnum.READ}, role = PermissionToCollectedObject.ANY_ROLE) },
			enableApplicationForm = true,
			applicationFormJspPath = "/netmarkets/jsp/catl/promotion/inlineTaskFormDesignDisabled.jsp"
			)
	String design_disable_other = "design_disable_other"; // 该常量仅用于承载Annotation,并没有任何实际用途
	
	@ActivityNodeDef(
			nodeId = PromotionConst.design_disable_exception,
			name = @ResourceBundleKey,
			description = "处理设计禁用集成异常",
			phaseName = "design_disable_exception"
			)
	String design_disable_exception = "design_disable_exception"; // 该常量仅用于承载Annotation,并没有任何实际用途
	
	@ActivityNodeDef(
			nodeId = PromotionConst.design_disable_update,
			name = @ResourceBundleKey,
			description = "更新电子元器件库",
			phaseName = "design_disable_update",
			enableApplicationForm = true,
			applicationFormJspPath = "/netmarkets/jsp/catl/promotion/inlineTaskFormDesignDisabled.jsp"
			)
	String design_disable_update = "design_disable_update"; // 该常量仅用于承载Annotation,并没有任何实际用途
}
