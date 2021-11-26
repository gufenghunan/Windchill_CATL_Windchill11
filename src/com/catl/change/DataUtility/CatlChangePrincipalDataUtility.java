package com.catl.change.DataUtility;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import wt.log4j.LogR;
import wt.util.WTException;

import com.ptc.core.components.descriptor.ComponentDescriptor;
import com.ptc.core.components.descriptor.ModelContext;
import com.ptc.core.components.factory.AbstractDataUtility;
import com.ptc.core.components.factory.dataUtilities.AttributeDataUtilityHelper;
import com.ptc.core.components.factory.dataUtilities.PrincipalDataUtility;
import com.ptc.core.components.rendering.PickerRenderConfigs;
import com.ptc.core.components.rendering.guicomponents.PickerInputComponent;
import com.ptc.core.ui.resources.ComponentMode;
import com.ptc.windchill.enterprise.change2.ChangeManagementClientHelper;
import com.ptc.windchill.enterprise.change2.dataUtilities.ChangePickerDataUtility;
import com.ptc.windchill.enterprise.change2.dataUtilities.ChangeTaskRoleParticipantDataUtility;

public class CatlChangePrincipalDataUtility extends AbstractDataUtility
{

    public CatlChangePrincipalDataUtility()
    {
        mode = ComponentMode.VIEW;
    }

    public void setModelData(String s, List list, ModelContext modelcontext)
        throws WTException
    {
        mode = ChangeManagementClientHelper.getMode(modelcontext);
        if("ROLE_REVIEWER".equals(s) || "ROLE_ASSIGNEE".equals(s) || "varianceOwner".equals(s))
            if(ComponentMode.CREATE.equals(mode) || ComponentMode.EDIT.equals(mode))
            {
                picker = new ChangePickerDataUtility();
                picker.setModelData(s, list, modelcontext);
            } else
            if("ROLE_REVIEWER".equals(s) || "ROLE_ASSIGNEE".equals(s))
            {
                changeTaskDU = new ChangeTaskRoleParticipantDataUtility();
                changeTaskDU.setModelData(s, list, modelcontext);
            } else
            {
                pdu = new PrincipalDataUtility();
                pdu.setModelData(s, list, modelcontext);
            }
    }

    public Object getDataValue(String s, Object obj, ModelContext modelcontext)
        throws WTException
    {
        Object obj1 = null;
        if("ROLE_REVIEWER".equals(s) || "ROLE_ASSIGNEE".equals(s) || "varianceOwner".equals(s))
            if(ComponentMode.CREATE.equals(mode) || ComponentMode.EDIT.equals(mode))
                obj1 = getDataValueNoDefaultValue(s, obj1, modelcontext);
            else
            if("ROLE_REVIEWER".equals(s) || "ROLE_ASSIGNEE".equals(s))
                obj1 = changeTaskDU.getDataValue(s, obj, modelcontext);
            else
                obj1 = pdu.getDataValue(s, obj, modelcontext);
        logger.debug("obj1===="+obj1);
        return obj1;
    }
    public Object getDataValueNoDefaultValue(String s, Object obj, ModelContext modelcontext)
            throws WTException
        {
            ComponentDescriptor componentdescriptor = modelcontext.getDescriptor();
            Map map = componentdescriptor.getProperties();
            ComponentMode componentmode = modelcontext.getDescriptorMode();
            if(componentmode == ComponentMode.CREATE)
            {
            map.put("defaultValue", "");
            }
            validatePickerConfigs(map);
           
            Object obj1 = modelcontext.getRawValue();

            PickerInputComponent pickerinputcomponent = null;
            String s1 = getLabel(s, modelcontext);
            if(s1 == null)
                s1 = "Picker";
            if(componentmode == ComponentMode.CREATE || componentmode == ComponentMode.EDIT)
            {
               PickerRenderConfigs.setDefaultPickerProperty(map, "pickerCallback", "CreateEditPickerInputComponentCallback_UpdateOnly");
               PickerRenderConfigs.setDefaultPickerProperty(map, "includeTypeInstanceId", "true");
            }
            pickerinputcomponent = new PickerInputComponent(s1, (String)obj1, PickerRenderConfigs.getPickerConfigs(map));
            pickerinputcomponent.setColumnName(AttributeDataUtilityHelper.getColumnName(s, obj, modelcontext));
            pickerinputcomponent.setRequired(AttributeDataUtilityHelper.isInputRequired(modelcontext));
            return pickerinputcomponent;
        }
    
    public void validatePickerConfigs(Map map)
            throws WTException
        {
            StringBuffer stringbuffer = new StringBuffer();
            if(map == null || map.size() == 0)
            {
                stringbuffer.append("pickerId, objectType");
                throw new WTException((new StringBuilder()).append("Picker Configuration Exception:: Please provide the following missing parameters for this picker. ").append(stringbuffer).toString());
            }
            String s = (String)map.get("pickerId");
            if(s == null || s.trim().equals(""))
                stringbuffer.append("pickerId");
            s = (String)map.get("objectType");
            if(s == null || s.trim().equals(""))
                stringbuffer.append("objectType");
            if(stringbuffer.length() > 0)
                throw new WTException((new StringBuilder()).append("Picker Configuration Exception:: Please provide the following missing parameters for this picker. ").append(stringbuffer).toString());
            else
                return;
        }
    
    public String getLabel(String s, ModelContext modelcontext)
        throws WTException
    {
        String s1 = null;
        if("ROLE_REVIEWER".equals(s) || "ROLE_ASSIGNEE".equals(s) || "varianceOwner".equals(s))
            if(ComponentMode.CREATE.equals(mode) || ComponentMode.EDIT.equals(mode))
                s1 = picker.getLabel(s, modelcontext);
            else
            if("ROLE_REVIEWER".equals(s) || "ROLE_ASSIGNEE".equals(s))
                s1 = changeTaskDU.getLabel(s, modelcontext);
            else
                s1 = pdu.getLabel(s, modelcontext);
        return s1;
    }

    static final Logger logger = LogR.getLogger(CatlChangePrincipalDataUtility.class.getName());
    private static final String VARIANCE_OWNER = "varianceOwner";
    private static final String ASSIGNEE = "ROLE_ASSIGNEE";
    private static final String REVIEWER = "ROLE_REVIEWER";
    ComponentMode mode;
    ChangeTaskRoleParticipantDataUtility changeTaskDU;
    ChangePickerDataUtility picker;
    PrincipalDataUtility pdu;

}
