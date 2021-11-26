package com.ptc.windchill.csm.client.mvc.builders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import wt.fc.ReferenceFactory;
import wt.fc.WTReference;
import wt.inf.container.WTContainer;
import wt.log4j.LogR;
import wt.part.WTPart;
import wt.session.SessionHelper;
import wt.type.TypedUtility;
import wt.util.WTException;

import com.catl.line.constant.ConstantLine;
import com.ptc.core.HTMLtemplateutil.server.processors.AttributeKey;
import com.ptc.core.components.descriptor.DescriptorConstants;
import com.ptc.core.components.descriptor.DescriptorConstants.DescriptorPropertyValues;
import com.ptc.core.components.forms.CreateEditFormProcessorHelper;
import com.ptc.core.components.util.AttributeHelper;
import com.ptc.core.lwc.common.AttributeTemplateFlavor;
import com.ptc.core.lwc.common.LayoutComponent;
import com.ptc.core.lwc.common.LayoutPosition;
import com.ptc.core.lwc.common.LayoutProperty;
import com.ptc.core.lwc.common.PropertyDefinitionConstants;
import com.ptc.core.lwc.common.TypeDefinitionService;
import com.ptc.core.lwc.common.view.AttributeDefinitionReadView;
import com.ptc.core.lwc.common.view.DisplayStyleReadView;
import com.ptc.core.lwc.common.view.GroupDefinitionReadView;
import com.ptc.core.lwc.common.view.GroupMembershipReadView;
import com.ptc.core.lwc.common.view.LayoutComponentReadView;
import com.ptc.core.lwc.common.view.LayoutDefinitionReadView;
import com.ptc.core.lwc.common.view.PropertyHolderHelper;
import com.ptc.core.lwc.common.view.PropertyValueReadView;
import com.ptc.core.lwc.common.view.SeparatorReadView;
import com.ptc.core.lwc.common.view.TypeDefinitionReadView;
import com.ptc.core.lwc.server.TypeDefinitionServiceHelper;
import com.ptc.core.meta.common.AttributeIdentifier;
import com.ptc.core.meta.common.AttributeTypeIdentifier;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.core.meta.common.TypeInstanceIdentifier;
import com.ptc.core.ui.resources.ComponentMode;
import com.ptc.core.ui.resources.ComponentType;
import com.ptc.jca.mvc.LayoutComponentComparator;
import com.ptc.jca.mvc.components.AbstractAttributesComponentBuilder;
import com.ptc.jca.mvc.components.JcaAttributeConfig;
import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.jca.mvc.components.JcaComponentParamsUtils;
import com.ptc.jca.mvc.components.JcaGroupConfig;
import com.ptc.jca.mvc.components.JcaNestedGroupConfig;
import com.ptc.mvc.components.AttributePanelConfig;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentConfigFactory;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.CustomizableViewConfig;
import com.ptc.netmarkets.search.utils.SearchUtils;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.netmarkets.util.beans.NmHelperBean;
import com.ptc.windchill.csm.client.utils.CSMUtils;

/**
 * Attribute panel builder to display the attributes of the classification node while classifying a part.
 *
 * @author vgarg
 */
