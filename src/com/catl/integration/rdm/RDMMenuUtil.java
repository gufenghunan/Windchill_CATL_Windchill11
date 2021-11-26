package com.catl.integration.rdm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;

import wt.services.applicationcontext.implementation.ServiceProperties;
import wt.util.WTException;

import com.catl.integration.rdm.bean.MenuBean;

public class RDMMenuUtil {
	
	public static final String IS_TOP_Y = "Y";
	public static final String IS_TOP_N = "N";
	public static final String MENU_SEP = "|";
	private static final Logger logger = Logger.getLogger(RDMMenuUtil.class);

	public static HashMap getTestMenuMap(String userName) throws IOException, JSONException {
		HashMap<String, MenuBean> ret = new HashMap<String, MenuBean>();
		logger.debug("Start getMenuString");
		ArrayList list = getTestMenuList(userName);
		
		if (list != null && list.size() > 0) {
			for (int i = 0; i<list.size(); i++) {
				MenuBean bean = (MenuBean)list.get(i);
				if (bean.getIsTop().equals(IS_TOP_N)) {
					if (!ret.containsKey(bean.getActionId())) {
						ret.put(bean.getActionId(), bean);
					} 
				}
			}
		}
		
		logger.debug("show getMenuString:::" + ret);
		logger.debug("End getMenuString");
		return ret;
	}
	
	@SuppressWarnings("unchecked")
	public static ArrayList<MenuBean> getTestMenuList(String userName) throws IOException, JSONException {
		ArrayList ret = new ArrayList<MenuBean>();
		MenuBean bean = null;
		
		String testcontent = "{id:'1006daab-6fa3-4833-97be-2cf343c04aad', name:'Product', page:'#*', isTop: 'Y'},{id:'32f5b357-295e-aaaa-ba6d-aaa76d38aaaa', name:'Portfolio', page:'/pages/lifecycle/entity/list.jsf?type=PDL', isTop: 'N'},{id:'32f5b357-295e-bbbb-ba6d-bbb76d38a719', name:'Product', page:'/pages/lifecycle/entity/list.jsf?type=PRD', isTop: 'N'},{id:'1006daab-1111-4833-97be-2cf343c04aad', name:'Market ', page:'/pages/entity/list.jsf?type=MRQ', isTop: 'N'},{id:'b00df805-aaaa-bbbb-aaaa-110603c8e130', name:'Department', page:'#*', isTop: 'Y'},{id:'b00df805-aaaa-bbbb-bbbb-110603c8e130', name:'Department', page:'/pages/rddepartment/deptMain.jsf', isTop: 'N'},{id:'b00df333-aaaa-bbbb-cccc-110603c8e130', name:'Meeting', page:'/pages/entity/list.jsf?type=MET', isTop: 'N'},{id:'b00df444-aaaa-bbbb-cccc-110603c8e130', name:'Equipment', page:'/pages/entity/list.jsf?type=EQP', isTop: 'N'}";
        
        JSONArray  jsonarray = new JSONArray ("[" + testcontent + "]");
        
        for(int i=0;i<jsonarray.length();i++){  
        	bean = new MenuBean();
            String name = jsonarray.getJSONObject(i).getString("name");  
            String page = jsonarray.getJSONObject(i).getString("page");
	        String isTop = jsonarray.getJSONObject(i).getString("isTop");
	        String id = jsonarray.getJSONObject(i).getString("id");
	        
	        System.out.println("name:" + name);
	        System.out.println("page:" + page);
	        System.out.println("isTop:" + isTop);
	        System.out.println("id:" + id);
	        bean.setName(name);
	        bean.setPage(page);
	        bean.setIsTop(isTop);
	        bean.setActionId(id);
	        ret.add(bean);
        } 
        
        return ret;
	}
	
	public static HashMap<String, MenuBean> getMenuMap(String userName) throws IOException, JSONException, WTException {
		HashMap<String, MenuBean> ret = new HashMap<String, MenuBean>();
		logger.debug("Start getMenuString");
		ArrayList list = getMenuList(userName);
		
		if (list != null && list.size() > 0) {
			for (int i = 0; i<list.size(); i++) {
				MenuBean bean = (MenuBean)list.get(i);
				if (bean.getIsTop().equals(IS_TOP_N)) {
					if (!ret.containsKey(bean.getActionId())) {
						ret.put(bean.getActionId(), bean);
					} 
				}
			}
		}
		
		logger.debug("show getMenuString:::" + ret);
		logger.debug("End getMenuString");
		return ret;
	}
	
