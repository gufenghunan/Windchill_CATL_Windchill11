package com.catl.line.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import wt.session.SessionServerHelper;

import com.catl.line.constant.ConstantLine;
import com.catl.line.service.BoxExplainService;

@Controller
public class BoxExplainController {
	@Resource
	private BoxExplainService boxExplainService;
	
	/**
	 * 获取客户
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "/boxExplain/getCustomer.do")
	public void getCustomer(HttpServletResponse response) throws Exception {
		response.setContentType("text/html;charset=utf-8");
	    response.setCharacterEncoding("utf-8");
		response.getWriter().write(boxExplainService.getCustomer().toString()); 
	}
	/**
	 * 获取包装要求
	 * @param customer
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "/boxExplain/getPackageAsk.do")
	public void getPackageAsk(@RequestParam String customer,HttpServletResponse response) throws Exception {
		response.setContentType("text/html;charset=utf-8");
	    response.setCharacterEncoding("utf-8");
		response.getWriter().write(boxExplainService.getPackageAsk(customer).toString()); 
	}
	/**
	 * 获取备注
	 * @param customer
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "/boxExplain/getRemark.do")
	public void getRemark(@RequestParam String customer,HttpServletResponse response) throws Exception {
		response.setContentType("text/html;charset=utf-8");
	    response.setCharacterEncoding("utf-8");
		response.getWriter().write(boxExplainService.getRemark(customer).toString()); 
	}
	
	/**
	 * 获取装箱说明模版 客户输入
	 * @param customer
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "/boxExplain/getCustomerInput.do")
	public void getCustomerInput(@RequestParam String customer,@RequestParam String oid,HttpServletResponse response) throws Exception {
		response.setContentType("text/html;charset=utf-8");
	    response.setCharacterEncoding("utf-8");
		response.getWriter().write(boxExplainService.getCustomerInput(customer,oid).toString()); 
	}
	
	/**
	 * 获取子件
	 * @param number
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "/boxExplain/getChildPN.do")
	public void getChildPN(@RequestParam String number,HttpServletResponse response) throws Exception {
		response.setContentType("text/html;charset=utf-8");
	    response.setCharacterEncoding("utf-8");
		response.getWriter().write(boxExplainService.getChildPN(number).toString()); 
	}
	
	/**
	 * 获取子件
	 * @param number
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "/boxExplain/getAsmInfo.do")
	public ModelAndView getAsmInfo(@RequestParam String oid) throws Exception {
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		ModelAndView mav = new ModelAndView("jsonView");
		try {
			mav.addObject(ConstantLine.str_data, boxExplainService.getAsmInfo(oid));
			mav.addObject(ConstantLine.str_success, true);
		} catch (Exception e) {
			e.printStackTrace();
			mav.addObject(ConstantLine.str_msg,e.getLocalizedMessage());
			mav.addObject(ConstantLine.str_success, true);
		} finally {
			SessionServerHelper.manager.setAccessEnforced(enforce);
		}
		return mav;
	}
	
	/**
	 * 创建装箱说明
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "/boxExplain/createBoxExplain.do",method = { RequestMethod.POST,
            RequestMethod.GET })
	public void createBoxExplain(@RequestParam("dataList")String boxExplainFromPage,HttpServletResponse response) throws Exception {
		response.setContentType("text/html;charset=utf-8");
	    response.setCharacterEncoding("utf-8");
		response.getWriter().write(boxExplainService.createBoxExplain(boxExplainFromPage)); 
	}
	
}
