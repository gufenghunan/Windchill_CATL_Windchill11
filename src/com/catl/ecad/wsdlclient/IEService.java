/**
 * IEService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.catl.ecad.wsdlclient;

public interface IEService extends java.rmi.Remote {
    public java.lang.String checkin(java.lang.String json) throws java.rmi.RemoteException;
    public java.lang.String checkoutlist(java.lang.String json) throws java.rmi.RemoteException;
    public java.lang.String queryepm(java.lang.String number, java.lang.String softname) throws java.rmi.RemoteException;
    public java.lang.String checkout(java.lang.String number) throws java.rmi.RemoteException;
}
