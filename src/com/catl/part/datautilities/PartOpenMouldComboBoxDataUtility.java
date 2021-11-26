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
import com.ptc.carambola.rendering.HTMLComponent;
import com.ptc.core.components.descriptor.ModelContext;
import com.ptc.core.components.factory.dataUtilities.AttributeDataUtilityHelper;
import com.ptc.core.components.factory.dataUtilities.DefaultDataUtility;
import com.ptc.core.components.rendering.GuiComponent;
import com.ptc.core.components.rendering.guicomponents.ComboBox;
import com.ptc.core.components.rendering.guicomponents.GUIComponentArray;
import com.ptc.core.lwc.server.LWCStructEnumAttTemplate;
import com.ptc.core.meta.type.common.impl.DefaultTypeInstance;
import com.ptc.core.ui.resources.ComponentMode;


public class PartOpenMouldComboBoxDataUtility extends DefaultDataUtility{
	private static Logger logger=Logger.getLogger(PartOpenMouldComboBoxDataUtility.class.getName());
	
	
	@Override
	public Object getDataValue(String componentId, Object datum, ModelContext modelContext) throws WTException {
		Object object = super.getDataValue(componentId, datum, modelContext);
		Map<String,String> clsnamesource = PartLoadNameSourceUtil.getPartClsNameSource();
		
		GUIComponentArray array = new GUIComponentArray();
        ComboBox comboBox = new ComboBox(); 
        HTMLComponent htmlComponent = new HTMLComponent(componentId);
        String html = "<p style='font-family:verdana;color:red'>是否开模，如果是开模件则选是，否则选否</p>";
        htmlComponent.setHTML(html); 
        
        ArrayList<String> displayList = new ArrayList<String>();
        ArrayList<String> internalList = new ArrayList<String>();
        //String names =",";
        
		Object obj = modelContext.getNmCommandBean().getPageOid().getRefObject();
		String oldom = "";
		WTPart part = null;
		if(obj instanceof WTPart){
			part = (WTPart)obj;
			WTPart prePart = null;
			try {
				prePart = (WTPart) CommonUtil.getPreVersionObject(part);
				if(prePart != null){
					if(!WorkInProgressHelper.isWorkingCopy(prePart)&WorkInProgressHelper.isWorkingCopy(part)){
						System.out.println("is work copy");
						prePart = (WTPart) CommonUtil.getPreVersionObject(prePart);
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			oldom = (String) GenericUtil.getObjectAttributeValue(part, PartConstant.OpenMould);
			if("是".equals(oldom)){
				internalList.add(oldom);
				displayList.add(oldom);
			}else{
				Vector changetypes = new Vector<>();
				if(prePart != null){
					List<WTChangeOrder2> ecos = getECOByPersistable(prePart);				
					for (int i = 0; i < ecos.size(); i++) {
						IBAUtility iba = new IBAUtility(ecos.get(i));
						Vector vector = iba.getIBAValues("changeType");
						changetypes.addAll(vector);
					}
				}
				String openMould = "";
				LWCStructEnumAttTemplate cls = NodeUtil.getLWCStructEnumAttTemplateByPart(part);
				String clsname = cls.getName();
				System.out.println("CLSNAME:\t"+clsname);
				if(clsnamesource.containsKey(clsname)){
					String sourcename = clsnamesource.get(clsname);
					System.out.println("Source and name:\t"+sourcename);
					String[] nsarray = sourcename.split("qqqq;;;;");
					if(nsarray.length == 4){
						openMould = nsarray[3];
						System.out.println("Source:\t"+openMould);
					}
				}
				if("不适用".equals(openMould)||"否".equals(openMould)){
					internalList.add(openMould);
					displayList.add(openMould);
					if(!part.getVersionInfo().getIdentifier().getValue().equals("1")&&"否".equals(openMould)&&changetypes.contains("开模图纸升级")){
						internalList.add("是");
						displayList.add("是");
					}
				}
			}	       
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
            array.addGUIComponent(htmlComponent);
            return array;            
           
		 }
        array.addGUIComponent((GuiComponent) object);
        array.addGUIComponent(htmlComponent);
        return array;
        
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
