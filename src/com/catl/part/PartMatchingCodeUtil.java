package com.catl.part;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;

import com.catl.common.global.GlobalVariable;
import com.catl.common.util.WCLocationConstants;
import com.catl.doc.workflow.DocClassificationModelNew;
import com.catl.doc.workflow.DocWfUtil;
import com.catl.loadData.util.ExcelReader;

import wt.part.Source;
import wt.workflow.engine.WfProcess;

public class PartMatchingCodeUtil {
	public static Logger logger = Logger.getLogger(PartMatchingCodeUtil.class.getName());
	
	public static final String PartMatchingCodePath = WCLocationConstants.WT_HOME + File.separator + "codebase"+ File.separator+"config"+ File.separator+"custom"+ File.separator+"PartMatchingCode.xlsx";
	
	public static Map<String,PartMatchingCodeBean> partAllMatchingCode= new HashMap<String, PartMatchingCodeBean>();
	

	public static void main(String[] args){
		 Map<String, PartMatchingCodeBean> test = getAllPartMatchingCode();
		 for (String key : test.keySet()) {
			PartMatchingCodeBean pmc = test.get(key);
			System.out.println(key+"\t"+pmc.getBmuVersion().size());
			for(int i =0 ; i < pmc.getBmuVersion().size(); i++){
				System.out.println(key + "\t" + pmc.getBmuVersion().get(i));
			}
			System.out.println(key+"\t"+pmc.getCscVersion().size());
			
			for(int i =0 ; i < pmc.getCscVersion().size(); i++){
				System.out.println(key + "\t" + pmc.getCscVersion().get(i));
			}
			
			System.out.println(key+"\t"+pmc.getHvbVersion().size());
			
			for(int i =0 ; i < pmc.getHvbVersion().size(); i++){
				System.out.println(key + "\t" + pmc.getHvbVersion().get(i));
			}
		}
	}
	
	public static Map<String, PartMatchingCodeBean> getAllPartMatchingCode() {
		checkNewFile();
		return partAllMatchingCode;
	}
	
	private static void checkNewFile() {
		// 获取文件更新时间,若修改时间改变则更新规则文件
		String filePath = PartMatchingCodePath;
		File file = new File(filePath);
		Long fileModifyTime = file.lastModified();
		Long sysModifyTime = GlobalVariable.fileLastModifyTime.get("PartMatchingCode.xlsx");
		if (!(sysModifyTime == fileModifyTime)) {
			PartMatchingCodeUtil.getPartMatchingCode();
		}
	}
	

	/**
	 * 获取软件版本匹配码
	 * @return
	 */
	public static Map<String, PartMatchingCodeBean> getPartMatchingCode() {
		
		String filePath = PartMatchingCodePath;
		File file = new File(filePath);
		ExcelReader reader = new ExcelReader(file);
		try {
			reader.open();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		partAllMatchingCode.clear();
		reader.setSheetNum(0);
		int count = reader.getRowCount();
		//Map<String, PartMatchingCodeBean> partAllMatchingCode = new HashMap<String, PartMatchingCodeBean>();
		String matchingCode = "";
		String cscVersion = "";
		String hvbVersion = "";
		for (int i = 1; i <= count; i++) {
			String rows[] = reader.readExcelLine(i);

			if (!(rows == null || rows[1].isEmpty())) {

				String bumVersion = rows[1].isEmpty() ? "" : rows[1].trim();
				matchingCode = rows[2].isEmpty() ? matchingCode : rows[2].trim();
				cscVersion = rows[3].isEmpty() ? cscVersion : rows[3].trim();
				hvbVersion = rows[5].isEmpty() ? hvbVersion : rows[5].trim();
				
				if(partAllMatchingCode.containsKey(matchingCode)){
					PartMatchingCodeBean pmc = partAllMatchingCode.get(matchingCode);
					List<String> bmuList = pmc.getBmuVersion();
					List<String> cscList = pmc.getCscVersion();
					List<String> hvbList = pmc.getHvbVersion();
					
					if(!bmuList.contains(bumVersion)){
						bmuList.add(bumVersion);
					}
					
					if(!cscList.contains(cscVersion)){
						cscList.add(cscVersion);
					}
					
					if(!hvbList.contains(hvbVersion)){
						hvbList.add(hvbVersion);
					}
					
					pmc.setBmuVersion(bmuList);
					pmc.setCscVersion(cscList);
					pmc.setHvbVersion(hvbList);
					
					partAllMatchingCode.put(matchingCode, pmc);
					
				}else{
					PartMatchingCodeBean pmc = new PartMatchingCodeBean();
					pmc.setMatchingCode(matchingCode);
					List<String> bmuList = new ArrayList<>();
					List<String> cscList = new ArrayList<>();
					List<String> hvbList = new ArrayList<>();
					bmuList.add(bumVersion);
					cscList.add(cscVersion);
					hvbList.add(hvbVersion);
					
					pmc.setBmuVersion(bmuList);
					pmc.setCscVersion(cscList);
					pmc.setHvbVersion(hvbList);
					
					partAllMatchingCode.put(matchingCode, pmc);
				}
	
			}// end if
		}// end for
		GlobalVariable.fileLastModifyTime.put("PartMatchingCode.xlsx", file.lastModified());
		return partAllMatchingCode;
	}
}
