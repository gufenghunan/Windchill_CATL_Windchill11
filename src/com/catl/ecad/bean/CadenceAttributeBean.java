package com.catl.ecad.bean;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.Map;

import com.catl.ecad.resource.ECADResource;
import com.catl.ecad.utils.ECADConst;
import com.ptc.xworks.xmlobject.FlexXmlObject;
import com.ptc.xworks.xmlobject.XmlObject;
import com.ptc.xworks.xmlobject.annotation.GuiComponentType;
import com.ptc.xworks.xmlobject.annotation.ResourceBundleKey;
import com.ptc.xworks.xmlobject.annotation.WebFormAttribute;
import com.ptc.xworks.xmlobject.annotation.XmlObjectMarker;

@XmlObjectMarker()
public class CadenceAttributeBean extends FlexXmlObject implements XmlObject{
	
	static final long serialVersionUID = 1L;
	
	private static final long EXTERNALIZATION_VERSION_UID = -4411422323343954125L;
	
	@WebFormAttribute(
		label = @ResourceBundleKey(resourceBundle = ECADConst.RESOURCE, key = ECADResource.schematic_part_label),
		tooltip = @ResourceBundleKey(resourceBundle = ECADConst.RESOURCE, key = ECADResource.schematic_part_tooltip),
		createGui = GuiComponentType.TEXTBOX,
		editGui = GuiComponentType.TEXTBOX,
		viewGui = GuiComponentType.TEXT,
		required = true
	)
	private String schematic_part;    //原理图符号名
	
	@WebFormAttribute(
		label = @ResourceBundleKey(resourceBundle = ECADConst.RESOURCE, key = ECADResource.old_footprint_label),
		tooltip = @ResourceBundleKey(resourceBundle = ECADConst.RESOURCE, key = ECADResource.old_footprint_tooltip),
		createGui = GuiComponentType.TEXTBOX,
		editGui = GuiComponentType.TEXTBOX,
		viewGui = GuiComponentType.TEXT,
		required = true
	)
	private String old_Footprint;    //PCB元件封装1
	
	@WebFormAttribute(
		label = @ResourceBundleKey(resourceBundle = ECADConst.RESOURCE, key = ECADResource.alt_symbols_label),
		tooltip = @ResourceBundleKey(resourceBundle = ECADConst.RESOURCE, key = ECADResource.alt_symbols_tooltip),
		createGui = GuiComponentType.TEXTBOX,
		editGui = GuiComponentType.TEXTBOX,
		viewGui = GuiComponentType.TEXT
		//required = true
	)
	private String alt_Symbols;    //PCB元件封装2
	
	@WebFormAttribute(
		label = @ResourceBundleKey(resourceBundle = ECADConst.RESOURCE, key = ECADResource.edadoc_Footprint_label),
		tooltip = @ResourceBundleKey(resourceBundle = ECADConst.RESOURCE, key = ECADResource.edadoc_Footprint_tooltip),
		createGui = GuiComponentType.TEXTBOX,
		editGui = GuiComponentType.TEXTBOX,
		viewGui = GuiComponentType.TEXT
		//required = true
	)
	private String edadoc_Footprint;    //PCB元件封装3
	
	@WebFormAttribute(
		label = @ResourceBundleKey(resourceBundle = ECADConst.RESOURCE, key = ECADResource.ipc7351_Footprint_A_Maximum_label),
		tooltip = @ResourceBundleKey(resourceBundle = ECADConst.RESOURCE, key = ECADResource.ipc7351_Footprint_A_Maximum_tooltip),
		createGui = GuiComponentType.TEXTBOX,
		editGui = GuiComponentType.TEXTBOX,
		viewGui = GuiComponentType.TEXT
		//required = true
	)
	private String ipc7351_Footprint_A_Maximum;    //PCB元件封装4
	
	@WebFormAttribute(
		label = @ResourceBundleKey(resourceBundle = ECADConst.RESOURCE, key = ECADResource.ipc7351_Footprint_B_Normal_label),
		tooltip = @ResourceBundleKey(resourceBundle = ECADConst.RESOURCE, key = ECADResource.ipc7351_Footprint_B_Normal_tooltip),
		createGui = GuiComponentType.TEXTBOX,
		editGui = GuiComponentType.TEXTBOX,
		viewGui = GuiComponentType.TEXT
		//required = true
	)
	private String ipc7351_Footprint_B_Normal;    //PCB元件封装5
	
	@WebFormAttribute(
		label = @ResourceBundleKey(resourceBundle = ECADConst.RESOURCE, key = ECADResource.ipc7351_Footprint_C_Minimum_label),
		tooltip = @ResourceBundleKey(resourceBundle = ECADConst.RESOURCE, key = ECADResource.ipc7351_Footprint_C_Minimum_tooltip),
		createGui = GuiComponentType.TEXTBOX,
		editGui = GuiComponentType.TEXTBOX,
		viewGui = GuiComponentType.TEXT
		//required = true
	)
	private String ipc7351_Footprint_C_Minimum;    //PCB元件封装6
	
