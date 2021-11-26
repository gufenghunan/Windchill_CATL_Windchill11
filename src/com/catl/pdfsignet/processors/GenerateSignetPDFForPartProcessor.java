package com.catl.pdfsignet.processors;

import java.util.HashSet;
import java.util.Set;

import wt.fc.Persistable;
import wt.fc.collections.WTArrayList;
import wt.part.WTPart;
import wt.part.WTPartConfigSpec;
import wt.part.WTPartHelper;
import wt.part.WTPartStandardConfigSpec;
import wt.util.WTException;

import com.catl.pdfsignet.PDFSignetUtil;
import com.itextpdf.text.Image;
import com.ptc.core.components.forms.DynamicRefreshInfo;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.core.ui.resources.FeedbackType;
import com.ptc.netmarkets.util.beans.NmCommandBean;

public class GenerateSignetPDFForPartProcessor {
	
	public static FormResult generateFile(NmCommandBean clientData) throws WTException {
		FormResult result = new FormResult();
		FeedbackMessage feedbackmessage;
		Object obj = clientData.getActionOid().getRefObject();
		if(obj instanceof WTPart){
			WTPart part = (WTPart)obj;
			Image image = PDFSignetUtil.getPrintImange(30, 30);
			String pass = PDFSignetUtil.getEncryptionPW();
			try{
				PDFSignetUtil.printSignetAndEncryptionFromPart(part, image, pass);
				Set<WTPart> allChildren = new HashSet<WTPart>();
				WTArrayList parents = new WTArrayList();
				parents.add(part);
				WTPartConfigSpec config = WTPartHelper.service.findWTPartConfigSpec();
				WTPartStandardConfigSpec standardConfig = config.getStandard();
				fetchAllChildren(allChildren, parents, standardConfig);
				for (WTPart child : allChildren) {
					PDFSignetUtil.printSignetAndEncryptionFromPart(child, image, pass);
				}
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
		return result;
	}
	
	private static void fetchAllChildren(Set<WTPart> allChildren,WTArrayList parents,WTPartStandardConfigSpec standardConfig) throws WTException{
		WTArrayList children = new WTArrayList();
		Persistable[][][] linkInfos = WTPartHelper.service.getUsesWTParts(parents, standardConfig);
		for (Persistable[][] linkInfo : linkInfos) {
			if (linkInfo == null) {
				continue;
			}
			for (Persistable[] childInfo : linkInfo) {
				if(childInfo == null){
					continue;
				}
				WTPart child = (WTPart) childInfo[1];
				allChildren.add(child);
				children.add(child);
			}
		}
		if (children.size() > 0) {
			fetchAllChildren(allChildren, children, standardConfig);
		}
	}
}
