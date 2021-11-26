package com.catl.report;

import com.catl.common.util.GenericUtil;
import com.ptc.core.lwc.server.PersistableAdapter;

import wt.fc.ObjectIdentifier;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.WTObject;
import wt.util.WTException;

public class MultiValuedAttribute {
    
    public static String getAttributes(String refStr) throws WTException {
        System.out.println(" Enter into getAttributes()");
        WTObject wtObject = (WTObject)GenericUtil.getInstance(refStr);
        PersistableAdapter  obj = new com.ptc.core.lwc.server.PersistableAdapter(wtObject,null,
                                java.util.Locale.CHINA, new com.ptc.core.meta.common.DisplayOperationIdentifier());
        obj.load("changeType");
        java.lang.String string_value = "";
        java.lang.String string_value1 = "";
        java.lang.String string_value2 = "";
       Object valueObject=obj.get("changeType");
        if(!(valueObject.getClass().equals(String.class))){
                Object values[]=(Object[])valueObject;
                for(int i=0;i<values.length;i++)
                {
                      System.out.println("***********************"+values[i]+"***************");
                      if(i<values.length-1)  {
                          string_value=(String) values[i];
                          string_value1=string_value1+string_value+",";
                      }
                      else {
                            string_value=(String) values[i];
                            string_value1=string_value1+string_value;
                        }
                      string_value2= string_value1;
                }//end for 
        } else {
              string_value=(String) valueObject;
              string_value2 = string_value;
        }
        System.out.println("***********************"+string_value2+"***************");
         return string_value2;
    }
}