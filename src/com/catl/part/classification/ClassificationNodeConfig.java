package com.catl.part.classification;

import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;

import wt.fc.WTObject;

@GenAsPersistable(superClass = WTObject.class,
properties = {
		@GeneratedProperty(name="nodeId",type=Long.class),
		@GeneratedProperty(name="nodeInternalName",type=String.class,constraints = @PropertyConstraints(upperLimit=250)),
		@GeneratedProperty(name="needFae",type=Boolean.class,initialValue="false"),
		@GeneratedProperty(name="attributeRef",type=AttributeForFAE.class, initialValue="AttributeForFAE.getAttributeForFAEDefault()"),
		@GeneratedProperty(name="makeNeedFae",type=Boolean.class,initialValue="false"),
		@GeneratedProperty(name="buyNeedFae",type=Boolean.class,initialValue="false"),
		@GeneratedProperty(name="makeBuyNeedFae",type=Boolean.class,initialValue="false"),
		@GeneratedProperty(name="customerNeedFae",type=Boolean.class,initialValue="false"),
		@GeneratedProperty(name="virtualNeedFae",type=Boolean.class,initialValue="false"),
		@GeneratedProperty(name="needNonFaeReport",type=Boolean.class, initialValue="true")
})
public class ClassificationNodeConfig extends _ClassificationNodeConfig {
	private static final long serialVersionUID = 1L;
}
