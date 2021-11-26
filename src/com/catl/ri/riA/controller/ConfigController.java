package com.catl.ri.riA.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import wt.session.SessionServerHelper;

import com.catl.ri.constant.ConstantRI;
import com.catl.ri.riA.service.ConfigService;

/**
 * 管理员配置文件模版
 * @author hdong
 *
 */
@Scope("prototype")
@Controller("riAConfigController")
public class ConfigController {
	@Resource
	private ConfigService riAConfigService;
	@RequestMapping(value="/riA/getTemplate.do")
	public ModelAndView getTemplate() throws Exception{
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		ModelAndView mav = new ModelAndView("jsonView");
		try {
			mav.addObject(ConstantRI.str_data,riAConfigService.getTemplate());
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
	
	@RequestMapping(value="/riA/downloadTemplate.do")
	public ModelAndView downloadTemplate(String oids) throws Exception{
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		ModelAndView mav = new ModelAndView("jsonView");
		try {
			mav.addObject(ConstantRI.str_data,riAConfigService.downloadTemplate(oids));
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
	
	@RequestMapping(value="/riA/deleteTemplate.do")
	public ModelAndView deleteTemplate(String oids) throws Exception{
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		ModelAndView mav = new ModelAndView("jsonView");
		try {
			riAConfigService.deleteTemplate(oids);
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
	
	@RequestMapping(value = "/riA/opTemplate.do")
	public void opTemplate(@RequestParam(required = true) MultipartFile uploadfile, @RequestParam(required = true) String oid,String description,HttpServletResponse response)
			throws Exception {
		       String msg="";
		       try{
		    	   riAConfigService.opTemplate(uploadfile,oid,description); 
		       }catch(Exception e){
		    	   msg=e.getLocalizedMessage();
		    	   e.printStackTrace();
		       }
			       response.setContentType("text/html;charset=utf-8");
				   response.setCharacterEncoding("utf-8");
				   response.getWriter().write("{success:true,msg:'"+msg+"'}");
	}
	
	@RequestMapping(value = "/riA/isvalidAdmin.do")
	public ModelAndView isvalidAdmin() throws Exception {
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		ModelAndView mav = new ModelAndView("jsonView", "data",
				riAConfigService.isvalidAdmin());
		mav.addObject("success", true);
		SessionServerHelper.manager.setAccessEnforced(enforce);
		return mav;
	}
}
