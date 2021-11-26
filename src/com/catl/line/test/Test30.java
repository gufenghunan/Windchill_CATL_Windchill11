package com.catl.line.test;

import java.util.ArrayList;
import java.util.Locale;

import oracle.net.aso.u;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.util.WTException;

import com.catl.ecad.utils.CommonUtil;
import com.catl.loadData.IBAUtility;
import com.ptc.core.HTMLtemplateutil.server.processors.EntityTaskDelegate;
import com.ptc.core.meta.common.AttributeTypeIdentifier;
import com.ptc.core.meta.container.common.AttributeTypeSummary;

public class Test30 implements RemoteAccess{
	public static String getPsize(Object clf) throws WTException{
        ArrayList vals = new ArrayList();
        ArrayList attributeTypeSummaries = new ArrayList();
        ArrayList attributeIdentifierStrings = new ArrayList();
        StringBuffer typeInstanceIdentifierString = new StringBuffer();
        ArrayList attributeStates = new ArrayList();
        EntityTaskDelegate.getSoftAttributes(clf, true, false, typeInstanceIdentifierString, attributeIdentifierStrings,
          attributeTypeSummaries, vals, attributeStates, Locale.ENGLISH);
        for(int i=0;i<attributeTypeSummaries.size();i++){
			 AttributeTypeSummary ats = (AttributeTypeSummary)attributeTypeSummaries.get(i);
			 String lable = ats.getLabel();
			 AttributeTypeIdentifier ati = ats.getAttributeTypeIdentifier();
			 String attrName = ati.getAttributeName();
			 System.out.println(attrName);
        }
		return null;
		
	}
	public static void main(String[] args) throws Exception {
		RemoteMethodServer rms = RemoteMethodServer.getDefault();
        rms.setUserName(args[0]);
        rms.setPassword(args[1]);
		rms.invoke("create", Test30.class.getName(), null, null, null);
		
	}
	public static void create() throws Exception{
		WTPart part=CommonUtil.getLatestWTpartByNumber("550151-M0010005");
		//IBAUtility utility=new IBAUtility(part);
		getPsize(part);
	}
}
