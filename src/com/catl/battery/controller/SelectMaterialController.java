package com.catl.battery.controller;

import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import wt.session.SessionServerHelper;

import com.catl.battery.constant.ConstantBattery;
import com.catl.battery.service.SelectMaterialService;

/**
 * 选择材料界面请求
 * @author hdong
 *
 */
@Scope("prototype")
@Controller
public class SelectMaterialController {
	
	@Resource
	private SelectMaterialService selectMaterialService;
	
	@RequestMapping(value="/battery/getRecipeJson.do")
	public ModelAndView getRecipeJson(@RequestParam String recipenumber,String containerOid) throws Exception {
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		ModelAndView mav = new ModelAndView("jsonView");
		try {
			mav.addObject(ConstantBattery.str_data,selectMaterialService.getRecipeJson(containerOid,recipenumber));
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
	
	@RequestMapping(value="/battery/getSearchJson.do")
	public ModelAndView getSearchJson(@RequestParam String type) throws Exception {
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		ModelAndView mav = new ModelAndView("jsonView");
		try {
			mav.addObject(ConstantBattery.str_data,selectMaterialService.getSearchJson(type));
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
	
	@RequestMapping(value="/battery/getPartRowJson.do")
	public ModelAndView getPartRowJson(@RequestParam String oid) throws Exception {
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		ModelAndView mav = new ModelAndView("jsonView");
		try {
			mav.addObject(ConstantBattery.str_data,selectMaterialService.getPartRowJson(oid));
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
	
	@RequestMapping(value="/battery/getRecipenumbers.do")
	public ModelAndView getRecipenumbers(@RequestParam String materialNumber,String containerOid) throws Exception{
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		ModelAndView mav = new ModelAndView("jsonView");
		try {
			mav.addObject(ConstantBattery.str_data,selectMaterialService.getRecipenumbers(containerOid,materialNumber));
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
	
	@RequestMapping(value="/battery/createPhantom.do")
	public ModelAndView createPhantom(@RequestParam String jsonStr) throws Exception{
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		ModelAndView mav = new ModelAndView("jsonView");
		try {
			selectMaterialService.createPhantom(jsonStr);
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
	
	@RequestMapping(value="/battery/getMaterialPNJson.do")
	public ModelAndView getMaterialPNJson(String containerOid) throws Exception {
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		ModelAndView mav = new ModelAndView("jsonView");
		try {
			mav.addObject(ConstantBattery.str_data,selectMaterialService.getMaterialPNJson(containerOid));
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
	
	@RequestMapping(value="/battery/getRemarkByNumber.do")
	public ModelAndView getRemarkByNumber(String value) throws Exception {
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		ModelAndView mav = new ModelAndView("jsonView");
		try {
			mav.addObject(ConstantBattery.str_data,selectMaterialService.getRemarkByNumber(value));
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
