package com.catl.bom.load;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.catl.common.util.PartUtil;
import com.catl.doc.maturityUpReport.MaturityUpReportHelper;
import com.catl.promotion.util.PromotionUtil;
import com.catl.promotion.util.WorkflowUtil;

import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.fc.collections.WTKeyedHashMap;
import wt.fc.collections.WTKeyedMap;
import wt.inf.container.WTContainer;
import wt.inf.library.WTLibrary;
import wt.method.RemoteAccess;
import wt.occurrence.OccurrenceHelper;
import wt.part.PartUsesOccurrence;
import wt.part.Quantity;
import wt.part.QuantityUnit;
import wt.part.ReferenceDesignatorSet;
import wt.part.ReferenceDesignatorSetDelegateFactory;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.part.WTPartUsageLink;
import wt.pds.StatementSpec;
import wt.pom.Transaction;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.query.WhereExpression;
import wt.util.WTException;
import wt.util.WTRuntimeException;
import wt.vc.config.LatestConfigSpec;

public class BOMLoad implements RemoteAccess
{
    File file = null;
	ArrayList<BOMLoadBean> bomLoadBeanList = new ArrayList<BOMLoadBean>();

	public BOMLoad(File file)
	{
		this.file = file;
	}

	public ArrayList<String> precheckLoadFile(String oid) throws InvalidFormatException, FileNotFoundException, IOException, WTException
	{
		ArrayList<String> checkResultList = new ArrayList<String>();
		Workbook wb = WorkbookFactory.create(new FileInputStream(file));
		Sheet sheet = wb.getSheetAt(0);
		// 编码 名称 数量 位号
		// String title[] = { "number", "name", "quantity",
		// "referenceDesignator" };
		ReferenceFactory rf = new ReferenceFactory();
        Persistable obj = rf.getReference(oid).getObject();
        WTPart parent = (WTPart) obj;
        String parentNumber = parent.getNumber();
		
	    List<String> numberList = new ArrayList<String>();
		for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++)
		{
			Row row = sheet.getRow(i);
			if (row == null)
			{
				break;
			}
			Cell cell = row.getCell(0);
			String cellValue = getCellValue(cell);
			if ("#EOF".equalsIgnoreCase(cellValue))
			{
				break;
			}

			String number = getCellValue(row.getCell(0));
			String name = getCellValue(row.getCell(1));
			String quantity = getCellValue(row.getCell(2));
			String referenceDesignator = getCellValue(row.getCell(3));
			if (number.isEmpty() )
			{
				continue;
			}
			
			if(numberList.contains(number)){
			    System.out.println("number =" + number);
			    checkResultList.add("编号为"+number+"的物料重复!");
			}
			else{
				if(!MaturityUpReportHelper.checkMaturity(parent, number)){
					checkResultList.add("零部件"+number+"的成熟度必须为3或者6");
				}
				
				Set<Persistable> all = PromotionUtil.getAllLatestVersionByMaster(PartUtil.getWTPartMaster(number));
				for (Persistable p : all) {
					WTPart part = (WTPart) p;
					Set<String> promotionNumbers = PromotionUtil.isExsitPromotion(part);
					if (promotionNumbers.size() > 0) {
						checkResultList.add("零部件" + part.getNumber() + "已经被加入未完成的编号为" + promotionNumbers.toString() + "的物料设计禁用单中！\n");
					}
				}
			} 
				
		    
			if(parentNumber.equals(number)){
			    System.err.println("Error =" + number + "is parent part Number,");
                checkResultList.add("导入的Excel文件中不能包含当前的父件编码");
			}
			
			
			
			numberList.add(number);
			
			WTPart children = getPart(number);
			if (children != null)
			{
			    String state=children.getState().toString();
				if (!isLoadState(children))
				{
					checkResultList.add("第" + i + "行的物料" + number+"存在于:"+children.getContainerName()+
					        "生命周期状态为："+children.getState().getState().getDisplay()+"，不符合业务规则。");
				}
			} else
			{
				checkResultList.add("第" + i + "行的物料“" + number + "”在系统中不存在");
				continue;
			}
			
			
		    
			
			BOMLoadBean bomLoadBean = new BOMLoadBean(number, name, quantity, referenceDesignator);
			if (!bomLoadBeanList.contains(bomLoadBean))
			{
				bomLoadBean.setPart(children);
				bomLoadBeanList.add(bomLoadBean);
			}
		}
		
		
		
