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
package com.catl.change.mvc;

import java.util.ArrayList;
import java.util.List;

import wt.change2.AffectedActivityData;
import wt.change2.Changeable2;
import wt.change2.WTChangeActivity2;
import wt.fc.WTObject;
import wt.util.WTException;

import com.catl.common.constant.ChangeState;
import com.ptc.core.components.beans.CreateAndEditWizBean;
import com.ptc.core.components.descriptor.DescriptorConstants;
import com.ptc.core.htmlcomp.components.ConfigurableTableBuilder;
import com.ptc.core.htmlcomp.tableview.ConfigurableTable;
import com.ptc.core.ui.resources.ComponentMode;
import com.ptc.jca.mvc.components.JcaColumnConfig;
import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.jca.mvc.components.JcaTableConfig;
import com.ptc.mvc.components.ColumnConfig;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentConfigFactory;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.TableConfig;
import com.ptc.mvc.components.TypeBased;
import com.ptc.mvc.util.ClientMessageSource;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.object.mvc.helper.ItemIdVersion;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.netmarkets.util.beans.NmHelperBean;
import com.ptc.windchill.enterprise.change2.ChangeLinkAttributeHelper;
import com.ptc.windchill.enterprise.change2.ChangeManagementDescriptorConstants;
import com.ptc.windchill.enterprise.change2.ChangeManagementDescriptorConstants.ColumnIdentifiers;
import com.ptc.windchill.enterprise.change2.ChangeManagementDescriptorConstants.PropertyDescriptorKeys;
import com.ptc.windchill.enterprise.change2.beans.ChangeWizardBean;
import com.ptc.windchill.enterprise.change2.commands.ChangeItemQueryCommands;
import com.ptc.windchill.enterprise.change2.constraints.ChangeMgmtConstraintsClientHelper;
import com.ptc.windchill.enterprise.change2.mvc.builders.tables.AbstractAffectedAndResultingItemsTableBuilder;
import com.ptc.windchill.enterprise.change2.search.AffectedObjectsPickerConfig;
import com.ptc.windchill.enterprise.change2.tableViews.ChangeTaskAffectedItemsTableViews;
import com.ptc.windchill.enterprise.changeable.ChangeableDataFactory;
import com.ptc.windchill.enterprise.changeable.ChangeableObjectBean;


/**
 * Builder for the Change Task Affected Objects table.
 * 
 * <BR>
 * <BR>
 * <B>Supported API: </B>true <BR>
 * <BR>
 * <B>Extendable: </B>true
 */
@TypeBased(value = "wt.change2.ChangeActivity2")
@ComponentBuilder("affectedItemsTable")
public class AffectedItemsTableBuilder extends AbstractAffectedAndResultingItemsTableBuilder implements ConfigurableTableBuilder {
    private static final String TABLE_DATA_COLUMNID = ChangeManagementDescriptorConstants.ColumnIdentifiers.CHANGE_TABLE_DATA_COLUMN;

    ClientMessageSource messageChange2ClientResource = getMessageSource("com.ptc.windchill.enterprise.change2.change2ClientResource");

    private static final String TABLE_ID = "changeTask_affectedItems_table";

    private static final String COMMENTS_COLUMN_ID = ChangeManagementDescriptorConstants.ColumnIdentifiers.AFFECTED_ACTIVITY_DATA_DESCRIPTION;

    private static final String CHANGE_INTENT_COLUMNN_ID = ChangeManagementDescriptorConstants.ColumnIdentifiers.CHANGE_INTENT;

    private static final org.apache.log4j.Logger log = wt.log4j.LogR.getLogger(AffectedItemsTableBuilder.class.getName());

    @Override
    public ConfigurableTable buildConfigurableTable(String id) throws WTException {
        return new ChangeTaskAffectedItemsTableViews();
    }

