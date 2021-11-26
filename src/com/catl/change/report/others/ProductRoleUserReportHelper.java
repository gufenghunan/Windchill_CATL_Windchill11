package com.catl.change.report.others;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.TimeZone;
import java.util.Vector;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Workbook;

import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.inf.container.WTContainerHelper;
import wt.inf.team.ContainerTeam;
import wt.org.DirectoryContextProvider;
import wt.org.OrganizationServicesHelper;
import wt.org.WTGroup;
import wt.org.WTOrganization;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.project.Role;
import wt.query.QuerySpec;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.catl.change.report.Excel2007Handler;
import com.catl.common.util.WCLocationConstants;

public class ProductRoleUserReportHelper {
	
	private static Logger log = Logger.getLogger(ProductRoleUserReportHelper.class.getName());
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

	public ProductRoleUserReportHelper() {
	}

	public void generateReport(HttpServletResponse response) throws WTException, WTPropertyVetoException {
		
		boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
		try {
			// WTContainerHelper.service.get
			// ContainerTeamHelper.service.getMembersMap(containerteam);
			// WTContainerHelper.service
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
			dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
			String currentDate = dateFormat.format((Date) new Timestamp(System.currentTimeMillis()));
			String fileName = "PLM_ProductRoleUser_" + currentDate + ".xlsx";

			Workbook wb = generateExcel(fileName);
			String filename = java.net.URLEncoder.encode(fileName, "UTF-8");
			response.setContentType("application/vnd.ms-excel");
			response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

			OutputStream os = response.getOutputStream();
			wb.write(os);

			response.setStatus(HttpServletResponse.SC_OK);
			response.flushBuffer();

			os.flush();
			os.close();

		} catch (IOException e) {
			throw new WTException(e, e.getLocalizedMessage());
		} finally {
			SessionServerHelper.manager.setAccessEnforced(flag);
		}
	}

	private Workbook generateExcel(String fileName) throws WTException {

		String filePathName = WCLocationConstants.WT_CODEBASE + File.separator
				+ "com" + File.separator + "catl" + File.separator
				+ "checkPDFData" + File.separator
				+ "productRoleUser_template.xlsx";

		Excel2007Handler excelHander;
		try {
			excelHander = new Excel2007Handler(filePathName);

			List<ContainerTeam> containerteamlist = getContainerTeam();

			int rowNum = 1;
			int iCol = 0;

//			for (int i=0; i<100000; i++){
//				iCol = 0;
//				rowNum++;
//				excelHander.setStringValue(rowNum, iCol++, "1111111111111");
//				excelHander.setStringValue(rowNum, iCol++, "2222222222222");
//				excelHander.setStringValue(rowNum, iCol++, "3333333333333");
//				excelHander.setStringValue(rowNum, iCol++, "44444444444444");
//				excelHander.setStringValue(rowNum, iCol++, "5555555555555");
//				excelHander.setStringValue(rowNum, iCol++, "66666666666666");
//			}
			for (ContainerTeam containerteam : containerteamlist) {

				Vector<Role> roles = containerteam.getRoles();
				for (int i = 0; i < roles.size(); i++) {
					Role role = roles.get(i);
					Enumeration enumPrin = containerteam.getPrincipalTarget(role);
					
					if (!enumPrin.hasMoreElements()){
						
						iCol = 0;
						rowNum++;
						excelHander.setStringValue(rowNum, iCol++, containerteam.getName());
						excelHander.setStringValue(rowNum, iCol++, containerteam.getContainerName());
						excelHander.setStringValue(rowNum, iCol++, role.getDisplay());
//						excelHander.setStringValue(rowNum, iCol++, "");
						excelHander.setStringValue(rowNum, iCol++, "");
						excelHander.setStringValue(rowNum, iCol++, "");
						continue;
					}
					while (enumPrin.hasMoreElements()) {
						WTPrincipalReference tempPrinRef = (WTPrincipalReference) enumPrin.nextElement();
						if (tempPrinRef.getObject() instanceof WTGroup) {
							WTGroup wtGroup = (WTGroup) tempPrinRef.getObject();
							wtGroup.getName();
							
							
							iCol = 0;
							rowNum++;
							excelHander.setStringValue(rowNum, iCol++, containerteam.getName());
							excelHander.setStringValue(rowNum, iCol++, containerteam.getContainerName());
							excelHander.setStringValue(rowNum, iCol++, role.getDisplay());
//							excelHander.setStringValue(rowNum, iCol++, "");
							excelHander.setStringValue(rowNum, iCol++, wtGroup.getName());
							excelHander.setStringValue(rowNum, iCol++, "");
							
							
//							DirectoryContextProvider dcp = WTContainerHelper.service.getExchangeContainer().getContextProvider();
//							WTOrganization org = OrganizationServicesHelper.manager.getOrganization("CATL", dcp);
//							Enumeration<?> enu = OrganizationServicesHelper.manager.getGroups(wtGroup.getName(),WTContainerHelper.service.getOrgContainer(org).getContextProvider());
//
//							if (!enu.hasMoreElements()) {
//								String[] services = wt.org.OrganizationServicesHelper.manager.getDirectoryServiceNames();
//								wt.org.DirectoryContextProvider dc_provider = wt.org.OrganizationServicesHelper.manager.newDirectoryContextProvider(services, null);
//								enu = wt.org.OrganizationServicesHelper.manager.getGroups(wtGroup.getName(), dc_provider);
//							}
//							while (enu.hasMoreElements()) {
//								Object o = enu.nextElement();
//								if (o instanceof WTGroup) {
//									WTGroup group = (WTGroup) o;
//									Enumeration users = group.members();
//									while (users.hasMoreElements()) {
//										WTUser principal = (WTUser) users.nextElement();
//										
//										iCol = 0;
//										rowNum++;
//										excelHander.setStringValue(rowNum, iCol++, containerteam.getName());
//										excelHander.setStringValue(rowNum, iCol++, containerteam.getContainerName());
//										excelHander.setStringValue(rowNum, iCol++, role.getDisplay());
//										excelHander.setStringValue(rowNum, iCol++, group.getName());
//										excelHander.setStringValue(rowNum, iCol++, principal.getName());
//										excelHander.setStringValue(rowNum, iCol++, principal.getFullName());
//									}
//								}
//							}
						} else {
							WTUser principal = (WTUser)tempPrinRef.getPrincipal();

							iCol = 0;
							rowNum++;
							excelHander.setStringValue(rowNum, iCol++,containerteam.getName());
							excelHander.setStringValue(rowNum, iCol++,containerteam.getContainerName());
							excelHander.setStringValue(rowNum, iCol++,role.getDisplay());
//							excelHander.setStringValue(rowNum, iCol++, "");
							excelHander.setStringValue(rowNum, iCol++,principal.getName());
							excelHander.setStringValue(rowNum, iCol++,principal.getFullName());
						}
					}
				}
			}
			return excelHander.getWorkbook();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	static List<ContainerTeam> getContainerTeam() throws WTException {

		List<ContainerTeam> containerteamlist = new ArrayList<ContainerTeam>();
		QuerySpec qs = new QuerySpec(ContainerTeam.class);
        qs.appendOrderBy(ContainerTeam.class, ContainerTeam.NAME, false);

		QueryResult qr = PersistenceHelper.manager.find(qs);
		while (qr.hasMoreElements()) {

			ContainerTeam containerteam = (ContainerTeam) qr.nextElement();
			containerteamlist.add(containerteam);

		}
		return containerteamlist;
	}

}
