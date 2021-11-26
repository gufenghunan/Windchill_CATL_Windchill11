package com.catl.change.processors;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import wt.enterprise.RevisionControlled;
import wt.part.WTPart;
import wt.util.WTException;

import com.catl.bom.workflow.BomWfUtil;
import com.catl.change.ChangeUtil;
import com.catl.common.constant.PartState;
import com.catl.common.util.CatiaCheck;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;

public class CatlcheckChangeTaskAffectData {

	static List affectedItems = null;
	private static Logger logger = Logger.getLogger(CatlcheckChangeTaskAffectData.class.getName());

	public CatlcheckChangeTaskAffectData(NmCommandBean clientData) {
		this.affectedItems = clientData.getAddedItemsByName("changeTask_affectedItems_table");
		logger.debug("affected items size()=====" + affectedItems.size());

	}

	public static String validateReleaseByAffectedItems() throws WTException {
		logger.debug("start to check ---------->");
		StringBuffer message = new StringBuffer("");
		List<WTPart> retListPart = new ArrayList<WTPart>();
		for (int i = 0; i < affectedItems.size(); i++) {
			NmOid nmOid = (NmOid) affectedItems.get(i);
			Object object = nmOid.getLatestIterationObject();

			if (object instanceof RevisionControlled) {
				RevisionControlled revisionControlled = (RevisionControlled) object;
				logger.debug("object number==" + BomWfUtil.getObjectnumber(revisionControlled));
				if (!revisionControlled.getState().getState().toString().equals(PartState.RELEASED)) {
					message.append(BomWfUtil.getObjectnumber(revisionControlled) + ",不是已发布的状态，不能添加到受影响列表中！\n");
				}
				if (BomWfUtil.isECAchange(revisionControlled)) {
					message.append(BomWfUtil.getObjectnumber(revisionControlled) + ",有正在进行中的变更活动，不能添加到受影响列表中！\n");
				}
				ChangeUtil.checkMaturityType(message,retListPart,revisionControlled);
			}
		}
		CatiaCheck.check2D3D(affectedItems);
		return message.toString();
	}

	public static String validateReleaseByAffectedItemsByDca() throws WTException {
		logger.debug("start to validateReleaseByAffectedItemsByDca ---------->");
		StringBuffer msg = CatlCreateChangeTaskFormProcessor.validateReleaseByAffectedItemsByDca(affectedItems);
		logger.debug("end to validateReleaseByAffectedItemsByDca ---------->");
		CatiaCheck.check2D3D(affectedItems);
		return msg.toString();
	}

}
