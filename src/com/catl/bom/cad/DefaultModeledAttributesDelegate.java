/* bcwti
 *
 * Copyright (c) 2010 Parametric Technology Corporation (PTC). All Rights Reserved.
 *
 * This software is the confidential and proprietary information of PTC
 * and is subject to the terms of a software license agreement. You shall
 * not disclose such confidential information and shall use it only in accordance
 * with the terms of the license agreement.
 *
 * ecwti
 */
package com.catl.bom.cad;

/*
20-Sep-04 WNCNDM-X05-41  SHH  $$1  Created
18-May-05 WNCNDM-X05-M010-7 pkalra $$2 added getECNForCADDoc, SPR 1139557
27-May-05 WNCNDM-X05-M010-8 pkalra $$3 fixed getECNForCADDoc, SPR 1147495
13-Nov-12 X-20 M050         shavale  SPR 2154061
06-Aug-15 X-26              shavale  SPR 2845535 
*/

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import com.catl.common.constant.PartState;
import com.catl.common.util.ElecSignConstant;
import com.catl.common.util.EpmUtil;
import com.catl.ecad.utils.CommonUtil;
import com.ptc.windchill.uwgm.proesrv.c11n.ModeledAttributesDelegate;

import wt.epm.EPMDocument;
import wt.util.WTException;
import wt.vc.VersionControlHelper;

public class DefaultModeledAttributesDelegate implements ModeledAttributesDelegate {
	private static HashMap ModeledAttrList = new HashMap();

	public DefaultModeledAttributesDelegate() {
	}

	// getAvailableAttributes() returns
	// HashMap<String, Object> which contains
	// HashMap<Attribute name, Attribute type>
	public HashMap getAvailableAttributes() {
		return ModeledAttrList;
	}

	// getModeledAttributes(Collection docs) returns
	// HashMap<input object, HashMap<Attribute name, Attribute value>>
	public HashMap getModeledAttributes(Collection docs) throws WTException {
		HashMap result = new HashMap();
		for (Iterator iter = docs.iterator(); iter.hasNext();) {
			HashMap attrs = new HashMap();
			EPMDocument cadDoc = (EPMDocument) iter.next();
			if(EpmUtil.isCATDrawing(cadDoc)){
				String number=cadDoc.getNumber().split("\\.")[0];
				attrs.put(ElecSignConstant.PTC_WM_PN, number);
				attrs.put("PTC_WM_REVISION", VersionControlHelper.getIterationDisplayIdentifier(cadDoc).toString());
				
				if(cadDoc.getVersionIdentifier().getValue().equalsIgnoreCase("A")){
					attrs.put(ElecSignConstant.PTC_WM_ECN_NO, "—");						
				}else{
					try {
						//如果当前版本不是发布状态，则取其前一个发布版本
						if(!cadDoc.getLifeCycleState().toString().equals(PartState.RELEASED)){
							EPMDocument preEPM = (EPMDocument) CommonUtil.getPreVersionObject(cadDoc);
							if(preEPM!=null){
								if(VersionControlHelper.getIterationIdentifier(cadDoc).getValue().equals("1")){
									attrs.put("PTC_WM_REVISION", VersionControlHelper.getIterationDisplayIdentifier(preEPM).toString());
								}
								if(preEPM.getVersionIdentifier().getValue().equalsIgnoreCase("A")){
									attrs.put(ElecSignConstant.PTC_WM_ECN_NO, "—");						
								}
							}						
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
			}else if(EpmUtil.isCAT3D(cadDoc)){
				attrs.put("CAT:REVISION", VersionControlHelper.getIterationDisplayIdentifier(cadDoc).toString());
			}	
		
			// end
			result.put(cadDoc, attrs);
		}

		return result;
	}

} // end class
