package com.catl.part;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.catl.common.util.ClassificationUtil;
import com.catl.common.util.PartUtil;
import com.ptc.core.HTMLtemplateutil.server.processors.AttributeKey;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.CreateEditFormProcessorHelper;
import com.ptc.core.components.util.AttributeHelper;
import com.ptc.core.lwc.common.AttributeTemplateFlavor;
import com.ptc.core.lwc.common.LayoutComponent;
import com.ptc.core.lwc.common.view.AttributeDefinitionReadView;
import com.ptc.core.lwc.common.view.ConstraintDefinitionReadView;
import com.ptc.core.lwc.common.view.ConstraintDefinitionReadView.RuleDataObject;
import com.ptc.core.lwc.common.view.EnumerationEntryReadView;
import com.ptc.core.lwc.common.view.GroupDefinitionReadView;
import com.ptc.core.lwc.common.view.GroupMembershipReadView;
import com.ptc.core.lwc.common.view.LayoutComponentReadView;
import com.ptc.core.lwc.common.view.LayoutDefinitionReadView;
import com.ptc.core.lwc.common.view.PropertyHolderHelper;
import com.ptc.core.lwc.common.view.SeparatorReadView;
import com.ptc.core.lwc.common.view.TypeDefinitionReadView;
import com.ptc.core.lwc.server.LWCStructEnumAttTemplate;
import com.ptc.core.lwc.server.PersistableAdapter;
import com.ptc.core.lwc.server.TypeDefinitionServiceHelper;
import com.ptc.core.meta.common.AttributeIdentifier;
import com.ptc.core.meta.common.AttributeTypeIdentifier;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.core.meta.common.TypeIdentifierHelper;
import com.ptc.jca.mvc.LayoutComponentComparator;
import com.ptc.netmarkets.util.beans.NmCommandBean;

import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.iba.definition.AttributeDefinition;
import wt.iba.definition.StringDefinition;
import wt.iba.definition.litedefinition.AttributeDefDefaultView;
import wt.iba.definition.service.IBADefViewManager;
import wt.iba.definition.service.IBADefinitionCache;
import wt.iba.value.StringValue;
import wt.log4j.LogR;
import wt.part.WTPart;
import wt.pds.StatementSpec;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.util.WTRuntimeException;

public class ValidateSpecification
{
	private static final Logger log = LogR.getLogger(ValidateSpecification.class.getName());
	Boolean foceCreate = false;
	String cls;
	String partType = "WCTYPE|wt.part.WTPart|com.CATLBattery.CATLPart";
	NmCommandBean clientData;
	private HashMap clsAttributeEnumeMap = new HashMap();
	LWCStructEnumAttTemplate clsNodeAttTemplate;

	public ValidateSpecification(NmCommandBean clientData) throws WTRuntimeException, WTException
	{
		this.clientData = clientData;
		ReferenceFactory rf = new ReferenceFactory();
		HashMap textMap = clientData.getText();
		Iterator it = textMap.entrySet().iterator();
		while (it.hasNext())
		{
			Map.Entry entry = (Map.Entry) it.next();
			Object key = entry.getKey();
			Object val = entry.getValue();
			if (key instanceof String)
			{
				String k = (String) key;
				if (k != null && k.contains("cls~~"))
				{
					cls = val.toString();
					log.debug("cls~~:" + cls);
					break;
				}
			}
		}
		String type = null;

		HashMap comboxMap = clientData.getComboBox();
		Iterator it1 = comboxMap.entrySet().iterator();
		while (it1.hasNext())
		{
			Map.Entry entry = (Map.Entry) it1.next();
			Object key = entry.getKey();
			Object val = entry.getValue();
			if (key instanceof String)
			{
				String k = (String) key;
				if (k != null && k.contains("!~objectHandle~partHandle~!createType"))
				{
					type = val.toString();
					type = "WCTYPE|" + type.substring(1, type.length() - 1);
					log.debug("type~~:" + cls);
					continue;
				}
				if (k != null && k.contains("createDuplicatePart~~"))
				{
					String value = val.toString();
					log.debug("value===" + value);
					if ("[True]".equals(value))
						foceCreate = true;
					continue;
				}
			}
		}

		System.out.println("parttype:" + type);
		if (type != null)
		{
			SetPartType(type);
		}
		clsNodeAttTemplate = ClassificationUtil.getLWCStructEnumAttTemplateByName(cls);
		log.debug("clsNodeAttTemplate:" + clsNodeAttTemplate);
	}

