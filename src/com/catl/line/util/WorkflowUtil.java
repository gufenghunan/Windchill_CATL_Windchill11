package com.catl.line.util;

import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import wt.doc.WTDocument;
import wt.epm.EPMAuthoringAppType;
import wt.fc.ObjectReference;
import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.maturity.Promotable;
import wt.maturity.PromotionNotice;
import wt.org.OrganizationServicesHelper;
import wt.pom.Transaction;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.workflow.engine.WfActivity;
import wt.workflow.work.WorkItem;
import wt.workflow.work.WorkflowHelper;

import com.catl.common.util.PropertiesUtil;
import com.catl.line.constant.ConstantLine;
import com.catl.line.helper.CadHelper;
import com.ptc.xworks.windchill.util.PromotionNoticeUtils;

public class WorkflowUtil {
	/**
	 * dwg签名 更新文档主内容与附件
	 * @param pbo
	 * @throws WTException
	 * @throws RemoteException
	 * @throws WTPropertyVetoException
	 */
	public static void signature(WTObject pbo)throws WTException, RemoteException, WTPropertyVetoException {
		SessionServerHelper.manager.setAccessEnforced(false);
		try{
		Map<String,String> signaturemap=new HashMap<String,String>();
		if(pbo instanceof PromotionNotice){
			PromotionNotice pn=(PromotionNotice) pbo;
			QueryResult qrWorkItem = WorkflowHelper.service.getWorkItems(pn);
			WorkItem wi = null;
			ObjectReference source = null;
			WfActivity ac = null;
			//获取签名信息
			while (qrWorkItem.hasMoreElements()) {
				wi = (WorkItem) qrWorkItem.nextElement();
				if (null == wi.getCompletedBy()) {
					break;
				}
				source = wi.getSource();
				ac = (WfActivity) source.getObject();
                 String[] activitys=ConstantLine.config_signature_activity;
                 String[] sign_users=ConstantLine.config_signature;
                 System.out.println(ac.getName());
                 int index=Arrays.asList(activitys).indexOf(ac.getName());
                 if(index>-1){
                	 String[] sign_user=sign_users[index].split("\\|");
                	 try{
                     signaturemap.put(sign_user[0], OrganizationServicesHelper.manager.getAuthenticatedUser(wi.getCompletedBy()).getFullName());
                     signaturemap.put(sign_user[1], new SimpleDateFormat(ConstantLine.config_signature_dataformat).format(ac.getEndTime()));
                	 }catch(Exception e){
                		 System.out.println("未获取到节点"+ac.getName()+"信息");
                	 }
                 }
			}
		Transaction trx = null;
		  try {
		   trx = new Transaction();
		   trx.start();
			 Collection<Promotable> objs=PromotionNoticeUtils.getPromotionNoticeItems((PromotionNotice) pn);
			 Object[] arrayobjs=objs.toArray();
             for (int i = 0; i < arrayobjs.length; i++) {
            	 Object obj=arrayobjs[i];
            	 if(obj instanceof WTDocument){
            		 WTDocument doc=(WTDocument) obj;
            		 String state=doc.getLifeCycleState().getDisplay(Locale.CHINA);
            		 if(PropertiesUtil.getValueByKey("config_nosignature_state").indexOf(state)==-1){
            				 if(doc.getNumber().length()>11){//不是母PN的编码
            					 CadHelper.updateDocDwgAndPDF(doc.getNumber(), signaturemap);
            				 }
            		 }
 				 }
			}
            trx.commit();
		  }catch(Exception e){
			  e.printStackTrace();
			  trx.rollback();
		  }
			
		}
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}finally{
			SessionServerHelper.manager.setAccessEnforced(true);
		}
	}	
	
	
}
