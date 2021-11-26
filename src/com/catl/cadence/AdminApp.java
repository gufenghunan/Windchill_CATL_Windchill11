package com.catl.cadence;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.catl.cadence.CadenceHelper;
import com.catl.cadence.conf.InitSystemConfigContant;
import com.catl.cadence.util.NodeUtil;
import com.catl.ecad.utils.ECADutil;
import com.catl.ecad.utils.HistoryUtils;
import com.ptc.core.lwc.server.LWCStructEnumAttTemplate;

import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.maturity.MaturityHelper;
import wt.maturity.PromotionNotice;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.util.WTException;
import wt.util.WTProperties;

public class AdminApp {
	private static String INIT = "-init";
	private static String ADD_COLUMN = "-addTableColumn";
	private static String ADD_PARTINFO = "-releasePart";

	private static String LOG_FILE_PATH = null;
	private static PrintStream logout = null;
	static {
		String wtHomePath = null;
		try {
			wtHomePath = WTProperties.getServerProperties().getProperty(
					"wt.home");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		LOG_FILE_PATH = wtHomePath + File.separator + "logs" + File.separator
				+ "InitForItegradeCadence.log";
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) {
		RemoteMethodServer rms = RemoteMethodServer.getDefault();
		if (args.length > 2) {
			rms.setUserName(args[1]);
			rms.setPassword(args[2]);
		}
		try {
			// TODO Auto-generated method stub
			boolean needhelp = false;
			if (args.length > 0) {
				String action = args[0];
				if (INIT.equals(action)) {
					createAttrTable();// 创建cadence表
				} else if (ADD_COLUMN.equals(action)) {
					addTableColumn();// cadence表新增列（必需已配置相关信息）
				} else if (ADD_PARTINFO.equals(action)) {
					addPartAttribute(null);// 发送part信息到cadence表
				} else
					needhelp = true;
			} else
				needhelp = true;
			if (needhelp)
				help();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (logout != null) {
				try {
					logout.flush();
					logout.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					logout = null;
				}

			}
		}
		System.exit(0);
	}

	/**
	 * 添加字段
	 */
	private static void addTableColumn() {
		try {
			writeLog("***********Create Table Column  Begin**************");
			CadenceHelper.service.addTableColumn();
			writeLog("***********Create Table Column End**************");
		} catch (WTException e) {
			// TODO Auto-generated catch block
			writeLog(e);
		}
	}

	/**
	 * 创建属性表
	 */
	private static void createAttrTable() {
		try {
			writeLog("***********Create Table Begin**************");
			CadenceHelper.service.createAttrTable();
			writeLog("***********Creat Table End**************");
		} catch (WTException e) {
			// TODO Auto-generated catch block
			writeLog(e);
		}
	}

	/**
	 * 发送part信息至cadence
	 * 
	 * @param obj
	 * @throws Exception 
	 */
	public static void addPartAttribute(WTObject obj) throws Exception {
		writeLog("*******Release PartInfo To Table Begin****************");
		WTPart part = null;
		if (obj instanceof WTPart) {
			part = (WTPart) obj;
			CadenceHelper.service.addPartAttribute(part);
		} else if (obj instanceof PromotionNotice) {
			PromotionNotice notice = (PromotionNotice) obj;
			QueryResult qr = MaturityHelper.service.getPromotionTargets(notice);
			while (qr.hasMoreElements()) {
				Object object = qr.nextElement();
				if (object instanceof WTPart) {
					part = (WTPart) object;
					LWCStructEnumAttTemplate lwc=NodeUtil.getLWCStructEnumAttTemplateByPart(part);
					if(lwc==null){
						continue;
					}
					String pn =lwc.getName();
					Map<String, List<String>> mapList = HistoryUtils
							.getClfNumber();
					List<String> pcbList = mapList.get("PCB");
					if (pcbList.contains(pn)) {
						CadenceHelper.service.addPartAttribute(part);
					}
				}
			}
		}
		writeLog("*******Release PartInfo To Table End****************");
	}

	/**
	 * 发送part信息至cadence
	 * 
	 * @param obj
	 * @throws Exception 
	 */
	public static void updatePartState(WTObject obj) throws Exception {
		writeLog("*******Update Part State To Table Begin****************");
		WTPart part = null;
		if (obj instanceof WTPart) {
			part = (WTPart) obj;
			CadenceHelper.service.updatePartState(part);
		} else if (obj instanceof PromotionNotice) {
			PromotionNotice notice = (PromotionNotice) obj;
			QueryResult qr = MaturityHelper.service.getPromotionTargets(notice);
			while (qr.hasMoreElements()) {
				Object object = qr.nextElement();
				if (object instanceof WTPart) {
					part = (WTPart) object;
					LWCStructEnumAttTemplate lwc=NodeUtil.getLWCStructEnumAttTemplateByPart(part);
					if(lwc==null){
						continue;
					}
					String pn =lwc.getName();
					Map<String, List<String>> mapList = HistoryUtils.getClfNumber();
					List<String> pcbList = mapList.get("PCB");
					//List<String> other = InitSystemConfigContant.init().getInitSystemNodeOther();
					
					if (ECADutil.isElectronicPart(part)||pcbList.contains(pn)) {
						CadenceHelper.service.updatePartState(part);
					}
				}
			}
		}
		writeLog("*******Update Part State To Table End****************");
	}
	
	private static void writeLog(Object message) {
		try {
			if (logout == null) {
				File logFile = new File(LOG_FILE_PATH);
				if (!logFile.exists())
					logFile.createNewFile();
				logout = new PrintStream(new FileOutputStream(logFile, true));
			}
			logout.write(("\n" + new Date() + ":").getBytes());
			logout.write(message.toString().getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(message);
	}

	private static void writeLog(Exception e) {
		try {
			if (logout == null) {
				File logFile = new File(LOG_FILE_PATH);
				if (!logFile.exists())
					logFile.createNewFile();
				logout = new PrintStream(new FileOutputStream(logFile, true));
			}
			logout.write(("\n" + new Date() + ":").getBytes());
			e.printStackTrace(logout);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		e.printStackTrace();
	}

	private static void help() {
		System.out.println(helpString);
	}

	private static String helpString = "此工具为管理员所做操作，目的是与Cadence整合的初始化"
			+ "\n\n -init		        \t根据配置文档建库"
			+ "\n\n -addTableColumn	\t在维护时，根据配置文档添加字段"
			+ "\n\n -releasePart	    \t批量将part的相关属性值发送至Cadence"
			+ "\n\n***********************************************************";

}
