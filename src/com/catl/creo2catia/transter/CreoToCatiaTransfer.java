package com.catl.creo2catia.transter;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class CreoToCatiaTransfer {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	 public static String getTransfer() throws Exception{
	    	//Process p = Runtime.getRuntime().exec("cmd /k call \"E:\\Program Files\\PTC\\Creo 3.0\\M080\\Parametric\\bin\\parametric.exe\" -g:no_graphics  -Proe2catia -Creo.txt");
	    	File file=new File("E:\\plus_space\\Creo2Catia\\text");
	    	Process p = Runtime.getRuntime().exec("cmd /c \"E:\\plus_space\\Creo2Catia\\text\\creo.bat\"",null,file);
	    	InputStream stream1 = p.getErrorStream();
	    	InputStream stream2 = p.getInputStream();
	    	String error =  getErrorMsg(stream1);
	    	String output = getErrorMsg(stream2);
	    	return error+output;
	    }
	    
	    public static String getErrorMsg(InputStream stream) throws IOException{
	    	StringBuffer output = new StringBuffer();
	    	try{
	    	BufferedReader br = new BufferedReader(new InputStreamReader(stream, "GBK"));
	    	String line =br.readLine();
	    	while(line != null){
	    		if(line.trim().length() >0){
	    			output.append(line).append("\n");
	    		}
	    		line = br.readLine();
	    	}
	    	}finally{
	    		if(stream != null){
	    			stream.close();
	    		}
	    	}
	    	return output.toString();
	    }
}
