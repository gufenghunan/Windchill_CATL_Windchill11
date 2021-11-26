
package com.catl.integration.pi.ecn;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.catl.integration.pi package. 
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

    private final static QName _MTEcnCreateResponse_QNAME = new QName("http://atlbattery.com/PLM/ECN", "MT_EcnCreateResponse");
    private final static QName _MTEcnCreateRequest_QNAME = new QName("http://atlbattery.com/PLM/ECN", "MT_EcnCreateRequest");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.catl.integration.pi
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link DTEcnCreateRequest }
     * 
     */
    public DTEcnCreateRequest createDTEcnCreateRequest() {
        return new DTEcnCreateRequest();
    }

    /**
     * Create an instance of {@link DTEcnCreateResponse }
     * 
     */
    public DTEcnCreateResponse createDTEcnCreateResponse() {
        return new DTEcnCreateResponse();
    }

    /**
     * Create an instance of {@link DTACKNOW2 }
     * 
     */
    public DTACKNOW2 createDTACKNOW2() {
        return new DTACKNOW2();
    }

    /**
     * Create an instance of {@link DTEcnCreateRequest.TECN }
     * 
     */
    public DTEcnCreateRequest.TECN createDTEcnCreateRequestTECN() {
        return new DTEcnCreateRequest.TECN();
    }

    /**
     * Create an instance of {@link DTEcnCreateResponse.TECNR }
     * 
     */
    public DTEcnCreateResponse.TECNR createDTEcnCreateResponseTECNR() {
        return new DTEcnCreateResponse.TECNR();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DTEcnCreateResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://atlbattery.com/PLM/ECN", name = "MT_EcnCreateResponse")
    public JAXBElement<DTEcnCreateResponse> createMTEcnCreateResponse(DTEcnCreateResponse value) {
        return new JAXBElement<DTEcnCreateResponse>(_MTEcnCreateResponse_QNAME, DTEcnCreateResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DTEcnCreateRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://atlbattery.com/PLM/ECN", name = "MT_EcnCreateRequest")
    public JAXBElement<DTEcnCreateRequest> createMTEcnCreateRequest(DTEcnCreateRequest value) {
        return new JAXBElement<DTEcnCreateRequest>(_MTEcnCreateRequest_QNAME, DTEcnCreateRequest.class, null, value);
    }

}
