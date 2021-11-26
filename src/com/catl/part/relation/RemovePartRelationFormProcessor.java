package com.catl.part.relation;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.apache.log4j.Logger;

import wt.doc.WTDocument;
import wt.log4j.LogR;
import wt.org.WTPrincipal;
import wt.org.WTPrincipalReference;
import wt.part.WTPart;
import wt.project.Role;
import wt.session.SessionHelper;
import wt.team.Team;
import wt.team.TeamException;
import wt.team.TeamHelper;
import wt.util.WTException;

import com.catl.common.constant.RoleName;
import com.mks.api.Session;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.netmarkets.util.misc.NmContext;

public class RemovePartRelationFormProcessor extends DefaultObjectFormProcessor
{

	private static final Logger log = LogR.getLogger(RemovePartRelationFormProcessor.class.getName());

	public FormResult doOperation(NmCommandBean nmcommandbean, List<ObjectBean> objectBeans) throws WTException
	{
		FormResult result = super.doOperation(nmcommandbean, objectBeans);
		PartRelationUtil partUtil = new PartRelationUtil();
		// get current business object
		WTPart part = (WTPart) nmcommandbean.getElementOid().getRefObject();
		// get selected obj
		List<NmContext> selected = nmcommandbean.getSelected();
		List<WTDocument> docList = new ArrayList<WTDocument>();
		if (selected != null)
		{
			for (int i = 0; i < selected.size(); i++)
			{
				WTDocument doc = (WTDocument) (selected.get(0)).getTargetOid().getRefObject();
				docList.add(doc);
			}
			try
			{
				partUtil.removeRelatePartDoc(part, docList);
			} catch (RemoteException e)
			{
				e.printStackTrace();
			}
		}

		result = new FormResult(FormProcessingStatus.SUCCESS);
		return result;
	}

}
