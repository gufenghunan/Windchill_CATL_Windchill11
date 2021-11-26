package com.catl.ri.riB.util;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import wt.doc.WTDocument;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.inf.container.OrgContainer;
import wt.inf.container.WTContainer;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.pds.StatementSpec;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTException;
import wt.util.WTProperties;

import com.catl.ri.constant.ConstantRI;
import com.catl.ri.util.ZipUtil;
import com.ptc.core.meta.common.TypeIdentifierHelper;

public class RIUtil implements RemoteAccess{
	private static String wt_home;
	static {
		try {
			wt_home = WTProperties.getLocalProperties().getProperty("wt.home",
					"UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 获取所有模版
	 * 
	 * @return
	 * @throws Exception
	 */
	public static List<WTDocument> getAllTemplate() throws Exception {
		List<WTDocument> pns = new ArrayList<WTDocument>();
		WTDocument doca=RIUtil.getConfigByName(ConstantRI.config_rimath_nameB);
		if(doca!=null){
			pns.add(doca);
		}
		WTDocument docb=RIUtil.getConfigByName(ConstantRI.config_rimath_nameB);
		if(docb!=null){
			pns.add(docb);
		}
		return pns;
	}
	public static void main(String[] args) throws Exception {
		RemoteMethodServer rms = RemoteMethodServer.getDefault();
		rms.setUserName("wcadmin");
		rms.setPassword("wcadmin");
		rms.invoke("getAllTemplate", RIUtil.class.getName(), null, null, null);
	}
	
	public static WTContainer getOrg()
			throws WTException {
		QuerySpec qs = new QuerySpec(OrgContainer.class);
		qs.appendWhere(new SearchCondition(OrgContainer.class,
				OrgContainer.NAME, SearchCondition.EQUAL, "CATL"),
				new int[] { 0 });
		QueryResult qr=PersistenceHelper.manager.find((StatementSpec) qs);
		WTContainer container=null;
		if(qr.hasMoreElements()){
			container=(WTContainer) qr.nextElement();
		}
		return container;
	}
	public static String downloadDocZip(List<WTDocument> docs) throws WTException, IOException, PropertyVetoException {
		for (int i = 0; i < docs.size(); i++) {
			WTDocument doc=docs.get(i);
			WTDocumentUtil.downloadDoc(doc, wt_home+ConstantRI.config_ri_temp);
		}
		String filename= "ri_template.zip";
		ZipUtil.fileToZip(wt_home+ConstantRI.config_ri_temp, wt_home+ConstantRI.config_ri_temp,filename);
		return ConstantRI.config_ri_temp.replace("/codebase", "/Windchill")+filename;
	}
	
	public static void emptyTempDir(){
		File file=new File(wt_home+ConstantRI.config_ri_temp);
		if(file.isDirectory()){
			File[] files=file.listFiles();
			for (int i = 0; i < files.length; i++) {
				File infile=files[i];
				if(infile.isFile()){
					infile.delete();
				}
			}
		}
	}
	public static void emptyTempExcel(){
		File file=new File(wt_home+ConstantRI.config_ri_temp);
		if(file.isDirectory()){
			File[] files=file.listFiles();
			for (int i = 0; i < files.length; i++) {
				File infile=files[i];
				if(infile.isFile()&&infile.getName().toLowerCase().endsWith(".xlsx")){
					infile.delete();
				}
			}
		}
	}
		public static WTDocument getConfigByName(String name)
			throws WTException {
		WTDocument wtdoc = null;
		QuerySpec qs = new QuerySpec(WTDocument.class);
		qs.appendWhere(new SearchCondition(WTDocument.class, "master>name",
				SearchCondition.EQUAL, name, false),
				new int[] { 0 });
		qs.appendAnd();
		qs.appendWhere(new SearchCondition(WTDocument.class,
				"iterationInfo.latest", SearchCondition.IS_TRUE),
				new int[] { 0 });
		qs.appendOrderBy(new OrderBy(new ClassAttribute(WTDocument.class,
				"versionInfo.identifier.versionSortId"), true), new int[] { 0 });

		QueryResult qr = PersistenceServerHelper.manager.query(qs);
        while(qr.hasMoreElements()){
        	WTDocument cdoc=(WTDocument) qr.nextElement();
        	if(TypeIdentifierHelper.getType(cdoc).getTypename().contains(ConstantRI.TEMPLATE_WTDOCUMENT_TYPE)){
        		wtdoc=cdoc;
        	}
        }
		return wtdoc;
	}
}
