package com.catl.battery.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import wt.util.WTProperties;

import com.catl.battery.constant.ConstantBattery;
import com.catl.battery.helper.CommonHelper;
import com.catl.battery.service.SummaryService;

@Scope("prototype")
@Service("summaryService")
public class SummaryServiceImpl implements SummaryService{
	private static final Logger logger = Logger.getLogger(SummaryServiceImpl.class.getName());
	private static String wt_home = "";
	static {
		try {
			wt_home = WTProperties.getLocalProperties().getProperty("wt.home",
					"UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getSummarySimpleHtml(String oid,String name,String level,String remark,String templatename) throws Exception {
		// TODO Auto-generated method stub
		String htmlStr = "";
		String htmString = "";
		try {
			FileInputStream fileInputStream = new FileInputStream(new File(
					wt_home + ConstantBattery.config_path_htmltemplate
							+ templatename));
			InputStreamReader streamReader = new InputStreamReader(
					fileInputStream);
			BufferedReader reader = new BufferedReader(streamReader);
			while ((htmlStr = reader.readLine()) != null) {
				if (htmlStr != null) {
					htmString += htmlStr;

					continue;
				}
			}
			streamReader.close();
			fileInputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String fileName = CommonHelper.getFileName(oid, name, level, remark);
		JSONArray array=CommonHelper.getExcelValue(CommonHelper.getBatteryDirFile(fileName), "Summary");
		if(array!=null){
			for (int i = 0; i < array.size(); i++) {
				JSONObject attr=(JSONObject) array.get(i);
				String region=(String) attr.get("region");
				if(htmString.indexOf("#"+region+"#")>-1){
					Object value=attr.get("value");
					htmString=htmString.replace("#"+region+"#", value+"");
				}
			}
		}
		return htmString;
	}

}
