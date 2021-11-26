package com.catl.common.util;

import java.util.StringTokenizer;

import wt.folder.Folder;
import wt.folder.FolderEntry;
import wt.folder.FolderHelper;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerRef;
import wt.util.WTException;

import com.ptc.windchill.cadx.common.util.FolderUtilities;

public class CatlFolderUtil {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}	
	public static Folder getFolder(String path, WTContainer con) throws WTException
	{
		Folder folder = null;
		StringTokenizer tokenizer = new StringTokenizer(path, "/");
		String subPath = "";
		while (tokenizer.hasMoreTokens())
		{
			String token = tokenizer.nextToken();
			subPath = subPath + "/" + token;
			if (FolderUtilities.doesFolderExist(subPath, con))
			{
				folder = FolderHelper.service.getFolder(subPath, WTContainerRef.newWTContainerRef(con));
			} else
			{
				folder = FolderHelper.service.createSubFolder(subPath, WTContainerRef.newWTContainerRef(con));
			}
		}
		return folder;
	}
	
	public static String getLocationString(FolderEntry entry) throws WTException{
		if(entry != null){
			Folder folder = FolderHelper.getFolder(entry);
			String folderPatch = folder.getFolderPath();
			String containerName = folder.getContainerName();
			return folderPatch.replaceFirst("Default", containerName);
		}
		return "";
	}

}
