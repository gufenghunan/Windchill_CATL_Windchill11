package com.catl.test;

import java.io.File;
import java.io.BufferedReader;
import java.io.BufferedWriter;  
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;  
  
public class TestFile {  
    public static void main(String args[]) throws Exception {  
        //try { // 防止文件建立或读取失败，用catch捕捉错误并打印，也可以throw  
  
            /* 读入TXT文件 */  
           /* String pathname = "D:\\twitter\\13_9_6\\dataset\\en\\input.txt"; // 绝对路径或相对路径都可以，这里是绝对路径，写入文件时演示相对路径  
            File filename = new File(pathname); // 要读取以上路径的input。txt文件  
            InputStreamReader reader = new InputStreamReader(  
                    new FileInputStream(filename)); // 建立一个输入流对象reader  
            BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言  
            String line = "";  
            line = br.readLine();  
            while (line != null) {  
                line = br.readLine(); // 一次读入一行数据  
            }  */
  
            /* 写入Txt文件 */  
            File writename = new File("E:\\test\\output.txt"); // 相对路径，如果没有则要建立一个新的output。txt文件  
            writename.createNewFile(); // 创建新文件  
            BufferedWriter out = new BufferedWriter(new FileWriter(writename));  
            out.write("E:\\test\\asm0001.asm\r\n"); // \r\n即为换行  
            out.write("output filename\r\n");
            out.write("workspace");
            out.flush(); // 把缓存区内容压入文件  
            out.close(); // 最后记得关闭文件  
            
            System.out.println(getTransfer());
            System.out.println("1111111111111111111111111");
       // } catch (Exception e) {  
       //     e.printStackTrace();  
       // }  
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