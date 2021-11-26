package com.catl.pd.util;  
  
import java.io.File;  
import java.io.FileInputStream;  
import java.io.IOException;  
import java.io.InputStream;  
import java.io.PushbackInputStream;  
  
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletOutputStream;  
import javax.servlet.http.HttpServletResponse;  
  


import org.apache.log4j.Logger;  
import org.apache.poi.POIXMLDocument;  
import org.apache.poi.hssf.usermodel.HSSFDateUtil;  
import org.apache.poi.hssf.usermodel.HSSFWorkbook;  
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;  
import org.apache.poi.openxml4j.opc.OPCPackage;  
import org.apache.poi.poifs.filesystem.POIFSFileSystem;  
import org.apache.poi.ss.usermodel.Cell;  
import org.apache.poi.ss.usermodel.DateUtil;  
import org.apache.poi.ss.usermodel.Sheet;  
import org.apache.poi.ss.usermodel.Workbook;  
import org.apache.poi.ss.usermodel.WorkbookFactory;  
import org.apache.poi.ss.util.CellRangeAddress;  
import org.apache.poi.xssf.usermodel.XSSFCell;  
import org.apache.poi.xssf.usermodel.XSSFRow;  
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;  

import com.catl.pd.helper.CommonHelper;
  
public class PoiUtil {  
  
    /** 日志. */  
    public static Logger logger = Logger.getLogger(PoiUtil.class);  
  
    /** 
     * 私有构造方法. 
     */  
    private PoiUtil() {  
  
    }  
  
    /** 
     * 打开excle文件 
     *  
     * @param fileName 
     *            带后缀excle文件名称 
     * @return 
     * @throws IOException 
     * @throws InvalidFormatException 
     */  
    public static Workbook openExcleFile(String filePath) throws Exception {  
        // 定义返回值  
        Workbook workbook = null;  
        try {  
            // 打开工作簿  
            workbook = WorkbookFactory.create(new File(filePath));  
        } catch (Exception e) {  
            e.printStackTrace();  
            logger.error(e.getMessage());  
        }  
        // 返回  
        return workbook;  
    }  
    public static Workbook createworkbook(String filePath) throws IOException,InvalidFormatException {  
        InputStream inp = new FileInputStream(filePath);  
        if (!inp.markSupported()) {  
           inp = new PushbackInputStream(inp, 8);  
        }  
        if (POIFSFileSystem.hasPOIFSHeader(inp)) {  
          return new HSSFWorkbook(inp);  
        }  
        if (POIXMLDocument.hasOOXMLHeader(inp)) {  
          return new XSSFWorkbook(OPCPackage.open(inp));  
        }  
        throw new IllegalArgumentException("你的excel版本目前poi解析不了");  
    }  
  
    /** 
     *  
     * @param cell 
     *            获取的单元格 
     * @return 返回单元格中的值 
     */  
    public static Object getCellValue(Cell cell) {  
        // 定义返回值  
        Object objResult = null;  
  
        // 判断单元格中的值  
        if (cell != null) {  
            // 匹配格式类型  
            switch (cell.getCellType()) {  
  
            // 字符串类型  
            case Cell.CELL_TYPE_STRING:  
  
                objResult = cell.getRichStringCellValue().getString();  
  
                break;  
            // 货币类型  
            case Cell.CELL_TYPE_NUMERIC:  
  
                if (DateUtil.isCellDateFormatted(cell)) {  
                    objResult = cell.getDateCellValue();  
                } else {  
                    objResult = cell.getNumericCellValue();  
                }  
  
                break;  
            // 布尔类型  
            case Cell.CELL_TYPE_BOOLEAN:  
  
                objResult = cell.getBooleanCellValue();  
  
                break;  
            // 公式  
            case Cell.CELL_TYPE_FORMULA:  
                try {  
                    objResult = cell.getNumericCellValue();  
                } catch (IllegalStateException e) {  
                    objResult = String.valueOf(cell.getRichStringCellValue());  
                }  
            default:  
            }  
        }  
  
        // 返回取到的单元格值  
        return objResult;  
    }  
  