	@WebFormAttribute(
		label = @ResourceBundleKey(resourceBundle = ECADConst.RESOURCE, key = ECADResource.vibration_Footprint_label),
		tooltip = @ResourceBundleKey(resourceBundle = ECADConst.RESOURCE, key = ECADResource.vibration_Footprint_tooltip),
		createGui = GuiComponentType.TEXTBOX,
		editGui = GuiComponentType.TEXTBOX,
		viewGui = GuiComponentType.TEXT
		//required = true
	)
	private String vibration_Footprint;    //PCB元件封装7
	
	private String partOid;
	
	public String getSchematic_part() {
		return schematic_part;
	}

	public void setSchematic_part(String schematic_part) {
		this.schematic_part = schematic_part;
	}

	public String getOld_Footprint() {
		return old_Footprint;
	}

	public void setOld_Footprint(String old_Footprint) {
		this.old_Footprint = old_Footprint;
	}

	public String getAlt_Symbols() {
		return alt_Symbols;
	}

	public void setAlt_Symbols(String alt_Symbols) {
		this.alt_Symbols = alt_Symbols;
	}

	public String getEdadoc_Footprint() {
		return edadoc_Footprint;
	}

	public void setEdadoc_Footprint(String edadoc_Footprint) {
		this.edadoc_Footprint = edadoc_Footprint;
	}

	public String getIpc7351_Footprint_A_Maximum() {
		return ipc7351_Footprint_A_Maximum;
	}

	public void setIpc7351_Footprint_A_Maximum(String ipc7351_Footprint_A_Maximum) {
		this.ipc7351_Footprint_A_Maximum = ipc7351_Footprint_A_Maximum;
	}

	public String getIpc7351_Footprint_B_Normal() {
		return ipc7351_Footprint_B_Normal;
	}

	public void setIpc7351_Footprint_B_Normal(String ipc7351_Footprint_B_Normal) {
		this.ipc7351_Footprint_B_Normal = ipc7351_Footprint_B_Normal;
	}

	public String getIpc7351_Footprint_C_Minimum() {
		return ipc7351_Footprint_C_Minimum;
	}

	public void setIpc7351_Footprint_C_Minimum(String ipc7351_Footprint_C_Minimum) {
		this.ipc7351_Footprint_C_Minimum = ipc7351_Footprint_C_Minimum;
	}

	public String getVibration_Footprint() {
		return vibration_Footprint;
	}

	public void setVibration_Footprint(String vibration_Footprint) {
		this.vibration_Footprint = vibration_Footprint;
	}

	public String getPartOid() {
		return partOid;
	}

	public void setPartOid(String partOid) {
		this.partOid = partOid;
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		long versionId = in.readLong();
		if (versionId != EXTERNALIZATION_VERSION_UID) {
			throw new IOException("Invalid version id:" + versionId);
		}
		super.readExternal(in);
		schematic_part = (String) in.readObject();
		old_Footprint = (String) in.readObject();
		alt_Symbols = (String) in.readObject();
		edadoc_Footprint = (String) in.readObject();
		ipc7351_Footprint_A_Maximum = (String) in.readObject();
		ipc7351_Footprint_B_Normal = (String) in.readObject();
		ipc7351_Footprint_C_Minimum = (String) in.readObject();
		vibration_Footprint = (String)in.readObject();
	}
	
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeLong(EXTERNALIZATION_VERSION_UID);
		super.writeExternal(out);
		out.writeObject(this.schematic_part);
		out.writeObject(this.old_Footprint);
		out.writeObject(this.alt_Symbols);
		out.writeObject(this.edadoc_Footprint);
		out.writeObject(this.ipc7351_Footprint_A_Maximum);
		out.writeObject(this.ipc7351_Footprint_B_Normal);
		out.writeObject(this.ipc7351_Footprint_C_Minimum);
		out.writeObject(this.vibration_Footprint);
	}
	
	/**
	 * 用于插入cadence数据时的列值对应
	 * @param bean
	 * @return map
	 */
	public Map<String,String> getNameAndValue(){
		Map<String,String> map=new HashMap<>();
		map.put("schematic_part",this.schematic_part);
		map.put("old_Footprint", this.old_Footprint);
		map.put("alt_Symbols", this.alt_Symbols);
		map.put("edadoc_Footprint", this.edadoc_Footprint);
		map.put("ipc7351_Footprint_A_Maximum", this.ipc7351_Footprint_A_Maximum);
		map.put("ipc7351_Footprint_B_Normal", this.ipc7351_Footprint_B_Normal);
		map.put("ipc7351_Footprint_C_Minimum",this.ipc7351_Footprint_C_Minimum);
		map.put("vibration_Footprint", this.vibration_Footprint);
		return map;
	}
}
