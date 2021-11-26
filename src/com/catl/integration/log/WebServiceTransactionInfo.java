package com.catl.integration.log;

/**
 * Web Service的传入、返回参数等信息
 * 
 * @author lizhou
 *
 */
public class WebServiceTransactionInfo {

	/**
	 * Web Service的传入参数
	 */
	private Object parameterObject;

	/**
	 * Web Service的返回结果对象
	 */
	private Object resultObject;

	/**
	 * Web Service调用过程中发生的异常信息
	 */
	private Throwable exception;

	public Object getParameterObject() {
		return parameterObject;
	}

	public void setParameterObject(Object parameterObject) {
		this.parameterObject = parameterObject;
	}

	public Object getResultObject() {
		return resultObject;
	}

	public void setResultObject(Object resultObject) {
		this.resultObject = resultObject;
	}

	public Throwable getException() {
		return exception;
	}

	public void setException(Throwable exception) {
		this.exception = exception;
	}

}
