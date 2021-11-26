package com.catl.integration.trp;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.catl.line.constant.ConstantLine;
import com.catl.line.util.ClassificationUtil;
import com.catl.line.util.IBAUtility;
import com.catl.line.util.NodeUtil;
import com.ptc.core.lwc.server.LWCStructEnumAttTemplate;

import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.httpgw.GatewayAuthenticator;
import wt.iba.definition.StringDefinition;
import wt.iba.value.StringValue;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.pds.StatementSpec;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTAttributeNameIfc;
import wt.util.WTException;

public class ExportTrpAttrExcel_E implements RemoteAccess{
	public static void createReport(String clf, String name,String clfs) throws Exception {
		HSSFWorkbook workbook = new HSSFWorkbook();
		List rows=getReportData(clf,name);
		createDocsSheet("数据",workbook,rows);
		FileOutputStream os=new FileOutputStream("/ptc/"+clf+".xls");
		workbook.write(os);
		os.flush();
		os.close();
	}
	public static void test() throws IOException, Exception {
		String clfs="14,75,FC,SC,P,RD,TM";
		String names="14,75,FC,SC,P,RD,TM";
		String[] clfsarray=clfs.split(",");
		String[] namearray=names.split(",");
		for (int i = 0; i < clfsarray.length; i++) {
			createReport(clfsarray[i],namearray[i],names);
		}
		
	}
	public static void main(String[] args) throws Exception {
		RemoteMethodServer rm=RemoteMethodServer.getDefault();
		GatewayAuthenticator auth = new GatewayAuthenticator();
		auth.setRemoteUser("60021782");
		rm.setAuthenticator(auth);
		rm.invoke("test", ExportTrpAttrExcel_E.class.getName(), null, null, null);
		//createReport();
	}
	
    public static HSSFWorkbook createDocsSheet(String key,HSSFWorkbook wb,List rows) {     //创建excel文件对象  
    	if(rows.size()>0){
         	 List headers=getListHeaders();
         	 System.out.println("header size:"+headers.size());
    		 Sheet sheet = wb.createSheet(key+"_1");  
             Row row1 = sheet.createRow(0);  
             Font font0 = createFonts(wb, Font.BOLDWEIGHT_BOLD, "宋体", false,  
                     (short) 200);  
             Font font1 = createFonts(wb, Font.BOLDWEIGHT_NORMAL, "宋体", false,  
                     (short) 200);  
             for (int i = 0; i < headers.size(); i++) {
            	 sheet.setColumnWidth(i, 5000);
             	createCell(wb, row1, i, (String) headers.get(i), font0);  
 			}
             int l=1;
             int f=2;
             for (int i = 0; i < rows.size(); i++) { 
            	 if(l==60000){
            		 sheet = wb.createSheet(key+"_"+f);  
            		 f++;
            		 l=1;
            	 }
             	Map map=(Map) rows.get(i);
                 Row rowData = sheet.createRow(l++);  
                 for(int j = 0; j < headers.size(); j++){
                 	createCell(wb, rowData, j, (String) map.get(headers.get(j)), font1);  
                 }
             }  
    	}
       
        return wb;  
    }     
  
     private static List getListHeaders(){
		List headers=new ArrayList();
		headers.add("PN");
		headers.add("物料名称");
		headers.add("电芯容量(Ah)");
		headers.add("电芯能量(Wh)");
		headers.add("模组数量(PCS)");
		headers.add("标称电压(V)");
		headers.add("电芯并串联方式");
		headers.add("产品能量(kWh)");
		headers.add("长(mm)");
		headers.add("宽(mm)");
		headers.add("高(mm)");
		return headers;
	}

/** 
     * 创建单元格并设置样式,值 
     *  
     * @param wb 
     * @param row 
     * @param column 
     * @param 
     * @param 
     * @param value 
     */  
    public static void createCell(Workbook wb, Row row, int column,  
        String value, Font font) {  
        Cell cell = row.createCell(column);  
        cell.setCellValue(value);  
    }  
  
    /** 
     * 设置字体 
     *  
     * @param wb 
     * @return 
     */  
    public static Font createFonts(Workbook wb, short bold, String fontName,  
            boolean isItalic, short hight) {  
        Font font = wb.createFont();  
        font.setFontName(fontName);  
        font.setBoldweight(bold);  
        font.setItalic(isItalic);  
        font.setFontHeight(hight);  
        return font;  
    }  
  

