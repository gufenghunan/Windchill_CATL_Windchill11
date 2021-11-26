package com.catl.line.util;

import java.beans.PropertyVetoException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.catl.line.constant.ConstantLine;

import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentItem;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.content.FormatContentHolder;
import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.doc.WTDocumentMasterIdentity;
import wt.doc.WTDocumentUsageLink;
import wt.fc.IdentityHelper;
import wt.fc.ObjectNoLongerExistsException;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.fc.collections.WTHashSet;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.util.WTRuntimeException;
import wt.vc.struct.StructHelper;
public class WTDocumentUtil implements RemoteAccess{

	/**
	 * 修改WTDocument名称
	 * 
	 * @param doc
	 * @param newName
	 * @return
	 * @throws ObjectNoLongerExistsException
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 * @modified: ☆joy_gb(2016年1月14日 下午3:01:11): <br>
	 */
	public static WTDocument changeDocName(WTDocument doc, String newName)
			throws ObjectNoLongerExistsException, WTException,
			WTPropertyVetoException {
		WTDocumentMaster master = (WTDocumentMaster) doc.getMaster();
		WTDocumentMasterIdentity identity = (WTDocumentMasterIdentity) master
				.getIdentificationObject();
		identity.setName(newName);
		IdentityHelper.service.changeIdentity(master, identity);
		return (WTDocument) PersistenceHelper.manager.refresh(doc);
	}

	/**
	 * 获取下载链接
	 * 
	 * @param doc
	 * @return
	 * @throws ObjectNoLongerExistsException
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 * @modified: ☆joy_gb(2016年1月14日 下午3:01:11): <br>
	 */
	public static URL getDownloadUrl(WTDocument doc) throws WTException, PropertyVetoException {
		ContentHolder holder = ContentHelper.service.getContents(doc);
		ContentItem contentitem = ContentHelper
				.getPrimary((FormatContentHolder) holder);
		ApplicationData appData = (ApplicationData) contentitem;
		return ContentHelper.getDownloadURL(holder, appData, false);
	}

	/**
	 * 获取文档主内容
	 * 
	 * @param doc
	 * @return
	 * @throws WTException
	 * @throws PropertyVetoException
	 * @modified: ☆joy_gb(2015年9月15日 下午7:10:17): <br>
	 */
	public static ApplicationData getDocPrimaryApplicationData(WTDocument doc)
			throws WTException, PropertyVetoException {
		ApplicationData appData = null;
		ContentHolder contentHolder = ContentHelper.service.getContents(doc);
		ContentItem contentitem = ContentHelper
				.getPrimary((FormatContentHolder) contentHolder);
		appData = (ApplicationData) contentitem;
		return appData;
	}

	/**
	 * 获取文档附件
	 * 
	 * @param doc
	 * @return
	 * @throws WTException
	 * @throws PropertyVetoException
	 * @modified: ☆joy_gb(2015年9月15日 下午7:10:17): <br>
	 */
	public static List getDocSecondApplicationData(WTDocument doc)
			throws WTException, PropertyVetoException {
		List appdatas=new ArrayList();
		ContentHolder contentHolder = ContentHelper.service.getContents(doc);
		QueryResult contentitems = ContentHelper.service.getContentsByRole(contentHolder, ContentRoleType.SECONDARY);
		while(contentitems.hasMoreElements()){
			ContentItem contentitem=(ContentItem) contentitems.nextElement();
			if(contentitem instanceof ApplicationData){
				ApplicationData appData = (ApplicationData) contentitem;
				appdatas.add(appData);
			}
		}
		
		return appdatas;
	}
	/**
	 * 获取文档主文件文件流
	 * 
	 * @param doc
	 * @return
	 * @throws WTException
	 * @throws IOException
	 * @throws PropertyVetoException
	 * @modified: ☆joy_gb(2016年1月26日 下午2:23:48): <br>
	 */
	public static InputStream downloadDocPrimaryStream(WTDocument doc)
			throws WTException, IOException, PropertyVetoException {
		ContentHolder contentholder = ContentHelper.service.getContents(doc);
		ContentItem contentitem = ContentHelper
				.getPrimary((FormatContentHolder) contentholder);// 得到主文档
		ApplicationData applicationdata = (ApplicationData) contentitem;
		return ContentServerHelper.service.findContentStream(applicationdata);
	}

