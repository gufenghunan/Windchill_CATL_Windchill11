package com.catl.change.inventory.processor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import wt.change2.WTChangeActivity2;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.vc.config.LatestConfigSpec;

import com.catl.change.inventory.ECAPartLink;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;

/**
 * 
 * @author Administrator 2014-6-23
 * 
 * @Description
 */
public class CatlChangeInventoryFormProcessor extends DefaultObjectFormProcessor{//CreateChangeTaskFormProcessor {
	public CatlChangeInventoryFormProcessor() {
	}

	public FormResult doOperation(NmCommandBean nmcommandbean, List list)
			throws WTException {
		FormResult formResult = new FormResult(FormProcessingStatus.SUCCESS);
//		WTUser currentUser = (WTUser) SessionHelper.manager.getPrincipal();
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		try{
//			SessionHelper.manager.setAdministrator();
			NmOid nmoid = nmcommandbean.getActionOid();
			Object obj = nmoid.getRef();
			//if (obj instanceof WTChangeActivity2) {
				WTChangeActivity2 eca = (WTChangeActivity2) obj;
				List<ObjectBean> addlist = new ArrayList<ObjectBean>();
				List<ECAPartLink> deletelist = new ArrayList<ECAPartLink>();
				List<ObjectBean> updatelist = new ArrayList<ObjectBean>();
				for (Object object : list) {
					if (object instanceof ObjectBean) {
						ObjectBean bean = (ObjectBean) object;
						if (bean.getObjectHandle().startsWith("newRowObj")) {
							addlist.add(bean);
						} else if(bean.getObjectHandle().contains("ECAPartLink")){
							updatelist.add(bean);
						}
					}
				}
				QueryResult qr = PersistenceHelper.manager.navigate(eca,
						ECAPartLink.PART_ROLE,
						ECAPartLink.class, false);
				qr = new LatestConfigSpec().process(qr);
				while (qr.hasMoreElements()) {
					ECAPartLink ECAPartLink = (ECAPartLink) qr.nextElement();
					ReferenceFactory factory = new ReferenceFactory();
					String oid = factory.getReferenceString(ECAPartLink);
					for (int i = 0; i < updatelist.size(); i++) {
						ObjectBean bean = updatelist.get(i);
						if (bean.getObjectHandle().equals(oid)) {
							break;
						}
						if (i == updatelist.size() - 1) {
							deletelist.add(ECAPartLink);
						}
					}
					if(updatelist.size() == 0){
						deletelist.add(ECAPartLink);
					}
				}
				for (ECAPartLink ECAPartLink : deletelist) {
					//delete(ECAPartLink);
				}
				for (ObjectBean bean : updatelist) {
					String oid = bean.getObjectHandle();
					ReferenceFactory factory = new ReferenceFactory();
					ECAPartLink ECAPartLink = (ECAPartLink) factory.getReference(oid)
							.getObject();
					Map map = bean.getText();
//					try {
//						//update(ECAPartLink, map);
//					} catch (WTPropertyVetoException e) {
//						e.printStackTrace();
//					}
				}
				for (ObjectBean bean : addlist) {
					Map map = bean.getText();
//					try {
//						//ECAPartLink ECAPartLink = createECAPartLink(doc, map);
//						//createECAPartLinkLink(doc, ECAPartLink);
//					} catch (WTPropertyVetoException e) {
//						e.printStackTrace();
//					}
				}
			//}
		} finally {
//			SessionHelper.manager.setPrincipal(currentUser.getName());
			SessionServerHelper.manager.setAccessEnforced(enforce);
		}
		return formResult;
	}


}