		return checkResultList;
	}

	private WTPart getPart(String number) throws WTException
	{
		WTPart part = null;
		StatementSpec stmtSpec = new QuerySpec(WTPart.class);
		WhereExpression where = new SearchCondition(WTPart.class, WTPart.NUMBER, SearchCondition.EQUAL, number.toUpperCase());
		QuerySpec querySpec = (QuerySpec) stmtSpec;
		querySpec.appendWhere(where, new int[] { 0 });
		QueryResult qr = PersistenceServerHelper.manager.query(stmtSpec);
		if (qr.hasMoreElements())
		{
			LatestConfigSpec configSpec = new LatestConfigSpec();
			qr = configSpec.process(qr);
			part = (WTPart) qr.nextElement();
		}
		return part;
	}

	private boolean isLoadState(WTPart part)
	{
		WTContainer container=part.getContainer();
		String state=part.getState().toString();
		Boolean isok=false;
		if(container  instanceof WTLibrary)
		{
			if (state .equals("RELEASED"))
			{
				isok=true;
			}
		}else {
			if (state .equals("DESIGN")||state .equals("DESIGNMODIFICATION")||state .equals("DESIGNREVIEW")||state .equals("RELEASED"))
			{
				isok=true;
			}
		}

		return isok;
	}

	private String getCellValue(Cell cell) throws WTException
	{
		String cellValue = "";
		if (cell == null)
		{
			cellValue = "";
		} else
		{
			switch (cell.getCellType())
			{
			case Cell.CELL_TYPE_NUMERIC:
				java.text.DecimalFormat formatter = new java.text.DecimalFormat("########.###");
				cellValue = formatter.format(cell.getNumericCellValue());
				if (cellValue != null)
				{
					cellValue = cellValue.trim();
				}
				break;
			case Cell.CELL_TYPE_STRING:
				cellValue = cell.getStringCellValue().trim();
				break;
			default:
				cellValue = "";
			}
		}
		return cellValue;
	}

	public void loadBOM(String oid) throws WTRuntimeException, WTException
	{
		ReferenceFactory rf = new ReferenceFactory();
		Persistable obj = rf.getReference(oid).getObject();
		Transaction tranc = new Transaction();
		tranc.start();
		if (obj instanceof WTPart)
		{
			try
			{
				WTPart parent = (WTPart) obj;
				removeAllChildren(parent);
				for (BOMLoadBean bean : bomLoadBeanList)
				{
					WTPartMaster master = (WTPartMaster) bean.getPart().getMaster();
					WTPartUsageLink link = WTPartUsageLink.newWTPartUsageLink(parent, master);
					QuantityUnit quantityUnit = master.getDefaultUnit();
					double amount = Double.parseDouble(bean.getQuantity());
					link.setQuantity(Quantity.newQuantity(amount, quantityUnit));
					PersistenceServerHelper.manager.insert(link);
					String locationOrder = bean.getReferenceDesignatorRange();
					locationOrder = locationOrder.replace("，", ",");
					ReferenceDesignatorSetDelegateFactory rdsFactory = new ReferenceDesignatorSetDelegateFactory();
					ReferenceDesignatorSet rds = rdsFactory.get(locationOrder);
					List<?> occList = rds.getExpandedReferenceDesignators();
					OccurrenceHelper.service.setSkipValidation(false);
//					StringTokenizer occstrs = new StringTokenizer(bean.getReferenceDesignatorRange(), ",");
					WTKeyedMap occuranceMap = new WTKeyedHashMap();
					for (Object objocc : occList) {
						String curoccstr = (String)objocc;
						//System.out.println("reference Designator = " + curoccstr);//C1-C4
						if ((curoccstr != null) && (curoccstr.length() > 0))
						{
							PartUsesOccurrence newOccurrence = PartUsesOccurrence.newPartUsesOccurrence(link);
							newOccurrence.setName(curoccstr);
			                occuranceMap.put(newOccurrence,null);
						}
					}
//					while (occstrs.hasMoreTokens())
//					{
//						String curoccstr = occstrs.nextToken();
//						System.out.println("reference Designator = " + curoccstr);//C1-C4
//						if ((curoccstr != null) && (curoccstr.length() > 0))
//						{
//							PartUsesOccurrence newOccurrence = PartUsesOccurrence.newPartUsesOccurrence(link);
//							newOccurrence.setName(curoccstr);
//			                occuranceMap.put(newOccurrence,null);
//						}
//					}
					//System.out.println("map size= " + occuranceMap.size());
					OccurrenceHelper.service.saveUsesOccurrenceAndData(occuranceMap);
				    occuranceMap.clear();
				}
			} catch (Exception e)
			{
				e.printStackTrace();
				tranc.rollback();
			}
		}
		tranc.commit();
	}

	private void removeAllChildren(WTPart parent) throws WTException
	{
		QueryResult qr = new QueryResult();
		QuerySpec queryspec = new QuerySpec(WTPartUsageLink.class);
		queryspec.appendWhere(new SearchCondition(WTPartUsageLink.class, "roleAObjectRef.key", "=", PersistenceHelper.getObjectIdentifier(parent)), new int[] {});
		qr = PersistenceServerHelper.manager.query(queryspec);
		while (qr.hasMoreElements())
		{
			PersistenceServerHelper.manager.remove((Persistable) qr.nextElement());
		}
	}
}
