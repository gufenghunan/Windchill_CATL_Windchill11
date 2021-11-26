package com.catl.loadData;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.catl.loadData.util.ExcelWriter;

public class ExportClassification {
	
	private static String rootPath="E:\\ATL\\project\\PLM\\loadtest\\";
	
	public static void main(String[] args) throws DocumentException {				
		exportClassification(rootPath+"destotal\\Classification_3.xml");
	}
	
	/**
	 * 导出分类
	 * @throws DocumentException
	 */
	public static void exportClassification(String path) throws DocumentException{
		
		Map<String,String> TYPE_NAME_1= loadGlobalAttribute();
		
		SAXReader saxReader = new SAXReader();
        Document document = saxReader.read(new File(path));
        Element root = document.getRootElement();
        
        List<String[]> classification =new ArrayList<String[]>();       
        
        List<Element> childList = root.elements();
        
        Map<String,String> attMap=new HashMap<String, String>();
        
        Map<String,Map<Integer,String>>  innerNameattMap=new HashMap<String, Map<Integer,String>>();
        
        Map<String,String[]> constraintMap=new HashMap<String, String[]>();
        
        String name="";
        String parentName="";
        StringBuffer attributes=new StringBuffer();
        String displayNameValue="";
        String instantiableValue="";
        String valueRequired="否";
        String optionValue="";
        String classificationattr="";
        
        boolean bool=false;
        boolean csvBeginTypeDefView=false;
        boolean csvBeginGroupMemberView=false;
        boolean csvBeginAttributeDefView=false;
        for(Element child : childList){
        	//System.out.println(child.getName());
        	List<Element> childList2=child.elements();
        	if(child.getName().equals("csvBeginTypeDefView")){        	    
	        	for(Element child2 :childList2){	        		
	            	if(child2.getName().equals("csvname")){
	            		name=child2.getText();
	            		//System.out.println("内部名称："+name);
	            	}
	            	if(child2.getName().equals("csvtypeParent")){
	            		parentName=child2.getText();	            		
	            	}
	        	}	        	
	        	bool=true;
	        	csvBeginTypeDefView=true;
        	}

        	if(bool){
        		if(child.getName().equals("csvBeginLayoutDefView")){        			
	        		csvBeginTypeDefView=false;
	        	}
        		
        		if(child.getName().equals("csvBeginGroupDefView")){        			
	        		csvBeginTypeDefView=false;
	        	}        		
        		
        		if(child.getName().equals("csvBeginAttributeDefView")){
        			//csvBeginAttributeDefView=true;
	        		csvBeginTypeDefView=false;
	        		
	        		for(Element child2 :childList2){
	        			if(child2.getName().equals("csvname")){
	        				classificationattr=child2.getText().trim();
	        				//System.out.println("分类属性："+child2.getText());
	        			}
	        		}
	        		
	        	}
        		
        		
        		if(child.getName().equals("csvBeginConstraintDefView")){
        			csvBeginTypeDefView=false;
        			for(Element child2 :childList2){
        				
	        			if(child2.getName().equals("csvruleClassname") && child2.getText().equals("com.ptc.core.meta.container.common.impl.ValueRequiredConstraint")){
	        				valueRequired="是";
	        				//System.out.println("约束必填！");
	        			}
	        			
	        			if(child2.getName().equals("csvruleClassname") && child2.getText().equals("com.ptc.core.meta.container.common.impl.DiscreteSetConstraint")){
	        				Element csvruleData = child.element("csvruleData");
	        				optionValue=csvruleData.getText();
	        				
	        				String[] temp001=optionValue.split(",");
	        				String simplifyOptionValue="";
	        				for(int i=1;i<temp001.length;i++){
	        					simplifyOptionValue=simplifyOptionValue+temp001[i].substring(temp001[i].lastIndexOf("|")+1).trim()+"|";
	        				}
	        				simplifyOptionValue=simplifyOptionValue.substring(0,simplifyOptionValue.lastIndexOf("|"));
	        				
	        				optionValue=simplifyOptionValue;
	        				//System.out.println("simplifyOptionValue:"+simplifyOptionValue);
	        				//System.out.println("可选值 "+csvruleData.getText());
	        			}
	        		}
        		}
        		
	        	if(child.getName().equals("csvBeginGroupMemberView")){
	        		csvBeginTypeDefView=false;
	        		csvBeginGroupMemberView=true;
	        		for(Element child2 :childList2){
	        			if(child2.getName().equals("csvname")){
	        				attributes.append(child2.getText().trim());
	        				//System.out.println("属性："+child2.getText());
	        			}
	        		}
	        	}
	        	
	        	if(child.getName().equals("csvPropertyValue")){
	        		
		        	if(csvBeginTypeDefView){
		        		boolean displayName=false;
		        		boolean instantiable=false;		        		
		        		
		        		for(Element child2 :childList2){
		        			if(child2.getText().equals("displayName")){
		        				displayName=true;
		        			}
		        			
		        			if(child2.getText().equals("instantiable")){
		        				instantiable=true;
		        			}
		        			
		        			if(displayName&&child2.getName().equals("csvvalue")){
		        				displayNameValue=child2.getText();
		        				//System.out.println("displayName："+displayNameValue);
		        			}
		        			
		        			if(instantiable&& child2.getName().equals("csvvalue")){
		        				instantiableValue=child2.getText();
		        				//System.out.println("instantiable："+instantiableValue);
		        			}
			        	}
	        		}
		        	
		        	if(csvBeginGroupMemberView){
		        		boolean row_coord=false;
		        		for(Element child2 :childList2){
		        			if(child2.getText().equals("row_coord")){
		        				row_coord=true;
		        				
		        			}
		        			
		        			if(row_coord&&child2.getName().equals("csvvalue")){
		        				
		        				attributes.append("&&&"+child2.getText().trim()+"|");
		        				//System.out.println("排序："+child2.getText());
		        			}
		        		}
		        	}
	        	}
	        	
	        	if(child.getName().equals("csvEndAttributeDefView")){
	        		String[] parentConstraintValue=constraintMap.get(parentName+TYPE_NAME_1.get(classificationattr)+"("+classificationattr+")");
	        		if(parentConstraintValue!=null){
	        			if(parentConstraintValue[0].equals("是")){
	        				valueRequired=parentConstraintValue[0];
	        			}
	        		}
	        		constraintMap.put(name+TYPE_NAME_1.get(classificationattr)+"("+classificationattr+")", new String[]{valueRequired,optionValue});
	        		
	        		valueRequired="否";
	        		optionValue="";
	        	}
	        	
	        	if(child.getName().equals("csvEndGroupMemberView")){
	        		csvBeginGroupMemberView=false;
	        		csvBeginTypeDefView=false;
	        	}
	        	
	        	if(child.getName().equals("csvEndTypeDefView")){
	        		if(instantiableValue.equals("true")){
	        			instantiableValue="是";
	        		}else if(instantiableValue.equals("false")||instantiableValue.equals("")){
	        			instantiableValue="否";
	        		}
	        		
	        		String attributeAll=attributes.toString();
	        		
	        		//System.err.println("attributeAll:"+attributeAll);
	        		
	        		Map<Integer,String> map=new HashMap<Integer, String>();
	        		if(attributeAll!=null&&!attributeAll.equals("")){
	        			
	        			attributeAll=attributeAll.substring(0,attributeAll.lastIndexOf("|"));
	        			
	        			String[] attribute=attributeAll.split("\\|");
	        			
		        		for(int z=0;z<attribute.length;z++){
		        			String value=attribute[z];
		        			String[] temp=value.split("&&&");
		        			//System.out.println("0000000"+temp[1]+"."+temp[0]);
		        			map.put(Integer.valueOf(temp[1]), temp[0].trim());
		        		}
	        		}   		
	        		
	        		Map<Integer,String> parentMap=null;
	        		
	        		if(map.size()==0){
	        			innerNameattMap.put(name, innerNameattMap.get(parentName));
	        		}else{
	        			int min=0;
		        		int max=0;
		        		int count=0;
		        		for(Integer key5:map.keySet()){
		        			if(count==0){
		        				min=key5;
			        			max=key5;
		        			}else{
		        				if(min>key5){
		        					min=key5;
		        				}	        				
		        				if(max<key5){
		        					max=key5;
		        				}        				
		        			}
		        			count++;
		        		}
	        			
		        		//System.out.println("max:"+max+" min:"+min);
		        		
		        		//System.out.println(parentName);
		        		parentMap=innerNameattMap.get(parentName);
		        		/*if(parentMap!=null){
		        			System.out.println("parentMap.size:"+parentMap.size());
		        		}*/
		        		
		        		for(int u=0;u<min;u++){
		        			map.put(u, parentMap.get(u));
		        		}
		        		
		        		for(int f=min;f<max;f++){
		        			if(map.get(f)==null){		        				
		        				map.put(f, parentMap.get(f));
		        			}
		        			
		        		}	
		        		
		        		if(parentMap!=null&&parentMap.size()>map.size()){
		        			for(int kl=map.size();kl<parentMap.size();kl++){
		        				map.put(kl, parentMap.get(kl));
		        			}
		        		}
		        		
	        			innerNameattMap.put(name, map);
	        		}    		
	        		
	        		String atemp="";
	        		
	        		for(int h=0;h<map.size();h++){
	        			atemp=atemp+TYPE_NAME_1.get(map.get(h))+"("+map.get(h)+")"+"|";	        			
    					if(constraintMap.get(name+TYPE_NAME_1.get(map.get(h))+"("+map.get(h)+")")==null){
        					constraintMap.put(name+TYPE_NAME_1.get(map.get(h))+"("+map.get(h)+")", constraintMap.get(parentName+TYPE_NAME_1.get(map.get(h))+"("+map.get(h)+")"));
        				}
	        			
	        		}	        		
	        		
	        		String temp3="";
	        		if(atemp.equals("")){
	        			temp3=attMap.get(parentName);
	        			attMap.put(name, attMap.get(parentName));
	        			String temp11=temp3;
	        			if(temp11!=null && !temp11.equals("")){
		        			temp11=temp11.substring(0,temp11.lastIndexOf("|"));
		        			String hh[]=temp11.split("\\|");
		        			for(int v=0;v<hh.length;v++){
		        				if(constraintMap.get(name+hh[v].trim())==null){
		        					constraintMap.put(name+hh[v].trim(), constraintMap.get(parentName+hh[v].trim()));
		        				}
		        			}
	        			}
	        			
	        		}else{
	        			temp3=atemp;
	        			attMap.put(name, atemp);
	        		}	        		
	        		
        			if(temp3!=null && !temp3.equals("")){
	        			temp3=temp3.substring(0,temp3.lastIndexOf("|"));
	        			String t[]=temp3.split("\\|");
	        			for(int k=0;k<t.length;k++){
	        				//System.out.println("name+t[k]:"+name+t[k]);
	        				String[] temp9=constraintMap.get(name+t[k].trim());
	        				if(temp9!=null){
	        					classification.add(new String[]{name,displayNameValue,instantiableValue,t[k].trim(),temp9[0],temp9[1],""});
	        				}else{
	        					//System.out.println("name+t[k].trim():"+name+t[k].trim());
	        					classification.add(new String[]{name,displayNameValue,instantiableValue,t[k].trim(),"否","",""});
	        				}
	        				
	        			}
        			}else{
        				classification.add(new String[]{name,displayNameValue,instantiableValue,"","","",""});
        			}
	        		
	        		name="";
	        		attributes=new StringBuffer();
	                displayNameValue="";
	                instantiableValue="";
	        		csvBeginTypeDefView=false;
	        		bool=false;
	        	}
        	}
        }
        
        
        ExcelWriter writer = new ExcelWriter();
		try {
			boolean flag = writer.exportExcelList("E:\\ATL\\project\\PLM\\loadtest\\destotal\\分类属性对应表.xlsx","分类属性对应表", new String[]{"内部名称","显示名称","实例化","属性","是否必填","约束","备注"}, classification);
			System.out.println("分类属性对应关系.xlsx flag="+flag);
		} catch (Exception e) {
			e.printStackTrace();
		}
        
        
	}
	
	public static Map<String,String> loadGlobalAttribute() throws DocumentException{
		
		Map<String,String> TYPE_NAME_1= new HashMap<String,String>();
		
		SAXReader saxReader = new SAXReader();
        Document document = saxReader.read(new File("E:\\ATL\\project\\PLM\\loadtest\\destotal\\Classification_1.xml"));
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
        		TYPE_NAME_1.put(csvname, displayname);
        	}
        	
        }
        return TYPE_NAME_1;
	}
}
