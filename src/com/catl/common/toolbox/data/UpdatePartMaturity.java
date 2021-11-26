package com.catl.common.toolbox.data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


















import java.util.Set;

import wt.fc.ObjectReference;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.collections.WTCollection;
import wt.httpgw.GatewayAuthenticator;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.part.WTPartMaster;
import wt.part.WTPartSubstituteLink;
import wt.part.WTPartUsageLink;
import wt.pom.PersistenceException;
import wt.pom.Transaction;
import wt.util.WTException;

import com.catl.change.mvc.UsagePartTreesHandler;
import com.catl.common.util.IBAUtil;
import com.catl.common.util.PartUtil;
import com.catl.loadData.Constant;
import com.catl.loadData.IBAUtility;
import com.catl.loadData.util.ExcelReader;
import com.catl.loadData.util.ExcelWriter;

public class UpdatePartMaturity implements RemoteAccess{
   
    public static void main(String[] args) {

        RemoteMethodServer rms = RemoteMethodServer.getDefault();
        GatewayAuthenticator auth = new GatewayAuthenticator();
        auth.setRemoteUser("wcadmin");
        rms.setAuthenticator(auth);

        if(args.length<2){
        	/*****************************down表示降级包含附件/normal表示刷新当前的物料不管上下级*****************************/
            System.out.println("Example Usage:" + 
                    "java com.catl.common.toolbox.data.UpdatePartMaturity /data/UpdatePartMaturity.xlsx down/normal -t");
        }
        System.out.println(args[0]+"......"+args[1]+"......"+args[2]);
        invokeRemoteLoad(args[0],args[1],args[2]);
    }

    public static void invokeRemoteLoad(String filePath,String action,String command){
        String method = "doLoad";
        String CLASSNAME = UpdatePartMaturity.class.getName();
        Class[] types = {String.class,String.class,String.class};
        Object[] values={filePath,action,command};
        try {
            RemoteMethodServer.getDefault().invoke(method, CLASSNAME, null, types, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 查找物料的父级
     * @param part
     * @param map
     * @param level	表示第几层
     * @throws WTException
     */
    private static void queryParentPart(WTPart part,Map<WTPart,Integer> map,int level) throws WTException{
    	String number = part.getNumber();
    	String maturity = (String) IBAUtil.getIBAValue(part, "CATL_Maturity");
    	if(maturity.equals("3")){
	    	if(map.get(part) == null){
	    		map.put(part,level);
	        	QueryResult qr = WTPartHelper.service.getUsedByWTParts((WTPartMaster) part.getMaster());
	        	while (qr.hasMoreElements()){
	        		WTPart parent = (WTPart) qr.nextElement();
	        		parent = PartUtil.getLastestWTPartByNumber(parent.getNumber());
	        		queryParentPart(parent,map,level+1);
	        	}
	        	QueryResult subQr = WTPartHelper.service.getSubstituteForWTPartUsageLinks((WTPartMaster)part.getMaster());
	        	while(subQr.hasMoreElements()){
	        		WTPartUsageLink link = (WTPartUsageLink)subQr.nextElement();
	        		WTPart subParent = link.getUsedBy();
	        		subParent = PartUtil.getLastestWTPartByNumber(subParent.getNumber());
	        		queryParentPart(subParent,map,level+1);
	        	}
	    	}
    	}
    }

    public static void doLoad(String filePath,String action,String command){
    	Transaction ts = new Transaction();
        try {
            File f = new File(filePath);
            ExcelReader reader = new ExcelReader(f);
            reader.open();
            reader.setSheetNum(0);
            
            int count = reader.getRowCount();
            Map<Map<WTPart,Integer>,String> data = new LinkedHashMap<Map<WTPart,Integer>,String>();//搜集所有需要更新的物料
            for(int i=1; i<=count; i++){
                String[] rows = reader.readExcelLine(i);
                WTPart wtpart = null;
            	wtpart = PartUtil.getLastestWTPartByNumber(rows[0]);
                if (wtpart == null) { 
                    System.out.println(" failed number="+rows[0]+" no exist!");
                    throw new WTException(" failed number="+rows[0]+" no exist!");
                }
                Map<WTPart,Integer> map = new LinkedHashMap<WTPart,Integer>();
                if(action.equals("down")){//物料及父件成熟度3 刷成 1
                	queryParentPart(wtpart,map,0);
                	data.put(map,rows[1]); 
                }else if(action.equals("normal")){
                	map.put(wtpart, 0);
                	data.put(map,rows[1]); 
                }
            }
            ts.start();
            List<String[]> list = new ArrayList<String[]>();
            String[] strArr;
            for(Map<WTPart,Integer> map : data.keySet()){//更新物料
            	String value = data.get(map);
            	for(WTPart part : map.keySet()){
            		strArr = new String[3];
            		int level = map.get(part);
            		String pre = "";
            		for(int i=0; i<level; i++){
            			pre = pre + "  ";
            		}
            		strArr[0] = pre+part.getNumber();
            		strArr[1] = part.getName();
            		strArr[2] = "无操作";
                    if(command.equals("-r")){
                    	PersistenceHelper.manager.save(IBAUtil.setIBAVaue(part.getMaster(), "CATL_Maturity", value));
                    	strArr[2] = "成熟度更新为"+value;
                    }
                    list.add(strArr);
            	}
            }
            ts.commit();
            System.out.println("java com.catl.common.toolbox.data.UpdatePartMaturity end ");
            ExcelWriter.exportExcelList("/data/UpdatePartMaturity_result.xlsx", "UpdatePartMaturity", new String[]{"编号","名称","操作"}, list);
        }catch (Exception e) {
            e.printStackTrace();
            ts.rollback();
        }
    }


    public static boolean isEmpty(String str){
        if(str == null || str.trim().equals("") || str.equals("null"))
            return true;
        return false;
    }



}
