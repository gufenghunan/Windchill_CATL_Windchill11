
package com.catl.integration.pi.drawing;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.4-b01
 * Generated source version: 2.2
 * 
 */
@WebService(name = "SI_ZEINRCreateOut", targetNamespace = "http://atlbattery.cpm/PLM/ZEINR")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
@XmlSeeAlso({
    ObjectFactory.class
})
public interface SIZEINRCreateOut {


    /**
     * 
     * @param mtZEINRCreateRequest
     * @return
     *     returns com.catl.integration.pi.drawing.DTZEINRCreateResponse
     */
    @WebMethod(operationName = "SI_ZEINRCreateOut", action = "http://sap.com/xi/WebService/soap1.1")
    @WebResult(name = "MT_ZEINRCreateResponse", targetNamespace = "http://atlbattery.cpm/PLM/ZEINR", partName = "MT_ZEINRCreateResponse")
    public DTZEINRCreateResponse siZEINRCreateOut(
        @WebParam(name = "MT_ZEINRCreateRequest", targetNamespace = "http://atlbattery.cpm/PLM/ZEINR", partName = "MT_ZEINRCreateRequest")
        DTZEINRCreateRequest mtZEINRCreateRequest);

}
