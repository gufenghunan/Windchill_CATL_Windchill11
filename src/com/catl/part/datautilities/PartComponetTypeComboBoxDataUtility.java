package com.catl.part.datautilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.fop.util.bitmap.DitherUtil;
import org.apache.log4j.Logger;

import wt.change2.ChangeHelper2;
import wt.change2.WTChangeActivity2;
import wt.change2.WTChangeOrder2;
import wt.fc.Persistable;
import wt.fc.QueryResult;
import wt.part.WTPart;
import wt.util.WTException;
import wt.vc.VersionControlHelper;
import wt.vc.wip.WorkInProgressHelper;

import com.catl.cadence.util.NodeUtil;
import com.catl.change.ChangeUtil;
import com.catl.change.workflow.DcnWorkflowfuncion;
import com.catl.common.util.ClassificationUtil;
import com.catl.common.util.GenericUtil;
import com.catl.ecad.utils.CommonUtil;
import com.catl.ecad.utils.IBAUtility;
import com.catl.part.PartConstant;
import com.catl.part.PartLoadNameSourceUtil;
import com.ptc.core.components.descriptor.ModelContext;
import com.ptc.core.components.factory.dataUtilities.AttributeDataUtilityHelper;
import com.ptc.core.components.factory.dataUtilities.DefaultDataUtility;
import com.ptc.core.components.rendering.guicomponents.ComboBox;
import com.ptc.core.components.rendering.guicomponents.GUIComponentArray;
import com.ptc.core.lwc.server.LWCStructEnumAttTemplate;
import com.ptc.core.meta.type.common.impl.DefaultTypeInstance;
import com.ptc.core.ui.resources.ComponentMode;


public class PartComponetTypeComboBoxDataUtility extends DefaultDataUtility{
	private static Logger logger=Logger.getLogger(PartComponetTypeComboBoxDataUtility.class.getName());
	
	
	@Override
	public Object getDataValue(String componentId, Object datum, ModelContext modelContext) throws WTException {
		Object object = super.getDataValue(componentId, datum, modelContext);
		//Map<String,String> clsnamesource = PartLoadNameSourceUtil.getPartClsNameSource();
		
		GUIComponentArray array = new GUIComponentArray();
        ComboBox comboBox = new ComboBox(); 
        System.out.println(PartComponetTypeComboBoxDataUtility.class.getName()+"1111111111111111");
        ArrayList<String> displayList = new ArrayList<String>();
        ArrayList<String> internalList = new ArrayList<String>();
        //String names =",";
        
		Object obj = modelContext.getNmCommandBean().getPageOid().getRefObject();
		String oldom = "";
		WTPart part = null;
		if(obj instanceof WTPart){
			part = (WTPart)obj;
			
			oldom = (String) GenericUtil.getObjectAttributeValue(part, "CATL_ComponentType");
			internalList.add(oldom);
			displayList.add(oldom);
			    
		}
        
        String actionName = modelContext.getNmCommandBean().getTextParameter("actionName");
        logger.info("===actionName:"+actionName);
        if (modelContext.getDescriptorMode().equals(ComponentMode.EDIT)) {
        	logger.info("componentId==== name======"+componentId);
			String columnName = AttributeDataUtilityHelper.getColumnName(componentId, datum, modelContext);
			logger.info("==columnName:"+columnName);
			comboBox.setId(componentId);
            comboBox.setColumnName(columnName);
            comboBox.setInternalValues(internalList);
            comboBox.setValues(displayList);
            comboBox.setRequired(true);
            
            comboBox.setSelected(oldom);
            
            comboBox.setEnabled(true);
            comboBox.setValueHidden(true);
            //comboBox.addJsAction("onchange","loadHarnessVariant()");
            array.addGUIComponent(comboBox);
            return array;            
           
		 }
       
        return object;
        
	}
	
	/**
	 * 通过受影响对象获取ECO
	 * @param persi
	 * @return
	 * @throws WTException
	 */
	public static List<WTChangeOrder2> getECOByPersistable(Persistable persi) throws WTException{
		List<WTChangeOrder2> ecos = new ArrayList<>();
		WTChangeActivity2 dca = ChangeUtil.getEcaWithPersiser(persi);
		if(dca!=null){
			QueryResult qc = ChangeHelper2.service.getChangeOrder(dca);
			while(qc.hasMoreElements()){
				WTChangeOrder2 eco = (WTChangeOrder2) qc.nextElement();
				System.out.println(eco.getNumber()+"\n"+eco.getName());
				ecos.add(eco);
			}
		}
		return ecos;
	}
}
