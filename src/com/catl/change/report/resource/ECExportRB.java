package com.catl.change.report.resource;

import wt.util.resource.RBEntry;
import wt.util.resource.RBUUID;
import wt.util.resource.WTListResourceBundle;

@RBUUID(value="com.catl.change.report.resource.ECExportRB")
public class ECExportRB extends WTListResourceBundle  {
    @RBEntry("Export EC Affect target parent part info")
    public static final String PART_Export_100 = "catl.loadECAffect.description";

    @RBEntry("export.gif")
    public static final String PART_BOMLOAD_101 = "catl.loadECAffect.icon";

    @RBEntry("export ec Affect targets parent part info")
    public static final String PART_BOMLOAD_102 = "catl.loadECAffect.tooltip";

    @RBEntry("delete")
    public static final String PART_Export_103 = "customChangeTask.deleteChangeTask.description";

    @RBEntry("netmarkets/images/delete.gif")
    public static final String PART_BOMLOAD_104 = "customChangeTask.deleteChangeTask.icon";

    @RBEntry("delete")
    public static final String PART_BOMLOAD_105 = "customChangeTask.deleteChangeTask.tooltip";
    
    @RBEntry("是否确认删除DCA？")
    public static final String DELETE = "delete";
}
