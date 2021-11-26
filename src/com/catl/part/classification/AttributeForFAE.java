package com.catl.part.classification;

import com.ptc.windchill.annotations.metadata.GenAsEnumeratedType;

@GenAsEnumeratedType
public class AttributeForFAE extends _AttributeForFAE{
	
	private static final long serialVersionUID = 1L;

	public static final AttributeForFAE NONE = toAttributeForFAE("NONE");
	
	public static final AttributeForFAE CUSTOMIZED = toAttributeForFAE("CUSTOMIZED");
	
	public static final AttributeForFAE SOURCE = toAttributeForFAE("SOURCE");
}
