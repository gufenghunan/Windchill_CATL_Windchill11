package com.catl.part.relation;


import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ptc.mvc.components.ColumnConfig;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.OverrideComponentBuilder;
import com.ptc.mvc.components.TableConfig;
import com.ptc.windchill.enterprise.part.mvc.builders.ReplacementsSubstituteTableBuilder;

import wt.log4j.LogR;
import wt.util.WTException;

@OverrideComponentBuilder
@ComponentBuilder({"relatedObjects.replacementsSubstituteTableBuilder"})
public class ReplacementsSubstituteTableBuilderCATL extends ReplacementsSubstituteTableBuilder
{
    private static final Logger log = LogR.getLogger(ReplacementsSubstituteTableBuilderCATL.class.getName());
  @Override
  public ComponentConfig buildComponentConfig(ComponentParams paramComponentParams)throws WTException{
      TableConfig config = (TableConfig)super.buildComponentConfig(paramComponentParams);
      //log.debug("size="+config.getComponents().size());
      List<ComponentConfig> old = config.getComponents();
      List<ComponentConfig> newCC = new ArrayList<ComponentConfig>();
      for(ComponentConfig cc: old){
          if(cc.getId().equals("substituteReferenceDesignator")){
              newCC.add(cc);
          }else if(cc.getId().equals("substituteUnit")){
              ColumnConfig colC = (ColumnConfig)cc;
              colC.setDataUtilityId("ReplacementsSubstituteDataUtility");
          }
      }
      for(ComponentConfig cc : newCC){
          config.removeComponent(cc);
      }
      return config;
  }
    @Override
    public Object buildComponentData(ComponentConfig config,
            ComponentParams params) throws Exception {
        return super.buildComponentData(config, params);
    }
}