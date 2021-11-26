package com.catl.pdfsignet.processors;

import wt.fc.Persistable;
import wt.util.WTException;

import com.catl.pdfsignet.PDFSignetUtil;
import com.ptc.core.components.forms.DynamicRefreshInfo;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.core.ui.resources.FeedbackType;
import com.ptc.netmarkets.util.beans.NmCommandBean;

public class GenerateSignetPDFProcessor {
	
	public static FormResult generateFile(NmCommandBean clientData) throws WTException {
		FormResult result = new FormResult();
		FeedbackMessage feedbackmessage;
		Object obj = clientData.getActionOid().getRefObject();
		if(PDFSignetUtil.isAutoCADDoc(obj) || PDFSignetUtil.isCATIADrawing(obj)){
			try{
				PDFSignetUtil.printSignetAndEncryption(obj, PDFSignetUtil.getPrintImange(30, 30), PDFSignetUtil.getEncryptionPW());
			}
			catch(WTException e){
				feedbackmessage = new FeedbackMessage(FeedbackType.FAILURE, clientData.getLocale(), "", null,
	                    new String[] { "生成DC签名文件失败:"+e.getLocalizedMessage()});
				result.setStatus(FormProcessingStatus.FAILURE);
				result.addFeedbackMessage(feedbackmessage);
				return result;
			}
			feedbackmessage = new FeedbackMessage(FeedbackType.SUCCESS, clientData.getLocale(), "", null,
                    new String[] { "生成DC签名文件成功"});
			result.setStatus(FormProcessingStatus.SUCCESS);
			result.addFeedbackMessage(feedbackmessage);
			result.addDynamicRefreshInfo(new DynamicRefreshInfo((Persistable)obj, (Persistable)obj, "U"));
		}
		else {
			feedbackmessage = new FeedbackMessage(FeedbackType.FAILURE, clientData.getLocale(), "", null,
                    new String[] { "生成DC签名文件失败：不是线束AutoCAD图纸、Catia二维图纸"});
			result.setStatus(FormProcessingStatus.FAILURE);
			result.addFeedbackMessage(feedbackmessage);
		}
		return result;
	}

}
