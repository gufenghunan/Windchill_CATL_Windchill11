package com.catl.doc.DataUtility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import wt.doc.WTDocument;
import wt.fc.Persistable;
import wt.fc.ReferenceFactory;
import wt.services.applicationcontext.implementation.ServiceProperties;
import wt.util.WTException;

import com.catl.common.util.GenericUtil;
import com.ptc.core.components.descriptor.ModelContext;
import com.ptc.core.components.factory.dataUtilities.DefaultDataUtility;
import com.ptc.core.components.rendering.guicomponents.AttributeDisplayCompositeComponent;
import com.ptc.core.components.rendering.guicomponents.AttributeInputCompositeComponent;
import com.ptc.core.meta.type.common.impl.DefaultTypeInstance;
import com.ptc.core.ui.resources.ComponentMode;


public class ENWDataUtility extends DefaultDataUtility{
	private static Logger logger=Logger.getLogger(ENWDataUtility.class.getName());
	@Override
	public Object getDataValue(String componentId, Object datum, ModelContext modelContext) throws WTException {
		
		Object object = super.getDataValue(componentId, datum, modelContext);
		
		//获取ENW类型配置
		Properties wtproperties = ServiceProperties.getServiceProperties("WTServiceProviderFromProperties");
		String ENWDocType = wtproperties.getProperty("ENWDocType");
		List<String> enwdoctypelist = new ArrayList<String>();
        if(StringUtils.isNotEmpty(ENWDocType)){
        	String[] docTypeArr = ENWDocType.split(",");
        	enwdoctypelist = Arrays.asList(docTypeArr); 

        }
        
		if (ComponentMode.EDIT.equals(modelContext.getDescriptorMode())){
			DefaultTypeInstance typeInstance=(DefaultTypeInstance)datum;
	        logger.debug("----2-----"+typeInstance.getIdentifier().getInstanceIdentifier());
	        String oidString=typeInstance.getIdentifier().getInstanceIdentifier().toString();
	        String oid ="VR:wt.doc.WTDocument:"+oidString.substring(oidString.lastIndexOf("|")+1,oidString.length());
	        logger.debug("oid==="+oid);
	        ReferenceFactory rf = new ReferenceFactory();
	        Persistable rfobj = rf.getReference(oid).getObject();
	        WTDocument docObj= (WTDocument) rfobj;
	        
	        String subCategory = (String) GenericUtil.getObjectAttributeValue(docObj, "subCategory");
	        
	        AttributeInputCompositeComponent obj = (AttributeInputCompositeComponent) object;
        	obj.setComponentHidden(true);
	        if (StringUtils.isNotEmpty(subCategory)){
	        	String subType = subCategory.substring(subCategory.lastIndexOf("-")+1, subCategory.length());
		        if (enwdoctypelist.contains(subType)){
		        	obj.setComponentHidden(false);
		        }
	        }
		} else if (ComponentMode.VIEW.equals(modelContext.getDescriptorMode())){
			
			WTDocument docObj= (WTDocument) datum;
	        String subCategory = (String) GenericUtil.getObjectAttributeValue(docObj, "subCategory");
	        
	        AttributeDisplayCompositeComponent obj = (AttributeDisplayCompositeComponent) object;
        	obj.setComponentHidden(true);
	        if (StringUtils.isNotEmpty(subCategory)){
	        	String subType = subCategory.substring(subCategory.lastIndexOf("-")+1, subCategory.length());
		        if (enwdoctypelist.contains(subType)){
		        	obj.setComponentHidden(false);
		        }
	        }
		}
        return object;
	}
}
