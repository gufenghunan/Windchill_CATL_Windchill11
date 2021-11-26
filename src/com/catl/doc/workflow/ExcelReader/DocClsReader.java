package com.catl.doc.workflow.ExcelReader;

import java.util.Vector;

import org.apache.poi.hssf.usermodel.HSSFSheet;

import com.catl.doc.workflow.DocClassificationModel;

/**
 * 增强的读取Excel模板功能类
 * 用来处理文档分类模板清单 
 */
public class DocClsReader extends ExcelReader
{
	private static final long serialVersionUID = 1L;
	protected  HSSFSheet secondhssfsheet = null;
	protected  HSSFSheet thirdhssfsheet = null;

	public DocClsReader(String fileName)
	{
		super(fileName);
	}
	
	public DocClsReader(String fileName,String sheetCount)
	{
		super(fileName);
	}
	
	public void readData()
	{
		Vector keyVector=new Vector();
		
		for (int j=2;j<65536;j++)
		{
			String key=getCell(1,j, this.sourcehssfsheet);
			
			if (key.equals(""))
				break;
			
			keyVector.add(key);
			System.out.println("key="+key);
			
		}
		
		for (int i = 2; i < 65536; i++)
		{
			String number=getCell(i, 1, this.sourcehssfsheet);
			
			if (number.equals(""))
				break;
	        	DocClassificationModel DocObject= new DocClassificationModel();
	        	DocObject.setId(getCell(i,1, this.sourcehssfsheet).trim());
	        	DocObject.setDocType(getCell(i,2, this.sourcehssfsheet).trim());
	        	DocObject.setDocclassify(getCell(i,3, this.sourcehssfsheet).trim());
				DocObject.setSumbit(getCell(i,4, this.sourcehssfsheet).trim());
				DocObject.setCollator(getCell(i,5, this.sourcehssfsheet).trim());
				DocObject.setCheck(getCell(i,6, this.sourcehssfsheet).trim());
				DocObject.setReview(getCell(i,7, this.sourcehssfsheet).trim());
				DocObject.setCountersign(getCell(i,8, this.sourcehssfsheet).trim());
				DocObject.setNotice(getCell(i,9, this.sourcehssfsheet).trim());
				
				dataVector.add(DocObject);
		
			
		}
	}

}
