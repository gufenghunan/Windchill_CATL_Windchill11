package com.catl.line.webservice;


public class Test {
	   public static void main(String[] args) throws Exception {  
	        DwgWebserviceProxy helloPxy = new DwgWebserviceProxy();  
	        DwgWebservice service = helloPxy.getDwgWebservice();  
	        String res = service.dwgtopdf("test/550150-M002.dwg");  
	        System.out.println(res);  
	    }  
	   
}
