package com.catl.bom.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.httpgw.GatewayAuthenticator;
import wt.lifecycle.State;
import wt.maturity.MaturityHelper;
import wt.maturity.PromotionNotice;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.part.WTPartUsageLink;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.catl.common.util.CatlConstant;
import com.catl.common.util.PartUtil;

public class BomCheckUtil {

	private static Logger log = Logger.getLogger(BomCheckUtil.class.getName());

	private static void checkBOMItemState(WTPart part,
			HashMap<String, List<WTPart>> noreleasedpartlist)
			throws WTException {
		if (null != part) {
			WTPart fatherPart = part;

			QueryResult qr = null;
			for (qr = PersistenceHelper.manager.navigate(fatherPart, "uses",
					WTPartUsageLink.class, false); qr.hasMoreElements();) {
				WTPartUsageLink partUsageLink = (WTPartUsageLink) qr
						.nextElement();
				WTPartMaster partMaster = (WTPartMaster) partUsageLink
						.getUses();
				WTPart partChild = (WTPart) PartUtil
						.getLastestWTPartByNumber(partMaster.getNumber());
				log.debug("partchaild number=" + partChild.getNumber());
				if (null != partChild) {
					if (ChekPartlibrary(partChild)) {
						// allow states:RELEASED
						boolean statusbolean = partChild.getState().toString()
								.endsWith("RELEASED");
						if (!statusbolean) {
							if (noreleasedpartlist
									.containsKey(part.getNumber())) {
								String key = part.getNumber();
								List<WTPart> childList = noreleasedpartlist
										.get(key);
								childList.add(partChild);
							} else {
								List<WTPart> childList = new ArrayList<WTPart>();
								childList.add(partChild);
								noreleasedpartlist.put(part.getNumber(),
										childList);
							}
						}
						checkBOMItemState(partChild, noreleasedpartlist);

					} else {
						// allow
						// states:RELEASED,DESIGNING,DESIGNMODIFICATION,DESIGNREVIEW
						String partstatus = partChild.getState().toString();
						if (!partstatus.endsWith("RELEASED")
								|| !partstatus.endsWith("DESIGN")
								|| !partstatus.endsWith("DESIGNMODIFICATION")
								|| !partstatus.endsWith("DESIGNREVIEW")) {
							if (noreleasedpartlist
									.containsKey(part.getNumber())) {
								String key = part.getNumber();
								List<WTPart> childList = noreleasedpartlist
										.get(key);
								childList.add(partChild);
							} else {
								List<WTPart> childList = new ArrayList<WTPart>();
								childList.add(partChild);
								noreleasedpartlist.put(part.getNumber(),
										childList);
							}
						}

						checkBOMItemState(partChild, noreleasedpartlist);
					}

				}
			}
		}
	}

	public static String checkBOM(WTObject pbo) throws WTException {
		String errorpart = "";

		if (pbo instanceof PromotionNotice) {
			QueryResult qr = MaturityHelper.service
					.getPromotionTargets((PromotionNotice) pbo);
			while (qr.hasMoreElements()) {
				WTObject obj = (WTObject) qr.nextElement();
				if (obj instanceof WTPart) {
					WTPart part = (WTPart) obj;
					part = (WTPart) PartUtil.getLastestWTPartByNumber(part
							.getNumber());
					HashMap<String, List<WTPart>> result = new HashMap<String, List<WTPart>>();

					checkBOMItemState(part, result);
					for (Iterator<String> ite = result.keySet().iterator(); ite
							.hasNext();) {
						String key = ite.next();
						List<WTPart> childList = result.get(key);
						for (int i = 0; i < childList.size(); i++) {
							WTPart tempPart = childList.get(i);
							errorpart = errorpart
									+ "\r\n BOM "
									+ key
									+ " ： "
									+ tempPart.getNumber()
									+ " "
									+ State.toState(
											tempPart.getState().toString())
											.getDisplay();
						}
					}
				}
			}

		}
		return errorpart;
	}

	public static void checkECBOMItemState(WTPart part,
			HashMap<String, List<WTPart>> noreleasedpartlist)
			throws WTException {
		if (null != part) {
			WTPart fatherPart = part;

			QueryResult qr = null;
			for (qr = PersistenceHelper.manager.navigate(fatherPart, "uses",
					WTPartUsageLink.class, false); qr.hasMoreElements();) {
				WTPartUsageLink partUsageLink = (WTPartUsageLink) qr
						.nextElement();
				WTPartMaster partMaster = (WTPartMaster) partUsageLink
						.getUses();
				WTPart partChild = (WTPart) PartUtil
						.getLastestWTPartByNumber(part.getNumber());
				if (null != partChild) {
					boolean statusbolean = partChild.getState().toString()
							.endsWith("RELEASED");
					if ((!statusbolean)) {
						if (noreleasedpartlist.containsKey(part.getNumber())) {
							String key = part.getNumber();
							List<WTPart> childList = noreleasedpartlist
									.get(key);
							childList.add(partChild);
						} else {
							List<WTPart> childList = new ArrayList<WTPart>();
							childList.add(partChild);
							noreleasedpartlist.put(part.getNumber(), childList);
						}
					}
				}
			}
		}
	}

	public static Boolean ChekPartlibrary(WTPart part) {
		Boolean fag = false;
		if (null == part) {
			return false;
		} else {
			String container = part.getContainer().getName();
			if (container.startsWith(CatlConstant.FIX_LIBRARY_NAME)
					|| container.startsWith(CatlConstant.EE_LIBRARY_NAME)
					|| container.startsWith(CatlConstant.BATTERY_LIBRARY_NAME)
					|| container.startsWith(CatlConstant.METRIAL_LIBRARY_NAME)) {
				fag = true;
				;
			}
		}
		return fag;
	}

	public static void main(String args[]) throws WTException,
			WTPropertyVetoException {

		RemoteMethodServer rms = RemoteMethodServer.getDefault();
		GatewayAuthenticator auth = new GatewayAuthenticator();
		auth.setRemoteUser("wcadmin");
		rms.setAuthenticator(auth);
		// get part all child
		String toplevel = args[0];
		String errorpart = "";
		WTPart fpart = PartUtil.getLastestWTPartByNumber(toplevel);
		HashMap<String, List<WTPart>> noreleasedpartlist = new HashMap<String, List<WTPart>>();
		checkBOMItemState(fpart, noreleasedpartlist);
		for (Iterator<String> ite = noreleasedpartlist.keySet().iterator(); ite
				.hasNext();) {
			String key = ite.next();
			List<WTPart> childList = noreleasedpartlist.get(key);
			for (int i = 0; i < childList.size(); i++) {
				WTPart tempPart = childList.get(i);
				errorpart = errorpart
						+ "\r\n BOM "
						+ key
						+ " ： "
						+ tempPart.getNumber()
						+ " "
						+ State.toState(tempPart.getState().toString())
								.getDisplay();
			}
		}
		// String errorpart = BomCheckUtil.checkBOM(fpart);

		System.out.println("errorpart==" + errorpart);

	}

}
