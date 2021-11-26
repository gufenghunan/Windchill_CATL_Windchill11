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
public class DesignDisabledAppFormBean extends BaseXmlObject {

	static final long serialVersionUID = 1L;

	private static final long EXTERNALIZATION_VERSION_UID = -1545313596601995624L;

	@XmlObjectLinkMarker(childRole = "DesignDisabledXmlObjectUtil")
	private List<DesignDisabledXmlObjectBean> DesignDisabledXmlObjectUtil = new ArrayList<DesignDisabledXmlObjectBean>();

	public List<DesignDisabledXmlObjectBean> getDesignDisabledXmlObjectBean() {
		return DesignDisabledXmlObjectUtil;
	}

	public void setDesignDisabledXmlObjectBean(List<DesignDisabledXmlObjectBean> DesignDisabledXmlObjectUtil) {
		this.DesignDisabledXmlObjectUtil = DesignDisabledXmlObjectUtil;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		long versionId = in.readLong();
		if (versionId != EXTERNALIZATION_VERSION_UID) {
			throw new IOException("Invalid version id:" + versionId);
		}
		super.readExternal(in);
		this.DesignDisabledXmlObjectUtil = (List<DesignDisabledXmlObjectBean>) in.readObject();
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeLong(EXTERNALIZATION_VERSION_UID);
		super.writeExternal(out);
		out.writeObject(this.DesignDisabledXmlObjectUtil);
	}
}
