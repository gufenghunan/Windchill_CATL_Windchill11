package com.catl.part.maturity;

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
		//oldMaturity
		@GeneratedProperty(name = "oldMaturity", type = String.class,constraints = @PropertyConstraints(upperLimit=250)),
		//newMaturity
		@GeneratedProperty(name = "newMaturity", type = String.class,constraints = @PropertyConstraints(upperLimit=250)),
		//partMaster
		@GeneratedProperty(name = "partMaster", type = ObjectReference.class)
	}
)

public class PartMaturityChangeLog extends _PartMaturityChangeLog {
	private static final long serialVersionUID = 1L;
	public static PartMaturityChangeLog newPart(final WTPartMaster partmaster) throws WTException, WTPropertyVetoException{
		final PartMaturityChangeLog partlog = new PartMaturityChangeLog();
		partlog.initialize();
		partlog.setPartMaster(ObjectReference.newObjectReference(partmaster));
		return partlog;
	}
}
