package com.catl.common.toolbox.data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLDecoder;
import java.rmi.RemoteException;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;



import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.solr.common.util.Hash;

import wt.change2.WTChangeOrder2;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentRoleType;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.fc.collections.WTArrayList;
import wt.fc.collections.WTList;
import wt.httpgw.GatewayAuthenticator;
import wt.iba.value.IBAHolder;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleState;
import wt.lifecycle.State;
import wt.maturity.PromotionNotice;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.query.ArrayExpression;
import wt.query.ClassAttribute;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTAttributeNameIfc;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.util.WTPropertyVetoException;
import wt.vc.config.LatestConfigSpec;

import com.catl.change.workflow.DcnWorkUtil;
import com.catl.change.workflow.DcnWorkflowfuncion;
import com.catl.common.constant.ContainerName;
import com.catl.common.constant.DocState;
import com.catl.common.constant.TypeName;
import com.catl.common.util.GenericUtil;
import com.catl.common.util.PartUtil;
import com.catl.integration.DrawingInfo;
import com.catl.integration.DrawingSendERP;
import com.catl.integration.ErpResponse;
import com.catl.integration.Message;
import com.catl.integration.PIService;
import com.catl.integration.PartInfo;
import com.catl.loadData.Constant;
import com.catl.loadData.IBAUtility;
import com.catl.loadData.StrUtils;
import com.catl.loadData.util.ExcelReader;
import com.catl.loadData.util.ExcelWriter;
import com.catl.test.TestMain;
import com.ptc.core.lwc.server.PersistableAdapter;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.core.meta.common.UpdateOperationIdentifier;
import com.ptc.core.meta.server.TypeIdentifierUtility;
import com.ptc.windchill.enterprise.part.commands.PartDocServiceCommand;

public class StartWFDcn implements RemoteAccess{
   
    public static void main(String[] args) {

        RemoteMethodServer rms = RemoteMethodServer.getDefault();
        GatewayAuthenticator auth = new GatewayAuthenticator();
        auth.setRemoteUser("wcadmin");
        rms.setAuthenticator(auth);

        if(args.length<1){
            System.out.println("Example Usage:" + 
                    "windchill com.catl.common.toolbox.data.StartWFDcn dcnNumber");
        }
        System.out.println(args[0]);
        invokeRemoteLoad(args[0]);
    }

    public static void invokeRemoteLoad(String dcnNumber){
        String method = "doLoad";
        String CLASSNAME= StartWFDcn.class.getName();
        Class[] types = {String.class};
        Object[] values={dcnNumber};
        try {
            RemoteMethodServer.getDefault().invoke(method, CLASSNAME, null, types, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void doLoad(String dcnNumber) throws WTException{
    	System.out.println("StartWFDcn start");
    	
    	WTChangeOrder2 dcn = getDcnByNumber(dcnNumber);
    	DcnWorkflowfuncion.start_workflow(dcn,"1");
    	
        System.out.println("DrawingSendToERP end");
    }

    public static boolean isEmpty(String str){
        if(str == null || str.trim().equals("") || str.equals("null"))
            return true;
        return false;
    }

    public static WTChangeOrder2 getDcnByNumber(String dcnNumber) throws WTException {
		QuerySpec queryspec = new QuerySpec(WTChangeOrder2.class);

		queryspec.appendSearchCondition(new SearchCondition(WTChangeOrder2.class, WTChangeOrder2.NUMBER, SearchCondition.EQUAL, dcnNumber));
		QueryResult queryresult = PersistenceHelper.manager.find(queryspec);
		LatestConfigSpec cfg = new LatestConfigSpec();
		QueryResult qr = cfg.process(queryresult);
		while (qr.hasMoreElements()) {
			return (WTChangeOrder2)qr.nextElement();
		}
		return null;
	}
}
