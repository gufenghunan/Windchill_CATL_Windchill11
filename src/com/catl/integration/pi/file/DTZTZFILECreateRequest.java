
package com.catl.integration.pi.file;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DT_ZTZFILECreateRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DT_ZTZFILECreateRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SYSNAME" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="I_MATNR" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="I_PLMURL" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="I_FILENAME" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DT_ZTZFILECreateRequest", propOrder = {
    "sysname",
    "imatnr",
    "iplmurl",
    "ifilename"
})
public class DTZTZFILECreateRequest {

    @XmlElement(name = "SYSNAME", required = true)
    protected String sysname;
    @XmlElement(name = "I_MATNR", required = true)
    protected String imatnr;
    @XmlElement(name = "I_PLMURL", required = true)
    protected String iplmurl;
    @XmlElement(name = "I_FILENAME", required = true)
    protected String ifilename;

    /**
     * Gets the value of the sysname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSYSNAME() {
        return sysname;
    }

    /**
     * Sets the value of the sysname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSYSNAME(String value) {
        this.sysname = value;
    }

    /**
     * Gets the value of the imatnr property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIMATNR() {
        return imatnr;
    }

    /**
     * Sets the value of the imatnr property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIMATNR(String value) {
        this.imatnr = value;
    }

    /**
     * Gets the value of the iplmurl property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIPLMURL() {
        return iplmurl;
    }

    /**
     * Sets the value of the iplmurl property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIPLMURL(String value) {
        this.iplmurl = value;
    }

    /**
     * Gets the value of the ifilename property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIFILENAME() {
        return ifilename;
    }

    /**
     * Sets the value of the ifilename property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIFILENAME(String value) {
        this.ifilename = value;
    }

}
