package com.catl.integration.rdm;

import wt.fc.WTObject;

import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;

@GenAsPersistable(superClass=WTObject.class,
properties={
	@GeneratedProperty(name="objectType",type=String.class),
    @GeneratedProperty(name="objectNumber",type=String.class),
    @GeneratedProperty(name="branchId",type=long.class),
    @GeneratedProperty(name="deliverableId",type=String.class)
}
)
public class ObjectLinkedByRdm extends _ObjectLinkedByRdm{
	private static final long serialVersionUID = 1L;
}
