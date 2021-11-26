package com.catl.part.platformChange;

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
		@GeneratedProperty(name = "oldPlatform", type = String.class,constraints = @PropertyConstraints(upperLimit=250)),
		@GeneratedProperty(name = "newPlatform", type = String.class,constraints = @PropertyConstraints(upperLimit=250)),
		@GeneratedProperty(name = "changeReason", type = String.class,constraints = @PropertyConstraints(upperLimit=1000)),
		//partMaster
		@GeneratedProperty(name = "partMaster", type = ObjectReference.class)
	}
)

public class PartPlatformChangeLog extends _PartPlatformChangeLog {
	private static final long serialVersionUID = 1L;
	public static PartPlatformChangeLog newPart(final WTPartMaster partmaster) throws WTException, WTPropertyVetoException{
		final PartPlatformChangeLog partlog = new PartPlatformChangeLog();
		partlog.initialize();
		partlog.setPartMaster(ObjectReference.newObjectReference(partmaster));
		return partlog;
	}
}
