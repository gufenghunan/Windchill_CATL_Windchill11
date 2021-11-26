
package com.catl.integration.pi.sourceChange;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.catl.integration.pi.sourceChange package. 
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

    private final static QName _MTBESKZCreateRequest_QNAME = new QName("http://atlbattery.com/PLM/BESKZ", "MT_BESKZCreateRequest");
    private final static QName _MTBESKZCreateResponse_QNAME = new QName("http://atlbattery.com/PLM/BESKZ", "MT_BESKZCreateResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.catl.integration.pi.sourceChange
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link DTBESKZCreateResponse }
     * 
     */
    public DTBESKZCreateResponse createDTBESKZCreateResponse() {
        return new DTBESKZCreateResponse();
    }

    /**
     * Create an instance of {@link DTBESKZCreateRequest }
     * 
     */
    public DTBESKZCreateRequest createDTBESKZCreateRequest() {
        return new DTBESKZCreateRequest();
    }

    /**
     * Create an instance of {@link EACKNOW }
     * 
     */
    public EACKNOW createEACKNOW() {
        return new EACKNOW();
    }

    /**
     * Create an instance of {@link DTBESKZCreateResponse.TRETURN }
     * 
     */
    public DTBESKZCreateResponse.TRETURN createDTBESKZCreateResponseTRETURN() {
        return new DTBESKZCreateResponse.TRETURN();
    }

    /**
     * Create an instance of {@link DTBESKZCreateRequest.TBESKZ }
     * 
     */
    public DTBESKZCreateRequest.TBESKZ createDTBESKZCreateRequestTBESKZ() {
        return new DTBESKZCreateRequest.TBESKZ();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DTBESKZCreateRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://atlbattery.com/PLM/BESKZ", name = "MT_BESKZCreateRequest")
    public JAXBElement<DTBESKZCreateRequest> createMTBESKZCreateRequest(DTBESKZCreateRequest value) {
        return new JAXBElement<DTBESKZCreateRequest>(_MTBESKZCreateRequest_QNAME, DTBESKZCreateRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DTBESKZCreateResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://atlbattery.com/PLM/BESKZ", name = "MT_BESKZCreateResponse")
    public JAXBElement<DTBESKZCreateResponse> createMTBESKZCreateResponse(DTBESKZCreateResponse value) {
        return new JAXBElement<DTBESKZCreateResponse>(_MTBESKZCreateResponse_QNAME, DTBESKZCreateResponse.class, null, value);
    }

}
