package com.catl.doc.processor;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import wt.doc.WTDocument;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.catl.common.constant.TypeName;
import com.catl.common.util.DocUtil;
import com.catl.doc.DocInvalidUtil;
import com.catl.doc.workflow.DocWfUtil;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.EditWorkableFormProcessor;
import com.ptc.core.components.forms.FormResult;
import com.ptc.netmarkets.util.beans.NmCommandBean;

public class CatlDocEditProcessor extends EditWorkableFormProcessor {

	private static Logger logger = Logger.getLogger(CatlDocEditProcessor.class.getName());

	@Override
	public FormResult postProcess(NmCommandBean nmcommandBean, List<ObjectBean> list) throws WTException {
		FormResult result = super.postProcess(nmcommandBean, list);
		StringBuffer message = new StringBuffer();
		
		WTDocument doc = (WTDocument) list.get(0).getObject();
		String docType = DocUtil.getObjectType(doc);
		if(docType != null && (docType.contains(TypeName.technicalDoc) || docType.contains(TypeName.rdDoc)
				|| docType.contains(TypeName.pcbaDrawing) || docType.contains(TypeName.gerberDoc) || docType.contains(TypeName.kopdmdoc)))
			message.append(DocWfUtil.checkSubmit(list));
		
		//文档失效流程
		if(docType != null && docType.contains(TypeName.technicalDoc)) {
			
			//ENW文档，预计失效日期为必填
			message.append(DocInvalidUtil.checkENWSubmit(list));
		}
		
		if (message.length() > 0) {
			throw new WTException("错误信息：" + message);
		}
		
		return result;
	}
}
