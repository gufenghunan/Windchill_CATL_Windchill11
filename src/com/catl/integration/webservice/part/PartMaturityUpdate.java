package com.catl.integration.webservice.part;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.apache.commons.lang.StringUtils;

import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.part.WTPartMaster;
import wt.pom.Transaction;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.util.WTException;

import com.catl.common.util.IBAUtil;
import com.catl.common.util.PartUtil;
import com.catl.common.util.WCLocationConstants;
import com.catl.integration.log.TransactionLogHelper;
import com.catl.integration.log.WebServiceTransactionInfo;
import com.catl.integration.log.WebServiceTransactionLog;
import com.catl.integration.webservice.ItemResult;
import com.catl.integration.webservice.ProcessResult;
import com.catl.part.PartConstant;
import com.catl.part.maturity.PartMaturityChangeLogHelper;
import com.catl.promotion.util.PromotionUtil;

@WebService(serviceName = "PartMaturityUpdate")
public class PartMaturityUpdate {

	@WebMethod(operationName = "updateMaturity")
	public ProcessResult updateMaturity(PartMaturityInfo infos[]) {

		String filePathName = WCLocationConstants.WT_CODEBASE + File.separator + "config" + File.separator + "custom" + File.separator
				+ "configuration.properties";

		// 开始准备日志记录所需的信息
		WebServiceTransactionLog transactionLog = new WebServiceTransactionLog();
		try {
			transactionLog.setServiceType(WebServiceTransactionLog.SERVICE_TYPE_PROVIDER);
			transactionLog.setServiceSide("PLM");
			transactionLog.setClientSide("ERP");
			transactionLog.setClientId(SessionHelper.getPrincipal().getName());
			transactionLog.setServiceClass(PartMaturityUpdate.class.getName());
			transactionLog.setServiceMethod("updateMaturity");
			transactionLog.setStartTime(new Timestamp(System.currentTimeMillis()));
		} catch (WTException e1) {
			e1.printStackTrace();
		}

		// Web Service的处理逻辑在此
		ProcessResult result = new ProcessResult();
		List<ItemResult> lists = new ArrayList<ItemResult>();

		try {
			if (infos.length > 0) {
				for (int i = 0; i < infos.length; i++) {
					String user = SessionHelper.getPrincipal().getName();
					FileInputStream file = new FileInputStream(filePathName);
					Properties properties = new Properties();
					properties.load(file);
					String users = properties.getProperty("webservice.clientUser.pi");
					if (users.contains(user)) {
						PartMaturityInfo PartMaturityInfo = infos[i];
						String PartNumber = PartMaturityInfo.getPartNumber();
						String Maturity = PartMaturityInfo.getMaturity();

						QueryResult qr = PromotionUtil.queryPartMaster(PartNumber);
						if (qr.size() < 1) {
							ItemResult itemResult = new ItemResult();
							itemResult.setResult("1");
							itemResult.setNumber(PartNumber);
							itemResult.setMessage("物料编码：" + PartNumber + "在系统中不存在！");
							lists.add(itemResult);
						}

						if (!(StringUtils.equals(Maturity, "3") || StringUtils.equals(Maturity, "6"))) {
							ItemResult itemResult = new ItemResult();
							itemResult.setResult("2");
							itemResult.setNumber(PartNumber);
							itemResult.setMessage("物料编码：" + PartNumber + "的成熟度" + Maturity + "不合法！");
							lists.add(itemResult);
						}

						if (qr.size() > 0 && (StringUtils.equals(Maturity, "3") || StringUtils.equals(Maturity, "6"))) {
							while (qr.hasMoreElements()) {
								WTPartMaster master = (WTPartMaster) qr.nextElement();
								String maturity = (String) IBAUtil.getIBAValue(master, PartConstant.IBA_CATL_Maturity);
								if (StringUtils.equals(maturity, "1") && (StringUtils.equals(Maturity, "3") || StringUtils.equals(Maturity, "6"))) {
									ItemResult itemResult = new ItemResult();
									itemResult.setResult("3");
									itemResult.setNumber(PartNumber);
									itemResult.setMessage("物料：" + PartNumber + "当前成熟度为1，不允许直接升级为3或6！");
									lists.add(itemResult);
								} else if (StringUtils.equals(maturity, "3") && StringUtils.equals(Maturity, "3")) {
									ItemResult itemResult = new ItemResult();
									itemResult.setResult("4");
									itemResult.setNumber(PartNumber);
									itemResult.setMessage("物料：" + PartNumber + "成熟度当前值已经为3，无需更新！");
									lists.add(itemResult);
								} else if (StringUtils.equals(maturity, "3") && StringUtils.equals(Maturity, "6")) {

									boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
									Transaction transaction = null;
									try {
										transaction = new Transaction();
										transaction.start();

										PersistenceHelper.manager.save(IBAUtil.setIBAVaue(master, PartConstant.IBA_CATL_Maturity, "6"));
										PartMaturityChangeLogHelper.addPartMaturityChangeLog(SessionHelper.manager.getPrincipalReference(), PartUtil.getLastestWTPartByNumber(master.getNumber()), "3", "6");

										transaction.commit();
										transaction = null;
									} catch (WTException e) {
										e.printStackTrace();
									} finally {
										if (transaction != null) {
											transaction.rollback();
											transaction = null;
										}
										SessionServerHelper.manager.setAccessEnforced(flag);
									}

									ItemResult itemResult = new ItemResult();
									itemResult.setResult("0");
									itemResult.setNumber(PartNumber);
									itemResult.setMessage("物料：" + PartNumber + "当前成熟度已经更新为6。");
									lists.add(itemResult);
								}
							}
						}
					} else {
						ItemResult itemResult = new ItemResult();
						itemResult.setResult("9");
						itemResult.setNumber(user);
						itemResult.setMessage("调用此接口的用户不在" + filePathName + "文件参数“webservice.clientUser.pi”指定的用户账中！");
						lists.add(itemResult);
					}
				}
			}

		} catch (WTException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		ItemResult[] items = lists.toArray(new ItemResult[lists.size()]);
		result.setItemResults(items);
		String temp = "";
		for (ItemResult item : lists) {
			if (item.getResult().equals("0")) {
				temp = "0";
			} else {
				temp = "1";
				break;
			}
		}
		result.setResult(temp);

		// 记录交易日志的结果信息的信息
		transactionLog.setFinishTime(new Timestamp(System.currentTimeMillis()));
		transactionLog.setStatus(WebServiceTransactionLog.STATUS_SUCCESS);
		WebServiceTransactionInfo transactionInfo = new WebServiceTransactionInfo();
		transactionLog.setTransactionInfo(transactionInfo);
		transactionInfo.setParameterObject(infos);
		transactionInfo.setResultObject(result);
		TransactionLogHelper.logTransaction(transactionLog);

		return result;
	}
}
