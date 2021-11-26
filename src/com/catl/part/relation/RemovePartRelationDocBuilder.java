package com.catl.part.relation;

import java.util.ArrayList;
import java.util.List;

import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.fc.WTReference;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.vc.VersionControlHelper;

import com.ptc.mvc.components.AbstractComponentBuilder;
import com.ptc.mvc.components.ColumnConfig;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentConfigFactory;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.TableConfig;
import com.ptc.mvc.util.ClientMessageSource;

@ComponentBuilder("com.catl.part.relation.RemovePartRelationDocBuilder")
public class RemovePartRelationDocBuilder extends AbstractComponentBuilder
{

	private ClientMessageSource msgSource;

	@Override
	public Object buildComponentData(ComponentConfig arg0, ComponentParams params) throws Exception
	{
		String oid = (String) params.getParameter("oid");
		List<WTDocument> docList = new ArrayList<WTDocument>();
		ReferenceFactory rf = new ReferenceFactory();
		WTReference objRef = rf.getReference(oid);
		if (objRef != null && objRef.getObject() instanceof WTPart)
		{
			WTPart part = (WTPart) objRef.getObject();
			List<WTDocument> refDocList = getRefDocByPart(part);
			List<WTDocument> desDocList = getDescDocsByPart(part);
			// get reference Doc
			if (refDocList != null)
			{
				docList.addAll(refDocList);
			}
			// get describe Doc
			if (desDocList != null)
			{
				docList.addAll(desDocList);
			}
		}
		return docList;
	}

	@Override
	public ComponentConfig buildComponentConfig(ComponentParams arg0) throws WTException
	{
		ComponentConfigFactory factory = getComponentConfigFactory();
		TableConfig config = factory.newTableConfig();

		config.setConfigurable(false);
		config.setType("wt.fc.WTObject");
		config.setLabel("RelatedDocument");
		config.setSelectable(true);

		ColumnConfig oidColumn = factory.newColumnConfig("oid", true);
		config.addComponent(oidColumn);
		oidColumn.setHidden(true);
		oidColumn.setSortable(true);

		ColumnConfig iconColumn = factory.newColumnConfig("type_icon", true);
		config.addComponent(iconColumn);

		Object numberColumn = factory.newColumnConfig("number", true);
		config.addComponent((ComponentConfig) numberColumn);

		Object versionColumn = factory.newColumnConfig("version", true);
		config.addComponent((ComponentConfig) versionColumn);

		ColumnConfig infoPageColumn = factory.newColumnConfig("infoPageAction", false);
		infoPageColumn.setDataUtilityId("infoPageAction");
		config.addComponent(infoPageColumn);

		ColumnConfig nameColumn = factory.newColumnConfig("name", true);
		nameColumn.setLabel("NAME");
		config.addComponent(nameColumn);

		return config;
	}

	/**
	 * 
	 * Get RefDocMasters By Part.
	 * 
	 * @param part
	 *            -- WTPart : part object
	 * @return List<WTDocumentMaster> : all refDocMaster
	 * @throws WTException
	 *             : exception
	 * 
	 * 
	 */
	public List<WTDocument> getRefDocByPart(WTPart part) throws WTException
	{
		ArrayList<WTDocument> results = new ArrayList<WTDocument>();
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		try
		{
			QueryResult queryResult = WTPartHelper.service.getReferencesWTDocumentMasters(part);
			while (queryResult.hasMoreElements())
			{
				Object tempObj = queryResult.nextElement();
				if (tempObj instanceof WTDocumentMaster)
				{
					WTDocumentMaster docMaster = (WTDocumentMaster) tempObj;
					WTDocument doc = null;
					QueryResult qr2 = VersionControlHelper.service.allIterationsOf(docMaster);
					if (qr2.hasMoreElements())
					{
						doc = (WTDocument) qr2.nextElement();
					}
					if (doc != null)
					{
						results.add(doc);
					}
				}
			}
		} finally
		{
			SessionServerHelper.manager.setAccessEnforced(enforce);
		}
		return results;
	}

	/**
	 * 
	 * Get DescDocs By Part.
	 * 
	 * @param part
	 *            -- WTPart : part object
	 * @return List<WTDocument> : descDocs
	 * @throws WTException
	 *             : exception
	 * 
	 * 
	 */
	public List<WTDocument> getDescDocsByPart(WTPart part) throws WTException
	{
		ArrayList<WTDocument> results = new ArrayList<WTDocument>();
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		try
		{
			QueryResult queryResult = WTPartHelper.service.getDescribedByWTDocuments(part);
			while (queryResult.hasMoreElements())
			{
				Object tempObj = queryResult.nextElement();
				if (tempObj instanceof WTDocument)
				{
					results.add((WTDocument) tempObj);
				}
			}
		} finally
		{
			SessionServerHelper.manager.setAccessEnforced(enforce);
		}
		return results;
	}

}
