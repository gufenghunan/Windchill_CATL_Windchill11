package com.catl.common.toolbox.data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;

import wt.fc.ObjectReference;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.collections.WTCollection;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.part.WTPartMaster;
import wt.part.WTPartSubstituteLink;
import wt.part.WTPartUsageLink;
import wt.pom.Transaction;
import wt.query.QuerySpec;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.vc.wip.WorkInProgressHelper;

import com.catl.common.util.GenericUtil;
import com.catl.common.util.PartUtil;
import com.catl.loadData.util.ExcelReader;
import com.catl.loadData.util.ExcelWriter;
import com.catl.part.PartConstant;
import com.ptc.core.lwc.server.PersistableAdapter;
import com.ptc.core.meta.common.UpdateOperationIdentifier;

public class UpdateWTPartMaturity implements RemoteAccess {

	private static String homePath = "";
	
	private static boolean checkflg = true;
	
	static {
		try {
			WTProperties wtproperties = WTProperties.getLocalProperties();
			homePath = wtproperties.getProperty("wt.home");
		} catch (Throwable t) {
			throw new ExceptionInInitializerError(t);
		}
	}
	
	public static void main(String[] args) throws RemoteException, InvocationTargetException {
		
		if (args == null || args.length < 3){
        	
        	System.out.printf("请输入正确的文件路径、用户名、密码！");
        } else {
			RemoteMethodServer ms = RemoteMethodServer.getDefault();
			ms.setUserName(args[1]);
			ms.setPassword(args[2]);
			
			String filePath = args[0];
			try {
				SessionHelper.manager.setAuthenticatedPrincipal(args[1]);
				Class[] types = {String.class};
		        Object[] values={filePath};
				RemoteMethodServer.getDefault()
				.invoke("doLoad", UpdateWTPartMaturity.class.getName(), null, types, values);
			} catch (WTException e) {
				e.printStackTrace();
			}
        }
	}
	