    /** 
     * 复制单元格 
     *  
     * @param currentSheet 
     *            sheet页 
     * @param startRow 
     *            开始行 
     * @param endRow 
     *            结束行 
     * @param pPosition 
     *            目标位置 
     */  
    public static void copyRows(Sheet currentSheet, int startRow, int endRow,  
            int pPosition) {  
  
        int pStartRow = startRow - 1;  
        int pEndRow = endRow - 1;  
        int targetRowFrom;  
        int targetRowTo;  
        int columnCount;  
        CellRangeAddress region = null;  
        int i;  
        int j;  
  
        if (pStartRow == -1 || pEndRow == -1) {  
            return;  
        }  
  
        for (i = 0; i < currentSheet.getNumMergedRegions(); i++) {  
            region = currentSheet.getMergedRegion(i);  
            if ((region.getFirstRow() >= pStartRow)  
                    && (region.getLastRow() <= pEndRow)) {  
                targetRowFrom = region.getFirstRow() - pStartRow + pPosition;  
                targetRowTo = region.getLastRow() - pStartRow + pPosition;  
                CellRangeAddress newRegion = region.copy();  
                newRegion.setFirstRow(targetRowFrom);  
                newRegion.setFirstColumn(region.getFirstColumn());  
                newRegion.setLastRow(targetRowTo);  
                newRegion.setLastColumn(region.getLastColumn());  
                currentSheet.addMergedRegion(newRegion);  
            }  
        }  
  
        for (i = pStartRow; i <= pEndRow; i++) {  
            XSSFRow sourceRow = (XSSFRow) currentSheet.getRow(i);  
            columnCount = sourceRow.getLastCellNum();  
            if (sourceRow != null) {  
                XSSFRow newRow = (XSSFRow) currentSheet.createRow(pPosition  
                        - pStartRow + i);  
                newRow.setHeight(sourceRow.getHeight());  
                for (j = 0; j < columnCount; j++) {  
                    XSSFCell templateCell = sourceRow.getCell(j);  
                    if (templateCell != null) {  
                        XSSFCell newCell = newRow.createCell(j);  
                        copyCell(templateCell, newCell);  
                    }  
                }  
            }  
        }  
    }  
  
    public static void copyCell(XSSFCell srcCell, XSSFCell distCell) {  
        distCell.setCellStyle(srcCell.getCellStyle());  
        if (srcCell.getCellComment() != null) {  
            distCell.setCellComment(srcCell.getCellComment());  
        }  
        int srcCellType = srcCell.getCellType();  
        distCell.setCellType(srcCellType);  
        if (srcCellType == XSSFCell.CELL_TYPE_NUMERIC) {  
            if (HSSFDateUtil.isCellDateFormatted(srcCell)) {  
                distCell.setCellValue(srcCell.getDateCellValue());  
            } else {  
                distCell.setCellValue(srcCell.getNumericCellValue());  
            }  
        } else if (srcCellType == XSSFCell.CELL_TYPE_STRING) {  
            distCell.setCellValue(srcCell.getRichStringCellValue());  
        } else if (srcCellType == XSSFCell.CELL_TYPE_BLANK) {  
            // nothing21  
        } else if (srcCellType == XSSFCell.CELL_TYPE_BOOLEAN) {  
            distCell.setCellValue(srcCell.getBooleanCellValue());  
        } else if (srcCellType == XSSFCell.CELL_TYPE_ERROR) {  
            distCell.setCellErrorValue(srcCell.getErrorCellValue());  
        } else if (srcCellType == XSSFCell.CELL_TYPE_FORMULA) {  
            distCell.setCellFormula(srcCell.getCellFormula());  
        } else { // nothing29  
  
        }  
    }  
      
    

