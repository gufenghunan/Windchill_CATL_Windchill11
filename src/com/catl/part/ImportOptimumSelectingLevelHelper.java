package com.catl.part;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.catl.common.constant.Ranking;
import com.ptc.core.lwc.server.PersistableAdapter;
import com.ptc.core.meta.common.UpdateOperationIdentifier;

import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.log4j.LogR;
import wt.method.RemoteAccess;
import wt.org.WTPrincipal;
import wt.org.WTUser;
import wt.part.WTPartMaster;
import wt.pom.Transaction;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.query.WhereExpression;
import wt.session.SessionContext;
import wt.session.SessionHelper;
import wt.util.WTException;

public class ImportOptimumSelectingLevelHelper implements RemoteAccess
{
	private static String className = ImportOptimumSelectingLevelHelper.class.getName();
	private static Logger logger = LogR.getLogger(className);

	public ImportOptimumSelectingLevelHelper()
	{
	}

	public String getErrMsg(File file)
	{
		String errorMsg = "";
		Transaction transaction = new Transaction();
		WTUser previous = null;
		try
		{
			WTPrincipal wtadministrator = SessionHelper.manager.getAdministrator();
			previous = (WTUser) SessionHelper.manager.getPrincipal();
			SessionContext.setEffectivePrincipal(wtadministrator);

			// 从excel中取得数据
			ArrayList<LevelBean> levelList = getDataFromExcel(file);
			transaction.start();
			doImportLevle(levelList);
			transaction.commit();
		} catch (Exception e)
		{
			transaction.rollback();
			errorMsg = e.getMessage();
			logger.error(e.getLocalizedMessage(), e);
			return errorMsg;
		} finally
		{
			if (previous != null)
				SessionContext.setEffectivePrincipal(previous);
		}
		return "successful";
	}

	private void doImportLevle(ArrayList<LevelBean> levelList) throws WTException
	{
		for (LevelBean bean : levelList)
		{
			WTPartMaster master = bean.getMaster();
			if (master == null)
				continue;
			String level = bean.getLevel();
			PersistableAdapter genericObj = new PersistableAdapter(master, null, null, new UpdateOperationIdentifier());
			genericObj.load("ranking");
			genericObj.set("ranking", level);
			Persistable updatedObject = genericObj.apply();
			master = (WTPartMaster) PersistenceHelper.manager.save(updatedObject);
		}
	}

	private WTPartMaster getMaster(String number) throws WTException
	{
		QuerySpec querySpec = new QuerySpec(WTPartMaster.class);
		WhereExpression where = new SearchCondition(WTPartMaster.class, WTPartMaster.NUMBER, SearchCondition.EQUAL, number.toUpperCase());
		querySpec.appendWhere(where);
		QueryResult qr = PersistenceServerHelper.manager.query(querySpec);
		if (qr.hasMoreElements())
		{
			WTPartMaster master = (WTPartMaster) qr.nextElement();
			return master;
		}
		return null;
	}

	private ArrayList<LevelBean> getDataFromExcel(File file) throws Exception
	{
		String errMsg = "";
		ArrayList<LevelBean> result = new ArrayList<LevelBean>();
		Workbook wb = WorkbookFactory.create(new FileInputStream(file));
		Sheet sheet = wb.getSheetAt(0);

		for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++)
		{
			Row row = sheet.getRow(i);
			if (row == null)
			{
				break;
			}
			Cell cell = row.getCell(0);
			String number = getCellValue(cell);
			Cell cell1 = row.getCell(1);
			String level = getCellValue(cell1);

			if (number.isEmpty())
				continue;

			WTPartMaster master = getMaster(number);

			if (master == null)
			{
				errMsg = errMsg + "第" + (i + 1) + "行，" + number + " 在系统中不存在<br>";
			}
			if (!(Ranking.HIGHT.equals(level) || Ranking.MIDDLE.equals(level) || Ranking.LOWER.equals(level) || Ranking.PROHIBIT_PURCHASE.equals(level) || Ranking.DISABLED.equals(level)))
			{
				errMsg = errMsg + "第" + (i + 1) + "行，等级填写不正确，等级必须是“"+Ranking.ALL+"” <br>";
			}

			LevelBean bean = new LevelBean(number, level);
			bean.setMaster(master);

			result.add(bean);
		}
		if ("".equals(errMsg))
		{
			return result;
		} else
		{
			throw new Exception(errMsg);
		}
	}

	private static String getCellValue(Cell cell) throws WTException
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
				// get numeric
				java.text.DecimalFormat formatter = new java.text.DecimalFormat("########.###");
				cellValue = formatter.format(cell.getNumericCellValue());
				if (cellValue != null)
				{
					cellValue = cellValue.trim();
				}
				break;
			case Cell.CELL_TYPE_STRING:
				// get string
				cellValue = cell.getStringCellValue().trim();
				break;
			default:
				cellValue = "";
			}
		}
		return cellValue;
	}

	class LevelBean
	{
		String number, level;
		WTPartMaster master;

		public LevelBean(String number, String level)
		{
			this.number = number;
			this.level = level;
		}

		public WTPartMaster getMaster()
		{
			return master;
		}

		public void setMaster(WTPartMaster master)
		{
			this.master = master;
		}

		public String getNumber()
		{
			return number;
		}

		public void setNumber(String number)
		{
			this.number = number;
		}

		public String getLevel()
		{
			return level;
		}

		public void setLevel(String level)
		{
			this.level = level;
		}

		@Override
		public String toString()
		{
			return number + ":" + level;
		}

		@Override
		public boolean equals(Object obj)
		{
			if (obj instanceof LevelBean)
			{
				LevelBean bean = (LevelBean) obj;
				if (this.number.equals(bean.getNumber()) && this.level.equals(bean.getLevel()))
					return true;
			}
			return false;
		}
	}
}
