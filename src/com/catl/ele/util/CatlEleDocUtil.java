/**
 * **********************************************************************************
 * Copyright (c) 2018, yxhu@true-u.com.cn True-u Co.,Ltd All Rights Reserved.	*
 ************************************************************************************
*/

package com.catl.ele.util;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.catl.common.constant.TypeName;
import com.catl.common.util.WCLocationConstants;

import wt.content.ApplicationData;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.doc.DocumentType;
import wt.doc.WTDocument;
import wt.fc.ObjectReference;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.folder.Folder;
import wt.folder.FolderHelper;
import wt.folder.SubFolder;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerRef;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleTemplate;
import wt.method.MethodContext;
import wt.part.WTPart;
import wt.part.WTPartDescribeLink;
import wt.pom.WTConnection;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.type.TypeDefinitionReference;
import wt.type.TypedUtility;
import wt.util.WTException;
import wt.vc.config.ConfigSpec;
import wt.vc.config.LatestConfigSpec;

/**
 * ClassName:CatlEleDocUtil Function: 为电气文档提供的工具类，包含一些电气文档需要的方法. Date:
 * 2018年7月25日 上午11:11:54
 * 
 * @author JohnnyR
 * @version
 * @since JDK 1.8
 * @see
 */
public class CatlEleDocUtil {
	/**
	 * createEleDocByWTPart:方法作用：在WTPart创建时，同时创建一个电器类文档<br/>
	 * date: 2018年7月25日 上午11:14:00
	 * 
	 * @author JohnnyR
	 * @throws Exception
	 * @since JDK 1.7
	 */
	public static void createEleDocByWTPart(WTPart part) throws Exception {
		if (part != null && EleCommonUtil.isPBUSPart(part)) {
			String docFolderPath = "/" + part.getFolderPath().split("/")[1];
			docFolderPath = docFolderPath + "/" + part.getFolderPath().split("/")[2] + "/产品资料/研发过程文档";
			WTContainer container = part.getContainer();
			String docnumber = getMaxMakeNumber("ES-%");
			if (docnumber == null) {
				throw new WTException("获取‘ES-’开头的最大流水码时发生异常，请联系管理员");
			}
			String filePath = WCLocationConstants.WT_HOME + File.separator + "codebase" + File.separator + "config"
					+ File.separator + "custom" + File.separator + "docTemplate"+ File.separator +"template-proj.tewzip";
			WTDocument doc = createDocument(
					"ES" + part.getName().substring(part.getName().indexOf("_"), part.getName().lastIndexOf("_")) + "_"
							+ part.getNumber() + "-电气原理图",
					docnumber, container, docFolderPath, filePath);
			if (doc != null) {
				WTPartDescribeLink describelink = WTPartDescribeLink.newWTPartDescribeLink(part, doc);
				PersistenceServerHelper.manager.insert(describelink);
			}
		} else {
			System.out.println("part is null / NOT PBUS part,operation abort.");
		}
	}

	/**
	 * 创建空软件文档
	 * 
	 * @param docName
	 * @param docNumber
	 * @param wtContainer
	 * @param docPath
	 * @return
	 * @throws Exception
	 */
	public static WTDocument createDocument(String docName, String docNumber, WTContainer wtContainer, String docPath,
			String filePath) throws Exception {

		// 获取文档对象
		WTDocument wtDocument = WTDocument.newWTDocument(docNumber, docName, DocumentType.getDocumentTypeDefault());
		// 为文档设置容器
		wtDocument.setContainer(wtContainer);

		// 获取文件夹对象
		Folder folder = getFolder(docPath, wtContainer);
		FolderHelper.assignLocation(wtDocument, folder);
		TypeDefinitionReference tdr = TypedUtility.getTypeDefinitionReference(TypeName.eleDoc);// 设置软类型
		wtDocument.setTypeDefinitionReference(tdr);
//		reassignLifeCycle(wtDocument, "LC_epmdoc_Cycle");
		wtDocument = (WTDocument) PersistenceHelper.manager.save(wtDocument);
		File file = new File(filePath);
		ApplicationData app = ApplicationData.newApplicationData(wtDocument);
		app.setRole(ContentRoleType.PRIMARY);
		app.setFileName(file.getName());
		app.setFileSize(file.length());

		ContentServerHelper.service.updateContent(wtDocument, app, new FileInputStream(file));
		ContentServerHelper.service.updateHolderFormat(wtDocument);

		return wtDocument;
	}

	/**
	 * 得到文件夹结构,没有的则创建
	 * 
	 * @param folderPath
	 * @param wtcontainer
	 * @return
	 * @throws Exception
	 */
	public static Folder getFolder(String folderPath, WTContainer wtcontainer) throws Exception {
		if (folderPath == null || folderPath.equals("")) {
			return null;
		}
		Folder subfolder = null;
		String folderRef = "";

		if (!folderPath.startsWith("/")) {
			folderPath = "/" + folderPath;
		}

		if (!folderPath.equalsIgnoreCase("/Default") && !folderPath.startsWith("/Default")) {
			folderPath = "/Default" + folderPath;
		}

		String nextfolder[] = folderPath.split("/");
		// System.out.println("nextfolder[]==="+nextfolder.length);
		ArrayList list = new ArrayList();
		for (int p = 0; p < nextfolder.length; p++) {
			if (nextfolder[p] != null && !nextfolder[p].trim().equals("") && !nextfolder[p].trim().equals("Default")) {
				list.add(nextfolder[p]);
			}
		}
		// System.out.println("list==="+list);
		createMultiLevelDirectory(list, WTContainerRef.newWTContainerRef(wtcontainer));

		subfolder = FolderHelper.service.getFolder(folderPath, WTContainerRef.newWTContainerRef(wtcontainer));
		if (subfolder == null) {
			folderPath = "/Default";
			subfolder = FolderHelper.service.getFolder(folderPath, WTContainerRef.newWTContainerRef(wtcontainer));
		} else {
			ReferenceFactory rf = new ReferenceFactory();
			folderRef = rf.getReferenceString(ObjectReference
					.newObjectReference(((Persistable) subfolder).getPersistInfo().getObjectIdentifier()));
		}
		return subfolder;
	}

