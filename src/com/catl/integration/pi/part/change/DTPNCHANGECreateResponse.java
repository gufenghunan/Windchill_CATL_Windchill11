
package com.catl.integration.pi.part.change;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DT_PN_CHANGECreateResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DT_PN_CHANGECreateResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="E_ACKNOW" type="{http://atlbattery.com/PLM/Basic}E_ACKNOW"/>
 *         &lt;element name="T_RETURN" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="MANDT" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="MATNR" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="CDATE" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="CTIME" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
@XmlType(name = "DT_PN_CHANGECreateResponse", propOrder = {
    "eacknow",
    "treturn"
})
public class DTPNCHANGECreateResponse {

    @XmlElement(name = "E_ACKNOW", required = true)
    protected EACKNOW eacknow;
    @XmlElement(name = "T_RETURN")
    protected List<DTPNCHANGECreateResponse.TRETURN> treturn;

    /**
     * Gets the value of the eacknow property.
     * 
     * @return
     *     possible object is
     *     {@link EACKNOW }
     *     
     */
    public EACKNOW getEACKNOW() {
        return eacknow;
    }

    /**
     * Sets the value of the eacknow property.
     * 
     * @param value
     *     allowed object is
     *     {@link EACKNOW }
     *     
     */
    public void setEACKNOW(EACKNOW value) {
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
     * {@link DTPNCHANGECreateResponse.TRETURN }
     * 
     * 
     */
    public List<DTPNCHANGECreateResponse.TRETURN> getTRETURN() {
        if (treturn == null) {
            treturn = new ArrayList<DTPNCHANGECreateResponse.TRETURN>();
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
     *         &lt;element name="MANDT" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="MATNR" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="CDATE" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="CTIME" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
        "mandt",
        "matnr",
        "cdate",
        "ctime",
        "status",
        "message"
    })
    public static class TRETURN {

        @XmlElement(name = "MANDT", required = true)
        protected String mandt;
        @XmlElement(name = "MATNR", required = true)
        protected String matnr;
        @XmlElement(name = "CDATE", required = true)
        protected String cdate;
        @XmlElement(name = "CTIME", required = true)
        protected String ctime;
        @XmlElement(name = "STATUS", required = true)
        protected String status;
        @XmlElement(name = "MESSAGE", required = true)
        protected String message;

        /**
         * Gets the value of the mandt property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getMANDT() {
            return mandt;
        }

        /**
         * Sets the value of the mandt property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setMANDT(String value) {
            this.mandt = value;
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

        /**
         * Gets the value of the cdate property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getCDATE() {
            return cdate;
        }

        /**
         * Sets the value of the cdate property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setCDATE(String value) {
            this.cdate = value;
        }

        /**
         * Gets the value of the ctime property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getCTIME() {
            return ctime;
        }

        /**
         * Sets the value of the ctime property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setCTIME(String value) {
            this.ctime = value;
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
