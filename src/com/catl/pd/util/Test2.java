package com.catl.pd.util;

import wt.doc.WTDocument;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;

public class Test2 implements RemoteAccess{
  public static void main(String[] args) throws Exception {
		RemoteMethodServer rms = RemoteMethodServer.getDefault();
		rms.setUserName("wcadmin");
		rms.setPassword("wcadmin");
		rms.invoke("test", Test2.class.getName(), null, null, null);
	}

	public static void test() throws Exception {
		WTDocument doc=CommonUtil.getLatestWTDocByNumber("000000-00000088");
		throw new Exception(doc.getContainer().getType());
	}
}
