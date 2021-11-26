
package com.catl.integration.pi.part.fae;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>DT_ZFAECreateRequest complex type的 Java 类。
 * 
 * <p>以下模式片段指定包含在此类中的预期内容。
 * 
 * <pre>
 * &lt;complexType name="DT_ZFAECreateRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SYSNAME" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="T_ZFAE" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="MATNR" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="ZFAE" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
@XmlType(name = "DT_ZFAECreateRequest", propOrder = {
    "sysname",
    "tzfae"
})
public class DTZFAECreateRequest {

    @XmlElement(name = "SYSNAME", required = true)
    protected String sysname;
    @XmlElement(name = "T_ZFAE")
    protected List<DTZFAECreateRequest.TZFAE> tzfae;

    /**
     * 获取sysname属性的值。
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
     * 设置sysname属性的值。
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
     * Gets the value of the tzfae property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the tzfae property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTZFAE().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DTZFAECreateRequest.TZFAE }
     * 
     * 
     */
    public List<DTZFAECreateRequest.TZFAE> getTZFAE() {
        if (tzfae == null) {
            tzfae = new ArrayList<DTZFAECreateRequest.TZFAE>();
        }
        return this.tzfae;
    }


    /**
     * <p>anonymous complex type的 Java 类。
     * 
     * <p>以下模式片段指定包含在此类中的预期内容。
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="MATNR" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="ZFAE" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
        "zfae"
    })
    public static class TZFAE {

        @XmlElement(name = "MATNR", required = true)
        protected String matnr;
        @XmlElement(name = "ZFAE", required = true)
        protected String zfae;

        /**
         * 获取matnr属性的值。
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
         * 设置matnr属性的值。
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
         * 获取zfae属性的值。
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
         * 设置zfae属性的值。
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setZFAE(String value) {
            this.zfae = value;
        }

    }

}
