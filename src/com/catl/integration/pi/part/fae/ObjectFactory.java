
package com.catl.integration.pi.part.fae;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.catl.integration.pi.part.fae package. 
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

    private final static QName _MTZFAECreateResponse_QNAME = new QName("http://atlbattery.com/PLM/ZFAE", "MT_ZFAECreateResponse");
    private final static QName _MTZFAECreateRequest_QNAME = new QName("http://atlbattery.com/PLM/ZFAE", "MT_ZFAECreateRequest");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.catl.integration.pi.part.fae
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link DTZFAECreateResponse }
     * 
     */
    public DTZFAECreateResponse createDTZFAECreateResponse() {
        return new DTZFAECreateResponse();
    }

    /**
     * Create an instance of {@link DTZFAECreateRequest }
     * 
     */
    public DTZFAECreateRequest createDTZFAECreateRequest() {
        return new DTZFAECreateRequest();
    }

    /**
     * Create an instance of {@link EACKNOW }
     * 
     */
    public EACKNOW createEACKNOW() {
        return new EACKNOW();
    }

    /**
     * Create an instance of {@link DTZFAECreateResponse.TRETURN }
     * 
     */
    public DTZFAECreateResponse.TRETURN createDTZFAECreateResponseTRETURN() {
        return new DTZFAECreateResponse.TRETURN();
    }

    /**
     * Create an instance of {@link DTZFAECreateRequest.TZFAE }
     * 
     */
    public DTZFAECreateRequest.TZFAE createDTZFAECreateRequestTZFAE() {
        return new DTZFAECreateRequest.TZFAE();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DTZFAECreateResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://atlbattery.com/PLM/ZFAE", name = "MT_ZFAECreateResponse")
    public JAXBElement<DTZFAECreateResponse> createMTZFAECreateResponse(DTZFAECreateResponse value) {
        return new JAXBElement<DTZFAECreateResponse>(_MTZFAECreateResponse_QNAME, DTZFAECreateResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DTZFAECreateRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://atlbattery.com/PLM/ZFAE", name = "MT_ZFAECreateRequest")
    public JAXBElement<DTZFAECreateRequest> createMTZFAECreateRequest(DTZFAECreateRequest value) {
        return new JAXBElement<DTZFAECreateRequest>(_MTZFAECreateRequest_QNAME, DTZFAECreateRequest.class, null, value);
    }

}
