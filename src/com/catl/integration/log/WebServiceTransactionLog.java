package com.catl.integration.log;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.sql.Timestamp;
import java.sql.Types;

import com.ptc.xworks.xmlobject.BaseXmlObject;
import com.ptc.xworks.xmlobject.annotation.XmlObjectColumnMarker;
import com.ptc.xworks.xmlobject.annotation.XmlObjectMarker;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

@XmlObjectMarker(tableName = "A_WS_TRANSCATION_LOGS", xmlColumn = "XML_CONTENT", columns = {
		@XmlObjectColumnMarker(attribute = "serviceType", columnName = "WS_TYPE", javaType = String.class, sqlType = Types.VARCHAR),
		@XmlObjectColumnMarker(attribute = "serviceSide", columnName = "SERVICE_SIDE", javaType = String.class, sqlType = Types.VARCHAR),
		@XmlObjectColumnMarker(attribute = "clientSide", columnName = "CLIENT_SIDE", javaType = String.class, sqlType = Types.VARCHAR),
		@XmlObjectColumnMarker(attribute = "clientId", columnName = "CLIENT_ID", javaType = String.class, sqlType = Types.VARCHAR),
		@XmlObjectColumnMarker(attribute = "serviceClass", columnName = "SERVICE_CLASS", javaType = String.class, sqlType = Types.VARCHAR),
		@XmlObjectColumnMarker(attribute = "serviceMethod", columnName = "SERVICE_METHOD", javaType = String.class, sqlType = Types.VARCHAR),
		@XmlObjectColumnMarker(attribute = "clientClass", columnName = "CLIENT_CLASS", javaType = String.class, sqlType = Types.VARCHAR),
		@XmlObjectColumnMarker(attribute = "clientMethod", columnName = "CLIENT_METHOD", javaType = String.class, sqlType = Types.VARCHAR),
		@XmlObjectColumnMarker(attribute = "startTime", columnName = "START_TIME", javaType = Timestamp.class, sqlType = Types.VARCHAR),
		@XmlObjectColumnMarker(attribute = "finishTime", columnName = "FINISH_TIME", javaType = Timestamp.class, sqlType = Types.TIMESTAMP),
		@XmlObjectColumnMarker(attribute = "status", columnName = "STATUS", javaType = String.class, sqlType = Types.TIMESTAMP)

})
public class WebServiceTransactionLog extends BaseXmlObject {

	public static final String SERVICE_TYPE_PROVIDER = "PROVIDER";

	public static final String SERVICE_TYPE_CLIENT = "CLIENT";

	public static final String STATUS_SUCCESS = "S";

	public static final String STATUS_FAILED = "F";

	static final long serialVersionUID = 1L;

	private static final long EXTERNALIZATION_VERSION_UID = 3605779903239640929L;

	/**
	 * Web Service交易的类型，是服务提供者还是服务客户端，可选择值为PROVIDER|CLIENT
	 */
	@XStreamOmitField
	private String serviceType;

	/**
	 * Web Service的服务端系统是什么，可选择的值为PLM|RDM|ERP
	 */
	@XStreamOmitField
	private String serviceSide;

	/**
	 * Web Service的客户端系统是什么，可选择的值为PLM|RDM|ERP
	 */
	@XStreamOmitField
	private String clientSide;

	/**
	 * Web Service的客户端所使用的账号
	 */
	@XStreamOmitField
	private String clientId;

	/**
	 * 如果PLM是服务端，则记录Web Service的实现类class
	 */
	@XStreamOmitField
	private String serviceClass;

	/**
	 * 如果PLM是服务端，则记录Web Service的实现类java method
	 */
	@XStreamOmitField
	private String serviceMethod;

	/**
	 * 如果PLM是客户端端，则记录Web Service的实现类
	 */
	@XStreamOmitField
	private String clientClass;

	/**
	 * 如果PLM是客户端端，则记录Web Service的实现类java method
	 */
	@XStreamOmitField
	private String clientMethod;

	/**
	 * Web Service调用开始的时间
	 */
	@XStreamOmitField
	private Timestamp startTime;

	/**
	 * Web Service调用完成的时间
	 */
	@XStreamOmitField
	private Timestamp finishTime;

	/**
	 * Web Service调用的状态，可选值为S|F
	 */
	@XStreamOmitField
	private String status;

	/**
	 * Web Service的交易的传入和返回参数以及异常信息等
	 */
	private WebServiceTransactionInfo transactionInfo;

	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	public String getServiceSide() {
		return serviceSide;
	}

	public void setServiceSide(String serviceSide) {
		this.serviceSide = serviceSide;
	}

	public String getClientSide() {
		return clientSide;
	}

	public void setClientSide(String clientSide) {
		this.clientSide = clientSide;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getServiceClass() {
		return serviceClass;
	}

	public void setServiceClass(String serviceClass) {
		this.serviceClass = serviceClass;
	}

	public String getServiceMethod() {
		return serviceMethod;
	}

	public void setServiceMethod(String serviceMethod) {
		this.serviceMethod = serviceMethod;
	}

	public String getClientClass() {
		return clientClass;
	}

	public void setClientClass(String clientClass) {
		this.clientClass = clientClass;
	}

	public String getClientMethod() {
		return clientMethod;
	}

	public void setClientMethod(String clientMethod) {
		this.clientMethod = clientMethod;
	}

	public Timestamp getStartTime() {
		return startTime;
	}

	public void setStartTime(Timestamp startTime) {
		this.startTime = startTime;
	}

	public Timestamp getFinishTime() {
		return finishTime;
	}

	public void setFinishTime(Timestamp finishTime) {
		this.finishTime = finishTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public WebServiceTransactionInfo getTransactionInfo() {
		return transactionInfo;
	}

	public void setTransactionInfo(WebServiceTransactionInfo transactionInfo) {
		this.transactionInfo = transactionInfo;
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		long versionId = in.readLong();
		if (versionId != EXTERNALIZATION_VERSION_UID) {
			throw new IOException("Invalid EXTERNALIZATION_VERSION_UID id:" + versionId);
		}
		super.readExternal(in);
		this.serviceType = (String) in.readObject();
		this.serviceSide = (String) in.readObject();
		this.clientSide = (String) in.readObject();
		this.clientId = (String) in.readObject();
		this.serviceClass = (String) in.readObject();
		this.serviceMethod = (String) in.readObject();
		this.clientClass = (String) in.readObject();
		this.clientMethod = (String) in.readObject();
		this.startTime = (Timestamp) in.readObject();
		this.finishTime = (Timestamp) in.readObject();
		this.status = (String) in.readObject();
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeLong(EXTERNALIZATION_VERSION_UID);
		super.writeExternal(out);
		out.writeObject(this.serviceType);
		out.writeObject(this.serviceSide);
		out.writeObject(this.clientSide);
		out.writeObject(this.clientId);
		out.writeObject(this.serviceClass);
		out.writeObject(this.serviceMethod);
		out.writeObject(this.clientClass);
		out.writeObject(this.clientMethod);
		out.writeObject(this.startTime);
		out.writeObject(this.finishTime);
		out.writeObject(this.status);
	}

}