	/**
	 * 下载主文件dwg完整路径
	 * 
	 * @param doc
	 * @return
	 * @throws WTException
	 * @throws IOException
	 * @throws PropertyVetoException
	 * @modified: ☆joy_gb(2015年10月26日 下午7:33:07): <br>
	 */
	public static ApplicationData downloadDocPrimaryDwg(WTDocument doc,String filePath,String filename)
			throws WTException, IOException, PropertyVetoException {
		ContentHolder contentholder = ContentHelper.service.getContents(doc);
		ContentItem contentitem = ContentHelper
				.getPrimary((FormatContentHolder) contentholder);// 得到主文档
		ApplicationData applicationdata=null;
		if(contentitem!=null){
			applicationdata= (ApplicationData) contentitem;
			if(applicationdata.getFileName().contains(".dwg")){
				filePath=filePath+filename;
				ContentServerHelper.service.writeContentStream(applicationdata,filePath);
				return applicationdata;
			}
		}
		return null;
		
	}

	/**
	 * 下载主文件dwg完整路径
	 * 
	 * @param doc
	 * @return
	 * @throws WTException
	 * @throws IOException
	 * @throws PropertyVetoException
	 * @modified: ☆joy_gb(2015年10月26日 下午7:33:07): <br>
	 */
	public static ApplicationData downloadDocPrimaryDwg(WTDocument doc,String filePath)
			throws WTException, IOException, PropertyVetoException {
		ContentHolder contentholder = ContentHelper.service.getContents(doc);
		ContentItem contentitem = ContentHelper
				.getPrimary((FormatContentHolder) contentholder);// 得到主文档
		ApplicationData applicationdata=null;
		if(contentitem!=null){
			applicationdata= (ApplicationData) contentitem;
			if(applicationdata.getFileName().contains(".dwg")){
				filePath=filePath+applicationdata.getFileName();
				ContentServerHelper.service.writeContentStream(applicationdata,filePath);
				return applicationdata;
			}
		}
		return null;
		
	}
	
	/**
	 * 下载附件完整路径
	 * 
	 * @param doc
	 * @return
	 * @throws WTException
	 * @throws IOException
	 * @throws PropertyVetoException
	 * @modified: ☆joy_gb(2015年10月26日 下午7:33:07): <br>
	 */
	public static String getViewAttachURL(WTDocument doc)
			throws WTException, IOException, PropertyVetoException {
		 QueryResult qr = ContentHelper.service.getContentsByRole(doc,
				ContentRoleType.SECONDARY);
		 ApplicationData ap=null;
			while (qr.hasMoreElements()) {
				Object o = qr.nextElement();
				if ((o instanceof ApplicationData)) {
					ap = (ApplicationData) o;
					qr = null;
					break;
				}
			}
			if(ap!=null){
				URL url=ap.getViewContentURL(doc);
			     return url.getHost()+url.getPath()+"?"+url.getQuery();
			}
			return "";
		
	}
	
