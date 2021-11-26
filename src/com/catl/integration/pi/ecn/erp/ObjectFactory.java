
package com.catl.integration.pi.ecn.erp;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.catl.integration.pi.ecn.erp package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _MTEcndateResponse_QNAME = new QName("http://atlbattery.com/PLM/ECN_DATE", "MT_Ecndate_Response");
    private final static QName _MTEcndateRequest_QNAME = new QName("http://atlbattery.com/PLM/ECN_DATE", "MT_Ecndate_Request");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.catl.integration.pi.ecn.erp
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link DTEcndateRequest }
     * 
     */
    public DTEcndateRequest createDTEcndateRequest() {
        return new DTEcndateRequest();
    }

    /**
     * Create an instance of {@link DTEcndateResponse }
     * 
     */
    public DTEcndateResponse createDTEcndateResponse() {
        return new DTEcndateResponse();
    }

    /**
     * Create an instance of {@link DTEcndateRequest.TECN }
     * 
     */
    public DTEcndateRequest.TECN createDTEcndateRequestTECN() {
        return new DTEcndateRequest.TECN();
    }

    /**
     * Create an instance of {@link DTEcndateResponse.TRETURN }
     * 
     */
    public DTEcndateResponse.TRETURN createDTEcndateResponseTRETURN() {
        return new DTEcndateResponse.TRETURN();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DTEcndateResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://atlbattery.com/PLM/ECN_DATE", name = "MT_Ecndate_Response")
    public JAXBElement<DTEcndateResponse> createMTEcndateResponse(DTEcndateResponse value) {
        return new JAXBElement<DTEcndateResponse>(_MTEcndateResponse_QNAME, DTEcndateResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DTEcndateRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://atlbattery.com/PLM/ECN_DATE", name = "MT_Ecndate_Request")
    public JAXBElement<DTEcndateRequest> createMTEcndateRequest(DTEcndateRequest value) {
        return new JAXBElement<DTEcndateRequest>(_MTEcndateRequest_QNAME, DTEcndateRequest.class, null, value);
    }

}
