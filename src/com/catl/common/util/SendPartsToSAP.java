package com.catl.common.util;

import java.io.File;
import java.net.URLDecoder;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentRoleType;
import wt.fc.QueryResult;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.util.WTException;

import com.catl.common.constant.Constant;
import com.catl.common.constant.ContainerName;
import com.catl.integration.ErpResponse;
import com.catl.integration.Message;
import com.catl.integration.PIService;
import com.catl.integration.PartInfo;
import com.catl.integration.ReleaseUtil;
import com.catl.line.constant.ConstantLine;
import com.catl.line.helper.ExpressHelper;
import com.catl.line.test.Test23;
import com.catl.line.util.ExcelUtil;
import com.catl.loadData.StrUtils;

public class SendPartsToSAP implements RemoteAccess{
  private static PIService service = PIService.getInstance();
  public static void main(String[] args) throws ParseException, Exception {
	    RemoteMethodServer rms = RemoteMethodServer.getDefault();
	    rms.setUserName(args[0]);
	    rms.setPassword(args[1]);
		rms.invoke("sendParts", SendPartsToSAP.class.getName(), null, null, null);
  }
  public static void sendParts() throws ParseException, Exception{
	  File file=new File("/ptc/exportpartstoSAP.xls");
	  String[][] result = ExcelUtil.getData(0,null, file,1, false);
	  List<PartInfo> partInfoList = new ArrayList<PartInfo>();
	  List partnumbers=new ArrayList();
	  for (int i = 0; i < result.length; i++) {
		  partnumbers.add(result[i][0]);
		  WTPart part=com.catl.line.util.CommonUtil.getLatestWTpartByNumber(result[i][0]);
		  initPartInfo(partInfoList, part, null, "VR:wt.change2.WTChangeOrder2:226774685");
	  }
	  ErpResponse response=service.sendParts(partInfoList, Constant.COMPANY);
	  List<Message> messages=( List<Message>) response.getMessage();
	  for (int i = 0; i < messages.size(); i++) {
		  Message message=messages.get(i);
		  System.out.println(message.isSuccess()+"   "+message.getNumber());
	  }
  }
	/**
	 * 组装part数据
	 * 
	 * @param list
	 * @param part
	 * @param ecnNumber
	 * @throws ParseException
	 * @throws WTException
	 */
	private static void initPartInfo(List<PartInfo> list, WTPart part, String ecnNumber, String oid) throws ParseException, WTException, Exception {

		PartInfo partInfo = new PartInfo();

		partInfo.setOid(oid);
		partInfo.setVersionBig(part.getVersionIdentifier().getValue());
		partInfo.setVersionSmall(part.getIterationIdentifier().getValue());
		partInfo.setPartName(part.getName());
		partInfo.setPartNumber(part.getNumber());
		partInfo.setCreateDate(part.getCreateTimestamp());
		if (part.getCreatorName() != null && part.getCreatorName().equalsIgnoreCase("dms")) {
			partInfo.setCreator(Constant.dmsConstrastEmpno);
		} else {
			partInfo.setCreator(part.getCreatorName());
		}

		String unit = part.getDefaultUnit().toString().toUpperCase();
		if (!StrUtils.isEmpty(unit) && unit.equals("EA")) {
			unit = "PCS";
		}
		partInfo.setSource(ReleaseUtil.changeSource(part.getSource().toString()));

		if (part.getContainerName().equals(ContainerName.BATTERY_LIBRARY_NAME)) {
			if (unit.equals("PCS")) {
				unit = "EA";
			}
			partInfo.setMaterialGroup(null);
		} else {
			partInfo.setMaterialGroup(part.getNumber().substring(0, part.getNumber().lastIndexOf("-")));
		}
		partInfo.setDefaultUnit(unit);
		partInfo.setIteration(part.getVersionIdentifier().getValue());

		Object englishNameObj = GenericUtil.getObjectAttributeValue(part, "englishName");
		String englishName = englishNameObj == null ? "" : englishNameObj.toString();
		if (englishName.equals(Constant.DEFAULT)) {
			englishName = "";
		}
		Object specificationObj = GenericUtil.getObjectAttributeValue(part, "specification");
		String specification = specificationObj == null ? "" : specificationObj.toString();
		Object oldPartNumberObj = GenericUtil.getObjectAttributeValue(part, "oldPartNumber");// 旧物料号
		String oldPartNumber = oldPartNumberObj == null ? "" : oldPartNumberObj.toString();
		if (oldPartNumber.equals(Constant.DEFAULT)) {
			oldPartNumber = "";
		}

		Object standardVoltageObj = GenericUtil.getObjectAttributeValue(part, "Nominal_Voltage");// 标称电压(V)
		String standardVoltage = standardVoltageObj == null ? "" : standardVoltageObj.toString();
		Object productEnergyObj = GenericUtil.getObjectAttributeValue(part, "Product_Energy");// 产品能量(kWh)
		String productEnergy = productEnergyObj == null ? "" : productEnergyObj.toString();
		/*
		 * Object modelObj =
		 * GenericUtil.getObjectAttributeValue(part,"Product_Model");//Model号
		 * String model = modelObj==null?"":modelObj.toString();
		 */
		Object cellVolumeObj = GenericUtil.getObjectAttributeValue(part, "Cell_Capacity");// 电芯容量(Ah)
		String cellVolume = cellVolumeObj == null ? "" : cellVolumeObj.toString();
		Object cellModeObj = GenericUtil.getObjectAttributeValue(part, "Cell_Mode");// 电芯类型
		String cellMode = cellModeObj == null ? "" : cellModeObj.toString();
		Object cellConnectionModeObj = GenericUtil.getObjectAttributeValue(part, "Cell_Connection_Mode");// 电芯并串联方式
		String cellConnectionMode = cellConnectionModeObj == null ? "" : cellConnectionModeObj.toString();
		Object moduleQuantityObj = GenericUtil.getObjectAttributeValue(part, "Module_Quantity");// 模组数量(PCS)
		String moduleQuantity = moduleQuantityObj == null ? "" : moduleQuantityObj.toString();
		Object hardwareVersionObj = GenericUtil.getObjectAttributeValue(part, "Hardware_Version");// HW版本
		String hardwareVersion = hardwareVersionObj == null ? "" : hardwareVersionObj.toString();
		Object softwareVersionObj = GenericUtil.getObjectAttributeValue(part, "Software_Version");// SW版本
		String softwareVersion = softwareVersionObj == null ? "" : softwareVersionObj.toString();
		Object parameterVersionObj = GenericUtil.getObjectAttributeValue(part, "Parameter_Version");// PAR版本
		String parameterVersion = parameterVersionObj == null ? "" : parameterVersionObj.toString();
		Object faeStatusObj = GenericUtil.getObjectAttributeValue(part, "CATL_FAEStatus");// PAR版本
		String mpn=(String) GenericUtil.getObjectAttributeValue(part, ConstantLine.var_parentPN);
		String l=ExpressHelper.getLineL(part)==null ? "" : String.valueOf(ExpressHelper.getLineL(part).doubleValue());
		
		String faeStatus = faeStatusObj == null ? "" : faeStatusObj.toString();
		partInfo.setStandardVoltage(standardVoltage);
		partInfo.setProductEnergy(productEnergy);
		partInfo.setCellVolume(cellVolume);
		partInfo.setCellMode(cellMode);
		partInfo.setCellConnectionMode(cellConnectionMode);
		partInfo.setModuleQuantity(moduleQuantity);
		partInfo.setHardwareVersion(hardwareVersion);
		partInfo.setSoftwareVersion(softwareVersion);
		partInfo.setParameterVersion(parameterVersion);
		partInfo.setFaeStatus(faeStatus);
		if(mpn!=null&&!mpn.equals("是")){//衍生PN发长度
			partInfo.setL(l);
			partInfo.setParentPN(mpn);
		}
		if (partInfo.getDrawing() == null) {// 没有PCBA、AUTO、GERBER文档
			QueryResult qr = ContentHelper.service.getContentsByRole(part, ContentRoleType.SECONDARY);
			while (qr.hasMoreElements()) {
				ApplicationData fileContent = (ApplicationData) qr.nextElement();
				String strFileName = URLDecoder.decode(fileContent.getFileName(), "utf-8");
				if (strFileName.toUpperCase().equals(part.getNumber() + ".PDF")) {
					partInfo.setDrawing(part.getNumber());
					partInfo.setDrawingVersion("A");
					break;
				}
			}
		}
		partInfo.setEnglishName(englishName);
		partInfo.setSpecification(specification);
		partInfo.setEcnNumber(ecnNumber);
		partInfo.setOldPartNumber(oldPartNumber);

		list.add(partInfo);

	}

}
