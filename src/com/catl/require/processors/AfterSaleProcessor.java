package com.catl.require.processors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import wt.access.AccessControlHelper;
import wt.access.AccessPermission;
import wt.lifecycle.LifeCycleException;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.State;
import wt.part.WTPart;
import wt.pom.Transaction;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.util.WTInvalidParameterException;
import wt.util.WTPropertyVetoException;

import com.catl.common.constant.PartState;
import com.catl.line.util.CommonUtil;
import com.catl.line.util.PNUtil;
import com.catl.line.util.WCUtil;
import com.catl.require.util.LinkUtil;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.forms.FormResultAction;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;

public class AfterSaleProcessor extends DefaultObjectFormProcessor {

	@Override
	public FormResult doOperation(NmCommandBean clientData, List<ObjectBean> objectBeans) throws WTException {
		FormResult result=new FormResult(FormProcessingStatus.SUCCESS);
		Transaction trx = null;
		try{
		trx=new Transaction();
		trx.start();
		SessionServerHelper.manager.setAccessEnforced(false);
		HashMap map=clientData.getAddedItems();
		ArrayList oids=(ArrayList) map.get("newaftersalebuilder");
		if(oids.size()==0){
			throw new WTException("请先添加部件!");
		}
		String folderoid=clientData.getActionOid().getOid().getStringValue();
		List<WTPart> afterverifyparts=new ArrayList<WTPart> ();
		for (int i = 0; i < oids.size(); i++) {
			NmOid oid=(NmOid) oids.get(i);
			String partoid=oid.toString();
			WTPart ppart=(WTPart) WCUtil.getWTObject(partoid);
			if(ppart.getNumber().endsWith("S")&&!ppart.getNumber().endsWith("-S")){
				throw new WTException("售后再利用件不允许创建售后再利用件!");
			}
//			if(!ppart.getLifeCycleState().equals(State.toState(PartState.RELEASED))){
//				throw new WTException("只允许已发布状态的部件创建售后再利用件!");
//			}
			afterverifyparts.add(ppart);
		}
		
		for (int i = 0; i < afterverifyparts.size(); i++) {
			WTPart ppart=afterverifyparts.get(i);
			newAfterSale(ppart,ppart.getNumber()+"S",WCUtil.getOid(clientData.getContainer()),folderoid);
		}
		trx.commit();
		}catch(WTException e){
			trx.rollback();
			e.printStackTrace();
			throw e;
		} catch (WTInvalidParameterException e) {
			trx.rollback();
			e.printStackTrace();
			throw new WTException(e.getLocalizedMessage());
		} catch (WTPropertyVetoException e) {
			trx.rollback();
			e.printStackTrace();
			throw new WTException(e.getLocalizedMessage());
		}finally{
			SessionServerHelper.manager.setAccessEnforced(true);
		}
		result.setJavascript("reloadOpenerTable()");
		result.setNextAction(FormResultAction.JAVASCRIPT);
		return result;
	}
	
	public static void newAfterSale(WTPart mpn,String newnum,String containeroid,String folderoid) throws WTInvalidParameterException, LifeCycleException, WTException, WTPropertyVetoException{
		CommonUtil.PartSaveAs(mpn, newnum, containeroid, folderoid);
		WTPart part = CommonUtil.getPartByNumber(newnum);
		if (part == null) {
			throw new WTException("创建售后再用件"+newnum+"失败");
		}else{
			SessionServerHelper.manager.setAccessEnforced(true);
			boolean access = AccessControlHelper.manager.hasAccess(
					SessionHelper.getPrincipal(), part,
					AccessPermission.CREATE);
			if(!access){
				throw new WTException("您没有权限创建部件");
			}
			SessionServerHelper.manager.setAccessEnforced(false);
		}
		LinkUtil.removePartDescriptionLink(part);
		LinkUtil.removePartReferenceLink(part);
		LinkUtil.removeEPMBuildSource(part);
		//CommonUtil.reassign(part);// 重新分配生命周期
		LifeCycleHelper.service.setLifeCycleState(part,
				State.toState("DESIGN"), true);
		
	}

}