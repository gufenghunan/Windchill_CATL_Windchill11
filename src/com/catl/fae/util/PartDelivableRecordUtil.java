package com.catl.fae.util;

import java.util.List;

import com.catl.common.util.PartUtil;
import com.catl.ecad.utils.ECADutil;

import wt.doc.WTDocument;
import wt.part.WTPart;
import wt.util.WTException;

public class PartDelivableRecordUtil {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public static void setRecord(WTPart part,String AVL,String delivableID){
		
	}

	public static void broadDelivableRecord(WTDocument doc, WTPart part,String AVL,String delivableID) throws WTException{
		List<WTPart> childParts = ECADutil.getChildPart(part);
		for(int i = 0 ; i < childParts.size(); i ++){
			WTPart childPart = childParts.get(i);
			setRecord(childPart, AVL, delivableID);
		}
	}
	
	public static String substituteDelivable(WTDocument doc, WTPart part, String AVL){
		String type = doc.getTypeDefinitionReference().toString();
		if(true){
			
		}
		return null;
	}
}
