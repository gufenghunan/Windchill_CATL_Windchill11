package com.catl.battery.controller;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import wt.session.SessionServerHelper;

import com.catl.battery.constant.ConstantBattery;
import com.catl.battery.service.BatteryService;

/**
 * 电芯设计页面跳转、保存等操作
 * @author hdong
 *
 */
@Scope("prototype")
@Controller
public class BatteryController {
	@Resource
	private BatteryService batteryService;

	@RequestMapping(value="/battery/getDisplayHtml.do")
	public void getDisplayHtml(HttpServletResponse response,@RequestParam String templatename) {
		response.setContentType("text/html;charset=utf-8");
		response.setCharacterEncoding("utf-8");
		try {
			response.getWriter().write(batteryService.getDisplayHtml(templatename));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value="/battery/outputDoc.do")
	public ModelAndView outputDoc(@RequestParam String wtDocOid,@RequestParam String description,@RequestParam String name,@RequestParam String level,@RequestParam String remark,@RequestParam String folderOid) throws Exception{
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		ModelAndView mav = new ModelAndView("jsonView");
		try {
			batteryService.outputDoc(wtDocOid,description,name,level,remark,folderOid);
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
	

	@RequestMapping(value="/battery/submitForm.do")
	public ModelAndView submitForm(@RequestParam String name,@RequestParam String level,@RequestParam String remark,@RequestParam String jsonstr,@RequestParam String sheetname,@RequestParam String wtDocOid,@RequestParam boolean formcheck) throws Exception{
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		ModelAndView mav = new ModelAndView("jsonView");
		try {
			batteryService.submitForm(wtDocOid,name,level,remark,jsonstr,sheetname,formcheck);
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
	
	@RequestMapping(value="/battery/createDesignExcel.do")
	public ModelAndView createDesignExcel(@RequestParam String wtDocOid,@RequestParam String oldname,@RequestParam String oldlevel,@RequestParam String oldremark,@RequestParam String name,@RequestParam String level,@RequestParam String remark) throws Exception{
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		ModelAndView mav = new ModelAndView("jsonView");
		try {
			mav.addObject(ConstantBattery.str_success, true);
			mav.addObject(ConstantBattery.str_msg, batteryService.createDesignExcel(wtDocOid,oldname,oldlevel,oldremark,name, level, remark));
		} catch (Exception e) {
			e.printStackTrace();
			mav.addObject(ConstantBattery.str_msg, e.getLocalizedMessage());
			mav.addObject(ConstantBattery.str_success, true);
		} finally {
			SessionServerHelper.manager.setAccessEnforced(enforce);
		}
		return mav;
	}
	
	@RequestMapping(value="/battery/getPageJson.do")
	public ModelAndView getPageJson(@RequestParam String sheetname,@RequestParam String name,@RequestParam String level,@RequestParam String remark,@RequestParam String wtDocOid) throws Exception {
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		ModelAndView mav = new ModelAndView("jsonView");
		try {
			mav.addObject(ConstantBattery.str_data,batteryService.getPageJson(wtDocOid,name, level, remark, sheetname));
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
	
	@RequestMapping(value="/battery/getMathData.do")
	public ModelAndView getMathData(@RequestParam String sheetname,@RequestParam String region,@RequestParam String value,@RequestParam String name,@RequestParam String level,@RequestParam String remark,@RequestParam String wtDocOid) throws Exception {
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		ModelAndView mav = new ModelAndView("jsonView");
		try {
			mav.addObject(ConstantBattery.str_data,batteryService.getMathData(wtDocOid,region,value,name, level, remark, sheetname));
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
	
	@RequestMapping(value="/battery/updateBattery.do")
	public ModelAndView updateBattery(@RequestParam String wtDocOid){
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		ModelAndView mav = new ModelAndView("jsonView");
		try {
			mav.addObject(ConstantBattery.str_data,batteryService.updateBattery(wtDocOid));
			mav.addObject(ConstantBattery.str_success, true);
			mav.addObject(ConstantBattery.str_msg, "");
		} catch (Exception e) {
			mav.addObject(ConstantBattery.str_msg, e.getLocalizedMessage());
			mav.addObject(ConstantBattery.str_success, true);
		} finally {
			SessionServerHelper.manager.setAccessEnforced(enforce);
		}
		return mav;
	} 
	

	@RequestMapping(value="/battery/saveDesign.do")
	public ModelAndView saveDesign(@RequestParam String wtDocOid,String sheetname,@RequestParam String jsonstr,@RequestParam String name,@RequestParam String level,@RequestParam String remark){
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		ModelAndView mav = new ModelAndView("jsonView");
		try {
			batteryService.saveDesign(wtDocOid,sheetname,jsonstr,name,level,remark);
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
	
	@RequestMapping(value="/battery/updatePageJson.do")
	public ModelAndView updatePageJson(@RequestParam String wtDocOid,@RequestParam String jsonstr,@RequestParam String sheetname,@RequestParam String name,@RequestParam String level,@RequestParam String remark){
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		ModelAndView mav = new ModelAndView("jsonView");
		try {
			mav.addObject(ConstantBattery.str_data, batteryService.updatePageJson(wtDocOid,jsonstr,sheetname,name,level,remark));
			mav.addObject(ConstantBattery.str_success, true);
			mav.addObject(ConstantBattery.str_msg, "");
		} catch (Exception e) {
			mav.addObject(ConstantBattery.str_msg, e.getLocalizedMessage());
			mav.addObject(ConstantBattery.str_success, true);
		} finally {
			SessionServerHelper.manager.setAccessEnforced(enforce);
		}
		return mav;
	} 
	
	@RequestMapping(value="/battery/getUserAllFileName.do")
	public ModelAndView getUserAllFileName(){
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		ModelAndView mav = new ModelAndView("jsonView");
		try {
			mav.addObject(ConstantBattery.str_data, batteryService.getUserAllFileName());
			mav.addObject(ConstantBattery.str_success, true);
			mav.addObject(ConstantBattery.str_msg, "");
		} catch (Exception e) {
			mav.addObject(ConstantBattery.str_msg, e.getLocalizedMessage());
			mav.addObject(ConstantBattery.str_success, true);
		} finally {
			SessionServerHelper.manager.setAccessEnforced(enforce);
		}
		return mav;
	}
	
	@RequestMapping(value="/battery/exportConf.do")
	public ModelAndView exportIfConf(@RequestParam String wtDocOid,@RequestParam String name,@RequestParam String level,@RequestParam String remark,HttpServletRequest request,HttpServletResponse response){
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		ModelAndView mav = new ModelAndView("jsonView");
		try {
			batteryService.exportIfConf(wtDocOid, name, level, remark, request, response);
			mav.addObject(ConstantBattery.str_data, "");
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
	
	@RequestMapping(value="/battery/uploadConf.do")
	public void exportIfConf(@RequestParam(required = true) MultipartFile uploadfile,@RequestParam String wtDocOid,@RequestParam String name,@RequestParam String level,@RequestParam String remark,HttpServletResponse response) throws Exception{
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		String msg="";
		try {
			batteryService.importIfConf(uploadfile, wtDocOid, name, level, remark);
		} catch (Exception e) {
			msg=e.getLocalizedMessage();
			e.printStackTrace();
		} finally {
			SessionServerHelper.manager.setAccessEnforced(enforce);
		}
		response.setContentType("text/html;charset=utf-8");
	    response.setCharacterEncoding("utf-8");
	    response.getWriter().write("{success:true,msg:'"+msg+"'}");
	}
	
	@RequestMapping(value="/battery/getDefaultConfig.do")
	public ModelAndView defaultConfig(@RequestParam String wtDocOid,@RequestParam String name,@RequestParam String level,@RequestParam String remark,@RequestParam String currentValue){
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		ModelAndView mav = new ModelAndView("jsonView");
		try {
			mav.addObject(ConstantBattery.str_data, batteryService.getDefaultValue(wtDocOid,name,level,remark,currentValue));
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
	
	@RequestMapping(value="/battery/submitvalidateform.do")
	public ModelAndView submitvalidateform(@RequestParam String wtDocOid,@RequestParam String description,@RequestParam String name,@RequestParam String level,@RequestParam String remark,@RequestParam String folderOid,@RequestParam String sheetname,@RequestParam String jsonstr){
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		ModelAndView mav = new ModelAndView("jsonView");
		try {
			batteryService.saveDesign(wtDocOid, sheetname, jsonstr, name, level, remark);
			batteryService.submitvalidateform(wtDocOid,name,level,remark);
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
	
	@RequestMapping(value="/battery/syncTemp.do")
	public ModelAndView syncTemp(@RequestParam String filename){
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		ModelAndView mav = new ModelAndView("jsonView");
		try {
			batteryService.syncTemp(filename);
			mav.addObject(ConstantBattery.str_success, true);
			mav.addObject(ConstantBattery.str_msg,"");
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
