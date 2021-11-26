package com.catl.pd.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import wt.doc.WTDocument;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.util.WTException;
import wt.util.WTProperties;

import com.catl.pd.constant.ConstantPD;
import com.catl.pd.entity.CellAttr;
import com.catl.pd.helper.CommonHelper;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;

public class SummaryCompare implements RemoteAccess{
	private static String wt_home = "";
	static {
		try {
			wt_home = WTProperties.getLocalProperties().getProperty("wt.home",
					"UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args) throws RemoteException, InvocationTargetException {
		RemoteMethodServer rm=RemoteMethodServer.getDefault();
		rm.setUserName("wcadmin");
		rm.setPassword("wcadmin");
		rm.invoke("test", SummaryCompare.class.getName(), null, null, null);
	}
	public static void test() throws Exception {
		List docs=new ArrayList();
		WTDocument doc=CommonUtil.getLatestWTDocByNumber("000000-00000079");
		docs.add(doc);
		XSSFWorkbook wb=combineSummary(new File(wt_home+ConstantPD.config_comparesummary_template), getListFiles(docs),getListNames(docs));
	    OutputStream out=new FileOutputStream("/ptc/aa.xlsx");
		wb.write(out);
		out.flush();
		out.close();
	}
	public static XSSFWorkbook compare(NmCommandBean bean) throws Exception{
			List<NmOid> oids=bean.getSelectedOidForPopup();
			List docs=new ArrayList();
			for(NmOid nmOid:oids){
				WTDocument document=(WTDocument) nmOid.getRefObject();
				System.out.println(document.getNumber());
				docs.add(document);
			}
			if(docs.size()==0){
				throw new WTException("请先选择");
			}
			XSSFWorkbook wb=combineSummary(new File(wt_home+ConstantPD.config_comparesummary_template), getListFiles(docs),getListNames(docs));
		    return wb;
	}
	
    private static List getListNames(List<WTDocument> docs) {
    	 List fills=new ArrayList();
		  for (int i = 0; i <docs.size(); i++) {
			     WTDocument doc=docs.get(i);
				 fills.add(doc.getName()	);
		   }
		  return fills;
	}
	  public static List<XSSFCell> getListFiles(List<WTDocument> docs) throws Exception {
		  List fills=new ArrayList();
		  for (int i = 0; i <docs.size(); i++) {
			     WTDocument doc=docs.get(i);
				 InputStream in=WTDocumentUtil.downloadDocPrimaryStream(doc);
				 XSSFWorkbook wb = new XSSFWorkbook(in);
				 XSSFSheet sheet=wb.getSheet("Summary");
				 List<XSSFCell> cells= PoiUtil.getRegionCell(ConstantPD.config_comparesummary_r1, ConstantPD.config_comparesummary_r2, 0, sheet);
				 fills.add(cells);
		   }
		  return fills;
	  }
	
    public static XSSFWorkbook combineSummary(File tfile,List fillcells,List names) throws Exception {
    	InputStream fin=new FileInputStream(tfile);
		XSSFWorkbook fwb = new XSSFWorkbook(fin);
		XSSFSheet fsheet=fwb.getSheetAt(0);
		Map map=new LinkedHashMap();
		//生成填写区域
		List templatecells=PoiUtil.getRegionCell(ConstantPD.config_comparesummary_r1,ConstantPD.config_comparesummary_r2,0,(XSSFSheet)fsheet);
		map.put(names.get(0), templatecells);
		for (int i = 0; i < fillcells.size()-1; i++) {
			List ncells=PoiUtil.copyXRows(fsheet, ConstantPD.config_comparesummary_startcol,ConstantPD.config_comparesummary_endcol,(i+1), templatecells,ConstantPD.config_comparesummary_interval);
			map.put(names.get((i+1)), ncells);
		}
		Set keyset=map.keySet();
		Iterator ites=keyset.iterator();
		int index=0;
		String titleregion=ConstantPD.config_comparesummary_title;
		int[] trow_col=CommonHelper.translateRegion(titleregion);
		XSSFRow row=fsheet.getRow(trow_col[1]);
		int lastcol=0;
		while(ites.hasNext()){
			String key=(String) ites.next();
			List fills=(List) fillcells.get(index);
			List cells=(List) map.get(key);
			XSSFCell tcell=row.getCell(trow_col[0]+ConstantPD.config_comparesummary_interval*index);
			lastcol=trow_col[0]+ConstantPD.config_comparesummary_interval*index;
			if(tcell==null){
				tcell=row.createCell(trow_col[0]+ConstantPD.config_comparesummary_interval*index);
			}
			tcell.setCellValue(key);
			for (int i = 0; i < cells.size(); i++) {
				XSSFCell cell=(XSSFCell) cells.get(i);
				XSSFCell fill=(XSSFCell) fills.get(i);
				//cell 要填写的单元格，fills 填写数据的单元格
				CellAttr attr=new CellAttr();
				attr=CommonHelper.getCellValue(fill, attr);
				cell.setCellValue(String.valueOf(attr.getValue()));
			}
			index++;
		}
		List columns=new ArrayList();
		for (int i = ConstantPD.config_comparesummary_endcol; i < lastcol; i++) {
			XSSFCell tcell=row.getCell(i);
			if(tcell==null){
				fsheet.setColumnWidth(i, 0);
			}
		}
		return fwb;
  		
      	
  	}
}
