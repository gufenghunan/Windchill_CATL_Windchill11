/**
 * TRPInfo.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.catl.integration.trp.client;

public class TRPInfo  implements java.io.Serializable {
    private com.catl.integration.trp.client.PartInfoItem[] testSampleInfo;

    public TRPInfo() {
    }

    public TRPInfo(
           com.catl.integration.trp.client.PartInfoItem[] testSampleInfo) {
           this.testSampleInfo = testSampleInfo;
    }


    /**
     * Gets the testSampleInfo value for this TRPInfo.
     * 
     * @return testSampleInfo
     */
    public com.catl.integration.trp.client.PartInfoItem[] getTestSampleInfo() {
        return testSampleInfo;
    }


    /**
     * Sets the testSampleInfo value for this TRPInfo.
     * 
     * @param testSampleInfo
     */
    public void setTestSampleInfo(com.catl.integration.trp.client.PartInfoItem[] testSampleInfo) {
        this.testSampleInfo = testSampleInfo;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof TRPInfo)) return false;
        TRPInfo other = (TRPInfo) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.testSampleInfo==null && other.getTestSampleInfo()==null) || 
             (this.testSampleInfo!=null &&
              java.util.Arrays.equals(this.testSampleInfo, other.getTestSampleInfo())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getTestSampleInfo() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getTestSampleInfo());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getTestSampleInfo(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(TRPInfo.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://service.in.ws.trp.catl.com/", "syncTestSampleInfo"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("testSampleInfo");
        elemField.setXmlName(new javax.xml.namespace.QName("", "testSampleInfo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://service.in.ws.trp.catl.com/", "item"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("", "item"));
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
