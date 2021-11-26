package com.catl.change;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;

import wt.inf.container.WTContainerRef;
import wt.method.MethodContext;
import wt.pom.WTConnection;
import wt.rule.algorithm.RuleAlgorithm;
import wt.util.WTException;

public class CatlEcrNewNunber  implements RuleAlgorithm{

	private static Logger log=Logger.getLogger(CatlEcrNewNunber.class.getName());
	@Override

	public Object calculate(Object[] arg0, WTContainerRef arg1)
			throws WTException {
		// TODO Auto-generated method stub
		String maxnumberString="";
		String ecrNumber="";
		String yearMonth=getSystemDate();
		try {
		maxnumberString=queryMaxEcrNumber("ECR"+yearMonth)==null?"":queryMaxEcrNumber("ECR"+yearMonth);
		log.debug("maxnumberstring="+maxnumberString);
		if(maxnumberString.length()==0)
		{
			ecrNumber="ECR"+yearMonth+"001";
		}else{
			String subnumber=maxnumberString.substring(maxnumberString.length()-3,maxnumberString.length());
			log.debug("subnumber="+subnumber);
			int temp=Integer.parseInt(subnumber);
			temp++;
			maxnumberString=Integer.toString(temp);
			String nextNumberString = String.valueOf(maxnumberString);
			log.debug("nextNumberString="+nextNumberString);
			//if number length less than 3  add "0"
			while(nextNumberString.length() <3){
				nextNumberString = "0"+nextNumberString;
			}
			ecrNumber="ECR"+yearMonth+nextNumberString;
		}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		log.debug("ecrNumber="+ecrNumber);
		return ecrNumber;
	}

	public static String queryMaxEcrNumber(String numberPrefix) throws Exception {
		
		MethodContext context = MethodContext.getContext();
		WTConnection wtConn = (WTConnection)context.getConnection();
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		String EcrNumber = null;
		try{
			
			
			String sql = "select max(wtchgrequestnumber) wtchgrequestnumber from wtchangerequest2master where wtchgrequestnumber like ? ";
			
			statement = wtConn.prepareStatement(sql);
			statement.setString(1, numberPrefix+"%");
			
			resultSet = statement.executeQuery();
			while(resultSet.next()){
				EcrNumber = resultSet.getString("wtchgrequestnumber");
			}
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}finally{
			if(resultSet != null) resultSet.close();
			if(statement != null) statement.close();
			if(wtConn != null && wtConn.isActive()) wtConn.release();
		}
		
		return EcrNumber;
	}
	public static String getSystemDate() {
		String year="";
		String month="";
		String timeString="";
		Calendar cal = Calendar.getInstance();
        year=String.valueOf(cal.get(Calendar.YEAR));
        month=String.valueOf(cal.get(Calendar.MONTH) + 1);
        log.debug("String month==="+month);
		while(month.length() <2){
			month = "0"+month;
		}
		log.debug("add 0 to month==="+month);
        timeString=year.substring(year.length()-2,year.length())+month;//get the last two number of year
		return timeString;
	}
}
