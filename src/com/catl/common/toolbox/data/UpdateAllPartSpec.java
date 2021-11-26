package com.catl.common.toolbox.data;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.httpgw.GatewayAuthenticator;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.query.QuerySpec;
import wt.session.SessionHelper;
import wt.type.TypedUtility;
import wt.util.WTException;
import wt.vc.config.LatestConfigSpec;
import wt.vc.wip.WorkInProgressHelper;

import com.catl.common.util.ClassificationUtil;
import com.catl.line.entity.LayoutComparator;
import com.catl.loadData.IBAUtility;
import com.ptc.core.lwc.common.LayoutComponent;
import com.ptc.core.lwc.common.view.AttributeDefinitionReadView;
import com.ptc.core.lwc.common.view.GroupDefinitionReadView;
import com.ptc.core.lwc.common.view.GroupMembershipReadView;
import com.ptc.core.lwc.common.view.LayoutComponentReadView;
import com.ptc.core.lwc.common.view.LayoutDefinitionReadView;
import com.ptc.core.lwc.common.view.PropertyHolderHelper;
import com.ptc.core.lwc.common.view.TypeDefinitionReadView;
import com.ptc.core.lwc.server.LWCStructEnumAttTemplate;
import com.ptc.core.lwc.server.TypeDefinitionServiceHelper;
import com.ptc.core.meta.common.AttributeTypeIdentifier;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.core.meta.common.TypeInstanceIdentifier;
import com.ptc.jca.mvc.LayoutComponentComparator;
import com.ptc.windchill.csm.client.utils.CSMUtils;

public class UpdateAllPartSpec implements RemoteAccess{
   
	public static String IBA_NAME1 ="specification";
    public static void main(String[] args) {

        RemoteMethodServer rms = RemoteMethodServer.getDefault();
        GatewayAuthenticator auth = new GatewayAuthenticator();
        auth.setRemoteUser("wcadmin");
        rms.setAuthenticator(auth);

        if(args.length<2){
            System.out.println("Example Usage:" + 
                    "java com.catl.common.toolbox.data.UpdateAllPartSpec");
        }
        System.out.println();
        invokeRemoteLoad();
    }

    public static void invokeRemoteLoad(){
        String method = "doLoad";
        String CLASSNAME= UpdateAllPartSpec.class.getName();
        Class[] types = {};
        Object[] values={};
        try {
            RemoteMethodServer.getDefault().invoke(method, CLASSNAME, null, types, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void doLoad(){
    	//FileWriter file=null;
        try {
           // file = new FileWriter("/data/UpdatePartSpec.txt");
            
          // BufferedWriter writer = new BufferedWriter(file);
           QuerySpec qs = new QuerySpec(WTPart.class);
           qs.setQueryLimit(-1);
           QueryResult qr = PersistenceHelper.manager.find(qs);
           LatestConfigSpec lcs = new LatestConfigSpec();
           qr = lcs.process(qr);
           System.out.println("Update All Part Spec size:\t"+qr.size());
           while(qr.hasMoreElements()){
        	   WTPart part = (WTPart) qr.nextElement();
        	   if(WorkInProgressHelper.isCheckedOut(part)){
        		   continue;
        	   }
        	   String number = part.getNumber();
        	   System.out.println("PartNumber:\t"+number);
        	   if(!(number.startsWith("550")&& (number.indexOf("-M")>0))){
        		   String spec = getAllAttr(part);
        		   if(StringUtils.isNotBlank(spec)){
        			   IBAUtility iba = new IBAUtility(part);
        			   String oldSpec = iba.getIBAValue(IBA_NAME1);
        			   System.out.println(" update "+part.getNumber()+",oldSpec="+oldSpec+"\nto new value="+spec);
        			   //writer.write(" update "+part.getNumber()+",value="+spec+"\n");
           	
        			   iba.setIBAValue(IBA_NAME1, spec);
        			   iba.updateAttributeContainer(part);
        			   iba.updateIBAHolder(part);
        			   //System.out.println(" update "+part.getNumber()+",oldSpec="+oldSpec+"\nto new value="+spec+" success");
        			   //writer.write(" update "+part.getNumber()+",oldSpec="+oldSpec+"\nto new value="+spec+" success\n");
        		   }
        	   }
           }
            //writer.write(" update "+dPart.getNumber()+",value="+spec+" success\n");
           
            //writer.flush();
           // writer.close();
            System.out.println("UpdateAllPartSpec 更新结束");
        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static boolean isEmpty(String str){
        if(str == null || str.trim().equals("") || str.equals("null"))
            return true;
        return false;
    }

    public static String getAllAttr(WTPart part) throws WTException{
    	
		IBAUtility iba = new IBAUtility(part);
		if(StringUtils.isBlank(iba.getIBAValue("cls"))){
			return null;
		}
		LWCStructEnumAttTemplate lwc=ClassificationUtil.getLWCStructEnumAttTemplateByName(iba.getIBAValue("cls"));
		if(lwc==null){
			return null;
		}
		String specification = "";
		StringBuffer sb = new StringBuffer();
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
					System.out.println("GourpName\t"+layGrp.getName());
					String groupDisplayName = PropertyHolderHelper.getDisplayName(layGrp, SessionHelper.getLocale());
                    System.out.println(groupDisplayName);
					if(groupDisplayName.equals("衍生PN")){
                    	continue;
                    }
                    if(groupDisplayName.equals("母PN")){
                    	continue;
                    }
			
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
							//System.out.println(displayName+"\t"+name);
							String value = iba.getIBAValue(name);
							//System.out.println(specification);
							
							sb.append("_").append(displayName).append(":").append(value==null?"":value);
						}
					}
				}
			}
		}
		specification = sb.toString();
		System.out.println("specification\t"+specification);
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

}