	public void SetPartType(String type)
	{
		this.partType = type;
	}

	public String GetPartType(String type)
	{
		return this.partType;
	}

	public String preCheckSpecification(List<ObjectBean> objectBeans)
	{
		if (foceCreate)
			return "";
		String existParts = null;
		WTPart part=null;
		String partnumber="";
		for (ObjectBean objBean : objectBeans)
		{
			Object obj = objBean.getObject();
			log.trace(obj);
			if (obj instanceof WTPart)
			{
				part=(WTPart)obj;
				partnumber=part.getNumber();
			}
		}
		try
		{
			existParts = getExistParts(cls, getSpecification(),partnumber);
		} catch (Exception e)
		{
			e.printStackTrace();
			existParts = e.getLocalizedMessage();
		}
		if (existParts == null)
			return "";
		else
			return existParts;
	}

	private String getExistParts(String cls, String specification,String partnumber) throws Exception
	{
		System.out.println("cls:" + cls + ",specification:" + specification);
		
		Set<WTPart> parts = PartUtil.getLastedPartByStringIBAValue(partnumber, "specification", specification);
		
		if(parts.size() == 0)
			return null;
		
		String result = "您创建的物料规格与：\n";
		String result2 = "物料规格重复，请确认是否需要创建！\n";
		for(WTPart part : parts){
			result = result + part.getNumber() + "\n";
		}
		return result + result2;
		/*
		 * 
		ArrayList<String> partList = new ArrayList<String>();
		String result = "您创建的物料规格与：\n";
		String result2 = "物料规格重复，请确认是否需要创建！\n";
		ArrayList list = getWTObjectByIBA("wt.part.WTPart", "cls", cls);
		log.debug("exsited part  count = " + list.size());
		for (int i = 0; i < list.size(); i++)
		{
			WTPart part = (WTPart) PartUtil.getLastestWTPartByNumber(list.get(i).toString());
			PersistableAdapter genericObj = new PersistableAdapter(part, null, null, null);
			genericObj.load("specification");
			String gg = (String) genericObj.get("specification");
			if (specification.equals(gg))
			{
				if (!partList.contains(part.getNumber()))
					partList.add(part.getNumber());
			}
		}
		partList.remove(partnumber);
		for (String s : partList)
		{
			result = result + s + "\n";
		}
		if (partList.isEmpty())
			return null;
		return result + result2;*/
	}

	private ArrayList getWTObjectByIBA(String objClassTypeName, String define, String value) throws Exception
	{

		Class objclass = Class.forName(objClassTypeName);

		ArrayList<String> returnArrayList = new ArrayList<String>();

		if (value != null && value.length() > 0 && define != null && define.length() > 0)
		{

			QuerySpec queryspec = new QuerySpec();// 構造查詢規格

			// i,j用于構造SearchCondition

			int td = queryspec.appendClassList(StringDefinition.class, false);// false表示返回此對象

			int tv = queryspec.appendClassList(StringValue.class, false);// false表示返回此對象

			int tp = queryspec.appendClassList(objclass, true);// true表示不返回此對象

			queryspec.appendOpenParen();// 表示'('符號

			queryspec.appendWhere(new SearchCondition(StringValue.class, "theIBAHolderReference.key.id", objclass, "thePersistInfo.theObjectIdentifier.id"), new int[] { tv, tp });

			queryspec.appendAnd();

			queryspec.appendWhere(new SearchCondition(StringValue.class, "definitionReference.key.id", StringDefinition.class, "thePersistInfo.theObjectIdentifier.id"),
					new int[] { tv, td });

			queryspec.appendAnd();

			queryspec.appendWhere(new SearchCondition(StringValue.class, "value2", SearchCondition.EQUAL, value), new int[] { tv });

			queryspec.appendAnd();

			queryspec.appendWhere(new SearchCondition(StringDefinition.class, "name", SearchCondition.LIKE, define), new int[] { td });

			queryspec.appendCloseParen();// 表示')'符號

			log.debug("queryspec==>" + queryspec);

			QueryResult queryresult = PersistenceHelper.manager.find((StatementSpec) queryspec);

			log.debug("queryresult==>" + queryresult.size());
			for (; queryresult.hasMoreElements();)
			{

				Persistable apersistable[] = (Persistable[]) queryresult.nextElement();

				if (apersistable[0] != null)
				{

					if (apersistable[0] instanceof WTPart)
					{

						WTPart part = (WTPart) apersistable[0];
						if (!returnArrayList.contains(part.getNumber()))
						{
							returnArrayList.add(part.getNumber());
						}
						//log.debug("part number====" + part.getNumber());

					}
				}
			}
		}
		//log.debug("returnArrayList==>" + returnArrayList);

		return returnArrayList;

	}

