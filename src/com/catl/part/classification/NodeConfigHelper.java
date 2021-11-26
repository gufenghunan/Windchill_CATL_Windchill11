package com.catl.part.classification;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.ptc.core.lwc.common.AttributeTemplateFlavor;
import com.ptc.core.lwc.common.view.TypeDefinitionReadView;
import com.ptc.core.lwc.server.LWCStructEnumAttTemplate;
import com.ptc.core.lwc.server.TypeDefinitionServiceHelper;
import com.ptc.windchill.csm.client.helpers.CSMTypeDefHelper;
import com.ptc.windchill.csm.client.utils.CSMUtils;
import com.ptc.windchill.csm.common.CsmConstants;

import wt.fc.ObjectReference;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.pds.StatementSpec;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

public class NodeConfigHelper {

	public static LWCStructEnumAttTemplate getClassificationNode(String internalName) throws WTException{
		TypeDefinitionReadView drv= TypeDefinitionServiceHelper.service.getTypeDefView(AttributeTemplateFlavor.LWCSTRUCT, CsmConstants.NAMESPACE, internalName);
		if(drv != null){
			Object obj = ObjectReference.newObjectReference(drv.getOid()).getObject();
			if(obj instanceof LWCStructEnumAttTemplate){
				return (LWCStructEnumAttTemplate)obj;
			}
		}
		return null;
	}
	
	public static Set<LWCStructEnumAttTemplate> getAllInstantiableNode() throws WTException{
		Set<LWCStructEnumAttTemplate> set = new HashSet<LWCStructEnumAttTemplate>();
		QuerySpec qs  = new QuerySpec(LWCStructEnumAttTemplate.class);
		QueryResult qr = PersistenceHelper.manager.find((StatementSpec)qs);
		while(qr.hasMoreElements()){
			LWCStructEnumAttTemplate node = (LWCStructEnumAttTemplate)qr.nextElement();
			System.out.println("node.getName():"+node.getName());
			if(!CSMUtils.isRootNode(node) && instantiable(node)){
				set.add(node);
			}
		}
		return set;
	}
	
	public static Set<LWCStructEnumAttTemplate> getAllNodes() throws WTException{
		Set<LWCStructEnumAttTemplate> set = new HashSet<LWCStructEnumAttTemplate>();
		QuerySpec qs  = new QuerySpec(LWCStructEnumAttTemplate.class);
		QueryResult qr = PersistenceHelper.manager.find((StatementSpec)qs);
		while(qr.hasMoreElements()){
			LWCStructEnumAttTemplate node = (LWCStructEnumAttTemplate)qr.nextElement();
			if(!CSMUtils.isRootNode(node)){
				set.add(node);
			}
		}
		return set;
	}
	
	public static void autoCreateNodeConfig(LWCStructEnumAttTemplate node) throws WTException{
		if(node != null){
			Long nodeId = node.getPersistInfo().getObjectIdentifier().getId();
			ClassificationNodeConfig nodeConfig = getNodeConfig(node);
			try {
				if(nodeConfig != null){
					if(!nodeConfig.getNodeId().equals(nodeId) || !nodeConfig.getNodeInternalName().equals(node.getName())){
						nodeConfig.setNodeId(nodeId);
						nodeConfig.setNodeInternalName(node.getName());
						PersistenceHelper.manager.save(nodeConfig);
					}
				}
				else {
					nodeConfig = new ClassificationNodeConfig();
					nodeConfig.setNodeId(nodeId);
					nodeConfig.setNodeInternalName(node.getName());
					PersistenceHelper.manager.save(nodeConfig);
				}
			} catch (WTPropertyVetoException e) {
				e.printStackTrace();
				throw new WTException(e.getLocalizedMessage());
			}
		}
	}
	
