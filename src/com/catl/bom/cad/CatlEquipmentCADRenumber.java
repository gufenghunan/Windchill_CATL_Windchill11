package com.catl.bom.cad;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.catl.common.constant.PartState;
import com.catl.ecad.utils.CommonUtil;
import com.catl.ecad.utils.ECADutil;
import com.catl.ecad.utils.EPMUtil;
import com.catl.line.constant.ConstantLine;

import wt.access.AccessControlHelper;
import wt.access.AccessPermission;
import wt.access.AdHocAccessKey;
import wt.access.AdHocControlled;
import wt.epm.EPMDocument;
import wt.epm.EPMDocumentMaster;
import wt.epm.EPMDocumentMasterIdentity;
import wt.epm.structure.EPMStructureHelper;
import wt.fc.IdentityHelper;
import wt.fc.ObjectVector;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.method.MethodContext;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.org.WTPrincipal;
import wt.org.WTPrincipalReference;
import wt.part.WTPart;
import wt.pds.StatementSpec;
import wt.pom.WTConnection;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.vc.config.LatestConfigSpec;
import wt.vc.wip.WorkInProgressHelper;

public class CatlEquipmentCADRenumber implements RemoteAccess {

	public static void main(String[] args) throws WTException, RemoteException, InvocationTargetException {
		RemoteMethodServer rm = RemoteMethodServer.getDefault();
		rm.setUserName("wcadmin");
		rm.setPassword("wcadmin");
		SessionHelper.manager.setAuthenticatedPrincipal("wcadmin");

		rm.invoke("test", CatlEquipmentCADRenumber.class.getName(), null, null, null);

	}

	public static void test() throws Exception {
		EPMDocument epm = EPMUtil.getEPMByNumber("601012-00001.ASM");
		renumberEquipment(epm);
		String number = queryMaxPartNumber("601012");
		System.out.println("Number\t" + number);
	}

	/**
	 * 重命名设备开发图档
	 * 
	 * @param epm
	 * @return 
	 * @throws Exception
	 */
	public static String renumberEquipment(EPMDocument epm) throws Exception {
		if (isEquipmentCAD(epm)) {
			String number = epm.getNumber();
			String cadName = epm.getCADName();
			String format = cadName.substring(cadName.lastIndexOf("."));
			return getAllChildrenByEPM(epm);

		}
		return "";
	}
	
	public static String beatchRenumberEquip() throws Exception{
		String message = "";
		QueryResult qr = getEPMByLikeNumber("6");
		if(qr != null){
			while(qr.hasMoreElements()){
				EPMDocument epm = (EPMDocument) qr.nextElement();
				if(!WorkInProgressHelper.isWorkingCopy(epm)){
					if(epm.getLifeCycleState().toString().equalsIgnoreCase(PartState.DESIGN)||epm.getLifeCycleState().toString().equalsIgnoreCase(PartState.DESIGNMODIFICATION)){
						message = message + renumberEquipment(epm);
					}
				}
			}
		}
		
		return message;
	}

	/**
	 * 判断图档是否为设备开发三维图档
	 * 
	 * @param epm
	 * @return
	 */
	public static boolean isEquipmentCAD(EPMDocument epm) {
		if (epm != null) {
			if(ECADutil.isSCHEPM(epm)||ECADutil.isPCBEPM(epm)){
				return false;
			}
			String number = epm.getNumber();
			String cadName = epm.getCADName();
			if(StringUtils.isNotBlank(cadName)&&cadName.indexOf(".")>0){
			String format = cadName.substring(cadName.lastIndexOf("."));

			if(format.equalsIgnoreCase(".sldprt")||format.equalsIgnoreCase(".sldasm")){
			//if (format.equalsIgnoreCase(".prt") || format.equalsIgnoreCase(".asm")) {
				if (number.startsWith("6") & number.length() == 19) {
					return true;
				}
			}
			}
		}

		return false;
	}

	/**
	 * 判断图档是否为设备开发三维图档
	 * 
	 * @param epm
	 * @return
	 */
	public static boolean isEquipmentTepmCAD(EPMDocument epm) {
		if (epm != null) {
			if(ECADutil.isSCHEPM(epm)||ECADutil.isPCBEPM(epm)){
				return false;
			}
			String number = epm.getNumber();
			String cadName = epm.getCADName();
			if(StringUtils.isNotBlank(cadName)&&cadName.indexOf(".")>0){
			String format = cadName.substring(cadName.lastIndexOf("."));

			if(format.equalsIgnoreCase(".sldprt")||format.equalsIgnoreCase(".sldasm")){
			//if (format.equalsIgnoreCase(".prt") || format.equalsIgnoreCase(".asm")) {
				if (number.startsWith("6") & checkString(number) > 1) {
					return true;
				}
			}
			}
		}

		return false;
	}