	/**
	 * 下载附件完整路径
	 * 
	 * @param doc
	 * @return
	 * @throws WTException
	 * @throws IOException
	 * @throws PropertyVetoException
	 * @modified: ☆joy_gb(2015年10月26日 下午7:33:07): <br>
	 */
	public static String getViewPrimaryURL(WTDocument doc)
			throws WTException, IOException, PropertyVetoException {
		 QueryResult qr = ContentHelper.service.getContentsByRole(doc,
				ContentRoleType.PRIMARY);
		 ApplicationData ap=null;
			while (qr.hasMoreElements()) {
				Object o = qr.nextElement();
				if ((o instanceof ApplicationData)) {
					ap = (ApplicationData) o;
					qr = null;
					break;
				}
			}
			if(ap!=null){
				URL url=ap.getViewContentURL(doc);
			     return url.getHost()+url.getPath()+"?"+url.getQuery();
			}
			return "";
		
	}
    public static void test() throws WTRuntimeException, WTException, IOException, PropertyVetoException {
    	WTDocument doc=(WTDocument) WCUtil.getWTObject("VR:wt.doc.WTDocument:155721");
		updateAttachment(doc, "图框.PDF", "E://a.pdf");
	}
    
	/**
	 * 文档，添加附件
	 * 
	 * @param doc
	 * @param filePath
	 * @throws WTException
	 * @throws FileNotFoundException
	 * @throws PropertyVetoException
	 * @throws IOException
	 * @modified: ☆joy_gb(2016年4月28日 上午1:59:04): <br>
	 */
	public static void updateAttachment(WTDocument doc,String attname,String filePath)
			throws WTException, FileNotFoundException, PropertyVetoException,
			IOException {
		ContentHolder contentHolder = ContentHelper.service.getContents(doc);
		QueryResult contentitems = ContentHelper.service.getContentsByRole(contentHolder, ContentRoleType.SECONDARY);
		while(contentitems.hasMoreElements()){
			ContentItem contentitem=(ContentItem) contentitems.nextElement();
			if(contentitem instanceof ApplicationData){
				ApplicationData appData = (ApplicationData) contentitem;
				String name=appData.getFileName();
				if(name.equals(attname)){
					ContentServerHelper.service.deleteContent(contentHolder, contentitem);
				}
			}
		}
		ContentHolder ch = (ContentHolder) doc;
		ApplicationData ap = ApplicationData.newApplicationData(ch);
		ap.setRole(ContentRoleType.SECONDARY);
		ap = ContentServerHelper.service.updateContent(ch, ap, filePath);
	}
	
