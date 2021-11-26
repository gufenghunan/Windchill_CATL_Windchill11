/*
 * Copyright (c) 2013-2015 SoftEasy. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of SoftEasy and is
 * subject to the terms of a software license agreement. You shall not disclose
 * such confidential information and shall use it only in accordance with the
 * terms of the license agreement. 
 */
package com.catl.ecad.utils;

import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import com.ptc.core.lwc.server.LWCNormalizedObject;
import com.ptc.core.meta.common.UpdateOperationIdentifier;

import wt.csm.navigation.litenavigation.ClassificationNodeDefaultView;
import wt.csm.navigation.service.ClassificationHelper;
import wt.fc.Persistable;
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
import wt.iba.value.litevalue.ReferenceValueDefaultView;
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
        initializeIBAHolder(ibaholder);
    }

    public String toString() {
        StringBuffer stringbuffer = new StringBuffer();
        Enumeration enumeration = ibaContainer.keys();
        try {
            while (enumeration.hasMoreElements()) {
                String s = (String) enumeration.nextElement();
                AbstractValueView abstractvalueview = (AbstractValueView) ((Object[]) ibaContainer.get(s))[1];
                stringbuffer.append(s + " - " + IBAValueUtility.getLocalizedIBAValueDisplayString(abstractvalueview, SessionHelper.manager.getLocale()));
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
            return IBAValueUtility.getLocalizedIBAValueDisplayString(abstractvalueview, locale);
        } catch (WTException wte) {
            wte.printStackTrace();
        }
        return null;
    }

    private void initializeIBAHolder(IBAHolder ibaholder) {
        ibaContainer = new Hashtable();
        try {
            ibaholder = IBAValueHelper.service.refreshAttributeContainer(ibaholder, null, SessionHelper.manager.getLocale(), null);
            DefaultAttributeContainer defaultattributecontainer = (DefaultAttributeContainer) ibaholder.getAttributeContainer();
            if (defaultattributecontainer != null) {
                AttributeDefDefaultView aattributedefdefaultview[] = defaultattributecontainer.getAttributeDefinitions();
                for (int i = 0; i < aattributedefdefaultview.length; i++) {
                    AbstractValueView aabstractvalueview[] = defaultattributecontainer.getAttributeValues(aattributedefdefaultview[i]);
                    if (aabstractvalueview != null) {
                        Object aobj[] = new Object[2];
                        aobj[0] = aattributedefdefaultview[i];
                        aobj[1] = aabstractvalueview[0];
                        ibaContainer.put(aattributedefdefaultview[i].getName(), ((Object) (aobj)));
                    }
                }

            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
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
            ibaContainer.put(attributedefdefaultview.getName(), ((Object) (aobj1)));
            return;
        }
    }

    public ClassificationNodeDefaultView getClassificationNodeDefaultView() throws WTException {
        if (ibaContainer == null || ibaContainer.size() == 0)
            return null;

        Object[] objs = (Object[]) ibaContainer.get("PartClassification");
        if (objs == null || objs.length != 2)
            return null;

        AbstractValueView abstractValueView = (AbstractValueView) objs[1];
        if (abstractValueView == null)
            return null;

        try {
            ClassificationNodeDefaultView classificationNodeDefaultView = ClassificationHelper.service.getClassificationNodeDefaultView(((ReferenceValueDefaultView) abstractValueView)
                    .getLiteIBAReferenceable());
            return classificationNodeDefaultView;
        } catch (RemoteException e) {
            throw new WTException(e);
        }
    }

    public IBAHolder updateIBAHolder(IBAHolder ibaholder) throws Exception {
        DefaultAttributeContainer defaultAttributeContainer = (DefaultAttributeContainer) (IBAValueHelper.service.refreshAttributeContainerWithoutConstraints(ibaholder)).getAttributeContainer();
        for (Enumeration enumeration = ibaContainer.elements(); enumeration.hasMoreElements();) {
            try {
                Object aobj[] = (Object[]) enumeration.nextElement();
                AbstractValueView abstractvalueview = (AbstractValueView) aobj[1];
                AttributeDefDefaultView attributedefdefaultview = (AttributeDefDefaultView) aobj[0];
                if (abstractvalueview.getState() == 1) {
                    defaultAttributeContainer.deleteAttributeValues(attributedefdefaultview);
                    abstractvalueview.setState(3);
                    defaultAttributeContainer.addAttributeValue(abstractvalueview);
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
        defaultAttributeContainer.setConstraintParameter(new String("CSM"));
        ibaholder.setAttributeContainer(defaultAttributeContainer);
        return ibaholder;
    }

    public static IBAHolder saveIBAHolder(IBAHolder ibaHolder) throws WTException {
        if (ibaHolder == null)
            return ibaHolder;
        DefaultAttributeContainer defaultAttributeContainer = (DefaultAttributeContainer) ibaHolder.getAttributeContainer();
        IBAValueDBService ibavaluedbservice = new IBAValueDBService();
        defaultAttributeContainer = (DefaultAttributeContainer) ibavaluedbservice.updateAttributeContainer(ibaHolder, defaultAttributeContainer != null ? defaultAttributeContainer
                .getConstraintParameter() : null, null, null);
        ibaHolder.setAttributeContainer(defaultAttributeContainer);
        return ibaHolder;
    }

    private AttributeDefDefaultView getAttributeDefinition(String s) {
        AttributeDefDefaultView attributedefdefaultview = null;
        try {
            attributedefdefaultview = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath(s);
            if (attributedefdefaultview == null) {
                AbstractAttributeDefinizerView abstractattributedefinizerview = DefinitionLoader.getAttributeDefinition(s);
                if (abstractattributedefinizerview != null)
                    attributedefdefaultview = IBADefinitionHelper.service.getAttributeDefDefaultView((AttributeDefNodeView) abstractattributedefinizerview);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return attributedefdefaultview;
    }

    private AbstractValueView internalCreateValue(AbstractAttributeDefinizerView abstractattributedefinizerview, String s) {
        AbstractValueView abstractvalueview = null;
        if (abstractattributedefinizerview instanceof FloatDefView)
            abstractvalueview = LoadValue.newFloatValue(abstractattributedefinizerview, s, null);
        else if (abstractattributedefinizerview instanceof StringDefView)
            abstractvalueview = LoadValue.newStringValue(abstractattributedefinizerview, s);
        else if (abstractattributedefinizerview instanceof IntegerDefView)
            abstractvalueview = LoadValue.newIntegerValue(abstractattributedefinizerview, s);
        else if (abstractattributedefinizerview instanceof RatioDefView)
            abstractvalueview = LoadValue.newRatioValue(abstractattributedefinizerview, s, null);
        else if (abstractattributedefinizerview instanceof TimestampDefView)
            abstractvalueview = LoadValue.newTimestampValue(abstractattributedefinizerview, s);
        else if (abstractattributedefinizerview instanceof BooleanDefView)
            abstractvalueview = LoadValue.newBooleanValue(abstractattributedefinizerview, s);
        else if (abstractattributedefinizerview instanceof URLDefView)
            abstractvalueview = LoadValue.newURLValue(abstractattributedefinizerview, s, null);
        else if (abstractattributedefinizerview instanceof ReferenceDefView)
            abstractvalueview = LoadValue.newReferenceValue(abstractattributedefinizerview, "ClassificationNode", s);
        else if (abstractattributedefinizerview instanceof UnitDefView)
            abstractvalueview = LoadValue.newUnitValue(abstractattributedefinizerview, s, null);
        return abstractvalueview;
    }

    private String parseMultiUnits(UnitDefView unitDefView, String value) {
        if (value != null)
            value = value.trim();

        if (value == null || value.equals("") || value.length() < 1)
            return "0";

        try {
            double d = Double.valueOf(value).doubleValue();
        } catch (Exception e) {
            return value;
        }

        value = value + "\t" + getDefaultUnit(unitDefView);

        return value;
    }

    private String getDefaultUnit(UnitDefView unitDefView) {
        if (measurementSystem == null)
            measurementSystem = MeasurementSystemCache.getCurrentMeasurementSystem();

        QuantityOfMeasureDefaultView quantityofmeasuredefaultview = unitDefView.getQuantityOfMeasureDefaultView();
        String defaultUnit = quantityofmeasuredefaultview.getBaseUnit();
        if (measurementSystem != null) {
            String s = unitDefView.getDisplayUnitString(measurementSystem);
            if (s == null)
                s = quantityofmeasuredefaultview.getDisplayUnitString(measurementSystem);
            if (s == null)
                s = quantityofmeasuredefaultview.getDefaultDisplayUnitString(measurementSystem);
            if (s != null)
                defaultUnit = s;
        }
        if (defaultUnit == null)
            defaultUnit = "";

        return defaultUnit;
    }
    
    public static void setIBAValue(Persistable p, Map<String, Object> dataMap)
            throws WTException {
    
        Locale loc = null;
        try {
                loc = SessionHelper.getLocale();
        } catch (WTException e) {
                e.printStackTrace();
                return;
    
        }
        setIBAValue(p, loc, dataMap);
    }
    
    /**
     * 
     * @param p
     * @param loc
     * @param dataMap
     * @return
     * @throws WTException
     */
    public static void setIBAValue(Persistable p, Locale loc,
                    Map<String, Object> dataMap) throws WTException {
            LWCNormalizedObject lwcObject = new LWCNormalizedObject(p, null, loc,
                            new UpdateOperationIdentifier());
            Iterator<String> keyIt = dataMap.keySet().iterator();
            String key = null;
            lwcObject.load(dataMap.keySet());
            while (keyIt.hasNext()) {
                    key = keyIt.next();
                    lwcObject.set(key, dataMap.get(key));
            }

            lwcObject.apply();
//          PersistenceServerHelper.update(p);
            PersistenceHelper.manager.save(p);
    }
}
