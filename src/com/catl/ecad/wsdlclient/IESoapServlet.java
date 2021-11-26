/**
 * IESoapServlet.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.catl.ecad.wsdlclient;

public interface IESoapServlet extends javax.xml.rpc.Service {
    public java.lang.String getIESoapPortAddress();

    public com.catl.ecad.wsdlclient.IEService getIESoapPort() throws javax.xml.rpc.ServiceException;

    public com.catl.ecad.wsdlclient.IEService getIESoapPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
