package com.catl.part.datautilities;

import java.io.PrintWriter;

import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.inf.container.WTContainerHelper;
import wt.inf.container.WTContainerRef;
import wt.inf.library.WTLibrary;
import wt.org.DirectoryContextProvider;
import wt.org.OrganizationServicesHelper;
import wt.org.WTOrganization;
import wt.org.WTPrincipal;
import wt.part.WTPart;
import wt.session.SessionHelper;
import wt.util.WTException;

import com.ptc.core.components.descriptor.ModelContext;
import com.ptc.core.components.rendering.RenderingContext;
import com.ptc.core.components.rendering.RenderingException;
import com.ptc.core.components.rendering.guicomponents.AttributeInputComponent;
import com.ptc.core.components.rendering.guicomponents.StringInputComponent;
import com.ptc.core.components.rendering.renderers.StringInputComponentRenderer;
import com.ptc.core.components.util.AttributeHelper;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.windchill.enterprise.object.dataUtilities.RenameNumberDataUtility;

public class CATLRenameNumberDataUtility extends RenameNumberDataUtility {

	static class RenameNumberStringInputComponentRender extends StringInputComponentRenderer {

		private static final long serialVersionUID = 1L;

		boolean readOnly = false;

		protected void renderObject(StringInputComponent component, PrintWriter out, RenderingContext renderContext) throws RenderingException {
			if (renderContext.isTrail()) {
				component.getTextUI().setLabel(component.getLabel());
			}
			if (readOnly) {
				component.getTextUI().setReadOnly(true);
			}
			component.getTextUI().draw(out, renderContext);
		}

		protected boolean isValidForObject(Object o) {
			return (o instanceof StringInputComponent);
		}
	}

	@Override
	public AttributeInputComponent createValueInputComponent(String component_id, Object datum, ModelContext mc) throws WTException {
		String label = getLabel(component_id, mc);
		Object value = mc.getRawValue();

		boolean isRequiredField = true;

		int maxChars = calculateMaxCharacterEntryLimit(mc);

		StringInputComponent gui = new StringInputComponent(label, maxChars, false);
		gui.setRawValue(value);
		gui.setRequired(isRequiredField);

		if (value != null) {
			String number = AttributeHelper.getStringValue(value, null, mc.getATS(false));
			gui.setValue(number);
		}

		WTPrincipal currentUser = SessionHelper.getPrincipal();
		
		boolean renameAllowed = false;
		
		// 如果是系统管理员或组织管理员则允许修改编码
		if (isSiteAdmin(currentUser) || isOrgAdministator(currentUser, "CATL")) {
			renameAllowed = true;
		}
		
		//如果是电芯库的部件，则允许修改编码
		NmCommandBean clientData = mc.getNmCommandBean();
		NmOid oid = clientData.getPageOid();
		if (oid != null && oid.isA(WTPart.class)) {
			WTPart part = (WTPart) oid.getRefObject();
			WTContainerRef containerRef = part.getContainerReference();
			renameAllowed = isElectronic(containerRef);
		}else if (oid != null && oid.isA(WTDocument.class)) {
			WTDocument doc = (WTDocument) oid.getRefObject();
			WTContainerRef containerRef = doc.getContainerReference();
			renameAllowed = isElectronic(containerRef);
		}else if (oid != null && oid.isA(EPMDocument.class)) {
			EPMDocument epm = (EPMDocument) oid.getRefObject();
			WTContainerRef containerRef = epm.getContainerReference();
			renameAllowed = isElectronic(containerRef);
		}
		
		if (renameAllowed) {
			// do nothing
		} else {
			RenameNumberStringInputComponentRender render = new RenameNumberStringInputComponentRender();
			render.readOnly = true;
			gui.setRenderer(render);
		}
		return gui;
	}

	private boolean isElectronic(WTContainerRef containerRef) {
		if (WTLibrary.class.isAssignableFrom(containerRef.getReferencedClass())) {
			WTLibrary lib = (WTLibrary) containerRef.getObject();
			if ("电芯".equals(lib.getName())) {
				return true;
			}
		}
		return false;
	}

	public static boolean isSiteAdmin(WTPrincipal wtPrincipal) {
		try {
			return WTContainerHelper.service.isAdministrator(WTContainerHelper.service.getExchangeRef(), wtPrincipal);
		} catch (WTException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean isOrgAdministator(WTPrincipal wtprincipal, String strOrgName) {
		try {
			DirectoryContextProvider dcp = WTContainerHelper.service.getExchangeContainer().getContextProvider();
			WTOrganization org = OrganizationServicesHelper.manager.getOrganization(strOrgName, dcp);
			if (org != null) {
				WTContainerRef wtcontainerref = WTContainerHelper.service.getOrgContainerRef(org);
				if (wtcontainerref != null) {
					if (WTContainerHelper.service.isAdministrator(wtcontainerref, wtprincipal)) {
						return true;
					}
				}
			} else {
				System.out.println("WTOrganization is null.");
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return false;
	}

}
