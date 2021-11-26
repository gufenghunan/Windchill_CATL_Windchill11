package com.catl.part.sourceChange;

import wt.fc.ObjectReference;
import wt.fc.WTObject;
import wt.org.WTPrincipalReference;
import wt.part.WTPartMaster;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.ptc.windchill.annotations.metadata.*;

@GenAsPersistable(superClass = WTObject.class,
	properties = {
		
		//物料成熟度属性的变更历史记录
		//version
		@GeneratedProperty(name = "version", type = String.class,constraints = @PropertyConstraints(upperLimit=250)),
		//operator
		@GeneratedProperty(name = "operator", type = WTPrincipalReference.class),
		@GeneratedProperty(name = "oldSource", type = String.class,constraints = @PropertyConstraints(upperLimit=250)),
		@GeneratedProperty(name = "newSource", type = String.class,constraints = @PropertyConstraints(upperLimit=250)),
		@GeneratedProperty(name = "oldFAE", type = String.class,constraints = @PropertyConstraints(upperLimit=250)),
		@GeneratedProperty(name = "newFAE", type = String.class,constraints = @PropertyConstraints(upperLimit=250)),
		@GeneratedProperty(name = "changeReason", type = String.class,constraints = @PropertyConstraints(upperLimit=1000)),
		//partMaster
		@GeneratedProperty(name = "partMaster", type = ObjectReference.class)
	}
)

public class PartSourceChangeLog extends _PartSourceChangeLog {
	private static final long serialVersionUID = 1L;
	public static PartSourceChangeLog newPart(final WTPartMaster partmaster) throws WTException, WTPropertyVetoException{
		final PartSourceChangeLog partlog = new PartSourceChangeLog();
		partlog.initialize();
		partlog.setPartMaster(ObjectReference.newObjectReference(partmaster));
		return partlog;
	}
}
