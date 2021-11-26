package com.catl.integration.rdm.bean;



import wt.util.WTException;

public class RDMWorkflowBean
{
    public final static String COLUMN_ID_NAME = "name";
    public final static String COLUMN_ID_STATE = "state";
    public final static String COLUMN_ID_TYPENAME = "typeName";
    public final static String COLUNM_ID_DEPARTMENT = "department";
    public final static String COLUNM_ID_PROJECT = "project";
    public final static String COLUNM_ID_CODE = "code";
    
    private String name;
    private String number;
    public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	private String state;
    private String externalURL;
    private String typeName;
    private String department;
    private String project;
    private String code;
    
    public RDMWorkflowBean(){
    	
    }

    public RDMWorkflowBean(String name, String state, String url) throws WTException
    {
        setName(name);
        setState(state);
        setExternalURL(url);
    }

	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
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


}
