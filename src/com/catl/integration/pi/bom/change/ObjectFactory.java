
package com.catl.integration.pi.bom.change;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.catl.integration.pi.bom.change package. 
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

    private final static QName _MTBOMCHANGECreateResponse_QNAME = new QName("http://atlbattery.com/PLM/BOM_CHANGE", "MT_BOM_CHANGECreateResponse");
    private final static QName _MTBOMCHANGECreateRequest_QNAME = new QName("http://atlbattery.com/PLM/BOM_CHANGE", "MT_BOM_CHANGECreateRequest");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.catl.integration.pi.bom.change
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link DTBOMCHANGECreateRequest }
     * 
     */
    public DTBOMCHANGECreateRequest createDTBOMCHANGECreateRequest() {
        return new DTBOMCHANGECreateRequest();
    }

    /**
     * Create an instance of {@link DTBOMCHANGECreateResponse }
     * 
     */
    public DTBOMCHANGECreateResponse createDTBOMCHANGECreateResponse() {
        return new DTBOMCHANGECreateResponse();
    }

    /**
     * Create an instance of {@link EACKNOW }
     * 
     */
    public EACKNOW createEACKNOW() {
        return new EACKNOW();
    }

    /**
     * Create an instance of {@link DTBOMCHANGECreateRequest.TBOMIN }
     * 
     */
    public DTBOMCHANGECreateRequest.TBOMIN createDTBOMCHANGECreateRequestTBOMIN() {
        return new DTBOMCHANGECreateRequest.TBOMIN();
    }

    /**
     * Create an instance of {@link DTBOMCHANGECreateResponse.TBOMOUT }
     * 
     */
    public DTBOMCHANGECreateResponse.TBOMOUT createDTBOMCHANGECreateResponseTBOMOUT() {
        return new DTBOMCHANGECreateResponse.TBOMOUT();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DTBOMCHANGECreateResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://atlbattery.com/PLM/BOM_CHANGE", name = "MT_BOM_CHANGECreateResponse")
    public JAXBElement<DTBOMCHANGECreateResponse> createMTBOMCHANGECreateResponse(DTBOMCHANGECreateResponse value) {
        return new JAXBElement<DTBOMCHANGECreateResponse>(_MTBOMCHANGECreateResponse_QNAME, DTBOMCHANGECreateResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DTBOMCHANGECreateRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://atlbattery.com/PLM/BOM_CHANGE", name = "MT_BOM_CHANGECreateRequest")
    public JAXBElement<DTBOMCHANGECreateRequest> createMTBOMCHANGECreateRequest(DTBOMCHANGECreateRequest value) {
        return new JAXBElement<DTBOMCHANGECreateRequest>(_MTBOMCHANGECreateRequest_QNAME, DTBOMCHANGECreateRequest.class, null, value);
    }

}
