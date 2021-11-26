package com.catl.doc.maturityUpReport.resource;

import wt.util.resource.RBEntry;
import wt.util.resource.RBUUID;
import wt.util.resource.WTListResourceBundle;

@RBUUID("com.catl.doc.maturityUpReport.resource.MaturityUpReportRB")
public class MaturityUpReportRB extends WTListResourceBundle {
	
	@RBEntry("The associated maturity report")
    public static final String ACTION_RELATED_REPORT_DESCRIPTION = "maturityReport.relatedReportList.description";
    @RBEntry("The associated maturity report")
    public static final String ACTION_RELATED_REPORT_TOOLTIP = "maturityReport.relatedReportList.tooltip";
    @RBEntry("The associated maturity report")
    public static final String ACTION_RELATED_REPORT_TITLE = "maturityReport.relatedReportList.title";
    
	@RBEntry("The associated material")
    public static final String ACTION_RELATED_PARTS_DESCRIPTION = "maturityReport.relatedPartList.description";
    @RBEntry("The associated material")
    public static final String ACTION_RELATED_PARTS_TOOLTIP = "maturityReport.relatedPartList.tooltip";
    @RBEntry("The associated material")
    public static final String ACTION_RELATED_PARTS_TITLE = "maturityReport.relatedPartList.title";
	
	@RBEntry("Add Parts")
    public static final String ACTION_ADD_PARTS_DESCRIPTION = "maturityReport.addParts.description";
    @RBEntry("Add Parts")
    public static final String ACTION_ADD_PARTS_TOOLTIP = "maturityReport.addParts.tooltip";
    @RBEntry("Add Parts")
    public static final String ACTION_ADD_PARTS_TITLE = "maturityReport.addParts.title";
    
    @RBEntry("Remove Parts")
    public static final String ACTION_REMOVE_PARTS_DESCRIPTION = "maturityReport.removeParts.description";
    @RBEntry("Remove Parts")
    public static final String ACTION_REMOVE_PARTS_TOOLTIP = "maturityReport.removeParts.tooltip";
    @RBEntry("Remove Parts")
    public static final String ACTION_REMOVE_PARTS_TITLE = "maturityReport.removeParts.title";
    
    @RBEntry("Paste Parts")
    public static final String ACTION_PASTE_PARTS_DESCRIPTION = "maturityReport.pasteParts.description";
    @RBEntry("Paste Parts")
    public static final String ACTION_PASTE_PARTS_TOOLTIP = "maturityReport.pasteParts.tooltip";
    @RBEntry("Paste Parts")
    public static final String ACTION_PASTE_PARTS_TITLE = "maturityReport.pasteParts.title";

	@RBEntry("The material related to NFAE maturity 3 upgrade report {0}, please remove the relationship and then deleted!")
	public static final String ERROR_DELETE_INITIALPART = "ERROR_DELETE_INITIALPART";
	
	@RBEntry("Material{0} related other maturity upgrade report!")
	public static final String ERROR_EXIST_OTHER_LINK = "ERROR_EXIST_OTHER_LINK";
	
	@RBEntry("Material Initial Version")
	public static final String LABEL_INITIAL_VERSION = "LABEL_INITIAL_VERSION";
	
	@RBEntry("Related Materials")
	public static final String TABLE_RELATED_PARTS = "TABLE_RELATED_PARTS";
	
	@RBEntry("Maturity Reports")
	public static final String TABLE_MATURITY_REPORTS = "TABLE_MATURITY_REPORTS";
}
