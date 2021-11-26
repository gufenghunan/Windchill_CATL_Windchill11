
package com.catl.integration.pi.part.create;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DT_MATNRCreateRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DT_MATNRCreateRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SYSNAME" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="T_MATNR" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="MATNR" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="BISMT" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="MAKTX_ZH" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="MAKTX_EN" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="ZZWLGG" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="ERNAM" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="MEINS" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="ZEIAR" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="BESKZ" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="MATKL" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="ZEINR" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="ZMODULE" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="ZCPRL" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="ZDXNL" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="ZMCDY" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="ZBCDY" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="ZZDXCL_CODE" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="ZBQFS" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="ZMZ" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="ZFAE" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="ZHWV" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="ZSWV" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="ZPARV" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="ZLENGTH" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="ZMATNR" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
@XmlType(name = "DT_MATNRCreateRequest", propOrder = {
    "sysname",
    "tmatnr"
})
public class DTMATNRCreateRequest {

    @XmlElement(name = "SYSNAME", required = true)
    protected String sysname;
    @XmlElement(name = "T_MATNR")
    protected List<DTMATNRCreateRequest.TMATNR> tmatnr;

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
     * Gets the value of the tmatnr property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the tmatnr property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTMATNR().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DTMATNRCreateRequest.TMATNR }
     * 
     * 
     */
    public List<DTMATNRCreateRequest.TMATNR> getTMATNR() {
        if (tmatnr == null) {
            tmatnr = new ArrayList<DTMATNRCreateRequest.TMATNR>();
        }
        return this.tmatnr;
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
     *         &lt;element name="BISMT" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="MAKTX_ZH" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="MAKTX_EN" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="ZZWLGG" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="ERNAM" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="MEINS" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="ZEIAR" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="BESKZ" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="MATKL" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="ZEINR" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="ZMODULE" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="ZCPRL" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="ZDXNL" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="ZMCDY" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="ZBCDY" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="ZZDXCL_CODE" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="ZBQFS" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="ZMZ" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="ZFAE" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="ZHWV" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="ZSWV" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="ZPARV" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="ZLENGTH" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="ZMATNR" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
        "bismt",
        "maktxzh",
        "maktxen",
        "zzwlgg",
        "ernam",
        "meins",
        "zeiar",
        "beskz",
        "matkl",
        "zeinr",
        "zmodule",
        "zcprl",
        "zdxnl",
        "zmcdy",
        "zbcdy",
        "zzdxclcode",
        "zbqfs",
        "zmz",
        "zfae",
        "zhwv",
        "zswv",
        "zparv",
        "zlength",
        "zmatnr"
    })
    public static class TMATNR {

        @XmlElement(name = "MATNR", required = true)
        protected String matnr;
        @XmlElement(name = "BISMT", required = true)
        protected String bismt;
        @XmlElement(name = "MAKTX_ZH", required = true)
        protected String maktxzh;
        @XmlElement(name = "MAKTX_EN", required = true)
        protected String maktxen;
        @XmlElement(name = "ZZWLGG", required = true)
        protected String zzwlgg;
        @XmlElement(name = "ERNAM", required = true)
        protected String ernam;
        @XmlElement(name = "MEINS", required = true)
        protected String meins;
        @XmlElement(name = "ZEIAR", required = true)
        protected String zeiar;
        @XmlElement(name = "BESKZ", required = true)
        protected String beskz;
        @XmlElement(name = "MATKL", required = true)
        protected String matkl;
        @XmlElement(name = "ZEINR", required = true)
        protected String zeinr;
        @XmlElement(name = "ZMODULE", required = true)
        protected String zmodule;
        @XmlElement(name = "ZCPRL", required = true)
        protected String zcprl;
        @XmlElement(name = "ZDXNL", required = true)
        protected String zdxnl;
        @XmlElement(name = "ZMCDY", required = true)
        protected String zmcdy;
        @XmlElement(name = "ZBCDY", required = true)
        protected String zbcdy;
        @XmlElement(name = "ZZDXCL_CODE", required = true)
        protected String zzdxclcode;
        @XmlElement(name = "ZBQFS", required = true)
        protected String zbqfs;
        @XmlElement(name = "ZMZ", required = true)
        protected String zmz;
        @XmlElement(name = "ZFAE", required = true)
        protected String zfae;
        @XmlElement(name = "ZHWV", required = true)
        protected String zhwv;
        @XmlElement(name = "ZSWV", required = true)
        protected String zswv;
        @XmlElement(name = "ZPARV", required = true)
        protected String zparv;
        @XmlElement(name = "ZLENGTH", required = true)
        protected String zlength;
        @XmlElement(name = "ZMATNR", required = true)
        protected String zmatnr;

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
         * Gets the value of the bismt property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getBISMT() {
            return bismt;
        }

        /**
         * Sets the value of the bismt property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setBISMT(String value) {
            this.bismt = value;
        }

        /**
         * Gets the value of the maktxzh property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getMAKTXZH() {
            return maktxzh;
        }

        /**
         * Sets the value of the maktxzh property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setMAKTXZH(String value) {
            this.maktxzh = value;
        }

        /**
         * Gets the value of the maktxen property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getMAKTXEN() {
            return maktxen;
        }

        /**
         * Sets the value of the maktxen property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setMAKTXEN(String value) {
            this.maktxen = value;
        }

        /**
         * Gets the value of the zzwlgg property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getZZWLGG() {
            return zzwlgg;
        }

        /**
         * Sets the value of the zzwlgg property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setZZWLGG(String value) {
            this.zzwlgg = value;
        }

        /**
         * Gets the value of the ernam property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getERNAM() {
            return ernam;
        }

        /**
         * Sets the value of the ernam property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setERNAM(String value) {
            this.ernam = value;
        }

        /**
         * Gets the value of the meins property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getMEINS() {
            return meins;
        }

        /**
         * Sets the value of the meins property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setMEINS(String value) {
            this.meins = value;
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
         * Gets the value of the matkl property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getMATKL() {
            return matkl;
        }

        /**
         * Sets the value of the matkl property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setMATKL(String value) {
            this.matkl = value;
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
         * Gets the value of the zmodule property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getZMODULE() {
            return zmodule;
        }

        /**
         * Sets the value of the zmodule property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setZMODULE(String value) {
            this.zmodule = value;
        }

        /**
         * Gets the value of the zcprl property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getZCPRL() {
            return zcprl;
        }

        /**
         * Sets the value of the zcprl property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setZCPRL(String value) {
            this.zcprl = value;
        }

        /**
         * Gets the value of the zdxnl property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getZDXNL() {
            return zdxnl;
        }

        /**
         * Sets the value of the zdxnl property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setZDXNL(String value) {
            this.zdxnl = value;
        }

        /**
         * Gets the value of the zmcdy property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getZMCDY() {
            return zmcdy;
        }

        /**
         * Sets the value of the zmcdy property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setZMCDY(String value) {
            this.zmcdy = value;
        }

        /**
         * Gets the value of the zbcdy property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getZBCDY() {
            return zbcdy;
        }

        /**
         * Sets the value of the zbcdy property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setZBCDY(String value) {
            this.zbcdy = value;
        }

        /**
         * Gets the value of the zzdxclcode property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getZZDXCLCODE() {
            return zzdxclcode;
        }

        /**
         * Sets the value of the zzdxclcode property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setZZDXCLCODE(String value) {
            this.zzdxclcode = value;
        }

        /**
         * Gets the value of the zbqfs property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getZBQFS() {
            return zbqfs;
        }

        /**
         * Sets the value of the zbqfs property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setZBQFS(String value) {
            this.zbqfs = value;
        }

        /**
         * Gets the value of the zmz property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getZMZ() {
            return zmz;
        }

        /**
         * Sets the value of the zmz property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setZMZ(String value) {
            this.zmz = value;
        }

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
         * Gets the value of the zhwv property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getZHWV() {
            return zhwv;
        }

        /**
         * Sets the value of the zhwv property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setZHWV(String value) {
            this.zhwv = value;
        }

        /**
         * Gets the value of the zswv property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getZSWV() {
            return zswv;
        }

        /**
         * Sets the value of the zswv property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setZSWV(String value) {
            this.zswv = value;
        }

        /**
         * Gets the value of the zparv property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getZPARV() {
            return zparv;
        }

        /**
         * Sets the value of the zparv property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setZPARV(String value) {
            this.zparv = value;
        }

        /**
         * Gets the value of the zlength property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getZLENGTH() {
            return zlength;
        }

        /**
         * Sets the value of the zlength property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setZLENGTH(String value) {
            this.zlength = value;
        }

        /**
         * Gets the value of the zmatnr property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getZMATNR() {
            return zmatnr;
        }

        /**
         * Sets the value of the zmatnr property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setZMATNR(String value) {
            this.zmatnr = value;
        }

    }

}
