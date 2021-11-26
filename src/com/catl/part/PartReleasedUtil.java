package com.catl.part;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.eclipse.ui.internal.PartList;

import wt.fc.Persistable;
import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.httpgw.GatewayAuthenticator;
import wt.maturity.MaturityException;
import wt.maturity.MaturityHelper;
import wt.maturity.PromotionNotice;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.util.WTException;

import com.catl.bom.workflow.BomWfUtil;
import com.catl.common.util.PartUtil;
import com.catl.integration.ErpData;
import com.catl.integration.PartInfo;

public class PartReleasedUtil {

	private static Logger log=Logger.getLogger(PartReleasedUtil.class.getName());
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		RemoteMethodServer rms = RemoteMethodServer.getDefault();
		GatewayAuthenticator auth = new GatewayAuthenticator();
		auth.setRemoteUser("wcadmin");
		rms.setAuthenticator(auth);
		String oid=args[0];
		String oidString = "OR:wt.maturity.PromotionNotice:"+oid;
		Persistable persistable = BomWfUtil.getPersistableByOid(oidString);
		PromotionNotice pNotice = (PromotionNotice) persistable;
		//refresh PromotionNotice targets
		try {
			BomWfUtil.refreshPromotionTargets(pNotice);
		} catch (WTException e1) {
			e1.printStackTrace();
		}
		ErpData erpData=getPartReleaseData(pNotice);
		ArrayList<PartInfo> partinfolist=(ArrayList<PartInfo>) erpData.getParts();
		for (int i = 0; i < partinfolist.size(); i++) {
			PartInfo partInfo = (PartInfo) partinfolist.get(i);
			System.out.println("PartNumber=="+partInfo.getPartNumber());
			System.out.println("PartName=="+partInfo.getPartName());
			System.out.println("Source=="+partInfo.getSource());
			System.out.println("Version=="+partInfo.getDrawingVersion());
			System.out.println("Iteration=="+partInfo.getIteration());
			System.out.println("MaterialGroup=="+partInfo.getMaterialGroup());
			System.out.println("DefaultUnit=="+partInfo.getDefaultUnit());
		}

	}
	public static ErpData  getPartReleaseData(WTObject pbo)
	{
		
		ErpData data=new ErpData();
		ArrayList<PartInfo> partinfolist=new ArrayList<PartInfo>();
		
		if(pbo instanceof PromotionNotice)
		{
		PromotionNotice pn=(PromotionNotice)pbo;
		try {
			QueryResult PRTargetResult=MaturityHelper.service.getPromotionTargets(pn);
			while (PRTargetResult.hasMoreElements()) {
				Object object = (Object) PRTargetResult.nextElement();
				if (object instanceof WTPart)
				{
					WTPart part = (WTPart)object;
					PartInfo partInfo=new PartInfo();
					partInfo=PartUtil.getPartInfo(part);
					partinfolist.add(partInfo);
				}				
			}
		} catch (MaturityException e) {
			// TODO Auto-generated catch block
			log.debug("get promotionTarget MaturityException-----");
			e.printStackTrace();
		} catch (WTException e) {
			// TODO Auto-generated catch block
			log.debug("can not get promotionTarget WTException-----");
			e.printStackTrace();
		}
		}
		data.setParts(partinfolist);
		
		return data;
	}
}