    /**
     * Given the params returns the affected data by calling the
     * {@link ChangeItemQueryCommands#getAffectedData(com.ptc.netmarkets.util.beans.NmCommandBean)}
     * method. <BR>
     * <BR>
     * <B>Supported API: </B>true
     * 
     * @see com.ptc.windchill.enterprise.change2.commands.ChangeItemQueryCommands
     */
    @Override
    public Object buildComponentData(ComponentConfig config, ComponentParams params) throws WTException {
        NmHelperBean helper = ((JcaComponentParams) params).getHelperBean();
        NmCommandBean cb = helper.getNmCommandBean();
        cb.addRequestDataParam(ChangeMgmtConstraintsClientHelper.BASE_LINK_CLASS, AffectedActivityData.class.getName(), false);
        List<ChangeableObjectBean> beans = ChangeableDataFactory.getAffectedObjects(cb);
        buildClientFeedBacks(beans, TABLE_ID);
        return beans; 
    }

    /**
     * Describes the change task affected objects table. The following columns
     * property are added to the table config:
     * 
     * <li>"statusFamily_General"</li> 
     * <li>"statusFamily_Share"</li> 
     * <li>"statusFamily_Change"</li> 
     * <li>"change_tableData"</li>
     * <li>"supportAnnotations"</li> 
     * <li>"infoPageAction"</li> 
     * <li>"nmActions"</li>
     * <li>"name"</li> 
     * <li>"lifeCycleState"</li> 
     * <li>"thePersistInfo.modifyStamp"</li> 
     * <li>"modifier.name"</li>
     * <li>"aiDescription"</li> 
     * <li>"changeIntent"</li>
     * <li>"associatedAnnotations"</li>
     * <li>disposition columns see <code>getDispositionComponentIds()</code></li>
     * 
     * <BR>
     * <BR>
     * <B>Supported API: </B>true
     * 
     */
    @Override
    public ComponentConfig buildComponentConfig(ComponentParams params) throws WTException {
        ComponentConfigFactory factory = getComponentConfigFactory();
        JcaTableConfig table = (JcaTableConfig) factory.newTableConfig();
        table.setTargetObject(ChangeableObjectBean.CHANGEABLE);
        table.setDescriptorProperty(DescriptorConstants.ENFORCE_TARGET_OBJECT, true);
        ChangeWizardBean changeWizardBean = ChangeWizardBean.getChangeWizardBean(params);
        boolean createOrEdit = false;

        String affectedItems_table_actionModel = "";
        String affectedItems_row_actionModel = "";
        String affectedItems_helpLink = "";

        NmHelperBean helper = ((JcaComponentParams) params).getHelperBean();
        NmCommandBean cb = helper.getNmCommandBean();        
        boolean isWizard = CreateAndEditWizBean.isCreateEditWizard(cb);
        if (isWizard) {
            affectedItems_table_actionModel = "changeTask.affectedItems.table.create_edit";
            affectedItems_row_actionModel = "changeTask.affectedItems.row.actions";
            affectedItems_helpLink = "change_affectedItems_edit";
            createOrEdit = true;
        } else {
            affectedItems_table_actionModel = "changeTask.affectedItems.table.view";
            affectedItems_row_actionModel = "changeTask.affectedItems.row.actions.view";
            affectedItems_helpLink = "change_affectedItems";
        }

        params.setAttribute("changeTableId", TABLE_ID);
        table.setId(TABLE_ID);
        table.setConfigurable(true);
        table.setLabel(messageChange2ClientResource.getMessage("AFFECTED_ITEMS_TABLE"));
        table.setSelectable(true);
        table.setActionModel(affectedItems_table_actionModel);
        table.setInitialRows(true);
        table.setTypes(Changeable2.class.getName());
        table.addComponent(factory.newColumnConfig(DescriptorConstants.ColumnIdentifiers.GENERAL_STATUS_FAMILY, true));

        table.addComponent(factory.newColumnConfig(DescriptorConstants.ColumnIdentifiers.SHARE_STATUS_FAMILY, true));

        table.addComponent(factory.newColumnConfig(DescriptorConstants.ColumnIdentifiers.CHANGE_STATUS_FAMILY, true));

        table.addComponents(ItemIdVersion.getColumnConfigs(factory));

        JcaColumnConfig col = (JcaColumnConfig) factory.newColumnConfig(TABLE_DATA_COLUMNID, false);
        addSupportedDispositionProperty(col);
        col.setDescriptorProperty(PropertyDescriptorKeys.ANNOTATIONS_SUPPORTED, true);
        col.setDataStoreOnly(true);
        table.addComponent(col);

        table.addComponent(factory.newColumnConfig(DescriptorConstants.ColumnIdentifiers.INFO_ACTION, true));

        col = (JcaColumnConfig) factory.newColumnConfig(DescriptorConstants.ColumnIdentifiers.NM_ACTIONS, true);
        col.setComponentMode(ComponentMode.valueOf(changeWizardBean.getChangeMode()));
        col.setDescriptorProperty(DescriptorConstants.ActionProperties.ACTION_MODEL, affectedItems_row_actionModel);
        table.addComponent(col);

        table.addComponent(factory.newColumnConfig(DescriptorConstants.ColumnIdentifiers.NAME, true));
        ColumnConfig lcsColumn = factory.newColumnConfig("lifeCycleState", true);
        lcsColumn.setHidden(true);
        table.addComponent(factory.newColumnConfig("lifeCycleState", true));

        ColumnConfig lastModifiedColumn = factory.newColumnConfig(DescriptorConstants.ColumnIdentifiers.LAST_MODIFIED, true);
        lastModifiedColumn.setHidden(true);
        table.addComponent(lastModifiedColumn);

        ColumnConfig modifierNameColumn = factory.newColumnConfig("modifier.name", true);
        modifierNameColumn.setHidden(true);
        table.addComponent(modifierNameColumn);

        addDispositionColumns(factory, table, changeWizardBean);

        col = (JcaColumnConfig) factory.newColumnConfig(COMMENTS_COLUMN_ID, true);
        col.setComponentMode(ComponentMode.valueOf(changeWizardBean.getChangeMode()));
        col.setVariableHeight(true);
        table.addComponent(col);

        ColumnConfig changeIntentColumn = factory.newColumnConfig(CHANGE_INTENT_COLUMNN_ID, true);
        changeIntentColumn.setComponentMode(ComponentMode.valueOf(changeWizardBean.getChangeMode()));
        table.addComponent(changeIntentColumn);

        table.addComponent(factory.newColumnConfig("associatedAnnotations", messageChange2ClientResource.getMessage("ANNOTATION_SET_LABEL"), true));

        addDataColumns(table, factory);
        String supportedDispositions = ChangeLinkAttributeHelper.createDataFieldString(getDispositionComponentIds());
        params.setAttribute(PropertyDescriptorKeys.SUPPORTED_DISPOSITION_PROPERTIES, supportedDispositions);

        List<String> plugins = new ArrayList<String>(1);
        if (createOrEdit) {
		
			log.debug("flag=======start to config object picker config");
            JcaComponentParams jcacomponentparams = (JcaComponentParams)params;
            NmCommandBean nmcommandbean = jcacomponentparams.getHelperBean().getNmCommandBean();
        	NmOid nmoid = nmcommandbean.getActionOid();
        	WTObject obj = (WTObject)nmoid.getRefObject();
        	log.debug("obj==="+obj);
        	String state="";
        	if (obj instanceof WTChangeActivity2) {
				WTChangeActivity2 eca =(WTChangeActivity2)obj;
				state =eca.getState().toString();
			}
        	if (state.equalsIgnoreCase(ChangeState.REWORK)||state.equalsIgnoreCase(ChangeState.OPEN)) {
                 AffectedObjectsPickerConfig pickerConfig = new AffectedObjectsPickerConfig();
            table.setToolbarAutoSuggestPickerConfig(pickerConfig);
            plugins.add("changeTablePlugin");
			}
		
          
        }
        if(!plugins.isEmpty()) {
            table.setPtypes(plugins);
        }

        table.setView("/changetask/affectedItemsTable.jsp");
        table.setHelpContext(affectedItems_helpLink);
        if (log.isDebugEnabled()) {
            log.debug("Table id is :-- " + table.getId());
        }
        return table;

    }

