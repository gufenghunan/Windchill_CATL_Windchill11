package com.catl.pdfsignet.resource;

import wt.util.resource.RBEntry;
import wt.util.resource.RBUUID;
import wt.util.resource.WTListResourceBundle;

@RBUUID("com.catl.pdfsignet.resource.CATLSignetRB")
public class CATLSignetRB extends WTListResourceBundle {

	@RBEntry("生成DC签名文件")
	public static final String PRINT_SIGNET_DESCRIPTION = "catlsignet.printSignet.description";
	@RBEntry("生成DC签名文件")
	public static final String PRINT_SIGNET_TOOLTIP = "catlsignet.printSignet.tooltip";
	@RBEntry("生成DC签名文件")
	public static final String PRINT_SIGNET_TITLE = "catlsignet.printSignet.title";
	
	@RBEntry("生成DC签名文件")
	public static final String PPART_PRINT_SIGNET_DESCRIPTION = "catlsignet.printSignetForPart.description";
	@RBEntry("生成DC签名文件")
	public static final String PPART_PRINT_SIGNET_TOOLTIP = "catlsignet.printSignetForPart.tooltip";
	@RBEntry("生成DC签名文件")
	public static final String PPART_PRINT_SIGNET_TITLE = "catlsignet.printSignetForPart.title";
	
	@RBEntry("批量下载PDF文件")
	public static final String BATCH_DOWNLOAD_PDF_DESCRIPTION = "catlsignet.batchDownloadSignetPDF.description";
	@RBEntry("批量下载PDF文件")
	public static final String BATCH_DOWNLOAD_PDF_TOOLTIP = "catlsignet.batchDownloadSignetPDF.tooltip";
	@RBEntry("批量下载PDF文件")
	public static final String BATCH_DOWNLOAD_PDF_TITLE = "catlsignet.batchDownloadSignetPDF.title";
}
