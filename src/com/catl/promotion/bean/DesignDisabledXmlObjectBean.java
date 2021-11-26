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

@XmlObjectMarker(tableName="A_XML_DESIGNDISABLEDOBJECTS", columns={
		@XmlObjectColumnMarker(attribute="pboId",columnName="PBOID",javaType=Long.class, sqlType=Types.INTEGER),
		@XmlObjectColumnMarker(attribute="partNumber",columnName="PARTNUMBER",javaType=String.class, sqlType=Types.VARCHAR),
		@XmlObjectColumnMarker(attribute="partName",columnName="PARTNAME",javaType=String.class, sqlType=Types.VARCHAR),
		@XmlObjectColumnMarker(attribute="partBranchId",columnName="PARTBRANCHID",javaType=Long.class, sqlType=Types.INTEGER),
		@XmlObjectColumnMarker(attribute="requestor",columnName="REQUESTOR",javaType=String.class, sqlType=Types.VARCHAR),
		@XmlObjectColumnMarker(attribute="reason",columnName="REASON",javaType=String.class, sqlType=Types.VARCHAR),
		@XmlObjectColumnMarker(attribute="changeNo",columnName="CHANGENO",javaType=String.class, sqlType=Types.VARCHAR),
		@XmlObjectColumnMarker(attribute="newPN",columnName="NEWPN",javaType=String.class, sqlType=Types.VARCHAR)
})
public class DesignDisabledXmlObjectBean extends BaseXmlObject{

	static final long serialVersionUID = 1L;

	private static final long EXTERNALIZATION_VERSION_UID = -7045389240324490768L;
	
	@WebFormAttribute(
			label = @ResourceBundleKey(resourceBundle = "com.catl.promotion.resource.promotionResource", key = promotionResource.PRIVATE_CONSTANT_20),
			tooltip = @ResourceBundleKey(resourceBundle = "com.catl.promotion.resource.promotionResource", key = promotionResource.PRIVATE_CONSTANT_20),
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
			label = @ResourceBundleKey(resourceBundle = "com.catl.promotion.resource.promotionResource", key = promotionResource.PRIVATE_CONSTANT_21),
			createGui = GuiComponentType.TYPE_ICON, 
			editGui = GuiComponentType.TYPE_ICON, 
			viewGui = GuiComponentType.TYPE_ICON,
			textSize = 15
	)
	@XStreamOmitField
	private String partTypeIcon;//图标
	
	@WebFormAttribute(
			label = @ResourceBundleKey(resourceBundle = "com.catl.promotion.resource.promotionResource", key = promotionResource.PRIVATE_CONSTANT_22),
			tooltip = @ResourceBundleKey(resourceBundle = "com.catl.promotion.resource.promotionResource", key = promotionResource.PRIVATE_CONSTANT_22),
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
			label = @ResourceBundleKey(resourceBundle = "com.catl.promotion.resource.promotionResource", key = promotionResource.PRIVATE_CONSTANT_23),
			tooltip = @ResourceBundleKey(resourceBundle = "com.catl.promotion.resource.promotionResource", key = promotionResource.PRIVATE_CONSTANT_23),
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
			label = @ResourceBundleKey(resourceBundle = "com.catl.promotion.resource.promotionResource", key = promotionResource.PRIVATE_CONSTANT_24),
			tooltip = @ResourceBundleKey(resourceBundle = "com.catl.promotion.resource.promotionResource", key = promotionResource.PRIVATE_CONSTANT_24),
			createGui = GuiComponentType.TEXT,
			editGui = GuiComponentType.TEXT,
			viewGui = GuiComponentType.TEXT,
			textSize = 30,
			maxLength = 100		
		)
	@XStreamOmitField
	private Long partBranchId;//部件的Branch ID
	
	@WebFormAttribute(
			label = @ResourceBundleKey(resourceBundle = "com.catl.promotion.resource.promotionResource", key = promotionResource.PRIVATE_CONSTANT_25),
			tooltip = @ResourceBundleKey(resourceBundle = "com.catl.promotion.resource.promotionResource", key = promotionResource.PRIVATE_CONSTANT_25),
			createGui = GuiComponentType.TEXTBOX,
			editGui = GuiComponentType.TEXTBOX,
			viewGui = GuiComponentType.TEXTBOX,
			textSize = 30,
			maxLength = 100,
			required = true,
			tableColumnConfig = @TableColumnConfig(width = 200)			
		)
	@XStreamOmitField
	private String requestor;//申请人
	
	@WebFormAttribute(
			label = @ResourceBundleKey(resourceBundle = "com.catl.promotion.resource.promotionResource", key = promotionResource.PRIVATE_CONSTANT_26),
			tooltip = @ResourceBundleKey(resourceBundle = "com.catl.promotion.resource.promotionResource", key = promotionResource.PRIVATE_CONSTANT_26),
			createGui = GuiComponentType.COMBOBOX,
			editGui = GuiComponentType.COMBOBOX,
			viewGui = GuiComponentType.COMBOBOX,
			enumerationProvider = @EnumerationProvider(provierType = EnumerationProviderType.GLOBAL_ENUMERATION, value = "ENUM_DISABLED_REASON"),
		    extraOptions = { @ExtraOption(name = ExtraOption.COMBOBOX_SHOW_BLANK_OPTION, value = "true") },
		    required = true,
		    tableColumnConfig = @TableColumnConfig(width = 150)
		)
	@XStreamOmitField
	private String reason;//设计禁用原因

