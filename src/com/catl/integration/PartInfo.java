package com.catl.integration;

import java.util.Date;

public class PartInfo {
   public String getPartName() {
		return partName;
	}
	public void setPartName(String partName) {
		this.partName = partName;
	}
	public String getPartNumber() {
		return partNumber;
	}
	public void setPartNumber(String partNumber) {
		this.partNumber = partNumber;
	}
	public String getDefaultUnit() {
		return defaultUnit;
	}
	public void setDefaultUnit(String defaultUnit) {
		this.defaultUnit = defaultUnit;
	}
	public String getSpecification() {
		return specification;
	}
	public void setSpecification(String specification) {
		this.specification = specification;
	}
	
	public String getDrawingVersion() {
        return drawingVersion;
    }
    public void setDrawingVersion(String drawingVersion) {
        this.drawingVersion = drawingVersion;
    }
    public String getIteration() {
		return iteration;
	}
	public void setIteration(String iteration) {
		this.iteration = iteration;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getMaterialGroup() {
		return materialGroup;
	}
	public void setMaterialGroup(String materialGroup) {
		this.materialGroup = materialGroup;
	}
	public String getEnglishName() {
		return englishName;
	}
	public void setEnglishName(String englishName) {
		this.englishName = englishName;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public String getFullVoltage() {
		return fullVoltage;
	}
	public void setFullVoltage(String fullVoltage) {
		this.fullVoltage = fullVoltage;
	}
	public String getStandardVoltage() {
		return standardVoltage;
	}
	public void setStandardVoltage(String standardVoltage) {
		this.standardVoltage = standardVoltage;
	}
	public String getDrawing() {
		return drawing;
	}
	public void setDrawing(String drawing) {
		this.drawing = drawing;
	}
	
   public String getEcnNumber() {
		return ecnNumber;
	}
	public void setEcnNumber(String ecnNumber) {
		this.ecnNumber = ecnNumber;
	}
	public String getProductEnergy() {
		return productEnergy;
	}
	public void setProductEnergy(String productEnergy) {
		this.productEnergy = productEnergy;
	}
	public String getCellVolume() {
		return cellVolume;
	}
	public void setCellVolume(String cellVolume) {
		this.cellVolume = cellVolume;
	}
    public String getVersionBig() {
        return versionBig;
    }
    public void setVersionBig(String versionBig) {
        this.versionBig = versionBig;
    }
    public String getVersionSmall() {
        return versionSmall;
    }
    public void setVersionSmall(String versionSmall) {
        this.versionSmall = versionSmall;
    }
    public String getOid() {
        return oid;
    }
    public void setOid(String oid) {
        this.oid = oid;
    }
    public String getOldPartNumber() {
		return oldPartNumber;
	}
	public void setOldPartNumber(String oldPartNumber) {
		this.oldPartNumber = oldPartNumber;
	}
	public String getCellMode() {
		return cellMode;
	}
	public void setCellMode(String cellMode) {
		this.cellMode = cellMode;
	}
	public String getCellConnectionMode() {
		return cellConnectionMode;
	}
	public void setCellConnectionMode(String cellConnectionMode) {
		this.cellConnectionMode = cellConnectionMode;
	}
	public String getModuleQuantity() {
		return moduleQuantity;
	}
	public void setModuleQuantity(String moduleQuantity) {
		this.moduleQuantity = moduleQuantity;
	}
	public String getSoftwareVersion() {
		return softwareVersion;
	}
	public void setSoftwareVersion(String softwareVersion) {
		this.softwareVersion = softwareVersion;
	}
	public String getHardwareVersion() {
		return hardwareVersion;
	}
	public void setHardwareVersion(String hardwareVersion) {
		this.hardwareVersion = hardwareVersion;
	}
	public String getParameterVersion() {
		return parameterVersion;
	}
	public void setParameterVersion(String parameterVersion) {
		this.parameterVersion = parameterVersion;
	}
	public String getFaeStatus() {
		return faeStatus;
	}
	public void setFaeStatus(String faeStatus) {
		this.faeStatus = faeStatus;
	}
	public String getParentPN() {
	    return parentPN;
	}
	public void setParentPN(String parentPN) {
		this.parentPN = parentPN;
	}
	public String getL() {
		return L;
	}
	public void setL(String l) {
		L = l;
	}





   private String partName;
   private String partNumber;
   private String defaultUnit;
   private String specification;
   private String drawingVersion;
   private String iteration;
   private String creator;
   private Date createDate;
   private String source;// mapping to SAP purchase type
   private String materialGroup;// part number start 6 number
   private String englishName;
   private String model;
   private String productEnergy;
   private String fullVoltage;
   private String standardVoltage;
   private String drawing;
   private String ecnNumber;
   private String cellVolume;
   private String versionBig;
   private String versionSmall;
   private String oid;
   private String oldPartNumber;
   private String cellMode;
   private String cellConnectionMode;
   private String moduleQuantity;
   private String softwareVersion;
   private String hardwareVersion;
   private String parameterVersion;
   private String faeStatus;
   private String parentPN;
   private String L;

}
