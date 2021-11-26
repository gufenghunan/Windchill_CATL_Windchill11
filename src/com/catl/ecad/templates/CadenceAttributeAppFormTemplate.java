package com.catl.ecad.templates;

import com.ptc.xworks.workflow.annotation.ActivityNodeDef;
import com.ptc.xworks.workflow.annotation.WorkflowTemplateDef;
import com.ptc.xworks.xmlobject.annotation.ResourceBundleKey;

@WorkflowTemplateDef(templateId = "CADENCE_01", templateName = "CIS_Library_Apply_WF", description = "CIS_Library_Apply_WF")
public interface CadenceAttributeAppFormTemplate{	
	
	/**
	 * 创建元器件Symbol
	 */
	@ActivityNodeDef(
		nodeId = "EDIT_CADENCEATTRS",
		name = @ResourceBundleKey,
		description = "创建元器件Symbol",
		enableApplicationForm = true,
		applicationFormJspPath = "/netmarkets/jsp/catl/ecad/cadenceAttributeForm.jsp"

	)
	String EDIT_CADENCEATTRS = "EDIT_CADENCEATTRS"; // 该常量仅用于承载Annotation,并没有任何实际用途
	
	/**
	 * 修改元器件Symbol
	 */
	@ActivityNodeDef(
		nodeId = "UPDATE_CADENCEATTRS",
		name = @ResourceBundleKey,
		description = "修改元器件Symbol",
		enableApplicationForm = true,
		applicationFormJspPath = "/netmarkets/jsp/catl/ecad/cadenceAttributeForm.jsp"

	)
	String UPDATE_CADENCEATTRS = "UPDATE_CADENCEATTRS"; // 该常量仅用于承载Annotation,并没有任何实际用途
	
	/**
	 * 校验元器件Symbol
	 */
	@ActivityNodeDef(
		nodeId = "REVIEW_CADENCEATTRS",
		name = @ResourceBundleKey,
		description = "校验元器件Symbol",
		enableApplicationForm = true,
		applicationFormJspPath = "/netmarkets/jsp/catl/ecad/cadenceAttributeForm.jsp"
	)
	String REVIEW_CADENCEATTRS = "REVIEW_CADENCEATTRS"; // 该常量仅用于承载Annotation,并没有任何实际用途
	
}
