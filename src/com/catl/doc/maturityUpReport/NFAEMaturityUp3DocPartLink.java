package com.catl.doc.maturityUpReport;

import com.ptc.windchill.annotations.metadata.Cardinality;
import com.ptc.windchill.annotations.metadata.GenAsBinaryLink;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.GeneratedRole;
import com.ptc.windchill.annotations.metadata.SupportedAPI;

import wt.doc.WTDocumentMaster;
import wt.fc.ObjectReference;
import wt.fc.ObjectToObjectLink;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

@GenAsBinaryLink(
		superClass=ObjectToObjectLink.class,
		roleA=@GeneratedRole(name="docMaster", type=WTDocumentMaster.class,supportedAPI=SupportedAPI.PUBLIC, cardinality=Cardinality.ONE_TO_MANY),
		roleB=@GeneratedRole(name="partMaster", type=WTPartMaster.class,supportedAPI=SupportedAPI.PUBLIC, cardinality=Cardinality.ONE, owner=false),
		properties={
				 @GeneratedProperty(name="initialPart",type=ObjectReference.class)
		}
)
public class NFAEMaturityUp3DocPartLink extends _NFAEMaturityUp3DocPartLink {
	private static final long serialVersionUID = 1L;
	public static NFAEMaturityUp3DocPartLink newLink(final WTDocumentMaster docMaster, final WTPart part) throws WTException, WTPropertyVetoException{
		final NFAEMaturityUp3DocPartLink link = new NFAEMaturityUp3DocPartLink();
		link.initialize(docMaster, part.getMaster());
		link.setInitialPart(ObjectReference.newObjectReference(part));
		return link;
	}
}
