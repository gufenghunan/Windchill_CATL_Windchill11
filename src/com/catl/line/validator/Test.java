package com.catl.line.validator;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import wt.doc.WTDocument;
import wt.fc.ReferenceFactory;
import wt.fc.WTObject;
import wt.maturity.Promotable;
import wt.maturity.PromotionNotice;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.catl.common.util.PropertiesUtil;
import com.catl.ecad.utils.ECADutil;
import com.catl.line.constant.ConstantLine;
import com.catl.line.exception.LineException;
import com.catl.line.util.CommonUtil;
import com.ptc.xworks.windchill.util.PromotionNoticeUtils;

public class Test implements RemoteAccess {

	public static void main(String[] args) throws RemoteException,
			InvocationTargetException, WTException {
		// TODO Auto-generated method stub
		test();
	}

	public static void test() throws RemoteException,
			InvocationTargetException, WTException {
		// OR:wt.change2.WTChangeIssue:215771

		RemoteMethodServer rm = RemoteMethodServer.getDefault();
		rm.setUserName("wcadmin");
		rm.setPassword("wcadmin");
		SessionHelper.manager.setAuthenticatedPrincipal("wcadmin");
		rm.invoke("verifyLineAsMRule", Test.class.getName(), null, null,
				null);
	}

	/**
	 * 验证线束总成的审核是否包含了完整的bom结构 未发布的部件 线束总成的总成的装箱说明是否关联(装箱说明的编码和线束总成的编码一致)
	 * 
	 * @param pbo
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public static String verifyLineAsMRule() throws WTException,
			WTPropertyVetoException {
		WTObject pbo = (WTObject) new ReferenceFactory().getReference(
				"OR:wt.maturity.PromotionNotice:164119042").getObject();
		StringBuffer msg = new StringBuffer();
		if (pbo instanceof PromotionNotice) {
			PromotionNotice pn = (PromotionNotice) pbo;
			Collection<Promotable> objs = PromotionNoticeUtils
					.getPromotionNoticeItems((PromotionNotice) pn);
			Object[] arrayobjs = objs.toArray();
			List<String> partsnumber = new ArrayList<String>();// 收集的部件
			List<String> lineasmnumbers = new ArrayList<String>();
			for (int i = 0; i < arrayobjs.length; i++) {
				Object obj = arrayobjs[i];
				if (obj instanceof WTPart) {
					WTPart part = (WTPart) obj;
					String number = part.getNumber();
					if (number.startsWith(PropertiesUtil.getValueByKey("create_box_explain_group"))) {
						lineasmnumbers.add(number);
					}
					partsnumber.add(number);
				}
			}
			if (lineasmnumbers.size() == 0) {
				return ConstantLine.route_normal;
			}

			for (int i = 0; i < lineasmnumbers.size(); i++) {
				String linenumber = lineasmnumbers.get(i);
				WTPart ppart = CommonUtil.getLatestWTpartByNumber(linenumber);

				List<WTPart> parts = new ArrayList<WTPart>();
				parts = CommonUtil.getAllChildParts(parts, ppart, null);
				for (WTPart part : parts) {
					if (!partsnumber.contains(part.getNumber())) {
						msg.append("线束总成" + linenumber + "的子件"
								+ part.getNumber() + "未添加进受影响对象！");
					}
				}

				List<WTDocument> documents = ECADutil.getDocByPart(ppart);
				boolean flag = false;
				for (WTDocument document : documents) {
					if (partsnumber.contains(document.getNumber())) {
						flag = true;
					}
				}
				if (!flag) {
					msg.append("线束总成" + linenumber + "的装箱单未添加进受影响对象！");
				}
			}
		}

		if (msg.length() != 0) {
			String errormsg = msg.toString();
			throw new LineException(errormsg);
		}
		return ConstantLine.route_lineasm;
	}
}
