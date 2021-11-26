
package com.catl.integration.pi.file;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DT_ZTZFILECreateResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DT_ZTZFILECreateResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="O_SUBRC" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="O_MESSAGE" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DT_ZTZFILECreateResponse", propOrder = {
    "osubrc",
    "omessage"
})
public class DTZTZFILECreateResponse {

    @XmlElement(name = "O_SUBRC", required = true)
    protected String osubrc;
    @XmlElement(name = "O_MESSAGE", required = true)
    protected String omessage;

    /**
     * Gets the value of the osubrc property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOSUBRC() {
        return osubrc;
    }

    /**
     * Sets the value of the osubrc property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOSUBRC(String value) {
        this.osubrc = value;
    }

    /**
     * Gets the value of the omessage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOMESSAGE() {
        return omessage;
    }

    /**
     * Sets the value of the omessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOMESSAGE(String value) {
        this.omessage = value;
    }

}
