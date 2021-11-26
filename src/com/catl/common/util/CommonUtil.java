package com.catl.common.util;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Map;
import java.util.PropertyResourceBundle;

import javax.servlet.ServletRequest;

import org.jfree.util.Log;

import com.catl.common.constant.PartState;
import com.catl.common.constant.RoleName;
import com.catl.common.constant.TypeName;
import com.ptc.cipjava.intdict;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;

import wt.doc.WTDocument;
import wt.enterprise.RevisionControlled;
import wt.epm.EPMDocument;
import wt.epm.util.EPMDebug;
import wt.fc.Persistable;
import wt.folder.SubFolder;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerHelper;
import wt.inf.container.WTContainerRef;
import wt.lifecycle.LifeCycleManaged;
import wt.method.RemoteAccess;
import wt.org.DirectoryContextProvider;
import wt.org.OrganizationServicesHelper;
import wt.org.WTOrganization;
import wt.org.WTPrincipal;
import wt.org.WTPrincipalReference;
import wt.part.WTPart;
import wt.project.Role;
import wt.team.Team;
import wt.team.TeamException;
import wt.team.TeamHelper;
import wt.util.WTException;

public class CommonUtil implements RemoteAccess {

	private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(CommonUtil.class.getName());

	public static String getDomainForType() {
		String strDomain = getStrFromProperties("wt.inf.container.SiteOrganization.internetDomain", "wt.inf.container.SiteOrganization");
		strDomain = reverse(strDomain);
		return strDomain;
	}

	/**
	 * get value from properties key
	 * 
	 * @param key
	 * @param propertiefile
	 * @return
	 * @throws WTException
	 */
	public static String checkPromotionType(NmCommandBean clientData) throws WTException {

		String type = TypeName.partPromotion;

		ArrayList selectedObject = clientData.getSelectedOidForPopup();

		int isdesign = 0;
		int iswriting = 0;

		if (selectedObject.size() == 0) {
			NmOid nmOid = clientData.getActionOid();
			selectedObject.add(nmOid);
		}

		for (int i = 0; i < selectedObject.size(); i++) {
			NmOid nmOid = (NmOid) selectedObject.get(i);
			logger.debug("selected promote object[" + i + "]=" + nmOid.toString());
			LifeCycleManaged objectSelected = (LifeCycleManaged) nmOid.getRefObject();

			String state = objectSelected.getState().toString();
			if (!state.equalsIgnoreCase(PartState.DESIGN) && !state.equalsIgnoreCase(PartState.WRITING) && !state.equalsIgnoreCase(PartState.MODIFICATION) && !state.equalsIgnoreCase(PartState.DESIGNMODIFICATION)) {
				throw new WTException("创建升级请求只能选择“编制” '修改'或“设计” ‘设计修改’状态的对象");
			}
			if (state.equalsIgnoreCase(PartState.DESIGN) || state.equalsIgnoreCase(PartState.DESIGNMODIFICATION)) {
				isdesign++;
			}
			if (state.equalsIgnoreCase(PartState.WRITING) || state.equalsIgnoreCase(PartState.MODIFICATION)) {
				iswriting++;
			}

			if (objectSelected instanceof WTDocument) {

				WTDocument doc = (WTDocument) objectSelected;
				String doctype = DocUtil.getObjectType(doc);
				logger.debug("doc type===" + doctype);
				if (!(doctype.endsWith("autocadDrawing")||doctype.endsWith(TypeName.softwareDoc))) {
					throw new WTException(doc.getNumber() + "不符合规范,只能上传部件，CATIA模型、AutoCAD图纸和软件文档！\n");
				}
				isdesign++;
			}// end if document

		} // end for loop

		if (isdesign * iswriting > 0) {
			throw new WTException("对象的升级状态不符合业务逻辑  \n");
		} else if (isdesign > 0) {
			type = TypeName.bomPromotion;
		}

		return type;

	}

	public static String getStrFromProperties(String key, String propertiefile) {
		String strinfo = "";
		try {

			PropertyResourceBundle prBundle = (PropertyResourceBundle) PropertyResourceBundle.getBundle(propertiefile);
			byte[] temp = null;
			temp = key.getBytes("GB2312");
			key = new String(temp, "ISO-8859-1");
			temp = prBundle.getString(key).getBytes("ISO-8859-1");
			strinfo = new String(temp, "GB2312");
		} catch (UnsupportedEncodingException ex) {
			logger.debug(CommonUtil.class.getName() + ".getStrFromProperties UnsupportedEncodingException!!!");
		}
		return strinfo;

	}

	/**
	 * 将字符串倒过来
	 * 
	 * @param s
	 * @return
	 */
	public static String reverse(String s) {
		if (s == null) {
			return null;
		}
		if ("".equals(s)) {
			return s;
		}
		if (s.length() == 1) {
			return s;
		}
		char ac[] = s.toCharArray();
		char ac1[] = new char[ac.length];
		int i = 0;
		int j = 0;
		while (i < ac.length) {
			switch (ac[i]) {
			case 45: // '-'
				ac[i++] = '_';
				break;

			case 46: // '.'
				System.arraycopy(ac, j, ac1, ac1.length - i, i - j);
				j = ++i;
				ac1[ac1.length - i] = '.';
				break;

			default:
				i++;
				break;
			}
		}
		System.arraycopy(ac, j, ac1, ac1.length - i, i - j);
		return new String(ac1);
	}
	/**
	 * 是否是设计者
	 * @param object2
	 * @param user
	 * @return
	 * @throws TeamException
	 * @throws WTException
	 */
	public static Boolean checkifDesigner(RevisionControlled object2, WTPrincipal user) throws WTException {
		Boolean isdesginerrole = false;
		Role role = Role.toRole(RoleName.DESIGNER);
		Team team2 = (Team) TeamHelper.service.getTeam(object2);
		if (team2 != null) {
			Enumeration enumPrin = team2.getPrincipalTarget(role);
			while (enumPrin.hasMoreElements()) {
				WTPrincipalReference tempPrinRef = (WTPrincipalReference) enumPrin.nextElement();
				WTPrincipal principal2 = tempPrinRef.getPrincipal();
				if (principal2.getName().equals(user.getName())) {
					isdesginerrole = true;
				}
			}

		}
		return isdesginerrole;
	}
	/**
	 * 是否管理员
	 * @param wtPrincipal
	 * @return
	 * @throws WTException 
	 */
	public static boolean isSiteAdmin(WTPrincipal wtPrincipal) throws WTException {
		return WTContainerHelper.service.isAdministrator(WTContainerHelper.service.getExchangeRef(), wtPrincipal);
    }
	
	public static WTContainerRef getORGCATLRef() throws WTException{
		DirectoryContextProvider dcp = WTContainerHelper.service.getExchangeContainer().getContextProvider();
		WTOrganization org = OrganizationServicesHelper.manager.getOrganization("CATL", dcp);
		return WTContainerHelper.service.getOrgContainerRef(org);
	}
}
