package com.catl.promotion.configs;

import com.catl.promotion.dbs.SourceChangeObjectsCollector;
import com.catl.promotion.resource.promotionResource;
import com.catl.promotion.util.PromotionConst;
import com.ptc.xworks.workflow.annotation.AccessPermissionEnum;
import com.ptc.xworks.workflow.annotation.ActivityNodeDef;
import com.ptc.xworks.workflow.annotation.PermissionToCollectedObject;
import com.ptc.xworks.workflow.annotation.ProcessPhaseDef;
import com.ptc.xworks.workflow.annotation.WorkflowTemplateDef;
import com.ptc.xworks.xmlobject.annotation.ResourceBundleKey;

@WorkflowTemplateDef(
		templateId = PromotionConst.source_change_pn,
		templateName = "CATL_采购类型变更流程",
		description = "CATL_采购类型变更流程",		
		processPhaseDefs = {
				@ProcessPhaseDef(
						name = "source_change_submit",
						description = @ResourceBundleKey(resourceBundle = PromotionConst.RESOURCE, key = promotionResource.SC_CONSTANT_1)
						),
				@ProcessPhaseDef(
						name = "source_change_src", 
						description = @ResourceBundleKey(resourceBundle = PromotionConst.RESOURCE, key = promotionResource.SC_CONSTANT_2)
						),			
				@ProcessPhaseDef(
						name = "source_change_pmc", 
						description = @ResourceBundleKey(resourceBundle = PromotionConst.RESOURCE, key = promotionResource.SC_CONSTANT_3)
						),
				@ProcessPhaseDef(
						name = "source_change_ie",
						description = @ResourceBundleKey(resourceBundle = PromotionConst.RESOURCE, key = promotionResource.SC_CONSTANT_4)
						),
				@ProcessPhaseDef(
						name = "source_change_exception",
						description = @ResourceBundleKey(resourceBundle = PromotionConst.RESOURCE, key = promotionResource.SC_CONSTANT_5)
						)
			}
		
)
public interface SourceChangeAppFormTemplate {
	
	@ActivityNodeDef(
			nodeId = PromotionConst.source_change_submit,
			name = @ResourceBundleKey,
			description = "调整采购类型",
			phaseName = "source_change_submit",
			enableApplicationForm = true,
			applicationFormJspPath = "/netmarkets/jsp/catl/promotion/inlineTaskFormSourceChange.jsp"
			)
	String source_change_submit = "source_change_submit"; // 该常量仅用于承载Annotation,并没有任何实际用途
	
	@ActivityNodeDef(
			nodeId = PromotionConst.source_change_src,
			name = @ResourceBundleKey,
			description = "SRC会签",
			phaseName = "source_change_src",
			permissionToCollectedObject = { @PermissionToCollectedObject(permissionTargetObjectCollector = SourceChangeObjectsCollector.class, 
				permissions = {AccessPermissionEnum.READ}, role = PermissionToCollectedObject.ANY_ROLE) },
			enableApplicationForm = true,
			applicationFormJspPath = "/netmarkets/jsp/catl/promotion/inlineTaskFormSourceChange.jsp"
			)
	String source_change_src = "source_change_src"; // 该常量仅用于承载Annotation,并没有任何实际用途
	
	@ActivityNodeDef(
			nodeId = PromotionConst.source_change_pmc,
			name = @ResourceBundleKey,
			description = "PMC会签",
			phaseName = "source_change_pmc",
			permissionToCollectedObject = { @PermissionToCollectedObject(permissionTargetObjectCollector = SourceChangeObjectsCollector.class, 
				permissions = {AccessPermissionEnum.READ}, role = PermissionToCollectedObject.ANY_ROLE) },
			enableApplicationForm = true,
			applicationFormJspPath = "/netmarkets/jsp/catl/promotion/inlineTaskFormSourceChange.jsp"
			)
	String source_change_pmc = "source_change_pmc"; // 该常量仅用于承载Annotation,并没有任何实际用途
	
	@ActivityNodeDef(
			nodeId = PromotionConst.source_change_ie,
			name = @ResourceBundleKey,
			description = "IEC会签",
			phaseName = "source_change_ie",
			permissionToCollectedObject = { @PermissionToCollectedObject(permissionTargetObjectCollector = SourceChangeObjectsCollector.class, 
				permissions = {AccessPermissionEnum.READ}, role = PermissionToCollectedObject.ANY_ROLE) },
			enableApplicationForm = true,
			applicationFormJspPath = "/netmarkets/jsp/catl/promotion/inlineTaskFormSourceChange.jsp"
			)
	String source_change_ie = "source_change_ie"; // 该常量仅用于承载Annotation,并没有任何实际用途
	
	@ActivityNodeDef(
			nodeId = PromotionConst.source_change_exception,
			name = @ResourceBundleKey,
			description = "发送采购类型到ERP异常处理",
			phaseName = "source_change_exception"
			)
	String source_change_exception = "source_change_exception"; // 该常量仅用于承载Annotation,并没有任何实际用途
	
}
