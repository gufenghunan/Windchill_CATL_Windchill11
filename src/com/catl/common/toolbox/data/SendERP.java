package com.catl.common.toolbox.data;

import java.io.IOException;

import org.apache.log4j.Logger;

import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.httpgw.GatewayAuthenticator;
import wt.maturity.PromotionNotice;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.query.ArrayExpression;
import wt.query.ClassAttribute;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

public class SendERP implements RemoteAccess {
	private static final String CLASSNAME = SendERP.class.getName();
    private static Logger log =Logger .getLogger(SendERP.class.getName());
    
    public static void main(String[] args) throws Exception, WTPropertyVetoException {
        RemoteMethodServer rms = RemoteMethodServer.getDefault();
        GatewayAuthenticator auth = new GatewayAuthenticator();
        auth.setRemoteUser("wcadmin");
        rms.setAuthenticator(auth);
        
        String method = "sendSAP";
        String refStr = args[0];
        Class[] types = {String.class};
        Object[] values={refStr};
        try {
        	RemoteMethodServer.getDefault().invoke(method, CLASSNAME, null, types, values);
        } catch (Exception e) {
          e.printStackTrace();
        }
    }
    public static void sendSAP(String pnNumber) throws IOException, WTPropertyVetoException, WTException{
    	
    	System.out.println("=====sendSAP  start=======");
    	QuerySpec qs = new QuerySpec(PromotionNotice.class);
    	qs.appendWhere(new SearchCondition(new ClassAttribute(PromotionNotice.class, PromotionNotice.NUMBER),SearchCondition.IN,new ArrayExpression(new String[]{pnNumber})), new int[]{0});
    	
    	QueryResult qr = PersistenceServerHelper.manager.query(qs);
    	
    	System.out.println("====PromotionNotice count:"+qr.size());
    	while(qr.hasMoreElements()){ 
    		PromotionNotice pn = (PromotionNotice)qr.nextElement();
    		System.out.println("=====pn"+pn.getNumber());
    		
    		com.catl.integration.ErpResponse response = com.catl.integration.ReleaseUtil.release(pn,null,null);

        	StringBuffer msg = new StringBuffer();
        	StringBuffer failedParts = new StringBuffer();
        	java.util.Set<String> failedBoms = new java.util.HashSet<String>();

        	StringBuffer failedDrawings = new StringBuffer();
        	java.util.Set<String> set = new java.util.HashSet<String>();


        	StringBuffer successParts = new StringBuffer();
        	java.util.Set<String> successBoms = new java.util.HashSet<String>();

        	if(!response.isSuccess()){
        	      for(com.catl.integration.Message message : response.getMessage()){
        		 if(!message.isSuccess()){               
        	             String partNumber = message.getNumber();
        		     String childNumber = message.getChildNumber();
        		     String stituteNumber= message.getStituteNumber();
        		     String ecnNumber= message.getEcnNumber();
        		     String drawingNumber= message.getDrawingNumber();
        		     String drawingVersion= message.getDrawingVersion();
        	             String action = message.getAction();
        		     if(action.equals(com.catl.integration.Message.PROGRAM_EXCEPTION)){
        			msg.append(action+",原因:" + message.getText()+"\n");
        		     }else if(action.equals(com.catl.integration.Message.PART_CREATE) || action.equals(com.catl.integration.Message.PART_CHANGE)){
        			set.add(partNumber);
        			failedParts.append(partNumber);
        	                failedParts.append(",");
        			msg.append(action+"，编号:"+partNumber + "，ecnNumber:" +ecnNumber+"，发布失败，原因:" + message.getText()+"\n");
        		     }else if(action.equals(com.catl.integration.Message.BOM_CREATE) || action.equals(com.catl.integration.Message.BOM_CHANGE)){
        			set.add(partNumber);
        			failedBoms.add(partNumber);
        			msg.append(action+"，编号:"+partNumber+"，子键:"+message.getChildNumber()+"，替代件:"+message.getStituteNumber()+ "，ecnNumber:" +ecnNumber+"，发布失败，原因:" + message.getText()+"\n");
        		     }else if(action.equals(com.catl.integration.Message.DRAWING)){
        			failedDrawings.append(drawingNumber);
        			failedDrawings.append(",");
        			msg.append(action+"，编号:"+partNumber +"，图纸编号:"+drawingNumber+"，图纸版本:"+drawingVersion+ "，发布失败，原因:" + message.getText()+"\n");
        		     }
        		              
        	          }else{
        	            String partNumber = message.getNumber();
        		    String action = message.getAction();
        		    if(action.equals(com.catl.integration.Message.PART_CREATE) || action.equals(com.catl.integration.Message.PART_CHANGE)){
        			successParts.append(partNumber);
        	                successParts.append(",");
        		     }else if(action.equals(com.catl.integration.Message.BOM_CREATE) || action.equals(com.catl.integration.Message.BOM_CHANGE)){
        			successBoms.add(partNumber);
        		     }
        		  }
        	      }
        	} 
        	System.out.println(msg.toString());
    	}
    	
    	System.out.println("=====sendSAP  end=======");
    }
	
}