	/**
	 * 判断是否有附件
	 * 
	 * @param doc
	 * @param filePath
	 * @throws WTException
	 * @throws FileNotFoundException
	 * @throws PropertyVetoException
	 * @throws IOException
	 * @modified: ☆joy_gb(2016年4月28日 上午1:59:04): <br>
	 */
	public static boolean haveAttachment(WTDocument doc,String attname)
			throws WTException, FileNotFoundException, PropertyVetoException,
			IOException {
		ContentHolder contentHolder = ContentHelper.service.getContents(doc);
		QueryResult contentitems = ContentHelper.service.getContentsByRole(contentHolder, ContentRoleType.SECONDARY);
		while(contentitems.hasMoreElements()){
			ContentItem contentitem=(ContentItem) contentitems.nextElement();
			if(contentitem instanceof ApplicationData){
				ApplicationData appData = (ApplicationData) contentitem;
				String name=appData.getFileName();
				if(name.equals(attname)){
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 *  * 文档，删除附件，并添加一个新的附件
	 * 
	 * @param doc	文档
	 * @param attname 要删除的附件名称
	 * @param filePath	添加附件的地址
	 * @param newAttname	添加附件新的名称  如果等于空 则按默认名称添加
	 * @throws WTException
	 * @throws FileNotFoundException
	 * @throws PropertyVetoException
	 * @throws IOException
	 */
	public static void updateAttachment(WTDocument doc,String attname,String filePath,String newAttname)
			throws WTException, FileNotFoundException, PropertyVetoException,
			IOException {
		ContentHolder contentHolder = ContentHelper.service.getContents(doc);
		QueryResult contentitems = ContentHelper.service.getContentsByRole(contentHolder, ContentRoleType.SECONDARY);
		while(contentitems.hasMoreElements()){
			ContentItem contentitem=(ContentItem) contentitems.nextElement();
			if(contentitem instanceof ApplicationData){
				ApplicationData appData = (ApplicationData) contentitem;
				String name=appData.getFileName();
				if(name.equals(attname)){
					ContentServerHelper.service.deleteContent(contentHolder, contentitem);
				}
			}
		}
		ContentHolder ch = (ContentHolder) doc;
		ApplicationData ap = ApplicationData.newApplicationData(ch);
		ap.setRole(ContentRoleType.SECONDARY);
		ap = ContentServerHelper.service.updateContent(ch, ap, filePath);
		if(newAttname != null){
			ap.setFileName(newAttname);
			PersistenceHelper.manager.save(ap);
		}
	}
	
	/**
	 * 修改文档 主内容
	 * 
	 * @param doc
	 * @param fis
	 * @param fileName
	 * @param fileSize
	 * @return
	 * @throws WTException
	 * @throws PropertyVetoException
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @modified: ☆joy_gb(2015年10月21日 下午2:16:18): <br>
	 */
	public static WTDocument replaceDocPrimaryContent(WTDocument doc,
			InputStream fis, String fileName, long fileSize)
			throws WTException, PropertyVetoException, FileNotFoundException,
			IOException {
		ContentHolder contentHolder = ContentHelper.service.getContents(doc);
		ContentItem contentitem = ContentHelper.getPrimary((FormatContentHolder) contentHolder);
		ContentServerHelper.service.deleteContent(contentHolder, contentitem);
		ApplicationData app = ApplicationData.newApplicationData(doc);
		app.setRole(ContentRoleType.PRIMARY);
		app.setFileName(fileName);
		app.setFileSize(fileSize);
		ContentServerHelper.service.updateContent(doc, app, fis);
		if (fis != null) {
			fis.close();
		}

		return doc;
	}
	
	/**
	 * 修改文档 主内容
	 * 
	 * @param doc
	 * @param fis
	 * @param fileName
	 * @param fileSize
	 * @return
	 * @throws WTException
	 * @throws PropertyVetoException
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @modified: ☆joy_gb(2015年10月21日 下午2:16:18): <br>
	 */
	public static WTDocument replaceDocPrimaryContent(WTDocument doc,
			String filepath, String fileName, long fileSize)
			throws WTException, PropertyVetoException, FileNotFoundException,
			IOException {
		ContentHolder contentHolder = ContentHelper.service.getContents(doc);
		ContentItem contentitem = ContentHelper
				.getPrimary((FormatContentHolder) contentHolder);
		ContentServerHelper.service.deleteContent(contentHolder, contentitem);
		ApplicationData app = ApplicationData.newApplicationData(doc);
		app.setRole(ContentRoleType.PRIMARY);
		app.setFileName(fileName);
		app.setFileSize(fileSize);
		ContentServerHelper.service.updateContent(doc, app, filepath);
		return doc;
	}
	/**
	 * 移除部件所有关联文档
	 * @param parent
	 * @throws WTException
	 */
	public static void removeChildren(WTDocument parent) throws WTException {
        QueryResult docUsageLinks = StructHelper.service.navigateUses(parent, WTDocumentUsageLink.class, false);
        System.out.println(docUsageLinks.size());
        if (docUsageLinks != null && docUsageLinks.size() > 0) {
            WTHashSet removeUsageLinkSet = new WTHashSet(docUsageLinks);
            PersistenceServerHelper.manager.remove(removeUsageLinkSet);
        }
        PersistenceHelper.manager.refresh(parent);
    }
	public static void main(String[] args) throws WTRuntimeException, WTException, InvocationTargetException, FileNotFoundException, PropertyVetoException, IOException {
		RemoteMethodServer rm=RemoteMethodServer.getDefault();
		rm.setUserName("wcadmin");
		rm.setPassword("wcadmin");
		rm.invoke("test", WTDocumentUtil.class.getName(), null, null, null);
	}
}
