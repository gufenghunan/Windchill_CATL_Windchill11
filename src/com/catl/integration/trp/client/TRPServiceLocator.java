/**
 * TRPServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.catl.integration.trp.client;

public class TRPServiceLocator extends org.apache.axis.client.Service implements com.catl.integration.trp.client.TRPWSServiceService {

    public TRPServiceLocator() {
    }


    public TRPServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public TRPServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for ITestSampleInfoWSServicePort
    private java.lang.String ITestSampleInfoWSServicePort_address = "http://172.26.164.180:8080/core/ws/testSampleInfoWSService";

    public java.lang.String getITestSampleInfoWSServicePortAddress() {
        return ITestSampleInfoWSServicePort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String ITestSampleInfoWSServicePortWSDDServiceName = "ITestSampleInfoWSServicePort";

    public java.lang.String getITestSampleInfoWSServicePortWSDDServiceName() {
        return ITestSampleInfoWSServicePortWSDDServiceName;
    }

    public void setITestSampleInfoWSServicePortWSDDServiceName(java.lang.String name) {
        ITestSampleInfoWSServicePortWSDDServiceName = name;
    }

    public com.catl.integration.trp.client.TRPWSService getITestSampleInfoWSServicePort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(ITestSampleInfoWSServicePort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getITestSampleInfoWSServicePort(endpoint);
    }

    public com.catl.integration.trp.client.TRPWSService getITestSampleInfoWSServicePort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.catl.integration.trp.client.TRPWebserviceStub _stub = new com.catl.integration.trp.client.TRPWebserviceStub(portAddress, this);
            _stub.setPortName(getITestSampleInfoWSServicePortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setITestSampleInfoWSServicePortEndpointAddress(java.lang.String address) {
        ITestSampleInfoWSServicePort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (com.catl.integration.trp.client.TRPWSService.class.isAssignableFrom(serviceEndpointInterface)) {
                com.catl.integration.trp.client.TRPWebserviceStub _stub = new com.catl.integration.trp.client.TRPWebserviceStub(new java.net.URL(ITestSampleInfoWSServicePort_address), this);
                _stub.setPortName(getITestSampleInfoWSServicePortWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("ITestSampleInfoWSServicePort".equals(inputPortName)) {
            return getITestSampleInfoWSServicePort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://service.in.ws.trp.catl.com/", "TRPWSServiceService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://service.in.ws.trp.catl.com/", "ITestSampleInfoWSServicePort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("ITestSampleInfoWSServicePort".equals(portName)) {
            setITestSampleInfoWSServicePortEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
