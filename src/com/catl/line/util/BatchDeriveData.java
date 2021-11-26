package com.catl.line.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import wt.util.WTProperties;

import com.catl.line.constant.ConstantLine;

public class BatchDeriveData {
	public static String wt_home;
	static {
		try {
			wt_home = WTProperties.getLocalProperties().getProperty("wt.home",
					"UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static List getData(String path) throws FileNotFoundException,
			IOException, InvalidFormatException {
		File file = new File(path);
		String[][] result = ExcelUtil.getData(1, "线长模版", file, 5, 0, false);
		List<Map<String, String>> map = getSheetInfo(result);
		return map;

	}

	/**
	 * 获取excel数据
	 * 
	 * @param result
	 * @return
	 */
	public static List<Map<String, String>> getSheetInfo(String[][] result) {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		int rowLength = result.length;
		List headers = new ArrayList();
		Map prerowmap = new HashMap();
		outer: for (int i = 0; i < rowLength; i++) {
			Map rowmap = new HashMap();
			for (int j = 0; j < result[i].length; j++) {
				if (i == 0) {
					headers.add(result[i][j]);
				} else {
					if (j == 0 && StringUtils.isEmpty(result[i][j])) {
						break outer;
					}
					if (headers.size() > j&& !headers.get(j).toString().trim().equals("")) {
						rowmap.put(headers.get(j).toString().trim(),result[i][j]);
					}
				}
			}
			if (!rowmap.isEmpty()) {
				list.add(rowmap);
			}
		}
		return list;
	}
	public static String getJSONStr(Map map) {
		Map rmap = new HashMap();
		Set keyset = map.keySet();
		String basicinternal_name = ConstantLine.config_batchcreate_internal_name;
		Map matchmap = getMatchMap(basicinternal_name);
		Iterator keyite = keyset.iterator();
		Map jsonmap = new HashMap();
		while (keyite.hasNext()) {
			String key = (String) keyite.next();
			String value = (String) map.get(key);
			if (!key.equals("母PN") && !key.equals("PN")) {
				if(matchmap.containsKey(key)) {
					key = (String) matchmap.get(key);
					jsonmap.put(key, value);
				}
			}
		}
		System.out.println(JSONObject.fromObject(jsonmap).toString());
		return JSONObject.fromObject(jsonmap).toString();
	}

	private static Map getMatchMap(String basicinternal_name) {
		Map map = new HashMap();
		String[] matchs = basicinternal_name.split(",");
		for (int i = 0; i < matchs.length; i++) {
			String[] matcharray = matchs[i].split("\\|");
			map.put(matcharray[0], matcharray[1]);
		}
		return map;
	}

	public static void main(String[] args) throws FileNotFoundException,
			IOException, InvalidFormatException {
		List maps = (List) getData("E://上传PLM系统的EXCEL模板.xls");
		for (int i = 0; i < maps.size(); i++) {
			getJSONStr((Map) maps.get(i));
		}
	}
}
