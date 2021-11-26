package com.catl.change.inventory.datautilities;

import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import org.apache.log4j.Logger;

import wt.log4j.LogR;
import wt.part.WTPart;
import wt.util.WTException;
import wt.util.WTMessage;

import com.catl.change.inventory.DispositionOption;
import com.catl.change.inventory.ECAPartLink;
import com.catl.change.inventory.MaterialStatus;
import com.ptc.core.components.descriptor.ModelContext;
import com.ptc.core.components.factory.dataUtilities.DefaultDataUtility;
import com.ptc.core.components.rendering.guicomponents.ComboBox;
import com.ptc.core.components.rendering.guicomponents.DateInputComponent;
import com.ptc.core.components.rendering.guicomponents.GUIComponentArray;
import com.ptc.core.components.rendering.guicomponents.IconComponent;
import com.ptc.core.components.rendering.guicomponents.TextBox;
/**
 * the datautility table builder: changeTask.changeInventory
 * @author admin
 *
 */
public class ChangeInventoryUtility extends DefaultDataUtility {

	private static Logger logger = LogR.getLogger(ChangeInventoryUtility.class.getName());
	
	@Override
	public Object getDataValue(String s, Object obj, ModelContext modelcontext)
			throws WTException {
		logger.debug("==============ChangeInventoryUtility.getDataValue in===============");
		logger.debug("s:::"+s);
		logger.debug("obj:::"+obj);
		logger.debug("modelcontext:::"+modelcontext);
		logger.debug("modelcontext mode:::"+modelcontext.getDescriptorMode());
		Object gui = null;
		try{
			if ("partNumber".equals(s)){
				gui = getPartNumberGUI(s, obj, modelcontext);
			}else if ("partName".equals(s)) {
				gui = getPartNameGUI(s, obj, modelcontext);
			}else if (ECAPartLink.QUANTITY.equals(s)) {
				gui = getQuantityGUI(s, obj, modelcontext);
			}else if (ECAPartLink.OWNER.equals(s)) {
				gui = getOwnerGUI(s, obj, modelcontext);
			}else if (ECAPartLink.MATERIAL_STATUS.equals(s)){
				gui = getMaterialStatusGUI(s, obj, modelcontext);
			}else if (ECAPartLink.DISPOSITION_OPTION.equals(s)){
				gui = getDispositionOptionGUI(s, obj, modelcontext);
			}else if (ECAPartLink.DUE_DAY.equals(s)){
				gui = getDueDayGUI(s, obj, modelcontext);
			}else if (ECAPartLink.REMARKS.equals(s)) {
				gui = getRemarksGUI(s, obj, modelcontext);
			}
		}catch(Exception e){
			throw new WTException(e);
		}
		logger.debug("==============ChangeInventoryUtility.getDataValue out===============");
		return gui;
	}
	
	private Object getRemarksGUI(String s, Object obj, ModelContext modelcontext) throws WTException {
		logger.debug("==============ChangeInventoryUtility getRemarksGUI in===============");
		TextBox text = new TextBox();
		text.setId(s);
		text.setName(s);
		String value = "";
		if(obj instanceof ECAPartLink){
			ECAPartLink link = (ECAPartLink) obj;
			if(link!=null){
				value = link.getRemarks();
			}
		}
		logger.debug("Remarks:::"+value);
		text.setValue(value);
		logger.debug("==============ChangeInventoryUtility getRemarksGUI out===============");
		return text;
	}
	
