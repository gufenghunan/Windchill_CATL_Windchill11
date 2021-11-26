package com.catl.bom.cad;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.log4j.Logger;

import wt.epm.EPMDocument;
import wt.epm.EPMDocumentMaster;
import wt.epm.structure.EPMMemberLink;
import wt.epm.structure.EPMStructureHelper;
import wt.epm.workspaces.EPMWorkspace;
import wt.fc.QueryResult;
import wt.inf.container.WTContainerHelper;
import wt.org.WTGroup;
import wt.org.WTPrincipal;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.part.WTPartMaster;
import wt.part.WTPartUsageLink;
import wt.pom.UniquenessException;
import wt.query.QuerySpec;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.util.WTMessage;
import wt.util.WTPropertyVetoException;
import wt.vc.VersionControlException;

import com.catl.common.constant.Ranking;
import com.catl.common.util.GenericUtil;
import com.catl.common.util.PartUtil;
import com.ptc.windchill.uwgm.common.autoassociate.AutoAssociatePartFinderCreator;
import com.ptc.windchill.uwgm.common.autoassociate.DefaultAutoAssociatePartFinderCreator;

public class CatlFinderCreator extends DefaultAutoAssociatePartFinderCreator
		implements AutoAssociatePartFinderCreator {
	private static Logger log=Logger.getLogger(CatlFinderCreator.class.getName());
	public boolean isIsNewPart() {

             log.debug("Invoked CustomizedAutoAssociatePartFinderCreator:: isIsNewPart()");
		return super.isIsNewPart();
	}

	public void setIsNewPart(boolean a_IsNewPart)
			throws WTPropertyVetoException {
		log.debug("Invoked CustomizedAutoAssociatePartFinderCreator:: setIsNewPart()");   
		super.setIsNewPart(a_IsNewPart);
	}

	public WTPart findOrCreateWTPart(EPMDocument epmDoc, EPMWorkspace workspace)
	        throws WTException, WTPropertyVetoException,
	        VersionControlException, UniquenessException {
	    log.debug("Invoked CustomizedAutoAssociatePartFinderCreator:: findOrCreateWTPart()");

	    String epmnumber=epmDoc.getNumber();
	    log.debug("file name="+epmnumber+", '-' counts="+checkString(epmnumber));
	    if(checkString(epmnumber)<2)
	    {
	        if(epmnumber.indexOf(".") >0 )	
	        {
	            epmnumber=epmnumber.substring(0,epmnumber.indexOf("."));
	        }
	        WTPart part=PartUtil.getLastestWTPartByNumber(epmnumber);
	        if(null==part)
	        {
	            throw new WTException("相应的零部件:"+epmnumber+",不存在!");
	        }else
	        {
	        	/*String ranking = (String)GenericUtil.getObjectAttributeValue(part.getMaster(), "ranking");
	            if(ranking != null){
	            	if(ranking.equals(Ranking.PROHIBIT_PURCHASE)){
	            		throw new WTException("不允许调用‘"+Ranking.PROHIBIT_PURCHASE+"’的物料PN："+part.getNumber());
	            	}else if(ranking.equals(Ranking.DISABLED)){
	            		throw new WTException("不允许调用‘"+Ranking.DISABLED+"’的物料PN："+part.getNumber());
	            	}
	            }*/
	            //check epmdocument child model qunlity is equalwith bom child part and unit  must be pcs
	            WTPrincipal user =SessionHelper.manager.getPrincipal();
	            log.debug("user name=="+user.getName());
                if (isDmsGroup(user)) {
	                StringBuffer message = compareBOM(part, epmDoc);
	                if (message.length() > 0)
	                {
	                    throw new WTException("错误信息：" + message);
	                }
	            }else{
	                if(!isSiteAdmin(user)){
	                    //for normal user, need check part state
    	                String state=part.getState().toString();
                        if (!state.equalsIgnoreCase("DESIGN") && !state.equalsIgnoreCase("DESIGNMODIFICATION"))
    	                {
    	                    throw new WTException("零件：" + epmnumber + "的生命周期状态不符合业务规范!");
    	                }
	               }//if user 
	            }
	        }
	    }//if it is not virtual part 
	    
	    
	    return super.findOrCreateWTPart(epmDoc, workspace);
	}

	
	public static boolean isDmsGroup(WTPrincipal user){
	    boolean isDms=false;
	    if(user.getName().equals("dms")){
	        return true;
	    }
	    String[] services;
        try {
            services = wt.org.OrganizationServicesHelper.manager.getDirectoryServiceNames();
           
            wt.org.DirectoryContextProvider dc_provider = wt.org.OrganizationServicesHelper.manager.newDirectoryContextProvider(services, null);
            Enumeration groups = wt.org.OrganizationServicesHelper.manager.getGroups("dms", dc_provider);
            while(groups.hasMoreElements()){
                WTGroup   group = (WTGroup) groups.nextElement();
                Enumeration users = group.members();
                while(users.hasMoreElements()){
                    WTUser u = (WTUser)users.nextElement();
                    if(u.getName().equals(user.getName())){
                        log.debug(u.getName()  + " is dms group");
                        isDms=true;
                    }
                }
            }
           
        } catch (WTException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
	   
	    
	    return isDms;
	    
	}
	
	public static StringBuffer compareBOM(WTPart part,EPMDocument epmDocument)
	{
		StringBuffer message=new StringBuffer();
		ArrayList<String[]> bomlist=new ArrayList<String[]>();
		ArrayList<epmModel> epmlist=new ArrayList<epmModel>();
		try {
			getBomchildpart(part, bomlist);
			getEpmchild(epmDocument, epmlist);
		} catch (Exception e) {
			log.debug("get child list failed ->"+part.getNumber());
			e.printStackTrace();
		}
		log.debug("bomlist==="+bomlist.size());
		log.debug("epmlist===="+epmlist.size());
		for (int i = 0; i < epmlist.size(); i++) {
			epmModel epmmodel=epmlist.get(i);
			boolean inBom = false;
			for (int j = 0; j < bomlist.size(); j++) {
				String[] bom=bomlist.get(j);
				log.debug("bom number==="+bom[0].toString());
				log.debug("epm number====="+epmmodel.getNumber().toString());
				log.debug("bom quantity==="+bom[1].toString());
				log.debug("epm quantity====="+epmmodel.getAmount().toString());
				log.debug("bom unit==="+bom[2].toString());
				if (bom[0].toString().equalsIgnoreCase(epmmodel.getNumber().toString())) {
					inBom = true;
				     if(!bom[1].toString().equalsIgnoreCase(epmmodel.getAmount().toString()))
				     {
				    	 message.append("模型:"+epmmodel.getNumber().toString()+"数量与bom中子件的数量不相同 \n");
				     }
				     if (!bom[2].toString().equalsIgnoreCase("ea")) {
					     message.append("BOM中子件:"+epmmodel.getNumber().toString()+"的单位不为pcs \n");	
					}
				}
			}
			if(checkString(epmmodel.getNumber())<2 && !inBom){
				message.append(WTMessage.formatLocalizedMessage("模型[{0}]对应的物料在BOM中不存在  \n", new Object[]{epmmodel.getNumber()}));
			}
		}
         return message;
	}
	
    public static boolean isSiteAdmin(WTPrincipal wtPrincipal) {
        try {
            return WTContainerHelper.service.isAdministrator(WTContainerHelper.service.getExchangeRef(), wtPrincipal);
        } catch (WTException e) {
            e.printStackTrace();
        }
        return false;
    }
	public static void getBomchildpart(WTPart part,ArrayList<String[]> childlist)throws Exception
	{

		log.debug("part number===="+part.getNumber());
		QueryResult qr =new QueryResult();
		 qr= WTPartHelper.service.getUsesWTPartMasters(part);
		log.debug("get uses wtpart master size==="+qr.size());
		while(qr.hasMoreElements()){
			WTPartUsageLink link = (WTPartUsageLink)qr.nextElement();			
			WTPartMaster master = link.getUses();
			WTPart childpart = PartUtil.getLastestWTPartByNumber(master.getNumber());
			//String unit = childpart.getDefaultUnit().toString();
			String unit = link.getQuantity().getUnit().toString();
			String quantity=String.valueOf(link.getQuantity().getAmount());
			String amount=quantity.substring(0,quantity.indexOf("."));
			String[] numberq={master.getNumber(),amount,unit};


			childlist.add(numberq);
		}
	}
	public static void getEpmchild(EPMDocument epm,ArrayList<epmModel> epmlist)throws Exception
	{
		QuerySpec qs = new QuerySpec(EPMMemberLink.class);
        QueryResult epmQr = EPMStructureHelper.service.navigateUses(epm, qs, false);
        log.debug("epmqr 1===="+epmQr.size());
        HashSet epmset=getHashSet(epmQr);
        log.debug("epm set ==="+epmset.size());
        
        Iterator iterator=epmset.iterator();
        log.debug("epmresult size==="+epmQr.size());
		while(iterator.hasNext()){
			String epmnumber=(String) iterator.next();
			epmModel model =new epmModel();
			model.setNumber(epmnumber);
			int count=0;
			log.debug("epm number=="+epmnumber);
			QueryResult epmQr2 = EPMStructureHelper.service.navigateUses(epm, qs, false);
			while(epmQr2.hasMoreElements()){
	        	EPMMemberLink link2 = (EPMMemberLink) epmQr2.nextElement();
				EPMDocumentMaster epmMaster2 = (EPMDocumentMaster) link2.getUses();
				String epmnumber2=epmMaster2.getNumber();
				
				if(epmnumber2.indexOf(".") >0 )	
				{
				epmnumber2=epmnumber2.substring(0,epmnumber2.indexOf("."));
				}
				if (epmnumber.equalsIgnoreCase(epmnumber2)) {
					count++;
				}
				
			}

			model.setAmount(String.valueOf(count));
			epmlist.add(model);
			
		}
	     log.debug("epm list ===="+epmlist.size());
	}
	
	public static HashSet getHashSet(QueryResult dataQueryResult)
	{
		HashSet dataSet =new HashSet();
		while (dataQueryResult.hasMoreElements()) {
			EPMMemberLink link = (EPMMemberLink) dataQueryResult.nextElement();
			EPMDocumentMaster epmMaster = (EPMDocumentMaster)link.getUses();
			String epmnumber2 = epmMaster.getNumber();
			if(epmnumber2.indexOf(".") >0 )	
			{
			epmnumber2=epmnumber2.substring(0,epmnumber2.indexOf("."));
			}
			dataSet.add(epmnumber2);
		}
		return dataSet;
	}

	public static int checkString(String epmnumber)
	{
		String str="-";
		int count=0;
		int start=0;
		while (epmnumber.indexOf(str,start)>=0&&start<epmnumber.length()) {
			count++;
			start=epmnumber.indexOf(str, start)+str.length();			
		}
		return count;	
	}
	public WTPart findWTPart(EPMDocument epmDoc) throws WTException {
		System.out
				.println("Invoked CustomizedAutoAssociatePartFinderCreator:: findWTPart()");
		return super.findWTPart(epmDoc);
	}

	
}