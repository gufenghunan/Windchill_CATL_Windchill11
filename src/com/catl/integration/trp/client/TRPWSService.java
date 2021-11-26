/**
 * TRPWSService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.catl.integration.trp.client;

public interface TRPWSService extends java.rmi.Remote {
    public java.lang.Object self() throws java.rmi.RemoteException;
    public com.catl.integration.trp.client.TRPWSResponse syncTestSampleInfo(com.catl.integration.trp.client.PartInfoItem[] testSampleInfo) throws java.rmi.RemoteException;
}
