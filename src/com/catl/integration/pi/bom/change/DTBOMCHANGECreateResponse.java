
package com.catl.integration.pi.bom.change;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DT_BOM_CHANGECreateResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DT_BOM_CHANGECreateResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="E_ACKNOW" type="{http://atlbattery.com/PLM/Basic}E_ACKNOW"/>
 *         &lt;element name="T_BOM_OUT" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="AENNR" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="MATNR" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="IDNRK" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="MENGE" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="ZTDAI" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
@XmlType(name = "DT_BOM_CHANGECreateResponse", propOrder = {
    "eacknow",
    "tbomout"
})
public class DTBOMCHANGECreateResponse {

    @XmlElement(name = "E_ACKNOW", required = true)
    protected EACKNOW eacknow;
    @XmlElement(name = "T_BOM_OUT")
    protected List<DTBOMCHANGECreateResponse.TBOMOUT> tbomout;

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
     * Gets the value of the tbomout property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the tbomout property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTBOMOUT().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DTBOMCHANGECreateResponse.TBOMOUT }
     * 
     * 
     */
    public List<DTBOMCHANGECreateResponse.TBOMOUT> getTBOMOUT() {
        if (tbomout == null) {
            tbomout = new ArrayList<DTBOMCHANGECreateResponse.TBOMOUT>();
        }
        return this.tbomout;
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
     *         &lt;element name="AENNR" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="MATNR" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="IDNRK" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="MENGE" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="ZTDAI" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
        "aennr",
        "matnr",
        "idnrk",
        "menge",
        "ztdai",
        "status",
        "message"
    })
    public static class TBOMOUT {

        @XmlElement(name = "AENNR", required = true)
        protected String aennr;
        @XmlElement(name = "MATNR", required = true)
        protected String matnr;
        @XmlElement(name = "IDNRK", required = true)
        protected String idnrk;
        @XmlElement(name = "MENGE", required = true)
        protected String menge;
        @XmlElement(name = "ZTDAI", required = true)
        protected String ztdai;
        @XmlElement(name = "STATUS", required = true)
        protected String status;
        @XmlElement(name = "MESSAGE", required = true)
        protected String message;

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
         * Gets the value of the idnrk property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getIDNRK() {
            return idnrk;
        }

        /**
         * Sets the value of the idnrk property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setIDNRK(String value) {
            this.idnrk = value;
        }

        /**
         * Gets the value of the menge property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getMENGE() {
            return menge;
        }

        /**
         * Sets the value of the menge property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setMENGE(String value) {
            this.menge = value;
        }

        /**
         * Gets the value of the ztdai property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getZTDAI() {
            return ztdai;
        }

        /**
         * Sets the value of the ztdai property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setZTDAI(String value) {
            this.ztdai = value;
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