	/**
	 * 获取EPM文档的所有子模型
	 * 
	 * @param epmdoc
	 * @return 
	 * @return
	 * @throws Exception
	 */
	public static String getAllChildrenByEPM(EPMDocument epmdoc) throws Exception {
		boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
		WTPrincipal current = SessionHelper.manager.getPrincipal();
		try {
			//Set<EPMDocument> set = new HashSet<EPMDocument>();
			StringBuffer sb = new StringBuffer();
			if(isEquipmentCAD(epmdoc)){
				String pNumber = epmdoc.getNumber();
				String prefix = pNumber.substring(0, pNumber.lastIndexOf("."));
				WTPart part = CommonUtil.getLatestWTpartByNumber(prefix);
				if(part!=null){
					if(!WorkInProgressHelper.isCheckedOut(part) & !WorkInProgressHelper.isCheckedOut(epmdoc)){
						EPMUtil.createLinkEpmToPart(epmdoc, part, 7);
					}
				}else{
					sb.append("编号为【").append(prefix).append("】的物料在系统中不系在!\n");
				}
			}
			QueryResult qr = EPMStructureHelper.service.navigateUsesToIteration(epmdoc, null, true,
					new LatestConfigSpec());
			while (qr.hasMoreElements()) {
				EPMDocument epmUse = (EPMDocument) qr.nextElement();
				//set.add(epmUse);
				if ((isEquipmentCAD(epmdoc) || isEquipmentTepmCAD(epmdoc))
						& !(isEquipmentCAD(epmUse) || isEquipmentTepmCAD(epmUse))) {
					System.out.println("Parent\t" + epmdoc.getNumber() + "\tEPMNumber \t" + epmUse.getNumber());
					String cNumber = epmUse.getNumber();
					String suffix = cNumber.substring(cNumber.lastIndexOf("."));
					String pNumber = epmdoc.getNumber();
					String prefix = pNumber.substring(0, pNumber.lastIndexOf(".")) + "-";
					System.out.println("perfix\t" + prefix);
					String maxNumber = queryMaxPartNumber(prefix);
					System.out.println("maxNumber\t" + maxNumber);
					String number = null;
					if (maxNumber == null) {
						maxNumber = prefix + "01";
					} else {
						int temp = 0;

						temp = Integer.parseInt(
								maxNumber.substring(maxNumber.lastIndexOf("-") + 1, maxNumber.lastIndexOf(".")));
						temp++;

						System.out.println("temp\t" + temp);
						maxNumber = Integer.toString(temp);
						while (maxNumber.length() < 2) {
							maxNumber = "0" + maxNumber;
						}
						System.out.println("MaxNumber1\t" + maxNumber);
						maxNumber = prefix + maxNumber;
						System.out.println("MaxNumber2\t" + maxNumber);

					}
					number = maxNumber;
					maxNumber = maxNumber + suffix;
					System.out.println("maxNumber3\t" + maxNumber);
					
					SessionServerHelper.manager.setAccessEnforced(false);
					SessionHelper.manager.setAdministrator();
					//WTPrincipalReference wtprincipalreference= SessionHelper.manager.getPrincipalReference();
					EPMDocumentMaster master = (EPMDocumentMaster) epmUse.getMaster();
					//AccessControlHelper.manager.addPermission((AdHocControlled) epmUse, wtprincipalreference, AccessPermission.MODIFY_IDENTITY, AdHocAccessKey.WNC_ACCESS_CONTROL);
					EPMDocumentMasterIdentity identity = (EPMDocumentMasterIdentity) master.getIdentificationObject();
					identity.setNumber(maxNumber);
					master = (EPMDocumentMaster) IdentityHelper.service.changeIdentity(master, identity);
					//AccessControlHelper.manager.removePermission((AdHocControlled) epmUse, wtprincipalreference, AccessPermission.MODIFY_IDENTITY, AdHocAccessKey.WNC_ACCESS_CONTROL);
					EPMDocument drawing = getDrawingBy3D(epmUse);
					if(drawing != null){
						EPMDocumentMaster drawingMaster = (EPMDocumentMaster) drawing.getMaster();
					//	AccessControlHelper.manager.addPermission((AdHocControlled) drawing, wtprincipalreference, AccessPermission.MODIFY_IDENTITY, AdHocAccessKey.WNC_ACCESS_CONTROL);
						EPMDocumentMasterIdentity drawingIdentity = (EPMDocumentMasterIdentity) drawingMaster.getIdentificationObject();
						number = number+".SLDDRW";
						drawingIdentity.setNumber(number);
						master = (EPMDocumentMaster) IdentityHelper.service.changeIdentity(drawingMaster, drawingIdentity);
						//AccessControlHelper.manager.removePermission((AdHocControlled) drawing, wtprincipalreference, AccessPermission.MODIFY_IDENTITY, AdHocAccessKey.WNC_ACCESS_CONTROL);
					}
				}

				//set.addAll();
				sb.append(getAllChildrenByEPM(epmUse));
			}
			return sb.toString();
		} finally {
			SessionHelper.manager.setPrincipal(current.getName());
			SessionServerHelper.manager.setAccessEnforced(flag);
		}

	}

