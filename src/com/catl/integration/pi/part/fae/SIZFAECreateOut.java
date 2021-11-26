
package com.catl.integration.pi.part.fae;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.9-b130926.1035
 * Generated source version: 2.2
 * 
 */
@WebService(name = "SI_ZFAECreateOut", targetNamespace = "http://atlbattery.com/PLM/ZFAE")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
@XmlSeeAlso({
    ObjectFactory.class
})
public interface SIZFAECreateOut {


    /**
     * 
     * @param mtZFAECreateRequest
     * @return
     *     returns com.catl.integration.pi.part.fae.DTZFAECreateResponse
     */
    @WebMethod(operationName = "SI_ZFAECreateOut", action = "http://sap.com/xi/WebService/soap1.1")
    @WebResult(name = "MT_ZFAECreateResponse", targetNamespace = "http://atlbattery.com/PLM/ZFAE", partName = "MT_ZFAECreateResponse")
    public DTZFAECreateResponse siZFAECreateOut(
        @WebParam(name = "MT_ZFAECreateRequest", targetNamespace = "http://atlbattery.com/PLM/ZFAE", partName = "MT_ZFAECreateRequest")
        DTZFAECreateRequest mtZFAECreateRequest);

}
