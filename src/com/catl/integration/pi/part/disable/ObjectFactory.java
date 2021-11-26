
package com.catl.integration.pi.part.disable;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.catl.integration.pi.part.disable package. 
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

    private final static QName _MTMSTAECreateResponse_QNAME = new QName("http://atlbattery.com/PLM/MSTAE", "MT_MSTAECreateResponse");
    private final static QName _MTMATAECreateRequest_QNAME = new QName("http://atlbattery.com/PLM/MSTAE", "MT_MATAECreateRequest");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.catl.integration.pi.part.disable
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link DTMSTAECreateRequest }
     * 
     */
    public DTMSTAECreateRequest createDTMSTAECreateRequest() {
        return new DTMSTAECreateRequest();
    }

    /**
     * Create an instance of {@link DTMSTAECreateResponse }
     * 
     */
    public DTMSTAECreateResponse createDTMSTAECreateResponse() {
        return new DTMSTAECreateResponse();
    }

    /**
     * Create an instance of {@link EACKNOW }
     * 
     */
    public EACKNOW createEACKNOW() {
        return new EACKNOW();
    }

    /**
     * Create an instance of {@link DTMSTAECreateRequest.TMSTAE }
     * 
     */
    public DTMSTAECreateRequest.TMSTAE createDTMSTAECreateRequestTMSTAE() {
        return new DTMSTAECreateRequest.TMSTAE();
    }

    /**
     * Create an instance of {@link DTMSTAECreateResponse.TRETURN }
     * 
     */
    public DTMSTAECreateResponse.TRETURN createDTMSTAECreateResponseTRETURN() {
        return new DTMSTAECreateResponse.TRETURN();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DTMSTAECreateResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://atlbattery.com/PLM/MSTAE", name = "MT_MSTAECreateResponse")
    public JAXBElement<DTMSTAECreateResponse> createMTMSTAECreateResponse(DTMSTAECreateResponse value) {
        return new JAXBElement<DTMSTAECreateResponse>(_MTMSTAECreateResponse_QNAME, DTMSTAECreateResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DTMSTAECreateRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://atlbattery.com/PLM/MSTAE", name = "MT_MATAECreateRequest")
    public JAXBElement<DTMSTAECreateRequest> createMTMATAECreateRequest(DTMSTAECreateRequest value) {
        return new JAXBElement<DTMSTAECreateRequest>(_MTMATAECreateRequest_QNAME, DTMSTAECreateRequest.class, null, value);
    }

}
