package com.catl.doc.soft.util;

import java.beans.PropertyVetoException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.catl.common.constant.TypeName;
import com.catl.common.util.PartUtil;
import com.catl.ecad.utils.IBAUtility;

import wt.content.ApplicationData;
import wt.content.ContentHolder;
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
import wt.part.WTPart;
import wt.part.WTPartDescribeLink;
import wt.type.TypeDefinitionReference;
import wt.type.TypedUtility;
import wt.util.WTException;

public class CatlSoftDocUtil {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public static void createSoftDocByPart(WTPart part) throws Exception{
		if(part != null && PartUtil.isSWPart(part)){
			String docFolderPath = "/" + part.getFolderPath().split("/")[1];
			docFolderPath = docFolderPath + "/" + part.getFolderPath().split("/")[2] + "/设计图档/产品类软件";
			WTContainer container = part.getContainer();
			WTDocument doc = createDocument(part.getName(), part.getNumber(), container, docFolderPath);
			if(doc != null){
				WTPartDescribeLink describelink = WTPartDescribeLink.newWTPartDescribeLink(part, doc);
				PersistenceServerHelper.manager.insert(describelink);
			}
		}else{
			System.out.println("WTPart is null!");
		}
	}
	
	public static WTDocument createDocByDoc(WTDocument olddoc,WTPart part) throws Exception{
		WTDocument doc = WTDocument.newWTDocument(part.getNumber(), part.getName(), olddoc.getDocType());
		WTContainer container = olddoc.getContainer();
		String docFolderPath = "/" + olddoc.getFolderPath().split("/")[1];
		docFolderPath = docFolderPath + "/" + olddoc.getFolderPath().split("/")[2] + "/设计图档/产品类软件";
		Folder folder = getFolder(docFolderPath, container);
		FolderHelper.assignLocation(doc, folder);
		TypeDefinitionReference tdr = TypedUtility.getTypeDefinitionReference(TypeName.softwareDoc);// 设置软类型
		doc.setTypeDefinitionReference(tdr);
		doc = (WTDocument) PersistenceHelper.manager.save(doc);
		
		ContentServerHelper.service.copyContent(olddoc, doc, true);
		return doc;
	}
	
	
	public static WTDocument createDocByPart(WTPart part,String filename,String filepath) throws Exception{
		WTDocument doc = WTDocument.newWTDocument(part.getNumber(), part.getName(), DocumentType.getDocumentTypeDefault());
		WTContainer container = part.getContainer();
		String docFolderPath = "/" + part.getFolderPath().split("/")[1];
		docFolderPath = docFolderPath + "/" + part.getFolderPath().split("/")[2] + "/设计图档/产品类软件";
		Folder folder = getFolder(docFolderPath, container);
		FolderHelper.assignLocation(doc, folder);
		TypeDefinitionReference tdr = TypedUtility.getTypeDefinitionReference(TypeName.softwareDoc);// 设置软类型
		doc.setTypeDefinitionReference(tdr);
		doc = (WTDocument) PersistenceHelper.manager.save(doc);
		
		ContentHolder ch = (ContentHolder) doc;
		ApplicationData theContent1 = ApplicationData.newApplicationData(ch);
		theContent1.setFileName(filename);
		theContent1.setRole(ContentRoleType.toContentRoleType("SOFTPACKAGE_ATTACHMENT"));
		theContent1 = ContentServerHelper.service.updateContent(ch, theContent1, filepath);
		//theContent1 = ContentServerHelper.service.updateContent(ch, theContent1, rjbin);
		doc = (WTDocument) ContentServerHelper.service.updateHolderFormat(doc);
		return doc;
	}
	
	/**
	 * 创建空软件文档
	 * @param docName
	 * @param docNumber
	 * @param wtContainer
	 * @param docPath
	 * @return
	 * @throws Exception
	 */
	public static WTDocument createDocument(String docName, String docNumber, WTContainer wtContainer, String docPath) throws Exception{

		// 获取文档对象
		WTDocument wtDocument = WTDocument.newWTDocument(docNumber, docName, DocumentType.getDocumentTypeDefault());
		// 为文档设置容器
		wtDocument.setContainer(wtContainer);

		// 获取文件夹对象
		Folder folder = getFolder(docPath, wtContainer);
		FolderHelper.assignLocation(wtDocument, folder);
		TypeDefinitionReference tdr = TypedUtility.getTypeDefinitionReference(TypeName.softwareDoc);// 设置软类型
		wtDocument.setTypeDefinitionReference(tdr);
		wtDocument = (WTDocument) PersistenceHelper.manager.save(wtDocument);
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
}
