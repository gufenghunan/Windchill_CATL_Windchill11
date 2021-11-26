package com.catl.promotion.bean;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.Map;

import com.catl.require.resource.requireRB;
import com.ptc.xworks.xmlobject.FlexXmlObject;
import com.ptc.xworks.xmlobject.XmlObject;
import com.ptc.xworks.xmlobject.annotation.EnumerationProvider;
import com.ptc.xworks.xmlobject.annotation.EnumerationProviderType;
import com.ptc.xworks.xmlobject.annotation.ExtraOption;
import com.ptc.xworks.xmlobject.annotation.GuiComponentType;
import com.ptc.xworks.xmlobject.annotation.JavascriptAction;
import com.ptc.xworks.xmlobject.annotation.ResourceBundleKey;
import com.ptc.xworks.xmlobject.annotation.TableColumnConfig;
import com.ptc.xworks.xmlobject.annotation.WebFormAttribute;
import com.ptc.xworks.xmlobject.annotation.XmlObjectMarker;

@XmlObjectMarker()
public class PlatformChangeXmlObjectBean extends FlexXmlObject implements XmlObject{
	
	static final long serialVersionUID = 1L;
	
	private static final long EXTERNALIZATION_VERSION_UID = -4411422323343954126L;
	
	private long pboId;    
	@WebFormAttribute(
		label = @ResourceBundleKey(resourceBundle = "com.catl.require.resource.requireRB", key = requireRB.partNumber),
		tooltip = @ResourceBundleKey(resourceBundle = "com.catl.require.resource.requireRB", key =requireRB.partNumber),
		createGui = GuiComponentType.TEXT,
		editGui = GuiComponentType.TEXT,
		viewGui = GuiComponentType.TEXT,
		tableColumnConfig = @TableColumnConfig(width = 150)
	)
	private String partNumber;   
	
	@WebFormAttribute(
		label = @ResourceBundleKey(resourceBundle = "com.catl.require.resource.requireRB", key = requireRB.partName),
		tooltip = @ResourceBundleKey(resourceBundle = "com.catl.require.resource.requireRB", key = requireRB.partName),
		createGui = GuiComponentType.TEXT,
		editGui = GuiComponentType.TEXT,
		viewGui = GuiComponentType.TEXT,
		tableColumnConfig = @TableColumnConfig(width = 150)
		//required = true
	)
    private String partName;   
	
	private long partBranchId;   
	
	@WebFormAttribute(
		label = @ResourceBundleKey(resourceBundle = "com.catl.require.resource.requireRB", key = requireRB.partCreator),
		tooltip = @ResourceBundleKey(resourceBundle = "com.catl.require.resource.requireRB", key = requireRB.partCreator),
		createGui = GuiComponentType.TEXT,
		editGui = GuiComponentType.TEXT,
		viewGui = GuiComponentType.TEXT
		//required = true
	)
	private String partCreator;    //PCB元件封装3
	
	@WebFormAttribute(
		label = @ResourceBundleKey(resourceBundle = "com.catl.require.resource.requireRB", key = requireRB.partModifier),
		tooltip = @ResourceBundleKey(resourceBundle = "com.catl.require.resource.requireRB", key = requireRB.partModifier),
		createGui = GuiComponentType.TEXT,
		editGui = GuiComponentType.TEXT,
		viewGui = GuiComponentType.TEXT
		//required = true
	)
	private String partModifier;   
	
	@WebFormAttribute(
		label = @ResourceBundleKey(resourceBundle = "com.catl.require.resource.requireRB", key = requireRB.platformBefore),
		tooltip = @ResourceBundleKey(resourceBundle = "com.catl.require.resource.requireRB", key = requireRB.platformBefore),
		createGui = GuiComponentType.TEXT,
		editGui = GuiComponentType.TEXT,
		viewGui = GuiComponentType.TEXT
		//required = true
	)
	private String platformBefore;   
	
	@WebFormAttribute(
		label = @ResourceBundleKey(resourceBundle = "com.catl.require.resource.requireRB", key = requireRB.platformAfter),
		tooltip = @ResourceBundleKey(resourceBundle = "com.catl.require.resource.requireRB", key = requireRB.platformAfter),
		createGui = GuiComponentType.COMBOBOX,
		editGui = GuiComponentType.COMBOBOX,
		viewGui = GuiComponentType.TEXT,
		enumerationProvider = @EnumerationProvider(provierType = EnumerationProviderType.JAVA_CLASS, value = "com.catl.require.util.PlatformXmlObjectUtils|getProcessPlatformEnum"),
	    extraOptions = { @ExtraOption(name = ExtraOption.COMBOBOX_SHOW_BLANK_OPTION, value = "true") },
	    required = true,
	    javascriptActions={@JavascriptAction(event="onchange",javascript="validatePlatform(this);")},
	    tableColumnConfig = @TableColumnConfig(width = 150)		
	)
	private String platformAfter;  

	
	@WebFormAttribute(
		label = @ResourceBundleKey(resourceBundle = "com.catl.require.resource.requireRB", key = requireRB.cause),
		tooltip = @ResourceBundleKey(resourceBundle = "com.catl.require.resource.requireRB", key = requireRB.cause),
		createGui = GuiComponentType.TEXTBOX,
		editGui = GuiComponentType.TEXTBOX,
		viewGui = GuiComponentType.TEXT,
		tableColumnConfig = @TableColumnConfig(width = 250)
		//required = true
	)
	private String cause;    
	
	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		long versionId = in.readLong();
		if (versionId != EXTERNALIZATION_VERSION_UID) {
			throw new IOException("Invalid version id:" + versionId);
		}
		super.readExternal(in);
		pboId = (long) in.readObject();
		partNumber = (String) in.readObject();
		partName = (String) in.readObject();
		partBranchId = (long) in.readObject();
		partCreator = (String) in.readObject();
		partModifier = (String) in.readObject();
		platformBefore = (String) in.readObject();
		platformAfter = (String)in.readObject();
		cause = (String)in.readObject();
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
		out.writeObject(this.platformBefore);
		out.writeObject(this.platformAfter);
		out.writeObject(this.cause);
	}
	
	/**
	 * 用于插入cadence数据时的列值对应
	 * @param bean
	 * @return map
	 */
	public Map getNameAndValue(){
		Map map=new HashMap<>();
		map.put("pboId",this.pboId);
		map.put("partNumber", this.partNumber);
		map.put("partName", this.partName);
		map.put("partBranchId", this.partBranchId);
		map.put("partCreator", this.partCreator);
		map.put("partModifier", this.partModifier);
		map.put("platformBefore",this.platformBefore);
		map.put("platformAfter", this.platformAfter);
		map.put("cause", this.cause);
		return map;
	}

	public long getPboId() {
		return pboId;
	}

	public void setPboId(long pboId) {
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

	public long getPartBranchId() {
		return partBranchId;
	}

	public void setPartBranchId(long partBranchId) {
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

	public String getPlatformBefore() {
		return platformBefore;
	}

	public void setPlatformBefore(String platformBefore) {
		this.platformBefore = platformBefore;
	}

	public String getPlatformAfter() {
		return platformAfter;
	}

	public void setPlatformAfter(String platformAfter) {
		this.platformAfter = platformAfter;
	}

	public String getCause() {
		return cause;
	}

	public void setCause(String cause) {
		this.cause = cause;
	}

}
