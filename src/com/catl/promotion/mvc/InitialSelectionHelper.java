/* bcwti
*
* Copyright (c) 2014 PTC, Inc. All Rights Reserved.
*
* This software is the confidential and proprietary information of PTC
* and is subject to the terms of a software license agreement. You shall
* not disclose such confidential information and shall use it only in accordance
* with the terms of the license agreement.
*
* ecwti
*/
package com.catl.promotion.mvc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import wt.fc.ReferenceFactory;
import wt.fc.WTReference;
import wt.fc.collections.CollectionsHelper;
import wt.fc.collections.WTArrayList;

import com.ptc.core.components.descriptor.DescriptorConstants;
import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.mvc.client.feedback.ClientFeedback;
import com.ptc.mvc.client.feedback.DefaultClientComponentFeedback;

/**
* This is a helper class to construct a feedback message for the initialRows
* hidden field.
* 
 * <BR>
* <B>Supported API: </B>false <BR>
* <BR>
* <B>Extendable: </B>false
* 
 */
public class InitialSelectionHelper {
    
    public static final String INITIAL_SELECTION_PLUGIN = "initiallySelectedPlugin";
    
    /**
     * Gets a feedback message.
     * <B>Supported API: </B>false <BR>
     * 
     * @param processor
     * @param objects
     * @throws Exception
     */
    public static List<ClientFeedback> getFeedbackMessage(JcaComponentParams params, List<Object> objects) throws Exception {
        List<ClientFeedback> feedbackList = new ArrayList<ClientFeedback>();
        Map<String, Serializable> data = new HashMap<String, Serializable>();
        data.put("initiallySelected", getOidsStr(params, objects));
        data.put("tableId", params.getDescriptor().getId());
        ClientFeedback feedback = new DefaultClientComponentFeedback(599, null, null, data);
        feedbackList.add(feedback);
        return feedbackList;
    }
    
    /**
     * Returns a comma separated list of oids.
     * 
     * <B>Supported API: </B>false <BR>
     * 
     * @param objects
     * @return
     */
    public static String getOidsStr(JcaComponentParams params, List<Object> objects) throws Exception  {
        StringBuilder sb = new StringBuilder();
        
        WTArrayList collection = new WTArrayList(objects.size(), CollectionsHelper.VERSION_FOREIGN_KEY);
        Object type = params.getDescriptor().getProperty(DescriptorConstants.TableProperties.REFERENCE_TYPE);
        if(type != null && type instanceof String && ((String)type).equals("OR")) {
            collection = new WTArrayList(CollectionsHelper.OBJECT_IDENTIFIER); 
        }
        collection.addAll(objects);
        
        ReferenceFactory rf = new ReferenceFactory();
        for(int i=0; i<collection.size(); i++) {
            WTReference ref = (WTReference)collection.get(i);
            sb.append(rf.getReferenceString(ref));
            if(i+1 != objects.size()) {
                sb.append("#");
            }
        }
        return sb.toString();
    }
    
    /**
     * Returns a string Tags can use to include the JS.
     * 
     * <B>Supported API: </B>false <BR>
     */
    public static String getIncludePluginJS() {
        return "<script src=\"netmarkets/javascript/table/initiallySelectedPlugin.js\"></script>\n";
    }

}


