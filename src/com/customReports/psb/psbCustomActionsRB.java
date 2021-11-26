package com.customReports.psb;

import wt.util.resource.RBComment;
import wt.util.resource.RBEntry;
import wt.util.resource.RBPseudo;
import wt.util.resource.RBUUID;
import wt.util.resource.WTListResourceBundle;

@RBUUID("com.customReports.psb.psbCustomActionsRB")
public final class psbCustomActionsRB extends WTListResourceBundle {

    /********
     * Custom Report 01
     */
    @RBEntry("专用物料报表") 
// this is what is displayed in the menu
    public static final String PSB_CUSTOMREPORTGWT01_DESCRIPTION
 = "catlpsb.psbCustomReportGWT01.description";

    @RBEntry("专用物料报表")
 // not used, but is here for completeness in case used in the future
    public static final String PSB_CUSTOMREPORTGWT01_TITLE
 = "catlpsb.psbCustomReportGWT01.title";

    @RBEntry("专用物料报表") 
// not used, but is here for completeness in case used in the future
    public static final String PSB_CUSTOMREPORTGWT01_TOOLTIP
 = "catlpsb.psbCustomReportGWT01.tooltip";

    @RBEntry("height=900,width=1000")   
 // specifies the size in pixels of the report window
    @RBPseudo(false)
    @RBComment("DO NOT TRANSLATE")
    public static final String PSB_CUSTOMREPORTGWT01_MOREURLINFO
 = "catlpsb.psbCustomReportGWT01.moreurlinfo";

    
    @RBEntry("作为特定替换件的使用情况")
    public static final String PARTSTEAD_USEDBYSTEAD_DESCRIPTION= "partStead.usedByStead.description";

    @RBEntry("作为特定替换件的使用情况")
    public static final String PARTSTEAD_USEDBYSTEAD_TITLE= "partStead.usedByStead.title";

    @RBEntry("作为特定替换件的使用情况") 
    public static final String PARTSTEAD_USEDBYSTEAD_TOOLTIP= "partStead.usedByStead.tooltip";
    
    @RBEntry("物料成熟度变更历史记录")
    public static final String PARTSTEAD_PARTMATURITYCHANGELOG_DESCRIPTION= "partStead.partMaturityChangeLog.description";

    @RBEntry("物料成熟度变更历史记录")
    public static final String PARTSTEAD_PARTMATURITYCHANGELOG_TITLE= "partStead.partMaturityChangeLog.title";
    
    @RBEntry("物料成熟度变更历史记录") 
    public static final String PARTSTEAD_PARTMATURITYCHANGELOG_TOOLTIP= "partStead.partMaturityChangeLog.tooltip";
    
    @RBEntry("采购类型更改历史记录")
    public static final String PARTSTEAD_PARTSOURCECHANGELOG_DESCRIPTION= "partStead.partSourceChangeLog.description";

    @RBEntry("采购类型更改历史记录")
    public static final String PARTSTEAD_PARTSOURCECHANGELOG_TITLE= "partStead.partSourceChangeLog.title";
    
    @RBEntry("采购类型更改历史记录") 
    public static final String PARTSTEAD_PARTSOURCECHANGELOG_TOOLTIP= "partStead.partSourceChangeLog.tooltip";
    
}