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
import wt.vc.config.LatestConfigSpec;

import com.catl.common.constant.TypeName;
import com.catl.common.util.DocUtil;
import com.catl.loadData.util.ExcelReader;
import com.catl.loadData.util.ExcelWriter;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.core.meta.server.TypeIdentifierUtility;
import com.ptc.windchill.enterprise.part.commands.PartDocServiceCommand;

public class QueryPDF implements RemoteAccess{
   
    public static void main(String[] args) {

        RemoteMethodServer rms = RemoteMethodServer.getDefault();
        GatewayAuthenticator auth = new GatewayAuthenticator();
        auth.setRemoteUser("wcadmin");
        rms.setAuthenticator(auth);

        if(args.length<2){
            System.out.println("Example Usage:" + 
                    "java com.catl.common.toolbox.data.QueryPDF /data/QueryPDF_OUT.xlsx -t");
        }
        System.out.println(args[0]+"......"+args[1]);
        invokeRemoteLoad(args[0],args[1]);
    }

    public static void invokeRemoteLoad(String filePath,String command){
        String method = "doLoad";
        String CLASSNAME = QueryPDF.class.getName();
        Class[] types = {String.class,String.class};
        Object[] values={filePath,command};
        try {
            RemoteMethodServer.getDefault().invoke(method, CLASSNAME, null, types, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void doLoad(String filePath,String command){
        try {
            
        	QuerySpec queryspec = new QuerySpec(WTPart.class);

            QueryResult queryresult = PersistenceHelper.manager.find(queryspec);
            LatestConfigSpec cfg = new LatestConfigSpec();
            QueryResult qr = cfg.process(queryresult);
            
            QueryResult attach;
            List<String []> list = new ArrayList<String[]>();
            String[]  str=null;
            while(qr.hasMoreElements()){
            	WTPart part = (WTPart)qr.nextElement();
            	str = new String[15];
            	str[0]=part.getState().toString();//part状态
            	str[1]=part.getNumber();
            	str[2]=part.getName();
            	str[3]="";//part所有附件
            	str[4]="否";//part是否有正确命名的pdf图纸
            	str[5]="";//caddrawing状态
            	str[6]="";//caddrawing编号
            	str[7]="否";//caddrawing是否有且仅有一个并且与part同编号
            	str[8]="";//caddrawing所有附件
            	str[9]="否";//caddrawing是否有正确命名的pdf图纸
            	str[10]="";//autocad、gerber、pcab状态
            	str[11]="";//autocad、gerber、pcab编号
            	str[12]="否";//autocad、gerber、pcab是否有且仅有一个并且与part同编号
            	str[13]="";//autocad、gerber、pcab所有附件
            	str[14]="否";//autocad、gerber、pcab是否有正确命名的pdf图纸
            	attach = ContentHelper.service.getContentsByRole(part, ContentRoleType.SECONDARY);
            	ApplicationData fileContent;
        		while(attach.hasMoreElements()){
        			Object obj = attach.nextElement();
        			if(obj instanceof ApplicationData){
        				fileContent = (ApplicationData) obj;
            			str[3]=str[3]+fileContent.getFileName()+"||";
                        if(fileContent.getFileName().equalsIgnoreCase(part.getNumber()+".pdf")){
                        	str[4]="是";
                        }
        			}
        		}
        		if(str[3].endsWith("||")){
        			str[3]=str[3].substring(0,str[3].length()-2);
        		}
        		QueryResult cadresult =PartDocServiceCommand.getAssociatedCADDocuments(part);
        		EPMDocument epmdoc;
        		Boolean iscaddrawing;
                while(cadresult.hasMoreElements()){
                	epmdoc =(EPMDocument)cadresult.nextElement();
                    iscaddrawing = epmdoc.getDocType().toString().equals("CADDRAWING");
                    if (iscaddrawing) {
                    	str[6]=str[6]+epmdoc.getNumber()+"||";
                    	str[5]=str[5]+epmdoc.getState().toString()+"||";
                    	if(epmdoc.getNumber().equalsIgnoreCase(part.getNumber()+".CATDRAWING")){
                    		str[7]="是";
                    	}
                    	attach = ContentHelper.service.getContentsByRole(epmdoc, ContentRoleType.SECONDARY);
                    	while(attach.hasMoreElements()){
                    		Object obj = attach.nextElement();
                			if(obj instanceof ApplicationData){
	                			fileContent = (ApplicationData) obj;
	                			str[8]=str[8]+fileContent.getFileName()+"||";
	                            if(epmdoc.getNumber().indexOf(".CATDRAWING") != -1 && fileContent.getFileName().equalsIgnoreCase(epmdoc.getNumber().substring(0,epmdoc.getNumber().indexOf(".CATDRAWING"))+".pdf")){
	                            	str[9]="是";
	                            }
                			}
                		}
                    }
                }
                if(str[6].endsWith("||")){
        			str[6]=str[6].substring(0,str[6].length()-2);
        		}
                if(str[6].contains("||")){
                	str[7]="否";
                }
                if(str[5].endsWith("||")){
        			str[5]=str[5].substring(0,str[5].length()-2);
        		}
                if(str[8].endsWith("||")){
        			str[8]=str[8].substring(0,str[8].length()-2);
        		}
                
                
                QueryResult docresult =PartDocServiceCommand.getAssociatedDescribeDocuments(part);
                WTDocument doc;
                while (docresult.hasMoreElements()) {
                    doc = (WTDocument) docresult.nextElement();
                    String  type = TypeIdentifierUtility.getTypeIdentifier(doc).getTypename();
                    boolean isDrawing = type.contains(TypeName.doc_type_pcbaDrawing) || type.contains(TypeName.doc_type_autocadDrawing)
                            || type.contains(TypeName.doc_type_gerberDoc);
                    if (isDrawing) {
                    	str[11]=str[11]+doc.getNumber()+"||";
                    	str[10]=str[10]+doc.getState().toString()+"||";
                    	if(doc.getNumber().equalsIgnoreCase(part.getNumber())){
                    		str[12]="是";
                    	}
                    	attach = ContentHelper.service.getContentsByRole(doc, ContentRoleType.SECONDARY);
                    	while(attach.hasMoreElements()){
                    		Object obj = attach.nextElement();
                			if(obj instanceof ApplicationData){
                				fileContent = (ApplicationData) obj;
	                			str[13]=str[13]+fileContent.getFileName()+"||";
	                            if(fileContent.getFileName().equalsIgnoreCase(doc.getNumber()+".pdf")){
	                            	str[14]="是";
	                            }
                			}
                		}
                    }
                }
                if(str[11].endsWith("||")){
        			str[11]=str[11].substring(0,str[11].length()-2);
        		}
                if(str[11].contains("||")){
                	str[12]="否";
                }
                if(str[10].endsWith("||")){
        			str[10]=str[10].substring(0,str[10].length()-2);
        		}
                if(str[13].endsWith("||")){
        			str[13]=str[13].substring(0,str[13].length()-2);
        		}
                
                list.add(str);
            }

            ExcelWriter.exportExcelList(filePath, "drawingCheck", new String[]{"状态","part编号","part名称","part所有附件","part附件是否有正确命名的pdf图纸",
            		"caddrawing状态","caddrawing编号","caddrawing是否有且仅有一个并且与part同编号","caddrawing所有附件","caddrawing是否有正确命名的pdf图纸",
            		"autocad、gerber、pcab状态","autocad、gerber、pcab编号","autocad、gerber、pcab是否有且仅有一个并且与part同编号","autocad、gerber、pcab所有附件","autocad、gerber、pcab是否有正确命名的pdf图纸"}, list);
            
        }catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static boolean isEmpty(String str){
        if(str == null || str.trim().equals("") || str.equals("null"))
            return true;
        return false;
    }



}
