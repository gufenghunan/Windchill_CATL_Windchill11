package com.catl.integration.rdm;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String s = "http://10.16.11.163:2000/platform/windchillAdapter.jsp?username=yang&page=/mailLinked?workflowType=MRQ&objectId=bca6bd02-f1dd-42b1-967b-3165e8fb71f1";
		String[] sv = s.split("page=");
		System.out.println(sv.length);
		System.out.println(sv[0]);
		System.out.println(sv[1]);
		String re = sv[1].replaceAll("[?]", "%3F");
		re = re.replaceAll("&", "%26");
		s = sv[0]+"page="+re;
		System.out.println(s);
	}

}
