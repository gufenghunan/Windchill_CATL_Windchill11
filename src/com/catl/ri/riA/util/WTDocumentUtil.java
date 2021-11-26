package com.catl.ri.riA.util;

import java.beans.PropertyVetoException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

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
import wt.fc.ReferenceFactory;
import wt.fc.collections.WTHashSet;
import wt.folder.Folder;
import wt.inf.container.WTContainer;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.pom.PersistenceException;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.util.WTRuntimeException;
import wt.vc.struct.StructHelper;
import wt.vc.wip.WorkInProgressException;
import wt.vc.wip.WorkInProgressHelper;
import wt.vc.wip.Workable;

import com.catl.ri.constant.ConstantRI;
import com.catl.line.util.WCUtil;
public class WTDocumentUtil implements RemoteAccess{
	private static final Logger logger = Logger.getLogger(WTDocumentUtil.class.getName());
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
	 * 下载主文件完整路径
	 * 
	 * @param doc
	 * @return
	 * @throws WTException
	 * @throws IOException
	 * @throws PropertyVetoException
	 * @modified: ☆joy_gb(2015年10月26日 下午7:33:07): <br>
	 */
	public static ApplicationData downloadDocPrimaryUseItName(WTDocument doc,String filePath)
			throws WTException, IOException, PropertyVetoException {
		ContentHolder contentholder = ContentHelper.service.getContents(doc);
		ContentItem contentitem = ContentHelper.getPrimary((FormatContentHolder) contentholder);// 得到主文档
		ApplicationData applicationdata=null;
		if(contentitem!=null){
			applicationdata= (ApplicationData) contentitem;
			filePath=filePath+doc.getName()+".xlsx";
			ContentServerHelper.service.writeContentStream(applicationdata,filePath);
			return applicationdata;
		}
		return null;
		
	}
	
	
	/**
	 * 下载电芯设计表文档临时文件
	 * 
	 * @param doc
	 * @return
	 * @throws WTException
	 * @throws IOException
	 * @throws PropertyVetoException
	 * @modified: ☆joy_gb(2015年10月26日 下午7:33:07): <br>
	 */
	public static String downloadRIDoc(WTDocument doc,String filePath)
			throws WTException, IOException, PropertyVetoException {
		ContentHolder contentholder = ContentHelper.service.getContents(doc);
		ContentItem contentitem = ContentHelper.getPrimary((FormatContentHolder) contentholder);// 得到主文档
		ApplicationData applicationdata=null;
		if(contentitem!=null){
			applicationdata= (ApplicationData) contentitem;
			String numberStr=null;
			String docName=doc.getName();
			Pattern pattern=Pattern.compile("^*_\\d{3}_");
			Matcher matcher=pattern.matcher(docName);
			if(matcher.find()){
				numberStr=matcher.group();
			}else{
				throw new WTException("电芯设计文档名称不规范");
			}
			filePath=filePath+docName.replace(numberStr, "_XXX_")+"###"+doc.getNumber()+".xlsx";
			ContentServerHelper.service.writeContentStream(applicationdata,filePath);
			return filePath;
		}
		return null;
		
	}
	/**
	 * 下载主文件完整路径
	 * 
	 * @param doc
	 * @return
	 * @throws WTException
	 * @throws IOException
	 * @throws PropertyVetoException
	 * @modified: ☆joy_gb(2015年10月26日 下午7:33:07): <br>
	 */
	public static ApplicationData downloadDocPrimary(WTDocument doc,String filePath)
			throws WTException, IOException, PropertyVetoException {
		ContentHolder contentholder = ContentHelper.service.getContents(doc);
		ContentItem contentitem = ContentHelper.getPrimary((FormatContentHolder) contentholder);// 得到主文档
		ApplicationData applicationdata=null;
		if(contentitem!=null){
			applicationdata= (ApplicationData) contentitem;
			filePath=filePath+applicationdata.getFileName();
			ContentServerHelper.service.writeContentStream(applicationdata,filePath);
			return applicationdata;
		}
		return null;
		
	}
	 /** 获取主文件文件名
	 * 
	 * @param doc
	 * @return
	 * @throws WTException
	 * @throws IOException
	 * @throws PropertyVetoException
	 * @modified: ☆joy_gb(2015年10月26日 下午7:33:07): <br>
	 */
	public static String getDocPrimaryName(WTDocument doc)
			throws WTException, IOException, PropertyVetoException {
		ContentHolder contentholder = ContentHelper.service.getContents(doc);
		ContentItem contentitem = ContentHelper.getPrimary((FormatContentHolder) contentholder);// 得到主文档
		ApplicationData applicationdata=null;
		if(contentitem!=null){
			applicationdata= (ApplicationData) contentitem;
			if(applicationdata!=null){
				return applicationdata.getFileName();
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
	
	/**
	 * 根据Oid获取文档
	 * @param oid
	 * @throws WTException 
	 * @throws WTRuntimeException 
	 */
	public static WTDocument getDocumentByOid(String oid) throws WTRuntimeException, WTException{
		WTDocument doc=(WTDocument) new ReferenceFactory().getReference(oid).getObject();
		return doc;
	}
	
	/**
	 * 检出
	 * 
	 * @param epmDoc
	 * @return
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public static WTDocument checkOutObject(WTDocument wtDoc)
			throws WTException, WTPropertyVetoException {
		Folder myCOFolder = null;
		myCOFolder = WorkInProgressHelper.service.getCheckoutFolder();
		// 判断工作副本是否是检出状态
		if (!WorkInProgressHelper.isCheckedOut(wtDoc)) {
			WorkInProgressHelper.service.checkout(wtDoc, myCOFolder, null);
			wtDoc = (WTDocument) WorkInProgressHelper.service
					.workingCopyOf(wtDoc);
		}
		return wtDoc;
	}

	/**
	 * 检入
	 * 
	 * @param object
	 * @return
	 * @throws WorkInProgressException
	 * @throws WTPropertyVetoException
	 * @throws PersistenceException
	 * @throws WTException
	 */
	public static Workable checkInObject(Workable object)
			throws WorkInProgressException, WTPropertyVetoException,
			PersistenceException, WTException {
		object = WorkInProgressHelper.service.checkin(object, "");
		return object;
	}
	
	public static WTDocument getRecipeDoc(String containerOid)throws WTException{
		        WTContainer container=(WTContainer) WCUtil.getWTObject(containerOid);
		        List<WTDocument> docs=CommonUtil.getWTDocumentByName(ConstantRI.config_recipedocname);
			    WTDocument doc=null;
			    for (int i = 0; i < docs.size(); i++) {
			    	WTDocument cdoc=(WTDocument)docs.get(i);
			    	if(cdoc.getContainerName().equals(container.getName())){
			    		doc=cdoc;
			    		break;
			    	}
				}
                if(doc==null){
                	throw new WTException(container.getName()+"没有有效配方文件");
                }
			    return doc;
			  }

	/**
	 * 下载文档目录
	 * 
	 * @param doc
	 * @return
	 * @throws WTException
	 * @throws IOException
	 * @throws PropertyVetoException
	 * @modified: ☆hdong(2015年10月26日 下午7:33:07): <br>
	 */
	public static String downloadDoc(WTDocument doc,String filePath)
			throws WTException, IOException, PropertyVetoException {
		ContentHolder contentholder = ContentHelper.service.getContents(doc);
		ContentItem contentitem = ContentHelper.getPrimary((FormatContentHolder) contentholder);// 得到主文档
		ApplicationData applicationdata=null;
		if(contentitem!=null){
			applicationdata= (ApplicationData) contentitem;
			filePath=filePath+applicationdata.getFileName();
			ContentServerHelper.service.writeContentStream(applicationdata,filePath);
			return filePath;
		}
		return null;
		
	}
}