	private Object getDueDayGUI(String s, Object obj, ModelContext modelcontext) {
		logger.debug("==============ChangeInventoryUtility getDueDayGUI in===============");
		
		DateInputComponent dateComp = new DateInputComponent();
		dateComp.setEditable(true);
		dateComp.setRequired(true);
		dateComp.setColumnName(s);
		dateComp.setRequired(true);
		dateComp.setId(s);
		
		Timestamp time = new Timestamp(new Date().getTime());
		logger.debug("time 1 :::"+time);
		if(obj instanceof ECAPartLink){
			ECAPartLink link = (ECAPartLink) obj;
			if(link != null){
				time = link.getDueDay();
			}
		}
		logger.debug("time 2 :::"+time);
		dateComp.setValue(time);
		logger.debug("==============ChangeInventoryUtility getDueDayGUI out===============");
		
		return dateComp;
	}
	
	
	private Object getDispositionOptionGUI(String s, Object obj, ModelContext modelcontext) throws WTException, Exception {
		logger.debug("==============ChangeInventoryUtility getDispositionOptionGUI in===============");
		
		ComboBox comboBox = new ComboBox();
		comboBox.setEditable(true);
		comboBox.setRequired(true);
		comboBox.setColumnName(s);
		comboBox.setId(s);
		
		String selectedValue = "";//(String) mats.getStringValue();
		
		DispositionOption[] disos = DispositionOption.getDispositionOptionSet();
		logger.debug("disos.length :::"+disos.length);
		ArrayList<String> keyList = new ArrayList<String>();
		ArrayList<String> valueList = new ArrayList<String>();
		
		DispositionOption diso = null;
		for(int i=0;i<disos.length;i++){
			diso = disos[i];
			if(i==0){
				selectedValue = diso.getStringValue();
			}
			keyList.add(diso.getStringValue());
			logger.debug("modelcontext.getLocale():"+modelcontext.getLocale());
			logger.debug("DispositionOption key:"+modelcontext.getLocale());
			logger.debug("DispositionOption value:"+diso.getDisplay(modelcontext.getLocale()));
			logger.debug("DispositionOption value china:"+diso.getDisplay(Locale.CHINA));
			
			String value = diso.getDisplay(modelcontext.getLocale());
			value = new String(value.getBytes("UTF-8"),"GBK");
			
			valueList.add(diso.getDisplay(modelcontext.getLocale()));
		}
		logger.debug("selectedValue 1 :::"+selectedValue);
		
		if(obj instanceof ECAPartLink){
			ECAPartLink link = (ECAPartLink) obj;
			if(link!=null){
				diso = link.getDispositionOption();
				if(diso!=null){
					selectedValue = diso.getStringValue();
				}
			}
		}
		logger.debug("selectedValue 2 :::"+selectedValue);
		
		comboBox.setValues(valueList);
		comboBox.setInternalValues(keyList);
		comboBox.setSelected(selectedValue);
		logger.debug("==============ChangeInventoryUtility getDispositionOptionGUI out===============");
		return comboBox;
	}
	
	private Object getMaterialStatusGUI(String s, Object obj, ModelContext modelcontext) throws WTException {
		logger.debug("==============ChangeInventoryUtility getMaterialStatusGUI in===============");
		
		ComboBox comboBox = new ComboBox();
		comboBox.setEditable(true);
		comboBox.setRequired(true);
		comboBox.setColumnName(s);
		comboBox.setId(s);
		
		String selectedValue = "";//(String) mats.getStringValue();
		
		MaterialStatus[] matss = MaterialStatus.getMaterialStatusSet();
		logger.debug("matss.length :::"+matss.length);
		ArrayList<String> keyList = new ArrayList<String>();
		ArrayList<String> valueList = new ArrayList<String>();
		
		MaterialStatus mats = null;
		for(int i=0;i<matss.length;i++){
			mats = matss[i];
			if(i==0){
				selectedValue = mats.getStringValue();
			}
			keyList.add(mats.getStringValue());
			valueList.add(mats.getDisplay(modelcontext.getLocale()));
		}
		logger.debug("selectedValue 1 :::"+selectedValue);
		
		if(obj instanceof ECAPartLink){
			ECAPartLink link = (ECAPartLink) obj;
			if(link!=null){
				mats = link.getMaterialStatus();
				if(mats!=null){
					selectedValue = mats.getStringValue();
				}
			}
		}
		logger.debug("selectedValue 2 :::"+selectedValue);
		
		comboBox.setValues(valueList);
		comboBox.setInternalValues(keyList);
		comboBox.setSelected(selectedValue);
		logger.debug("==============ChangeInventoryUtility getMaterialStatusGUI out===============");
		return comboBox;
	}
	
	
	
	
	private Object getOwnerGUI(String s, Object obj, ModelContext modelcontext) throws WTException {
		logger.debug("==============ChangeInventoryUtility getOwnerGUI in===============");
		TextBox text = new TextBox();
		text.setId(s);
		text.setName(s);
		text.setRequired(true);
		String value = "";
		if(obj instanceof ECAPartLink){
			ECAPartLink link = (ECAPartLink) obj;
			if(link!=null){
				value = link.getOwner();
			}
		}
		logger.debug("Owner:::"+value);
		text.setValue(value);
		logger.debug("==============ChangeInventoryUtility getOwnerGUI out===============");
		return text;
	}
	
