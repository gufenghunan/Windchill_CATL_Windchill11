package com.catl.line.filter;


import wt.doc.WTDocument;
import wt.fc.Persistable;
import wt.fc.QueryResult;
import wt.org.WTPrincipal;
import wt.part.WTPart;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.vc.config.LatestConfigSpec;
import wt.vc.wip.WorkInProgressHelper;

import com.catl.common.constant.DocState;
import com.catl.common.util.DocUtil;
import com.catl.common.util.PartUtil;
import com.catl.common.util.PropertiesUtil;
import com.catl.common.util.UserUtil;
import com.catl.pdfsignet.PDFSignetUtil;
import com.ptc.core.ui.validation.DefaultSimpleValidationFilter;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationStatus;
import com.ptc.windchill.enterprise.part.commands.PartDocServiceCommand;

public class CATLCreateBoxExplainFilter extends DefaultSimpleValidationFilter{

	@Override
	public UIValidationStatus preValidateAction(UIValidationKey key,
			UIValidationCriteria criteria) {
		UIValidationStatus status = UIValidationStatus.HIDDEN;
		try {
			Persistable persistable = criteria.getContextObject().getObject();
			if(PDFSignetUtil.isPart(persistable)){
				WTPart part = (WTPart)persistable;
				String partNumber = part.getNumber();
				String partState = part.getState().toString();
				
				String groupLimit = PropertiesUtil.getValueByKey("create_box_explain_group");
				String stateLimit = PropertiesUtil.getValueByKey("create_box_explain_part_state");
				if(partNumber.startsWith(groupLimit) && stateLimit.contains(partState+",")){//物料组 状态是否符合
					if(!WorkInProgressHelper.isCheckedOut(part) && PartUtil.isLastedWTPart(part)){//不能是检出 必须是最新版本
						status = UIValidationStatus.ENABLED;
						QueryResult cadresult = PartDocServiceCommand.getAssociatedDescribeDocuments(part);
						cadresult = new LatestConfigSpec().process(cadresult);
						while (cadresult.hasMoreElements()) {
							WTDocument document = (WTDocument) cadresult.nextElement();
							if(document.getNumber().equals(part.getNumber())){
								String doctype = DocUtil.getObjectType(document);
								if (doctype.endsWith("autocadDrawing") && document.getState().toString().equals(DocState.RELEASED)) {//autocad已经是已发布 隐藏
									status = UIValidationStatus.HIDDEN;
									break;
								}
							}
						}
					}
				}
			}
			
			WTPrincipal principal = SessionHelper.getPrincipal();
			if(UserUtil.isOrgAdministator(principal) || UserUtil.isSiteAdmin(principal)){
				return UIValidationStatus.ENABLED;
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return status;
	}
	
}