	@WebFormAttribute(
			label = @ResourceBundleKey(resourceBundle = "com.catl.promotion.resource.promotionResource", key = promotionResource.PRIVATE_CONSTANT_27),
			tooltip = @ResourceBundleKey(resourceBundle = "com.catl.promotion.resource.promotionResource", key = promotionResource.PRIVATE_CONSTANT_27),
			createGui = GuiComponentType.TEXTBOX,
			editGui = GuiComponentType.TEXTBOX,
			viewGui = GuiComponentType.TEXTBOX,
			textSize = 30,
			maxLength = 100,
			tableColumnConfig = @TableColumnConfig(width = 200)
		)
	@XStreamOmitField
	private String changeNo;//变更单号
	
	@WebFormAttribute(
			label = @ResourceBundleKey(resourceBundle = "com.catl.promotion.resource.promotionResource", key = promotionResource.PRIVATE_CONSTANT_28),
			tooltip = @ResourceBundleKey(resourceBundle = "com.catl.promotion.resource.promotionResource", key = promotionResource.PRIVATE_CONSTANT_28),
			createGui = GuiComponentType.TEXTBOX,
			editGui = GuiComponentType.TEXTBOX,
			viewGui = GuiComponentType.TEXTBOX,
			textSize = 30,
			maxLength = 100,
			tableColumnConfig = @TableColumnConfig(width = 200)
		)
	@XStreamOmitField
	private String newPN;//新料号
	
	@WebFormAttribute(
			label = @ResourceBundleKey(resourceBundle = "com.catl.promotion.resource.promotionResource", key = promotionResource.PRIVATE_CONSTANT_29),
			tooltip = @ResourceBundleKey(resourceBundle = "com.catl.promotion.resource.promotionResource", key = promotionResource.PRIVATE_CONSTANT_29),
			createGui = GuiComponentType.TEXTAREA,
			editGui = GuiComponentType.TEXTAREA,
			viewGui = GuiComponentType.TEXTAREA,
			textRows = 1,
            textSize = 35,
            tableColumnConfig = @TableColumnConfig(width = 200)
		)
	private String comments;//备注

	public Long getpboId() {
		return pboId;
	}

	public void setpboId(Long pboId) {
		this.pboId = pboId;
	}
	
	public String getpartTypeIcon() {
		return "wt.part.WTPart";
	}

	public void setpartTypeIcon(String partTypeIcon) {
		this.partTypeIcon = partTypeIcon;
	}

	public String getpartNumber() {
		return partNumber;
	}

	public void setpartNumber(String partNumber) {
		this.partNumber = partNumber;
	}

	public String getpartName() {
		return partName;
	}

	public void setpartName(String partName) {
		this.partName = partName;
	}

	public Long getpartBranchId() {
		return partBranchId;
	}

	public void setpartBranchId(Long partBranchId) {
		this.partBranchId = partBranchId;
	}

	public String getrequestor() {
		return requestor;
	}

	public void setrequestor(String requestor) {
		this.requestor = requestor;
	}
	
	public String getreason() {
		return reason;
	}

	public void setreason(String reason) {
		this.reason = reason;
	}
	
	public String getchangeNo() {
		return changeNo;
	}

	public void setchangeNo(String changeNo) {
		this.changeNo = changeNo;
	}
	
	public String getnewPN() {
		return newPN;
	}

	public void setnewPN(String newPN) {
		this.newPN = newPN;
	}
	
	public String getcomments() {
		return comments;
	}

	public void setcomments(String comments) {
		this.comments = comments;
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeLong(EXTERNALIZATION_VERSION_UID);
		super.writeExternal(out);
		out.writeObject(this.pboId);
		out.writeObject(this.partTypeIcon);
		out.writeObject(this.partNumber);
		out.writeObject(this.partName);
		out.writeObject(this.partBranchId);
        out.writeObject(this.requestor);
        out.writeObject(this.reason);
        out.writeObject(this.changeNo);
        out.writeObject(this.newPN);
        out.writeObject(this.comments);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		long versionId = in.readLong();
		if (versionId != EXTERNALIZATION_VERSION_UID) {
			throw new IOException("Invalid version id " + versionId);
		}
		super.readExternal(in);
		pboId = (Long) in.readObject();
		partTypeIcon = (String) in.readObject();
		partNumber = (String) in.readObject();
		partName = (String) in.readObject();
		partBranchId = (Long) in.readObject();
		requestor = (String) in.readObject();
		reason = (String) in.readObject();
		changeNo = (String) in.readObject();
		newPN = (String) in.readObject();
		comments = (String) in.readObject();
	}
}
