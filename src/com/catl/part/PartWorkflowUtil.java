package com.catl.part;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import wt.epm.EPMDocument;
import wt.fc.QueryResult;
import wt.maturity.MaturityException;
import wt.maturity.MaturityHelper;
import wt.maturity.PromotionNotice;
import wt.part.WTPart;
import wt.util.WTException;
import wt.util.WTMessage;
import wt.vc.wip.WorkInProgressHelper;

import com.catl.bom.workflow.BomWfUtil;
import com.catl.common.constant.PartState;
import com.catl.common.util.EpmUtil;
import com.catl.common.util.PartUtil;
import com.ptc.windchill.enterprise.part.commands.PartDocServiceCommand;

public class PartWorkflowUtil {
	private static Logger log = Logger.getLogger(BomWfUtil.class.getName());

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public static StringBuilder checkPromotionTargets(PromotionNotice pn) {
		StringBuilder message = new StringBuilder();
		String pncreator = pn.getCreatorName();
		log.debug("pn creator=" + pncreator);
		QueryResult qr = new QueryResult();
		try {
			qr = MaturityHelper.service.getPromotionTargets(pn);
		} catch (MaturityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		while (qr.hasMoreElements()) {
			Object object = (Object) qr.nextElement();
			if (object instanceof WTPart) {
				WTPart part = (WTPart) object;
				String partcreator = part.getCreatorName();
				log.debug("part creator==" + partcreator);
				if (!partcreator.equalsIgnoreCase(pncreator)) {
					message.append("\n部件：" + part.getNumber() + ",不是本人创建的部件 \n");
				}
				if (!part.getState().toString().startsWith("WRITING")) {
					message.append("部件：" + part.getNumber()
							+ ",状态不符合业务规范，只能添加生命周期为‘编制’状态下的部件");
				}
			}

		}
		return message;
	}

	public static void checkPartPDFInfo(WTPart part, StringBuilder message)
			throws WTException {
		if (part != null) {
			String partNumber = part.getNumber();
			String checkStr = null;
			if (partNumber.length() > 6) {
				checkStr = part.getNumber().substring(0, 6);
			} else {
				checkStr = partNumber;
			}
			boolean need2dAutoCAD = false;
			int mnum = 0;
			String msg1 = null;
			String msg2 = null;
			Set<String> needCheck2DInfo = CheckPDFConstants.needCheck2DInfo;
			// System.out.println("===checkStr:"+checkStr);
			Set<String> needCheckInfo = CheckPDFConstants.needCheckInfo;
			// System.out.println("===needCheckInfo:"+needCheckInfo.toString());
			if (needCheck2DInfo.contains(checkStr)) {
				mnum = 1;
				System.out.println("need autocad");
				if (!PartUtil.hasPDF(part)) {
					msg1 = WTMessage.formatLocalizedMessage(
							"零部件[{0}]必需有PDF图纸或者PDF图纸的命名有误！\n",
							new Object[] { partNumber });
					// message.append(WTMessage.formatLocalizedMessage("零部件[{0}]必需有PDF图纸或者PDF图纸的命名有误！\n",
					// new Object[]{partNumber}));
				} else {
					need2dAutoCAD = true;
					System.out.println("Need 2D is true");
				}
			}

			Set<String> needCheck3DInfo = CheckPDFConstants.needCheck3DInfo;
			if (needCheck3DInfo.contains(checkStr)) {
				
				boolean flag = true;
				
				QueryResult qr =PartDocServiceCommand.getAssociatedCADDocuments(part);

				while(qr.hasMoreElements()){
					Object obj = qr.nextElement();
					if(obj instanceof EPMDocument){
						EPMDocument epmdoc = (EPMDocument) obj;
						if (epmdoc.getCADName().toUpperCase()
								.endsWith(".CATPART")
								|| epmdoc.getCADName().toUpperCase()
										.endsWith(".CATPRODUCT")) {
							flag = false;
						}
					}
				}
				
				
				if (flag) {
					message.append(WTMessage.formatLocalizedMessage(
							"零部件[{0}]必需有3D图纸！\n", new Object[] { partNumber }));
				}

			}

			if (!need2dAutoCAD) {
				
				if (needCheck2DInfo.contains(checkStr)) {
					if (mnum == 1) {
						mnum = 3;
					} else {
						mnum = 2;
					}

					boolean flag = true;
					
					QueryResult qr =PartDocServiceCommand.getAssociatedCADDocuments(part);

					while(qr.hasMoreElements()){
						Object obj = qr.nextElement();
						if(obj instanceof EPMDocument){
							EPMDocument epmdoc = (EPMDocument) obj;
							if (epmdoc.getCADName().toUpperCase()
									.endsWith(".CATDRAWING")) {
								flag = false;
							}
						}
					}
					
					
					if (flag) {
						msg2 = WTMessage.formatLocalizedMessage(
								"零部件[{0}]必需有2D图纸！\n",
								new Object[] { partNumber });
					} else {
						need2dAutoCAD = true;
					}

				}
				System.out.println("Need2d\t"+need2dAutoCAD+"\t"+mnum);
				if (!need2dAutoCAD) {
					if (mnum == 1) {
						message.append(msg1);
					} else if (mnum == 2) {
						message.append(msg2);
					} else if(mnum ==3){
						message.append(WTMessage.formatLocalizedMessage(
								"零部件[{0}]必需有2D图纸或者PDF图纸，或者PDF图纸的命名有误！\n",
								new Object[] { partNumber }));
					}
				}
			}
		}
	}

	public static void deletePDFAttachmentData(WTPart part) throws WTException {
		String version = part.getVersionIdentifier().getValue();
		if (!StringUtils.equals(version, "1")) {
			PartUtil.deletePDFAttachmentData(part);
		}
	}
}
