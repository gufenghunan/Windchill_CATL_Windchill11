package com.catl.line.util.excel2pdf;

import java.io.InputStream;

/**
 * Created by cary on 6/15/17.
 */
public class ExcelObject {
    /**
     * 锚名称
     */
    private String anchorName;
    /**
     * Excel Stream
     */
    private InputStream inputStream;
    /**
     * POI Excel
     */
    private Excel excel;

    public ExcelObject(InputStream inputStream,String sheetName){
        this.inputStream = inputStream;
        this.excel = new Excel(this.inputStream,sheetName);
    }

    public ExcelObject(String anchorName , InputStream inputStream,String sheetName){
        this.anchorName = anchorName;
        this.inputStream = inputStream;
        this.excel = new Excel(this.inputStream,sheetName);
    }
    public String getAnchorName() {
        return anchorName;
    }
    public void setAnchorName(String anchorName) {
        this.anchorName = anchorName;
    }
    public InputStream getInputStream() {
        return this.inputStream;
    }
    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }
    Excel getExcel() {
        return excel;
    }
}