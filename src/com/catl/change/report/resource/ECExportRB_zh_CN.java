package com.catl.change.report.resource;

import wt.util.resource.RBEntry;
import wt.util.resource.RBUUID;
import wt.util.resource.WTListResourceBundle;

@RBUUID(value="com.catl.change.report.resource.ECExportRB")
public class ECExportRB_zh_CN extends WTListResourceBundle  {
    @RBEntry("导出受影响报告")
    public static final String PART_Export_100 = "catl.loadECAffect.description";

    @RBEntry("export.gif")
    public static final String PART_BOMLOAD_101 = "catl.loadECAffect.icon";

    @RBEntry("导出受影响报告")
    public static final String PART_BOMLOAD_102 = "catl.loadECAffect.tooltip";

    @RBEntry("删除")
    public static final String PART_Export_103 = "customChangeTask.deleteChangeTask.description";

    @RBEntry("netmarkets/images/delete.gif")
    public static final String PART_BOMLOAD_104 = "customChangeTask.deleteChangeTask.icon";

    @RBEntry("删除")
    public static final String PART_BOMLOAD_105 = "customChangeTask.deleteChangeTask.tooltip";
    
    @RBEntry("是否确认删除DCA？")
    public static final String DELETE = "delete";
}
