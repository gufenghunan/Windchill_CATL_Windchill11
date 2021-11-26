package com.catl.ri.riB.controller;

import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import wt.session.SessionServerHelper;

import com.catl.ri.constant.ConstantRI;
import com.catl.ri.riB.service.SelectMaterialService;

/**
 * 选择材料界面请求
 * @author hdong
 *
 */
@Scope("prototype")
@Controller("riBSelectMaterialController")
public class SelectMaterialController {
	
	@Resource
	private SelectMaterialService riBSelectMaterialService;
	
	@RequestMapping(value="/riB/getRecipeJson.do")
	public ModelAndView getRecipeJson(@RequestParam String recipenumber,String containerOid) throws Exception {
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		ModelAndView mav = new ModelAndView("jsonView");
		try {
			mav.addObject(ConstantRI.str_data,riBSelectMaterialService.getRecipeJson(containerOid,recipenumber));
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
	
	@RequestMapping(value="/riB/getSearchJson.do")
	public ModelAndView getSearchJson(@RequestParam String type) throws Exception {
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		ModelAndView mav = new ModelAndView("jsonView");
		try {
			mav.addObject(ConstantRI.str_data,riBSelectMaterialService.getSearchJson(type));
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
	
	@RequestMapping(value="/riB/getPartRowJson.do")
	public ModelAndView getPartRowJson(@RequestParam String oid) throws Exception {
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		ModelAndView mav = new ModelAndView("jsonView");
		try {
			mav.addObject(ConstantRI.str_data,riBSelectMaterialService.getPartRowJson(oid));
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
	
	@RequestMapping(value="/riB/getRecipenumbers.do")
	public ModelAndView getRecipenumbers(@RequestParam String materialNumber,String containerOid) throws Exception{
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		ModelAndView mav = new ModelAndView("jsonView");
		try {
			mav.addObject(ConstantRI.str_data,riBSelectMaterialService.getRecipenumbers(containerOid,materialNumber));
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
	
	@RequestMapping(value="/riB/createPhantom.do")
	public ModelAndView createPhantom(@RequestParam String jsonStr) throws Exception{
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		ModelAndView mav = new ModelAndView("jsonView");
		try {
			riBSelectMaterialService.createPhantom(jsonStr);
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
	
	@RequestMapping(value="/riB/getMaterialPNJson.do")
	public ModelAndView getMaterialPNJson(String containerOid) throws Exception {
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		ModelAndView mav = new ModelAndView("jsonView");
		try {
			mav.addObject(ConstantRI.str_data,riBSelectMaterialService.getMaterialPNJson(containerOid));
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
	
	@RequestMapping(value="/riB/getRemarkByNumber.do")
	public ModelAndView getRemarkByNumber(String value) throws Exception {
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		ModelAndView mav = new ModelAndView("jsonView");
		try {
			mav.addObject(ConstantRI.str_data,riBSelectMaterialService.getRemarkByNumber(value));
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
