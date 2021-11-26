package com.catl.require.controller;

import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import wt.session.SessionServerHelper;

import com.catl.require.constant.ConstantRequire;
import com.catl.require.service.CommonService;

@Scope("prototype")
@Controller
public class CommonController {
	@Resource
	private CommonService commonservice;
	/**
	 * @param oid
	 * @throws Exception
	 */
	@RequestMapping(value = "/require/getPlatformPartsInfo.do")
	public ModelAndView getPlatformPartsInfo(@RequestParam String oid) throws Exception {
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		ModelAndView mav = new ModelAndView("jsonView");
		try {
			mav.addObject(ConstantRequire.str_data, commonservice.getPlatformPartsInfo(oid));
			mav.addObject(ConstantRequire.str_msg, "");
			mav.addObject(ConstantRequire.str_success, true);
		} catch (Exception e) {
			e.printStackTrace();
			mav.addObject(ConstantRequire.str_msg, e.getLocalizedMessage());
			mav.addObject(ConstantRequire.str_success, true);
		} finally {
			SessionServerHelper.manager.setAccessEnforced(enforce);
		}
		return mav;
	}
	
	/**
	 * @param oid
	 * @throws Exception
	 */
	@RequestMapping(value = "/require/updateplatform.do")
	public ModelAndView updateplatform(
			@RequestParam(required = true) String partNumber,
			@RequestParam String name, @RequestParam String value) throws Exception {
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		ModelAndView mav = new ModelAndView("jsonView");
		try {
			mav.addObject(ConstantRequire.str_data, commonservice.updateplatform(partNumber, name, value));
			mav.addObject(ConstantRequire.str_msg, "");
			mav.addObject(ConstantRequire.str_success, true);
		} catch (Exception e) {
			e.printStackTrace();
			mav.addObject(ConstantRequire.str_msg, e.getLocalizedMessage());
			mav.addObject(ConstantRequire.str_success, true);
		} finally {
			SessionServerHelper.manager.setAccessEnforced(enforce);
		}
		return mav;
	}
	
	/**
	 * @param oid
	 * @throws Exception
	 */
	@RequestMapping(value = "/require/validateplatform.do")
	public ModelAndView validateplatform(
			@RequestParam(required = true) String partNumber,@RequestParam String value) throws Exception {
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		ModelAndView mav = new ModelAndView("jsonView");
		try {
			 commonservice.validateplatform(partNumber,value);
			mav.addObject(ConstantRequire.str_msg, "");
			mav.addObject(ConstantRequire.str_success, true);
		} catch (Exception e) {
			e.printStackTrace();
			mav.addObject(ConstantRequire.str_msg, e.getLocalizedMessage());
			mav.addObject(ConstantRequire.str_success, true);
		} finally {
			SessionServerHelper.manager.setAccessEnforced(enforce);
		}
		return mav;
	}
	
	/**
	 * @param oid
	 * @throws Exception
	 */
	@RequestMapping(value = "/require/ishideplatform.do")
	public ModelAndView ishideplatform(@RequestParam String containeroid) throws Exception {
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		ModelAndView mav = new ModelAndView("jsonView");
		try {
			mav.addObject(ConstantRequire.str_data, commonservice.ishideplatform(containeroid));
			mav.addObject(ConstantRequire.str_msg, "");
			mav.addObject(ConstantRequire.str_success, true);
		} catch (Exception e) {
			e.printStackTrace();
			mav.addObject(ConstantRequire.str_msg, e.getLocalizedMessage());
			mav.addObject(ConstantRequire.str_success, true);
		} finally {
			SessionServerHelper.manager.setAccessEnforced(enforce);
		}
		return mav;
	}
	
}
