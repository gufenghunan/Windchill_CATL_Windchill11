package com.catl.cadence.util;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;

import wt.csm.navigation.ClassificationNode;
import wt.csm.navigation.litenavigation.ClassificationNodeDefaultView;
import wt.csm.navigation.service.ClassificationObjectsFactory;
import wt.fc.ObjectNoLongerExistsException;
import wt.fc.PersistenceHelper;
import wt.iba.definition.DefinitionLoader;
import wt.iba.definition.litedefinition.AbstractAttributeDefinizerView;
import wt.iba.definition.litedefinition.AttributeDefDefaultView;
import wt.iba.definition.litedefinition.AttributeDefNodeView;
import wt.iba.definition.litedefinition.BooleanDefView;
import wt.iba.definition.litedefinition.FloatDefView;
import wt.iba.definition.litedefinition.IntegerDefView;
import wt.iba.definition.litedefinition.RatioDefView;
import wt.iba.definition.litedefinition.ReferenceDefView;
import wt.iba.definition.litedefinition.StringDefView;
import wt.iba.definition.litedefinition.TimestampDefView;
import wt.iba.definition.litedefinition.URLDefView;
import wt.iba.definition.litedefinition.UnitDefView;
import wt.iba.definition.service.IBADefinitionHelper;
import wt.iba.value.DefaultAttributeContainer;
import wt.iba.value.IBAHolder;
import wt.iba.value.IBAValueUtility;
import wt.iba.value.litevalue.AbstractValueView;
import wt.iba.value.litevalue.FloatValueDefaultView;
import wt.iba.value.litevalue.ReferenceValueDefaultView;
import wt.iba.value.litevalue.UnitValueDefaultView;
import wt.iba.value.service.IBAValueDBService;
import wt.iba.value.service.IBAValueHelper;
import wt.iba.value.service.LoadValue;
import wt.session.SessionHelper;
import wt.units.service.MeasurementSystemCache;
import wt.units.service.QuantityOfMeasureDefaultView;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

public class IBAUtil {
	Hashtable ibaContainer;
	String measurementSystem = "SI";
	

	public IBAUtil(IBAHolder ibaholder) {
		initializeIBAPart(ibaholder);
	}

