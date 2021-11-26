package com.catl.promotion.bean;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.sql.Types;

import com.catl.promotion.resource.promotionResource;
import com.ptc.xworks.xmlobject.BaseXmlObject;
import com.ptc.xworks.xmlobject.annotation.AttributeClientValidator;
import com.ptc.xworks.xmlobject.annotation.EnumerationProviderType;
import com.ptc.xworks.xmlobject.annotation.ExtraOption;
import com.ptc.xworks.xmlobject.annotation.GuiComponentType;
import com.ptc.xworks.xmlobject.annotation.ResourceBundleKey;
import com.ptc.xworks.xmlobject.annotation.TableColumnConfig;
import com.ptc.xworks.xmlobject.annotation.AttributeClientValidatorType;
import com.ptc.xworks.xmlobject.annotation.WebFormAttribute;
import com.ptc.xworks.xmlobject.annotation.XmlObjectColumnMarker;
import com.ptc.xworks.xmlobject.annotation.XmlObjectMarker;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import com.ptc.xworks.xmlobject.annotation.EnumerationProvider;

@XmlObjectMarker(tableName="A_XML_SOURCECHANGEOBJECTS", columns={
		@XmlObjectColumnMarker(attribute="pboId",columnName="PBOID",javaType=Long.class, sqlType=Types.INTEGER),
		@XmlObjectColumnMarker(attribute="partNumber",columnName="PARTNUMBER",javaType=String.class, sqlType=Types.VARCHAR),
		@XmlObjectColumnMarker(attribute="partName",columnName="PARTNAME",javaType=String.class, sqlType=Types.VARCHAR),
		@XmlObjectColumnMarker(attribute="partBranchId",columnName="PARTBRANCHID",javaType=Long.class, sqlType=Types.INTEGER),
		@XmlObjectColumnMarker(attribute="partCreator",columnName="PARTCREATOR",javaType=String.class, sqlType=Types.VARCHAR),
		@XmlObjectColumnMarker(attribute="partModifier",columnName="PARTMODIFIER",javaType=String.class, sqlType=Types.VARCHAR),
		@XmlObjectColumnMarker(attribute="sourceBefore",columnName="SOURCEBEFORE",javaType=String.class, sqlType=Types.VARCHAR),
		@XmlObjectColumnMarker(attribute="sourceAfter",columnName="SOURCEAFTER",javaType=String.class, sqlType=Types.VARCHAR),
		@XmlObjectColumnMarker(attribute="cause",columnName="CHANGEREASON",javaType=String.class, sqlType=Types.VARCHAR)
})
public class SourceChangeXmlObjectBean extends BaseXmlObject{

	static final long serialVersionUID = 1L;

	private static final long EXTERNALIZATION_VERSION_UID = -3456787654324492837L;
	
	@WebFormAttribute(
			label = @ResourceBundleKey(resourceBundle = "com.catl.promotion.resource.promotionResource", key = promotionResource.SC_CONSTANT_6),
			tooltip = @ResourceBundleKey(resourceBundle = "com.catl.promotion.resource.promotionResource", key = promotionResource.SC_CONSTANT_6),
			createGui = GuiComponentType.TEXT,
			editGui = GuiComponentType.TEXT,
			viewGui = GuiComponentType.TEXT,
			textSize = 30,
			maxLength = 100,
			clientValidator = @AttributeClientValidator(type = AttributeClientValidatorType.REGEXP, value = "^[\\w]{10,}$")
		)
	@XStreamOmitField
	private Long pboId;//PBO的ID
	
	@WebFormAttribute(
			label = @ResourceBundleKey(resourceBundle = "com.catl.promotion.resource.promotionResource", key = promotionResource.SC_CONSTANT_8),
			tooltip = @ResourceBundleKey(resourceBundle = "com.catl.promotion.resource.promotionResource", key = promotionResource.SC_CONSTANT_8),
			createGui = GuiComponentType.TEXT,
			editGui = GuiComponentType.TEXT,
			viewGui = GuiComponentType.TEXT,
			textSize = 30,
			maxLength = 100,
			tableColumnConfig = @TableColumnConfig(width = 150)
		)
	@XStreamOmitField
	private String partNumber;//物料编码
	