    /**
     * Creates the diposition columns from the list of disposition component ids
     * returned from <code>getDispositionComponentIds</code>.
     * 
     * <BR>
     * <BR>
     * <B>Supported API: </B>false
     * 
     * @param factory
     * @param table
     * @param changeWizardBean
     */
    private void addDispositionColumns(ComponentConfigFactory factory, JcaTableConfig table, ChangeWizardBean changeWizardBean) {
        List<String> dispositionIds = getDispositionComponentIds();
        for (String id : dispositionIds) {
            JcaColumnConfig col = (JcaColumnConfig) factory.newColumnConfig(id, true);
            col.setComponentMode(ComponentMode.valueOf(changeWizardBean.getChangeMode()));
            table.addComponent(col);
        }
    }

    /**
     * Add additional property data to the table data. Currently supports the
     * following link attributes: <br>
     * <br>
     * <li>dispositions</li> 
     * <li>disposition comments</li>
     * 
     * <BR>
     * <BR>
     * <B>Supported API: </B>false
     * 
     * @param tableDataColumn
     */
    public void addSupportedDispositionProperty(JcaColumnConfig tableDataColumn) {
        List<String> dispositionIds = getDispositionComponentIds();
        StringBuilder dpStrBld = new StringBuilder(1024);
        for (String id : dispositionIds) {
            if (dpStrBld.length() > 0) {
                dpStrBld.append(",");
            }
            dpStrBld.append(id);
        }
        tableDataColumn.setDescriptorProperty(PropertyDescriptorKeys.SUPPORTED_DISPOSITION_PROPERTIES, dpStrBld.toString());
    }

