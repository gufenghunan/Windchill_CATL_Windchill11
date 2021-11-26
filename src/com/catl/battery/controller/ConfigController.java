package com.catl.battery.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import wt.session.SessionServerHelper;

import com.catl.battery.constant.ConstantBattery;
import com.catl.battery.service.ConfigService;

/**
 * 管理员配置文件模版
 * @author hdong
 *
 */
@Scope("prototype")
@Controller
public class ConfigController {
	@Resource
	private ConfigService configService;
	@RequestMapping(value="/battery/getTemplate.do")
	public ModelAndView getTemplate() throws Exception{
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		ModelAndView mav = new ModelAndView("jsonView");
		try {
			mav.addObject(ConstantBattery.str_data,configService.getTemplate());
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
	@RequestMapping(value="/battery/downloadTemplate.do")
	public ModelAndView downloadTemplate(String oids) throws Exception{
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		ModelAndView mav = new ModelAndView("jsonView");
		try {
			mav.addObject(ConstantBattery.str_data,configService.downloadTemplate(oids));
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
	
	@RequestMapping(value="/battery/deleteTemplate.do")
	public ModelAndView deleteTemplate(String oids) throws Exception{
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		ModelAndView mav = new ModelAndView("jsonView");
		try {
			configService.deleteTemplate(oids);
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
	
	@RequestMapping(value = "/battery/opTemplate.do")
	public void opTemplate(@RequestParam(required = true) MultipartFile uploadfile, @RequestParam(required = true) String oid,String description,HttpServletResponse response)
			throws Exception {
		       String msg="";
		       try{
		    	   configService.opTemplate(uploadfile,oid,description); 
		       }catch(Exception e){
		    	   msg=e.getLocalizedMessage();
		    	   e.printStackTrace();
		       }
			       response.setContentType("text/html;charset=utf-8");
				   response.setCharacterEncoding("utf-8");
				   response.getWriter().write("{success:true,msg:'"+msg+"'}");
	}
	
	@RequestMapping(value = "/battery/isvalidAdmin.do")
	public ModelAndView isvalidAdmin() throws Exception {
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		ModelAndView mav = new ModelAndView("jsonView", "data",
				configService.isvalidAdmin());
		mav.addObject("success", true);
		SessionServerHelper.manager.setAccessEnforced(enforce);
		return mav;
	}
}
