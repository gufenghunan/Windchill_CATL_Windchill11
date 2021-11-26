
package com.catl.integration.pi.part.maturity;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.catl.integration.pi.part.maturity package. 
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

    private final static QName _MTZEIVRCreateResponse_QNAME = new QName("http://atlbattery.com/PLM/ZEIVR", "MT_ZEIVRCreateResponse");
    private final static QName _MTZEIVRCreateRequest_QNAME = new QName("http://atlbattery.com/PLM/ZEIVR", "MT_ZEIVRCreateRequest");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.catl.integration.pi.part.maturity
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link DTZEIVRCreateResponse }
     * 
     */
    public DTZEIVRCreateResponse createDTZEIVRCreateResponse() {
        return new DTZEIVRCreateResponse();
    }

    /**
     * Create an instance of {@link DTZEIVRCreateRequest }
     * 
     */
    public DTZEIVRCreateRequest createDTZEIVRCreateRequest() {
        return new DTZEIVRCreateRequest();
    }

    /**
     * Create an instance of {@link EACKNOW }
     * 
     */
    public EACKNOW createEACKNOW() {
        return new EACKNOW();
    }

    /**
     * Create an instance of {@link DTZEIVRCreateResponse.TRETURN }
     * 
     */
    public DTZEIVRCreateResponse.TRETURN createDTZEIVRCreateResponseTRETURN() {
        return new DTZEIVRCreateResponse.TRETURN();
    }

    /**
     * Create an instance of {@link DTZEIVRCreateRequest.TZEIVR }
     * 
     */
    public DTZEIVRCreateRequest.TZEIVR createDTZEIVRCreateRequestTZEIVR() {
        return new DTZEIVRCreateRequest.TZEIVR();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DTZEIVRCreateResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://atlbattery.com/PLM/ZEIVR", name = "MT_ZEIVRCreateResponse")
    public JAXBElement<DTZEIVRCreateResponse> createMTZEIVRCreateResponse(DTZEIVRCreateResponse value) {
        return new JAXBElement<DTZEIVRCreateResponse>(_MTZEIVRCreateResponse_QNAME, DTZEIVRCreateResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DTZEIVRCreateRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://atlbattery.com/PLM/ZEIVR", name = "MT_ZEIVRCreateRequest")
    public JAXBElement<DTZEIVRCreateRequest> createMTZEIVRCreateRequest(DTZEIVRCreateRequest value) {
        return new JAXBElement<DTZEIVRCreateRequest>(_MTZEIVRCreateRequest_QNAME, DTZEIVRCreateRequest.class, null, value);
    }

}
