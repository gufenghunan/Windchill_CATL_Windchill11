/**
 * DwgWebserviceService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.catl.line.webservice;

public interface DwgWebserviceService extends javax.xml.rpc.Service {
    public java.lang.String getDwgWebserviceAddress();

    public com.catl.line.webservice.DwgWebservice getDwgWebservice() throws javax.xml.rpc.ServiceException;

    public com.catl.line.webservice.DwgWebservice getDwgWebservice(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
