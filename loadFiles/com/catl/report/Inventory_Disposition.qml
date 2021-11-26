<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE qml SYSTEM "/wt/query/qml/qml.dtd">
<qml>
    <statement>
        <query>
            <select>
                <object
                    alias="Change Task (wt.change2.WTChangeActivity2)"
                    heading="更改任务" propertyName=""/>
                <object alias="Part" heading="物料" propertyName=""/>
                <column alias="com.catl.change.inventory.ECAPartLink"
                    heading="物料状态" propertyName="materialStatus" type="com.catl.change.inventory.MaterialStatus">materialStatus</column>
                <column alias="com.catl.change.inventory.ECAPartLink"
                    heading="所有者" propertyName="owner" type="java.lang.String">owner</column>
                <column alias="com.catl.change.inventory.ECAPartLink"
                    heading="数量" propertyName="quantity" type="java.lang.Double">quantity</column>
                <column alias="com.catl.change.inventory.ECAPartLink"
                    heading="备注" propertyName="remarks" type="java.lang.String">remarks</column>
                <column alias="com.catl.change.inventory.ECAPartLink"
                    heading="处理意见" propertyName="dispositionOption" type="com.catl.change.inventory.DispositionOption">dispositionOption</column>
            </select>
            <from>
                <table alias="com.catl.change.inventory.ECAPartLink">com.catl.change.inventory.ECAPartLink</table>
                <table alias="Change Task (wt.change2.WTChangeActivity2)">wt.change2.WTChangeActivity2</table>
                <table alias="Part">wt.part.WTPart</table>
            </from>
            <where>
                <compositeCondition type="and">
                    <condition>
                        <operand>
                            <column
                                alias="Change Task (wt.change2.WTChangeActivity2)"
                                heading="Persist Info.Object Identifier.Id"
                                propertyName="persistInfo.objectIdentifier.id" type="long">thePersistInfo.theObjectIdentifier.id</column>
                        </operand>
                        <operator type="equal"/>
                        <operand>
                            <column
                                alias="com.catl.change.inventory.ECAPartLink"
                                heading="roleAObjectRef.key.id" type="long">roleAObjectRef.key.id</column>
                        </operand>
                    </condition>
                    <condition>
                        <operand>
                            <column alias="Part"
                                heading="Persist Info.Object Identifier.Id"
                                propertyName="persistInfo.objectIdentifier.id" type="long">thePersistInfo.theObjectIdentifier.id</column>
                        </operand>
                        <operator type="equal"/>
                        <operand>
                            <column
                                alias="com.catl.change.inventory.ECAPartLink"
                                heading="roleBObjectRef.key.id" type="long">roleBObjectRef.key.id</column>
                        </operand>
                    </condition>
                </compositeCondition>
            </where>
        </query>
    </statement>
</qml>
