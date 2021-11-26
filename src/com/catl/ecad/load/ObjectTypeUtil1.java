package com.catl.ecad.load;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.core.meta.common.TypeIdentifierHelper;
import com.ptc.core.meta.type.mgmt.server.impl.WTTypeDefinitionMaster;

import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.type.TypedUtility;
import wt.type.TypedUtilityServiceHelper;
import wt.util.WTException;

public class ObjectTypeUtil1 {
	public static void main(String[] args) throws WTException{
		getAllTypeDefinitionMap();
	}

	/**
	 * 得到类型定义Master
	 * 
	 * @param type
	 * @return
	 * @throws WTException
	 */
	public static WTTypeDefinitionMaster getTypeDefinitionMaster(String type)
			throws WTException {
		QuerySpec qs = new QuerySpec(WTTypeDefinitionMaster.class);
		qs.appendSearchCondition(new SearchCondition(
				WTTypeDefinitionMaster.class,
				WTTypeDefinitionMaster.DISPLAY_NAME_KEY, SearchCondition.EQUAL,
				type));
		System.out.println(qs);
		QueryResult qr = PersistenceHelper.manager.find(qs);
		while (qr.hasMoreElements()) {
			return (WTTypeDefinitionMaster) qr.nextElement();
		}

		return null;
	}
	
	/**
	 * 得到所有类型定义显示名称与内部名称的Map
	 * 
	 * @param type
	 * @return
	 * @throws WTException
	 */
	public static Map<String, String> getAllTypeDefinitionMaster()
			throws WTException {
		Map<String, String> typeDisplayInternalMap = new HashMap<>();
		QuerySpec qs = new QuerySpec(WTTypeDefinitionMaster.class);

		
		QueryResult qr = PersistenceHelper.manager.find(qs);
		while (qr.hasMoreElements()) {
			WTTypeDefinitionMaster typemaster = (WTTypeDefinitionMaster) qr.nextElement();
			if(typemaster.getDeleted_id() == null){
			String internalName = typemaster.getIntHid();
			
			TypeIdentifier typeIdentifier = TypedUtilityServiceHelper.service.getTypeIdentifier(internalName);
			TypeIdentifier supertypeIdentifier=null;
			
			try{
				supertypeIdentifier = TypedUtilityServiceHelper.service.getSupertype(typeIdentifier);
			}catch(Exception e){
			}
			if(supertypeIdentifier==null){
				continue;
			}
			String superName=TypedUtility.getLocalizedTypeName(supertypeIdentifier, Locale.CHINA);
			String dispalyName= TypedUtility.getLocalizedTypeName(typeIdentifier, Locale.CHINA);
			
			if(!superName.equals("文档")&&!superName.equals("参考文档")){
				dispalyName = superName+dispalyName;
			}
			
			typeDisplayInternalMap.put(dispalyName, internalName);			
			System.out.println(dispalyName + "\t" + internalName);
			}
		}

		return typeDisplayInternalMap;
	}
	
	/**
	 * 得到所有类型定义显示名称与内部名称的Map
	 * 
	 * @param type
	 * @return
	 * @throws WTException
	 */
	public static Map<String, String> getAllTypeDefinitionMap()
			throws WTException {
		Map<String, String> typeDisplayInternalMap = new HashMap<>();
		QuerySpec qs = new QuerySpec(WTTypeDefinitionMaster.class);
		/*SearchCondition sc = new SearchCondition(WTTypeDefinitionMaster.class, WTTypeDefinitionMaster.DELETED_ID, SearchCondition.IS_NULL);
		qs.appendWhere(sc);
		System.out.println(qs);*/
		QueryResult qr = PersistenceHelper.manager.find(qs);
		System.out.println(qr.size());
		while (qr.hasMoreElements()) {
			WTTypeDefinitionMaster typemaster = (WTTypeDefinitionMaster) qr.nextElement();			
			//System.out.println(typemaster.getDeleted_id());
			if(typemaster.getDeleted_id() == null){
				String internalName = typemaster.getIntHid();
				//System.out.println(internalName);
				TypeIdentifier typeIdentifier = TypedUtilityServiceHelper.service.getTypeIdentifier(internalName);
				//TypeIdentifier supertypeIdentifier = TypedUtilityServiceHelper.service.getSupertype(typeIdentifier);
				System.out.println("InternalName:\t"+typeIdentifier.getTypeInternalName());
				System.out.println("TypeName:\t"+typeIdentifier.getTypename());
				
				
				String dispalyName= TypedUtility.getLocalizedTypeName(typeIdentifier, Locale.CHINA);
				
				
				
				typeDisplayInternalMap.put(dispalyName, internalName);			
				System.out.println(dispalyName + "\t" + internalName);
			}
			
		}

		return typeDisplayInternalMap;
	}
	
	/**
	 * 得到所有类型定义显示名称与内部名称的Map
	 * 
	 * @param type
	 * @return
	 * @throws WTException
	 */
	public static Map<String, String> getAllTypeInternalDisMap()
			throws WTException {
		Map<String, String> typeDisplayInternalMap = new HashMap<>();
		QuerySpec qs = new QuerySpec(WTTypeDefinitionMaster.class);
		/*SearchCondition sc = new SearchCondition(WTTypeDefinitionMaster.class, WTTypeDefinitionMaster.DELETED_ID, SearchCondition.IS_NULL);
		qs.appendWhere(sc);
		System.out.println(qs);*/
		QueryResult qr = PersistenceHelper.manager.find(qs);
		System.out.println(qr.size());
		while (qr.hasMoreElements()) {
			WTTypeDefinitionMaster typemaster = (WTTypeDefinitionMaster) qr.nextElement();			
			//System.out.println(typemaster.getDeleted_id());
			if(typemaster.getDeleted_id() == null){
				String internalName = typemaster.getIntHid();
				//System.out.println(internalName);
				TypeIdentifier typeIdentifier = TypedUtilityServiceHelper.service.getTypeIdentifier(internalName);
				//TypeIdentifier supertypeIdentifier = TypedUtilityServiceHelper.service.getSupertype(typeIdentifier);
				
				
				String dispalyName= TypedUtility.getLocalizedTypeName(typeIdentifier, Locale.CHINA);
				
				
				
				typeDisplayInternalMap.put(internalName, dispalyName);			
				//System.out.println(dispalyName + "\t" + internalName);
			}
			
		}

		return typeDisplayInternalMap;
	}
}
