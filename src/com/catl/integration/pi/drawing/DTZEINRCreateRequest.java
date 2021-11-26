
package com.catl.integration.pi.drawing;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DT_ZEINRCreateRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DT_ZEINRCreateRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SYSNAME" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="T_ZEINR" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="MATNR" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="ZEINR" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="ZEIAR" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DT_ZEINRCreateRequest", propOrder = {
    "sysname",
    "tzeinr"
})
public class DTZEINRCreateRequest {

    @XmlElement(name = "SYSNAME", required = true)
    protected String sysname;
    @XmlElement(name = "T_ZEINR")
    protected List<DTZEINRCreateRequest.TZEINR> tzeinr;

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
     * Gets the value of the tzeinr property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the tzeinr property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTZEINR().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DTZEINRCreateRequest.TZEINR }
     * 
     * 
     */
    public List<DTZEINRCreateRequest.TZEINR> getTZEINR() {
        if (tzeinr == null) {
            tzeinr = new ArrayList<DTZEINRCreateRequest.TZEINR>();
        }
        return this.tzeinr;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="MATNR" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="ZEINR" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="ZEIAR" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "matnr",
        "zeinr",
        "zeiar"
    })
    public static class TZEINR {

        @XmlElement(name = "MATNR", required = true)
        protected String matnr;
        @XmlElement(name = "ZEINR", required = true)
        protected String zeinr;
        @XmlElement(name = "ZEIAR", required = true)
        protected String zeiar;

        /**
         * Gets the value of the matnr property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getMATNR() {
            return matnr;
        }

        /**
         * Sets the value of the matnr property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setMATNR(String value) {
            this.matnr = value;
        }

        /**
         * Gets the value of the zeinr property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getZEINR() {
            return zeinr;
        }

        /**
         * Sets the value of the zeinr property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setZEINR(String value) {
            this.zeinr = value;
        }

        /**
         * Gets the value of the zeiar property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getZEIAR() {
            return zeiar;
        }

        /**
         * Sets the value of the zeiar property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setZEIAR(String value) {
            this.zeiar = value;
        }

    }

}
