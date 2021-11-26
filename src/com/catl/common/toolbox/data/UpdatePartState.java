package com.catl.common.toolbox.data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;



import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.solr.common.util.Hash;

import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.httpgw.GatewayAuthenticator;
import wt.iba.value.IBAHolder;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleManaged;
import wt.lifecycle.LifeCycleState;
import wt.lifecycle.State;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTAttributeNameIfc;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.util.WTPropertyVetoException;

import com.catl.common.constant.PartState;
import com.catl.common.constant.TypeName;
import com.catl.common.util.GenericUtil;
import com.catl.common.util.PartUtil;
import com.catl.loadData.Constant;
import com.catl.loadData.IBAUtility;
import com.catl.loadData.StrUtils;
import com.catl.loadData.util.ExcelReader;
import com.catl.loadData.util.ExcelWriter;
import com.catl.promotion.util.PromotionUtil;
import com.catl.test.TestMain;
import com.ptc.core.lwc.server.PersistableAdapter;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.core.meta.common.UpdateOperationIdentifier;
import com.ptc.core.meta.server.TypeIdentifierUtility;
import com.ptc.windchill.enterprise.part.commands.AssociationLinkObject;
import com.ptc.windchill.enterprise.part.commands.PartDocServiceCommand;

public class UpdatePartState implements RemoteAccess{
   
    public static void main(String[] args) {

        RemoteMethodServer rms = RemoteMethodServer.getDefault();
        GatewayAuthenticator auth = new GatewayAuthenticator();
        auth.setRemoteUser("wcadmin");
        rms.setAuthenticator(auth);

        if(args.length<2){
            System.out.println("Example Usage:" + 
                    "java com.catl.common.toolbox.data.UpdatePartState /data/UpdatePartState.xlsx -t");
        }
        System.out.println(args[0]+"......"+args[1]);
        invokeRemoteLoad(args[0],args[1]);
    }

    public static void invokeRemoteLoad(String filePath,String command){
        String method = "doLoad";
        String CLASSNAME= UpdatePartState.class.getName();
        Class[] types = {String.class,String.class};
        Object[] values={filePath,command};
        try {
            RemoteMethodServer.getDefault().invoke(method, CLASSNAME, null, types, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void doLoad(String filePath,String command){
    	FileWriter file=null;
    	BufferedWriter writer = null;
        try {
            file = new FileWriter(filePath.substring(0,filePath.lastIndexOf("."))+".txt");
            writer = new BufferedWriter(file);
            
        	File f = new File(filePath);
            ExcelReader reader = new ExcelReader(f);
            reader.open();
            reader.setSheetNum(0);
            
            int count = reader.getRowCount();
            Map<LifeCycleManaged,String> data = new LinkedHashMap<LifeCycleManaged,String>();
            List<String[]> releasedPart = new ArrayList<String[]>();
            List<String[]> notExistPart = new ArrayList<String[]>();
            for(int i=1; i<=count; i++){
                String[] rows = reader.readExcelLine(i);
                if(rows == null || isEmpty(rows[0]) || isEmpty(rows[1])){
                    writer.write("第"+i+"行，为空"+"\n");
                	continue;
                }
                WTPart wtpart = PartUtil.getLastestWTPartByNumber(rows[0]);
                if (wtpart == null) {
                    System.out.println(" failed number="+rows[0]+" no exist!");
                    notExistPart.add(new String[]{rows[0]});
                    continue;
                }
                if(wtpart.getState().toString().equals(PartState.RELEASED) && rows[1].equals(PartState.DISABLEDFORDESIGN)){
                	releasedPart.add(new String[]{rows[0]});
                	continue;
                }
                	
                data.put(wtpart, rows[1]);
                List<EPMDocument> epmlist = getEPMDocument(wtpart);
				for (EPMDocument epm : epmlist) {
					if(epm.getNumber().startsWith(wtpart.getNumber())){
						data.put(epm, rows[1]);
					}
				}
				List<WTDocument> document = PromotionUtil.getAssociatedDescribeDocuments(wtpart);
				for (WTDocument doc : document) {
					TypeIdentifier ti = TypeIdentifierUtility.getTypeIdentifier(doc);
					String type = ti.getTypename();
					if (type.endsWith(TypeName.gerberDoc) || type.endsWith(TypeName.pcbaDrawing) || type.endsWith(TypeName.autocadDrawing)) {
						data.put(doc, rows[1]);
					}
				}
            }
        	for(LifeCycleManaged obj : data.keySet()){
        		String val = data.get(obj);
            	if(command.equals("-r")){
            		try {
            			LifeCycleHelper.service.setLifeCycleState(obj, State.toState(val),false);
            			writer.write(" update "+getObjectNumber(obj)+",value="+val+" success\n");
        			} catch (Exception e1) {
        				writer.write(" update "+getObjectNumber(obj)+",value="+val+" fail\n");
        			}
            	}else{
            		writer.write(" update "+getObjectNumber(obj)+",value="+val+"\n");
            	}
        	}
        	ExcelWriter.exportExcelList("D://data/UpdatePartState_releasedPart.xlsx", "releasedPart", new String[]{"物料编号"}, releasedPart);
        	ExcelWriter.exportExcelList("D://data/UpdatePartState_notExistPart.xlsx", "notExistPart", new String[]{"物料编号"}, notExistPart);
            writer.flush();
            writer.close();
        }catch (Exception e) {
        	e.printStackTrace();
        }
        System.out.println("UpdatePartState 导入结束");
    }

    public static boolean isEmpty(String str){
        if(str == null || str.trim().equals("") || str.equals("null"))
            return true;
        return false;
    }

    /**
	 * 获取part关联的EPMDocuemnt
	 */
	@SuppressWarnings("unchecked")
	public static List<EPMDocument> getEPMDocument(WTPart part) throws WTException {
		List<EPMDocument> list = new ArrayList<EPMDocument>();
		Collection<AssociationLinkObject> cols = PartDocServiceCommand.getAssociatedCADDocumentsAndLinks(part);
		for (AssociationLinkObject alo : cols) {
			EPMDocument epm = alo.getCadObject();
			list.add(epm);
		}
		return list;
	}
	private static String getObjectNumber(Object obj) {
		String number = "";
		if (obj instanceof WTDocument) {
			number = ((WTDocument) obj).getNumber();
		} else if (obj instanceof EPMDocument) {
			number = ((EPMDocument) obj).getNumber();
		}	else if (obj instanceof WTPart) {
			number = ((WTPart) obj).getNumber();
		}
		return number;
	}
}
