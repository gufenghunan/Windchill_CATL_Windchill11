package com.catl.line.util;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.ServletContextAware;
import org.springframework.web.multipart.MultipartResolver;

/**
 * 上传文件的过滤限制
 * @author hdong
 *
 */
public class UploadResolver extends org.springframework.web.multipart.commons.CommonsMultipartResolver 
  implements MultipartResolver, ServletContextAware
{
	  public boolean isMultipart(HttpServletRequest request)
	  {
	  if(request.getRequestURL().indexOf("/line/")==-1 && request.getRequestURL().indexOf("/riA/")==-1&& request.getRequestURL().indexOf("/riB/")==-1&& request.getRequestURL().indexOf("/battery/")==-1){
		  return false;
	  }else{
		  return super.isMultipart(request);
	  }
	  }
} 