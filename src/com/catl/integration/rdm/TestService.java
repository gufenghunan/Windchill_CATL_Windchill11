package com.catl.integration.rdm;

import javax.xml.rpc.ServiceException;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;

public class TestService {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String endpoint = "http://icenterv01.ptcnet.ptc.com/Windchill/servlet/RPC?CLASS=com.midea.rdm";
		Service service = new Service();
		try {
			Call call = (Call)service.createCall();
			call.setTargetEndpointAddress(endpoint);
			call.setUsername("Administrator");
			call.setPassword("ptc");
			call.setOperationName("QueryProductID");
			call.addParameter("productName", org.apache.axis.encoding.XMLType.XSD_STRING, javax.xml.rpc.ParameterMode.IN);
			call.setReturnType(org.apache.axis.encoding.XMLType.XSD_STRING);
			String productName = "Product 1";
			String result = (String)call.invoke(new Object[]{productName});
			System.out.println(">>>>>Result is :" + result);
		} catch (ServiceException e) {
			e.printStackTrace();
			System.out.println(e.toString());
		} catch (Exception e1) {
			e1.printStackTrace();
			System.out.println(e1.toString());
		}
	}

}
