package com.catl.integration.trp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.xml.sax.InputSource;

import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.maturity.Promotable;
import wt.maturity.PromotionNotice;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.vc.config.LatestConfigSpec;

import com.catl.integration.trp.client.ITestSampleInfoWSService;
import com.catl.integration.trp.client.ITestSampleInfoWSServiceService;
import com.catl.integration.trp.client.Item;
import com.catl.integration.trp.client.TestSampleInfoWSRequest;
import com.catl.integration.trp.client.TestSampleInfoWSResponse;
import com.catl.line.constant.ConstantLine;
import com.catl.line.util.ExcelUtil;
import com.catl.line.util.IBAUtility;
import com.catl.line.util.WCUtil;
import com.ptc.prolog.pub.RunTimeException;
import com.ptc.xworks.windchill.util.PromotionNoticeUtils;

public class GetAfterTimeParts implements RemoteAccess{
	private static String wt_home = "";
	private static long configTime = 0;
	private static List<List> configList = new ArrayList<List>();
	static {
		try {
			wt_home = WTProperties.getLocalProperties().getProperty("wt.home",
					"UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	  public static void sendToTrp(WTObject pbo) throws Exception{
			SessionServerHelper.manager.setAccessEnforced(false);
			try{
			List parts=new ArrayList();
			if(pbo instanceof PromotionNotice){
				PromotionNotice pn=(PromotionNotice) pbo;
				 Collection<Promotable> objs=PromotionNoticeUtils.getPromotionNoticeItems((PromotionNotice) pn);
				 Object[] arrayobjs=objs.toArray();
	             for (int i = 0; i < arrayobjs.length; i++) {
	            	 Object obj=arrayobjs[i];
	            	 if(obj instanceof WTPart){
	            		 WTPart part=(WTPart) obj;
	            		 parts.add(part);
	            	 }
				}
	            List partsinfos=partsinfo(parts);  
	            if(partsinfos.size()>0){
	            	ITestSampleInfoWSServiceService service=new ITestSampleInfoWSServiceService();
	            	ITestSampleInfoWSService port=service.getITestSampleInfoWSServicePort();
	   	            TestSampleInfoWSRequest request = new TestSampleInfoWSRequest();
	   	            for (int i = 0; i < partsinfos.size(); i++) {
	   	            	Item infoitem=new Item();
	   	            	Map<String,String> map=(Map) partsinfos.get(i);
	   	            	infoitem.setPnCode(map.get("PN_CODE"));
	   	            	infoitem.setCellCapacity(map.get("CELL_CAPACITY"));
	   	            	infoitem.setCellModel(map.get("CELL_MODEL"));
	   	            	infoitem.setNominalVoltage(map.get("NOMINAL_VOLTAGE"));
	   	            	infoitem.setNormalEnerge(map.get("PRODUCET_ENERGY"));
	   	            	infoitem.setModuleQuantity(map.get("MODULE_QUANTITY"));
	   	            	infoitem.setMaterialName(map.get("MATERIAL_TYPE"));
	   	            	infoitem.setLength(map.get("MATERIAL_LENGTH").replace("mm", "").trim());
	   	            	infoitem.setWidth(map.get("MATERIAL_WIDTH").replace("mm", "").trim());
	   	            	infoitem.setHigth(map.get("MATERIAL_HIGTH").replace("mm", "").trim());
	   	            	infoitem.setLinkType(map.get("CELLCONNECTION_MODEL"));
	   	                request.getItem().add(infoitem);
	   				}
	   	          System.out.println("发送"+partsinfos.size()+"条数据到TRP");
	   	          TestSampleInfoWSResponse response=port.syncTestSampleInfo(request);
	   	          if(response.getCode().equals("0")){
	   	        	  throw new RunTimeException(response.getDesc());
	   	          }
	            }
			}
			}catch(Exception e){
				e.printStackTrace();
				throw e;
			}finally{
				SessionServerHelper.manager.setAccessEnforced(true);
			}
		
	  }
	  
	  public static Enumeration<WTPart> getAllPart(Timestamp time)
	  {
	    QueryResult qr = null;
	    try {
	      QuerySpec qs = new QuerySpec(WTPart.class);
	      SearchCondition sc = new SearchCondition(WTPart.class, "thePersistInfo.modifyStamp", ">", time);
	      qs.appendWhere(sc);
	      qr = PersistenceHelper.manager.find(qs);
	      System.out.println(qs);
	      LatestConfigSpec lcs = new LatestConfigSpec();
	      qr = lcs.process(qr);
	      System.out.println(qr.size());
	    } catch (WTException e) {
	      e.printStackTrace();
	    }
	    if (qr == null) {
	      return null;
	    }
	    return qr.getEnumeration();
	  }
	  
	  public static List<Map> partsinfo(List parts) throws ParseException, WTException, FileNotFoundException, IOException {
		SimpleDateFormat sdf=new SimpleDateFormat(ConstantLine.config_rds_time_format);
	    List list=new ArrayList();
	    for (int i = 0; i < parts.size(); i++) {
			WTPart wtPart = (WTPart) parts.get(i);
			    String number=wtPart.getNumber();
				String sendlwcname=getSendLwcname(number);
				if(!StringUtils.isEmpty(sendlwcname)){
						Map<String, Object> map=new HashMap();
						map.put("PN_CODE", wtPart.getNumber());
						map.put("MATERIAL_TYPE",sendlwcname);
						String[] attrs=ConstantLine.config_rds_part_attr.split(",");
						for (int j = 0; j < attrs.length;j++) {
							String attr=attrs[j];
							String[] names=attr.split("\\|");
							String rdsname=names[0];
							String plmname=names[1];
							IBAUtility iba=new IBAUtility(wtPart);
							String value=iba.getIBAValue(plmname);
							if(value==null){
								value="";
							}
							value=value.toUpperCase().replace("AH","").replace("KWH", "").replace("V", "")
									.replace("WH", "").replace("PCS", "").replace("MM", "");
							map.put(rdsname, value);
						}
						list.add(map);
				}
			
		
		}
		return list;
	 }
	  private static String getSendLwcname(String number) throws FileNotFoundException, IOException {
		  try{
		  loadExcelConfig();
		  for (int i = 0; i < configList.size(); i++) {
			  String prefix=(String) configList.get(i).get(0);
			  if(!StringUtils.isEmpty(prefix)){
				  if(number.startsWith(prefix)){
					  return  (String) configList.get(i).get(1);
				  }
			  }
		  }
		  }catch(Exception e){
			  e.printStackTrace();
		  }
		  return null;
	  }
	  public static void loadExcelConfig() throws FileNotFoundException, IOException{
		    File file=new File(wt_home+ConstantLine.config_sendtrp_path);
		    //File file=new File("E://config_send_trp.xls");
	  	 	if(configTime < file.lastModified()){
	  		    String[][] result =  ExcelUtil.getData(0,null,file,0,false);
	  		    configList=(List) getSheetInfo(result);
	  	 	}
	  }
	  
		/**
		 * 获取excel信息
		 * 
		 * @param result
		 * @return
		 */
		public static List getSheetInfo(String[][] result) {
			List list = new ArrayList();
			int rowLength = result.length;
			List headers = new ArrayList();
			for (int i = 0; i < rowLength; i++) {
				List rowlist = new ArrayList();
				int selectindex = 0;
				for (int j = 0; j < result[i].length; j++) {
					if (i == 0) {
						headers.add(result[i][j]);
					} else {
						if (headers.size() > j&& !headers.get(j).toString().trim().equals("")) {
							String value = result[i][j];
							rowlist.add(value);
						}
					}
				}
				if (rowlist.size()>0) {
					list.add(rowlist);
				}
			}
			return list;
		}
		
		
	/**
	   * 解析websevice返回的结果
	   * @param returnStr
	   * @return
	   * @throws Exception
	   */
	  public static String getFinalResult(String returnStr) throws Exception {
	  	String result = "";
	  	String exception="";
	  	StringReader read = new StringReader(returnStr);
	  	InputSource source = new InputSource(read);
	  	SAXBuilder sb = new SAXBuilder();
	  	Document doc = sb.build(source);
	  	Element root = doc.getRootElement();
	  	List messageList = root.getChildren();
	  	if (messageList.size() > 0) {
	  		Element message = (Element) messageList.get(0);
	  		List instanceList = message.getChildren();
	  		if (instanceList.size() > 0) {
	  			Element instance = (Element) instanceList.get(0);
	  			List dieUrlLinkList = instance.getChildren();
	  			if (dieUrlLinkList.size() > 0) {
	  				Element dieUrlLink = (Element) dieUrlLinkList.get(0);
	  				List finalLinkList = dieUrlLink.getChildren();
	  				if(finalLinkList.size()>0){
	  					result=((Element) finalLinkList.get(0)).getValue().toString();
	  					System.out.println(result);
	  					if(result.equals("0")){
	  						exception=((Element) finalLinkList.get(1)).getValue().toString();
	  						throw new RunTimeException(exception);
	  					}
	  					
	  				}
	  			}
	  		}
	  		return "SUCCESS";
	  	}else{
	  		return "FAILED";
	  	}
	  }
		public static void main(String[] args) throws Exception {
			RemoteMethodServer rms = RemoteMethodServer.getDefault();
			rms.setUserName("wcadmin");
			rms.setPassword("wcadmin");
			rms.invoke("test", GetAfterTimeParts.class.getName(), null, null, null);
		}
		public static void main11(String[] args) throws Exception {
			String sendlwcname=getSendLwcname("55");
			if(!StringUtils.isEmpty(sendlwcname)){
				System.out.println(sendlwcname);
			}
		}

		public static void test() throws Exception {
			PromotionNotice pn=(PromotionNotice) WCUtil.getWTObject("OR:wt.maturity.PromotionNotice:179661505");
			sendToTrp(pn);
		}

	 
}
