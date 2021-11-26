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

@WebService(serviceName = "PartFaeStatusUpdate")
public class PartFaeStatusUpdate {

	@WebMethod(operationName = "updateFaeStatus")
	public ProcessResult updateFaeStatus(PartFaeInfo infos[]) {

		String filePathName = WCLocationConstants.WT_CODEBASE + File.separator + "config" + File.separator + "custom" + File.separator
				+ "configuration.properties";

		// 开始准备日志记录所需的信息
		WebServiceTransactionLog transactionLog = new WebServiceTransactionLog();
		try {
			transactionLog.setServiceType(WebServiceTransactionLog.SERVICE_TYPE_PROVIDER);
			transactionLog.setServiceSide("PLM");
			transactionLog.setClientSide("ERP");
			transactionLog.setClientId(SessionHelper.getPrincipal().getName());
			transactionLog.setServiceClass(PartFaeStatusUpdate.class.getName());
			transactionLog.setServiceMethod("updateFaeStatus");
			transactionLog.setStartTime(new Timestamp(System.currentTimeMillis()));
		} catch (WTException e1) {
			e1.printStackTrace();
		}

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
						PartFaeInfo PartFaeInfo = infos[i];
						String number = PartFaeInfo.getPartNumber();
						String FaeStatus = PartFaeInfo.getFaeStatus();
						QueryResult qr = PromotionUtil.queryPartMaster(number);
						if (qr.size() < 1) {
							ItemResult itemResult = new ItemResult();
							itemResult.setResult("1");
							itemResult.setNumber(number);
							itemResult.setMessage("编码为：" + number + "的零部件不存在！");
							lists.add(itemResult);
						}

						if (!(StringUtils.equals(FaeStatus, PartConstant.CATL_FAEStatus_4) || StringUtils.equals(FaeStatus, PartConstant.CATL_FAEStatus_5))) {
							ItemResult itemResult = new ItemResult();
							itemResult.setResult("2");
							itemResult.setNumber(number);
							itemResult.setMessage("零部件：" + number + "的FAE状态" + FaeStatus + "不是合法的值！");
							lists.add(itemResult);
						}

						if (qr.size() > 0
								&& (StringUtils.equals(FaeStatus, PartConstant.CATL_FAEStatus_4) || StringUtils
										.equals(FaeStatus, PartConstant.CATL_FAEStatus_5))) {
							while (qr.hasMoreElements()) {
								Transaction transaction = null;
								boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
								try {
									transaction = new Transaction();
									transaction.start();

									WTPartMaster master = (WTPartMaster) qr.nextElement();
									String FAEStatus = (String) IBAUtil.getIBAValue(master, PartConstant.IBA_CATL_FAEStatus);
									if (StringUtils.equals(FAEStatus, PartConstant.CATL_FAEStatus_3)
											&& StringUtils.equals(FaeStatus, PartConstant.CATL_FAEStatus_4)) {

										PersistenceHelper.manager.save(IBAUtil.setIBAVaue(master, PartConstant.IBA_CATL_FAEStatus,
												PartConstant.CATL_FAEStatus_4));
										PersistenceHelper.manager.save(IBAUtil.setIBAVaue(master, PartConstant.IBA_CATL_Maturity, "3"));

										ItemResult itemResult = new ItemResult();
										itemResult.setResult("0");
										itemResult.setNumber(number);
										itemResult.setMessage("零部件：" + number + "的FAE状态已更新为“已完成”！");
										lists.add(itemResult);
										PartMaturityChangeLogHelper.addPartMaturityChangeLog(SessionHelper.manager.getPrincipalReference(), PartUtil.getLastestWTPartByNumber(master.getNumber()), "1", "3");

									} else if (StringUtils.equals(FAEStatus, PartConstant.CATL_FAEStatus_3)
											&& StringUtils.equals(FaeStatus, PartConstant.CATL_FAEStatus_5)) {

										PersistenceHelper.manager.save(IBAUtil.setIBAVaue(master, PartConstant.IBA_CATL_FAEStatus,
												PartConstant.CATL_FAEStatus_2));

										ItemResult itemResult = new ItemResult();
										itemResult.setResult("0");
										itemResult.setNumber(number);
										itemResult.setMessage("零部件：" + number + "的FAE状态已更新为“未发起”！");
										lists.add(itemResult);

									} else if (!StringUtils.equals(FAEStatus, PartConstant.CATL_FAEStatus_3)) {

										ItemResult itemResult = new ItemResult();
										itemResult.setResult("0");
										itemResult.setNumber(number);
										itemResult.setMessage("零部件：" + number + "的FAE状态未做任何更新！");
										lists.add(itemResult);
									}

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
