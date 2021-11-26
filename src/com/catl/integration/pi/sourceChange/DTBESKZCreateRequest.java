
package com.catl.integration.pi.sourceChange;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DT_BESKZCreateRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DT_BESKZCreateRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SYSNAME" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="T_BESKZ" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="ZFAE" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="BESKZ" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="MATNR" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
@XmlType(name = "DT_BESKZCreateRequest", propOrder = {
    "sysname",
    "tbeskz"
})
public class DTBESKZCreateRequest {

    @XmlElement(name = "SYSNAME", required = true)
    protected String sysname;
    @XmlElement(name = "T_BESKZ")
    protected List<DTBESKZCreateRequest.TBESKZ> tbeskz;

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
     * Gets the value of the tbeskz property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the tbeskz property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTBESKZ().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DTBESKZCreateRequest.TBESKZ }
     * 
     * 
     */
    public List<DTBESKZCreateRequest.TBESKZ> getTBESKZ() {
        if (tbeskz == null) {
            tbeskz = new ArrayList<DTBESKZCreateRequest.TBESKZ>();
        }
        return this.tbeskz;
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
     *         &lt;element name="ZFAE" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="BESKZ" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="MATNR" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
        "zfae",
        "beskz",
        "matnr"
    })
    public static class TBESKZ {

        @XmlElement(name = "ZFAE", required = true)
        protected String zfae;
        @XmlElement(name = "BESKZ", required = true)
        protected String beskz;
        @XmlElement(name = "MATNR", required = true)
        protected String matnr;

        /**
         * Gets the value of the zfae property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getZFAE() {
            return zfae;
        }

        /**
         * Sets the value of the zfae property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setZFAE(String value) {
            this.zfae = value;
        }

        /**
         * Gets the value of the beskz property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getBESKZ() {
            return beskz;
        }

        /**
         * Sets the value of the beskz property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setBESKZ(String value) {
            this.beskz = value;
        }

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

    }

}
