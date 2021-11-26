package com.catl.line.util;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import wt.content.ApplicationData;
import wt.content.ContentHolder;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.doc.WTDocument;
import wt.part.WTPart;
import wt.util.WTException;

public class WTPartUtil {

	/**
	 * 部件，添加附件
	 * 
	 * @param doc
	 * @param filePath
	 * @throws WTException
	 * @throws FileNotFoundException
	 * @throws PropertyVetoException
	 * @throws IOException
	 * @modified: ☆joy_gb(2016年4月28日 上午1:59:04): <br>
	 */
	public static void updateAttachment(WTPart part,String filePath)
			throws WTException, FileNotFoundException, PropertyVetoException,
			IOException {
		ContentHolder ch = (ContentHolder) part;
		ApplicationData ap = ApplicationData.newApplicationData(ch);
		ap.setRole(ContentRoleType.SECONDARY);
		ap = ContentServerHelper.service.updateContent(ch, ap, filePath);
	}


}
