package com.catl.line.webservice;

import java.rmi.RemoteException;

import com.catl.line.webservice.DwgWebservice;

public class DwgWebserviceProxy implements com.catl.line.webservice.DwgWebservice {
  private String _endpoint = null;
  private com.catl.line.webservice.DwgWebservice dwgWebservice = null;
  
  public DwgWebserviceProxy() {
    _initDwgWebserviceProxy();
  }
  
  public DwgWebserviceProxy(String endpoint) {
    _endpoint = endpoint;
    _initDwgWebserviceProxy();
  }
  
  private void _initDwgWebserviceProxy() {
    try {
      dwgWebservice = (DwgWebservice) (new com.catl.line.webservice.DwgWebserviceServiceLocator()).getDwgWebservice();
      if (dwgWebservice != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)dwgWebservice)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)dwgWebservice)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (dwgWebservice != null)
      ((javax.xml.rpc.Stub)dwgWebservice)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public com.catl.line.webservice.DwgWebservice getDwgWebservice() {
    if (dwgWebservice == null)
      _initDwgWebserviceProxy();
    return dwgWebservice;
  }
  
  public java.lang.String dwgtopdf(java.lang.String path) throws RemoteException{
    if (dwgWebservice == null)
      _initDwgWebserviceProxy();
    return dwgWebservice.dwgtopdf(path);
  }
  
  public java.lang.String modifyDwgAndToPdf(java.lang.String path, java.lang.String params) throws RemoteException{
    if (dwgWebservice == null)
      _initDwgWebserviceProxy();
    return dwgWebservice.modifyDwgAndToPdf(path, params);
  }
  
  
}