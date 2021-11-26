package com.catl.loadData;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.catl.common.util.IBAUtil;
import com.catl.common.util.PartUtil;
import com.catl.common.util.WCLocationConstants;
import com.catl.part.PartConstant;
import com.catl.promotion.util.PromotionUtil;

import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.pds.StatementSpec;
import wt.query.QuerySpec;
import wt.util.WTException;
import wt.util.WTMessage;

public class CheckMaturityUtil implements RemoteAccess {

	public static void main(String[] args) {
		if (args != null) {
			String userName = args[0];
			String password = args[1];
			
			RemoteMethodServer ms = RemoteMethodServer.getDefault();
			ms.setUserName(userName);
			ms.setPassword(password);
			
			try {
				System.out.println("=========开始执行=======");
				ms.invoke("checkPartsMaturity", CheckMaturityUtil.class.getName(), null, null, null);
				System.out.println("=========执行完毕=======");
				System.out.println("执行结果请查看Windchill=>logs中的日志文件");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	
	public static void checkPartsMaturity(){
		try {
			StringBuilder msg = new StringBuilder();
			QuerySpec queryspec = new QuerySpec(WTPartMaster.class);
			QueryResult qr = PersistenceHelper.manager.find((StatementSpec)queryspec);
			while(qr.hasMoreElements()){
				WTPartMaster master = (WTPartMaster)qr.nextElement();
				checkPart(master, msg);
			}
			writeLogFile(msg.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void checkPart(WTPartMaster master,StringBuilder msg) throws WTException{
		WTPart parent = PartUtil.getLastestWTPartByNumber(master.getNumber());
		String parentMaturity = (String) IBAUtil.getIBAValue(parent.getMaster(), PartConstant.IBA_CATL_Maturity);
		if(StringUtils.equals("3", parentMaturity) || StringUtils.equals("6", parentMaturity)){
			Map<WTPart,Set<WTPart>> map = PromotionUtil.getOneLevelChild(parent);
			for(WTPart child : map.keySet()){
				WTPartMaster childMaster = (WTPartMaster) child.getMaster();
				String childMaturity = (String) IBAUtil.getIBAValue(childMaster, PartConstant.IBA_CATL_Maturity);
				if(childMaturity == null || !(StringUtils.equals(childMaturity, "3") || StringUtils.equals(childMaturity, "6"))){
					msg.append(WTMessage.formatLocalizedMessage("{0}的直接下层子件{1}的“成熟度”必须为“3”或者“6”！\r\n", new Object[]{parent.getNumber(),child.getNumber()}));
				}
				for(WTPart substitute : map.get(child)){
					WTPartMaster substituteMaster = (WTPartMaster) substitute.getMaster();
					String substituteMaturity = (String) IBAUtil.getIBAValue(substituteMaster, PartConstant.IBA_CATL_Maturity);
					if(substituteMaturity == null || !(StringUtils.equals(substituteMaturity, "3") || StringUtils.equals(substituteMaturity, "6"))){
						msg.append(WTMessage.formatLocalizedMessage("{0}的直接下层子件{1}的替代件{2}的“成熟度”必须为“3”或者“6”！ \r\n", new Object[]{parent.getNumber(), child.getNumber(), substitute.getNumber()}));
					}
				}
			}
		}
	}

	private static void writeLogFile(String msg) throws Exception{
		String filePathName = getLogFilePathName();
		FileWriter fw = null;
		try{
			File txtFile = new File(filePathName);
			if(txtFile.exists()){
				txtFile.delete();
			}
			fw = new FileWriter(filePathName, true);
			fw.write(msg);
		}
		finally{
			fw.flush();
			fw.close();
		}
	}
	
	private static String getLogFilePathName(){
		StringBuilder filePathName = new StringBuilder(WCLocationConstants.WT_LOG);
		filePathName.append(File.separator).append("check_allPartsMaturity_");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		filePathName.append(sdf.format(new Date())).append(".log");
		return filePathName.toString();
	}
}
