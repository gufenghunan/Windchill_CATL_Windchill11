
package com.catl.integration.pi.file;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.catl.integration.pi.file package. 
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

    private final static QName _MTZTZFILECreateResponse_QNAME = new QName("http://atlbattery.com/PLM/ZTZFILE", "MT_ZTZFILECreateResponse");
    private final static QName _MTZTZFILECreateRequest_QNAME = new QName("http://atlbattery.com/PLM/ZTZFILE", "MT_ZTZFILECreateRequest");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.catl.integration.pi.file
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link DTZTZFILECreateResponse }
     * 
     */
    public DTZTZFILECreateResponse createDTZTZFILECreateResponse() {
        return new DTZTZFILECreateResponse();
    }

    /**
     * Create an instance of {@link DTZTZFILECreateRequest }
     * 
     */
    public DTZTZFILECreateRequest createDTZTZFILECreateRequest() {
        return new DTZTZFILECreateRequest();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DTZTZFILECreateResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://atlbattery.com/PLM/ZTZFILE", name = "MT_ZTZFILECreateResponse")
    public JAXBElement<DTZTZFILECreateResponse> createMTZTZFILECreateResponse(DTZTZFILECreateResponse value) {
        return new JAXBElement<DTZTZFILECreateResponse>(_MTZTZFILECreateResponse_QNAME, DTZTZFILECreateResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DTZTZFILECreateRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://atlbattery.com/PLM/ZTZFILE", name = "MT_ZTZFILECreateRequest")
    public JAXBElement<DTZTZFILECreateRequest> createMTZTZFILECreateRequest(DTZTZFILECreateRequest value) {
        return new JAXBElement<DTZTZFILECreateRequest>(_MTZTZFILECreateRequest_QNAME, DTZTZFILECreateRequest.class, null, value);
    }

}
