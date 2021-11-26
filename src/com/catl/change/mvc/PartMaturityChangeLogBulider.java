package com.catl.change.mvc;

import org.apache.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.*;

import wt.fc.Persistable;
import wt.log4j.LogR;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.util.WTException;

import com.catl.change.ChangeUtil;
import com.catl.part.maturity.PartMaturityChangeLog;
import com.catl.part.maturity.PartMaturityChangeLogHelper;
import com.ptc.mvc.components.AbstractComponentBuilder;
import com.ptc.mvc.components.ColumnConfig;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentConfigFactory;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.TableConfig;


@ComponentBuilder("com.catl.change.mvc.PartMaturityChangeLogBulider")
public class PartMaturityChangeLogBulider extends AbstractComponentBuilder {
	
	private static final Logger log = LogR.getLogger(PartMaturityChangeLogBulider.class.getName());
	
	private final static String table_id = "com.catl.change.mvc.PartMaturityChangeLogBulider";
	
	public Object buildComponentData(ComponentConfig config, ComponentParams params) throws Exception {
		
		String oid = (String)params.getParameter("oid");
		WTPart part = (WTPart)ChangeUtil.getPersistable(oid);
		
		//[查询当前部件对应的升级记录]
		return PartMaturityChangeLogHelper.getPartMaturityChangeLogByPart((WTPartMaster)part.getMaster());
	}
	
	public ComponentConfig buildComponentConfig(ComponentParams params) throws WTException{

		ComponentConfigFactory factory = getComponentConfigFactory();
		TableConfig table = factory.newTableConfig();
		table.setLabel("物料成熟度变更历史记录");
		table.setId(table_id);
		
		ColumnConfig upgradeTime = factory.newColumnConfig("thePersistInfo.createStamp", true);
		upgradeTime.setLabel("升级时的时间");
		table.addComponent(upgradeTime);
		
		ColumnConfig upgradeVersion= factory.newColumnConfig("version", true);
		upgradeVersion.setLabel("升级时的版本号");
		table.addComponent(upgradeVersion);
		
		ColumnConfig operator = factory.newColumnConfig("operator", true);
		operator.setLabel("操作者");
		table.addComponent(operator);
		
		ColumnConfig upgradeBeforeMaturity = factory.newColumnConfig("oldMaturity", true);
		upgradeBeforeMaturity.setLabel("升级前成熟度");
		table.addComponent(upgradeBeforeMaturity);
		
		ColumnConfig upgradeAfterMaturity = factory.newColumnConfig("newMaturity", true);
		upgradeAfterMaturity.setLabel("升级后成熟度");
		table.addComponent(upgradeAfterMaturity);
		
		return table;
	}
}
