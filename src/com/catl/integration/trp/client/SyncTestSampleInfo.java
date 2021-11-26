
package com.catl.integration.trp.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 * <pre>
 * &lt;complexType name="syncTestSampleInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="testSampleInfo" type="{http://service.in.ws.trp.catl.com/}testSampleInfoWSRequest" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "syncTestSampleInfo", propOrder = {
    "testSampleInfo"
})
public class SyncTestSampleInfo {

    protected TestSampleInfoWSRequest testSampleInfo;

    /**
     * 
     * @return
     *     possible object is
     *     {@link TestSampleInfoWSRequest }
     *     
     */
    public TestSampleInfoWSRequest getTestSampleInfo() {
        return testSampleInfo;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link TestSampleInfoWSRequest }
     *     
     */
    public void setTestSampleInfo(TestSampleInfoWSRequest value) {
        this.testSampleInfo = value;
    }

}