	@WebFormAttribute(
			label = @ResourceBundleKey(resourceBundle = "com.catl.promotion.resource.promotionResource", key = promotionResource.SC_CONSTANT_9),
			tooltip = @ResourceBundleKey(resourceBundle = "com.catl.promotion.resource.promotionResource", key = promotionResource.SC_CONSTANT_9),
			createGui = GuiComponentType.TEXT,
			editGui = GuiComponentType.TEXT,
			viewGui = GuiComponentType.TEXT,
			textSize = 30,
			maxLength = 100,
			tableColumnConfig = @TableColumnConfig(width = 150)
		)
	@XStreamOmitField
	private String partName;//物料名称
	
	@WebFormAttribute(
			label = @ResourceBundleKey(resourceBundle = "com.catl.promotion.resource.promotionResource", key = promotionResource.SC_CONSTANT_10),
			tooltip = @ResourceBundleKey(resourceBundle = "com.catl.promotion.resource.promotionResource", key = promotionResource.SC_CONSTANT_10),
			createGui = GuiComponentType.TEXT,
			editGui = GuiComponentType.TEXT,
			viewGui = GuiComponentType.TEXT,
			textSize = 30,
			maxLength = 100		
		)
	@XStreamOmitField
	private Long partBranchId;//部件的Branch ID
	
	@WebFormAttribute(
			label = @ResourceBundleKey(resourceBundle = "com.catl.promotion.resource.promotionResource", key = promotionResource.SC_CONSTANT_11),
			tooltip = @ResourceBundleKey(resourceBundle = "com.catl.promotion.resource.promotionResource", key = promotionResource.SC_CONSTANT_11),
			createGui = GuiComponentType.TEXT,
			editGui = GuiComponentType.TEXT,
			viewGui = GuiComponentType.TEXT,
			textSize = 10,
			maxLength = 100,
			tableColumnConfig = @TableColumnConfig(width = 50)			
		)
	@XStreamOmitField
	private String partCreator;//申请人
	
	@WebFormAttribute(
			label = @ResourceBundleKey(resourceBundle = "com.catl.promotion.resource.promotionResource", key = promotionResource.SC_CONSTANT_12),
			tooltip = @ResourceBundleKey(resourceBundle = "com.catl.promotion.resource.promotionResource", key = promotionResource.SC_CONSTANT_12),
			createGui = GuiComponentType.TEXT,
			editGui = GuiComponentType.TEXT,
			viewGui = GuiComponentType.TEXT,
			textSize = 10,
			maxLength = 100,
			tableColumnConfig = @TableColumnConfig(width = 50)			
		)
	@XStreamOmitField
	private String partModifier;//修改者
	
	@WebFormAttribute(
			label = @ResourceBundleKey(resourceBundle = "com.catl.promotion.resource.promotionResource", key = promotionResource.SC_CONSTANT_13),
			tooltip = @ResourceBundleKey(resourceBundle = "com.catl.promotion.resource.promotionResource", key = promotionResource.SC_CONSTANT_13),
			createGui = GuiComponentType.TEXT,
			editGui = GuiComponentType.TEXT,
			viewGui = GuiComponentType.TEXT,
			enumerationProvider = @EnumerationProvider(provierType = EnumerationProviderType.GLOBAL_ENUMERATION, value = "source"),
			extraOptions = { @ExtraOption(name = ExtraOption.COMBOBOX_SHOW_BLANK_OPTION, value = "true") },
			textSize = 10,
			maxLength = 100,
			tableColumnConfig = @TableColumnConfig(width = 50)			
		)
	@XStreamOmitField
	private String sourceBefore;//采购类型变更前
	