    /**
     * Enables the disposition comments attribute on the set disposition picker.
     * The default is that the set disposition comments is enabled.
     * 
     * <BR>
     * <BR>
     * <B>Supported API: </B>true
     * 
     * @return true to enable the set disposition comments.
     */
    protected boolean enableDispositionComments() {
        return true;
    }

    /**
     * Returns the list of supported disposition component ids. The default
     * supported disposition component ids are:
     * 
     * <table border=1>
     * <thead>
     * <tr>
     * <th>Component Id</th>
     * <th>Attribute Name</th>
     * </tr>
     * </thead> <tbody>
     * <tr>
     * <td>onOrderDisposition</td>
     * <td>theOnOrderDisposition</td>
     * </tr>
     * <tr>
     * <td>inventoryDisposition</td>
     * <td>theInventoryDisposition</td>
     * </tr>
     * <tr>
     * <td>finishDisposition</td>
     * <td>theFinishDisposition</td>
     * </tr>
     * </tbody>
     * </table>
     * 
     * 
     * <BR>
     * <BR>
     * <B>Supported API: </B>true
     * 
     * @return list of disposition component ids
     */
    public List<String> getDispositionComponentIds() {
        List<String> ids = new ArrayList<String>();
        ids.add(ColumnIdentifiers.ON_ORDER_DISPOSITION);
        ids.add(ColumnIdentifiers.INVENTORY_DISPOSITION);
        ids.add(ColumnIdentifiers.FINISHED_DISPOSITION);
        if (enableDispositionComments()) {
            ids.add(ColumnIdentifiers.DISPOSITION_COMMENTS);
        }
        return ids;
    }

    /**
     * Creates the data fields for dispositions and annotations.
     * 
     * @param tableConfig
     * @param factory
     */
    public void addDataColumns(TableConfig tableConfig, ComponentConfigFactory factory) {
        List<String> dataColumns = new ArrayList<String>();
        List<String> dispositionIds = getDispositionComponentIds();
        if (!dispositionIds.isEmpty()) {
            dataColumns.add(PropertyDescriptorKeys.SUPPORTED_DISPOSITION_PROPERTIES);
        }
        dataColumns.addAll(dispositionIds);
        dataColumns.add(ChangeManagementDescriptorConstants.ColumnIdentifiers.ANNOTATION_DATA);
        for (String id : dataColumns) {
            String dataColumnId = (!id.endsWith(ChangeLinkAttributeHelper.DATA_POSTFIX)) ? id + ChangeLinkAttributeHelper.DATA_POSTFIX : id;
            ColumnConfig col = factory.newColumnConfig(dataColumnId, false);
            col.setDataStoreOnly(true);
            col.setDataUtilityId(id);
            if (PropertyDescriptorKeys.SUPPORTED_DISPOSITION_PROPERTIES.equals(id)) {
                addSupportedDispositionProperty((JcaColumnConfig) col);
                col.setDataUtilityId(TABLE_DATA_COLUMNID);
            } 
            tableConfig.addComponent(col);
        }
    }
}