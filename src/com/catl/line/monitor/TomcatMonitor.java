package com.catl.line.monitor;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;  
import java.net.UnknownHostException;
import java.util.Date;

public class TomcatMonitor implements Runnable{  
    String testHttp="http://localhost:8080";  //测试连接地址  
    int testIntervalTime=1;//测试连接失败后间隔时间，单位为秒  
    int waitIntervalTime=1; //等待测试间隔时间，单位为秒  
    int testTotalCount=1; //测试连接总次数  
    Thread thread=null;  
    public TomcatMonitor(){  
        System.out.println("*******************初始化成功!*******************");  
        thread=new Thread(this);  
        thread.start();       
    }  
      
    public void run(){ 
        System.out.println("正在监控中...");     
        int testCount=0;  
        while(true){  
            testCount=0;  
            testCount++;   
            Date date=new Date();
            String currenttime=date.toLocaleString();
            if(currenttime.indexOf("24:00:00")>-1||currenttime.indexOf("12:50:00")>-1){
              	 File file=new File("D:\\tomcat6\\bin");
                   try {
       				Runtime.getRuntime().exec("cmd /c D:\\tomcat6\\bin\\shutdown.bat",null,file);
       				while(true){
                       if(!isPortUsing(8080)){
                    	   break;
                       }
                      thread.sleep(100);
       				}
       				Runtime.getRuntime().exec("cmd /c D:\\tomcat6\\bin\\startup.bat",null,file);
       				System.out.println(currenttime+"定时启动tomcat成功");  
                   } catch (IOException | InterruptedException e) {
       				e.printStackTrace();
       			} 
              }
            boolean isrun=test();  
            //System.out.println("正在启动测试连接,尝试连接次数为:"+testCount+",结果为:"+(isrun==false?"失败.":"成功!"));                 
            while(!isrun){  
                if(testCount>=testTotalCount)break;  
                try {  
                    thread.sleep(testIntervalTime*1000);  
                } catch (InterruptedException e) {  
                    e.printStackTrace();  
                }  
                testCount++;  
                //System.out.println("正在启动测试连接,尝试连接次数为:"+testCount+",结果为:"+(isrun==false?"失败.":"成功!"));                 
                isrun=test();  
            }  
            if(!isrun){               
                try{        
                    System.out.println("测试连接失败,正在启动tomcat");
                    File file=new File("D:\\tomcat6\\bin");
                    Runtime.getRuntime().exec("cmd /c D:\\tomcat6\\bin\\startup.bat",null,file); 
                    System.out.println(currenttime+"启动tomcat成功");  
                }catch(Exception e){  
                    e.printStackTrace();  
                    System.out.println("启动tomcat异常,请查看先关错误信息");  
                }                 
            }  
            try {  
                thread.sleep(waitIntervalTime*1000);  
            } catch (InterruptedException e) {  
                e.printStackTrace();  
            }  
            isrun=test();  
        }
       
    }  
      
    public boolean test(){  
          
        URL url=null;         
        try {  
            url = new URL(testHttp);  
        } catch (MalformedURLException e) {  
            e.printStackTrace();  
        }  
        try {  
            URLConnection urlConn=url.openConnection();  
            urlConn.setReadTimeout(15000);  
            BufferedReader reader = new BufferedReader(new InputStreamReader( urlConn.getInputStream()));            //实例化输入流，并获取网页代码  
                   String s;                                         
                   while ((s = reader.readLine()) != null) {  
                      return true;     
                   }                          
        } catch (Exception e) {  
          return false;  
        }  
        return false;  
    }  
      
      
    public static void main(String[] args) throws Exception{  
        TomcatMonitor tm=new TomcatMonitor();  
    }  
    
    /*** 
     *  true:already in using  false:not using  
     * @param host 
     * @param port 
     * @throws UnknownHostException  
     */  
    public static boolean isPortUsing(int port) throws UnknownHostException{  
        boolean flag = false;  
        InetAddress theAddress = InetAddress.getByName("127.0.0.1");  
        try {  
            Socket socket = new Socket(theAddress,port);  
            flag = true;  
        } catch (IOException e) {  
        }  
        return flag;  
    }  
  
} 