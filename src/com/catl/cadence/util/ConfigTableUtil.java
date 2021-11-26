package com.catl.cadence.util;

import java.io.IOException;
import java.util.Enumeration;
import java.util.List;

import wt.util.WTException;

import com.catl.cadence.conf.CadenceConfConstant;
import com.catl.cadence.conf.InitSystemConfigContant;
import com.catl.cadence.conf.InitSystemConfigContant.InitSystemAttrConfig;
import com.infoengine.SAK.Task;
import com.infoengine.object.factory.Group;
import com.infoengine.util.IEException;

public class ConfigTableUtil {
	
	static String VARCHAR2 = "VARCHAR2(255)";
	static String TYPE_VARCHAR2 = "VARCHAR2";
	static String TYPE_VARCHAR = "VARCHAR";
	
	/**
	 * 添加表列
	 * @param columnname
	 * @throws WTException
	 */
	public static void addTableColumn(List columnnames,String tablename,String viewname) throws WTException{
			String[] sqls=getCreateSql(columnnames);
			Task task = new Task(CadenceConfConstant.TASK_ADD_TABLECOLUMN);
			task.addParam("w4cinstance", CadenceConfConstant.TASK_W4C_JDBCADAPTER_SERVICES_NAME);
			task.addParam("tablename", tablename);
			task.addParam("viewname", viewname);
			task.addParam("columnname", sqls[0]);
			try {
				task.invoke();
			} catch (IEException e) {
				// TODO Auto-generated catch block
				throw new WTException(e);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				throw new WTException(e);
			}
			String message="";
			for(int i=0;i<columnnames.size();i++){
				InitSystemAttrConfig attr=(InitSystemAttrConfig) columnnames.get(i);
				message+="\n【"+attr.getColumnName()+"】";
			}
			System.out.println("成功新增列："+message);
			//createSynonym();
	}
	
	/**
	 * 根据添加的字段选择新增的表
	 * @throws WTException
	 */
	public static void selectTable() throws WTException{
		List columnnames = selectColumn();
		List columnnames2= selectColumn2();
		if(columnnames.size()!=0){
			for(int i=0;i<2;i++){
				if(i==0){
					String tablename=CadenceConfConstant.CATL_T_CADENCE_PARTS;
					String viewname=CadenceConfConstant.AUTOMOTIVE_PARTS;
					addTableColumn(columnnames,tablename,viewname);
				}else{
					String tablename=CadenceConfConstant.CATL_T_CADENCE_PARTS2;
					String viewname=CadenceConfConstant.INDUSTRY_PARTS;
					addTableColumn(columnnames,tablename,viewname);
				}
			}
		}
		if(columnnames2.size()!=0){
			String tablename=CadenceConfConstant.CATL_T_CADENCE_PCB_OTHER;
			String viewname=CadenceConfConstant.PCB_OTHER_PARTS;
			addTableColumn(columnnames2,tablename,viewname);
		}
		if(columnnames.size()==0&&columnnames2.size()==0){
			throw new WTException("请先在【initSystemConfig.properties】中填写新增列的相关信息！");
		}
	}
	
