package com.catl.ri.riA.controller;

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

import com.catl.ri.constant.ConstantRI;
import com.catl.ri.riA.service.RIService;

/**
 * 电芯设计页面跳转、保存等操作
 * @author hdong
 *
 */
@Scope("prototype")
@Controller("riAController")
public class RIController {
	@Resource
	private RIService riAService;

	@RequestMapping(value="/riA/getDisplayHtml.do")
	public void getDisplayHtml(HttpServletResponse response,@RequestParam String templatename) {
		response.setContentType("text/html;charset=utf-8");
		response.setCharacterEncoding("utf-8");
		try {
			response.getWriter().write(riAService.getDisplayHtml(templatename));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value="/riA/outputDoc.do")
	public ModelAndView outputDoc(@RequestParam String wtDocOid,@RequestParam String description,@RequestParam String name,@RequestParam String level,@RequestParam String remark,@RequestParam String folderOid,String isspeical) throws Exception{
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		ModelAndView mav = new ModelAndView("jsonView");
		try {
			riAService.outputDoc(wtDocOid,description,name,level,remark,folderOid);
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
	

	@RequestMapping(value="/riA/submitForm.do")
	public ModelAndView submitForm(@RequestParam String name,@RequestParam String level,@RequestParam String remark,@RequestParam String jsonstr,@RequestParam String sheetname,@RequestParam String wtDocOid,@RequestParam boolean formcheck) throws Exception{
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		ModelAndView mav = new ModelAndView("jsonView");
		try {
			riAService.submitForm(wtDocOid,name,level,remark,jsonstr,sheetname,formcheck);
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
	
	@RequestMapping(value="/riA/createDesignExcel.do")
	public ModelAndView createDesignExcel(@RequestParam String wtDocOid,@RequestParam String oldname,@RequestParam String oldlevel,@RequestParam String oldremark,@RequestParam String name,@RequestParam String level,@RequestParam String remark) throws Exception{
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		ModelAndView mav = new ModelAndView("jsonView");
		try {
			mav.addObject(ConstantRI.str_success, true);
			mav.addObject(ConstantRI.str_msg, riAService.createDesignExcel(wtDocOid,oldname,oldlevel,oldremark,name, level, remark));
		} catch (Exception e) {
			e.printStackTrace();
			mav.addObject(ConstantRI.str_msg, e.getLocalizedMessage());
			mav.addObject(ConstantRI.str_success, true);
		} finally {
			SessionServerHelper.manager.setAccessEnforced(enforce);
		}
		return mav;
	}
	
	@RequestMapping(value="/riA/getPageJson.do")
	public ModelAndView getPageJson(@RequestParam String sheetname,@RequestParam String name,@RequestParam String level,@RequestParam String remark,@RequestParam String wtDocOid) throws Exception {
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		ModelAndView mav = new ModelAndView("jsonView");
		try {
			mav.addObject(ConstantRI.str_data,riAService.getPageJson(wtDocOid,name, level, remark, sheetname));
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
	
	@RequestMapping(value="/riA/getMathData.do")
	public ModelAndView getMathData(@RequestParam String sheetname,@RequestParam String region,@RequestParam String value,@RequestParam String name,@RequestParam String level,@RequestParam String remark,@RequestParam String wtDocOid) throws Exception {
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		ModelAndView mav = new ModelAndView("jsonView");
		try {
			mav.addObject(ConstantRI.str_data,riAService.getMathData(wtDocOid,region,value,name, level, remark, sheetname));
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
	
	@RequestMapping(value="/riA/updateRI.do")
	public ModelAndView updateRI(@RequestParam String wtDocOid){
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		ModelAndView mav = new ModelAndView("jsonView");
		try {
			mav.addObject(ConstantRI.str_data,riAService.updateRI(wtDocOid));
			mav.addObject(ConstantRI.str_success, true);
			mav.addObject(ConstantRI.str_msg, "");
		} catch (Exception e) {
			mav.addObject(ConstantRI.str_msg, e.getLocalizedMessage());
			mav.addObject(ConstantRI.str_success, true);
		} finally {
			SessionServerHelper.manager.setAccessEnforced(enforce);
		}
		return mav;
	} 
	

	@RequestMapping(value="/riA/saveDesign.do")
	public ModelAndView saveDesign(@RequestParam String wtDocOid,String sheetname,@RequestParam String jsonstr,@RequestParam String name,@RequestParam String level,@RequestParam String remark){
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		ModelAndView mav = new ModelAndView("jsonView");
		try {
			riAService.saveDesign(wtDocOid,sheetname,jsonstr,name,level,remark);
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
	
	@RequestMapping(value="/riA/updatePageJson.do")
	public ModelAndView updatePageJson(@RequestParam String wtDocOid,@RequestParam String jsonstr,@RequestParam String sheetname,@RequestParam String name,@RequestParam String level,@RequestParam String remark){
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		ModelAndView mav = new ModelAndView("jsonView");
		try {
			mav.addObject(ConstantRI.str_data, riAService.updatePageJson(wtDocOid,jsonstr,sheetname,name,level,remark));
			mav.addObject(ConstantRI.str_success, true);
			mav.addObject(ConstantRI.str_msg, "");
		} catch (Exception e) {
			mav.addObject(ConstantRI.str_msg, e.getLocalizedMessage());
			mav.addObject(ConstantRI.str_success, true);
		} finally {
			SessionServerHelper.manager.setAccessEnforced(enforce);
		}
		return mav;
	} 
	
	@RequestMapping(value="/riA/getUserAllFileName.do")
	public ModelAndView getUserAllFileName(){
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		ModelAndView mav = new ModelAndView("jsonView");
		try {
			mav.addObject(ConstantRI.str_data, riAService.getUserAllFileName());
			mav.addObject(ConstantRI.str_success, true);
			mav.addObject(ConstantRI.str_msg, "");
		} catch (Exception e) {
			mav.addObject(ConstantRI.str_msg, e.getLocalizedMessage());
			mav.addObject(ConstantRI.str_success, true);
		} finally {
			SessionServerHelper.manager.setAccessEnforced(enforce);
		}
		return mav;
	}
	
	@RequestMapping(value="/riA/exportSheet.do")
	public ModelAndView exportSheet(@RequestParam String wtDocOid,@RequestParam String name,@RequestParam String level,@RequestParam String remark,String sheetname,HttpServletRequest request,HttpServletResponse response){
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		ModelAndView mav = new ModelAndView("jsonView");
		try {
			riAService.exportSheet(wtDocOid, name, level, remark,sheetname, request, response);
			mav.addObject(ConstantRI.str_data, "");
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
	
	@RequestMapping(value="/riA/getDefaultConfig.do")
	public ModelAndView defaultConfig(@RequestParam String wtDocOid,@RequestParam String name,@RequestParam String level,@RequestParam String remark,@RequestParam String currentValue){
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		ModelAndView mav = new ModelAndView("jsonView");
		try {
			mav.addObject(ConstantRI.str_data, riAService.getDefaultValue(wtDocOid,name,level,remark,currentValue));
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
	
	@RequestMapping(value="/riA/submitvalidateform.do")
	public ModelAndView submitvalidateform(@RequestParam String wtDocOid,@RequestParam String description,@RequestParam String name,@RequestParam String level,@RequestParam String remark,@RequestParam String folderOid,@RequestParam String sheetname,@RequestParam String jsonstr){
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		ModelAndView mav = new ModelAndView("jsonView");
		try {
			riAService.saveDesign(wtDocOid, sheetname, jsonstr, name, level, remark);
			riAService.submitvalidateform(wtDocOid,name,level,remark);
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
	
	@RequestMapping(value="/riA/isSpeicalGroupUser.do")
	public ModelAndView isSpeicalGroupUser(String containerOid){
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		ModelAndView mav = new ModelAndView("jsonView");
		try {
			mav.addObject(ConstantRI.str_success, true);
			mav.addObject(ConstantRI.str_data, riAService.isSpeicalGroupUser(containerOid));
		} catch (Exception e) {
			e.printStackTrace();
			mav.addObject(ConstantRI.str_msg, e.getLocalizedMessage());
			mav.addObject(ConstantRI.str_success, true);
		} finally {
			SessionServerHelper.manager.setAccessEnforced(enforce);
		}
		return mav;
	}
	@RequestMapping(value="/riA/syncTemp.do")
	public ModelAndView syncTemp(@RequestParam String filename){
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		ModelAndView mav = new ModelAndView("jsonView");
		try {
			riAService.syncTemp(filename);
			mav.addObject(ConstantRI.str_success, true);
			mav.addObject(ConstantRI.str_msg,"");
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
