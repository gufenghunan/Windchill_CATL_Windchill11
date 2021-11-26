package com.catl.loadData;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.catl.loadData.util.ExcelWriter;

import wt.fc.IdentityHelper;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.part.WTPartMasterIdentity;
import wt.pom.PersistenceException;
import wt.pom.Transaction;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.util.WTPropertyVetoException;
import wt.vc.VersionControlHelper;

public class UpdatePartTemp50 implements RemoteAccess {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		RemoteMethodServer ms = RemoteMethodServer.getDefault();
		ms.setUserName("dms");
		ms.setPassword("dms");
		
		try {
			SessionHelper.manager.setAuthenticatedPrincipal("dms");
			Class[] types = {String.class};
	        Object[] values={args[0]};
			RemoteMethodServer.getDefault()
			.invoke("updateObject",
					UpdatePartTemp50.class.getName(), null, types,
					values);
		} catch (WTException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
	public static void updateObject(String command) throws WTException, WTPropertyVetoException, IOException{
		boolean run = false;
		if(command.equals("-r")){
			run = true;
		}
		String rootPath=WTProperties.getLocalProperties().getProperty("wt.home")+"/loadFiles/com/catl/dms";		
		List<String[]> logs=new ArrayList<String[]>();		
		
		Transaction trx = null;
		try{
			trx = new Transaction();
			trx.start();
			String[] temp=new String[]{"500601-00209","770151-00005","770152-00034","770250-00041","780151-00049"};
			for(int i=0;i<temp.length;i++){
				WTPart part=getPart(temp[i]);
				//updatePartName((WTPartMaster)part.getMaster(),nameMap.get(temp[i]));
				IBAUtility	iba_part = new IBAUtility(part);
				if(temp[i].equals("500601-00209")){					
					String Material=iba_part.getIBAValue("Material")==null?"":iba_part.getIBAValue("Material");
					String Length=iba_part.getIBAValue("Length")==null?"":iba_part.getIBAValue("Length");
					String Width=iba_part.getIBAValue("Width")==null?"":iba_part.getIBAValue("Width");
					String Height=iba_part.getIBAValue("Height")==null?"":iba_part.getIBAValue("Height");					
					String Special_Explanation=iba_part.getIBAValue("Special_Explanation")==null?"":iba_part.getIBAValue("Special_Explanation");
					String specification="材质:"+Material+"_长(mm):"+Length+"_宽(mm):"+Width+"_高(mm):"+Height+"_特殊说明:"+Special_Explanation;
					if(run){
						iba_part.setIBAValue("specification", specification);
						iba_part.updateAttributeContainer(part);
						iba_part.updateIBAHolder(part);
					}
					logs.add(new String[]{part.getNumber(),part.getName(),specification});
				}else if(temp[i].equals("770151-00005") || temp[i].equals("770152-00034") || temp[i].equals("770250-00041")){
					String Product_Energy=iba_part.getIBAValue("Product_Energy")==null?"":iba_part.getIBAValue("Product_Energy");
					String Voltage=iba_part.getIBAValue("Voltage")==null?"":iba_part.getIBAValue("Voltage");
					String Length=iba_part.getIBAValue("Length")==null?"":iba_part.getIBAValue("Length");
					String Width=iba_part.getIBAValue("Width")==null?"":iba_part.getIBAValue("Width");
					String Height=iba_part.getIBAValue("Height")==null?"":iba_part.getIBAValue("Height");					
					String Process=iba_part.getIBAValue("Process")==null?"":iba_part.getIBAValue("Process");
					String Module_Connection_Mode=iba_part.getIBAValue("Module_Connection_Mode")==null?"":iba_part.getIBAValue("Module_Connection_Mode");
					String Pack_Connection_Mode=iba_part.getIBAValue("Pack_Connection_Mode")==null?"":iba_part.getIBAValue("Pack_Connection_Mode");
					String Special_Explanation=iba_part.getIBAValue("Special_Explanation")==null?"":iba_part.getIBAValue("Special_Explanation");
					String specification="产品能量:"+Product_Energy+"_电压:"+Voltage+"_长(mm):"+Length+"_宽(mm):"+Width+"_高(mm):"+Height+"_工艺:"+Process+"_模组连接方式:"+Module_Connection_Mode+"_电箱连接方式:"+Pack_Connection_Mode+"_特殊说明:"+Special_Explanation;
					if(run){
						iba_part.setIBAValue("specification", specification);
						iba_part.updateAttributeContainer(part);
						iba_part.updateIBAHolder(part);
					}
					logs.add(new String[]{part.getNumber(),part.getName(),specification});
				}else if(temp[i].equals("780151-00049")){
					String Length=iba_part.getIBAValue("Length")==null?"":iba_part.getIBAValue("Length");
					String Width=iba_part.getIBAValue("Width")==null?"":iba_part.getIBAValue("Width");
					String Height=iba_part.getIBAValue("Height")==null?"":iba_part.getIBAValue("Height");	
					String Installation_Method=iba_part.getIBAValue("Installation_Method")==null?"":iba_part.getIBAValue("Installation_Method");
					String Branch_Quantity=iba_part.getIBAValue("Branch_Quantity")==null?"":iba_part.getIBAValue("Branch_Quantity");
					String International_Charging_Port=iba_part.getIBAValue("International_Charging_Port")==null?"":iba_part.getIBAValue("International_Charging_Port");
					String Special_Explanation=iba_part.getIBAValue("Special_Explanation")==null?"":iba_part.getIBAValue("Special_Explanation");
					String specification="长(mm):"+Length+"_宽(mm):"+Width+"_高(mm):"+Height+"_安装方式:"+Installation_Method+"_支路数:"+Branch_Quantity+"_是否带国际充电口:"+International_Charging_Port+"_特殊说明:"+Special_Explanation;
					if(run){
						iba_part.setIBAValue("specification", specification);
						iba_part.updateAttributeContainer(part);
						iba_part.updateIBAHolder(part);
					}
					logs.add(new String[]{part.getNumber(),part.getName(),specification});
				}
			}
			
			ExcelWriter writer = new ExcelWriter();
			boolean flag = writer.exportExcelList(rootPath+"/导出更新的规格.xlsx","导出更新的规格", new String[]{"编码","名称","规格"}, logs);
			System.out.println("导出更新的规格.xlsx flag="+flag);
			logs=new ArrayList<String[]>();
			
			trx.commit();
			trx = null;
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			if (trx != null){
				System.out.println("---------updateObject rollback--------");
				trx.rollback();
			}
		}
	}
	
	public static void updatePartName(WTPartMaster partMaster, String name)
			throws WTException, WTPropertyVetoException {		
		
		WTPartMasterIdentity partIdentity = (WTPartMasterIdentity) partMaster
					.getIdentificationObject();
		partIdentity.setName(name);
		partMaster = (WTPartMaster) IdentityHelper.service
				.changeIdentity(partMaster, partIdentity);
	}
	
	static WTPart getPart(String number) throws WTException {
		WTPart wtpart = null;
		QuerySpec qs= new QuerySpec(WTPart.class);
		SearchCondition sc = new SearchCondition(WTPart.class,
				WTPart.NUMBER, SearchCondition.EQUAL, number.trim());
		qs.appendWhere(sc);
		QueryResult qr = PersistenceHelper.manager.find(qs);
		if (qr.size() > 0)
			wtpart = (WTPart) qr.nextElement();
		
		if(wtpart!=null){
			wtpart=getLatestPart((WTPartMaster) wtpart.getMaster());
		}
		return wtpart;
	}
	
	static WTPart getLatestPart(WTPartMaster partMaster) throws PersistenceException, WTException{
		WTPart part = null;
		if (partMaster != null) {
			QueryResult qr= VersionControlHelper.service
						.allVersionsOf(partMaster);
			if (qr != null && qr.hasMoreElements()) {
				part = (WTPart) qr.nextElement();
			}
		}
		return part;
	}

}
