package com.catl.loadData;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;


public class Constant {
	public static final Map<String,String> TYPE_NAME= new HashMap<String,String>();
	static{
		TYPE_NAME.put("分类", "cls");
		
		TYPE_NAME.put("产品技术文件", "com.CATLBattery.technicalDoc");
		TYPE_NAME.put("研发过程文档", "com.CATLBattery.rdDoc");
		TYPE_NAME.put("GERBER文件", "com.CATLBattery.gerberDoc");
		TYPE_NAME.put("PCBA装配图", "com.CATLBattery.pcbaDrawing");
		TYPE_NAME.put("线束AUTOCAD图纸", "com.CATLBattery.autocadDrawing");
		
		TYPE_NAME.put("已发布", "RELEASED");
		
		TYPE_NAME.put("E", "make");
		TYPE_NAME.put("F", "buy");
		TYPE_NAME.put("W", "singlesource");
		TYPE_NAME.put("C", "customer");
		TYPE_NAME.put("V", "virtual");
		TYPE_NAME.put("电子电气件Datasheet", "com.CATLBattery.EDatasheetDoc");
		
		try {
			loadGlobalAttribute();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void loadGlobalAttribute() throws Exception{
		SAXReader saxReader = new SAXReader();
        Document document = saxReader.read(new File("/data/Classification/Classification_1.xml"));
        Element root = document.getRootElement();
        List<Element> childList = root.elements();
        for(Element child : childList){        	
        	List<Element> childList2=child.elements();
        	String csvname="";
    		String displayname="";
        	for(Element child2 :childList2){
        		//System.out.println(child2.getName());
        		
            	if(child2.getName().equals("csvname")){
            		csvname=child2.getText();
            		//System.out.println(child2.getName()+"..."+child2.getText());
            	}
            	
            	if(child2.getName().equals("csvdisplay_name")){
            		displayname=child2.getText();
            		//System.out.println(child2.getName()+"..."+child2.getText());
            	}
            	
        	}
        	
        	if(!csvname.equals("CATL")){
        		//System.out.println(displayname+"."+csvname);
        		if(TYPE_NAME.get(displayname.trim()) != null && !TYPE_NAME.get(displayname.trim()).equals(csvname.trim())){
        			if(StringUtils.isNotBlank(displayname.trim())){
        				throw new Exception("显示名称="+displayname.trim()+",内部值有多个:"+TYPE_NAME.get(displayname.trim())+","+csvname.trim());
        	        }
        	    }else{
        	        TYPE_NAME.put(displayname.trim(), csvname.trim());
        	    }
        	}
        	
        }
	}
	
}
