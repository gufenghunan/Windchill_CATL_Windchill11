package com.catl.change;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import wt.change2.ChangeException2;
import wt.change2.ChangeHelper2;
import wt.change2.WTChangeOrder2;
import wt.change2.WTChangeRequest2;
import wt.fc.QueryResult;
import wt.inf.container.WTContainerRef;
import wt.method.MethodContext;
import wt.pom.WTConnection;
import wt.rule.algorithm.RuleAlgorithm;
import wt.util.WTException;

public class CatlEcnNewNumber implements RuleAlgorithm{

	@Override
	public Object calculate(Object[] arg0, WTContainerRef arg1)
			throws WTException {
		// TODO Auto-generated method stub
		String maxnumberString="";
		WTChangeOrder2 ecn=null;
		String ECNmiddleNumer=getEcnNumber(ecn);
		try {
		maxnumberString=queryMaxEcnNumber("ECN")==null?"":queryMaxEcnNumber("ECN");
		if(maxnumberString.length()==0)
		{
		}else{
			String subnumber=maxnumberString.substring(maxnumberString.length()-2,maxnumberString.length());
			int temp=Integer.parseInt(subnumber);
			temp++;
			maxnumberString=Integer.toString(temp);
			String nextNumberString = String.valueOf(maxnumberString);
			//if number length less than 2  add "0"
			while(nextNumberString.length() < 2){
				nextNumberString = "0"+nextNumberString;
			}
		}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
   
public static String queryMaxEcnNumber(String numberPrefix) throws Exception {
		
		MethodContext context = MethodContext.getContext();
		WTConnection wtConn = (WTConnection)context.getConnection();
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		String EcnNumber = null;
		try{
			
			
			String sql = "select max(wtchgordernumber) wtchgordernumber from wtchangeorder2master where wtchgordernumber like ? ";
			
			statement = wtConn.prepareStatement(sql);
			statement.setString(1, numberPrefix+"%");
			
			resultSet = statement.executeQuery();
			while(resultSet.next()){
				EcnNumber = resultSet.getString("wtchgordernumber");
			}
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}finally{
			if(resultSet != null) resultSet.close();
			if(statement != null) statement.close();
			if(wtConn != null && wtConn.isActive()) wtConn.release();
		}
		
		return EcnNumber;
	}
	
	public static String getEcnNumber(WTChangeOrder2 ecn)
	{
		WTChangeRequest2 ecr=getEcrByEcn(ecn);
		String ecrNumber=ecr.getNumber();
		String  subEcrNumber=ecrNumber.substring(3,ecrNumber.length());
		return subEcrNumber;
	}
	public static WTChangeRequest2 getEcrByEcn(WTChangeOrder2 ecn)
	{
		WTChangeRequest2 ecr = null;
		
		if(ecn==null) return ecr;
	
		try
		{
			QueryResult ecrqr = ChangeHelper2.service.getChangeRequest(ecn);
			while(ecrqr.hasMoreElements())
			{
				return (WTChangeRequest2) ecrqr.nextElement();
			}
		} catch (ChangeException2 e)
		{
			e.printStackTrace();
		} catch (WTException e)
		{
			e.printStackTrace();
		}
		
		return ecr;
	}
}
