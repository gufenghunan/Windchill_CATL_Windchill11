package com.catl.line.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.catl.line.constant.ConstantLine;
import com.catl.line.service.ParentPNService;

import wt.pom.PersistenceException;
import wt.session.SessionServerHelper;

@Controller
public class PNController {
	@Resource
	private ParentPNService parentpnservice;

	/**
	 * 获取所有母PN
	 * 
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "/line/getParentPNs.do")
	public void getParentPNs(HttpServletResponse response) throws Exception {
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		try {
			response.setContentType("text/html;charset=utf-8");
			response.setCharacterEncoding("utf-8");
			response.getWriter().write(
					parentpnservice.getParentPNs().toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			SessionServerHelper.manager.setAccessEnforced(enforce);
		}

	}

	/**
	 * 获取线束总成PN 返回错误信息
	 * 
	 * @param number
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/line/deletePN.do")
	public ModelAndView deletePN(@RequestParam String number) throws Exception {
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		ModelAndView mav = new ModelAndView("jsonView");
		try {
			parentpnservice.deletePN(number);
			mav.addObject(ConstantLine.str_success, true);
			mav.addObject(ConstantLine.str_msg, "");
		} catch (Exception e) {
			mav.addObject(ConstantLine.str_msg, e.getLocalizedMessage());
			mav.addObject(ConstantLine.str_success, true);
		} finally {
			SessionServerHelper.manager.setAccessEnforced(enforce);
		}
		return mav;
	}

	/**
	 * 获取线束总成PN 返回错误信息
	 * 
	 * @param number
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/line/getAsmPN.do")
	public ModelAndView getAsmPN(@RequestParam String number) throws Exception {
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		ModelAndView mav = new ModelAndView("jsonView");
		try {
			mav.addObject(ConstantLine.str_msg,
					parentpnservice.getAsmPN(number));
			mav.addObject(ConstantLine.str_success, true);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			SessionServerHelper.manager.setAccessEnforced(enforce);
		}
		return mav;
	}

	/**
	 * 获取线束母PN的箱体描述信息
	 * 
	 * @param coltype
	 * @param linetype
	 * @param response
	 * @param key
	 * @param value
	 * @throws Exception
	 */
	@RequestMapping(value = "/line/getTagBoxDesc.do")
	public void getTagBoxDesc(@RequestParam String coltype,
			@RequestParam String linetype, HttpServletResponse response,
			String key, String value) throws Exception {
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		try {
			response.setContentType("text/html;charset=utf-8");
			response.setCharacterEncoding("utf-8");
			response.getWriter().write(
					parentpnservice
							.getTagBoxDesc(coltype, linetype, key, value)
							.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			SessionServerHelper.manager.setAccessEnforced(enforce);
		}

	}

	/**
	 * 预览母PN的pdf文件
	 * 
	 * @param number
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/line/getViewPDF.do")
	public ModelAndView getViewPDF(@RequestParam String number)
			throws Exception {
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		ModelAndView mav = new ModelAndView("jsonView");
		try {
			mav.addObject(ConstantLine.str_url,
					parentpnservice.getViewPDF(number));
			mav.addObject(ConstantLine.str_success, true);
		} catch (Exception e) {
			mav.addObject(ConstantLine.str_msg, e.getLocalizedMessage());
			mav.addObject(ConstantLine.str_success, true);
		} finally {
			SessionServerHelper.manager.setAccessEnforced(enforce);
		}
		return mav;
	}

	/**
	 * 预览母PN的pdf文件
	 * 
	 * @param number
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/line/downloadDwg.do")
	public ModelAndView downloadDwg(@RequestParam String number)
			throws Exception {
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		ModelAndView mav = new ModelAndView("jsonView");
		try {
			mav.addObject(ConstantLine.str_url,
					parentpnservice.downloadDwg(number));
			mav.addObject(ConstantLine.str_success, true);
		} catch (Exception e) {
			mav.addObject(ConstantLine.str_msg, e.getLocalizedMessage());
			mav.addObject(ConstantLine.str_success, true);
		} finally {
			SessionServerHelper.manager.setAccessEnforced(enforce);
		}
		return mav;
	}

	/**
	 * 获取同母PN的衍生PN
	 * 
	 * @param response
	 * @param number
	 * @throws Exception
	 */
	@RequestMapping(value = "/line/getChildPNs.do")
	public void getChildPNs(HttpServletResponse response,
			@RequestParam String number,String asmnumber) throws Exception {
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		try {
			response.setContentType("text/html;charset=utf-8");
			response.setCharacterEncoding("utf-8");
			response.getWriter().write(
					parentpnservice.getChildPNs(number).toString());
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			SessionServerHelper.manager.setAccessEnforced(enforce);
		}
	}

	/**
	 * 获取衍生PN信息
	 * 
	 * @param oid
	 * @throws Exception
	 */
	@RequestMapping(value = "/line/getChildPN.do")
	public ModelAndView getChildPN(@RequestParam String oid) throws Exception {
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		ModelAndView mav = new ModelAndView("jsonView");
		try {
			mav.addObject(ConstantLine.str_data,
					parentpnservice.getChildPN(oid));
			mav.addObject(ConstantLine.str_msg, "");
			mav.addObject(ConstantLine.str_success, true);
		} catch (Exception e) {
			e.printStackTrace();
			mav.addObject(ConstantLine.str_msg, e.getLocalizedMessage());
			mav.addObject(ConstantLine.str_success, true);
		} finally {
			SessionServerHelper.manager.setAccessEnforced(enforce);
		}
		return mav;
	}

	/**
	 * 获取衍母PN信息
	 * 
	 * @param oid
	 * @throws Exception
	 */
	@RequestMapping(value = "/line/getMPN.do")
	public ModelAndView getMPN(@RequestParam String oid) throws Exception {
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		ModelAndView mav = new ModelAndView("jsonView");
		try {
			mav.addObject(ConstantLine.str_data, parentpnservice.getMPN(oid));
			mav.addObject(ConstantLine.str_msg, "");
			mav.addObject(ConstantLine.str_success, true);
		} catch (Exception e) {
			e.printStackTrace();
			mav.addObject(ConstantLine.str_msg, e.getLocalizedMessage());
			mav.addObject(ConstantLine.str_success, true);
		} finally {
			SessionServerHelper.manager.setAccessEnforced(enforce);
		}
		return mav;
	}

	/**
	 * 验证衍生PN是否存在
	 * 
	 * @param number
	 * @param values
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/line/validateChildPN.do")
	public ModelAndView validateChildPN(@RequestParam String number,
			@RequestParam String values) throws Exception {
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		ModelAndView mav = new ModelAndView("jsonView");
		try {
			mav.addObject(ConstantLine.str_msg,
					parentpnservice.validateChildPN(number, values));
			mav.addObject(ConstantLine.str_success, true);
		} catch (Exception e) {
			mav.addObject(ConstantLine.str_msg, e.getLocalizedMessage());
			mav.addObject(ConstantLine.str_success, true);
		} finally {
			SessionServerHelper.manager.setAccessEnforced(enforce);
		}
		return mav;
	}

	/**
	 * 创建衍生PN
	 * 
	 * @param number
	 * @param values
	 * @param containeroid
	 * @param folderoid
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/line/createChildPN.do")
	public ModelAndView createChildPN(String asmnumber,@RequestParam String number,
			@RequestParam String values, @RequestParam String containeroid,
			@RequestParam String folderoid) throws Exception {
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		ModelAndView mav = new ModelAndView("jsonView");
		try {
			String newnum = parentpnservice.createChildPN(asmnumber,number, values,
					containeroid, folderoid);
			mav.addObject(ConstantLine.str_newnum, newnum);
			mav.addObject(ConstantLine.str_msg, "");
			mav.addObject(ConstantLine.str_success, true);
		} catch (Exception e) {
			e.printStackTrace();
			mav.addObject(ConstantLine.str_msg, e.getLocalizedMessage());
			mav.addObject(ConstantLine.str_success, true);
		} finally {
			SessionServerHelper.manager.setAccessEnforced(enforce);
		}
		return mav;
	}

	/**
	 * 关联线束总成PN和衍生PN
	 * 
	 * @param parentnumber
	 * @param childnumber
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/line/relativePN.do")
	public ModelAndView relativePN(@RequestParam String parentnumber,
			@RequestParam String childnumber) throws Exception {
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		ModelAndView mav = new ModelAndView("jsonView");
		try {
			parentpnservice.relativePN(parentnumber, childnumber);
			mav.addObject(ConstantLine.str_success, true);
			mav.addObject(ConstantLine.str_msg, "");
		} catch (Exception e) {
			mav.addObject(ConstantLine.str_msg, e.getLocalizedMessage());
			mav.addObject(ConstantLine.str_success, true);
		} finally {
			SessionServerHelper.manager.setAccessEnforced(enforce);
		}
		return mav;
	}

	/**
	 * 更新衍生PN
	 * 
	 * @param number
	 * @param values
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/line/updateChildPN.do")
	public ModelAndView createChildPN(String asmnumber,@RequestParam String oid,
			@RequestParam String values) throws Exception {
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		ModelAndView mav = new ModelAndView("jsonView");
		try {
			String newnum = parentpnservice.updateChildPN(asmnumber,oid, values);
			mav.addObject(ConstantLine.str_newnum, newnum);
			mav.addObject(ConstantLine.str_msg, "");
			mav.addObject(ConstantLine.str_success, true);
		} catch (Exception e) {
			e.printStackTrace();
			String msg = e.getMessage();
			if (msg != null && msg.length() > 100) {
				msg = msg.substring(0, 100) + "......";
			}
			mav.addObject(ConstantLine.str_msg, msg);
			mav.addObject(ConstantLine.str_success, true);
		} finally {
			SessionServerHelper.manager.setAccessEnforced(enforce);
		}
		return mav;
	}

	/**
	 * 上传dwg文件并更新文档主内容
	 * 
	 * @param uploadfile
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "/line/uploaddwg.do")
	public void uploadtype(
			@RequestParam(required = true) MultipartFile uploadfile,
			@RequestParam String number, HttpServletResponse response)
			throws Exception {
		String msg = "";
		try {
			parentpnservice.uploaddwg(uploadfile, number);
		} catch (Exception e) {
			msg = e.getLocalizedMessage();
		}
		response.setContentType("text/html;charset=utf-8");
		response.setCharacterEncoding("utf-8");
		response.getWriter().write("{success:true,msg:'" + msg + "'}");
	}

	/**
	 * 更新iba属性值
	 * 
	 * @param uploadibavalue
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/line/updateibavalue.do")
	public ModelAndView updateibavalue(
			@RequestParam(required = true) String partNumber,
			@RequestParam String name, @RequestParam String value)
			throws Exception {
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		ModelAndView mav = new ModelAndView("jsonView");
		try {
			parentpnservice.updateibavalue(partNumber, name, value);
			mav.addObject(ConstantLine.str_msg, "");
			mav.addObject(ConstantLine.str_success, true);
		} catch (Exception e) {
			e.printStackTrace();
			mav.addObject(ConstantLine.str_msg, e.getLocalizedMessage());
			mav.addObject(ConstantLine.str_success, true);
		} finally {
			SessionServerHelper.manager.setAccessEnforced(enforce);
		}
		return mav;
	}
	

	/**
	 * 获取推荐长度
	 */
	@RequestMapping(value = "/line/retireRecommendL.do")
	public ModelAndView retireRecommendL(@RequestParam String minl,@RequestParam String maxl,String currentl) throws Exception {
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		ModelAndView mav = new ModelAndView("jsonView");
		try {
			mav.addObject(ConstantLine.str_data, parentpnservice.retireRecommendL(minl,maxl,currentl));
			mav.addObject(ConstantLine.str_msg, "");
			mav.addObject(ConstantLine.str_success, true);
		} catch (Exception e) {
			e.printStackTrace();
			mav.addObject(ConstantLine.str_msg, e.getLocalizedMessage());
			mav.addObject(ConstantLine.str_success, true);
		} finally {
			SessionServerHelper.manager.setAccessEnforced(enforce);
		}
		return mav;
	}
	
	/**
	 * 批量创建衍生PN
	 */
	@RequestMapping(value = "/line/batchCreateOrUpdateChildPN.do")
	public void batchCreateOrUpdateChildPN(@RequestParam(required = true) MultipartFile uploadfile,@RequestParam(required = true) String containeroid,@RequestParam(required = true) String folderoid,@RequestParam(required = true) String asmnumber, @RequestParam(required = true) String type,HttpServletResponse response) throws Exception {
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		String msg = "";
		try {
			parentpnservice.batchCreateOrUpdateChildPN(containeroid, folderoid, asmnumber, uploadfile, type);
		} catch (Exception e) {
			e.printStackTrace();
			msg = e.getLocalizedMessage();
		}
		SessionServerHelper.manager.setAccessEnforced(enforce);
		response.setContentType("text/html;charset=utf-8");
		response.setCharacterEncoding("utf-8");
		if(!StringUtils.isEmpty(msg)){
			   if(msg.length()>100){
		        	msg=msg.substring(0,100)+"...";
		        }
				msg=msg.replace("\n", "");
		}
		String str="{success:true,msg:'" + msg + "'}";
		response.getWriter().write(str);
		try{
			JSONObject.fromObject(str);
		}catch(Exception e){
			response.getWriter().write("{success:true,msg:'出线错误，请联系管理员!'}");
		}
		
		
	}
}