	public static void saveNodeConfig(Map<String, Object> attrs) throws WTException{
		String nodeName = (String)attrs.get(ClassificationNodeConfig.NODE_INTERNAL_NAME);
		if(nodeName != null){
			AttributeForFAE attRef = (AttributeForFAE)attrs.get(ClassificationNodeConfig.ATTRIBUTE_REF);
			Boolean needFae = (Boolean)attrs.get(ClassificationNodeConfig.NEED_FAE);
			Boolean needNonFaeReport = (Boolean)attrs.get(ClassificationNodeConfig.NEED_NON_FAE_REPORT);
			Boolean makeFae = (Boolean)attrs.get(ClassificationNodeConfig.MAKE_NEED_FAE);
			Boolean buyFae = (Boolean)attrs.get(ClassificationNodeConfig.BUY_NEED_FAE);
			Boolean makeBuyFae = (Boolean)attrs.get(ClassificationNodeConfig.MAKE_BUY_NEED_FAE);
			Boolean customerFae = (Boolean)attrs.get(ClassificationNodeConfig.CUSTOMER_NEED_FAE);
			Boolean virtualFae = (Boolean)attrs.get(ClassificationNodeConfig.VIRTUAL_NEED_FAE);
			LWCStructEnumAttTemplate node = getClassificationNode(nodeName);
			if(node != null){
				try {
					Long nodeId = node.getPersistInfo().getObjectIdentifier().getId();
					ClassificationNodeConfig nodeConfig = getNodeConfig(node);
					if(nodeConfig == null){
						nodeConfig = new ClassificationNodeConfig();
					}
					nodeConfig.setNodeId(nodeId);
					nodeConfig.setNodeInternalName(nodeName);
					if(attRef != null){
						nodeConfig.setAttributeRef(attRef);
					}
					if(needFae != null){
						nodeConfig.setNeedFae(needFae);
					}
					if(needNonFaeReport != null){
						nodeConfig.setNeedNonFaeReport(needNonFaeReport);
					}
					if(makeFae != null){
						nodeConfig.setMakeNeedFae(makeFae);
					}
					if(buyFae != null){
						nodeConfig.setBuyNeedFae(buyFae);
					}
					if(makeBuyFae != null){
						nodeConfig.setMakeBuyNeedFae(makeBuyFae);
					}
					if(customerFae != null){
						nodeConfig.setCustomerNeedFae(customerFae);
					}
					if(virtualFae != null){
						nodeConfig.setVirtualNeedFae(virtualFae);
					}
					PersistenceHelper.manager.save(nodeConfig);
				} catch (WTPropertyVetoException e) {
					e.printStackTrace();
					throw new WTException(e.getLocalizedMessage());
				}
			}
		}
	}
	
	public static ClassificationNodeConfig getNodeConfig(LWCStructEnumAttTemplate node) throws WTException{
		if(node != null){
			long nodeId = node.getPersistInfo().getObjectIdentifier().getId();
			String name = node.getName();
			int[] index = new int[]{0};
			QuerySpec qs  = new QuerySpec(ClassificationNodeConfig.class);
			qs.appendWhere(new SearchCondition(ClassificationNodeConfig.class,ClassificationNodeConfig.NODE_ID,SearchCondition.EQUAL,nodeId), index);
			QueryResult qr = PersistenceHelper.manager.find((StatementSpec)qs);
			if(qr.hasMoreElements()){
				return (ClassificationNodeConfig)qr.nextElement();
			}
			qs  = new QuerySpec(ClassificationNodeConfig.class);
			qs.appendWhere(new SearchCondition(ClassificationNodeConfig.class,ClassificationNodeConfig.NODE_INTERNAL_NAME,SearchCondition.EQUAL,name), index);
			qr = PersistenceHelper.manager.find((StatementSpec)qs);
			if(qr.hasMoreElements()){
				return (ClassificationNodeConfig)qr.nextElement();
			}
		}
		return null;
	}
	
	public static ClassificationNodeConfig getNodeConfig(String internalName) throws WTException{
		LWCStructEnumAttTemplate node = getClassificationNode(internalName);
		return getNodeConfig(node);
	}
	
	public static boolean instantiable(LWCStructEnumAttTemplate node){
		TypeDefinitionReadView defRV = CSMTypeDefHelper.getRV(node);
		if(defRV != null){
			return (Boolean)defRV.getPropertyValueByName("instantiable").getValue();
		}
		return false;
	}
	
	public static void deleteNodeConfig(LWCStructEnumAttTemplate node) throws WTException{
		ClassificationNodeConfig nodeConfig = getNodeConfig(node);
		if(nodeConfig != null){
			PersistenceHelper.manager.delete(nodeConfig);
		}
	}
}
