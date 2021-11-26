package com.catl.battery.controller;

import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import wt.session.SessionServerHelper;

import com.catl.battery.constant.ConstantBattery;
import com.catl.battery.service.MechanicalAsmService;

/**
 * 机械件组合界面请求
 * @author hdong
 *
 */
@Scope("prototype")
@Controller
public class MechanicalAsmController {
	@Resource
	private MechanicalAsmService asmService;
	
	@RequestMapping(value="/battery/getAsmInfo.do")
	public ModelAndView getAsmInfo(String name) throws Exception {
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		ModelAndView mav = new ModelAndView("jsonView");
		try {
			mav.addObject(ConstantBattery.str_data,asmService.getAsmInfo(name));
			mav.addObject(ConstantBattery.str_success, true);
			mav.addObject(ConstantBattery.str_msg, "");
		} catch (Exception e) {
			e.printStackTrace();
			mav.addObject(ConstantBattery.str_msg, e.getLocalizedMessage());
			mav.addObject(ConstantBattery.str_success, true);
		} finally {
			SessionServerHelper.manager.setAccessEnforced(enforce);
		}
		return mav;
	}
	
	@RequestMapping(value="/battery/getAsmPNJson.do")
	public ModelAndView getAsmPNJson() throws Exception {
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		ModelAndView mav = new ModelAndView("jsonView");
		try {
			mav.addObject(ConstantBattery.str_data,asmService.getAsmPNJson());
			mav.addObject(ConstantBattery.str_success, true);
			mav.addObject(ConstantBattery.str_msg, "");
		} catch (Exception e) {
			e.printStackTrace();
			mav.addObject(ConstantBattery.str_msg, e.getLocalizedMessage());
			mav.addObject(ConstantBattery.str_success, true);
		} finally {
			SessionServerHelper.manager.setAccessEnforced(enforce);
		}
		return mav;
	}
	
}