	public static String getHTMLContent(String urlString) throws IOException {
		URL url;
		url = new URL(urlString);
		
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestProperty("Accept-Language: ", "en-us");
		
		BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
		StringBuffer sb = new StringBuffer();
		String line;
		while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        br.close();
        //{id:'e6c82f4f-010d-4b20-9291-e060271bea4c', name:'个人空间', page:'#*', isTop: 'Y'},{id:'3b8cc05e-597e-4500-84e5-310a8b54d75b', name:'我的任务', page:'/pages/task/list/myTask.jsf', isTop: 'N'},{id:'137621bf-b92a-4f71-a1b8-bb23e71dfbbb', name:'工作日志', page:'/pages/myspace/log/workLogList.jsf', isTop: 'N'},{id:'9dd0572a-fd97-4b18-b546-940243a94fdf', name:'个人仪表盘', page:'/pages/myspace/myMeterTray.jsf', isTop: 'N'},{id:'507621bf-b92a-4f71-a1b8-bb23e71dfee3', name:'我的通讯录', page:'/pages/myspace/myAddressBookList.jsf', isTop: 'N'},{id:'507621bf-b92a-4f71-a1b8-bb23e71dfaaa', name:'我的日历', page:'/pages/myspace/myCalendar.jsf', isTop: 'N'},{id:'b00df805-73bf-aaaa-96d1-ee0603c8e1aa', name:'我的地盘', page:'/pages/rdextend/dashboardTab.jsf?menuId=b00df805-73bf-aaaa-96d1-ee0603c8e1aa&isMyscope=Y', isTop: 'N'},{id:'32f5b357-295e-4af7-ba6d-4d676d38a719', name:'项目管理', page:'#*', isTop: 'Y'},{id:'342babd2-0d04-4828-a0ac-9350cc9cf668', name:'立项管理', page:'/pages/rdextend/dashboardTab.jsf?menuId=342babd2-0d04-4828-a0ac-9350cc9cf668', isTop: 'N'},{id:'2c4d70cd-2671-46e7-a9b6-9d057e1082bf', name:'项目管理', page:'/pages/lifecycle/entity/list.jsf?type=PJT', isTop: 'N'},{id:'37c97e40-52f2-4e04-b208-aa6ca1ffff45', name:'评审管理', page:'/pages/entity/list.jsf?type=REW', isTop: 'N'},{id:'c4415e6c-d159-4b16-9fdc-7f4e29545ad2', name:'专项评审', page:'/pages/rdextend/dashboardTab.jsf?menuId=c4415e6c-d159-4b16-9fdc-7f4e29545ad2', isTop: 'N'},{id:'b00df111-aaaa-bbbb-cccc-110603c8e130', name:'会议管理', page:'/pages/entity/list.jsf?type=MET', isTop: 'N'},{id:'ff49a612-5728-4369-ba82-45de85c4c9c0', name:'设备使用', page:'/pages/rdextend/dashboardTab.jsf?menuId=ff49a612-5728-4369-ba82-45de85c4c9c0', isTop: 'N'},{id:'b00df333-aaaa-bbbb-cccc-333603c8e130', name:'变更管理', page:'/pages/entity/list.jsf?type=CHG', isTop: 'N'},{id:'6c3ee2da-dd22-49f2-be3c-3781db4f3a54', name:'结项管理', page:'/pages/rdextend/dashboardTab.jsf?menuId=6c3ee2da-dd22-49f2-be3c-3781db4f3a54', isTop: 'N'},{id:'311fea93-9e8a-4be0-80d3-c34c9ce20173', name:'Lesson Learn', page:'/pages/rdextend/dashboardTab.jsf?menuId=311fea93-9e8a-4be0-80d3-c34c9ce20173', isTop: 'Y'},{id:'76d41b6d-8518-4241-b6df-b7fdd39ce7cc', name:'风险管理', page:'/pages/entity/list.jsf?type=RSK', isTop: 'N'},{id:'76d41b6d-8518-4241-b6df-b7fdd39ce79c', name:'问题管理', page:'/pages/entity/list.jsf?type=ISU', isTop: 'N'},{id:'8a4467fb-92ac-43de-b818-c930d6ed2acb', name:'评审问题管理', page:'/pages/rdextend/dashboardTab.jsf?menuId=8a4467fb-92ac-43de-b818-c930d6ed2acb', isTop: 'N'},{id:'a0d47055-456f-496b-bbc5-f7357c97351f', name:'8D报告', page:'/pages/rdextend/dashboardTab.jsf?menuId=a0d47055-456f-496b-bbc5-f7357c97351f', isTop: 'N'},{id:'7c74f4b1-ed2a-4323-97d7-b34f7ca5dd0f', name:'文档管理', page:'#*', isTop: 'Y'},{id:'b00df805-aaaa-492b-96d1-222203c8e130', name:'文档仓库', page:'/pages/lifecycle/entity/list.jsf?type=DOC', isTop: 'N'},{id:'d4a86e04-17d1-4698-aa66-88d23e96af1c', name:'文档审核/变更', page:'/pages/rdextend/dashboardTab.jsf?menuId=d4a86e04-17d1-4698-aa66-88d23e96af1c', isTop: 'N'},{id:'b00df805-aaaa-492b-96d1-111103c8e130', name:'文档查询', page:'/pages/lifecycle/entity/document/docSearch.jsf?type=DOC&pageType=SEARCH', isTop: 'N'}
        System.out.println(sb.toString());
        return sb.toString();
	}
	
