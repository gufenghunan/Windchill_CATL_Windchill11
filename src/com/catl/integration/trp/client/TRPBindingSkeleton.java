/**
 * TRPBindingSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.catl.integration.trp.client;

public class TRPBindingSkeleton implements com.catl.integration.trp.client.TRPWSService, org.apache.axis.wsdl.Skeleton {
    private com.catl.integration.trp.client.TRPWSService impl;
    private static java.util.Map _myOperations = new java.util.Hashtable();
    private static java.util.Collection _myOperationsList = new java.util.ArrayList();

    /**
    * Returns List of OperationDesc objects with this name
    */
    public static java.util.List getOperationDescByName(java.lang.String methodName) {
        return (java.util.List)_myOperations.get(methodName);
    }

    /**
    * Returns Collection of OperationDescs
    */
    public static java.util.Collection getOperationDescs() {
        return _myOperationsList;
    }

    static {
        org.apache.axis.description.OperationDesc _oper;
        org.apache.axis.description.FaultDesc _fault;
        org.apache.axis.description.ParameterDesc [] _params;
        _params = new org.apache.axis.description.ParameterDesc [] {
        };
        _oper = new org.apache.axis.description.OperationDesc("self", _params, new javax.xml.namespace.QName("", "return"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "anyType"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://service.in.ws.trp.catl.com/", "self"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("self") == null) {
            _myOperations.put("self", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("self")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "testSampleInfo"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://service.in.ws.trp.catl.com/", "testSampleInfoWSRequest"), com.catl.integration.trp.client.PartInfoItem[].class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("syncTestSampleInfo", _params, new javax.xml.namespace.QName("", "return"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://service.in.ws.trp.catl.com/", "testSampleInfoWSResponse"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://service.in.ws.trp.catl.com/", "syncTestSampleInfo"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("syncTestSampleInfo") == null) {
            _myOperations.put("syncTestSampleInfo", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("syncTestSampleInfo")).add(_oper);
    }

    public TRPBindingSkeleton() {
        this.impl = new com.catl.integration.trp.client.TRPBindingImpl();
    }

    public TRPBindingSkeleton(com.catl.integration.trp.client.TRPWSService impl) {
        this.impl = impl;
    }
    public java.lang.Object self() throws java.rmi.RemoteException
    {
        java.lang.Object ret = impl.self();
        return ret;
    }

    public com.catl.integration.trp.client.TRPWSResponse syncTestSampleInfo(com.catl.integration.trp.client.PartInfoItem[] testSampleInfo) throws java.rmi.RemoteException
    {
        com.catl.integration.trp.client.TRPWSResponse ret = impl.syncTestSampleInfo(testSampleInfo);
        return ret;
    }

}
