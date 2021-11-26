package com.catl.change.inventory;

import java.sql.Timestamp;

import wt.change2.WTChangeActivity2;
import wt.fc.ObjectToObjectLink;
import wt.fc.Persistable;
import wt.part.WTPart;

import com.ptc.windchill.annotations.metadata.GenAsBinaryLink;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.GeneratedRole;

@GenAsBinaryLink(
		superClass=ObjectToObjectLink.class,
		roleA=@GeneratedRole(name="eca", type=WTChangeActivity2.class),
		roleB=@GeneratedRole(name="part",type=WTPart.class),
		properties={
			@GeneratedProperty(name="quantity",type=Double.class),
			@GeneratedProperty(name="materialStatus",type=MaterialStatus.class),
			@GeneratedProperty(name="dispositionOption",type=DispositionOption.class),
			@GeneratedProperty(name="owner",type=String.class),
			@GeneratedProperty(name="dueDay",type=Timestamp.class),
			@GeneratedProperty(name="remarks",type=String.class),
		}
			
)


public class ECAPartLink extends _ECAPartLink{
	
	static final long serialVersionUID = 1;
	public static ECAPartLink newECAPartLink(final Persistable eca, final Persistable part) throws Exception{
		ECAPartLink instance = new ECAPartLink();
		instance.initialize(eca, part);
		return instance;
	}

}
