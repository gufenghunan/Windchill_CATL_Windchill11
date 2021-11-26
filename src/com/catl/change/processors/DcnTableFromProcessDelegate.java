package com.catl.change.processors;

import java.util.*;

import wt.change2.*;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.fc.Persistable;
import wt.part.WTPart;
import wt.util.WTException;
import wt.util.WTMessage;
import wt.workflow.work.WorkItem;

import com.catl.change.resource.changeActionRB;
import com.catl.change.util.ChangeConst;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessorDelegate;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.core.ui.resources.FeedbackType;
import com.ptc.netmarkets.util.beans.NmCommandBean;

public class DcnTableFromProcessDelegate extends DefaultObjectFormProcessorDelegate{

	public FormResult postProcess(NmCommandBean clientData, List<ObjectBean> paramList)
		    throws WTException{
		FormResult result = new FormResult();
		result.setStatus(FormProcessingStatus.SUCCESS);
		
		Map checkmp = clientData.getChecked();
		if(checkmp.size()==0){
			result.setStatus(FormProcessingStatus.FAILURE);
			FeedbackMessage feedbackmessage = new FeedbackMessage(FeedbackType.CONFIRMATION,
					clientData.getLocale(), null, null, WTMessage.getLocalizedMessage(ChangeConst.RESOURCE,
							changeActionRB.NOT_ISCHECKINFO, null, clientData.getLocale()));
			result.addFeedbackMessage(feedbackmessage);
		}
		
	    return result;
	    
	  }
}