	/**
	 * 获取EPM文档的单层子模型
	 * 
	 * @param epmdoc
	 * @return
	 * @throws WTException
	 */
	public static List<EPMDocument> getSingleLevelChildren(EPMDocument epmdoc) throws WTException {
		List<EPMDocument> list = new ArrayList<EPMDocument>();
		QueryResult qr = EPMStructureHelper.service.navigateUsesToIteration(epmdoc, null, true, new LatestConfigSpec());
		while (qr.hasMoreElements()) {
			EPMDocument epmUse = (EPMDocument) qr.nextElement();
			list.add(epmUse);
		}
		return list;
	}

	public static int checkString(String epmnumber) {
		String str = "-";
		int count = 0;
		int start = 0;
		while (epmnumber.indexOf(str, start) >= 0 && start < epmnumber.length()) {
			count++;
			start = epmnumber.indexOf(str, start) + str.length();
		}
		return count;
	}

	public static String queryMaxPartNumber(String numberPrefix) throws Exception {
		MethodContext context = MethodContext.getContext();
		WTConnection wtConn = (WTConnection) context.getConnection();
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		String partNumber = null;
		try {
			String sql = "select max(substr(documentnumber,?,length(documentnumber))) documentnumber from epmdocumentMaster where documentnumber like ? and documentnumber not like ?";
			statement = wtConn.prepareStatement(sql);
			statement.setInt(1, (numberPrefix + "M").length());
			statement.setString(2, numberPrefix + "%");
			statement.setString(3, numberPrefix + "M%");
			System.out.println(statement.toString());
			resultSet = statement.executeQuery();
			while (resultSet.next()) {
				partNumber = resultSet.getString("documentnumber");
				System.out.println("PartNumber\t" + partNumber);
				if (StringUtils.isEmpty(partNumber)) {
					partNumber = null;
				} else {
					partNumber = numberPrefix + partNumber;
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (resultSet != null)
				resultSet.close();
			if (statement != null)
				statement.close();
			if (wtConn != null && wtConn.isActive())
				wtConn.release();
		}
		return partNumber;
	}
	
	/**
	 * 根据三维EPM对象获取对应二维的EPM对象
	 * @param epm 三维EPM对象
	 * @return
	 * @throws WTException
	 */
	public static EPMDocument getDrawingBy3D(EPMDocument epm) throws WTException{
		if(isEquipmentCAD(epm)||isEquipmentTepmCAD(epm)){
			ObjectVector ov = new ObjectVector();
			QueryResult qr = EPMStructureHelper.service.navigateReferencedBy((EPMDocumentMaster)epm.getMaster(), null, true);
			while(qr.hasMoreElements()){
				Object refDoc = qr.nextElement();
				if(refDoc instanceof EPMDocument){
					EPMDocument drawing = (EPMDocument)refDoc;
					if(drawing.getNumber().endsWith(".DRW")){
						ov.addElement(drawing);
					}
				}
			}
			if(!ov.isEmpty()){
				QueryResult result = new QueryResult(ov);
				result = new LatestConfigSpec().process(result);
				if(result.hasMoreElements()){
					return (EPMDocument)result.nextElement();
				}
			}
		}
		return null;
	}
	
	/**
	 * 获取最新版EPMDocument
	 * 
	 * @param docNumber
	 * @return
	 */
	public static QueryResult getEPMByLikeNumber(String docNumber) {
		
		QueryResult qr =null;
		try {
			docNumber = docNumber+"%";
			QuerySpec qs = new QuerySpec(EPMDocument.class);
			SearchCondition sc = new SearchCondition(EPMDocument.class, EPMDocument.NUMBER, SearchCondition.LIKE,
					docNumber);
			qs.appendWhere(sc, new int[] { 0 });
			qr = PersistenceHelper.manager.find((StatementSpec) qs);
			qr = new LatestConfigSpec().process(qr); // 过滤最新

			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return qr;
	}
}