	/**
	 * 获取新增字段
	 * @return List新增字段
	 * @throws WTException
	 */
	public static List selectColumn() throws WTException{
		Task task = new Task(CadenceConfConstant.TASK_GET_NEWCOLUMN);
		task.addParam("w4cinstance", CadenceConfConstant.TASK_W4C_JDBCADAPTER_SERVICES_NAME);
		task.addParam("tablename", CadenceConfConstant.CATL_T_CADENCE_PARTS);
		List conf=InitSystemConfigContant.init().getInitSystemAttrConfig();
		List conf2=InitSystemConfigContant.init().getInitSystemCadAttrConfig();
		conf.addAll(conf2);

		try {
			task.invoke();
		} catch (IEException e) {
			// TODO Auto-generated catch block
			throw new WTException(e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new WTException(e);
		}
		Enumeration groups = task.getGroupNames();
		while(groups.hasMoreElements()){
			String groupName = (String)groups.nextElement();
			if("QueryColumn".equals(groupName)){
				Group g = task.getGroup(groupName);
				int count=g.getElementCount();
				for(int i=0;i<count;i++){
					//InitSystemAttrConfig con=(InitSystemAttrConfig) conf.get(i);
					String column=(String) g.getAttributeValue(i, "COLUMN_NAME");
					for(int j=0;j<conf.size();j++){
						InitSystemAttrConfig con=(InitSystemAttrConfig) conf.get(j);
						if(con.getColumnName().toUpperCase().equals(column)){
							conf.remove(j);
						}
					}
				}
			}
		}
		return conf;
	}
	
	/**
	 * 获取新增字段（PCB+其他）
	 * @return List新增字段
	 * @throws WTException
	 */
	public static List selectColumn2() throws WTException{
		Task task = new Task(CadenceConfConstant.TASK_GET_NEWCOLUMN);
		task.addParam("w4cinstance", CadenceConfConstant.TASK_W4C_JDBCADAPTER_SERVICES_NAME);
		task.addParam("tablename", CadenceConfConstant.CATL_T_CADENCE_PCB_OTHER);
		List conf=InitSystemConfigContant.init().getInitPCBOtherConfig();
		try {
			task.invoke();
		} catch (IEException e) {
			// TODO Auto-generated catch block
			throw new WTException(e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new WTException(e);
		}
		Enumeration groups = task.getGroupNames();
		while(groups.hasMoreElements()){
			String groupName = (String)groups.nextElement();
			if("QueryColumn".equals(groupName)){
				Group g = task.getGroup(groupName);
				int count=g.getElementCount();
				for(int i=0;i<count;i++){
					//InitSystemAttrConfig con=(InitSystemAttrConfig) conf.get(i);
					String column=(String) g.getAttributeValue(i, "COLUMN_NAME");
					for(int j=0;j<conf.size();j++){
						InitSystemAttrConfig con=(InitSystemAttrConfig) conf.get(j);
						if(con.getColumnName().toUpperCase().equals(column)){
							conf.remove(j);
						}
					}
				}
			}
		}
		return conf;
	}
	
	/**
	 * 创建cadence所需表和视图
	 * @throws IOException 
	 * @throws IEException 
	 */
	public static void createAttrTable() throws WTException{
		Task task = new Task(CadenceConfConstant.TASK_CREATE_ATTRTABLE);
		task.addParam("w4cinstance", CadenceConfConstant.TASK_W4C_JDBCADAPTER_SERVICES_NAME);
		task.addParam("tablename1", CadenceConfConstant.CATL_T_CADENCE_PARTS);
		task.addParam("tablename2", CadenceConfConstant.CATL_T_CADENCE_PARTS2);
		task.addParam("tablename3", CadenceConfConstant.CATL_T_CADENCE_PCB_OTHER);
		task.addParam("viewname1", CadenceConfConstant.AUTOMOTIVE_PARTS);
		task.addParam("viewname2", CadenceConfConstant.INDUSTRY_PARTS);
		task.addParam("viewname3", CadenceConfConstant.PCB_OTHER_PARTS);
		try {
			task.invoke();
		} catch (IEException e) {
			// TODO Auto-generated catch block
			throw new WTException(e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new WTException(e);
		}
		Group group = task.getGroup("groupout");
		group.printTree();
		createSynonym();
	}
	
	/**
	 * 获得创建属性栏位的SQL语句，创建View中的显示栏位及查询结果栏位的SQL语句
	 * String[0] 创建属性栏位的SQL语句
	 * String[1] View中的显示属性栏位SQL语句
	 * String[2] View中的查询结果栏位SQL语句
	 * @param attrConfList
	 * @return
	 */
	private static String[] getCreateSql(List attrConfList){
		StringBuffer createColSql = null;
		StringBuffer showNames = null;
		StringBuffer selectNames = null;
		for(int i = 0; i < attrConfList.size(); i++){
			InitSystemAttrConfig attrConf = (InitSystemAttrConfig)attrConfList.get(i);
			if(createColSql == null){
				createColSql = new StringBuffer();
				showNames = new StringBuffer();
				selectNames = new StringBuffer();
			}else{
				createColSql.append(",");
				showNames.append(",");
				selectNames.append(",");
			}
			createColSql.append(attrConf.getColumnName()).append(" ").append(VARCHAR2);
			showNames.append(attrConf.getViewColumnName());
			selectNames.append(attrConf.getColumnName());
		}
		String[] result = new String[3];
		result[0] = createColSql.toString();
		result[1] = showNames.toString();
		result[2] = selectNames.toString();
		return result;
	}
	
	/**
	 * 授予cadence用户查询权限并创建同义词
	 * @throws WTException
	 */
	public static void createSynonym() throws WTException{
		Task task=new Task(CadenceConfConstant.TASK_CREATE_GRANT);
		task.addParam("w4cinstance", CadenceConfConstant.TASK_W4C_JDBCADAPTER_SERVICES_NAME);
		String viewname1=CadenceConfConstant.AUTOMOTIVE_PARTS;
		String viewname2=CadenceConfConstant.INDUSTRY_PARTS;
		String viewname3=CadenceConfConstant.PCB_OTHER_PARTS;
		String username =CadenceConfConstant.TASK_DATABASE_USERNAME;
		task.addParam("viewname1", viewname1);
		task.addParam("viewname2", viewname2);
		task.addParam("viewname3", viewname3);
		task.addParam("usertable1", username+"."+viewname1);
		task.addParam("usertable2", username+"."+viewname2);
		task.addParam("usertable3", username+"."+viewname3);
		try {
			task.invoke();
		} catch (IEException e) {
			// TODO Auto-generated catch block
			throw new WTException(e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new WTException(e);
		}
		Group group = task.getGroup("groupout");
		group.printTree();
	}
}
