package com.catl.change.configs;

import com.catl.change.util.ChangeConst;
import com.catl.change.resource.*;
import com.catl.promotion.dbs.DesignDisabledObjectsCollector;
import com.catl.promotion.util.PromotionConst;
import com.ptc.xworks.xmlobject.annotation.ResourceBundleKey;
import com.ptc.xworks.workflow.annotation.*;


@WorkflowTemplateDef(
		templateId = ChangeConst.chanActivit2_temp_dca,
		templateName = "CATL设计变更任务流程",
		description = "CATL设计变更任务流程",
		processPhaseDefs = {
				@ProcessPhaseDef(
						name = "changeorder2_temp_wcbgrw",
						description = @ResourceBundleKey(resourceBundle = ChangeConst.RESOURCE, key = changeActionRB.PRIVATE_CONSTANT_7)
						)
			}
		
)
public interface DcaApprovalGrantFromTempalte {
	
	@ActivityNodeDef(
			nodeId = ChangeConst.chanActivit_temp_wcbgrw,
			name = @ResourceBundleKey,
			description = "完成更改任务",
			phaseName = "chanActivit_temp_wcbgrw",
			enableApplicationForm = true,				//是否加载JSP
			applicationFormJspPath = "/netmarkets/jsp/catl/changeTask/DcnDatalistFromTemp.jsp"
			)
	String chanActivit_temp_wcbgrw = "chanActivit_temp_wcbgrw"; // 该常量仅用于承载Annotation,并没有任何实际用途
	
}
