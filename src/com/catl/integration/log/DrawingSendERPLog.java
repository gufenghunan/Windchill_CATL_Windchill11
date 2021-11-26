package com.catl.integration.log;

import com.ptc.windchill.annotations.metadata.ColumnProperties;
import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;

import wt.fc.WTObject;
import wt.util.WTException;

@GenAsPersistable(superClass=WTObject.class,
properties={
	@GeneratedProperty(name="oid",type=String.class),
    @GeneratedProperty(name="objectInPromotionNumber",type=String.class),
    @GeneratedProperty(name="objectInPromotionType",type=String.class),
    @GeneratedProperty(name="objectInPromotionVersion",type=String.class),
    @GeneratedProperty(name="objectInPromotionIteration",type=String.class),
    @GeneratedProperty(name="relationObjectNumber",type=String.class),
    @GeneratedProperty(name="relationObjectType",type=String.class),
    @GeneratedProperty(name="partNumber",type=String.class),
    @GeneratedProperty(name="partVersion",type=String.class),
    @GeneratedProperty(name="partIteration",type=String.class),
    @GeneratedProperty(name="rootPath",type=String.class),
    @GeneratedProperty(name="fileName",type=String.class)
}
)

public class DrawingSendERPLog extends _DrawingSendERPLog{
    static final long serialVersionUID = 1;
    public static DrawingSendERPLog newDrawingSendERPLog() throws WTException {
        final DrawingSendERPLog instance = new DrawingSendERPLog();
        instance.initialize();
        return instance;
     }

}
