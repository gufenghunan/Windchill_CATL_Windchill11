package com.catl.ecad.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;

import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.doc.WTDocument;
import wt.fc.ObjectVector;
import wt.fc.QueryResult;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.util.WTRuntimeException;

public class ZipDoc implements RemoteAccess{
	
	public static void test() throws IOException, Exception {
		WTDocument doc = CommonUtil.getLatestWTDocByNumber("0000000021");
		WTDocument doc1 = CommonUtil.getLatestWTDocByNumber("0000000001");
		List<WTDocument> docs = new ArrayList<>();
		docs.add(doc);
		docs.add(doc1);
		zipDocs(docs, "E:\\doxs");
	}
	public static void main(String[] args) throws WTRuntimeException, WTException, RemoteException, InvocationTargetException {
		RemoteMethodServer rm=RemoteMethodServer.getDefault();
		rm.setUserName("wcadmin");
		rm.setPassword("wcadmin");
		rm.invoke("test", ZipDoc.class.getName(), null, null, null);
	}
	public static String zipDocs(List<WTDocument> docs,String filename) throws IOException, Exception{
		QueryResult qr = new QueryResult();
		for(WTDocument doc:docs){
		
		QueryResult qr1 = ContentHelper.service.getContentsByRole(doc, ContentRoleType.PRIMARY);
		while (qr1.hasMoreElements()) {
			Object object = (Object) qr1.nextElement();
			ObjectVector ovi = new ObjectVector();
       		 ovi.addElement(object);
       	    qr.appendObjectVector(ovi);
		}
		}
		HashMap<InputStream, String> fileMap=new HashMap();
		
		while(qr.hasMoreElements()){
			ApplicationData app = (ApplicationData) qr.nextElement();
			InputStream in=ContentServerHelper.service.findLocalContentStream(app);
			fileMap.put(in, app.getFileName());
		}
		if(fileMap.size()>0){
			zip(fileMap,filename+".zip");
		}
		return null;
	}
	
	
	

	
	  public static void zip(HashMap<InputStream, String> fileMap, String outZipFilePath) throws Exception {  
	        File zipFile = new File(outZipFilePath);  
	        ZipOutputStream zos = new ZipOutputStream(zipFile); 
	        zos.setEncoding("GBK"); 
	        Set<InputStream> set = fileMap.keySet();  
	        Iterator<InputStream> it = set.iterator();  
	        BufferedOutputStream bo =new BufferedOutputStream(zos);
	        while (it.hasNext()) {  
	            InputStream is = it.next();  
	            String fileName = fileMap.get(is);  
	            System.out.println("---"+fileName);
	            if (is != null) { 
	                zos.putNextEntry(new ZipEntry(fileName));  
	                BufferedInputStream bi = new BufferedInputStream(is);  
		            int b;  
		            while ((b = bi.read()) != -1) {  
		                bo.write(b); 
		            }  
	                is.close();  
	                bi.close();
	                bo.flush();
	            }  
	        }
	        bo.close();
	        zos.close();
	    }  

}
