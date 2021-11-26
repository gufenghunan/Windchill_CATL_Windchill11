package com.catl.line.controller;

import java.io.IOException;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.catl.line.constant.ConstantLine;
import com.catl.line.service.MultiService;

import net.sf.json.JSONArray;
import wt.session.SessionServerHelper;

@Controller
public class MultiController {
	@Resource
	private MultiService multiservice;
	
	/**
	 * 上传压缩文件
	 * @param uploadfile
	 * @param type  类型(对应临时文件夹下存放路径)
	 * @param allowtype(允许压缩文件中存在的文件类型)
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "/line/uploadzip.do")
	public void uploadzip(@RequestParam(required = true) MultipartFile uploadfile, @RequestParam(required = true) String type, @RequestParam(required = true) String allowtype,HttpServletResponse response)
			throws Exception {
		       String msg="";
		       try{
		    	   msg=multiservice.uploadzip(uploadfile,type,allowtype).toString(); 
		       }catch(Exception e){
		    	   msg=e.getLocalizedMessage();
		    	   e.printStackTrace();
		       }
			       response.setContentType("text/html;charset=utf-8");
				   response.setCharacterEncoding("utf-8");
				   response.getWriter().write("{success:true,msg:'"+msg+"'}");
	}
	
	/**
	 * 批量创建文档
	 * @param docinfo
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/line/createDoc.do")
	public ModelAndView createDoc(@RequestParam String docinfo) throws IOException{
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		ModelAndView mav = new ModelAndView("jsonView");
		try {
			multiservice.createDoc(docinfo);
			mav.addObject(ConstantLine.str_msg, "");
			mav.addObject(ConstantLine.str_success, true);
		} catch (Exception e) {
			e.printStackTrace();
			mav.addObject(ConstantLine.str_msg,"错误:"+e.getLocalizedMessage());
			mav.addObject(ConstantLine.str_success, true);
		} finally {
			SessionServerHelper.manager.setAccessEnforced(enforce);
		}
		return mav;
	}
   
	/**
	 * 上传附件
	 * @param uploadfile
	 * @param type
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "/line/uploadattach.do")
	public void uploadattach(@RequestParam(required = true) MultipartFile uploadfile,@RequestParam(required = true) String type,HttpServletResponse response)
			throws Exception {
		       String filename="";
		       try{
		    	   filename=multiservice.uploadattach(uploadfile,type).toString(); 
		       }catch(Exception e){
		    	   e.printStackTrace();
		       }
			       response.setContentType("text/html;charset=utf-8");
				   response.setCharacterEncoding("utf-8");
				   response.getWriter().write("{success:true,filename:'"+filename+"'}");
	}
	@RequestMapping(value = "/line/validateAttachContainer.do")
	public ModelAndView validateAttachContainer(@RequestParam(required = true) String containerOid){
			boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
			ModelAndView mav = new ModelAndView("jsonView");
			try {
				mav.addObject(ConstantLine.str_msg, multiservice.validateAttachContainer(containerOid));
				mav.addObject(ConstantLine.str_success, true);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				SessionServerHelper.manager.setAccessEnforced(enforce);
			}
			return mav;
	}
	
	/**
	 * 创建多个PN
	 * @param jsonstr
	 * @param uploadtype
	 * @param type
	 * @param folderOid
	 * @param containerOid
	 * @return
	 */
	@RequestMapping(value = "/line/createMultiPart.do")
	public ModelAndView createMultiPart(@RequestParam(required = true) String jsonstr,@RequestParam(required = true) String uploadtype,@RequestParam(required = true) String type,@RequestParam(required = true) String folderOid,@RequestParam(required = true) String containerOid){
			boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
			ModelAndView mav = new ModelAndView("jsonView");
			try {
				multiservice.createMultiPart(jsonstr,type,uploadtype,folderOid,containerOid);
				mav.addObject(ConstantLine.str_msg, "");
				mav.addObject(ConstantLine.str_success, true);
			} catch (Exception e) {
				e.printStackTrace();
				mav.addObject(ConstantLine.str_msg,e.getLocalizedMessage());
				mav.addObject(ConstantLine.str_success, true);
			} finally {
				SessionServerHelper.manager.setAccessEnforced(enforce);
			}
			return mav;
	}
	/**
	 * 解析创建多个PN的分类json数据
	 * @param type
	 * @param clfnode
	 * @param clfjson
	 * @param jsonstr
	 * @param folderOid
	 * @param containerOid
	 * @return
	 */
	@RequestMapping(value = "/line/parseClfAttributes.do")
	public ModelAndView parseClfAttributes(String type,String clfnode,String clfjson,@RequestParam(required = true) String jsonstr,@RequestParam(required = true) String folderOid,@RequestParam(required = true) String containerOid){
			boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
			ModelAndView mav = new ModelAndView("jsonView");
			try {
				mav.addObject(ConstantLine.str_data, multiservice.parseClfAttributes(jsonstr,type,clfnode,clfjson,folderOid,containerOid));
				mav.addObject(ConstantLine.str_success, true);
			} catch (Exception e) {
				e.printStackTrace();
				mav.addObject(ConstantLine.str_msg,e.getLocalizedMessage());
			} finally {
				SessionServerHelper.manager.setAccessEnforced(enforce);
			}
			return mav;
	}
	
	/**
	 * 获取来源的下拉值数据
	 * @return
	 */
	@RequestMapping(value = "/line/querySources.do")
	public ModelAndView querySources(){
			boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
			ModelAndView mav = new ModelAndView("jsonView");
			try {
				mav.addObject(ConstantLine.str_data, multiservice.querySources());
				mav.addObject(ConstantLine.str_success, true);
			} catch (Exception e) {
				e.printStackTrace();
				mav.addObject(ConstantLine.str_msg,e.getLocalizedMessage());
			} finally {
				SessionServerHelper.manager.setAccessEnforced(enforce);
			}
			return mav;
	}
	
	/**
	 * 获取单位的下拉值数据
	 * @return
	 */
	@RequestMapping(value = "/line/queryUnits.do")
	public ModelAndView queryUnits(){
			boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
			ModelAndView mav = new ModelAndView("jsonView");
			try {
				mav.addObject(ConstantLine.str_data, multiservice.queryUnits());
				mav.addObject(ConstantLine.str_success, true);
			} catch (Exception e) {
				e.printStackTrace();
				mav.addObject(ConstantLine.str_msg,e.getLocalizedMessage());
			} finally {
				SessionServerHelper.manager.setAccessEnforced(enforce);
			}
			return mav;
	}
	
}
