
package com.catl.integration.pi.bom.create;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.catl.integration.pi.bom.create package. 
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

    private final static QName _MTBOMCreateCreateRequest_QNAME = new QName("http://atlbattery.com/PLM/Bom_create", "MT_BOM_CreateCreateRequest");
    private final static QName _MTBOMCreateCreateResponse_QNAME = new QName("http://atlbattery.com/PLM/Bom_create", "MT_BOM_CreateCreateResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.catl.integration.pi.bom.create
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link DTBOMCreateCreateResponse }
     * 
     */
    public DTBOMCreateCreateResponse createDTBOMCreateCreateResponse() {
        return new DTBOMCreateCreateResponse();
    }

    /**
     * Create an instance of {@link DTBomCreateCreateRequest }
     * 
     */
    public DTBomCreateCreateRequest createDTBomCreateCreateRequest() {
        return new DTBomCreateCreateRequest();
    }

    /**
     * Create an instance of {@link DTACKNOW2 }
     * 
     */
    public DTACKNOW2 createDTACKNOW2() {
        return new DTACKNOW2();
    }

    /**
     * Create an instance of {@link DTBOMCreateCreateResponse.TBOMOUT }
     * 
     */
    public DTBOMCreateCreateResponse.TBOMOUT createDTBOMCreateCreateResponseTBOMOUT() {
        return new DTBOMCreateCreateResponse.TBOMOUT();
    }

    /**
     * Create an instance of {@link DTBomCreateCreateRequest.TBOMIN }
     * 
     */
    public DTBomCreateCreateRequest.TBOMIN createDTBomCreateCreateRequestTBOMIN() {
        return new DTBomCreateCreateRequest.TBOMIN();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DTBomCreateCreateRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://atlbattery.com/PLM/Bom_create", name = "MT_BOM_CreateCreateRequest")
    public JAXBElement<DTBomCreateCreateRequest> createMTBOMCreateCreateRequest(DTBomCreateCreateRequest value) {
        return new JAXBElement<DTBomCreateCreateRequest>(_MTBOMCreateCreateRequest_QNAME, DTBomCreateCreateRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DTBOMCreateCreateResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://atlbattery.com/PLM/Bom_create", name = "MT_BOM_CreateCreateResponse")
    public JAXBElement<DTBOMCreateCreateResponse> createMTBOMCreateCreateResponse(DTBOMCreateCreateResponse value) {
        return new JAXBElement<DTBOMCreateCreateResponse>(_MTBOMCreateCreateResponse_QNAME, DTBOMCreateCreateResponse.class, null, value);
    }

}
