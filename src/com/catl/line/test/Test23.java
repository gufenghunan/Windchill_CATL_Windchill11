package com.catl.line.test;

import java.util.HashMap;
import java.util.Map;

import wt.fc.Persistable;
import wt.fc.QueryResult;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.part.WTPartConfigSpec;
import wt.part.WTPartHelper;
import wt.part.WTPartMaster;
import wt.part.WTPartStandardConfigSpec;
import wt.part.WTPartUsageLink;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.catl.ecad.utils.CommonUtil;
import com.catl.line.constant.ConstantLine;
import com.catl.line.exception.LineException;
import com.catl.line.util.IBAUtility;
import com.catl.line.util.NodeUtil;
import com.catl.line.util.PNUtil;
import com.ptc.core.lwc.server.LWCStructEnumAttTemplate;

public class Test23 implements RemoteAccess{
public static void main(String[] args) throws Exception {
	RemoteMethodServer rms = RemoteMethodServer.getDefault();
    rms.setUserName(args[0]);
    rms.setPassword(args[1]);
	rms.invoke("create", Test23.class.getName(), null, null, null);
	
}
public static void create() throws Exception{
	WTPart part=CommonUtil.getLatestWTpartByNumber("550250-M0060005");
	WTPart currentpart=CommonUtil.getLatestWTpartByNumber("130200-00035");
	System.out.println(part.getVersionIdentifier().getValue()+"."+part.getIterationIdentifier().getValue());
	boolean flag=isBigBellows(part, currentpart);
	System.out.println("========="+flag);
}
public static boolean isBigBellows(WTPart part, WTPart currentpart)
		throws WTException, WTPropertyVetoException {
	Map<String, String> result = new HashMap<String, String>();
	double probellowsval = 0.0;
	QueryResult qr = WTPartHelper.service.getUsesWTPartMasters(part);
	System.out.println("-----------"+qr.size());
	do {
		WTPartUsageLink link = (WTPartUsageLink) qr.nextElement();
		WTPartMaster cpartmaster = (WTPartMaster) link.getRoleBObject();
		WTPart cpart = (WTPart) CommonUtil.getLatestVersionOf(cpartmaster);
		LWCStructEnumAttTemplate lwc = NodeUtil
				.getLWCStructEnumAttTemplateByPart(cpart);
		if (lwc != null) {
			if (lwc.getName().startsWith(ConstantLine.config_bom_type1)) {
				IBAUtility iba = new IBAUtility(cpart);
				String inner_Diameter_Str = iba.getIBAValue(ConstantLine.var_inner_diameter);
				System.out.println(inner_Diameter_Str);
				double bellowsval = 0.0;
				if (inner_Diameter_Str != null) {
					inner_Diameter_Str = inner_Diameter_Str.split(" ")[0];
					bellowsval = Double.valueOf(inner_Diameter_Str);
				}
				if (probellowsval == 0.0) {
					result.put(ConstantLine.config_bom_type1_name,cpart.getNumber());
					probellowsval = bellowsval;
				} else {
					String prenumber = result.get(ConstantLine.config_bom_type1_name);
					if (result.containsKey(ConstantLine.config_bom_type1_name1)) {
						throw new LineException(ConstantLine.exception_bellowscounterror);
					}
					if (bellowsval > probellowsval) {
						System.out.println(ConstantLine.config_bom_type1_name1+"============"+cpart.getNumber());
						System.out.println(ConstantLine.config_bom_type1_name2+"============"+prenumber);
						result.put(ConstantLine.config_bom_type1_name1,
								cpart.getNumber());
						result.put(ConstantLine.config_bom_type1_name2,
								prenumber);
					} else {
						result.put(ConstantLine.config_bom_type1_name1,
								prenumber);
						result.put(ConstantLine.config_bom_type1_name2,
								cpart.getNumber());
					}
				}

			}
		}
	} while (qr.hasMoreElements());
	System.out.println(result.size());
	if (result.size() != 3) {
		throw new LineException("找不到波纹管或波纹管数目少于两个!");
	}
	System.out.println(result);
	if (result.get(ConstantLine.config_bom_type1_name1).equals(currentpart.getNumber())) {
		return true;
	} else {
		return false;
	}

}
}
