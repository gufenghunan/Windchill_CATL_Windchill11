
package com.catl.integration.pi.ecn.erp;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 * 
 * 
 * 
 * <pre>
 * &lt;complexType name="DT_Ecndate_request">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="T_ECN" maxOccurs="unbounded">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="MATNR" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="AENNR" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="Result" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
@XmlType(name = "DT_Ecndate_request", propOrder = {
    "tecn"
})
public class DTEcndateRequest {

    @XmlElement(name = "T_ECN", required = true)
    protected List<DTEcndateRequest.TECN> tecn;

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
     * {@link DTEcndateRequest.TECN }
     * 
     * 
     */
    public List<DTEcndateRequest.TECN> getTECN() {
        if (tecn == null) {
            tecn = new ArrayList<DTEcndateRequest.TECN>();
        }
        return this.tecn;
    }


    /**
     * 
     * 
     * 
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="MATNR" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="AENNR" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="Result" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
        "aennr",
        "result"
    })
    public static class TECN {

        @XmlElement(name = "MATNR", required = true)
        protected String matnr;
        @XmlElement(name = "AENNR", required = true)
        protected String aennr;
        @XmlElement(name = "Result", required = true)
        protected String result;

        /**
         * 
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
         * 
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
         * 
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
         * 
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
         * 
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getResult() {
            return result;
        }

        /**
         * 
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setResult(String value) {
            this.result = value;
        }

    }

}