	@SuppressWarnings("unchecked")
	public static ArrayList<MenuBean> getMenuList(String userName) throws IOException, JSONException, WTException {
		ArrayList<MenuBean> ret = new ArrayList<MenuBean>();
		
//		Properties wtproperties = WTProperties.getLocalProperties();
//		String rdmhost = wtproperties.getProperty("rdm.address", "default value");
//		logger.debug(">>>>>rdmhost:" + rdmhost);
//		System.out.println(">>>>>rdmhost:" + rdmhost);
		
		Properties props = ServiceProperties.getServiceProperties("WTServiceProviderFromProperties");
		String rdmaddress = props.getProperty("rdm.address");
		logger.debug(">>>>>rdmaddress:" + rdmaddress);
		
		String urlStr = rdmaddress + "/menu/getMenu.wbs?username=" + userName;
		String htmlContent = "";
		MenuBean bean = null;
		
		htmlContent = getHTMLContent(urlStr);
        
        JSONArray  jsonarray = new JSONArray ("[" + htmlContent + "]");
        
        for(int i=0;i<jsonarray.length();i++){  
        	bean = new MenuBean();
            String name = jsonarray.getJSONObject(i).getString("name");  
            String page = jsonarray.getJSONObject(i).getString("page");
	        String isTop = jsonarray.getJSONObject(i).getString("isTop");
	        String id = jsonarray.getJSONObject(i).getString("id");
	        
	        System.out.println("name:" + name);
	        System.out.println("page:" + page);
	        System.out.println("isTop:" + isTop);
	        System.out.println("id:" + id);
	        bean.setName(name);
	        bean.setPage(page);
	        bean.setIsTop(isTop);
	        bean.setActionId(id);
	        ret.add(bean);
        } 
        
        return ret;
	}
	
	public static void main(String[] args) {
		
		URL url;
		try {
			String urlStr = "http://10.16.11.163:2000/catl/getMenu.wbs?username=wcadmin";
			String htmlContent = "";
			
			htmlContent = getHTMLContent(urlStr);
	        
	        JSONArray  jsonarray = new JSONArray ("[" + htmlContent + "]");
	        
	        for(int i=0;i<jsonarray.length();i++){  
	            String name = jsonarray.getJSONObject(i).getString("name");  
	            String page = jsonarray.getJSONObject(i).getString("page");
		        String isTop = jsonarray.getJSONObject(i).getString("isTop");
		        String id = jsonarray.getJSONObject(i).getString("id");
		        
		        System.out.println("name:" + name);
		        System.out.println("page:" + page);
		        System.out.println("isTop:" + isTop);
		        System.out.println("id:" + id);
	        } 
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
