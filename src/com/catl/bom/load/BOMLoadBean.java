package com.catl.bom.load;

import wt.part.WTPart;

public class BOMLoadBean
{
	String number, name, quantity, referenceDesignatorRange;
	WTPart part = null;

	public WTPart getPart()
	{
		return part;
	}

	public void setPart(WTPart part)
	{
		this.part = part;
	}

	public BOMLoadBean()
	{
		number = "";
		name = "";
		quantity = "";
		referenceDesignatorRange = "";
	}

	public BOMLoadBean(String number2, String name2, String quantity2, String referenceDesignator)
	{
		number = number2;
		name = name2;
		quantity = quantity2;
		referenceDesignatorRange = referenceDesignator;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof BOMLoadBean)
		{
			BOMLoadBean bean = (BOMLoadBean) obj;
			String number = bean.getNumber();
			String name = bean.getName();
			String quantity = bean.getQuantity();
			String referenceDesignatorRange = bean.getReferenceDesignatorRange();
			if (number.equals(this.number) && name.equals(this.name) && quantity.equals(this.quantity) && referenceDesignatorRange.equals(this.referenceDesignatorRange))
				return true;
		}
		return false;
	}

	public String getNumber()
	{
		return number == null ? "" : number;
	}

	public void setNumber(String number)
	{
		this.number = number;
	}

	public String getName()
	{
		return name == null ? "" : name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getQuantity()
	{
		return quantity == null ? "" : quantity;
	}

	public void setQuantity(String quantity)
	{
		this.quantity = quantity;
	}

	public String getReferenceDesignatorRange()
	{
		return referenceDesignatorRange == null ? "" : referenceDesignatorRange;
	}

	public void setReferenceDesignatorRange(String referenceDesignatorRange)
	{
		this.referenceDesignatorRange = referenceDesignatorRange;
	}

}
