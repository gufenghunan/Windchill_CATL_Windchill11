
package com.catl.integration.pi.part.create;

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
@WebService(name = "SI_MATNRCreateOut", targetNamespace = "http://atlbattery.com/PLM/MATNR")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
@XmlSeeAlso({
    ObjectFactory.class
})
public interface SIMATNRCreateOut {


    /**
     * 
     * @param mtMATNRCreateRequest
     * @return
     *     returns com.catl.integration.pi.part.create.DTMATNRCreateResponse
     */
    @WebMethod(operationName = "SI_MATNRCreateOut", action = "http://sap.com/xi/WebService/soap1.1")
    @WebResult(name = "MT_MATNRCreateResponse", targetNamespace = "http://atlbattery.com/PLM/MATNR", partName = "MT_MATNRCreateResponse")
    public DTMATNRCreateResponse siMATNRCreateOut(
        @WebParam(name = "MT_MATNRCreateRequest", targetNamespace = "http://atlbattery.com/PLM/MATNR", partName = "MT_MATNRCreateRequest")
        DTMATNRCreateRequest mtMATNRCreateRequest);

}
