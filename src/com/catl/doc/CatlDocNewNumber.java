package com.catl.doc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;

import wt.inf.container.WTContainerRef;
import wt.method.MethodContext;
import wt.pom.WTConnection;
import wt.rule.algorithm.RuleAlgorithm;
import wt.util.WTException;

public class CatlDocNewNumber implements RuleAlgorithm {

	private static Logger lg=Logger.getLogger(CatlDocNewNumber.class.getName());

	/**
	 * @param args
	 * @throws Exception 
	 * @author cjt
	 */

	public static int queryMaxDocNumber(String subCategory) throws Exception {
	    lg.debug("subCategory = " + subCategory);
		MethodContext context = MethodContext.getContext();
		WTConnection wtConn = (WTConnection)context.getConnection();
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		int maxNumber = 0;
		int nextNumber = 0;
		try{
			String sql = "select maxNumber from ObjectNumber where subCategory = ? for update";
			statement = wtConn.prepareStatement(sql);
			statement.setString(1, subCategory);
			resultSet = statement.executeQuery();
			
			while(resultSet.next()){
				maxNumber = resultSet.getInt(1);
			}
			
			if(maxNumber ==0){
			    String insertSql = "insert into ObjectNumber values(?,?)";
			    statement = wtConn.prepareStatement(insertSql);
			    statement.setString(1,subCategory);
			    statement.setInt(2, maxNumber);
			    statement.executeUpdate();
			}
			
			lg.debug("max Number =" + maxNumber);
			nextNumber = maxNumber+1;
			String updateSql = "update ObjectNumber set maxNumber = ? where subCategory=?";
			statement = wtConn.prepareStatement(updateSql);
			statement.setInt(1, nextNumber);
			statement.setString(2, subCategory);
			statement.executeUpdate();
			        
			
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}finally{
			if(resultSet != null) resultSet.close();
			if(statement != null) statement.close();
			if(wtConn != null && wtConn.isActive()) wtConn.release();
		}
		lg.debug("max Number " + nextNumber);
		
		return nextNumber;
	}
	@Override
	public Object calculate(Object[] arg0, WTContainerRef arg1)
			throws WTException {
		// TODO Auto-generated method stub
		String numberString="";
		String prefix="";
		if(arg0[0]==null)
		{
			prefix="000000";
		}else{
		 String classification = arg0[0].toString();
		 if(null!=classification){
		    prefix=classification.substring(classification.indexOf("-")+1,classification.length());
		 }
		}
         try {
            int  maxNumber = queryMaxDocNumber(prefix);
        	String nextNumberString = String.valueOf(maxNumber);
			while(nextNumberString.length() <8){
					nextNumberString = "0"+nextNumberString;
			}
			numberString=prefix+"-"+nextNumberString;
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
         
        if(lg.isDebugEnabled()){
            lg.debug("number = " + numberString);
        }
		return numberString;
	
	}
}
