/**
 * SoapBindingSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.catl.ecad.wsdlclient;

public class SoapBindingSkeleton implements com.catl.ecad.wsdlclient.IEService, org.apache.axis.wsdl.Skeleton {
    private com.catl.ecad.wsdlclient.IEService impl;
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
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "json"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("checkin", _params, new javax.xml.namespace.QName("", "Collection"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.ptc.com/infoengine/soap/rpc/message/", "checkin"));
        _oper.setSoapAction("urn:ie-soap-rpc:com.catl.ecad!checkin");
        _myOperationsList.add(_oper);
        if (_myOperations.get("checkin") == null) {
            _myOperations.put("checkin", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("checkin")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "json"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("checkoutlist", _params, new javax.xml.namespace.QName("", "Collection"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.ptc.com/infoengine/soap/rpc/message/", "checkoutlist"));
        _oper.setSoapAction("urn:ie-soap-rpc:com.catl.ecad!checkoutlist");
        _myOperationsList.add(_oper);
        if (_myOperations.get("checkoutlist") == null) {
            _myOperations.put("checkoutlist", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("checkoutlist")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "number"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "softname"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("queryepm", _params, new javax.xml.namespace.QName("", "Collection"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.ptc.com/infoengine/soap/rpc/message/", "queryepm"));
        _oper.setSoapAction("urn:ie-soap-rpc:com.catl.ecad!queryepm");
        _myOperationsList.add(_oper);
        if (_myOperations.get("queryepm") == null) {
            _myOperations.put("queryepm", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("queryepm")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "number"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("checkout", _params, new javax.xml.namespace.QName("", "Collection"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.ptc.com/infoengine/soap/rpc/message/", "checkout"));
        _oper.setSoapAction("urn:ie-soap-rpc:com.catl.ecad!checkout");
        _myOperationsList.add(_oper);
        if (_myOperations.get("checkout") == null) {
            _myOperations.put("checkout", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("checkout")).add(_oper);
    }

    public SoapBindingSkeleton() {
        this.impl = new com.catl.ecad.wsdlclient.SoapBindingImpl();
    }

    public SoapBindingSkeleton(com.catl.ecad.wsdlclient.IEService impl) {
        this.impl = impl;
    }
    public java.lang.String checkin(java.lang.String json) throws java.rmi.RemoteException
    {
        java.lang.String ret = impl.checkin(json);
        return ret;
    }

    public java.lang.String checkoutlist(java.lang.String json) throws java.rmi.RemoteException
    {
        java.lang.String ret = impl.checkoutlist(json);
        return ret;
    }

    public java.lang.String queryepm(java.lang.String number, java.lang.String softname) throws java.rmi.RemoteException
    {
        java.lang.String ret = impl.queryepm(number, softname);
        return ret;
    }

    public java.lang.String checkout(java.lang.String number) throws java.rmi.RemoteException
    {
        java.lang.String ret = impl.checkout(number);
        return ret;
    }

}
