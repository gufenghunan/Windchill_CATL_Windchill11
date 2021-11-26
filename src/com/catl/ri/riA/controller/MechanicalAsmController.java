package com.catl.ri.riA.controller;

import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.catl.ri.constant.ConstantRI;
import com.catl.ri.riA.service.MechanicalAsmService;

import wt.session.SessionServerHelper;

/**
 * 机械件组合界面请求
 * @author hdong
 *
 */
@Scope("prototype")
@Controller("riAAsmController")
public class MechanicalAsmController {
	@Resource
	private MechanicalAsmService riAAsmService;
	
	@RequestMapping(value="/riA/getAsmInfo.do")
	public ModelAndView getAsmInfo(String name) throws Exception {
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		ModelAndView mav = new ModelAndView("jsonView");
		try {
			mav.addObject(ConstantRI.str_data,riAAsmService.getAsmInfo(name));
			mav.addObject(ConstantRI.str_success, true);
			mav.addObject(ConstantRI.str_msg, "");
		} catch (Exception e) {
			e.printStackTrace();
			mav.addObject(ConstantRI.str_msg, e.getLocalizedMessage());
			mav.addObject(ConstantRI.str_success, true);
		} finally {
			SessionServerHelper.manager.setAccessEnforced(enforce);
		}
		return mav;
	}
	
	@RequestMapping(value="/riA/getAsmPNJson.do")
	public ModelAndView getAsmPNJson() throws Exception {
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		ModelAndView mav = new ModelAndView("jsonView");
		try {
			mav.addObject(ConstantRI.str_data,riAAsmService.getAsmPNJson());
			mav.addObject(ConstantRI.str_success, true);
			mav.addObject(ConstantRI.str_msg, "");
		} catch (Exception e) {
			e.printStackTrace();
			mav.addObject(ConstantRI.str_msg, e.getLocalizedMessage());
			mav.addObject(ConstantRI.str_success, true);
		} finally {
			SessionServerHelper.manager.setAccessEnforced(enforce);
		}
		return mav;
	}
	
}
