package com.catl.common.toolbox.data;

import java.util.ArrayList;
import java.util.List;

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

public class Export2DHistoryIssue implements RemoteAccess {
	public static void main(String[] args) {
		RemoteMethodServer rms = RemoteMethodServer.getDefault();
		GatewayAuthenticator auth = new GatewayAuthenticator();
		auth.setRemoteUser("wcadmin");
		rms.setAuthenticator(auth);

		invokeRemoteLoad();
	}

	public static void invokeRemoteLoad() {
		String method = "doLoad";
		String CLASSNAME = Export2DHistoryIssue.class.getName();
		try {
			RemoteMethodServer.getDefault().invoke(method, CLASSNAME, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void doLoad(){
		try{
			System.out.println("开始导出");
			List<WTPart> list = getWTPartListByVersion("1");
			
			List<String[]> export = new ArrayList<String[]>();
			List<String[]> export1 = new ArrayList<String[]>();
			for(WTPart part : list){
				QueryResult secondaryContents = ContentHelper.service.getContentsByRole(part, ContentRoleType.SECONDARY);// 获取附件
				while(secondaryContents.hasMoreElements()){
					ApplicationData data = (ApplicationData)secondaryContents.nextElement();
					if(data.getFileName().equals(part.getNumber()+"_RELEASE.pdf")){
						part = PartUtil.getLastestWTPartByNumber(part.getNumber());//最新版本
						QueryResult cadresult = PartDocServiceCommand.getAssociatedCADDocuments(part);
						cadresult = new LatestConfigSpec().process(cadresult);
						boolean isHave = false;
						while (cadresult.hasMoreElements()) {
							EPMDocument epmdoc = (EPMDocument) cadresult.nextElement();
							Boolean iscaddrawing = epmdoc.getDocType().toString().equals("CADDRAWING");
							if (iscaddrawing && epmdoc.getVersionIdentifier().getValue().equals("A") && epmdoc.getState().toString().equals(DocState.RELEASED)) {
								export1.add(new String[]{part.getNumber(),part.getName(),part.getCreator().getName()});
								isHave = true;
							}
						}
						if (isHave == false) {// 没有CAD图纸
							QueryResult docresult = PartDocServiceCommand.getAssociatedDescribeDocuments(part);
							docresult = new LatestConfigSpec().process(docresult);
							while (docresult.hasMoreElements()) {// 取第一个 PCBA、AUTO、GERBER文档
								WTDocument doc = (WTDocument) docresult.nextElement();
								TypeIdentifier ti = TypeIdentifierUtility.getTypeIdentifier(doc);
								String type = ti.getTypename();
								boolean isNeedset = type.contains(TypeName.doc_type_pcbaDrawing) || type.contains(TypeName.doc_type_autocadDrawing) || type.contains(TypeName.doc_type_gerberDoc);
								if (isNeedset && doc.getVersionIdentifier().getValue().equals("A") && doc.getState().toString().equals(DocState.RELEASED)) {
									export1.add(new String[]{part.getNumber(),part.getName(),part.getCreator().getName()});
									isHave = true;
								}
							}
						}
						if(isHave == false)
							export.add(new String[]{part.getNumber(),part.getName(),part.getCreator().getName()});
					}
				}
			}
			System.out.println("所有有图纸作为PN的附件列表.xlsx 数量="+export.size());
			ExcelWriter writer = new ExcelWriter();
	    	boolean flag = writer.exportExcelList("/data/所有/data/纸作为PN的附件列表.xlsx","所有有图纸作为PN的附件列表", new String[]{"编号", "名称","创建者"}, export);
	    	boolean flag1 = writer.exportExcelList("/data/所有有图纸作为PN的附件并且关联的2D版本为A列表.xlsx","所有有图纸作为PN的附件并且关联的2D版本为A列表", new String[]{"编号", "名称","创建者"}, export1);
			System.out.println("ExportBom.xlsx flag="+flag+",flag1="+flag1);
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	public static List<WTPart> getWTPartListByVersion(String version) throws WTException {
		List<WTPart> list = new ArrayList<WTPart>();
		QuerySpec queryspec = new QuerySpec(WTPart.class);

		queryspec.appendSearchCondition(new SearchCondition(WTPart.class, "versionInfo.identifier.versionId", SearchCondition.EQUAL, version));
		queryspec.appendAnd();
		queryspec.appendSearchCondition(new SearchCondition(WTPart.class, "state.state", SearchCondition.EQUAL, "RELEASED"));
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
