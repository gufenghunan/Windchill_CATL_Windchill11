package com.catl.ri.riA.controller;

import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import wt.session.SessionServerHelper;

import com.catl.ri.constant.ConstantRI;
import com.catl.ri.riA.service.SelectMaterialService;

/**
 * 选择材料界面请求
 * @author hdong
 *
 */
@Scope("prototype")
@Controller("riASelectMaterialController")
public class SelectMaterialController {
	
	@Resource
	private SelectMaterialService riASelectMaterialService;
	
	@RequestMapping(value="/riA/getRecipeJson.do")
	public ModelAndView getRecipeJson(@RequestParam String recipenumber,String containerOid) throws Exception {
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		ModelAndView mav = new ModelAndView("jsonView");
		try {
			mav.addObject(ConstantRI.str_data,riASelectMaterialService.getRecipeJson(containerOid,recipenumber));
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
	
	@RequestMapping(value="/riA/getSearchJson.do")
	public ModelAndView getSearchJson(@RequestParam String type) throws Exception {
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		ModelAndView mav = new ModelAndView("jsonView");
		try {
			mav.addObject(ConstantRI.str_data,riASelectMaterialService.getSearchJson(type));
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
	
	@RequestMapping(value="/riA/getPartRowJson.do")
	public ModelAndView getPartRowJson(@RequestParam String oid) throws Exception {
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		ModelAndView mav = new ModelAndView("jsonView");
		try {
			mav.addObject(ConstantRI.str_data,riASelectMaterialService.getPartRowJson(oid));
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
	
	@RequestMapping(value="/riA/getRecipenumbers.do")
	public ModelAndView getRecipenumbers(@RequestParam String materialNumber,String containerOid) throws Exception{
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		ModelAndView mav = new ModelAndView("jsonView");
		try {
			mav.addObject(ConstantRI.str_data,riASelectMaterialService.getRecipenumbers(containerOid,materialNumber));
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
	
	@RequestMapping(value="/riA/createPhantom.do")
	public ModelAndView createPhantom(@RequestParam String jsonStr) throws Exception{
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		ModelAndView mav = new ModelAndView("jsonView");
		try {
			riASelectMaterialService.createPhantom(jsonStr);
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
	
	@RequestMapping(value="/riA/getMaterialPNJson.do")
	public ModelAndView getMaterialPNJson(String containerOid) throws Exception {
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		ModelAndView mav = new ModelAndView("jsonView");
		try {
			mav.addObject(ConstantRI.str_data,riASelectMaterialService.getMaterialPNJson(containerOid));
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
	
	@RequestMapping(value="/riA/getRemarkByNumber.do")
	public ModelAndView getRemarkByNumber(String value) throws Exception {
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		ModelAndView mav = new ModelAndView("jsonView");
		try {
			mav.addObject(ConstantRI.str_data,riASelectMaterialService.getRemarkByNumber(value));
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