    public static String getStringIBAValue(Object object, String ibaName) {
        Persistable pers = (Persistable) object;
        String value = "";
        long ida2a2 = pers.getPersistInfo().getObjectIdentifier().getId();

        try {
            QuerySpec qs = new QuerySpec();
            int defIndex = qs.appendClassList(StringDefinition.class, false);
            int valIndex = qs.appendClassList(StringValue.class, true);
            qs.appendWhere(new SearchCondition(StringValue.class,
                    "theIBAHolderReference.key.id", SearchCondition.EQUAL,
                    ida2a2), new int[] { valIndex });
            qs.appendAnd();
            qs.appendWhere(new SearchCondition(StringValue.class,
                    "definitionReference.key.id", StringDefinition.class,
                    "thePersistInfo.theObjectIdentifier.id"), new int[] {
                    valIndex, defIndex });
            qs.appendAnd();
            qs.appendWhere(new SearchCondition(StringDefinition.class,
                            StringDefinition.NAME, SearchCondition.EQUAL, ibaName),
                    new int[] { defIndex });
            QueryResult qr = PersistenceHelper.manager.find(qs);
            if (qr.hasMoreElements()) {
                Object obj[] = (Object[]) qr.nextElement();
                StringValue strValue = (StringValue) obj[0];
                value = strValue.getValue();
            }
        } catch (QueryException e) {
            e.printStackTrace();
        } catch (WTException e) {
            e.printStackTrace();
        }
        return value;
    }
    public static List<WTPart> getClfParts(String clf)
			throws WTException {
		List<WTPart> pns = new ArrayList<WTPart>();
		QuerySpec qs = new QuerySpec(WTPart.class);
		qs.setAdvancedQueryEnabled(true);
		SearchCondition sc1 = new SearchCondition(WTPart.class, WTPart.NUMBER,
				SearchCondition.LIKE, clf + "%");
		qs.appendWhere(sc1);
		qs.appendAnd();
		SearchCondition sc2 = new SearchCondition(WTPart.class,
				WTAttributeNameIfc.LATEST_ITERATION, SearchCondition.IS_TRUE);
		qs.appendWhere(sc2);
		QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
		while (qr.hasMoreElements()) {
			WTPart wt = (WTPart) qr.nextElement();
			pns.add(wt);
		}
		return pns;
	}
	private static List getReportData(String clf, String name) throws WTException, ParseException {
		List<WTPart> parts=getClfParts(clf);
		List rows=partsinfo(parts,name);
		return rows;
	}
	 public static List<Map> partsinfo(List parts,String name) throws ParseException, WTException {
			SimpleDateFormat sdf=new SimpleDateFormat(ConstantLine.config_rds_time_format);
		    List list=new ArrayList();
		    for (int i = 0; i < parts.size(); i++) {
				WTPart wtPart = (WTPart) parts.get(i);
				if(wtPart.getLifeCycleState().getDisplay(Locale.CHINA).equals("已发布")){
						Map<String, Object> map=new HashMap();
						map.put("PN", wtPart.getNumber());
						map.put("物料名称",name);
						IBAUtility iba=new IBAUtility(wtPart);
						map.put("电芯容量(Ah)", iba.getIBAValue("Cell_Capacity"));
						map.put("电芯能量(Wh)", iba.getIBAValue("Cell_Energy"));
						map.put("模组数量(PCS)", iba.getIBAValue("Module_Quantity"));
						map.put("标称电压(V)", iba.getIBAValue("Nominal_Voltage"));
						map.put("电芯并串联方式", iba.getIBAValue("Cell_Connection_Mode"));
						map.put("产品能量(kWh)", iba.getIBAValue("Product_Energy"));
						if(iba.getIBAValue("Length")!=null){
							map.put("长(mm)", iba.getIBAValue("Length").replace("mm", "").trim());	
						}
						if(iba.getIBAValue("Width")!=null){
							map.put("宽(mm)", iba.getIBAValue("Width").replace("mm", "").trim());
						}
							if(iba.getIBAValue("Height")!=null){
							map.put("高(mm)", iba.getIBAValue("Height").replace("mm", "").trim());
						}
						list.add(map);
				}
			}
			return list;
		 }
}
