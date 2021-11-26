/**
 * PartInfoItem.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.catl.integration.trp.client;

public class PartInfoItem  implements java.io.Serializable {
    private java.lang.String cellCapacity;

    private java.lang.String cellModel;

    private java.lang.String higth;

    private java.lang.String length;

    private java.lang.String linkType;

    private java.lang.String materialName;

    private java.lang.String moduleQuantity;

    private java.lang.String nominalVoltage;

    private java.lang.String normalEnerge;

    private java.lang.String pnCode;

    private java.lang.String width;

    public PartInfoItem() {
    }

    public PartInfoItem(
           java.lang.String cellCapacity,
           java.lang.String cellModel,
           java.lang.String higth,
           java.lang.String length,
           java.lang.String linkType,
           java.lang.String materialName,
           java.lang.String moduleQuantity,
           java.lang.String nominalVoltage,
           java.lang.String normalEnerge,
           java.lang.String pnCode,
           java.lang.String width) {
           this.cellCapacity = cellCapacity;
           this.cellModel = cellModel;
           this.higth = higth;
           this.length = length;
           this.linkType = linkType;
           this.materialName = materialName;
           this.moduleQuantity = moduleQuantity;
           this.nominalVoltage = nominalVoltage;
           this.normalEnerge = normalEnerge;
           this.pnCode = pnCode;
           this.width = width;
    }


    /**
     * Gets the cellCapacity value for this PartInfoItem.
     * 
     * @return cellCapacity
     */
    public java.lang.String getCellCapacity() {
        return cellCapacity;
    }


    /**
     * Sets the cellCapacity value for this PartInfoItem.
     * 
     * @param cellCapacity
     */
    public void setCellCapacity(java.lang.String cellCapacity) {
        this.cellCapacity = cellCapacity;
    }


    /**
     * Gets the cellModel value for this PartInfoItem.
     * 
     * @return cellModel
     */
    public java.lang.String getCellModel() {
        return cellModel;
    }


    /**
     * Sets the cellModel value for this PartInfoItem.
     * 
     * @param cellModel
     */
    public void setCellModel(java.lang.String cellModel) {
        this.cellModel = cellModel;
    }


    /**
     * Gets the higth value for this PartInfoItem.
     * 
     * @return higth
     */
    public java.lang.String getHigth() {
        return higth;
    }


    /**
     * Sets the higth value for this PartInfoItem.
     * 
     * @param higth
     */
    public void setHigth(java.lang.String higth) {
        this.higth = higth;
    }


    /**
     * Gets the length value for this PartInfoItem.
     * 
     * @return length
     */
    public java.lang.String getLength() {
        return length;
    }


    /**
     * Sets the length value for this PartInfoItem.
     * 
     * @param length
     */
    public void setLength(java.lang.String length) {
        this.length = length;
    }


    /**
     * Gets the linkType value for this PartInfoItem.
     * 
     * @return linkType
     */
    public java.lang.String getLinkType() {
        return linkType;
    }


    /**
     * Sets the linkType value for this PartInfoItem.
     * 
     * @param linkType
     */
    public void setLinkType(java.lang.String linkType) {
        this.linkType = linkType;
    }


    /**
     * Gets the materialName value for this PartInfoItem.
     * 
     * @return materialName
     */
    public java.lang.String getMaterialName() {
        return materialName;
    }


    /**
     * Sets the materialName value for this PartInfoItem.
     * 
     * @param materialName
     */
    public void setMaterialName(java.lang.String materialName) {
        this.materialName = materialName;
    }


    /**
     * Gets the moduleQuantity value for this PartInfoItem.
     * 
     * @return moduleQuantity
     */
    public java.lang.String getModuleQuantity() {
        return moduleQuantity;
    }


    /**
     * Sets the moduleQuantity value for this PartInfoItem.
     * 
     * @param moduleQuantity
     */
    public void setModuleQuantity(java.lang.String moduleQuantity) {
        this.moduleQuantity = moduleQuantity;
    }


    /**
     * Gets the nominalVoltage value for this PartInfoItem.
     * 
     * @return nominalVoltage
     */
    public java.lang.String getNominalVoltage() {
        return nominalVoltage;
    }


    /**
     * Sets the nominalVoltage value for this PartInfoItem.
     * 
     * @param nominalVoltage
     */
    public void setNominalVoltage(java.lang.String nominalVoltage) {
        this.nominalVoltage = nominalVoltage;
    }


    /**
     * Gets the normalEnerge value for this PartInfoItem.
     * 
     * @return normalEnerge
     */
    public java.lang.String getNormalEnerge() {
        return normalEnerge;
    }


    /**
     * Sets the normalEnerge value for this PartInfoItem.
     * 
     * @param normalEnerge
     */
    public void setNormalEnerge(java.lang.String normalEnerge) {
        this.normalEnerge = normalEnerge;
    }


    /**
     * Gets the pnCode value for this PartInfoItem.
     * 
     * @return pnCode
     */
    public java.lang.String getPnCode() {
        return pnCode;
    }


    /**
     * Sets the pnCode value for this PartInfoItem.
     * 
     * @param pnCode
     */
    public void setPnCode(java.lang.String pnCode) {
        this.pnCode = pnCode;
    }


    /**
     * Gets the width value for this PartInfoItem.
     * 
     * @return width
     */
    public java.lang.String getWidth() {
        return width;
    }


    /**
     * Sets the width value for this PartInfoItem.
     * 
     * @param width
     */
    public void setWidth(java.lang.String width) {
        this.width = width;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof PartInfoItem)) return false;
        PartInfoItem other = (PartInfoItem) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.cellCapacity==null && other.getCellCapacity()==null) || 
             (this.cellCapacity!=null &&
              this.cellCapacity.equals(other.getCellCapacity()))) &&
            ((this.cellModel==null && other.getCellModel()==null) || 
             (this.cellModel!=null &&
              this.cellModel.equals(other.getCellModel()))) &&
            ((this.higth==null && other.getHigth()==null) || 
             (this.higth!=null &&
              this.higth.equals(other.getHigth()))) &&
            ((this.length==null && other.getLength()==null) || 
             (this.length!=null &&
              this.length.equals(other.getLength()))) &&
            ((this.linkType==null && other.getLinkType()==null) || 
             (this.linkType!=null &&
              this.linkType.equals(other.getLinkType()))) &&
            ((this.materialName==null && other.getMaterialName()==null) || 
             (this.materialName!=null &&
              this.materialName.equals(other.getMaterialName()))) &&
            ((this.moduleQuantity==null && other.getModuleQuantity()==null) || 
             (this.moduleQuantity!=null &&
              this.moduleQuantity.equals(other.getModuleQuantity()))) &&
            ((this.nominalVoltage==null && other.getNominalVoltage()==null) || 
             (this.nominalVoltage!=null &&
              this.nominalVoltage.equals(other.getNominalVoltage()))) &&
            ((this.normalEnerge==null && other.getNormalEnerge()==null) || 
             (this.normalEnerge!=null &&
              this.normalEnerge.equals(other.getNormalEnerge()))) &&
            ((this.pnCode==null && other.getPnCode()==null) || 
             (this.pnCode!=null &&
              this.pnCode.equals(other.getPnCode()))) &&
            ((this.width==null && other.getWidth()==null) || 
             (this.width!=null &&
              this.width.equals(other.getWidth())));
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
        if (getCellCapacity() != null) {
            _hashCode += getCellCapacity().hashCode();
        }
        if (getCellModel() != null) {
            _hashCode += getCellModel().hashCode();
        }
        if (getHigth() != null) {
            _hashCode += getHigth().hashCode();
        }
        if (getLength() != null) {
            _hashCode += getLength().hashCode();
        }
        if (getLinkType() != null) {
            _hashCode += getLinkType().hashCode();
        }
        if (getMaterialName() != null) {
            _hashCode += getMaterialName().hashCode();
        }
        if (getModuleQuantity() != null) {
            _hashCode += getModuleQuantity().hashCode();
        }
        if (getNominalVoltage() != null) {
            _hashCode += getNominalVoltage().hashCode();
        }
        if (getNormalEnerge() != null) {
            _hashCode += getNormalEnerge().hashCode();
        }
        if (getPnCode() != null) {
            _hashCode += getPnCode().hashCode();
        }
        if (getWidth() != null) {
            _hashCode += getWidth().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(PartInfoItem.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://service.in.ws.trp.catl.com/", "item"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cellCapacity");
        elemField.setXmlName(new javax.xml.namespace.QName("", "cellCapacity"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cellModel");
        elemField.setXmlName(new javax.xml.namespace.QName("", "cellModel"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("higth");
        elemField.setXmlName(new javax.xml.namespace.QName("", "higth"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("length");
        elemField.setXmlName(new javax.xml.namespace.QName("", "length"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("linkType");
        elemField.setXmlName(new javax.xml.namespace.QName("", "linkType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("materialName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "materialName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("moduleQuantity");
        elemField.setXmlName(new javax.xml.namespace.QName("", "moduleQuantity"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("nominalVoltage");
        elemField.setXmlName(new javax.xml.namespace.QName("", "nominalVoltage"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("normalEnerge");
        elemField.setXmlName(new javax.xml.namespace.QName("", "normalEnerge"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pnCode");
        elemField.setXmlName(new javax.xml.namespace.QName("", "pnCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("width");
        elemField.setXmlName(new javax.xml.namespace.QName("", "width"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
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
