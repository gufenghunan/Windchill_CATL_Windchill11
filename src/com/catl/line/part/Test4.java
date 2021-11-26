package com.catl.line.part;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.inf.container.WTContainerRef;
import wt.method.MethodContext;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.pom.WTConnection;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.rule.algorithm.RuleAlgorithm;
import wt.util.WTException;

import com.catl.common.util.ZipDoc;
import com.catl.line.constant.ConstantLine;
import com.itextpdf.text.log.SysoCounter;
import com.ptc.core.lwc.server.LWCStructEnumAttTemplate;

public class Test4 implements RemoteAccess {

	private static Logger log=Logger.getLogger(Test4.class.getName());
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		RemoteMethodServer rm=RemoteMethodServer.getDefault();
		rm.setUserName("wcadmin");
		rm.setPassword("wcadmin");
		rm.invoke("test", Test4.class.getName(), null, null, null);
		
	}
	
	public static void test() throws Exception{
		String numberString="";
		String maxnumber="";
		String prefix="550151";
		String containername=ConstantLine.libary_lineparentpn;
		try {
			if(containername.equals(ConstantLine.libary_lineparentpn)){
				maxnumber=queryMaxMPNPartNumber(prefix.toUpperCase()+"-");
			}else{
				maxnumber=queryMaxPartNumber(prefix.toUpperCase()+"-");
			}
		
		System.out.println("maxnumber========"+maxnumber);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(maxnumber==null)
		{
			if(containername.equals(ConstantLine.libary_lineparentpn)){
				numberString=prefix+"-M001";
			}else{
				numberString=prefix+"-00001";
			}
			
		}else{
			int temp=0;
			if(containername.equals(ConstantLine.libary_lineparentpn)){
				temp=Integer.parseInt(maxnumber.split("-")[1].replace("M", "").substring(0,3));
				temp++;
				maxnumber=Integer.toString(temp);

				while(maxnumber.length() <3){
					maxnumber = "0"+maxnumber;
				}
				numberString=prefix+"-M"+maxnumber;
			}else{
				temp=Integer.parseInt(maxnumber.substring(maxnumber.indexOf("-")+1,maxnumber.length()));
				temp++;
				maxnumber=Integer.toString(temp);

				while(maxnumber.length() <5){
					maxnumber = "0"+maxnumber;
				}
				numberString=prefix+"-"+maxnumber;
			}
			
		}
	}


	public static String queryMaxPartNumber(String numberPrefix)
			throws Exception {
		MethodContext context = MethodContext.getContext();
		WTConnection wtConn = (WTConnection) context.getConnection();
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		String partNumber = null;
		try {
			String sql = "select max(substr(wtpartNumber,?,length(wtpartNumber))) wtpartnumber from wtpartMaster where wtpartNumber like ? and wtpartNumber not like ?";
			statement = wtConn.prepareStatement(sql);
			statement.setInt(1, (numberPrefix + "M").length());
			statement.setString(2, numberPrefix + "%");
			statement.setString(3, numberPrefix + "M%");
			resultSet = statement.executeQuery();
			while (resultSet.next()) {
				partNumber = resultSet.getString("wtpartnumber");
				System.out.println("##111##"+partNumber);
				if (StringUtils.isEmpty(partNumber)) {
					partNumber = null;
				} else {
					partNumber = numberPrefix + partNumber;
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (resultSet != null)
				resultSet.close();
			if (statement != null)
				statement.close();
			if (wtConn != null && wtConn.isActive())
				wtConn.release();
		}
		System.out.println("####"+partNumber);
		String maxnumber=partNumber;
		String numberString="";
		String prefix="550151";
		String containername="母PN库";
		if(maxnumber==null)
		{
			maxnumber="00001";
			numberString=prefix+"-"+maxnumber;
		}else{
			int temp=0;
			if(containername.equals(ConstantLine.libary_lineparentpn)){
				temp=Integer.parseInt(maxnumber.split("-")[1].replace("M", "").substring(0,3));
				temp++;
				maxnumber=Integer.toString(temp);

				while(maxnumber.length() <3){
					maxnumber = "0"+maxnumber;
				}
				numberString=prefix+"-M"+maxnumber;
			}else{
				System.out.println("-------------sadddddddddddd");
				temp=Integer.parseInt(maxnumber.substring(maxnumber.indexOf("-")+1,maxnumber.length()));
				temp++;
				System.out.println(temp+"#########");
				maxnumber=Integer.toString(temp);
				while(maxnumber.length() <5){
					maxnumber = "0"+maxnumber;
				}
				numberString=prefix+"-"+maxnumber;
			}
			
			
		}
		System.out.println("------------"+numberString);
		return partNumber;
	}
    public static String queryMaxMPNPartNumber(String numberPrefix) throws Exception {
		
		MethodContext context = MethodContext.getContext();
		WTConnection wtConn = (WTConnection)context.getConnection();
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		String partNumber = null;
		try{
			String sql = "select max(wtpartNumber) wtpartnumber from wtpartMaster where wtpartNumber like ?";
			statement = wtConn.prepareStatement(sql);
			statement.setString(1, numberPrefix+"M%");
			resultSet = statement.executeQuery();
			if(resultSet.next()){
				partNumber = resultSet.getString("wtpartnumber");
			}
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}finally{
			if(resultSet != null) resultSet.close();
			if(statement != null) statement.close();
			if(wtConn != null && wtConn.isActive()) wtConn.release();
		}
		return partNumber;
	}
	public static String createPartNewnumber(LWCStructEnumAttTemplate node,String containername) throws WTException
	{
		String numberString="";
		String maxnumber="";
		String prefix="";
		String nodename=node.getName();
		log.debug("node name=="+nodename);
		if(node.getParent()==null)
		{
			throw new WTException("不能选择根目录为分类节点!");
		} if(nodename.length()>=6)
		   {	    	
			prefix=nodename;
		   }else {
				while(nodename.length() < 6){
					nodename = nodename+"0";
				}
				prefix=nodename;
		}
	    
		log.debug("prefix===="+prefix);
		prefix=nodename;
		try {
			if(containername.equals(ConstantLine.libary_lineparentpn)){
				maxnumber=queryMaxMPNPartNumber(prefix.toUpperCase()+"-");
			}else{
				maxnumber=queryMaxPartNumber(prefix.toUpperCase()+"-");
			}
		
		log.debug("maxnumber========"+maxnumber);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(maxnumber==null)
		{
			maxnumber="00001";
			numberString=prefix+"-"+maxnumber;
		}else{
			int temp=0;
			if(containername.equals(ConstantLine.libary_lineparentpn)){
				temp=Integer.parseInt(maxnumber.split("-")[1].replace("M", "").substring(0,3));
				temp++;
				maxnumber=Integer.toString(temp);

				while(maxnumber.length() <3){
					maxnumber = "0"+maxnumber;
				}
				numberString=prefix+"-M"+maxnumber;
			}else{
				temp=Integer.parseInt(maxnumber.substring(maxnumber.indexOf("-")+1,maxnumber.length()));
				temp++;
				maxnumber=Integer.toString(temp);

				while(maxnumber.length() <5){
					maxnumber = "0"+maxnumber;
				}
				numberString=prefix+"-"+maxnumber;
			}
			
		}
		return numberString;
	}
	public static boolean isNumeric(String str){
		  for (int i = 0; i < str.length(); i++){
		   System.out.println(str.charAt(i));
		   if (!Character.isDigit(str.charAt(i))){
		    return false;
		   }
		  }
		  return true;
		 }
    public static LWCStructEnumAttTemplate getLWCStructEnumAttTemplateByName(String nodeName) throws WTException {
        LWCStructEnumAttTemplate node = null;
        QuerySpec qs = new QuerySpec(LWCStructEnumAttTemplate.class);
        SearchCondition searchCondition = new SearchCondition(LWCStructEnumAttTemplate.class, LWCStructEnumAttTemplate.NAME, SearchCondition.EQUAL, nodeName);
        qs.appendSearchCondition(searchCondition);
        QueryResult qr = PersistenceHelper.manager.find(qs);
        while (qr.hasMoreElements()) {
            node = (LWCStructEnumAttTemplate) qr.nextElement();

        }
        return node;
    }

		

}
