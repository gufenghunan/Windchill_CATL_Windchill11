package com.catl.part.datautilities;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import wt.part.Source;
import wt.part.WTPart;
import wt.session.SessionHelper;
import wt.util.WTException;

import com.catl.common.util.GenericUtil;
import com.catl.common.util.TypeUtil;
import com.ptc.core.components.descriptor.ModelContext;
import com.ptc.core.components.factory.AbstractDataUtility;
import com.ptc.core.components.rendering.guicomponents.ComboBox;
import com.ptc.core.components.rendering.guicomponents.UrlDisplayComponent;
import com.ptc.core.components.util.OidHelper;
import com.ptc.netmarkets.util.beans.NmURLFactoryBean;
import com.ptc.netmarkets.util.misc.NetmarketURL;

public class PartBuilderDataUtility extends AbstractDataUtility {

	@Override
	public Object getDataValue(String componentId, Object obj, ModelContext mc)
			throws WTException {
		if(obj instanceof WTPart){
			WTPart part = (WTPart)obj;
			if(StringUtils.equals(componentId, "number")){
				NmURLFactoryBean urlFactoryBean = new NmURLFactoryBean();
				urlFactoryBean.setRequestURI(NetmarketURL.BASEURL);
				String url = NetmarketURL.buildURL(urlFactoryBean, "object", "view", OidHelper.getNmOid(part));
				
				String number = part.getNumber();
				UrlDisplayComponent objUrlComp = new UrlDisplayComponent();
	            objUrlComp.setLabel(number);
	            objUrlComp.setLabelForTheLink(number);
	            objUrlComp.setLink(url);
	            objUrlComp.setTarget("_blank");
	            return objUrlComp;
			}
			else if(StringUtils.equals(componentId, "source")){
				try {
					System.out.println("Session Locale:"+SessionHelper.manager.getLocale());
					String type = TypeUtil.getTypeInternalName(part);
					Set<String> sources = GenericUtil.getDiscreteSetVaules(type, "source");
					ArrayList<String> displayList = new ArrayList<String>();
			        ArrayList<String> internalList = new ArrayList<String>();
					for (String key : sources) {
						Source s = Source.toSource(key);
						
						if(s.isSelectable()){
							
							displayList.add(Source.toSource(key).getDisplay(SessionHelper.manager.getLocale()));
							internalList.add(key);
						}
					}
					ComboBox comboBox = new ComboBox();
					comboBox.setId(part.toString());
					comboBox.setName(part.toString());
					comboBox.setInternalValues(internalList);
		            comboBox.setValues(displayList);
		            comboBox.setSelected(part.getSource().toString());
					return comboBox;
				} catch (RemoteException e) {
					e.printStackTrace();
					throw new WTException(e.getLocalizedMessage());
				}
			}
		}
		return null;
	}

}