	private Object getQuantityGUI(String s, Object obj, ModelContext modelcontext) throws WTException {
		logger.debug("==============ChangeInventoryUtility getQuantityGUI in===============");
		TextBox text = new TextBox();
		text.setId(s);
		text.setName(s);
		text.setRequired(true);
		String value = "";
		if(obj instanceof ECAPartLink){
			ECAPartLink link = (ECAPartLink) obj;
			if(link!=null){
				value = String.valueOf(link.getQuantity());
			}
		}
		logger.debug("Quantity:::"+value);
		text.setValue(value);
		text.addJsAction(
				"onBlur",
				"checkQuantity(this)");
		logger.debug("==============ChangeInventoryUtility getQuantityGUI out===============");
		return text;
	}

	
	private Object getPartNameGUI(String s, Object obj, ModelContext modelcontext) throws WTException {
		logger.debug("==============ChangeInventoryUtility.getReadOnlyTextGUI in===============");
		TextBox text = new TextBox();
		text.setId(s);
		text.setName(s);
		//text.setEditable(false);
		String value = "";
		if(obj instanceof ECAPartLink){
			ECAPartLink link = (ECAPartLink) obj;
			if(link!=null){
				WTPart part = link.getPart();
				value = part.getName();
				logger.debug("partName:::"+value);
			}
		}
		text.setValue(value);
		logger.debug("==============ChangeInventoryUtility.getReadOnlyTextGUI out===============");
		return text;
	}
	
	
	private Object getPartNumberGUI(String s, Object obj, ModelContext modelcontext) throws WTException {
		logger.debug("==============ChangeInventoryUtility getPartNumberGUI in===============");
		GUIComponentArray gui = new GUIComponentArray();
		TextBox text = new TextBox();
		text.setId(s);
		text.setName(s);
		text.setRequired(true);
		if(obj!=null && obj instanceof ECAPartLink){
			ECAPartLink link  = (ECAPartLink) obj;
			WTPart part = link.getPart();
			text.setValue(part.getNumber());
			text.setEditable(false);
			gui.addGUIComponent(text);
		}else{
			text.setEditable(true);
			IconComponent picker = new IconComponent();
	        picker.setSrc("netmarkets/images/search_14x14.png");
	        picker.setName(s);
	        picker.addJsAction(
					"onClick",
					"openPicker(this)");
	        gui.addGUIComponent(text);
			gui.addGUIComponent(picker);
		}
		gui.setRequired(true);
		logger.debug("==============ChangeInventoryUtility getPartNumberGUI out===============");
		return gui;
	}
	

	public String getLabel(String s, ModelContext modelcontext)
			throws WTException {
		return WTMessage.getLocalizedMessage(
				"com.catl.change.inventory.resource.ChangeInventoryRB", s, null,
				modelcontext.getLocale());
	}
	
}
