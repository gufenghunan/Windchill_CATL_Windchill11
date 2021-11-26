package com.catl.change.report.ecr;

public class ECRUsedByBean {
	// 待变更数据编码 待变更数据名称 直接父件编码 直接父件名称 产品编码 产品名称 产品文件夹 "被替换件编码", "被替换件的直接父件编码", "被替换件的直接父件名称"
	private String partNumber, partName, parentNumber, parentName, rootNumber, rootName, rootFolder, byReplaceNumber, byReplaceParentNumber,
			byReplaceParentName;

	@Override
	public String toString() {
		return "partNumber:" + partNumber + ";partName:" + partName + ";parentNumber:" + parentNumber + ";parentName:" + parentName + ";rootNumber:"
				+ rootNumber + ";rootName:" + rootName + ";rootFolder:" + rootFolder;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ECRUsedByBean) {
			ECRUsedByBean b = (ECRUsedByBean) obj;
			String bNumber = b.getPartNumber();
			String bName = b.getPartName();
			String bPNumber = b.getParentNumber();
			String bPName = b.getParentName();
			String bRNumber = b.getRootNumber();
			String bRName = b.getRootName();
			String bRFolder = b.getRootFolder();
			//System.out.println(bNumber + "---" + bName + "---" + bPNumber + "---" + bPName + "---" + bRNumber + "---" + bRName + "---" + bRFolder);
			if (bNumber.equals(this.getPartNumber()) && bName.equals(this.getPartName()) && bPNumber.equals(this.getParentNumber())
					&& bPName.equals(this.getParentName()) && bRNumber.equals(this.getRootNumber()) && bRName.equals(this.getRootName())
					&& bRFolder.equals(this.getRootFolder())) {
				return true;
			}
		}
		return false;
	}

	public ECRUsedByBean() {
		partNumber = "";
		partName = "";
		parentNumber = "";
		parentName = "";
		rootNumber = "";
		rootName = "";
		rootFolder = "";
		byReplaceNumber = "";
		byReplaceParentNumber = "";
		byReplaceParentName = "";
	}

	public ECRUsedByBean(String number, String name) {
		partNumber = "";
		partName = "";
		parentNumber = "";
		parentName = "";
		rootNumber = "";
		rootName = "";
		rootFolder = "";
		byReplaceNumber = "";
		byReplaceParentNumber = "";
		byReplaceParentName = "";
		setPartNumber(number);
		setPartName(name);
	}

	/**
	 * 待变更数据编码
	 * @return
	 */
	public String getPartNumber() {
		return partNumber;
	}

	/**
	 * 待变更数据编码
	 * @return
	 */
	public void setPartNumber(String partNumber) {
		this.partNumber = partNumber;
	}

	/**
	 * 待变更数据名称
	 * @return
	 */
	public String getPartName() {
		return partName;
	}

	/**
	 * 待变更数据名称
	 * @return
	 */
	public void setPartName(String partName) {
		this.partName = partName;
	}

	/**
	 * 直接父件编码
	 * @return
	 */
	public String getParentNumber() {
		return parentNumber;
	}

	/**
	 * 直接父件编码
	 * @return
	 */
	public void setParentNumber(String parentNumber) {
		this.parentNumber = parentNumber;
	}

	/**
	 * 直接父件名称
	 * @return
	 */
	public String getParentName() {
		return parentName;
	}

	/**
	 * 直接父件名称
	 * @return
	 */
	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	/**
	 * 产品编码
	 * @return
	 */
	public String getRootNumber() {
		return rootNumber;
	}

	/**
	 * 产品编码
	 * @return
	 */
	public void setRootNumber(String rootNumber) {
		this.rootNumber = rootNumber;
	}

	/**
	 * 产品名称
	 * @return
	 */
	public String getRootName() {
		return rootName;
	}

	/**
	 * 产品名称
	 * @return
	 */
	public void setRootName(String rootName) {
		this.rootName = rootName;
	}

	/**
	 * 产品文件夹
	 * @return
	 */
	public String getRootFolder() {
		return rootFolder;
	}

	/**
	 * 产品文件夹
	 * @return
	 */
	public void setRootFolder(String rootFolder) {
		this.rootFolder = rootFolder;
	}

	/**
	 * 被替换件编码
	 * @return
	 */
	public String getByReplaceNumber() {
		return byReplaceNumber;
	}

	/**
	 * 被替换件编码
	 * @return
	 */
	public void setByReplaceNumber(String byReplaceNumber) {
		this.byReplaceNumber = byReplaceNumber;
	}

	/**
	 * 被替换件的直接父件编码
	 * @return
	 */
	public String getByReplaceParentNumber() {
		return byReplaceParentNumber;
	}

	/**
	 * 被替换件的直接父件编码
	 * @return
	 */
	public void setByReplaceParentNumber(String byReplaceParentNumber) {
		this.byReplaceParentNumber = byReplaceParentNumber;
	}

	/**
	 * 被替换件的直接父件名称
	 * @return
	 */
	public String getByReplaceParentName() {
		return byReplaceParentName;
	}

	/**
	 * 被替换件的直接父件名称
	 * @return
	 */
	public void setByReplaceParentName(String byReplaceParentName) {
		this.byReplaceParentName = byReplaceParentName;
	}

}
