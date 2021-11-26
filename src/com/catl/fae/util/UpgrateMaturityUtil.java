package com.catl.fae.util;

import java.rmi.RemoteException;
import java.util.List;

import com.catl.common.util.GenericUtil;
import com.catl.common.util.PartUtil;
import com.catl.ecad.utils.ECADutil;
import com.catl.ecad.utils.IBAUtility;

import wt.part.WTPart;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

public class UpgrateMaturityUtil {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	/**
	 * 升级物料成熟度
	 * @param part
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 * @throws RemoteException
	 */
	public static void upgrateMaturity(WTPart part) throws WTException, WTPropertyVetoException, RemoteException{
		if(enableUpgrate(part)){
			String targetMaturity = getMaxMaturityOfDelivable(part);
			IBAUtility iba = new IBAUtility(part);
			iba.setIBAValue("CATL_Maturity", targetMaturity);
			
			iba.updateAttributeContainer(part);
			iba.updateIBAHolder(part);
			
			checkEnableUpgrateParentPart(part);
		}
	}
	
	/**
	 * 判断部件是否可以升级成熟度
	 * @param part
	 * @return
	 * @throws WTException
	 */
	public static boolean enableUpgrate(WTPart part) throws WTException{
		String targetMaturity = getMaxMaturityOfDelivable(part);
		String currentMaturity = getMaturityByPart(part);
		if(targetMaturity.compareTo(currentMaturity) > 0){
			String childMaturity = getMinMaturityOfChildPart(part);
			if(childMaturity.compareTo(targetMaturity) >= 0 ){
				//upgrateMaturity(part);
				return true;
			}			
		}
		return false;
	}
	
	/**
	 * 获取交付件最高可以升级成熟度
	 * @return
	 */
	public static String getMaxMaturityOfDelivable(WTPart part){
		
		return null;
	}
	
	/**
	 * 获取供应商成熟度
	 * @param AVL
	 * @return
	 */
	public static String getMaturityOfAVL(String AVL){
		
		return null;
	}
	
	/**
	 * 获取部件成熟度
	 * @param part
	 * @return
	 * @throws WTException
	 */
	public static String getMaturityByPart(WTPart part) throws WTException{
		String maturity = (String) GenericUtil.getObjectMasteredAttributeValue(part.getMaster(), "CATL_Maturity");
		return maturity;
	}
	
	/**
	 * 获取子件中最低成熟度
	 * @param parent
	 * @return
	 * @throws WTException
	 */
	public static String getMinMaturityOfChildPart(WTPart parent) throws WTException{
		List<WTPart> childParts = ECADutil.getChildPart(parent);
		String minMaturity = "6";
		for (int i = 0; i < childParts.size(); i++) {
			WTPart childPart = childParts.get(i);
			String maturity = getMaturityByPart(childPart);
			if(minMaturity.compareTo(maturity) > 0){
				minMaturity = maturity;
			}
		}
		return minMaturity;
	}
	
	/**
	 * 检查父级部件是否可以升级成熟度
	 * @param part
	 * @throws WTException 
	 * @throws RemoteException 
	 * @throws WTPropertyVetoException 
	 */
	public static void checkEnableUpgrateParentPart(WTPart part) throws WTException, WTPropertyVetoException, RemoteException{
		List<WTPart> parents = PartUtil.getParentPartByChildPart(part);
		for(int i = 0; i < parents.size(); i++){
			WTPart parentPart = parents.get(i);
			if(enableUpgrate(parentPart)){
				upgrateMaturity(parentPart);
			}
		}
		//return null;
	}

}
