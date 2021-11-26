package com.catl.common.toolbox.data;

import java.util.ArrayList;
import java.util.List;

import com.catl.common.constant.CadState;
import com.catl.common.constant.DocState;
import com.catl.common.constant.TypeName;
import com.catl.common.util.PartUtil;
import com.catl.loadData.util.ExcelWriter;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.core.meta.server.TypeIdentifierUtility;
import com.ptc.windchill.enterprise.part.commands.PartDocServiceCommand;

import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentRoleType;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.httpgw.GatewayAuthenticator;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.part.WTPartMaster;
import wt.part.WTPartUsageLink;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTException;
import wt.vc.config.LatestConfigSpec;
/**
 * 导出part有附件的物料
 * 导出列编号	名称	是否有2D
 * @author zhengjh
 *
 */
public class ExportPartAttachment implements RemoteAccess {
	public static void main(String[] args) {
		RemoteMethodServer rms = RemoteMethodServer.getDefault();
		GatewayAuthenticator auth = new GatewayAuthenticator();
		auth.setRemoteUser("wcadmin");
		rms.setAuthenticator(auth);

		invokeRemoteLoad();
	}

	public static void invokeRemoteLoad() {
		String method = "doLoad";
		String CLASSNAME = ExportPartAttachment.class.getName();
		try {
			RemoteMethodServer.getDefault().invoke(method, CLASSNAME, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void doLoad(){
		try{
			System.out.println("开始导出");
			List<WTPart> list = getWTPartList();
			
			List<String[]> export = new ArrayList<String[]>();
			for(WTPart part : list){
				String[] strArr = new String[3];
				QueryResult secondaryContents = ContentHelper.service.getContentsByRole(part, ContentRoleType.SECONDARY);// 获取附件
				while(secondaryContents.hasMoreElements()){
					Object obj = secondaryContents.nextElement();
					if(obj instanceof ApplicationData){
						ApplicationData data = (ApplicationData)obj;
						if(data.getFileName().equals(part.getNumber()+".pdf")){
							strArr[0] = part.getNumber();
							strArr[1] = part.getName();	
							strArr[2] = "否";
						}
					}
				}
				QueryResult cadresult = PartDocServiceCommand.getAssociatedCADDocuments(part);
				cadresult = new LatestConfigSpec().process(cadresult);
				while (cadresult.hasMoreElements()) {
					EPMDocument epmdoc = (EPMDocument) cadresult.nextElement();
					Boolean iscaddrawing = epmdoc.getDocType().toString().equals("CADDRAWING");
					if (iscaddrawing) {
						strArr[2] = "是";
					}
				}
				if(strArr[0] != null)
					export.add(strArr);
			}
			System.out.println("数量="+export.size());
			ExcelWriter writer = new ExcelWriter();
	    	boolean flag = writer.exportExcelList("/data/ExportPartAttachment.xlsx","ExportPartAttachment", new String[]{"编号","名称","是否有2D"}, export);
			System.out.println("ExportBom.xlsx flag="+flag);
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	public static List<WTPart> getWTPartList() throws WTException {
		List<WTPart> list = new ArrayList<WTPart>();
		QuerySpec queryspec = new QuerySpec(WTPart.class);

		System.out.println("SQL:"+queryspec.toString());
		QueryResult queryresult = PersistenceHelper.manager.find(queryspec);
		LatestConfigSpec cfg = new LatestConfigSpec();
		QueryResult qr = cfg.process(queryresult);
		while (qr.hasMoreElements()) {
			list.add((WTPart) qr.nextElement());
		}
		
		return list;
	}
}
