package com.catl.ecad.utils;

public class ECADConst {

	public final static String RESOURCE = "com.catl.ecad.resource.ECADResource"; // 指向资源文件

	// 常量定义==============================================================================================================================================
	public final static String SCHTOOL = "orcad"; // 原理图设计工具名称
	public final static String PCBTOOL = "pcb"; // PCB设计工具名称
	public final static String ECAD_EXCEL_PATH = "ECAD-CLS.xlsx";// PCBA组件/PCBA/PCB物料组

	public final static String ECADLOCATION = "/设计图档/ECAD图档"; // ECAD图档（原理图/PCB图）的存放位置

	public final static String SCHTYPE = "com.CATLBattery.ECADSchematic"; // 原理图类型
	public final static String PCBTYPE = "com.CATLBattery.ECADBoard"; // PCB类型

	// 角色定义==============================================================================================================================================

	public final static String ECAD = "ECAD_ENGINEER"; // ECAD工程师角色
	public final static String DESIGNER = "DESIGNER";// 设计者
	public final static String JAODUIZHE = "COLLATOR";// 校对者
	public final static String HARDWAREENGINEER = "HARDWARE_ENGINEER";// 硬件工程师
	public final static String SUBMITTER = "SUBMITTER";// 提交者

	// 组定义===============================================================================================================================================
	public final static String ECADGROUP = "ECAD工程师组"; // ECAD工程师组

	// Gerber文件类型===============================================================================================================================================
	public final static String GERBERTYPE = "com.CATLBattery.gerberDoc";// GERBER文件

	// 装配图类型===============================================================================================================================================
	public final static String ASSEMBLYDRAWING = "com.CATLBattery.pcbaDrawing";// PCBA装配图

	// 生命周期状态===================================================================================================================================================
	public final static String DESIGN_STATE = "DESIGN";// 设计
	public final static String DESIGNMODIFICATION_STATE = "DESIGNMODIFICATION";// 设计修改
	public final static String RELEASED_STATE = "RELEASED";// 已发布
	public final static String DISABLE_FOR_DESIGN_STATE = "DISABLED_FOR_DESIGN";// 设计禁用

	// BOM
	// CSV文件表头定义============================================================================================================================================
	public final static String PARTNUMBER = "PartNumber";// 部件编号
	public final static String QUANTITY = "Quantity";// 数量
	public final static String REFERENCE_DESIGNATOR = "Reference Designator";// 位号

	// 流程定义======================================================================================================================================================
	public final static String PCBDESIGN_WF = "PCB设计"; // PCB设计流程
	public final static String CREATECOMPONENT_WF = "元器件建库"; // 元器件建库流程
}
