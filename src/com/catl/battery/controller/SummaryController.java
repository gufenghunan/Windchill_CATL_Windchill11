package com.catl.battery.controller;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;

import javax.annotation.Resource;
import javax.script.ScriptException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import wt.util.WTException;
import wt.util.WTRuntimeException;

import com.catl.battery.constant.ConstantBattery;
import com.catl.battery.service.SummaryService;
import com.catl.battery.util.SummaryCompare;
import com.ptc.core.components.util.RequestHelper;
import com.ptc.netmarkets.util.beans.NmCommandBean;

/**
 * summary界面请求
 * @author hdong
 *
 */
@Scope("prototype")
@Controller
public class SummaryController {
	@Resource
	private SummaryService summaryService;
	@RequestMapping(value="/battery/getSummarySimpleHtml.do")
	public void getSummarySimpleHtml(HttpServletResponse response,String oid,String name,String level,String remark,@RequestParam String templatename) throws Exception {
		response.setContentType("text/html;charset=utf-8");
		response.setCharacterEncoding("utf-8");
		try {
			response.getWriter().write(summaryService.getSummarySimpleHtml(oid,name,level,remark,templatename));
		} catch (IOException | ScriptException e) {
			e.printStackTrace();
		}
	}
	@RequestMapping(value="/battery/compareSummary.do")
	public void compareSummary(HttpServletResponse response,HttpServletRequest request) throws WTRuntimeException, WTException, PropertyVetoException, IOException {
		try{
			RequestHelper.initializeCommandBean(request,response);
			NmCommandBean clientData = RequestHelper.getCommandBean(request);
			response.setCharacterEncoding("GB2312");
			SummaryCompare summary=new SummaryCompare();
			XSSFWorkbook wb=summary.compare(clientData);
			String fileName=ConstantBattery.config_export_comparesummary+".xlsx";
			String userAgent = request.getHeader("USER-AGENT");
	        if(StringUtils.contains(userAgent, "MSIE")){//IE浏览器
	        	fileName = URLEncoder.encode(fileName,"UTF8");
	        }else if(StringUtils.contains(userAgent, "Mozilla")){//google,火狐浏览器
	        	fileName = new String(fileName.getBytes(), "ISO8859-1");
	        }else{
	        	fileName = URLEncoder.encode(fileName,"UTF8");//其他浏览器
	        }
	        response.reset();
	        response.setContentType("application/vnd.ms-excel;charset=UTF-8");
	        response.setHeader("Content-Disposition", "attachment;fileName="+fileName);
		    OutputStream ouputStream = response.getOutputStream();    
	        wb.write(ouputStream);    
	        ouputStream.flush();    
	        ouputStream.close(); 
	        response.flushBuffer();
		}catch(Exception e){
			e.printStackTrace();
			response.getWriter().write(e.getLocalizedMessage());
		}
	}
	
}
