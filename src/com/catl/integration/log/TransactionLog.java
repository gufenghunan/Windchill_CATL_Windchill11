package com.catl.integration.log;

import com.ptc.windchill.annotations.metadata.ColumnProperties;
import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;

import wt.fc.WTObject;
import wt.util.WTException;

@GenAsPersistable(superClass=WTObject.class,

properties={
    @GeneratedProperty(name="action",type=String.class),
    @GeneratedProperty(name="partNumber",type=String.class),
    @GeneratedProperty(name="childPartNumber",type=String.class),
    @GeneratedProperty(name="substitutePartNumber",type=String.class),
    @GeneratedProperty(name="ecnNumber",type=String.class),
    @GeneratedProperty(name="drawingNumber",type=String.class),
    @GeneratedProperty(name="drawingVersion",type=String.class),
    @GeneratedProperty(name="quantity",type=String.class),
    @GeneratedProperty(name="ecnName",type=String.class,
            constraints=@PropertyConstraints(upperLimit=4000)),
    @GeneratedProperty(name="description",type=String.class,
            constraints=@PropertyConstraints(upperLimit=4000)),
    @GeneratedProperty(name="validDate",type=String.class),
    @GeneratedProperty(name="partName",type=String.class),
    @GeneratedProperty(name="materialGroup",type=String.class),
    @GeneratedProperty(name="defaultUnit",type=String.class),
    @GeneratedProperty(name="specification",type=String.class,
            constraints=@PropertyConstraints(upperLimit=1000)),
    @GeneratedProperty(name="englishName",type=String.class),
    @GeneratedProperty(name="source",type=String.class),
    @GeneratedProperty(name="creator",type=String.class),
    @GeneratedProperty(name="standardVoltage",type=String.class),
    @GeneratedProperty(name="productEnergy",type=String.class),
    @GeneratedProperty(name="cellVolume",type=String.class),
    @GeneratedProperty(name="fullVoltage",type=String.class),
    @GeneratedProperty(name="model",type=String.class),
    @GeneratedProperty(name="versionBig",type=String.class),
    @GeneratedProperty(name="versionSmall",type=String.class),
    @GeneratedProperty(name="oid",type=String.class),
    @GeneratedProperty(name="oldPartNumber",type=String.class),
    @GeneratedProperty(name="cellMode",type=String.class),
    @GeneratedProperty(name="str1",type=String.class),
    @GeneratedProperty(name="str2",type=String.class),
    @GeneratedProperty(name="str3",type=String.class),
    @GeneratedProperty(name="str4",type=String.class),
    @GeneratedProperty(name="str5",type=String.class),
    @GeneratedProperty(name="str6",type=String.class),
    @GeneratedProperty(name="str7",type=String.class),
    @GeneratedProperty(name="str8",type=String.class),
    @GeneratedProperty(name="str9",type=String.class),
    @GeneratedProperty(name="str10",type=String.class),
    @GeneratedProperty(name="str11",type=String.class),
    @GeneratedProperty(name="str12",type=String.class),
}
)

public class TransactionLog extends _TransactionLog{
    static final long serialVersionUID = 1;
    public static TransactionLog newTransactionLog() throws WTException {
        final TransactionLog instance = new TransactionLog();
        instance.initialize();
        return instance;
     }

}
