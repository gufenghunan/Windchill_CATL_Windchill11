
package com.catl.integration.pi.bom.change;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DT_BOM_CHANGECreateRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DT_BOM_CHANGECreateRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SYSNAME" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="T_BOM_IN" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="AENNR" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="MATNR" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="BMENG" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="IDNRK" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="MENGE" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="ZTDAI" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="ZTMENGE" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
@XmlType(name = "DT_BOM_CHANGECreateRequest", propOrder = {
    "sysname",
    "tbomin"
})
public class DTBOMCHANGECreateRequest {

    @XmlElement(name = "SYSNAME", required = true)
    protected String sysname;
    @XmlElement(name = "T_BOM_IN")
    protected List<DTBOMCHANGECreateRequest.TBOMIN> tbomin;

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
     * Gets the value of the tbomin property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the tbomin property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTBOMIN().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DTBOMCHANGECreateRequest.TBOMIN }
     * 
     * 
     */
    public List<DTBOMCHANGECreateRequest.TBOMIN> getTBOMIN() {
        if (tbomin == null) {
            tbomin = new ArrayList<DTBOMCHANGECreateRequest.TBOMIN>();
        }
        return this.tbomin;
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
     *         &lt;element name="BMENG" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="IDNRK" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="MENGE" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="ZTDAI" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="ZTMENGE" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
        "bmeng",
        "idnrk",
        "menge",
        "ztdai",
        "ztmenge"
    })
    public static class TBOMIN {

        @XmlElement(name = "AENNR", required = true)
        protected String aennr;
        @XmlElement(name = "MATNR", required = true)
        protected String matnr;
        @XmlElement(name = "BMENG", required = true)
        protected String bmeng;
        @XmlElement(name = "IDNRK", required = true)
        protected String idnrk;
        @XmlElement(name = "MENGE", required = true)
        protected String menge;
        @XmlElement(name = "ZTDAI", required = true)
        protected String ztdai;
        @XmlElement(name = "ZTMENGE", required = true)
        protected String ztmenge;

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
         * Gets the value of the bmeng property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getBMENG() {
            return bmeng;
        }

        /**
         * Sets the value of the bmeng property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setBMENG(String value) {
            this.bmeng = value;
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
         * Gets the value of the ztmenge property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getZTMENGE() {
            return ztmenge;
        }

        /**
         * Sets the value of the ztmenge property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setZTMENGE(String value) {
            this.ztmenge = value;
        }

    }

}