	private String getSpecification() throws WTException
	{
		ArrayList<AttributeTypeIdentifier> clsAttributList = getCLSAttributes();
		String specification = "";
		for (AttributeTypeIdentifier ati : clsAttributList)
		{
			HashMap pageValuemap = new HashMap();
			pageValuemap.putAll(clientData.getText());
			pageValuemap.putAll(clientData.getTextArea());
			pageValuemap.putAll(clientData.getComboBox());
			Iterator iter1 = pageValuemap.entrySet().iterator();
			while (iter1.hasNext())
			{
				Map.Entry entry = (Map.Entry) iter1.next();
				Object key = entry.getKey();
				Object val = entry.getValue();
				if (key instanceof String)
				{
					String k = (String) key;
					final AttributeDefinition ibaDef = IBADefinitionCache.getIBADefinitionCache().getAttributeDefinition(ati.getAttributeName());
					final AttributeDefDefaultView ibaView = IBADefViewManager.getAttDefView(ibaDef);
					String attributeDisplayName = ibaView.getDisplayName();
					String attributeName = ati.getAttributeName();
					if (k.contains("|"+attributeName+"~~NEW|")||k.contains("|"+attributeName+"~~WCI|"))
					{
					   
						specification = specification + "_" + attributeDisplayName + ":" + getEnumeValue(ati, val.toString());
						continue;
					}
				}
			}
		}
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

	public static String getSpecification(NmCommandBean clientData, WTPart part) throws WTException
	{
		ValidateSpecification validateSP = new ValidateSpecification(clientData);
		return validateSP.getSpecification();
	}

	private String getEnumeValue(AttributeTypeIdentifier ati, String val)
	{
		Collection coll = (Collection) clsAttributeEnumeMap.get(ati);
		if (coll != null)
		{
		   Iterator<EnumerationEntryReadView> it1 = coll.iterator();
		
			while (it1.hasNext())
			{
			 	EnumerationEntryReadView view = it1.next();
				String key = view.getName();
				if (val.substring(1, val.length() - 1).equals(key))
				{
					Object ibaValue = view.getPropertyValueByName("displayName").getValueAsString(Locale.CHINA, false);
					return ibaValue.toString();
				}
			}
		}
		if(val.indexOf("[")>-1 && val.indexOf("]")>-1){
		    val = val.substring(val.indexOf("[")+1,val.indexOf("]"));
		}
		return val;
	}

	private ArrayList<AttributeTypeIdentifier> getCLSAttributes() throws WTException
	{
		TypeIdentifier typeIdentifier = TypeIdentifierHelper.getTypeIdentifier(partType);// WCTYPE|wt.part.WTPart|cn.com.windchill.CATLPart
		ArrayList<AttributeTypeIdentifier> result = new ArrayList<AttributeTypeIdentifier>();
		if (typeIdentifier == null)
			return result;
		List<LayoutDefinitionReadView> layouts = new ArrayList<LayoutDefinitionReadView>();
		for (String paramKey : clientData.getParameterMap().keySet())
		{
			AttributeKey attrKey = CreateEditFormProcessorHelper.getAttributeKey(paramKey, false);
			if (attrKey != null)
			{
				AttributeIdentifier attrIdentifier = attrKey.getAttributeId();
				AttributeTypeIdentifier ati = (AttributeTypeIdentifier) attrIdentifier.getDefinitionIdentifier();
				Object value = clientData.getParameterMap().get(paramKey);
				Set<LayoutDefinitionReadView> nestedLayouts = TypeDefinitionServiceHelper.service.getLayoutDefinitions(ati, value, null, null);
				layouts.addAll(nestedLayouts);
			}
		}
		Collections.sort(layouts, new LayoutComparator());
		Iterator<LayoutDefinitionReadView> iterator = layouts.iterator();

		while (iterator.hasNext())
		{
			LayoutDefinitionReadView layout = iterator.next();
			for (LayoutComponentReadView layComp : layout.getAllLayoutComponents())
			{
				if (layComp instanceof GroupDefinitionReadView)
				{
					GroupDefinitionReadView layGrp = (GroupDefinitionReadView) layComp;
					ArrayList<LayoutComponent> attributesList = new ArrayList<LayoutComponent>(layGrp.getComponents());
					Collections.sort(attributesList, LayoutComponentComparator.getInstance());
					Iterator<LayoutComponent> iter = attributesList.iterator();
					while (iter.hasNext())
					{
						LayoutComponent comp = iter.next();
						AttributeTypeIdentifier tempAti = convertLayoutComponentToATI(comp, typeIdentifier);
						if (tempAti != null)
						{
							if (((GroupMembershipReadView) comp).getMember() instanceof AttributeDefinitionReadView)
							{
								AttributeDefinitionReadView adrv = (AttributeDefinitionReadView) ((GroupMembershipReadView) comp).getMember();
								Collection<ConstraintDefinitionReadView> constraints = adrv.getAllConstraints();
								for (ConstraintDefinitionReadView constraint : constraints)
								{
									String rule = constraint.getRule().getKey().toString();
									if (rule.indexOf("com.ptc.core.lwc.server.LWCEnumerationBasedConstraint") > -1)
									{
										RuleDataObject rdo = constraint.getRuleDataObj();
										if (rdo != null)
										{
											Collection coll = rdo.getEnumDef().getAllEnumerationEntries().values();
											clsAttributeEnumeMap.put(tempAti, coll);
										}
									}
								}
							}
							result.add(tempAti);
						}
					}
				}
			}
		}
		return result;
	}

	private AttributeTypeIdentifier convertLayoutComponentToATI(LayoutComponent attribute, TypeIdentifier typeIdentifier) throws WTException
	{
		final AttributeTypeIdentifier ati;
		if (attribute instanceof GroupMembershipReadView)
		{
			final GroupMembershipReadView groupMembership = (GroupMembershipReadView) attribute;
			if (groupMembership.getMember() instanceof AttributeDefinitionReadView)
			{
				ati = ((AttributeDefinitionReadView) groupMembership.getMember()).getAttributeTypeIdentifier(typeIdentifier);
			} else if (groupMembership.getMember() instanceof SeparatorReadView)
			{
				// Separator
				ati = AttributeHelper.getATI(attribute.getName(), typeIdentifier, false);
			} else
			{
				ati = AttributeHelper.getATI(attribute.getName(), typeIdentifier, true);
			}
		} else
		{
			ati = AttributeHelper.getATI(attribute.getName(), typeIdentifier, true);
		}
		return ati;
	}

	class LayoutComparator implements Comparator<LayoutDefinitionReadView>
	{

		@Override
		public int compare(LayoutDefinitionReadView layout1, LayoutDefinitionReadView layout2)
		{
			try
			{

				// Fetch the display name
				Long id1 = layout1.getReadViewIdentifier().getContextIdentifier().getId();
				TypeDefinitionReadView readView1 = TypeDefinitionServiceHelper.service.getTypeDefView(AttributeTemplateFlavor.LWCSTRUCT, id1);
				String displayName1 = PropertyHolderHelper.getDisplayName(readView1, SessionHelper.getLocale());

				Long id2 = layout2.getReadViewIdentifier().getContextIdentifier().getId();
				TypeDefinitionReadView readView2 = TypeDefinitionServiceHelper.service.getTypeDefView(AttributeTemplateFlavor.LWCSTRUCT, id2);
				String displayName2 = PropertyHolderHelper.getDisplayName(readView2, SessionHelper.getLocale());

				if (displayName1 != null && displayName2 != null)
				{
					return displayName1.toLowerCase().compareTo(displayName2.toLowerCase());
				}

			} catch (WTException e)
			{
				e.printStackTrace();
			}

			return 0;
		}
	}
}
