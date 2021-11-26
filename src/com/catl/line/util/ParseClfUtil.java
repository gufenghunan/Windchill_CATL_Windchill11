package com.catl.line.util;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import wt.doc.WTDocument;
import wt.part.WTPart;
import wt.type.ClientTypedUtility;
import wt.util.WTException;

import com.ptc.core.HTMLtemplateutil.server.processors.AttributeKey;
import com.ptc.core.components.forms.CreateEditFormProcessorHelper;

public class ParseClfUtil {
	public static String decodeDeformatJSONString(String s) {
		String s1 = null;
		if (s != null) {
			try {
				s1 = URLDecoder.decode(s, "UTF-8");
			} catch (Exception exception) {
			}
			if (s1 != null) {
				s1 = s1.replace("&quot;", "\"");
				s1 = s1.replace("&nbsp;", " ");
			}
		}
		return s1;
	}
	public static void test() throws Exception{
		WTPart part=(WTPart) WCUtil.getWTObject("VR:wt.part.WTPart:156592");
		WTDocument doc=(WTDocument) WCUtil.getWTObject("VR:wt.doc.WTDocument:189873");
		String type = ClientTypedUtility.getTypeIdentifier(doc).getTypename();
		System.out.println(type);
	}
	
	public static LinkedHashMap createClassificationAttributesHashMap(String s)
			throws WTException {
		LinkedHashMap map=new LinkedHashMap();
		if (s == null || "".equals(s))
			return null;
		String s1 = decodeDeformatJSONString(s);
		JSONObject jsonobject;
		try {
			jsonobject = new JSONObject(s1);
		} catch (JSONException jsonexception) {
			throw new WTException(jsonexception);
		}
		Iterator iterator = jsonobject.keys();
		do {
			if (!iterator.hasNext())
				break;
			String s2 = (String) iterator.next();
			Object obj = null;
			try {
				obj = convertJSONValueToFormValue(jsonobject.get(s2));
			} catch (JSONException jsonexception1) {
				continue;
			}
			map.put(s2, obj);
		} while (true);
		return map;
	}
	
	public static LinkedHashMap getClfAttkey_value(String s,boolean isformat)
			throws WTException {
		LinkedHashMap map=new LinkedHashMap();
		if (s == null || "".equals(s))
			return map;
		String s1 = s;
		if(!isformat){
			s1=decodeDeformatJSONString(s);
		}
		JSONObject jsonobject;
		try {
			jsonobject = new JSONObject(s1);
		} catch (JSONException jsonexception) {
			throw new WTException(jsonexception);
		}
		Iterator iterator = jsonobject.keys();
		do {
			if (!iterator.hasNext())
				break;
				String s2 = (String) iterator.next();
				Object obj = null;
			try {
				obj = convertJSONValueToFormValue(jsonobject.get(s2));
			} catch (JSONException jsonexception1) {
				continue;
			}
			if(!s2.endsWith("old")){
				AttributeKey key=CreateEditFormProcessorHelper.getAttributeKey(s2, true);
				if(key!=null){
					  map.put(key, obj);
				}
			}
			
		} while (true);
		return map;

	}
	private static Object convertJSONValueToFormValue(Object obj) {
		if (obj instanceof String)
			return obj;
		if (obj instanceof JSONObject)
			return obj.toString();
		if (obj instanceof JSONArray) {
			JSONArray jsonarray = (JSONArray) obj;
			if (jsonarray.length() == 1) {
				Object obj1 = getJSONArrayElementValue(jsonarray, 0);
				return obj1;
			}
			ArrayList arraylist = new ArrayList();
			for (int i = 0; i < jsonarray.length(); i++) {
				Object obj2 = getJSONArrayElementValue(jsonarray, i);
				arraylist.add(obj2);
			}

			return arraylist;
		} else {
			return "";
		}
	}
	private static Object getJSONArrayElementValue(JSONArray jsonarray, int i) {
		Object obj = null;
		try {
			obj = jsonarray.get(i);
		} catch (JSONException jsonexception) {
			System.out.println("Error getting json array element value");
		}
		Object obj1 = convertJSONValueToFormValue(obj);
		return obj1;
	}
}