	public String toString() {
		StringBuffer stringbuffer = new StringBuffer();
		Enumeration enumeration = ibaContainer.keys();
		try {
			while (enumeration.hasMoreElements()) {
				String s = (String) enumeration.nextElement();
				AbstractValueView abstractvalueview = (AbstractValueView) ((Object[]) ibaContainer
						.get(s))[1];
				stringbuffer.append(s
						+ " - "
						+ IBAValueUtility.getLocalizedIBAValueDisplayString(
								abstractvalueview, SessionHelper.manager
										.getLocale()));
				stringbuffer.append('\n');
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return stringbuffer.toString();
	}

	public String getIBAValue(String s) {
		try {
			return getIBAValue(s, SessionHelper.manager.getLocale());
		} catch (WTException wte) {
			wte.printStackTrace();
		}
		return null;
	}

	public String getIBAValue(String s, Locale locale) {

		try {
			Object[] obj = (Object[]) ibaContainer.get(s);
			if (obj == null)
				return null;
			AbstractValueView abstractvalueview = (AbstractValueView) obj[1];
			return IBAValueUtility.getLocalizedIBAValueDisplayString(
					abstractvalueview, locale);
		} catch (WTException wte) {
			wte.printStackTrace();
		}
		return null;
	}

	private void initializeIBAPart(IBAHolder ibaholder) {
		ibaContainer = new Hashtable();
		try {
			ibaholder = IBAValueHelper.service.refreshAttributeContainer(
					ibaholder, null, SessionHelper.manager.getLocale(), null);
			DefaultAttributeContainer defaultattributecontainer = (DefaultAttributeContainer) ibaholder
					.getAttributeContainer();
			if (defaultattributecontainer != null) {
				AttributeDefDefaultView aattributedefdefaultview[] = defaultattributecontainer
						.getAttributeDefinitions();
				for (int i = 0; i < aattributedefdefaultview.length; i++) {
					AbstractValueView aabstractvalueview[] = defaultattributecontainer
							.getAttributeValues(aattributedefdefaultview[i]);
					if (aabstractvalueview != null) {
						Object aobj[] = new Object[2];
						aobj[0] = aattributedefdefaultview[i];
						aobj[1] = aabstractvalueview[0];
						ibaContainer.put(aattributedefdefaultview[i].getName(),
								((Object) (aobj)));
					}
				}

			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	public IBAHolder updateIBAPart(IBAHolder ibaholder) throws Exception {
		DefaultAttributeContainer defaultAttributeContainer = (DefaultAttributeContainer) (IBAValueHelper.service
				.refreshAttributeContainerWithoutConstraints(ibaholder))
				.getAttributeContainer();
		for (Enumeration enumeration = ibaContainer.elements(); enumeration
				.hasMoreElements();) {
			try {
				Object aobj[] = (Object[]) enumeration.nextElement();
				AbstractValueView abstractvalueview = (AbstractValueView) aobj[1];
				AttributeDefDefaultView attributedefdefaultview = (AttributeDefDefaultView) aobj[0];
				if (abstractvalueview.getState() == 1) {
					defaultAttributeContainer
							.deleteAttributeValues(attributedefdefaultview);
					abstractvalueview.setState(3);
					defaultAttributeContainer
							.addAttributeValue(abstractvalueview);
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}
		defaultAttributeContainer.setConstraintParameter(new String("CSM"));
		ibaholder.setAttributeContainer(defaultAttributeContainer);
		return ibaholder;
	}

	public void setIBAValue(String s, String s1) throws WTPropertyVetoException {
		AbstractValueView abstractvalueview = null;
		AttributeDefDefaultView attributedefdefaultview = null;
		Object aobj[] = (Object[]) ibaContainer.get(s);
		if (aobj != null) {
			abstractvalueview = (AbstractValueView) aobj[1];
			attributedefdefaultview = (AttributeDefDefaultView) aobj[0];
		}
		if (abstractvalueview == null)
			attributedefdefaultview = getAttributeDefinition(s);
		if (attributedefdefaultview == null) {
			System.out.println("definition is null ...");
			return;
		}

		if (attributedefdefaultview instanceof UnitDefView)
			s1 = parseMultiUnits((UnitDefView) attributedefdefaultview, s1);

		abstractvalueview = internalCreateValue(attributedefdefaultview, s1);
		if (abstractvalueview == null) {
			System.out.println("after creation, iba value is null ..");
			return;
		} else {
			abstractvalueview.setState(1);
			Object aobj1[] = new Object[2];
			aobj1[0] = attributedefdefaultview;
			aobj1[1] = abstractvalueview;
			ibaContainer.put(attributedefdefaultview.getName(),
					((Object) (aobj1)));
			return;
		}
	}
	
	public Enumeration getAllAttriDefName(){
		return ibaContainer.keys();
	}

	public ClassificationNode getClassificationNode() throws ObjectNoLongerExistsException, WTException{
		if (ibaContainer == null || ibaContainer.size() == 0)
			return null;
		//"WTPartClassification"根据不同的系统有不同的设定
		Object[] objs = (Object[]) ibaContainer.get("WTPartClassification");
		if (objs == null || objs.length != 2)
			return null;

		AbstractValueView abstractValueView = (AbstractValueView) objs[1];
		if (abstractValueView == null)
			return null;
		return (ClassificationNode) PersistenceHelper.manager
		.refresh(((ReferenceValueDefaultView) abstractValueView)
				.getLiteIBAReferenceable().getReferencedLiteObject()
				.getObjectID());
	}
	
	public ClassificationNodeDefaultView getClassificationNodeDefaultView()
			throws WTException {
		
		ClassificationNode classificationnode = getClassificationNode();
		
		ClassificationNodeDefaultView classificationNodeDefaultView = ClassificationObjectsFactory
				.newClassificationNodeDefaultView(classificationnode);
		
		return classificationNodeDefaultView;
	}
	
	public static IBAHolder saveIBAHolder(IBAHolder ibaHolder)
			throws WTException {
		if (ibaHolder == null)
			return ibaHolder;
		DefaultAttributeContainer defaultAttributeContainer = (DefaultAttributeContainer) ibaHolder
				.getAttributeContainer();
		IBAValueDBService ibavaluedbservice = new IBAValueDBService();
		defaultAttributeContainer = (DefaultAttributeContainer) ibavaluedbservice
				.updateAttributeContainer(
						ibaHolder,
						defaultAttributeContainer != null ? defaultAttributeContainer
								.getConstraintParameter()
								: null, null, null);
		ibaHolder.setAttributeContainer(defaultAttributeContainer);
		return ibaHolder;
	}

	private AttributeDefDefaultView getAttributeDefinition(String s) {
		AttributeDefDefaultView attributedefdefaultview = null;
		try {
			attributedefdefaultview = IBADefinitionHelper.service
					.getAttributeDefDefaultViewByPath(s);
			if (attributedefdefaultview == null) {
				AbstractAttributeDefinizerView abstractattributedefinizerview = DefinitionLoader
						.getAttributeDefinition(s);
				if (abstractattributedefinizerview != null)
					attributedefdefaultview = IBADefinitionHelper.service
							.getAttributeDefDefaultView((AttributeDefNodeView) abstractattributedefinizerview);
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return attributedefdefaultview;
	}

	private AbstractValueView internalCreateValue(
			AbstractAttributeDefinizerView abstractattributedefinizerview,
			String s) {
		AbstractValueView abstractvalueview = null;

		if (abstractattributedefinizerview instanceof FloatDefView)
			abstractvalueview = LoadValue.newFloatValue(
					abstractattributedefinizerview, s, null);
		else if (abstractattributedefinizerview instanceof StringDefView)
			abstractvalueview = LoadValue.newStringValue(
					abstractattributedefinizerview, s);
		else if (abstractattributedefinizerview instanceof IntegerDefView)
			abstractvalueview = LoadValue.newIntegerValue(
					abstractattributedefinizerview, s);
		else if (abstractattributedefinizerview instanceof RatioDefView)
			abstractvalueview = LoadValue.newRatioValue(
					abstractattributedefinizerview, s, null);
		else if (abstractattributedefinizerview instanceof TimestampDefView)
			abstractvalueview = LoadValue.newTimestampValue(
					abstractattributedefinizerview, s);
		else if (abstractattributedefinizerview instanceof BooleanDefView)
			abstractvalueview = LoadValue.newBooleanValue(
					abstractattributedefinizerview, s);
		else if (abstractattributedefinizerview instanceof URLDefView)
			abstractvalueview = LoadValue.newURLValue(
					abstractattributedefinizerview, s, null);
		else if (abstractattributedefinizerview instanceof ReferenceDefView)
			abstractvalueview = LoadValue.newReferenceValue(
					abstractattributedefinizerview, "ClassificationNode", s);
		else if (abstractattributedefinizerview instanceof UnitDefView)
			abstractvalueview = LoadValue.newUnitValue(
					abstractattributedefinizerview, s, null);

		// Adjust precision of Float or Unit value view
		adjustValueView(abstractvalueview);

		return abstractvalueview;
	}

	private void adjustValueView(AbstractValueView abstractvalueview) {
		try {
			if (abstractvalueview instanceof FloatValueDefaultView) {
				int precision = getDoublePrecision(getLocalizedIBAValueDisplayString(
						abstractvalueview, Locale.US));
				((FloatValueDefaultView) abstractvalueview)
						.setPrecision(precision);
			} else if (abstractvalueview instanceof UnitValueDefaultView) {
				int precision = getDoublePrecision(getLocalizedIBAValueDisplayString(
						abstractvalueview, Locale.US));
				((UnitValueDefaultView) abstractvalueview)
						.setPrecision(precision);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String getLocalizedIBAValueDisplayString(
			AbstractValueView abstractValueView, Locale locale) {
		try {
			return IBAValueUtility.getLocalizedIBAValueDisplayString(
					abstractValueView, locale);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private static int getDoublePrecision(String value) {
		if (value == null)
			return 2;

		if (value.indexOf(" ") > 0)
			value = value.substring(0, value.indexOf(" "));

		if (value.length() == 0)
			return 2;

		value = getFormattedDoubleString(value);

		int precision = value.length();

		double d = Double.valueOf(value).doubleValue();
		if (d < 0)
			precision = precision - 1;

		if (d <= -1.0 || d >= 1.0) {
			if (value.indexOf(".") < 0)
				return precision + 1;
			else
				return precision - 1;
		} else {
			for (int i = 0; i < value.length(); i++) {
				char ch = value.charAt(i);
				if (ch != '0' && ch != '.' && ch != '+' && ch != '-') {
					return (value.length() - i);
				}
			}

			return 2;
		}
	}

	private static String getFormattedDoubleString(String input) {
		int nIndex = -1;
		int vExp;
		String s;
		String sExp;
		String sHead;
		String sTail;
		String sSign;

		if (input != null)
			input = input.trim();

		if (input == null || input.length() == 0)
			return input;

		sSign = "";
		if (input.indexOf("-") == 0) {
			input = input.substring(1);
			sSign = "-";
		} else if (input.indexOf("+") == 0)
			input = input.substring(1);

		StringBuffer sb = new StringBuffer();

		Double f = new Double(input);
		s = String.valueOf(f.doubleValue());

		if (s == null || s.length() == 0)
			return s;
		// 不带科学记数法的数据小数点后为的零时将小数点去掉
		// 科学记数法将科学记数法去掉，并将后面多余的零去掉
		if (s.indexOf("E") == -1) { // 不带科学记数法的数据
			sHead = s.substring(0, s.indexOf("."));
			sTail = s.substring(s.indexOf(".") + 1); // 小数点后面的数位
			if (sTail != null && sTail.length() > 0) {
				for (int j = sTail.length() - 1; j >= 0; j--) {
					if (sTail.charAt(j) != '0') {
						nIndex = j;
						break;
					}
				}
			}
			sb.append(sHead);
			if (nIndex >= 0) {
				sTail = sTail.substring(0, nIndex + 1);
				sb.append(".").append(sTail); // 去掉末位小数点
			}
		} else { // 带科学记数法的数据
			sExp = s.substring(s.indexOf("E") + 1);
			vExp = Integer.valueOf(sExp).intValue();

			s = s.substring(0, s.indexOf("E"));
			sHead = s.substring(0, s.indexOf("."));
			sTail = s.substring(s.indexOf(".") + 1);
			sb.append(sHead);
			int length = 0;
			if (Integer.valueOf(sTail).intValue() != 0) {
				sb.append(sTail);
				length = sTail.length();
			}
			if (vExp > 0) { // 小数位小于科学记数时，后面添0
				if (length <= vExp) {
					for (int i = 0; i < vExp - length; i++)
						sb.append("0");
				} else {
					sb.insert(vExp + 1, ".");
				}
			} else { // vExp <=0 时，在数据前面添加科学记数个0，并在第1位加小数点
				for (int i = 0; i < Math.abs(vExp); i++)
					sb.insert(0, "0");
				sb.insert(1, ".");
			}
		}

		if (sSign == null || sSign.length() == 0)
			return sb.toString();
		else
			return sSign + sb.toString();
	}

	private String parseMultiUnits(UnitDefView unitDefView, String value) {
		if (value != null)
			value = value.trim();

		if (value == null || value.equals("") || value.length() < 1)
			return "0";

		try {
			Double.valueOf(value).doubleValue();
		} catch (Exception e) {
			return value;
		}

		value = value + "\t" + getDefaultUnit(unitDefView);

		return value;
	}

	private String getDefaultUnit(UnitDefView unitDefView) {
		if (measurementSystem == null)
			measurementSystem = MeasurementSystemCache
					.getCurrentMeasurementSystem();

		QuantityOfMeasureDefaultView quantityofmeasuredefaultview = unitDefView
				.getQuantityOfMeasureDefaultView();
		String defaultUnit = quantityofmeasuredefaultview.getBaseUnit();
		if (measurementSystem != null) {
			String s = unitDefView.getDisplayUnitString(measurementSystem);
			if (s == null)
				s = quantityofmeasuredefaultview
						.getDisplayUnitString(measurementSystem);
			if (s == null)
				s = quantityofmeasuredefaultview
						.getDefaultDisplayUnitString(measurementSystem);
			if (s != null)
				defaultUnit = s;
		}
		if (defaultUnit == null)
			defaultUnit = "";

		return defaultUnit;
	}
}
