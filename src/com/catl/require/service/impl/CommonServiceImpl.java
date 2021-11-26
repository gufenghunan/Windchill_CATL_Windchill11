package com.catl.require.service.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.sf.json.JSONArray;

import org.drools.core.util.StringUtils;
import org.springframework.stereotype.Service;

import wt.clients.checker.checkerResource;
import wt.fc.WTObject;
import wt.inf.container.WTContainer;
import wt.maturity.Promotable;
import wt.maturity.PromotionNotice;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.util.WTRuntimeException;
import wt.workflow.work.WorkItem;

import com.catl.cadence.util.NodeUtil;
import com.catl.common.constant.ClassifyName;
import com.catl.common.util.ClassificationUtil;
import com.catl.line.util.CommonUtil;
import com.catl.line.util.IBAUtil;
import com.catl.line.util.IBAUtility;
import com.catl.line.util.WCUtil;
import com.catl.require.constant.ConstantRequire;
import com.catl.require.helper.CacheHelper;
import com.catl.require.service.CommonService;
import com.catl.require.util.PlatformUtil;
import com.catl.require.constant.GlobalData;
import com.ptc.core.lwc.server.LWCStructEnumAttTemplate;
import com.ptc.xworks.windchill.util.PromotionNoticeUtils;

@Service("commonservice")
public class CommonServiceImpl implements CommonService{

	private String oid;

	@Override
	public JSONArray getPlatformPartsInfo(String oid) throws WTRuntimeException, WTException {
		List list = new ArrayList();
		WorkItem workitem = (WorkItem) WCUtil.getWTObject(oid);
		WTObject promotionnotice = (WTObject) workitem
				.getPrimaryBusinessObject().getObject();
		if (promotionnotice instanceof PromotionNotice) {
			Collection<Promotable> objs = PromotionNoticeUtils
					.getPromotionNoticeItems((PromotionNotice) promotionnotice);
			Iterator<Promotable> objiter = objs.iterator();
			while (objiter.hasNext()) {
				Promotable promotable = objiter.next();
				if (promotable instanceof WTPart) {
				   WTPart part=(WTPart) promotable;
				   List partsinfo=new ArrayList();
				   partsinfo.add(part.getName());
				   partsinfo.add(part.getNumber());
				   String oldplatform=IBAUtil.getIBAStringValue(part, ConstantRequire.iba_CATL_Platform);
				   String newplatform="";
				   partsinfo.add(oldplatform);
				   if(oldplatform.equals("C")){
					   newplatform="A,B";
				   }else if(oldplatform.equals("B")){
					   newplatform="A";
				   }
				   partsinfo.add("");
				   partsinfo.add(newplatform);
				   list.add(partsinfo);
				}
				
			}
			
			return JSONArray.fromObject(list);
		} else {
			return null;
		}
	}

	@Override
	public String updateplatform(String partNumber, String name, String value) throws WTException, WTPropertyVetoException, RemoteException {
		WTPart part = CommonUtil.getLatestWTpartByNumber(partNumber);
		WTPartMaster partmaster=part.getMaster();
		PlatformUtil.checkChangePlatform(part, value);
		IBAUtility iba = new IBAUtility(partmaster);
		if (!StringUtils.isEmpty(value)) {
			iba.setIBAValue(name, value);
			iba.updateAttributeContainer(partmaster);
			iba.updateIBAHolder(partmaster);
		} else {
			iba.deleteIBAValueByLogical(name);
			iba.updateAttributeContainer(partmaster);
			iba.updateIBAHolder(partmaster);
		}
		return value;

	}

	public boolean ishideplatform(String containeroid) throws WTRuntimeException, WTException, FileNotFoundException, IOException {
		WTContainer container=(WTContainer) WCUtil.getWTObject(containeroid);
		String containername=container.getName();
		System.out.println("------当前产品库-----------"+containername);
		try {
			CacheHelper.loadExcelConfig();
		} catch (IOException e) {
			throw new WTException("加载隐藏产品线标识配置表出错");
		}
		for (int i = 0; i < GlobalData.config_hide_platform.size(); i++) {
			String name=(String) GlobalData.config_hide_platform.get(i);
			if(!StringUtils.isEmpty(name)&&containername.startsWith(name)){
				return true;
			}
		}
		return false;
	}

	@Override
	public void validateplatform(String partNumber, String value) throws WTException {
		WTPart part = CommonUtil.getLatestWTpartByNumber(partNumber);
		WTPartMaster partmaster=part.getMaster();
		PlatformUtil.checkChangePlatform(part, value);
	}

}
