package com.catl.part.datautilities;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import wt.inf.container.WTContainerHelper;
import wt.inf.container.WTContainerRef;
import wt.org.DirectoryContextProvider;
import wt.org.OrganizationServicesHelper;
import wt.org.WTOrganization;
import wt.org.WTPrincipal;
import wt.part.QuantityUnit;
import wt.part.WTPart;
import wt.session.SessionHelper;
import wt.util.WTException;

import com.catl.cadence.util.NodeUtil;
import com.catl.part.PartLoadNameSourceUtil;
import com.ptc.core.components.descriptor.ModelContext;
import com.ptc.core.components.factory.dataUtilities.AttributeDataUtilityHelper;
import com.ptc.core.components.factory.dataUtilities.DefaultDataUtility;
import com.ptc.core.components.rendering.guicomponents.ComboBox;
import com.ptc.core.components.rendering.guicomponents.GUIComponentArray;
import com.ptc.core.lwc.server.LWCStructEnumAttTemplate;
import com.ptc.core.ui.resources.ComponentMode;

public class EditDefaultUnitDataUtility extends DefaultDataUtility {
	private static Logger logger=Logger.getLogger(EditDefaultUnitDataUtility.class.getName());
	
	
	@Override
	public Object getDataValue(String componentId, Object datum, ModelContext modelContext) throws WTException {
		/*Object obj = modelContext.getNmCommandBean().getPageOid().getRefObject();
		Object componnet = super.getDataValue(componentId, datum, modelContext);
		WTPrincipal currentUser = SessionHelper.getPrincipal();
		
		if(obj instanceof WTPart && modelContext.getDescriptorMode().equals(ComponentMode.EDIT)){
			if(StringUtils.equals(componentId, "defaultUnit") && (isSiteAdmin(currentUser) || isOrgAdministator(currentUser, "CATL"))){
				Locale local = SessionHelper.getLocale();
				WTPart part = (WTPart)obj;
				ComboBox comboBox = new ComboBox();
				String columnName = AttributeDataUtilityHelper.getColumnName(componentId, obj, modelContext);
				comboBox.setId(componentId);
				comboBox.setColumnName(columnName);
				comboBox.setRequired(true);
				ArrayList<String> displayList = new ArrayList<String>();
		        ArrayList<String> internalList = new ArrayList<String>();
				for (QuantityUnit unit : QuantityUnit.getQuantityUnitSet()) {
					if(unit.isSelectable()){
						displayList.add(unit.getDisplay(local));
						internalList.add(unit.getStringValue());
					}
				}
				comboBox.setValues(displayList);
				comboBox.setInternalValues(internalList);
				comboBox.setSelected(part.getDefaultUnit().getStringValue());

				return comboBox;
			}
		}
			
		
		return componnet;*/
		Object object = super.getDataValue(componentId, datum, modelContext);
		Map<String,String> clsnamesource = PartLoadNameSourceUtil.getPartClsNameSource();
		
		GUIComponentArray array = new GUIComponentArray();
        ComboBox comboBox = new ComboBox(); 
      
        ArrayList<String> displayList = new ArrayList<String>();
        ArrayList<String> internalList = new ArrayList<String>();
        //String names =",";
        
		Object obj = modelContext.getNmCommandBean().getPageOid().getRefObject();
		String oldunit = "";
		WTPart part = null;
		if(obj instanceof WTPart){
			part = (WTPart)obj;
			oldunit = part.getDefaultUnit().toString();//.getSource().toString();
			String unit = "";
			LWCStructEnumAttTemplate cls = NodeUtil.getLWCStructEnumAttTemplateByPart(part);
			String clsname = cls.getName();
			System.out.println("CLSNAME:\t"+clsname);
			if(clsnamesource.containsKey(clsname)){
				String sourcename = clsnamesource.get(clsname);
				System.out.println("Source and name:\t"+sourcename);
				String[] nsarray = sourcename.split("qqqq;;;;");
				if(nsarray.length == 4){
					unit = nsarray[2];
					System.out.println("Source:\t"+unit);
				}
			}
			String[] ab=unit.split("\\|"); 
			for(String temp:ab){
				System.out.println("Add source ............");
				internalList.add(temp.equals("pcs")?"ea":temp);
				displayList.add(temp);
			}
	        //internalList.addAll(Arrays.asList(ab));
			//displayList.addAll(Arrays.asList(ab));
	       
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
            
            comboBox.setSelected(oldunit);
            
            comboBox.setEnabled(true);
            comboBox.setValueHidden(true);
            //comboBox.addJsAction("onchange","loadHarnessVariant()");
            array.addGUIComponent(comboBox);
            return array;            
           
		 }
       
        return object;
	}
	
	
	public static boolean isSiteAdmin(WTPrincipal wtPrincipal) {
		try {
			return WTContainerHelper.service.isAdministrator(WTContainerHelper.service.getExchangeRef(), wtPrincipal);
		} catch (WTException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean isOrgAdministator(WTPrincipal wtprincipal, String strOrgName) {
		try {
			DirectoryContextProvider dcp = WTContainerHelper.service.getExchangeContainer().getContextProvider();
			WTOrganization org = OrganizationServicesHelper.manager.getOrganization(strOrgName, dcp);
			if (org != null) {
				WTContainerRef wtcontainerref = WTContainerHelper.service.getOrgContainerRef(org);
				if (wtcontainerref != null) {
					if (WTContainerHelper.service.isAdministrator(wtcontainerref, wtprincipal)) {
						return true;
					}
				}
			} else {
				System.out.println("WTOrganization is null.");
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return false;
	}

}
