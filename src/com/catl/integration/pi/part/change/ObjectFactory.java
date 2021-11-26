
package com.catl.integration.pi.part.change;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.catl.integration.pi.part.change package. 
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

    private final static QName _MTPNCHANGECreateRequest_QNAME = new QName("http://atlbattery.com/PLM/PN_CHANGE", "MT_PN_CHANGECreateRequest");
    private final static QName _MTPNCHANGECreateResponse_QNAME = new QName("http://atlbattery.com/PLM/PN_CHANGE", "MT_PN_CHANGECreateResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.catl.integration.pi.part.change
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link DTPNCHANGECreateResponse }
     * 
     */
    public DTPNCHANGECreateResponse createDTPNCHANGECreateResponse() {
        return new DTPNCHANGECreateResponse();
    }

    /**
     * Create an instance of {@link DTPNCHANGECreateRequest }
     * 
     */
    public DTPNCHANGECreateRequest createDTPNCHANGECreateRequest() {
        return new DTPNCHANGECreateRequest();
    }

    /**
     * Create an instance of {@link EACKNOW }
     * 
     */
    public EACKNOW createEACKNOW() {
        return new EACKNOW();
    }

    /**
     * Create an instance of {@link DTPNCHANGECreateResponse.TRETURN }
     * 
     */
    public DTPNCHANGECreateResponse.TRETURN createDTPNCHANGECreateResponseTRETURN() {
        return new DTPNCHANGECreateResponse.TRETURN();
    }

    /**
     * Create an instance of {@link DTPNCHANGECreateRequest.TPN }
     * 
     */
    public DTPNCHANGECreateRequest.TPN createDTPNCHANGECreateRequestTPN() {
        return new DTPNCHANGECreateRequest.TPN();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DTPNCHANGECreateRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://atlbattery.com/PLM/PN_CHANGE", name = "MT_PN_CHANGECreateRequest")
    public JAXBElement<DTPNCHANGECreateRequest> createMTPNCHANGECreateRequest(DTPNCHANGECreateRequest value) {
        return new JAXBElement<DTPNCHANGECreateRequest>(_MTPNCHANGECreateRequest_QNAME, DTPNCHANGECreateRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DTPNCHANGECreateResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://atlbattery.com/PLM/PN_CHANGE", name = "MT_PN_CHANGECreateResponse")
    public JAXBElement<DTPNCHANGECreateResponse> createMTPNCHANGECreateResponse(DTPNCHANGECreateResponse value) {
        return new JAXBElement<DTPNCHANGECreateResponse>(_MTPNCHANGECreateResponse_QNAME, DTPNCHANGECreateResponse.class, null, value);
    }

}
