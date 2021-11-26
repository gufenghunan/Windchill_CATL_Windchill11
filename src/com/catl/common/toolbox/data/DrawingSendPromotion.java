package com.catl.common.toolbox.data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;



import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.solr.common.util.Hash;

import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.httpgw.GatewayAuthenticator;
import wt.iba.value.IBAHolder;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleState;
import wt.lifecycle.State;
import wt.maturity.PromotionNotice;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.query.ArrayExpression;
import wt.query.ClassAttribute;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTAttributeNameIfc;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.util.WTPropertyVetoException;

import com.catl.common.util.GenericUtil;
import com.catl.common.util.PartUtil;
import com.catl.loadData.Constant;
import com.catl.loadData.IBAUtility;
import com.catl.loadData.StrUtils;
import com.catl.loadData.util.ExcelReader;
import com.catl.loadData.util.ExcelWriter;
import com.catl.test.TestMain;
import com.ptc.core.lwc.server.PersistableAdapter;
import com.ptc.core.meta.common.UpdateOperationIdentifier;

public class DrawingSendPromotion implements RemoteAccess{
   
    public static void main(String[] args) {

        RemoteMethodServer rms = RemoteMethodServer.getDefault();
        GatewayAuthenticator auth = new GatewayAuthenticator();
        auth.setRemoteUser("wcadmin");
        rms.setAuthenticator(auth);

        if(args.length<2){
            System.out.println("Example Usage:" + 
                    "java com.catl.common.toolbox.data.DrawingSendPromotion pnNumber -t");
        }
        System.out.println(args[0]+"......"+args[1]);
        invokeRemoteLoad(args[0],args[1]);
    }

    public static void invokeRemoteLoad(String filePath,String command){
        String method = "doLoad";
        String CLASSNAME= DrawingSendPromotion.class.getName();
        Class[] types = {String.class,String.class};
        Object[] values={filePath,command};
        try {
            RemoteMethodServer.getDefault().invoke(method, CLASSNAME, null, types, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void doLoad(String pnNumber,String command){
        try {
            
        	QuerySpec qs = new QuerySpec(PromotionNotice.class);
        	qs.appendWhere(new SearchCondition(new ClassAttribute(PromotionNotice.class, PromotionNotice.NUMBER),SearchCondition.IN,new ArrayExpression(new String[]{pnNumber})), new int[]{0});
        	
        	QueryResult qr = PersistenceServerHelper.manager.query(qs);
        	
        	while(qr.hasMoreElements()){ 
        		PromotionNotice pn = (PromotionNotice)qr.nextElement();
        		String result = com.catl.integration.ReleaseUtil.sendDrawingToERP(pn);
        		System.out.println("sendDrawingToERP result="+result);
        	}
        	
        }catch (Exception e) {
        	e.printStackTrace();
        }
        System.out.println("DrawingSendPromotion 导入结束");
    }

    public static boolean isEmpty(String str){
        if(str == null || str.trim().equals("") || str.equals("null"))
            return true;
        return false;
    }



}
