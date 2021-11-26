package com.catl.common.toolbox.data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.epm.EPMDocumentMaster;
import wt.epm.structure.EPMReferenceLink;
import wt.epm.structure.EPMStructureHelper;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.httpgw.GatewayAuthenticator;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.pom.Transaction;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTAttributeNameIfc;
import wt.util.WTException;
import wt.vc.config.ConfigHelper;
import wt.vc.config.LatestConfigSpec;

import com.catl.cad.BatchDownloadPDFUtil;
import com.catl.common.constant.TypeName;
import com.catl.common.util.DocUtil;
import com.catl.common.util.EpmUtil;
import com.catl.loadData.util.ExcelReader;
import com.catl.loadData.util.ExcelWriter;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.core.meta.server.TypeIdentifierUtility;
import com.ptc.windchill.enterprise.part.commands.PartDocServiceCommand;

public class Query2D3D3 implements RemoteAccess {

	public static void main(String[] args) {

		RemoteMethodServer rms = RemoteMethodServer.getDefault();
		GatewayAuthenticator auth = new GatewayAuthenticator();
		auth.setRemoteUser("wcadmin");
		rms.setAuthenticator(auth);

		if (args.length < 2) {
			System.out.println("Example Usage:" + "java com.catl.common.toolbox.data.Query2D3D3 /data/ -t");
		}
		System.out.println(args[0] + "......" + args[1]);
		invokeRemoteLoad(args[0], args[1]);
	}

	public static void invokeRemoteLoad(String filePath, String command) {
		String method = "doLoad";
		String CLASSNAME = Query2D3D3.class.getName();
		Class[] types = { String.class, String.class };
		Object[] values = { filePath, command };
		try {
			RemoteMethodServer.getDefault().invoke(method, CLASSNAME, null, types, values);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void doLoad(String filePath, String command) {
		try {

			QuerySpec queryspec = new QuerySpec(EPMDocument.class);

			QueryResult queryresult = PersistenceHelper.manager.find(queryspec);
			LatestConfigSpec cfg = new LatestConfigSpec();
			QueryResult qr = cfg.process(queryresult);

			QueryResult attach;
			List<String[]> list = new ArrayList<String[]>();// 3d
			List<String[]> list2 = new ArrayList<String[]>();// 2d
			String[] str = null;
			int i = 0;
			while (qr.hasMoreElements()) {
				EPMDocument epm = (EPMDocument) qr.nextElement();
				str = new String[15];
				str[0] = epm.getState().toString();// part状态
				str[1] = epm.getNumber();
				str[2] = epm.getName();
				str[3] = "";
				str[4] = "";
				System.out.println("int i = "+i +",empNumber="+epm.getNumber());
				boolean iscaddrawing = epm.getDocType().toString().equals("CADDRAWING");
				if (iscaddrawing) {
					attach = ContentHelper.service.getContentsByRole(epm, ContentRoleType.SECONDARY);
					while (attach.hasMoreElements()) {
						Object obj = attach.nextElement();
						if (obj instanceof ApplicationData) {
							ApplicationData fileContent = (ApplicationData) obj;
							str[3] = str[3] + fileContent.getFileName() + "||";
						}
					}
					QueryResult qur = PersistenceHelper.manager.navigate(epm, EPMReferenceLink.REFERENCES_ROLE, EPMReferenceLink.class, true);
					while (qur.hasMoreElements()) {
						QueryResult qr1 = ConfigHelper.service.filteredIterationsOf((EPMDocumentMaster) qur.nextElement(), new LatestConfigSpec());
						while (qr1.hasMoreElements()) {
							EPMDocument epm1 = (EPMDocument) qr1.nextElement();
							str[4] = str[4] + epm1.getNumber() + "||";
						}
						
					}
					list.add(str);
				} else {
					QueryResult qr1 = EPMStructureHelper.service.navigateReferencedBy((EPMDocumentMaster)epm.getMaster(), null, true);
					while(qr1.hasMoreElements()){
						EPMDocument epmdoc = (EPMDocument)qr1.nextElement();
						str[3] = str[3] + epmdoc.getNumber() + "||";
						attach = ContentHelper.service.getContentsByRole(epmdoc, ContentRoleType.SECONDARY);
						while (attach.hasMoreElements()) {
							Object obj = attach.nextElement();
							if (obj instanceof ApplicationData) {
								ApplicationData fileContent = (ApplicationData) obj;
								str[4] = str[4] + fileContent.getFileName() + "||";
							}
						}
					}
					list2.add(str);
				}
				if(str[3].endsWith("||"))
					str[3] = str[3].substring(0,str[3].length()-2);
				if(str[4].endsWith("||"))
					str[4] = str[4].substring(0,str[4].length()-2);
				
				i++;
			}
			ExcelWriter.exportExcelList(filePath + "Query2D2.xlsx", "drawingCheck", new String[] { "状态", "2D编号", "2D名称", "所有附件", "3D编号" }, list);
			ExcelWriter.exportExcelList(filePath + "Query3D2.xlsx", "drawingCheck", new String[] { "状态", "3D编号", "3D名称", "2D编号", "所有附件" }, list2);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean isEmpty(String str) {
		if (str == null || str.trim().equals("") || str.equals("null"))
			return true;
		return false;
	}

}
