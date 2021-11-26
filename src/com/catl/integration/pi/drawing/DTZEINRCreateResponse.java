
package com.catl.integration.pi.drawing;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DT_ZEINRCreateResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DT_ZEINRCreateResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="E_ACKNOW" type="{http://atlbattery.com/PLM/ECN}DT_ACKNOW2"/>
 *         &lt;element name="T_RETURN" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="MATNR" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="ZEINR" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="ZEIAR" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="STATUS" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="MESSAGE" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
@XmlType(name = "DT_ZEINRCreateResponse", propOrder = {
    "eacknow",
    "treturn"
})
public class DTZEINRCreateResponse {

    @XmlElement(name = "E_ACKNOW", required = true)
    protected DTACKNOW2 eacknow;
    @XmlElement(name = "T_RETURN")
    protected List<DTZEINRCreateResponse.TRETURN> treturn;

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
     * Gets the value of the treturn property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the treturn property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTRETURN().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DTZEINRCreateResponse.TRETURN }
     * 
     * 
     */
    public List<DTZEINRCreateResponse.TRETURN> getTRETURN() {
        if (treturn == null) {
            treturn = new ArrayList<DTZEINRCreateResponse.TRETURN>();
        }
        return this.treturn;
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
     *         &lt;element name="STATUS" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="MESSAGE" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
        "zeiar",
        "status",
        "message"
    })
    public static class TRETURN {

        @XmlElement(name = "MATNR", required = true)
        protected String matnr;
        @XmlElement(name = "ZEINR", required = true)
        protected String zeinr;
        @XmlElement(name = "ZEIAR", required = true)
        protected String zeiar;
        @XmlElement(name = "STATUS", required = true)
        protected String status;
        @XmlElement(name = "MESSAGE", required = true)
        protected String message;

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
