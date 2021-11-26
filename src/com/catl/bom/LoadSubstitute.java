package com.catl.bom;

import java.util.Hashtable;
import java.util.Vector;

import org.apache.log4j.Logger;

import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.load.LoadServerHelper;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.part.WTPartMaster;
import wt.part.WTPartSubstituteLink;
import wt.part.WTPartUsageLink;
import wt.pom.Transaction;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTException;
import wt.vc.config.LatestConfigSpec;

public class LoadSubstitute {
	
	
	

	    public static final String CLASSNAME = LoadSubstitute.class.getName();
	    public static final Logger LOGGER = Logger.getLogger(CLASSNAME);
	 
	    public static boolean addSubstituteToAssembly(Hashtable nv, Hashtable cmd_line, Vector v) {
	    	 LOGGER.debug("load sbustitute");
	    	
	    	
	        boolean flag = false;
	        Transaction trs = null;
	        try {
	            trs = new Transaction();
	            trs.start();
	            String parentPartNumber = LoadServerHelper.getValue("assemblyPartNumber", nv, cmd_line, LoadServerHelper.REQUIRED);
	            String childPartNumber = LoadServerHelper.getValue("childPartNumber", nv, cmd_line, LoadServerHelper.REQUIRED);
	            String substitutePartNumber = LoadServerHelper.getValue("substitutePartNumber", nv, cmd_line, LoadServerHelper.REQUIRED);
	            WTPart parentPart = findLatestWTPart(parentPartNumber);
	            if(parentPart==null){
	            	LOGGER.debug("cannot find WTPart given number =" + parentPartNumber);
	            }
	            WTPartMaster substitutePartMaster = findWTPartMaster(substitutePartNumber);
	            if(substitutePartMaster==null){
	            	LOGGER.debug("cannot find WTPartMaster given number =" + substitutePartNumber);	            	
	            }
	            
	            QueryResult qr = WTPartHelper.service.getUsesWTPartMasters(parentPart);
	            LOGGER.debug("link size =" + qr.size());
	    		while(qr.hasMoreElements()){
	    			WTPartUsageLink link = (WTPartUsageLink)qr.nextElement();
	    			WTPartMaster childPartMaster = link.getUses();
	    			LOGGER.debug("childPartMaster number =" + childPartMaster.getNumber());
	    			if(childPartMaster.getNumber().equals(childPartNumber)){
	    				WTPartSubstituteLink subLink = WTPartSubstituteLink.newWTPartSubstituteLink(link, substitutePartMaster);
	    				PersistenceHelper.manager.save(subLink);	
	    				  flag =true;
	    			}
	    		}
	          
	            trs.commit();
	            trs = null;
	           
	        }  catch (WTException e) {
	            LOGGER.error("Error: Import fail, please check the file.\r\n" + e.getMessage(), e);
	        } finally {
	            if (trs != null) {
	                trs.rollback();
	            }
	        }
	        return flag;
	    }
	    
	    public static WTPart findLatestWTPart(String number) 
	    {
	        
		    WTPart thePart = null;
	        QuerySpec qs = null;
	        QueryResult qr = null;
	        SearchCondition sc = null;
	        try {
				qs = new QuerySpec(WTPart.class);
			    sc = new SearchCondition(WTPart.class, WTPart.NUMBER, SearchCondition.EQUAL, number);
		        qs.appendWhere(sc, new int[]{});
		        qr = PersistenceHelper.manager.find(qs);
		        qr = (new LatestConfigSpec()).process(qr);
		        if (qr.hasMoreElements())
		        {
		            thePart = (WTPart) qr.nextElement();
		        }
	
			} catch (WTException  e) {
				
				e.printStackTrace();
			}
	    
	     
	
	        return thePart;
	    }
    
	    public static WTPartMaster findWTPartMaster(String number) 
	    {
	        
		    WTPartMaster master = null;
	        QuerySpec qs = null;
	        QueryResult qr = null;
	        SearchCondition sc = null;
	        try {
				qs = new QuerySpec(WTPartMaster.class);
			    sc = new SearchCondition(WTPartMaster.class, WTPartMaster.NUMBER, SearchCondition.EQUAL, number);
		        qs.appendWhere(sc, new int[]{});
		        qr = PersistenceHelper.manager.find(qs);
		        if (qr.hasMoreElements())
		        {
		            master = (WTPartMaster) qr.nextElement();
		        }
	
			} catch (WTException  e) {
				
				e.printStackTrace();
			}
	    
	     
	
	        return master;
	    }
	    
	    

}
