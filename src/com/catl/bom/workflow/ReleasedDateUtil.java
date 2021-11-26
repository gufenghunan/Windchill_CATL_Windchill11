package com.catl.bom.workflow;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import com.catl.common.util.CatlConstant;
import com.catl.common.util.DocUtil;
import com.catl.common.util.WorkflowUtil;
import com.catl.ecad.utils.IBAUtility;

import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.iba.value.IBAHolder;
import wt.maturity.PromotionNotice;
import wt.part.WTPart;
import wt.util.WTException;
import wt.vc.wip.Workable;

public class ReleasedDateUtil {

	/**
	 * 设置单个对象发布时间
	 * @param per
	 * @throws Exception
	 */
	public static void setReleasedDate(IBAHolder per) throws Exception{
		//WTPart part = CommonUtil.getLatestWTpartByNumber("560220-00003");
		Date date = new Date();
		TimeZone china = TimeZone.getTimeZone("GMT+08:00");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sdf.setTimeZone(china);
		String time = sdf.format(date);
		//time = "0000-00-00 00:00:00";
		System.out.println(time);
		IBAUtility iba = new IBAUtility(per);
		iba.setIBAValue("ReleasedDate", String.valueOf(time));
		per = iba.updateAttributeContainer(per);
		iba.updateIBAHolder(per);
	}
	
	/**
	 * 设置单个对象发布时间为特定值
	 * @param per
	 * @param time
	 * @throws Exception
	 */
	public static void deleteReleasedDate(IBAHolder per,String attrname) throws Exception{
		//WTPart part = CommonUtil.getLatestWTpartByNumber("560220-00003");
		System.out.println(attrname);
		IBAUtility iba = new IBAUtility(per);
		iba.deleteIBAValueByLogical(attrname);
		per = iba.updateAttributeContainer(per);
		iba.updateIBAHolder(per);
	}
	
	/**
	 * 设置PN中所有对象发布时间
	 * @param pbo
	 * @throws Exception
	 */
	public static void setAllReleasedDate(WTObject pbo) throws Exception{
		if(pbo != null){
			if(pbo instanceof PromotionNotice){
				PromotionNotice pn = (PromotionNotice) pbo;
				QueryResult result = WorkflowUtil.getPromotionTargets(pn);
				while (result.hasMoreElements()) {
					Object object2 = (Object) result.nextElement();
					if(object2 instanceof EPMDocument){
						EPMDocument epm = (EPMDocument) object2;
						setReleasedDate(epm);
					}else if(object2 instanceof WTPart){
						WTPart part = (WTPart) object2;
						setReleasedDate(part);
					}else if(object2 instanceof WTDocument){
						WTDocument doc = (WTDocument) object2;
						if(isAutocad(doc)){
							setReleasedDate(doc);
						}
					}
					
				}
			}
		}
	}
	
	/**
	 * 判断是否为AutoCAD文档
	 * @param doc
	 * @return
	 * @throws WTException
	 */
	private static Boolean isAutocad(WTDocument doc) throws WTException {
		Boolean isdoc = false;
		if (null == doc)
			return isdoc;

		String doctypeString = DocUtil.getObjectType(doc).toString();
		if (doctypeString.endsWith(CatlConstant.AUTOCAD_DOC_TYPE)) {
			isdoc = true;
		}
		return isdoc;
	}
}
