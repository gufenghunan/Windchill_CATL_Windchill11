package com.catl.pd.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import wt.session.SessionServerHelper;

import com.catl.pd.constant.ConstantPD;
import com.catl.pd.service.ConfigService;

/**
 * 管理员配置文件模版
 * @author hdong
 *
 */
@Scope("prototype")
@Controller("PDConfigController")
public class ConfigController {
	@Resource
	private ConfigService pdConfigService;
	@RequestMapping(value="/pd/getTemplate.do")
	public ModelAndView getTemplate() throws Exception{
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		ModelAndView mav = new ModelAndView("jsonView");
		try {
			mav.addObject(ConstantPD.str_data,pdConfigService.getTemplate());
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
	@RequestMapping(value="/pd/downloadTemplate.do")
	public ModelAndView downloadTemplate(String oids) throws Exception{
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		ModelAndView mav = new ModelAndView("jsonView");
		try {
			mav.addObject(ConstantPD.str_data,pdConfigService.downloadTemplate(oids));
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
	
	@RequestMapping(value="/pd/deleteTemplate.do")
	public ModelAndView deleteTemplate(String oids) throws Exception{
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		ModelAndView mav = new ModelAndView("jsonView");
		try {
			pdConfigService.deleteTemplate(oids);
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
	
	@RequestMapping(value = "/pd/opTemplate.do")
	public void opTemplate(@RequestParam(required = true) MultipartFile uploadfile, @RequestParam(required = true) String oid,String description,HttpServletResponse response)
			throws Exception {
		       String msg="";
		       try{
		    	   pdConfigService.opTemplate(uploadfile,oid,description); 
		       }catch(Exception e){
		    	   msg=e.getLocalizedMessage();
		    	   e.printStackTrace();
		       }
			       response.setContentType("text/html;charset=utf-8");
				   response.setCharacterEncoding("utf-8");
				   response.getWriter().write("{success:true,msg:'"+msg+"'}");
	}
	
	@RequestMapping(value = "/pd/isvalidAdmin.do")
	public ModelAndView isvalidAdmin() throws Exception {
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		ModelAndView mav = new ModelAndView("jsonView", "data",
				pdConfigService.isvalidAdmin());
		mav.addObject("success", true);
		SessionServerHelper.manager.setAccessEnforced(enforce);
		return mav;
	}
}
