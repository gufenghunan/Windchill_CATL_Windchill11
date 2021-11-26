package com.catl.line.test;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.type.TypedUtility;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.catl.cadence.util.NodeUtil;
import com.catl.common.util.GenericUtil;
import com.catl.ecad.utils.CommonUtil;
import com.catl.line.entity.LayoutComparator;
import com.catl.loadData.IBAUtility;
import com.ptc.core.HTMLtemplateutil.server.processors.EntityTaskDelegate;
import com.ptc.core.lwc.common.LayoutComponent;
import com.ptc.core.lwc.common.view.AttributeDefinitionReadView;
import com.ptc.core.lwc.common.view.GroupDefinitionReadView;
import com.ptc.core.lwc.common.view.GroupMembershipReadView;
import com.ptc.core.lwc.common.view.LayoutComponentReadView;
import com.ptc.core.lwc.common.view.LayoutDefinitionReadView;
import com.ptc.core.lwc.common.view.TypeDefinitionReadView;
import com.ptc.core.lwc.server.LWCStructEnumAttTemplate;
import com.ptc.core.lwc.server.TypeDefinitionServiceHelper;
import com.ptc.core.meta.common.AttributeTypeIdentifier;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.core.meta.common.TypeInstanceIdentifier;
import com.ptc.core.meta.container.common.AttributeTypeSummary;
import com.ptc.jca.mvc.LayoutComponentComparator;
import com.ptc.windchill.csm.client.utils.CSMUtils;

public class TestGetClf7 implements RemoteAccess{
	public static String getPsize(String type) throws WTException{
        ArrayList vals = new ArrayList();
        ArrayList attributeTypeSummaries = new ArrayList();
        ArrayList attributeIdentifierStrings = new ArrayList();
        StringBuffer typeInstanceIdentifierString = new StringBuffer();
        ArrayList attributeStates = new ArrayList();
        EntityTaskDelegate.getSoftAttributes(type, true, false, typeInstanceIdentifierString, attributeIdentifierStrings,
        attributeTypeSummaries, vals, attributeStates, Locale.ENGLISH);
        for(int i=0;i<attributeTypeSummaries.size();i++){
			 AttributeTypeSummary ats = (AttributeTypeSummary)attributeTypeSummaries.get(i);
			 String lable = ats.getLabel();
			 AttributeTypeIdentifier ati = ats.getAttributeTypeIdentifier();
			 String attrName = ati.getAttributeName();
			 System.out.println("----"+attrName);
        }
    
		return type;
		
	}
	
	public static String getAllAttr(WTPart part) throws WTException{
		LWCStructEnumAttTemplate lwc=NodeUtil.getLWCStructEnumAttTemplateByPart(part);
		if(lwc==null){
			return null;
		}
		String specification = "";
        TypeInstanceIdentifier TiId = TypedUtility
                .getTypeInstanceIdentifier(part);
        TypeIdentifier tId = (TypeIdentifier) TiId.getDefinitionIdentifier();
        TypeDefinitionReadView typReadVw = TypeDefinitionServiceHelper.service.getTypeDefView(tId);
        Set<AttributeTypeIdentifier> attributes = CSMUtils.getClassificationConstraintAttributes(typReadVw);
       System.out.println("attributes.size="+attributes.size());
       AttributeTypeIdentifier clfati=null;
        for (AttributeTypeIdentifier ati : attributes) {
        	if(ati.getAttributeName().equals("cls")){
        		clfati=ati;
        		break;
        	}
        	
        }
        System.out.println("###"+clfati.getAttributeName());
		List<LayoutDefinitionReadView> layouts = new ArrayList<LayoutDefinitionReadView>();
		Set<LayoutDefinitionReadView> nestedLayouts = TypeDefinitionServiceHelper.service
				.getLayoutDefinitions(clfati, new String[] { lwc.getName() }, null,null);
		layouts.addAll(nestedLayouts);
		Collections.sort(layouts, new LayoutComparator());
		Iterator<LayoutDefinitionReadView> iterator = layouts.iterator();
		while (iterator.hasNext()) {
			LayoutDefinitionReadView layout = iterator.next();
			System.out.println(layout.getName());
			for (LayoutComponentReadView layComp : layout
					.getAllLayoutComponents()) {
				if (layComp instanceof GroupDefinitionReadView) {
					GroupDefinitionReadView layGrp = (GroupDefinitionReadView) layComp;
					ArrayList<LayoutComponent> attributesList = new ArrayList<LayoutComponent>(
							layGrp.getComponents());
					Collections.sort(attributesList,LayoutComponentComparator.getInstance());
					Iterator<LayoutComponent> iter = attributesList.iterator();
					while (iter.hasNext()) {
						LayoutComponent comp = iter.next();
						if (((GroupMembershipReadView) comp).getMember() instanceof AttributeDefinitionReadView) {
							AttributeDefinitionReadView attrReadView = (AttributeDefinitionReadView) ((GroupMembershipReadView) comp)
									.getMember();
							String name = attrReadView.getName();
							String displayName = attrReadView.getDisplayName();
							System.out.println(displayName+"\t"+name);
							specification = specification + "_" + displayName + ":" + GenericUtil.getObjectAttributeValue(part, name);
						}
					}
				}
			}
		}
		
		if (specification.startsWith("_"))
		{
			specification = specification.substring(1, specification.length());
		}
		if (specification.endsWith("_"))
		{
			specification = specification.substring(0, specification.length() - 1);
		}
	
		return specification;
	}
	public static void test() throws WTException, WTPropertyVetoException, RemoteException {
		WTPart part=CommonUtil.getLatestWTpartByNumber("530100-00358");
		String spec = getAllAttr(part);
 	   if(StringUtils.isNotBlank(spec)){
 		   IBAUtility iba = new IBAUtility(part);
 		   System.out.println(" update "+part.getNumber()+",value="+spec);
 		   
    	
 		   iba.setIBAValue("specification", spec);
 		   iba.updateAttributeContainer(part);
 		   iba.updateIBAHolder(part);
 		   System.out.println(" update "+part.getNumber()+",value="+spec+" success");
 		  // writer.write(" update "+part.getNumber()+",value="+spec+" success\n");
        }
	}
	public static void main(String[] args) throws Exception {
		RemoteMethodServer rms = RemoteMethodServer.getDefault();
	    rms.setUserName(args[0]);
	    rms.setPassword(args[1]);
		rms.invoke("test", TestGetClf7.class.getName(), null, null, null);
		
	}
}
