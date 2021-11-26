
package com.catl.integration.pi.ecn;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DT_EcnCreateResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DT_EcnCreateResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="E_ACKNOW" type="{http://atlbattery.com/PLM/ECN}DT_ACKNOW2"/>
 *         &lt;element name="T_ECN_R" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="STATUS">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                         &lt;minLength value="0"/>
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
 *                   &lt;element name="AETXT">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                         &lt;minLength value="0"/>
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
 *                   &lt;element name="AENNR">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                         &lt;minLength value="0"/>
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
 *                   &lt;element name="AEGRU">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                         &lt;minLength value="0"/>
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
 *                   &lt;element name="DATUV">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                         &lt;minLength value="0"/>
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
 *                   &lt;element name="MESSAGE">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                         &lt;minLength value="0"/>
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
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
@XmlType(name = "DT_EcnCreateResponse", propOrder = {
    "eacknow",
    "tecnr"
})
public class DTEcnCreateResponse {

    @XmlElement(name = "E_ACKNOW", required = true)
    protected DTACKNOW2 eacknow;
    @XmlElement(name = "T_ECN_R")
    protected List<DTEcnCreateResponse.TECNR> tecnr;

    /**
     * Gets the value of the eacknow property.
     * 
     * @return
     *     possible object is
     *     {@link DTACKNOW2 }
     *     
     */
    public DTACKNOW2 getEACKNOW() {
        return eacknow;
    }

    /**
     * Sets the value of the eacknow property.
     * 
     * @param value
     *     allowed object is
     *     {@link DTACKNOW2 }
     *     
     */
    public void setEACKNOW(DTACKNOW2 value) {
        this.eacknow = value;
    }

    /**
     * Gets the value of the tecnr property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the tecnr property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTECNR().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DTEcnCreateResponse.TECNR }
     * 
     * 
     */
    public List<DTEcnCreateResponse.TECNR> getTECNR() {
        if (tecnr == null) {
            tecnr = new ArrayList<DTEcnCreateResponse.TECNR>();
        }
        return this.tecnr;
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
     *         &lt;element name="STATUS">
     *           &lt;simpleType>
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *               &lt;minLength value="0"/>
     *             &lt;/restriction>
     *           &lt;/simpleType>
     *         &lt;/element>
     *         &lt;element name="AETXT">
     *           &lt;simpleType>
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *               &lt;minLength value="0"/>
     *             &lt;/restriction>
     *           &lt;/simpleType>
     *         &lt;/element>
     *         &lt;element name="AENNR">
     *           &lt;simpleType>
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *               &lt;minLength value="0"/>
     *             &lt;/restriction>
     *           &lt;/simpleType>
     *         &lt;/element>
     *         &lt;element name="AEGRU">
     *           &lt;simpleType>
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *               &lt;minLength value="0"/>
     *             &lt;/restriction>
     *           &lt;/simpleType>
     *         &lt;/element>
     *         &lt;element name="DATUV">
     *           &lt;simpleType>
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *               &lt;minLength value="0"/>
     *             &lt;/restriction>
     *           &lt;/simpleType>
     *         &lt;/element>
     *         &lt;element name="MESSAGE">
     *           &lt;simpleType>
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *               &lt;minLength value="0"/>
     *             &lt;/restriction>
     *           &lt;/simpleType>
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
    @XmlType(name = "", propOrder = {
        "status",
        "aetxt",
        "aennr",
        "aegru",
        "datuv",
        "message"
    })
    public static class TECNR {

        @XmlElement(name = "STATUS", required = true)
        protected String status;
        @XmlElement(name = "AETXT", required = true)
        protected String aetxt;
        @XmlElement(name = "AENNR", required = true)
        protected String aennr;
        @XmlElement(name = "AEGRU", required = true)
        protected String aegru;
        @XmlElement(name = "DATUV", required = true)
        protected String datuv;
        @XmlElement(name = "MESSAGE", required = true)
        protected String message;

        /**
         * Gets the value of the status property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getSTATUS() {
            return status;
        }

        /**
         * Sets the value of the status property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setSTATUS(String value) {
            this.status = value;
        }

        /**
         * Gets the value of the aetxt property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getAETXT() {
            return aetxt;
        }

        /**
         * Sets the value of the aetxt property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setAETXT(String value) {
            this.aetxt = value;
        }

        /**
         * Gets the value of the aennr property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getAENNR() {
            return aennr;
        }

        /**
         * Sets the value of the aennr property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setAENNR(String value) {
            this.aennr = value;
        }

        /**
         * Gets the value of the aegru property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getAEGRU() {
            return aegru;
        }

        /**
         * Sets the value of the aegru property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setAEGRU(String value) {
            this.aegru = value;
        }

        /**
         * Gets the value of the datuv property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getDATUV() {
            return datuv;
        }

        /**
         * Sets the value of the datuv property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setDATUV(String value) {
            this.datuv = value;
        }

        /**
         * Gets the value of the message property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getMESSAGE() {
            return message;
        }

        /**
         * Sets the value of the message property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setMESSAGE(String value) {
            this.message = value;
        }

    }

}
