package com.catl.ri.riA.controller;

import java.io.IOException;

import javax.annotation.Resource;
import javax.script.ScriptException;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.catl.ri.riA.service.SummaryService;


/**
 * summary界面请求
 * @author hdong
 *
 */
@Scope("prototype")
@Controller("riASummaryController")
public class SummaryController {
	@Resource
	private SummaryService riASummaryService;
	@RequestMapping(value="/riA/getSummarySimpleHtml.do")
	public void getSummarySimpleHtml(HttpServletResponse response,String oid,String name,String level,String remark,@RequestParam String templatename) throws Exception {
		response.setContentType("text/html;charset=utf-8");
		response.setCharacterEncoding("utf-8");
		try {
			response.getWriter().write(riASummaryService.getSummarySimpleHtml(oid,name,level,remark,templatename));
		} catch (IOException | ScriptException e) {
			e.printStackTrace();
		}
	}
	
}