	public static void doLoad(String filePath) throws WTException, RemoteException{
		
		boolean enforced=SessionServerHelper.manager.setAccessEnforced(false);
		
		checkflg = true;
		String logPath = homePath + "/logs/";
		BufferedWriter writer = null;
		
		FileWriter file = null;
		try {
			
			Format format = new SimpleDateFormat("yyyyMMddHHmmss"); 
			String nowTime = format.format(new Date()); 

			file = new FileWriter(logPath + "update_ maturity_" + nowTime + ".log");
			writer = new BufferedWriter(file);
			writer.write("");
			
			File upFile = new File(filePath);
			if (upFile != null && upFile.exists()){
				
				ExcelReader erp = new ExcelReader(upFile);
				erp.open();
		        erp.setSheetNum(0);
		        int count = erp.getRowCount();
				
		        List<String> maturityFormatList = Arrays.asList("1", "3", "6");
		        Map<String, String> needupdateMap = new HashMap<String, String>();
		        Map<String, WTPart> pnWTPartMap = new HashMap<String, WTPart>();
		        
		        for(int i=1; i<=count; i++){
		            String rows[] = erp.readExcelLine(i);
		            String partNumber = rows[0];
		            String maturity = rows[1];
		            
		            if (isEmpty(maturity)){
		            	
		            	writer.write(partNumber + "物料的成熟度没有填写！");
		            	writer.write("\n");
		            	checkflg = false;
		            	continue;
		            }
		            maturity = maturity.trim();
		            
		            WTPart part = PartUtil.getLastestWTPartByNumber(partNumber);
		            
		            if (part == null){
		            	
		            	writer.write(partNumber + "物料不存在！");
		            	writer.write("\n");
		            	checkflg = false;
		            	continue;
		            }
		            
		            if (!maturityFormatList.contains(maturity)){
		            	
		            	writer.write(partNumber + "的成熟度输入错误：" + maturity);
		            	writer.write("\n");
		            	checkflg = false;
		            	continue;
		            }
		            
		            needupdateMap.put(partNumber, maturity);
		            pnWTPartMap.put(partNumber, part);
		        }
		        
		        List<WTPart> partsNeedToUpgradeMatuirty = new ArrayList<WTPart>();
		        for (String partNum : needupdateMap.keySet()) {
		        	
		            String maturity = needupdateMap.get(partNum);
		            WTPart part  = pnWTPartMap.get(partNum);
		            if (!"1".equals(maturity)){
		            	
		            	checPartMaturity(part, needupdateMap, writer, partsNeedToUpgradeMatuirty);
		            }
		        }
		        
		        // 如果验证都通过了，则执行更新成熟度操作
		        if (checkflg){
		        	
		        	refreshWTPartMaturity(pnWTPartMap, needupdateMap, writer);
		        	
		        } else {
		        	
		        	List<WTPart> extraPartsNeedToUpradeMaturity = new ArrayList<WTPart>();
		        	List<String[]> logs = new ArrayList<String[]>();
		        	
		        	for (WTPart part : partsNeedToUpgradeMatuirty){
		        		String bigVer = part.getVersionIdentifier().getValue();//大版本
						String smallVer = part.getIterationIdentifier().getValue();//小版本
		        		logs.add(new String[]{part.getNumber(), part.getName(), bigVer+"."+smallVer, part.getModifier().getFullName(), part.getFolderPath()});
		        		checkNeedToUpgradeMatuirty(part, needupdateMap, extraPartsNeedToUpradeMaturity, logs);
		        	}
		        	
		        	ExcelWriter writerExcel = new ExcelWriter();
					boolean flag = writerExcel.exportExcelList(logPath+"update_ partsNeedToUpgradeMatuirty_" + nowTime + ".xlsx","update_ partsNeedToUpgradeMatuirty_" + nowTime, new String[]{"部件编码", "部件名称", "版本", "部件修改者", "所在位置"}, logs);
					System.out.println("update_ partsNeedToUpgradeMatuirty_" + nowTime +".xlsx flag="+flag);
					logs=new ArrayList<String[]>();
		        }
			} else {
				
				writer.write("所输入的文件不存在！");
            	writer.write("\n");
			}

            writer.flush();
            writer.close();
            
            System.out.println("UpdateWTPartMaturity 操作成功！");
		} catch (Exception e) {
			try {
				writer.write("报错："+e.getMessage());
				writer.write("\n");
				writer.flush();
	            writer.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
		}finally{
			SessionServerHelper.manager.setAccessEnforced(enforced);
		}
	}
	
	static void checPartMaturity(WTPart part, Map<String,  String> needupdateMap, BufferedWriter writer, List<WTPart> partsNeedToUpgradeMatuirty) throws WTException {
		
		String parentPartNumber = part.getNumber();
		String parentPartName = part.getName();
//		String parentMaturity = needupdateMap.get(parentPartNumber);
		try {
			
			boolean checkoutFlag = WorkInProgressHelper.isCheckedOut(part);
			if (checkoutFlag){
				
				writer.write("部件" + parentPartNumber + "被检出！");
				writer.write("\n");
				checkflg = false;
			}
			
			QueryResult qr2 = WTPartHelper.service.getUsesWTPartMasters(part);
		
			while(qr2.hasMoreElements()){
				
				WTPartUsageLink link = (WTPartUsageLink)qr2.nextElement();
				
				// 判断物料直接下层物料成熟度 		开始
				WTPartMaster master = link.getUses();
				WTPart childPart = PartUtil.getLastestWTPartByNumber(master.getNumber());
				
				String childPN = childPart.getNumber();
				String childName = childPart.getName();
				
				String childMaturity = "";
				if (needupdateMap.containsKey(childPN)){
					childMaturity = needupdateMap.get(childPN);
				} else {
					childMaturity = (String)GenericUtil.getObjectAttributeValue(part.getMaster(), PartConstant.IBA_CATL_Maturity);
				}
				
				if (!"3".equals(childMaturity) && !"6".equals(childMaturity)){	// 子部件成熟度不为3或6，且不在需要更新的excel中，则违规
					
					partsNeedToUpgradeMatuirty.add(childPart);
					writer.write("部件：" + parentPartNumber + "的下层物料：" + childPN + "的成熟度不为3或者6！");
					writer.write("\n");
					checkflg = false;
				}
				// 判断物料直接下层物料成熟度 		结束
				
				// 判断物料直接下层物料的替代料成熟度		开始
				WTCollection collection = WTPartHelper.service.getSubstituteLinks(link);
				if (!collection.isEmpty()) {
					Iterator itr = collection.iterator();
					while (itr.hasNext()) {
						ObjectReference objReference = (ObjectReference) itr.next();
						WTPartSubstituteLink subLink = (WTPartSubstituteLink) objReference.getObject();
						WTPartMaster subpartMaster = (WTPartMaster) subLink.getSubstitutes();
						String subPartName = subpartMaster.getName();
						String subPartNumber = subpartMaster.getNumber();
						String subMaturity = "";
						if (needupdateMap.containsKey(subPartNumber)){
							subMaturity = needupdateMap.get(subPartNumber);
						} else {
							subMaturity = (String)GenericUtil.getObjectAttributeValue(subpartMaster, PartConstant.IBA_CATL_Maturity);
						}
						
						if (!"3".equals(subMaturity) && !"6".equals(subMaturity)){	// 替代件的成熟度不为3或6，且不在需要更新的excel中，则违规
							
							WTPart subPart = PartUtil.getLastestWTPartByNumber(subPartNumber);
							partsNeedToUpgradeMatuirty.add(subPart);
							writer.write("部件" + parentPartNumber + "的下层物料" + childPN + "的替代料" + subPartNumber + "的成熟度不为3或者6！");
							writer.write("\n");
							checkflg = false;
						}
					}
				}
				// 判断物料直接下层物料的替代料成熟度		结束
			}
		} catch (Exception e) {
			try {
				checkflg = false;
				writer.write(e.getMessage());
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	
	static void refreshWTPartMaturity(Map<String, WTPart> pnWTPartMap, Map<String, String> needupdateMap, BufferedWriter writer){
        
		Transaction ts=null;
        try {
        	ts = new Transaction();
            ts.start();
        	for (String partNum : pnWTPartMap.keySet()) {
	        	
	            WTPart part  = pnWTPartMap.get(partNum);
	            String maturity = needupdateMap.get(partNum);
            	WTPartMaster srcmaster = (WTPartMaster)part.getMaster();
            	String oldMaturity = (String)GenericUtil.getObjectAttributeValue(srcmaster, PartConstant.IBA_CATL_Maturity);
            	if (oldMaturity == null || !oldMaturity.equals(maturity)){
            		
            		PersistableAdapter genericObj = new PersistableAdapter(srcmaster, null, null, new UpdateOperationIdentifier());
        			genericObj.load(PartConstant.IBA_CATL_Maturity);
        			genericObj.set(PartConstant.IBA_CATL_Maturity, maturity);
        			Persistable updatedObject = genericObj.apply();
        			srcmaster = (WTPartMaster) PersistenceHelper.manager.save(updatedObject);
            	}
            }
        	writer.write("UpdateWTPartMaturity 操作成功！");
        	ts.commit();
        }catch (Exception e) {
            e.printStackTrace();
            if(ts != null)
                ts.rollback();
        }

    }

	public static boolean isEmpty(String str){
        if(str == null || str.trim().equals("") || str.equals("null"))
            return true;
        return false;
    }
	
	static void checkSubPartLink(Map<String, String> needupdateMap, BufferedWriter writer) throws WTException {
		
		Vector<WTPartSubstituteLink> wtpartsubstitutelinks = getWTCollection();
        
		List<String> pnlist = new ArrayList<String>();
        for (WTPartSubstituteLink link : wtpartsubstitutelinks){
        	
        	WTPartUsageLink usagelink = (WTPartUsageLink)link.getRoleAObject();
        	
        	//主物料上层
        	WTPart part = usagelink.getUsedBy();
        	WTPartMaster subMaster = (WTPartMaster)link.getRoleBObject();
//        	String parentName = part.getName();
        	
        	//主物料
        	WTPartMaster usePartMaster = (WTPartMaster)usagelink.getUses();
//        	String usePartName = usePartMaster.getName();
        	String usePartNum = usePartMaster.getNumber();
        	
        	String usePartMaturity = "";
        	if (needupdateMap.containsKey(usePartNum)){
        		usePartMaturity = needupdateMap.get(usePartNum);
			} else {
				usePartMaturity = (String)GenericUtil.getObjectAttributeValue(subMaster, PartConstant.IBA_CATL_Maturity);
			}
        	if (StringUtils.isEmpty(usePartMaturity)){
        		usePartMaturity = "1";
        	}
        	
        	if (!pnlist.contains(subMaster.getNumber()) && PartUtil.isLastedWTPart(part) && !WorkInProgressHelper.isWorkingCopy(part)){
        		
        		pnlist.add(subMaster.getNumber());
//        		String subMasterName = subMaster.getName();
        		String subMasterNum = subMaster.getNumber();
        		String subMaturity = "";
        		
        		if (needupdateMap.containsKey(subMasterNum)){
					subMaturity = needupdateMap.get(subMasterNum);
				} else {
					subMaturity = (String)GenericUtil.getObjectAttributeValue(subMaster, PartConstant.IBA_CATL_Maturity);
				}
				
				if (subMaturity == null || "".equals(subMaturity)){
					subMaturity = "1";
				}
				
				//替代件的成熟度不能小于主料的成熟度
				if (Integer.valueOf(subMaturity) < Integer.valueOf(usePartMaturity)){
					
					try {
						writer.write("部件：" + subMasterNum + "是局部替换件，但没有设置成熟度为3或者6！");
						writer.write("\n");
//						checkflg = false;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					continue;
				}
        	}
        }
	}
	
	static Vector<WTPartSubstituteLink> getWTCollection() throws WTException {
    	
		Vector<WTPartSubstituteLink> wtcollections = new Vector<WTPartSubstituteLink>();
		QuerySpec qs= new QuerySpec(WTPartSubstituteLink.class);		
		QueryResult qr = PersistenceHelper.manager.find(qs);
		while(qr.hasMoreElements()){			
			WTPartSubstituteLink wtcollection = (WTPartSubstituteLink) qr.nextElement();
			wtcollections.add(wtcollection);
		}
		return wtcollections;
	}
	
	static void checkNeedToUpgradeMatuirty(WTPart part, Map<String,  String> needupdateMap, List<WTPart> extraPartsNeedToUpradeMaturity, List<String[]> logs) throws WTException {
		
		QueryResult qr2 = WTPartHelper.service.getUsesWTPartMasters(part);
			
		while(qr2.hasMoreElements()){
			
			WTPartUsageLink link = (WTPartUsageLink)qr2.nextElement();
			
			// 判断物料直接下层物料成熟度 		开始
			WTPartMaster master = link.getUses();
			WTPart childPart = PartUtil.getLastestWTPartByNumber(master.getNumber());
			
			String childPN = childPart.getNumber();
			String childName = childPart.getName();
			
			String childMaturity = "";
			if (needupdateMap.containsKey(childPN)){
				childMaturity = needupdateMap.get(childPN);
			} else {
				childMaturity = (String)GenericUtil.getObjectAttributeValue(part.getMaster(), PartConstant.IBA_CATL_Maturity);
			}
			
			if (!extraPartsNeedToUpradeMaturity.contains(childPart) && !"3".equals(childMaturity) && !"6".equals(childMaturity)){	// 子部件成熟度不为3或6，且不在需要更新的excel中，则违规
				
				String bigVer = childPart.getVersionIdentifier().getValue();//大版本
				String smallVer = childPart.getIterationIdentifier().getValue();//小版本
				logs.add(new String[]{childPN, childName, bigVer + "." + smallVer, childPart.getModifier().getFullName(), childPart.getFolderPath()});
				extraPartsNeedToUpradeMaturity.add(childPart);
				
				checkNeedToUpgradeMatuirty(childPart, needupdateMap, extraPartsNeedToUpradeMaturity, logs);
			}
			// 判断物料直接下层物料成熟度 		结束
			
			// 判断物料直接下层物料的替代料成熟度		开始
			WTCollection collection = WTPartHelper.service.getSubstituteLinks(link);
			if (!collection.isEmpty()) {
				Iterator itr = collection.iterator();
				while (itr.hasNext()) {
					ObjectReference objReference = (ObjectReference) itr.next();
					WTPartSubstituteLink subLink = (WTPartSubstituteLink) objReference.getObject();
					WTPartMaster subpartMaster = (WTPartMaster) subLink.getSubstitutes();
					String subPartName = subpartMaster.getName();
					String subPartNumber = subpartMaster.getNumber();
					WTPart subPart = PartUtil.getLastestWTPartByNumber(subPartNumber);
					String subMaturity = "";
					if (needupdateMap.containsKey(subPartNumber)){
						subMaturity = needupdateMap.get(subPartNumber);
					} else {
						subMaturity = (String)GenericUtil.getObjectAttributeValue(subpartMaster, PartConstant.IBA_CATL_Maturity);
					}
					
					if (!extraPartsNeedToUpradeMaturity.contains(subPart) && !"3".equals(subMaturity) && !"6".equals(subMaturity)){	// 替代件的成熟度不为3或6，且不在需要更新的excel中，则违规
						
						String subbigVer = subPart.getVersionIdentifier().getValue();//大版本
						String subsmallVer = subPart.getIterationIdentifier().getValue();//小版本
						logs.add(new String[]{subPartNumber, subPartName, subbigVer+""+subsmallVer, subPart.getModifier().getFullName(), subPart.getFolderPath()});
						extraPartsNeedToUpradeMaturity.add(childPart);
						
						checkNeedToUpgradeMatuirty(subPart, needupdateMap, extraPartsNeedToUpradeMaturity, logs);
					}
				}
			}
			// 判断物料直接下层物料的替代料成熟度		结束
		}
	}
}
