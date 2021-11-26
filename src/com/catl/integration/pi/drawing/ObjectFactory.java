
package com.catl.integration.pi.drawing;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.catl.integration.pi.drawing package. 
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

    private final static QName _MTZEINRCreateResponse_QNAME = new QName("http://atlbattery.cpm/PLM/ZEINR", "MT_ZEINRCreateResponse");
    private final static QName _MTZEINRCreateRequest_QNAME = new QName("http://atlbattery.cpm/PLM/ZEINR", "MT_ZEINRCreateRequest");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.catl.integration.pi.drawing
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link DTZEINRCreateResponse }
     * 
     */
    public DTZEINRCreateResponse createDTZEINRCreateResponse() {
        return new DTZEINRCreateResponse();
    }

    /**
     * Create an instance of {@link DTZEINRCreateRequest }
     * 
     */
    public DTZEINRCreateRequest createDTZEINRCreateRequest() {
        return new DTZEINRCreateRequest();
    }

    /**
     * Create an instance of {@link DTACKNOW2 }
     * 
     */
    public DTACKNOW2 createDTACKNOW2() {
        return new DTACKNOW2();
    }

    /**
     * Create an instance of {@link DTZEINRCreateResponse.TRETURN }
     * 
     */
    public DTZEINRCreateResponse.TRETURN createDTZEINRCreateResponseTRETURN() {
        return new DTZEINRCreateResponse.TRETURN();
    }

    /**
     * Create an instance of {@link DTZEINRCreateRequest.TZEINR }
     * 
     */
    public DTZEINRCreateRequest.TZEINR createDTZEINRCreateRequestTZEINR() {
        return new DTZEINRCreateRequest.TZEINR();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DTZEINRCreateResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://atlbattery.cpm/PLM/ZEINR", name = "MT_ZEINRCreateResponse")
    public JAXBElement<DTZEINRCreateResponse> createMTZEINRCreateResponse(DTZEINRCreateResponse value) {
        return new JAXBElement<DTZEINRCreateResponse>(_MTZEINRCreateResponse_QNAME, DTZEINRCreateResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DTZEINRCreateRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://atlbattery.cpm/PLM/ZEINR", name = "MT_ZEINRCreateRequest")
    public JAXBElement<DTZEINRCreateRequest> createMTZEINRCreateRequest(DTZEINRCreateRequest value) {
        return new JAXBElement<DTZEINRCreateRequest>(_MTZEINRCreateRequest_QNAME, DTZEINRCreateRequest.class, null, value);
    }

}