@ComponentBuilder("classification.attribute.panel")
public class ClassificationAttributePanel extends
        AbstractAttributesComponentBuilder {

    private static final Logger logger = LogR
            .getLogger(ClassificationAttributePanel.class.getName());

    private static TypeDefinitionService TYPE_DEF_SERVICE = TypeDefinitionServiceHelper.service;

    private static final String TOP = "top";

    private static final String hiddenField = "ClfBindingAttr";

    private static final String separator = "~~SEP~~";

    @Override
    protected CustomizableViewConfig buildAttributesComponentConfig(
            ComponentParams params) throws WTException {

        if (logger.isDebugEnabled()) {
            logger.debug("Entered buildAttributesComponentConfig of ClassificationAttributePanel.");
        }

        NmHelperBean helperBean = ((JcaComponentParams) params).getHelperBean();
        NmCommandBean commandBean = helperBean.getNmCommandBean();
        WTContainer wtContainer=commandBean.getContainer();
        String containerName=null;
        if(wtContainer!=null){
        	containerName=wtContainer.getName();
        }
       
        Map<String, Object> parameterMap = commandBean.getRequestData()
                .getParameterMap();
        Locale locale = commandBean.getLocale();
        String contextType = JcaComponentParamsUtils.getInstance()
                .getContextObjectClassName((JcaComponentParams) params);
        TypeIdentifier typeIdentifier = JcaComponentParamsUtils.getInstance()
                .getContextObjectTypeIdentifier((JcaComponentParams) params);
        ComponentConfigFactory factory = getComponentConfigFactory();
        AttributePanelConfig panelConfig = factory
                .newAttributePanelConfig("attributePanel");
        // To render common attributes only once in the UI.
        ArrayList<AttributeTypeIdentifier> renderedAtis = new ArrayList<AttributeTypeIdentifier>();
        List<LayoutDefinitionReadView> layouts = new ArrayList<LayoutDefinitionReadView>();
        ReferenceFactory rf = new ReferenceFactory();
        WTReference ref;
        WTPart mypartObj = null;
        TypeIdentifier tId = null;
        TypeDefinitionReadView typReadVw = null;
        boolean isEditMode = false;
        Set<AttributeTypeIdentifier> attributes = null;

        // get this only if it is Edit mode
        ComponentMode mode = getComponentMode(params);
        if (mode.equals(ComponentMode.EDIT)) {
            // original_oid field is not present on edit part wizard,
            // so reading action oid which refers to the target object of the action.
            String actionOid = commandBean.getActionOid().getReferenceString();
            if (SearchUtils.isValidReference(actionOid)) {
                ref = rf.getReference(actionOid);
                mypartObj = (WTPart) ref.getObject();
            }
            TypeInstanceIdentifier TiId = TypedUtility
                    .getTypeInstanceIdentifier(mypartObj);

            // get the TypeIdentifier from TypeInstanceIdentifier
            tId = (TypeIdentifier) TiId.getDefinitionIdentifier();
            // get the TypeReadView and ATIs of attributes with classification constraint
            typReadVw = TYPE_DEF_SERVICE.getTypeDefView(tId);
            attributes = CSMUtils.getClassificationConstraintAttributes(typReadVw);
            isEditMode = true; // i.e. EDIT mode
        }
        String atiExtForm;
        for (String paramKey : parameterMap.keySet()) {
            AttributeKey attrKey = CreateEditFormProcessorHelper
                    .getAttributeKey(paramKey, false);
            if (attrKey != null) {
                AttributeIdentifier attrIdentifier = attrKey.getAttributeId();
                AttributeTypeIdentifier ati = (AttributeTypeIdentifier) attrIdentifier
                        .getDefinitionIdentifier();
                Object value = parameterMap.get(paramKey);
               
                Set<LayoutDefinitionReadView> nestedLayouts = TypeDefinitionServiceHelper.service
                        .getLayoutDefinitions(ati, value, null, null);
                if (logger.isDebugEnabled()) {
                    logger.debug("AttributeTypeIdentifier :: " + ati);
                    logger.debug("Value :: " + value);
                    logger.debug("Layouts :: " + nestedLayouts);
                }

                layouts.addAll(nestedLayouts);
            }
            // SPR#2206797-03
            if (isEditMode) {
                if (paramKey.equalsIgnoreCase(hiddenField)) {
                    Object atiVal = parameterMap.get(paramKey);
                    if (atiVal instanceof String[]) {
                        String[] atiValarr = (String[]) parameterMap.get(paramKey); // [WCTYPE|wt.part.WTPart~IBA|ADWRootDesign~~SEP~~CHIPSET,
                                                                                    // WCTYPE|wt.part.WTPart~IBA|OVDemoChipset~~SEP~~CAPACITOR-VARIABLE-TUNING]
                        for (int i = 0; i < atiValarr.length; i++) {
                            String[] eachPair = atiValarr[i].split(separator); // [WCTYPE|wt.part.WTPart~IBA|ADWRootDesign,CHIPSET]
                            String[] attrName = eachPair[0].split(","); // WCTYPE|wt.part.WTPart~IBA|ADWRootDesign
                            String value = eachPair[1]; // CHIPSET
                            for (AttributeTypeIdentifier ati : attributes) {
                                atiExtForm = ati.toExternalForm();
                                // compare atiExtForm with the one received in the parameterMap
                                if (attrName[0].equals(atiExtForm)) {
                                    Set<LayoutDefinitionReadView> nestedLayouts1 = TypeDefinitionServiceHelper.service
                                            .getLayoutDefinitions(ati, value, null,
                                                    null);
                                    layouts.addAll(nestedLayouts1);
                                    break;
                                }

                            }
                        }

                    }
                }
            }
        }

        Collections.sort(layouts, new LayoutComparator()); // sorting the nested
                                                           // layouts.
        Iterator<LayoutDefinitionReadView> iterator = layouts.iterator();
        while (iterator.hasNext()) {
            LayoutDefinitionReadView layout = iterator.next();

            // Fetch the display name of the classification node.
            Long id = layout.getReadViewIdentifier().getContextIdentifier()
                    .getId();
            TypeDefinitionReadView readView = TypeDefinitionServiceHelper.service
                    .getTypeDefView(AttributeTemplateFlavor.LWCSTRUCT, id);
            String internalName = readView.getName();
            String displayName = PropertyHolderHelper.getDisplayName(readView,
                    commandBean.getLocale());
            if (logger.isDebugEnabled()) {
                logger.debug("Internal Name of classification node :: "
                        + internalName);
                logger.debug("Display Name of classification node :: "
                        + displayName);
            }

            JcaNestedGroupConfig nestedGroupConfig = new JcaNestedGroupConfig();
            nestedGroupConfig.setLabel(displayName);
            nestedGroupConfig.setId(internalName);
            nestedGroupConfig.setType(contextType);

            for (LayoutComponentReadView layComp : layout
                    .getAllLayoutComponents()) {
                if (layComp instanceof GroupDefinitionReadView) {
                    GroupDefinitionReadView layGrp = (GroupDefinitionReadView) layComp;

                    JcaGroupConfig grpConfig = (JcaGroupConfig) factory
                            .newGroupConfig();
                    grpConfig.setSortOrder(layGrp.getSortOrder());
                    // changed to fix  - SPR #4062454
                    String groupDisplayName = PropertyHolderHelper.getDisplayName(layGrp, locale);
                    if(groupDisplayName.equals("衍生PN")){
                    	continue;
                    }
                    
                    if(groupDisplayName.equals("母PN")&&containerName!=null&&!containerName.equals(ConstantLine.libary_lineparentpn)){
                    	continue;
                    }
                    
                    if(groupDisplayName.equals("EVC属性")&&containerName!=null&&!containerName.equals("电芯EVC材料库")){
                    	continue;
                    }
                    
                    if(groupDisplayName.equals("RI属性")&&containerName!=null&&!containerName.equals("电芯RI材料库")){
                    	continue;
                    }
                    
                    grpConfig.setLabel(groupDisplayName);
                    // each config has to have ID
                    grpConfig.setId(layGrp.getName() + Math.random());
                    String styleName = layGrp.getDisplayStyle().getName();
                    grpConfig
                            .setIsGridLayout(DisplayStyleReadView.GROUP_GRID_DISP_STYLE_NAME
                                    .equals(styleName));
                    grpConfig.setType(typeIdentifier.getTypename());

                    // Label position in attribute panel
                    String renderOnTop = PropertyHolderHelper.getPropertyValue(
                            layGrp, commandBean.getLocale(),
                            PropertyDefinitionConstants.LABEL_POSITION);
                    grpConfig.setRenderOnTop(TOP.equalsIgnoreCase(renderOnTop));

                    ArrayList<LayoutComponent> attributesList = new ArrayList<LayoutComponent>(
                            layGrp.getComponents());
                    // Sorting based on row for attributes needs to be here
                    // until the Renderer is hooked up.
                    Collections.sort(attributesList,
                            LayoutComponentComparator.getInstance());
                    Integer rowPos = 0;
                    Iterator<LayoutComponent> iter = attributesList.iterator();
                    while (iter.hasNext()) {

                        LayoutComponent comp = iter.next();
                        AttributeTypeIdentifier tempAti = convertLayoutComponentToATI(
                                comp, typeIdentifier);
                        JcaAttributeConfig attrConfig = (JcaAttributeConfig) factory
                                .newAttributeConfig();

                        // tempAti will be null for blank spaces and the spaces should be set in attrConfig to show
                        // attributes in exact row and column on create/edit part wizard as in layout in admin.
                        if (tempAti == null)
                        {
                            // set id for blank spaces to correctly call its renderer NBSPDataUtility
                            attrConfig.setId(comp.getName());
                        }
                        else
                        {
                            attrConfig.setId(tempAti.toExternalForm());
                        }
                        if (!renderedAtis.contains(tempAti)) {
                            // To render common attributes only once in the UI
                            if (logger.isDebugEnabled()) {
                                logger.debug("Creating attribute config for layout component :: "
                                        + tempAti);
                            }

                            String attrDisplayName = null;
                            if (((GroupMembershipReadView) comp).getMember() instanceof AttributeDefinitionReadView) {
                                // attribute is really the group membership and not the attribute itself.
                                // in theory getMember() could be an att or a separator, which doesn't have a display name.
                                // changed to fix  - SPR #4062454
                                AttributeDefinitionReadView attrReadView = (AttributeDefinitionReadView) ((GroupMembershipReadView) comp).getMember();
                                // check to see if there is a display value for an attribute
                                attrDisplayName = PropertyHolderHelper.getDisplayName(attrReadView, locale);
                                // SPR#2214462-01: set default value display mode attribute property
                                String displaymode = null;
                                PropertyValueReadView defaultDisplayProperty = ((AttributeDefinitionReadView) ((GroupMembershipReadView) comp)
                                        .getMember())
                                        .getPropertyValueByName("DEFAULT_VALUE_DISPLAY_MODE");
                                if (defaultDisplayProperty != null) {
                                    displaymode = defaultDisplayProperty.getValueAsString();

                                    if (DescriptorPropertyValues.DEFAULT_VALUE_DISPLAY_MODE_BUTTON
                                            .equals(displaymode)) {
                                        attrConfig
                                                .setDefaultValueDisplayMode(DescriptorPropertyValues.DEFAULT_VALUE_DISPLAY_MODE_BUTTON);
                                    }
                                    else if (DescriptorPropertyValues.DEFAULT_VALUE_DISPLAY_MODE_NONE
                                            .equals(displaymode)) {
                                        attrConfig
                                                .setDefaultValueDisplayMode(DescriptorPropertyValues.DEFAULT_VALUE_DISPLAY_MODE_NONE);
                                    }
                                    else if (DescriptorPropertyValues.DEFAULT_VALUE_DISPLAY_MODE_PREPOPULATE
                                            .equals(displaymode)) {
                                        attrConfig
                                                .setDefaultValueDisplayMode(DescriptorPropertyValues.DEFAULT_VALUE_DISPLAY_MODE_PREPOPULATE);
                                    }

                                }
                            }
                            if (attrDisplayName != null) {
                                attrConfig.setLabel(attrDisplayName);
                            }

                            LayoutPosition layoutPosition = comp
                                    .getLayoutPosition();
                            if (styleName
                                    .equalsIgnoreCase(DisplayStyleReadView.GROUP_LIST_DISP_STYLE_NAME)) {
                                attrConfig.setColPos(0);
                                attrConfig.setRowPos(rowPos++);
                            } else {
                                attrConfig.setColPos(layoutPosition.getCol());
                                attrConfig.setRowPos(layoutPosition.getRow());
                            }
                            attrConfig.setColSpan(layoutPosition.getColspan());

                            // SPR#2212390-01: set proper component mode for the attribute in layout
                            for (LayoutProperty prop : ((GroupMembershipReadView) comp).getAllProperties()) {
                                if (DescriptorConstants.ColumnProperties.NEED.equals(prop.getName())) {
                                    // special case for "need" property as it needs to set model attribute in cache
                                    attrConfig.setNeed((String) prop.getValue());

                                } else if ("mode".equalsIgnoreCase(prop.getName())) { // Need to set the mode on the
                                                                                      // component descriptor
                                    // and not as property.
                                    String modeValue = prop.getValueAsString();
                                    if (modeValue != null) {
                                        if (ComponentMode.VIEW.toString().equalsIgnoreCase(modeValue)) {
                                            attrConfig.setComponentMode(ComponentMode.VIEW);
                                        } else if (ComponentMode.EDIT.toString().equalsIgnoreCase(modeValue)) {
                                            attrConfig.setComponentMode(ComponentMode.EDIT);
                                        } else if (ComponentMode.CREATE.toString().equalsIgnoreCase(modeValue)) {
                                            attrConfig.setComponentMode(ComponentMode.CREATE);
                                        } else if (ComponentMode.SEARCH.toString().equalsIgnoreCase(modeValue)) {
                                            attrConfig.setComponentMode(ComponentMode.SEARCH);
                                        }
                                    }
                                }
                                // SPR#2214462-01:setting other LAYOUT PROPERTIES such as display mode, etc
                                else {
                                    attrConfig.setDescriptorProperty(prop.getName(), prop.getValue());
                                    attrConfig.setDescriptorProperty(prop.getName(), prop.getValue(locale, true));
                                }

                            }
                            grpConfig.addComponent(attrConfig);

                            if (tempAti != null)
                                renderedAtis.add(tempAti);
                        }
                    }
                    nestedGroupConfig.addComponent(grpConfig);
                }
            }
            panelConfig.addComponent(nestedGroupConfig);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Exiting buildAttributesComponentConfig of ClassificationAttributePanel.");
        }

        return panelConfig;
    }

    @Override
    protected ComponentType getComponentType(ComponentParams params,
            ComponentMode mode) {
        return ComponentType.WIZARD_ATTRIBUTES_TABLE;
    }

    /**
     * Method to convert the input layout component to ATI.
     *
     * @param attribute
     *            - layout component
     * @param typeIdentifier
     *            - type identifier
     * @return - an AttributeTypeIdentifier
     * @throws WTException - Thrown WTException, if any of the invoked method throws this exception.
     */
    private AttributeTypeIdentifier convertLayoutComponentToATI(
            LayoutComponent attribute, TypeIdentifier typeIdentifier)
            throws WTException {
        final AttributeTypeIdentifier ati;
        if (attribute instanceof GroupMembershipReadView) {
            final GroupMembershipReadView groupMembership = (GroupMembershipReadView) attribute;
            if (groupMembership.getMember() instanceof AttributeDefinitionReadView) {
                ati = ((AttributeDefinitionReadView) groupMembership
                        .getMember())
                        .getAttributeTypeIdentifier(typeIdentifier);
            } else if (groupMembership.getMember() instanceof SeparatorReadView) {
                // Separator
                ati = AttributeHelper.getATI(attribute.getName(),
                        typeIdentifier, false);
            } else {
                ati = AttributeHelper.getATI(attribute.getName(),
                        typeIdentifier, true);
            }
        } else {
            ati = AttributeHelper.getATI(attribute.getName(), typeIdentifier,
                    true);
        }
        return ati;
    }

    /**
     * Comparator to sort the layouts alphabetically.
     */
    class LayoutComparator implements Comparator<LayoutDefinitionReadView> {

        @Override
        public int compare(LayoutDefinitionReadView layout1,
                LayoutDefinitionReadView layout2) {
            try {

                // Fetch the display name
                Long id1 = layout1.getReadViewIdentifier()
                        .getContextIdentifier().getId();
                TypeDefinitionReadView readView1 = TypeDefinitionServiceHelper.service
                        .getTypeDefView(AttributeTemplateFlavor.LWCSTRUCT, id1);
                String displayName1 = PropertyHolderHelper.getDisplayName(
                        readView1, SessionHelper.getLocale());

                Long id2 = layout2.getReadViewIdentifier()
                        .getContextIdentifier().getId();
                TypeDefinitionReadView readView2 = TypeDefinitionServiceHelper.service
                        .getTypeDefView(AttributeTemplateFlavor.LWCSTRUCT, id2);
                String displayName2 = PropertyHolderHelper.getDisplayName(
                        readView2, SessionHelper.getLocale());

                if (displayName1 != null && displayName2 != null) {
                    return displayName1.toLowerCase().compareTo(
                            displayName2.toLowerCase());
                }

            } catch (WTException e) {
                logger.error(e.getLocalizedMessage());
            }

            return 0;
        }

    }

}
