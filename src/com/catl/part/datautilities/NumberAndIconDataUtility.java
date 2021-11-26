package com.catl.part.datautilities;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import wt.epm.EPMDocument;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.part.WTPartSubstituteLink;
import wt.util.WTException;

import com.ptc.carambola.rendering.HTMLComponent;
import com.ptc.core.components.descriptor.ModelContext;
import com.ptc.core.components.factory.dataUtilities.DefaultDataUtility;
import com.ptc.core.ui.resources.ComponentMode;

public class NumberAndIconDataUtility extends DefaultDataUtility {
	
	private static Logger logger = Logger.getLogger(NumberAndIconDataUtility.class);

	@Override
	public Object getDataValue(String componentId, Object obj, ModelContext modelContext)
			throws WTException {
	    //logger.debug("....................................componentId="+componentId);
	    //logger.debug("....................................obj="+obj);
	    //logger.debug("....................................modelContext="+modelContext);
	    if(obj instanceof WTPart){
	        WTPart part = (WTPart)obj;
	        HTMLComponent htmlComponent = new HTMLComponent(componentId);
	        String html = "<img title='零部件' src='wtcore/images/part.gif' />"+part.getNumber();
	        htmlComponent.setHTML(html); 
	        return htmlComponent;
	    }else if(obj instanceof EPMDocument){
	        EPMDocument epm = (EPMDocument)obj;
	        HTMLComponent htmlComponent = new HTMLComponent(componentId);
	        if(epm.getNumber().endsWith("CATDRAWING")){
	            String html = "<img title='绘图' src='wt/clients/images/catia5/epm_ctv5_dwg.gif' />"+epm.getNumber();
	            htmlComponent.setHTML(html);
	            return htmlComponent;
	        }else if(epm.getNumber().endsWith("CATPART")){
	            String html = "<img title='CAD 部件' src='wt/clients/images/catia5/epm_ctv5_part.gif' />"+epm.getNumber();
	            htmlComponent.setHTML(html);
	            return htmlComponent;
	        }else if(epm.getNumber().endsWith("CATPRODUCT")){
                String html = "<img title='CAD 部件' src='wt/clients/images/catia5/epm_ctv5_asm.gif' />"+epm.getNumber();
                htmlComponent.setHTML(html);
                return htmlComponent;
            }
        }
		return super.getDataValue(componentId, obj, modelContext);
	}

}
