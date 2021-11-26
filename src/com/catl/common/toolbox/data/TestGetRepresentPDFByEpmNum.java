package com.catl.common.toolbox.data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.LinkedList;
import java.util.List;

import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.epm.EPMDocument;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.httpgw.GatewayAuthenticator;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.pom.Transaction;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.representation.Representation;
import wt.representation.RepresentationHelper;
import wt.util.WTAttributeNameIfc;
import wt.util.WTException;

import com.catl.common.util.DocUtil;
import com.catl.loadData.util.ExcelReader;

public class TestGetRepresentPDFByEpmNum implements RemoteAccess {

	public static void main(String[] args) {

		RemoteMethodServer rms = RemoteMethodServer.getDefault();
		GatewayAuthenticator auth = new GatewayAuthenticator();
		auth.setRemoteUser("wcadmin");
		rms.setAuthenticator(auth);

		System.out.println(args[0]);
		invokeRemoteLoad(args[0]);
	}

	public static void invokeRemoteLoad(String epmNum) {
		String method = "doLoad";
		String CLASSNAME = TestGetRepresentPDFByEpmNum.class.getName();
		Class[] types = { String.class };
		Object[] values = { epmNum };
		try {
			RemoteMethodServer.getDefault().invoke(method, CLASSNAME, null, types, values);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void doLoad(String epmNum) {
		try {
			EPMDocument epm = DocUtil.getLastestEPMDocumentByNumber(epmNum);
			Representation r = RepresentationHelper.service.getDefaultRepresentation(epm);
			System.out.println(r.getType());
			System.out.println(r.getName());
			QueryResult qr = ContentHelper.service.getContentsByRole(r, ContentRoleType.SECONDARY);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean isEmpty(String str) {
		if (str == null || str.trim().equals("") || str.equals("null"))
			return true;
		return false;
	}

}
