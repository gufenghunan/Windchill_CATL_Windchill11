package com.catl.integration.rdm.bean;


import wt.util.WTException;

public class RDMTaskBean
{
    public final static String COLUMN_ID_NAME = "name";
    public final static String COLUMN_ID_STATE = "state";
    public final static String COLUMN_ID_SDATE = "planStartDate";
    public final static String COLUNM_ID_EDATE = "planEndDate";
    public final static String COLUNM_ID_OBJECT = "object";
    
    private String name;
    private String number;
    private String state;
    private String externalURL;
    private String planStartDate;
    private String planEndDate;
    private String object;
    
    public RDMTaskBean(){
    	
    }

    public RDMTaskBean(String name, String state, String url) throws WTException
    {
        setName(name);
        setState(state);
        setExternalURL(url);
    }
    
    public String getPlanStartDate() {
		return planStartDate;
	}

	public void setPlanStartDate(String planStartDate) {
		this.planStartDate = planStartDate;
	}

	public String getPlanEndDate() {
		return planEndDate;
	}

	public void setPlanEndDate(String planEndDate) {
		this.planEndDate = planEndDate;
	}

	public String getExternalURL() {
		return externalURL;
	}


	public void setExternalURL(String externalURL) {
		this.externalURL = externalURL;
	}


	public String getState()
    {
        return state;
    }

    public void setState(String state)
    {
        this.state = state;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }



    public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

	
}
