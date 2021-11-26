
package com.catl.integration.pi.ecn;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DT_EcnCreateRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DT_EcnCreateRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="T_ECN" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
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
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="SYSNAME" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DT_EcnCreateRequest", propOrder = {
    "tecn",
    "sysname"
})
public class DTEcnCreateRequest {

    @XmlElement(name = "T_ECN")
    protected List<DTEcnCreateRequest.TECN> tecn;
    @XmlElement(name = "SYSNAME", required = true)
    protected String sysname;

    /**
     * Gets the value of the tecn property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the tecn property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTECN().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DTEcnCreateRequest.TECN }
     * 
     * 
     */
    public List<DTEcnCreateRequest.TECN> getTECN() {
        if (tecn == null) {
            tecn = new ArrayList<DTEcnCreateRequest.TECN>();
        }
        return this.tecn;
    }

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
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
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
        "aetxt",
        "aennr",
        "aegru",
        "datuv"
    })
    public static class TECN {

        @XmlElement(name = "AETXT", required = true)
        protected String aetxt;
        @XmlElement(name = "AENNR", required = true)
        protected String aennr;
        @XmlElement(name = "AEGRU", required = true)
        protected String aegru;
        @XmlElement(name = "DATUV", required = true)
        protected String datuv;

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

    }

}
