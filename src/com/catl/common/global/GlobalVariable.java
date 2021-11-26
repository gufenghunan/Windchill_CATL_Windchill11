package com.catl.common.global;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import com.catl.doc.workflow.DocClassificationModelNew;

public class GlobalVariable {
	
	/**
	 * key 唯一值  
	 * value 文件的最后修改时间
	 */
	public static Map<String,Long> fileLastModifyTime = new HashMap<String, Long>();
	/**
	 * key 文档细类_文档分类
	 * value DocClassificationModel
	 */
	public static Map<String,DocClassificationModelNew> docWorkflowConfigBean = new HashMap<String, DocClassificationModelNew>();
	
	/**
	 * key 文档细类_文档分类
	 * value DocClassificationModel
	 */
	public static Map<String,DocClassificationModelNew> docneedPnConfigBean = new HashMap<String, DocClassificationModelNew>();
	
	
	public static Vector<String> eleCls = new Vector<String>();
}