    /** 
     * 复制单元格 
     *  
     * @param currentSheet 
     *            sheet页 
     * @param startRow 
     *            开始行 
     * @param endRow 
     *            结束行 
     * @param pPosition 
     *            目标位置 
     */  
    public static List copyXRows(Sheet currentSheet, int startCol, int endCol,int time,List templatecells,int  interval) {  
    	int pPosition=(startCol+interval*time)-1;
        int pStartCol = startCol - 1;  
        int pEndCol = endCol - 1;  
        int targetColFrom;  
        int targetColTo;  
        CellRangeAddress region = null;  
        int i;  
        int j;  
        List newcells=getRegionCellByInterval(templatecells,interval*time,(XSSFSheet)currentSheet);
        for (i = 0; i < currentSheet.getNumMergedRegions(); i++) {  
            region = currentSheet.getMergedRegion(i);  
            if ((region.getFirstColumn() >= pStartCol)&& (region.getLastColumn() <= pEndCol)) {  
                targetColFrom = region.getFirstColumn()  + pPosition;  
                targetColTo = region.getLastColumn() + pPosition;  
                CellRangeAddress newRegion = region.copy();  
                newRegion.setFirstRow(region.getFirstRow());  
                newRegion.setFirstColumn(targetColFrom);  
                newRegion.setLastRow(region.getLastRow());  
                newRegion.setLastColumn(targetColTo);  
                currentSheet.addMergedRegion(newRegion); 
            }  
        }  
        List <XSSFCell>ncells=new ArrayList<XSSFCell>();
        for (int k = 0; k < templatecells.size(); k++) {
        	 XSSFCell templateCell = (XSSFCell) templatecells.get(k);  
        	 XSSFCell newCell = (XSSFCell) newcells.get(k);  
        	 List records=new ArrayList();
             if (templateCell != null) {  
            	 //newCell.getRow().setHeight(templateCell.getRow().getHeight());
            	 int oldcolindex=templateCell.getColumnIndex();
            	 if(!records.contains(oldcolindex)){
                	 currentSheet.setColumnWidth(newCell.getColumnIndex(), currentSheet.getColumnWidth(oldcolindex));
                	 records.add(oldcolindex);
            	 }
                 copyCell(templateCell, newCell);  
                 ncells.add(newCell);
             }  
		}
        return ncells;
        
    }  
   
    private static List getRegionCellByInterval(List templatecells, int offset,
			XSSFSheet currentSheet) {
    	List<XSSFCell> cells=new ArrayList<XSSFCell>();
		for (int j = 0; j < templatecells.size(); j++) {
			XSSFCell cell=(XSSFCell) templatecells.get(j);
			if(cell!=null){
				cell=currentSheet.getRow(cell.getRowIndex()).createCell(cell.getColumnIndex()+offset);
			}
			cells.add(cell);
		}
		return cells;
	}

	public static List<XSSFCell> getRegionCell(String td1,String td2,int offset,XSSFSheet currentSheet){
    	String ids="ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    	List<XSSFCell> cells=new ArrayList<XSSFCell>();
    	String c1=td1.substring(0,1);
    	int r1=Integer.valueOf(td1.substring(1,td1.length()));
    	String c2=td2.substring(0,1);
    	int r2=Integer.valueOf(td2.substring(1,td2.length()));
    	int index1=ids.indexOf(c1);
    	int index2=ids.indexOf(c2);
    	for(int i=index1;i<=index2;i++){
    		String cid=ids.substring(i,i+1);
    		for (int j = r1; j <=r2; j++) {
    			String region=cid+j;
    			int[] rowcol=CommonHelper.translateRegion(region);
    			XSSFRow row=currentSheet.getRow(rowcol[1]);
    			XSSFCell cell=row.getCell(rowcol[0]+offset);
    			if(cell==null){
    				cell=currentSheet.getRow(rowcol[1]).createCell(rowcol[0]+offset);
    			}
    			cells.add(cell);
    		}
    	}
		return cells;
    }
    public static void fileDownload(HttpServletResponse response,String path){    
            //1.设置文件ContentType类型，这样设置，会自动判断下载文件类型    
            response.setContentType("multipart/form-data");    
            //2.设置文件头：最后一个参数是设置下载文件名(假如我们叫a.pdf)    
            response.setHeader("Content-Disposition", "attachment;fileName="+"a.xlsx");    
            ServletOutputStream out;    
            //通过文件路径获得File对象(假如此路径中有一个download.pdf文件)    
            File file = new File(path);    
            try {    
                FileInputStream inputStream = new FileInputStream(file);    
                //3.通过response获取ServletOutputStream对象(out)    
                out = response.getOutputStream();    
                int b = 0;    
                byte[] buffer = new byte[512];    
                while (b != -1){    
                    b = inputStream.read(buffer);    
                    //4.写到输出流(out)中    
                    out.write(buffer,0,b);    
                }    
                inputStream.close();    
                out.close();    
                out.flush();    
            } catch (IOException e) {    
                e.printStackTrace();    
            }    
   }    
}  