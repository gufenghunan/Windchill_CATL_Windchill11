package com.catl.line.exception;

public class LineException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private String errorCode;
    private boolean propertiesKey = true;
    public LineException(String message)
    {
        super(message);
    }
    public LineException(String errorCode, String message)
    {
        this(errorCode, message, true);
    }
    public LineException(String errorCode, String message, Throwable cause)
    {
        this(errorCode, message, cause, true);
    }
    public LineException(String errorCode, String message, boolean propertiesKey)
    {
        super(message);
        this.setErrorCode(errorCode);
        this.setPropertiesKey(propertiesKey);
    }
    public LineException(String errorCode, String message, Throwable cause, boolean propertiesKey)
    {
        super(message, cause);
        this.setErrorCode(errorCode);
        this.setPropertiesKey(propertiesKey);
    }
    public LineException(String message, Throwable cause)
    {
        super(message, cause);
    }
    
    public String getErrorCode()
    {
        return errorCode;
    }

    public void setErrorCode(String errorCode)
    {
        this.errorCode = errorCode;
    }

    public boolean isPropertiesKey()
    {
        return propertiesKey;
    }

    public void setPropertiesKey(boolean propertiesKey)
    {
        this.propertiesKey = propertiesKey;
    }
    
}