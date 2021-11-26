
package com.catl.integration.pi.part.sapfae;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.catl.integration.pi.part.sapfae package. 
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

    private final static QName _MTZPLMNCreateResponse_QNAME = new QName("http://atlbattery.com/PLM/ZPLMN", "MT_ZPLMNCreateResponse");
    private final static QName _MTZPLMNCreateRequest_QNAME = new QName("http://atlbattery.com/PLM/ZPLMN", "MT_ZPLMNCreateRequest");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.catl.integration.pi.part.sapfae
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link DTZPLMNCreateRequest }
     * 
     */
    public DTZPLMNCreateRequest createDTZPLMNCreateRequest() {
        return new DTZPLMNCreateRequest();
    }

    /**
     * Create an instance of {@link DTZPLMNCreateResponse }
     * 
     */
    public DTZPLMNCreateResponse createDTZPLMNCreateResponse() {
        return new DTZPLMNCreateResponse();
    }

    /**
     * Create an instance of {@link EACKNOW }
     * 
     */
    public EACKNOW createEACKNOW() {
        return new EACKNOW();
    }

    /**
     * Create an instance of {@link DTZPLMNCreateRequest.TZPLMN }
     * 
     */
    public DTZPLMNCreateRequest.TZPLMN createDTZPLMNCreateRequestTZPLMN() {
        return new DTZPLMNCreateRequest.TZPLMN();
    }

    /**
     * Create an instance of {@link DTZPLMNCreateResponse.TRETURN }
     * 
     */
    public DTZPLMNCreateResponse.TRETURN createDTZPLMNCreateResponseTRETURN() {
        return new DTZPLMNCreateResponse.TRETURN();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DTZPLMNCreateResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://atlbattery.com/PLM/ZPLMN", name = "MT_ZPLMNCreateResponse")
    public JAXBElement<DTZPLMNCreateResponse> createMTZPLMNCreateResponse(DTZPLMNCreateResponse value) {
        return new JAXBElement<DTZPLMNCreateResponse>(_MTZPLMNCreateResponse_QNAME, DTZPLMNCreateResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DTZPLMNCreateRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://atlbattery.com/PLM/ZPLMN", name = "MT_ZPLMNCreateRequest")
    public JAXBElement<DTZPLMNCreateRequest> createMTZPLMNCreateRequest(DTZPLMNCreateRequest value) {
        return new JAXBElement<DTZPLMNCreateRequest>(_MTZPLMNCreateRequest_QNAME, DTZPLMNCreateRequest.class, null, value);
    }

}
