package com.catl.part;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.inf.container.WTContainerRef;
import wt.method.MethodContext;
import wt.pom.WTConnection;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.rule.algorithm.RuleAlgorithm;
import wt.util.WTException;

import com.catl.line.constant.ConstantLine;
import com.ptc.core.lwc.server.LWCStructEnumAttTemplate;

public class CatlPartNewNumber implements RuleAlgorithm {

	private static Logger log=Logger.getLogger(CatlPartNewNumber.class.getName());
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		queryMaxPartNumber("550151-");
	}

	@Override
	public Object calculate(Object[] arg0, WTContainerRef arg1)
			throws WTException {
		// TODO Auto-generated method stub
		String Nodename=arg0[0].toString();
		String name=arg1.getName();
		
		LWCStructEnumAttTemplate node=getLWCStructEnumAttTemplateByName(Nodename);
        return createPartNewnumber(node,name);
		
	}

	public static String queryMaxPartNumber(String numberPrefix)
			throws Exception {
		MethodContext context = MethodContext.getContext();
		WTConnection wtConn = (WTConnection) context.getConnection();
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		String partNumber = null;
		try {
			String sql = "select max(substr(wtpartNumber,?,length(wtpartNumber))) wtpartnumber from wtpartMaster where wtpartNumber like ? and wtpartNumber not like ? and wtpartNumber not like ?";
			statement = wtConn.prepareStatement(sql);
			statement.setInt(1, (numberPrefix + "M").length());
			statement.setString(2, numberPrefix + "%");
			statement.setString(3, numberPrefix + "M%");
			statement.setString(4,"%S");//排除售后件
			resultSet = statement.executeQuery();
			while (resultSet.next()) {
				partNumber = resultSet.getString("wtpartnumber");
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
		return partNumber;
	}
	
	public static String queryMaxBatteryMaterialNumberWatercode(String numberPrefix)
			throws Exception {
		MethodContext context = MethodContext.getContext();
		WTConnection wtConn = (WTConnection) context.getConnection();
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		String partNumber = null;
		try {
			String sql = "select max(substr(wtpartNumber,length(wtpartNumber),length(wtpartNumber))) wtpartnumber from wtpartMaster where wtpartNumber like ?";
			statement = wtConn.prepareStatement(sql);
			statement.setString(1,numberPrefix+"%");//排除售后件
			resultSet = statement.executeQuery();
			while (resultSet.next()) {
				partNumber = resultSet.getString("wtpartnumber");
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
		} 
		//update by szeng 20171016 
		if(nodename.equals("CM")||nodename.equals("CK")||nodename.equals("CP")){
					
		}else{
			if(nodename.length()>=6)
			{	    	
				prefix=nodename;
			}else {
				while(nodename.length() < 6){
					nodename = nodename+"0";
				}
				prefix=nodename;
			}		
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
			if(containername.equals(ConstantLine.libary_lineparentpn)){
				numberString=prefix+"-M001";
			}else if(nodename.equals("CM")||nodename.equals("CK")||nodename.equals("CP")){
				numberString=prefix+"-00000001";
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

				//update by szeng 20171016 
				if(nodename.equals("CM")||nodename.equals("CK")||nodename.equals("CP")){
					while(maxnumber.length() <8){
						maxnumber = "0"+maxnumber;
					}
				}else{
					while(maxnumber.length() <5){
						maxnumber = "0"+maxnumber;
					}
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
