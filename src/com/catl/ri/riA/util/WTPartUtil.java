package com.catl.ri.riA.util;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.httpgw.GatewayAuthenticator;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.pds.StatementSpec;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTAttributeNameIfc;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.config.LatestConfigSpec;

import com.catl.line.util.WCUtil;
import com.catl.ri.constant.ConstantRI;

public class WTPartUtil implements RemoteAccess{

	  public static List queryRIAsmParts()throws QueryException, WTPropertyVetoException, WTException
	  {
		QueryResult qr=getPartsByPrefix(ConstantRI.config_riasm_clf.toUpperCase());
		List list=new ArrayList();
	    while (qr.hasMoreElements()) {
	      WTPart part = (WTPart)qr.nextElement();
	      if(part.getFolderPath().contains(ConstantRI.config_libary_asmfolder)){
		      Map map=new HashMap();
		      map.put("title", part.getName());
		      map.put("oid", WCUtil.getOid(part));
		      map.put("number", part.getNumber());
		      list.add(map);
	      }
	    }
	    return list;
	  }
	  public static void main(String[] args) throws QueryException, WTPropertyVetoException, WTException, RemoteException, InvocationTargetException {
			RemoteMethodServer rms = RemoteMethodServer.getDefault();
		    GatewayAuthenticator auth = new GatewayAuthenticator();
			auth.setRemoteUser("wcadmin");
			rms.setAuthenticator(auth);
			rms.invoke("test", WTPartUtil.class.getName(), null, null, null);
	}
	  public static void test() throws QueryException, WTPropertyVetoException, WTException{
		  queryRIAsmParts();
	  }
	  public static QueryResult getPartsByPrefix(String number)
				throws WTException {
			List<WTPart> pns = new ArrayList<WTPart>();
			QuerySpec qs = new QuerySpec(WTPart.class);
			qs.setAdvancedQueryEnabled(true);
			SearchCondition sc1 = new SearchCondition(WTPart.class, WTPart.NUMBER,
					SearchCondition.LIKE, number + "%");
			qs.appendWhere(sc1);
			qs.appendAnd();
			SearchCondition sc2 = new SearchCondition(WTPart.class,
					WTAttributeNameIfc.LATEST_ITERATION, SearchCondition.IS_TRUE);
			qs.appendWhere(sc2);
			qs.appendOrderBy(new OrderBy(new ClassAttribute(WTPart.class,
					WTPart.NUMBER), true), new int[] { 0 });// 按编号倒序排列
			QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
			return qr;
		}
	  
		public static WTPart getLastestWTPartByName(String name) {
			try {
				QuerySpec queryspec = new QuerySpec(WTPart.class);
				queryspec.appendSearchCondition(new SearchCondition(WTPart.class,
						WTPart.NAME, SearchCondition.EQUAL, name));
				QueryResult queryresult = PersistenceHelper.manager.find(queryspec);
				LatestConfigSpec cfg = new LatestConfigSpec();
				QueryResult qr = cfg.process(queryresult);
				if (qr.hasMoreElements()) {
					return (WTPart) qr.nextElement();
				}
			} catch (WTException e) {
				e.printStackTrace();
			}
			return null;
		}
}
