
package com.catl.integration.trp.client;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.catl.integration.trp.client package. 
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

    private final static QName _Self_QNAME = new QName("http://service.in.ws.trp.catl.com/", "self");
    private final static QName _SyncTestSampleInfo_QNAME = new QName("http://service.in.ws.trp.catl.com/", "syncTestSampleInfo");
    private final static QName _SyncTestSampleInfoResponse_QNAME = new QName("http://service.in.ws.trp.catl.com/", "syncTestSampleInfoResponse");
    private final static QName _SelfResponse_QNAME = new QName("http://service.in.ws.trp.catl.com/", "selfResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.catl.integration.trp.client
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link SyncTestSampleInfo }
     * 
     */
    public SyncTestSampleInfo createSyncTestSampleInfo() {
        return new SyncTestSampleInfo();
    }

    /**
     * Create an instance of {@link SelfResponse }
     * 
     */
    public SelfResponse createSelfResponse() {
        return new SelfResponse();
    }

    /**
     * Create an instance of {@link SyncTestSampleInfoResponse }
     * 
     */
    public SyncTestSampleInfoResponse createSyncTestSampleInfoResponse() {
        return new SyncTestSampleInfoResponse();
    }

    /**
     * Create an instance of {@link Self }
     * 
     */
    public Self createSelf() {
        return new Self();
    }

    /**
     * Create an instance of {@link Item }
     * 
     */
    public Item createItem() {
        return new Item();
    }

    /**
     * Create an instance of {@link TestSampleInfoWSResponse }
     * 
     */
    public TestSampleInfoWSResponse createTestSampleInfoWSResponse() {
        return new TestSampleInfoWSResponse();
    }

    /**
     * Create an instance of {@link TestSampleInfoWSRequest }
     * 
     */
    public TestSampleInfoWSRequest createTestSampleInfoWSRequest() {
        return new TestSampleInfoWSRequest();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Self }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.in.ws.trp.catl.com/", name = "self")
    public JAXBElement<Self> createSelf(Self value) {
        return new JAXBElement<Self>(_Self_QNAME, Self.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SyncTestSampleInfo }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.in.ws.trp.catl.com/", name = "syncTestSampleInfo")
    public JAXBElement<SyncTestSampleInfo> createSyncTestSampleInfo(SyncTestSampleInfo value) {
        return new JAXBElement<SyncTestSampleInfo>(_SyncTestSampleInfo_QNAME, SyncTestSampleInfo.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SyncTestSampleInfoResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.in.ws.trp.catl.com/", name = "syncTestSampleInfoResponse")
    public JAXBElement<SyncTestSampleInfoResponse> createSyncTestSampleInfoResponse(SyncTestSampleInfoResponse value) {
        return new JAXBElement<SyncTestSampleInfoResponse>(_SyncTestSampleInfoResponse_QNAME, SyncTestSampleInfoResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SelfResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.in.ws.trp.catl.com/", name = "selfResponse")
    public JAXBElement<SelfResponse> createSelfResponse(SelfResponse value) {
        return new JAXBElement<SelfResponse>(_SelfResponse_QNAME, SelfResponse.class, null, value);
    }

}
