
package com.catl.integration.trp.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 * 
 * <pre>
 * &lt;complexType name="syncTestSampleInfoResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="return" type="{http://service.in.ws.trp.catl.com/}testSampleInfoWSResponse" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "syncTestSampleInfoResponse", propOrder = {
    "_return"
})
public class SyncTestSampleInfoResponse {

    @XmlElement(name = "return")
    protected TestSampleInfoWSResponse _return;

    /**
     * 
     * @return
     *     possible object is
     *     {@link TestSampleInfoWSResponse }
     *     
     */
    public TestSampleInfoWSResponse getReturn() {
        return _return;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link TestSampleInfoWSResponse }
     *     
     */
    public void setReturn(TestSampleInfoWSResponse value) {
        this._return = value;
    }

}
