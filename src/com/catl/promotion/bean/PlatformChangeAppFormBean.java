package com.catl.promotion.bean;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;

import com.ptc.xworks.xmlobject.BaseXmlObject;
import com.ptc.xworks.xmlobject.annotation.XmlObjectLinkMarker;
import com.ptc.xworks.xmlobject.annotation.XmlObjectMarker;

@XmlObjectMarker
public class PlatformChangeAppFormBean extends BaseXmlObject {

	static final long serialVersionUID = 1L;

	private static final long EXTERNALIZATION_VERSION_UID = -1986313565761992222L;

	@XmlObjectLinkMarker(childRole = "PlatformChangeXmlObjectList")
	private List<PlatformChangeXmlObjectBean> PlatformChangeXmlObjectList = new ArrayList<PlatformChangeXmlObjectBean>();

	public List<PlatformChangeXmlObjectBean> getPlatformChangeXmlObjectList() {
		return PlatformChangeXmlObjectList;
	}

	public void setPlatformChangeXmlObjectList(List<PlatformChangeXmlObjectBean> PlatformChangeXmlObjectList) {
		PlatformChangeXmlObjectList = this.PlatformChangeXmlObjectList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		long versionId = in.readLong();
		if (versionId != EXTERNALIZATION_VERSION_UID) {
			throw new IOException("Invalid version id:" + versionId);
		}
		super.readExternal(in);
		this.PlatformChangeXmlObjectList = (List<PlatformChangeXmlObjectBean>) in.readObject();
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeLong(EXTERNALIZATION_VERSION_UID);
		super.writeExternal(out);
		out.writeObject(this.PlatformChangeXmlObjectList);
	}
}
