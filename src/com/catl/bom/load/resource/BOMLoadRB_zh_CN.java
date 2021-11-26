package com.catl.bom.load.resource;

import wt.util.resource.RBComment;
import wt.util.resource.RBEntry;
import wt.util.resource.RBUUID;
import wt.util.resource.WTListResourceBundle;

@RBUUID(value="com.catl.bom.load.resource.BOMLoadRB")
public class BOMLoadRB_zh_CN extends WTListResourceBundle  {
    
    @RBEntry("导入单层BOM")
    public static final String PART_BOMLOAD_100 = "bomload.loadBOM.description";

    @RBEntry("导入单层BOM")
    public static final String PART_BOMLOAD_101 = "bomload.loadBOM.title";

    @RBEntry("导入单层BOM")
    public static final String PART_BOMLOAD_102 = "bomload.loadBOM.tooltip";

    @RBEntry("upload.png")
    @RBComment("DO NOT TRANSLATE")
    public static final String PART_BOMLOAD_103 = "bomload.loadBOM.icon";
    
    @RBEntry("导入单层BOM")
    public static final String LOAD_SINGLE_LEVEL_BOM = "LOAD_SINGLE_LEVEL_BOM";
    
    @RBEntry("上传导入文件")
    public static final String LOADFILE = "LOADFILE";
}