	@WebFormAttribute(
			label = @ResourceBundleKey(resourceBundle = "com.catl.promotion.resource.promotionResource", key = promotionResource.SC_CONSTANT_14),
			tooltip = @ResourceBundleKey(resourceBundle = "com.catl.promotion.resource.promotionResource", key = promotionResource.SC_CONSTANT_14),
			createGui = GuiComponentType.COMBOBOX,
			editGui = GuiComponentType.COMBOBOX,
			viewGui = GuiComponentType.COMBOBOX,
			enumerationProvider = @EnumerationProvider(provierType = EnumerationProviderType.GLOBAL_ENUMERATION, value = "source"),
		    extraOptions = { @ExtraOption(name = ExtraOption.COMBOBOX_SHOW_BLANK_OPTION, value = "true") },
		    required = true,
		    tableColumnConfig = @TableColumnConfig(width = 50)		
		)
	@XStreamOmitField
	private String sourceAfter;//采购类型变更后
	
	@WebFormAttribute(
			label = @ResourceBundleKey(resourceBundle = "com.catl.promotion.resource.promotionResource", key = promotionResource.SC_CONSTANT_15),
			tooltip = @ResourceBundleKey(resourceBundle = "com.catl.promotion.resource.promotionResource", key = promotionResource.SC_CONSTANT_15),
			createGui = GuiComponentType.TEXTBOX,
			editGui = GuiComponentType.TEXTBOX,
			viewGui = GuiComponentType.TEXTBOX,
			textSize = 30,
			maxLength = 100,
			required = true,
			tableColumnConfig = @TableColumnConfig(width = 200)			
		)
	private String cause;//采购类型变更原因

	public Long getPboId() {
		return pboId;
	}

	public void setPboId(Long pboId) {
		this.pboId = pboId;
	}

	public String getPartNumber() {
		return partNumber;
	}

	public void setPartNumber(String partNumber) {
		this.partNumber = partNumber;
	}

	public String getPartName() {
		return partName;
	}

	public void setPartName(String partName) {
		this.partName = partName;
	}

	public Long getPartBranchId() {
		return partBranchId;
	}

	public void setPartBranchId(Long partBranchId) {
		this.partBranchId = partBranchId;
	}
	
	public String getPartCreator() {
		return partCreator;
	}

	public void setPartCreator(String partCreator) {
		this.partCreator = partCreator;
	}

	public String getPartModifier() {
		return partModifier;
	}

	public void setPartModifier(String partModifier) {
		this.partModifier = partModifier;
	}

	public String getSourceBefore() {
		return sourceBefore;
	}

	public void setSourceBefore(String sourceBefore) {
		this.sourceBefore = sourceBefore;
	}

	public String getSourceAfter() {
		return sourceAfter;
	}

	public void setSourceAfter(String sourceAfter) {
		this.sourceAfter = sourceAfter;
	}
	
	public String getCause() {
		return cause;
	}

	public void setCause(String cause) {
		this.cause = cause;
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeLong(EXTERNALIZATION_VERSION_UID);
		super.writeExternal(out);
		out.writeObject(this.pboId);
		out.writeObject(this.partNumber);
		out.writeObject(this.partName);
		out.writeObject(this.partBranchId);
        out.writeObject(this.partCreator);
        out.writeObject(this.partModifier);
        out.writeObject(this.sourceBefore);
        out.writeObject(this.sourceAfter);
        out.writeObject(this.cause);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		long versionId = in.readLong();
		if (versionId != EXTERNALIZATION_VERSION_UID) {
			throw new IOException("Invalid version id " + versionId);
		}
		super.readExternal(in);
		pboId = (Long) in.readObject();
		partNumber = (String) in.readObject();
		partName = (String) in.readObject();
		partBranchId = (Long) in.readObject();
		partCreator = (String) in.readObject();
		partModifier = (String) in.readObject();
		sourceBefore = (String) in.readObject();
		sourceAfter = (String) in.readObject();
		cause = (String) in.readObject();
	}
}
