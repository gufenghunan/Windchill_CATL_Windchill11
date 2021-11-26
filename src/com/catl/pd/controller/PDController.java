package com.catl.pd.controller;

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

import com.catl.pd.constant.ConstantPD;
import com.catl.pd.service.PDService;

/**
 * 电芯设计页面跳转、保存等操作
 * @author hdong
 *
 */
@Scope("prototype")
@Controller
public class PDController {
	@Resource
	private PDService pdService;

	@RequestMapping(value="/pd/getDisplayHtml.do")
	public void getDisplayHtml(HttpServletResponse response,@RequestParam String templatename) {
		response.setContentType("text/html;charset=utf-8");
		response.setCharacterEncoding("utf-8");
		try {
			response.getWriter().write(pdService.getDisplayHtml(templatename));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value="/pd/outputDoc.do")
	public ModelAndView outputDoc(@RequestParam String wtDocOid,@RequestParam String description,@RequestParam String name,@RequestParam String level,@RequestParam String remark,@RequestParam String folderOid) throws Exception{
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		ModelAndView mav = new ModelAndView("jsonView");
		try {
			pdService.outputDoc(wtDocOid,description,name,level,remark,folderOid);
			mav.addObject(ConstantPD.str_success, true);
			mav.addObject(ConstantPD.str_msg, "");
		} catch (Exception e) {
			e.printStackTrace();
			mav.addObject(ConstantPD.str_msg, e.getLocalizedMessage());
			mav.addObject(ConstantPD.str_success, true);
		} finally {
			SessionServerHelper.manager.setAccessEnforced(enforce);
		}
		return mav;
	}
	

	@RequestMapping(value="/pd/submitForm.do")
	public ModelAndView submitForm(@RequestParam String name,@RequestParam String level,@RequestParam String remark,@RequestParam String jsonstr,@RequestParam String sheetname,@RequestParam String wtDocOid,@RequestParam boolean formcheck) throws Exception{
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		ModelAndView mav = new ModelAndView("jsonView");
		try {
			pdService.submitForm(wtDocOid,name,level,remark,jsonstr,sheetname,formcheck);
			mav.addObject(ConstantPD.str_success, true);
			mav.addObject(ConstantPD.str_msg, "");
		} catch (Exception e) {
			e.printStackTrace();
			mav.addObject(ConstantPD.str_msg, e.getLocalizedMessage());
			mav.addObject(ConstantPD.str_success, true);
		} finally {
			SessionServerHelper.manager.setAccessEnforced(enforce);
		}
		return mav;
	}
	
	@RequestMapping(value="/pd/createDesignExcel.do")
	public ModelAndView createDesignExcel(@RequestParam String wtDocOid,@RequestParam String oldname,@RequestParam String oldlevel,@RequestParam String oldremark,@RequestParam String name,@RequestParam String level,@RequestParam String remark) throws Exception{
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		ModelAndView mav = new ModelAndView("jsonView");
		try {
			mav.addObject(ConstantPD.str_success, true);
			mav.addObject(ConstantPD.str_msg, pdService.createDesignExcel(wtDocOid,oldname,oldlevel,oldremark,name, level, remark));
		} catch (Exception e) {
			e.printStackTrace();
			mav.addObject(ConstantPD.str_msg, e.getLocalizedMessage());
			mav.addObject(ConstantPD.str_success, true);
		} finally {
			SessionServerHelper.manager.setAccessEnforced(enforce);
		}
		return mav;
	}
	
	@RequestMapping(value="/pd/getPageJson.do")
	public ModelAndView getPageJson(@RequestParam String sheetname,@RequestParam String name,@RequestParam String level,@RequestParam String remark,@RequestParam String wtDocOid) throws Exception {
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		ModelAndView mav = new ModelAndView("jsonView");
		try {
			mav.addObject(ConstantPD.str_data,pdService.getPageJson(wtDocOid,name, level, remark, sheetname));
			mav.addObject(ConstantPD.str_success, true);
			mav.addObject(ConstantPD.str_msg, "");
		} catch (Exception e) {
			e.printStackTrace();
			mav.addObject(ConstantPD.str_msg, e.getLocalizedMessage());
			mav.addObject(ConstantPD.str_success, true);
		} finally {
			SessionServerHelper.manager.setAccessEnforced(enforce);
		}
		return mav;
	}
	
	@RequestMapping(value="/pd/getMathData.do")
	public ModelAndView getMathData(@RequestParam String sheetname,@RequestParam String region,@RequestParam String value,@RequestParam String name,@RequestParam String level,@RequestParam String remark,@RequestParam String wtDocOid) throws Exception {
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		ModelAndView mav = new ModelAndView("jsonView");
		try {
			mav.addObject(ConstantPD.str_data,pdService.getMathData(wtDocOid,region,value,name, level, remark, sheetname));
			mav.addObject(ConstantPD.str_success, true);
			mav.addObject(ConstantPD.str_msg, "");
		} catch (Exception e) {
			e.printStackTrace();
			mav.addObject(ConstantPD.str_msg, e.getLocalizedMessage());
			mav.addObject(ConstantPD.str_success, true);
		} finally {
			SessionServerHelper.manager.setAccessEnforced(enforce);
		}
		return mav;
	}
	
	@RequestMapping(value="/pd/updatePD.do")
	public ModelAndView updatePD(@RequestParam String wtDocOid){
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		ModelAndView mav = new ModelAndView("jsonView");
		try {
			mav.addObject(ConstantPD.str_data,pdService.updatePD(wtDocOid));
			mav.addObject(ConstantPD.str_success, true);
			mav.addObject(ConstantPD.str_msg, "");
		} catch (Exception e) {
			mav.addObject(ConstantPD.str_msg, e.getLocalizedMessage());
			mav.addObject(ConstantPD.str_success, true);
		} finally {
			SessionServerHelper.manager.setAccessEnforced(enforce);
		}
		return mav;
	} 
	

	@RequestMapping(value="/pd/saveDesign.do")
	public ModelAndView saveDesign(@RequestParam String wtDocOid,String sheetname,@RequestParam String jsonstr,@RequestParam String name,@RequestParam String level,@RequestParam String remark){
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		ModelAndView mav = new ModelAndView("jsonView");
		try {
			pdService.saveDesign(wtDocOid,sheetname,jsonstr,name,level,remark);
			mav.addObject(ConstantPD.str_success, true);
			mav.addObject(ConstantPD.str_msg, "");
		} catch (Exception e) {
			e.printStackTrace();
			mav.addObject(ConstantPD.str_msg, e.getLocalizedMessage());
			mav.addObject(ConstantPD.str_success, true);
		} finally {
			SessionServerHelper.manager.setAccessEnforced(enforce);
		}
		return mav;
	} 
	
	@RequestMapping(value="/pd/updatePageJson.do")
	public ModelAndView updatePageJson(@RequestParam String wtDocOid,@RequestParam String jsonstr,@RequestParam String sheetname,@RequestParam String name,@RequestParam String level,@RequestParam String remark){
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		ModelAndView mav = new ModelAndView("jsonView");
		try {
			mav.addObject(ConstantPD.str_data, pdService.updatePageJson(wtDocOid,jsonstr,sheetname,name,level,remark));
			mav.addObject(ConstantPD.str_success, true);
			mav.addObject(ConstantPD.str_msg, "");
		} catch (Exception e) {
			mav.addObject(ConstantPD.str_msg, e.getLocalizedMessage());
			mav.addObject(ConstantPD.str_success, true);
		} finally {
			SessionServerHelper.manager.setAccessEnforced(enforce);
		}
		return mav;
	} 
	
	@RequestMapping(value="/pd/getUserAllFileName.do")
	public ModelAndView getUserAllFileName(){
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		ModelAndView mav = new ModelAndView("jsonView");
		try {
			mav.addObject(ConstantPD.str_data, pdService.getUserAllFileName());
			mav.addObject(ConstantPD.str_success, true);
			mav.addObject(ConstantPD.str_msg, "");
		} catch (Exception e) {
			mav.addObject(ConstantPD.str_msg, e.getLocalizedMessage());
			mav.addObject(ConstantPD.str_success, true);
		} finally {
			SessionServerHelper.manager.setAccessEnforced(enforce);
		}
		return mav;
	}
	
	@RequestMapping(value="/pd/exportConf.do")
	public ModelAndView exportIfConf(@RequestParam String wtDocOid,@RequestParam String name,@RequestParam String level,@RequestParam String remark,HttpServletRequest request,HttpServletResponse response){
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		ModelAndView mav = new ModelAndView("jsonView");
		try {
			pdService.exportIfConf(wtDocOid, name, level, remark, request, response);
			mav.addObject(ConstantPD.str_data, "");
			mav.addObject(ConstantPD.str_success, true);
			mav.addObject(ConstantPD.str_msg, "");
		} catch (Exception e) {
			e.printStackTrace();
			mav.addObject(ConstantPD.str_msg, e.getLocalizedMessage());
			mav.addObject(ConstantPD.str_success, true);
		} finally {
			SessionServerHelper.manager.setAccessEnforced(enforce);
		}
		return mav;
	}
	
	@RequestMapping(value="/pd/uploadConf.do")
	public void exportIfConf(@RequestParam(required = true) MultipartFile uploadfile,@RequestParam String wtDocOid,@RequestParam String name,@RequestParam String level,@RequestParam String remark,HttpServletResponse response) throws Exception{
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		String msg="";
		try {
			pdService.importIfConf(uploadfile, wtDocOid, name, level, remark);
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
	
	@RequestMapping(value="/pd/getDefaultConfig.do")
	public ModelAndView defaultConfig(@RequestParam String wtDocOid,@RequestParam String name,@RequestParam String level,@RequestParam String remark,@RequestParam String currentValue){
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		ModelAndView mav = new ModelAndView("jsonView");
		try {
			mav.addObject(ConstantPD.str_data, pdService.getDefaultValue(wtDocOid,name,level,remark,currentValue));
			mav.addObject(ConstantPD.str_success, true);
			mav.addObject(ConstantPD.str_msg, "");
		} catch (Exception e) {
			e.printStackTrace();
			mav.addObject(ConstantPD.str_msg, e.getLocalizedMessage());
			mav.addObject(ConstantPD.str_success, true);
		} finally {
			SessionServerHelper.manager.setAccessEnforced(enforce);
		}
		return mav;
	}
	
	@RequestMapping(value="/pd/submitvalidateform.do")
	public ModelAndView submitvalidateform(@RequestParam String wtDocOid,@RequestParam String description,@RequestParam String name,@RequestParam String level,@RequestParam String remark,@RequestParam String folderOid,@RequestParam String sheetname,@RequestParam String jsonstr){
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		ModelAndView mav = new ModelAndView("jsonView");
		try {
			pdService.saveDesign(wtDocOid, sheetname, jsonstr, name, level, remark);
			pdService.submitvalidateform(wtDocOid,name,level,remark);
			mav.addObject(ConstantPD.str_success, true);
			mav.addObject(ConstantPD.str_msg, "");
		} catch (Exception e) {
			e.printStackTrace();
			mav.addObject(ConstantPD.str_msg, e.getLocalizedMessage());
			mav.addObject(ConstantPD.str_success, true);
		} finally {
			SessionServerHelper.manager.setAccessEnforced(enforce);
		}
		return mav;
	}
}