	/**
	 * create multi-level directory
	 * 
	 * @param list:
	 * @param wtContainerRef
	 * @return
	 */
	public static Folder createMultiLevelDirectory(List<String> list, WTContainerRef wtContainerRef) {
		Folder subFolder = null;
		String path = ((WTContainer) wtContainerRef.getObject()).getDefaultCabinet().getFolderPath();
		int size = list.size();
		for (int i = 0; i < size; i++) {
			Folder folder = null;
			try {
				folder = FolderHelper.service.getFolder(path, wtContainerRef);
				path = path + "/" + list.get(i);
				QueryResult result = FolderHelper.service.findSubFolders(folder);
				if (!checkFolderExits(result, list.get(i)))
					subFolder = FolderHelper.service.createSubFolder(path, wtContainerRef);
			} catch (WTException e) {
				e.printStackTrace();
			}
		}

		if (subFolder == null) {
			try {
				Folder folder = FolderHelper.service.getFolder(path, wtContainerRef);
				subFolder = (Folder) folder;
				// System.out.println(">>>>SubFolder'Name is:" +
				// subFolder.getName());
			} catch (WTException e) {
				e.printStackTrace();
			}
		}
		return subFolder;
	}

	private static boolean checkFolderExits(QueryResult result, String str) {
		if (result == null)
			return false;
		while (result.hasMoreElements()) {
			Object obj = result.nextElement();
			if (obj instanceof SubFolder) {
				SubFolder subFolder = (SubFolder) obj;
				if (subFolder.getName().equals(str))
					return true;
			} else {
				return false;
			}
		}
		return false;
	}

	/**
	 * getMaxMakeNumber:方法作用：返回电气文档最大的正式编码 <br/>
	 * date: 2018年7月26日 下午4:09:25
	 * 
	 * @author JohnnyR
	 * @param number
	 * @return
	 * @throws Exception
	 * @since JDK 1.7
	 */
	public static String getMaxMakeNumber(String prefix) throws Exception {
		Connection conn = ((WTConnection) MethodContext.getContext().getConnection()).getConnection();
		String sql = "SELECT MAX(wtdocumentnumber) maxnumber from wtdocumentmaster  where wtdocumentnumber like ? ";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1, prefix);
		ResultSet rs = ps.executeQuery();
		String newNumber = null;
		if (rs.next()) {
			String maxnumber = rs.getString("maxnumber");
			if (maxnumber == null) {
				String formatStr = "00000000";
				DecimalFormat df = new DecimalFormat(formatStr);
				String newflowcode = df.format(1);
				newNumber = prefix.substring(0, prefix.length() - 1) + newflowcode;
				return newNumber;
			}
			String tempstr = maxnumber.substring(3);
			long flowcode = Long.parseLong(tempstr);
			Long longflowcode = flowcode + 1;
			String formatStr = "00000000";
			if (longflowcode > 99999999)
				formatStr = "000000000";
			DecimalFormat df = new DecimalFormat(formatStr);
			String newflowcode = df.format(longflowcode);
			newNumber = prefix.substring(0, prefix.length() - 1) + newflowcode;
		} else {
			String formatStr = "00000000";
			DecimalFormat df = new DecimalFormat(formatStr);
			String newflowcode = df.format(1);
			newNumber = prefix.substring(0, prefix.length() - 1) + newflowcode;
		}
		return newNumber;
	}

	public static void main(String[] args) {
		String p = "ES-%";
		String s = "产品_XLV_83.77KWh_271_LJ1B1CHBEVF8_F_电池系统";
	}

	public static void reassignLifeCycle(WTDocument doc, String lifecycleName) {
		// State state = doc.getLifeCycleState();
		LifeCycleTemplate temp = null;
		// LifeCycleHelper.service.getLifeCycleTemplate(arg0)//getLatestIteration(part.getLifeCycleTemplate().getObject().)
		temp = getLifeCycleTemplate(lifecycleName);
//		LifeCycleTemplate currentTemp = (LifeCycleTemplate) doc.getLifeCycleTemplate().getObject();
		if (temp != null ) {
			try {
				doc = (WTDocument) LifeCycleHelper.service.reassign(doc, temp.getLifeCycleTemplateReference());
				// LifeCycleHelper.service.setLifeCycleState(doc, state);
			} catch (WTException e) {
				e.printStackTrace();
			}

		}
	}

	public static LifeCycleTemplate getLifeCycleTemplate(String name) {
		LifeCycleTemplate temp = null;
		QuerySpec qs;
		try {
			qs = new QuerySpec(LifeCycleTemplate.class);
			SearchCondition sc = new SearchCondition(LifeCycleTemplate.class, LifeCycleTemplate.NAME,
					SearchCondition.EQUAL, name);
			qs.appendWhere(sc);
			QueryResult qr = PersistenceHelper.manager.find(qs);
			ConfigSpec cs = new LatestConfigSpec();
			qr = cs.process(qr);
			System.out.println(qr.size());
			while (qr != null && qr.hasMoreElements()) {
				temp = (LifeCycleTemplate) qr.nextElement();
			}

		} catch (QueryException e) {
			temp = null;
		} catch (WTException e) {
			temp = null;
		}
		return temp;
	}
}
