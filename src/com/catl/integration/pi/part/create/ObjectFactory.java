
package com.catl.integration.pi.part.create;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.catl.integration.pi.part.create package. 
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

    private final static QName _MTMATNRCreateRequest_QNAME = new QName("http://atlbattery.com/PLM/MATNR", "MT_MATNRCreateRequest");
    private final static QName _MTMATNRCreateResponse_QNAME = new QName("http://atlbattery.com/PLM/MATNR", "MT_MATNRCreateResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.catl.integration.pi.part.create
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link DTMATNRCreateResponse }
     * 
     */
    public DTMATNRCreateResponse createDTMATNRCreateResponse() {
        return new DTMATNRCreateResponse();
    }

    /**
     * Create an instance of {@link DTMATNRCreateRequest }
     * 
     */
    public DTMATNRCreateRequest createDTMATNRCreateRequest() {
        return new DTMATNRCreateRequest();
    }

    /**
     * Create an instance of {@link DTACKNOW2 }
     * 
     */
    public DTACKNOW2 createDTACKNOW2() {
        return new DTACKNOW2();
    }

    /**
     * Create an instance of {@link DTMATNRCreateResponse.TRETURN }
     * 
     */
    public DTMATNRCreateResponse.TRETURN createDTMATNRCreateResponseTRETURN() {
        return new DTMATNRCreateResponse.TRETURN();
    }

    /**
     * Create an instance of {@link DTMATNRCreateRequest.TMATNR }
     * 
     */
    public DTMATNRCreateRequest.TMATNR createDTMATNRCreateRequestTMATNR() {
        return new DTMATNRCreateRequest.TMATNR();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DTMATNRCreateRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://atlbattery.com/PLM/MATNR", name = "MT_MATNRCreateRequest")
    public JAXBElement<DTMATNRCreateRequest> createMTMATNRCreateRequest(DTMATNRCreateRequest value) {
        return new JAXBElement<DTMATNRCreateRequest>(_MTMATNRCreateRequest_QNAME, DTMATNRCreateRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DTMATNRCreateResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://atlbattery.com/PLM/MATNR", name = "MT_MATNRCreateResponse")
    public JAXBElement<DTMATNRCreateResponse> createMTMATNRCreateResponse(DTMATNRCreateResponse value) {
        return new JAXBElement<DTMATNRCreateResponse>(_MTMATNRCreateResponse_QNAME, DTMATNRCreateResponse.class, null, value);
    }

}
