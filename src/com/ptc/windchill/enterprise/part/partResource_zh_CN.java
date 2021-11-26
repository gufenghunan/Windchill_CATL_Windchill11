/* bcwti
 *
 * Copyright (c) 2010 Parametric Technology Corporation (PTC). All Rights Reserved.
 *
 * This software is the confidential and proprietary information of PTC
 * and is subject to the terms of a software license agreement. You shall
 * not disclose such confidential information and shall use it only in accordance
 * with the terms of the license agreement.
 *
 * ecwti
 */
package com.ptc.windchill.enterprise.part;

import wt.util.resource.RBArgComment0;
import wt.util.resource.RBArgComment1;
import wt.util.resource.RBArgComment2;
import wt.util.resource.RBComment;
import wt.util.resource.RBEntry;
import wt.util.resource.RBPseudo;
import wt.util.resource.RBUUID;
import wt.util.resource.WTListResourceBundle;

@RBUUID("com.ptc.windchill.enterprise.part.partResource")
public final class partResource_zh_CN extends WTListResourceBundle {
   @RBEntry("名称")
   @RBComment("This string is used for the name label in the jsp page")
   public static final String NAME_COLUMN_LABEL = "0";

   @RBEntry("编号")
   @RBComment("This string is used for the number label in the jsp page")
   public static final String NUMBER_COLUMN_LABEL = "1";

   @RBEntry("组织")
   @RBComment("This string is used for the Organization label in the jsp page")
   public static final String ORGANIZATION_COLUMN_LABEL = "2";

   @RBEntry("上下文")
   @RBComment("This string is used for thecontainer column label in the jsp page")
   public static final String CONTEXT_COLUMN_LABEL = "3";

   @RBEntry("双向")
   @RBComment("This string is used for the twoway label in the jsp page")
   public static final String TWOWAY_COLUMN_LABEL = "4";

   @RBEntry("全局替换部件")
   @RBComment("This string is used for the Alternates table label in the jsp page")
   public static final String ALTERNATES_TABLE_LABEL = "ALTERNATES_TABLE_LABEL";

   @RBEntry("特定替换部件")
   @RBComment("This string is used for the Substitutes table label in the jsp page")
   public static final String SUBSTITUTES_TABLE_LABEL = "SUBSTITUTES_TABLE_LABEL";

   @RBEntry("将全局替换")
   @RBComment("This string is used for the \"Alternate For\" table title. Probably should match the \"part.alternates.description\"")
   public static final String ALTERNATESFOR_LABEL = "5";

   @RBEntry("特定替换用于")
   @RBComment("This string is used for the Substitutes label in the jsp page")
   public static final String SUBSTITUTESFOR_LABEL = "6";

   @RBEntry("替换部件")
   @RBComment("This string is used for the Alternates and Substitutes label in the jsp page")
   public static final String ALTERNATES_SUBSTITUTES_LABEL = "9";

   @RBEntry("指定替换部件用于")
   @RBComment("This string is used for the Alternates and Substitutes label in the jsp wizard page")
   public static final String SPECIFY_ALTERNATES_SUBSTITUTES_LABEL = "10";

   @RBEntry("范围")
   @RBComment("This string is used for the Alternates and Substitutes label in the jsp wizard page")
   public static final String SPECIFY_IN_LABEL = "11";

   @RBEntry("移除")
   @RBComment("Used to remove alternates and subtitutes")
   public static final String PRIVATE_CONSTANT_0 = "part.remove_alternates_substitutes.description";

   @RBEntry("移除选定的")
   @RBComment("Tooltip for icon to remove alternates and substitutes")
   public static final String PRIVATE_CONSTANT_1 = "part.remove_alternates_substitutes.tooltip";

   @RBEntry("remove16x16.gif")
   @RBComment("Icon to remove alternates and substitutes")
   public static final String PRIVATE_CONSTANT_2 = "part.remove_alternates_substitutes.icon";

   @RBEntry("装配编号")
   @RBComment("This string is used for the assembly number label in the jsp page")
   public static final String ASSEMBLY_NUMBER_COLUMN_LABEL = "12";

   @RBEntry("装配名称")
   @RBComment("This string is used for the assembly name label in the jsp page")
   public static final String ASSEMBLY_NAME_COLUMN_LABEL = "13";

   @RBEntry("装配组织")
   @RBComment("This string is used for the assembly organization label in the jsp page")
   public static final String ASSEMBLY_ORG_COLUMN_LABEL = "14";

   @RBEntry("操作")
   @RBComment("This string is used for the action label in the jsp page")
   public static final String ACTIONS_COLUMN_LABEL = "15";

   @RBEntry("查找全局替换部件")
   @RBComment("This string is used for the header in the search picker")
   public static final String ADD_ALTERNATE_PART_HEADER = "18";

   @RBEntry("查找特定替换部件")
   @RBComment("This string is used for the header in the search picker")
   public static final String ADD_SUBSTITUTE_PART_HEADER = "19";

   @RBEntry("部件版本")
   @RBComment("This string is used for the column header for part version")
   public static final String PART_VERSION_COLUMN_LABEL = "20";

   @RBEntry("状态")
   @RBComment("This string is used for the column header for state.state")
   public static final String STATE_COLUMN_LABEL = "21";

   @RBEntry("主要成员编号")
   @RBComment("his string is used for the column header for primary member object number")
   public static final String PRIMARY_MEMBER_NUMBER_COLUMN_LABEL = "22";

   @RBEntry("模式")
   @RBComment("his string is used for the column header for mode")
   public static final String MODE_COLUMN_LABEL = "23";

   @RBEntry("上次修改时间")
   @RBComment("his string is used for the column header for Last Modified")
   public static final String LAST_UPDATED_COLUMN_LABEL = "24";

   @RBEntry("基线")
   @RBComment("his string is used for the related baseline table name")
   public static final String RELATED_BASELINE_TABLE_LABEL = "25";

   @RBEntry("数量")
   @RBComment("Quantity column")
   public static final String QUANTITY = "26";

   @RBEntry("单位")
   @RBComment("Units column")
   public static final String UNITS = "27";

   @RBEntry("子项部件")
   @RBComment("Name of the tabular input table")
   public static final String TABLE_NAME_TABULAR_INPUT = "28";

   @RBEntry("检索号")
   @RBComment("Item Find Number Column")
   public static final String ITEM_FIND_NUMBER = "29";

   @RBEntry("行号")
   @RBComment("Line Number Column")
   public static final String LINE_NUMBER = "30";

   @RBEntry("追踪代码")
   @RBComment("Trace Code Column")
   public static final String TRACE_CODE = "31";

   @RBEntry("BOM 注解")
   @RBComment("BOM Note Column")
   public static final String BOM_NOTE = "32";

   @RBEntry("您没有修改部件 {0} 的权限。")
   public static final String NO_MODIFY_PERMISSION = "33";

   @RBEntry("不允许从另一个容器将一个子项添加到部件主数据。")
   @RBComment("Message that displays when a user trys to add a child to part master that lives in another container.")
   public static final String ADDING_TO_MASTER_NOT_ALLOWED = "34";

   @RBEntry("对象已经由其他用户检出。")
   public static final String OBJECT_CHECKED_OUT = "35";

   @RBEntry("如果部件不是一个共享部件，就不能将它从另一个容器添加至一个产品结构内。")
   public static final String DIFFERING_CONTAINERS_OP_NOT_ALLOWED = "36";

   @RBEntry("将使用关系添加至:{0}")
   public static final String ADDED_USES = "37";

   @RBEntry("目前，未找到任何要移除的部件")
   public static final String NO_PARTS_TO_REMOVE = "38";

   @RBEntry("已移除使用关系至: {0}")
   public static final String REMOVED_USES = "39";

   @RBEntry("错误: 请务必在标有星号的名称字段内输入值。")
   @RBComment("Message that appears when user tries to leave a page without filling in a required (asterisked) field.")
   public static final String MISSING_REQUIRED_FIELD = "48";

   @RBEntry("编辑对象的公用属性时出错。")
   @RBComment("This string is used in case of error while editing common attributes of the object.")
   public static final String EDIT_COMMON_ATTRS_ERROR = "49";

   @RBEntry("停止有效性传播")
   @RBComment("Stop Effectivity Propagation Column")
   public static final String STOP_EFFECTIVITY_PROPAGATION = "50";

   @RBEntry("可折叠")
   @RBComment("This is a label for Collapsible column")
   public static final String COLLAPSIBLE = "51";

   @RBEntry("全部")
   @RBComment("This string is used for the Replacemant Table for all view in the jsp page")
   public static final String ALL_VIEW = "52";

   @RBEntry("按编号搜索:")
   @RBComment("This is a string used for Quick Search Filed label")
   public static final String QUICK_SEARCH_LABEL = "53";

   @RBEntry("10")
   @RBComment("This is a max search results shown in autosuggest")
   public static final String MAX_SEARCH_RESULTS = "54";

   @RBEntry("位号")
   @RBComment("Reference Designator Column")
   public static final String REFERENCE_DESIGNATOR = "55";

   @RBEntry("所有版本 (成员)")
   @RBComment("View that shows all it's verson iterations are member of")
   public static final String ALL_VERSION_MEMBER = "56";

   @RBEntry("修订版本 (成员)")
   @RBComment("View that shows all it's revisions are member of")
   public static final String REVISION_MEMBER = "57";

   @RBEntry("版本 (成员)")
   @RBComment("View that shows all it's iterations are member of")
   public static final String VERSION_MEMBER = "58";

   @RBEntry("所有版本 (主要)")
   @RBComment("View that shows all it's verson iterations are top object (context) of")
   public static final String ALL_VERSION_TOP = "59";

   @RBEntry("修订版本 (主要)")
   @RBComment("View that shows all it's revisions are top object (context) of")
   public static final String REVISION_TOP = "60";

   @RBEntry("版本 (主要)")
   @RBComment("View that shows all it's iteration is top object (context) of")
   public static final String VERSION_TOP = "61";

   @RBEntry("装配版本")
   @RBComment("This string is used for the assembly Version column label in the jsp page")
   public static final String ASSEMBLY_VERSION_COLUMN_LABEL = "62";

   @RBEntry("分类")
   @RBComment("Label for the Classification")
   public static final String CLASSIFICATION_LABEL = "63";

   @RBEntry("位置")
   @RBComment("Label for the Location")
   public static final String LOCATION_LABEL = "64";

   @RBEntry("制造商 ID")
   @RBComment("Label for the Manufacturer ID")
   public static final String MANUFACTURERID_LABEL = "65";

   @RBEntry("厂商 ID")
   @RBComment("Label for the Vendor ID")
   public static final String VENDORID_LABEL = "66";

   @RBEntry("无法为“个”以外的测量单位设置数量 = 0")
   @RBComment("Error message for quantity 0 and unit other than each")
   public static final String QUANTITY_VALIDATION_FOR_UNIT = "68";

   @RBEntry("类型")
   @RBComment("Label for the Part Type. For e.g. for a part of type wt.part.WTPart, it is 'Part'")
   public static final String TYPE_NAME_LABEL = "70";

   @RBEntry("替换部件类型")
   @RBComment("Label for the Replacement Type")
   public static final String REPLACEMENT_TYPE_LABEL = "71";

   @RBEntry("*名称")
   @RBComment("This string is used for the name required label in the jsp page")
   public static final String NAME_R_COLUMN_LABEL = "72";

   @RBEntry("*编号")
   @RBComment("This string is used for the number required label in the jsp page")
   public static final String NUMBER_R_COLUMN_LABEL = "73";

   @RBEntry("'{0}' 已检出。")
   @RBComment("This string is used for inform user that the part has been checkout when invoked from tabular input.")
   @RBArgComment0("item id of the checked out part")
   public static final String PART_CHECKED_OUT = "74";

   @RBEntry("上次修改时间")
   @RBComment("This string is used for the column header for last modified")
   public static final String LAST_MODIFIED_COLUMN_LABEL = "75";

   @RBEntry("错误: 无法为部件分配视图。该部件的所有版本并不属于同一视图。")
   @RBComment("Message that appears when user tries to launch Assign View action and the part has versions with different views.")
   public static final String ASSIGN_VIEW_ERROR = "76";

   @RBEntry("创建时间")
   @RBComment("This string is used for the column header for created column")
   public static final String CREATED_LABEL = "77";

   @RBEntry("修改者")
   @RBComment("This string is used for the column header for modified by column")
   public static final String MODIFIEDBY_LABEL = "78";

   @RBEntry("制造商")
   @RBComment("This string is used as a label for Manufacturer column in part info page")
   public static final String MFG_LABEL = "79";

   @RBEntry("厂商")
   @RBComment("This string is used as a label for Vendor column in part info page")
   public static final String VENDOR_LABEL = "80";

   @RBEntry("部件")
   @RBComment("Name of the multi part identity attributes table")
   public static final String TABLE_NAME_MULTI_PART_IDENTITY = "81";

   @RBEntry("数量应为数字且大于或等于零。\n\n                         {0}")
   @RBComment("Error message if the quantity entered is zero or less than zero. Arguments identifies the part identity (number, org) for which the error occurred.")
   public static final String QUANTITY_VALIDATION = "82";

   @RBEntry("行号应为正数值并且其位数小于10 位。\n\n                         {0}。")
   @RBComment("Error message if the line number entered is alphanumeric. Arguments identifies the part identity (number, org) for which the error occurred.")
   public static final String LINE_NUMBER_VALIDATION = "83";

   @RBEntry("由于存在带有有效位号的具体值，因此无法减少数量。请先调整位号。")
   @RBComment("Message for quantity validation.")
   public static final String QUANTITY_VALIDATION_MSG = "84";

   @RBEntry("确认: 是否调整数量?\n\n位号的设定计数 {0} 大于当前数量 {1}，最大数量限制为 {2}。\n\n 是否要调整数量以使其与位号的计数相符?\n\n请单击“确定”可更改数量，或单击“取消”不对位号进行任何更改。")
   @RBComment("Message for reference designator validation.")
   public static final String REFDESIGNATOR_MORE_VALIDATION_MSG = "85";

   @RBEntry("某些剪贴板中的对象不是部件")
   @RBComment("Message for validationg paste action.")
   public static final String INVALID_PASTE_TYPE_ERRMSG = "86";

   @RBEntry("剪贴板内没有可粘贴的对象")
   @RBComment("Message for validationg paste action.")
   public static final String NULLOBJ_PASTE_ERRMSG = "87";

   @RBEntry("对象创建成功")
   @RBComment("This string is used as the success message for multi-part create wizard.")
   public static final String MULTI_PART_CREATE_SUCCESSFUL_MSG = "88";

   @RBEntry("注意: 缺少必需的信息  \n\n \"{0}\" 的一个或多个必填字段为空白。\n请填写所有标有星号 (*) 的字段。")
   @RBComment("Message that appears when user tries to launch Assign View action and the part has versions with different views.")
   public static final String CREATE_MULTI_PART_NAME_NUMBER_VALIDATION_ERROR = "89";

   @RBEntry("不能根据对象的初始化规则来确定生命周期和团队模板。根据对象的初始化规则条目可能会没有为模板定义条目或无法找到有效的模板。")
   @RBComment("Message for validating lifecycle and team template.")
   public static final String INVALID_TEMPLATES = "90";

   @RBEntry("不能根据对象的初始化规则来确定生命周期模板。可能没有为模板定义条目或根据对象的初始化规则条目无法找到有效的模板。")
   @RBComment("Message for validating lifecycle template.")
   public static final String INVALID_LIFECYCLE_TEMPLATE = "91";

   @RBEntry("不能根据对象的初始化规则来确定团队模板。可能没有为模板定义条目或无法根据对象的初始化规则条目找到有效的模板。")
   @RBComment("Message for validating team template.")
   public static final String INVALID_TEAM_TEMPLATE = "92";

   @RBEntry("当单位为“个”时，数量必须是正整数。\n\n{0}")
   @RBComment("Error message for quantity when unit is each. Arguments identifies the part (number, org) for which the error occurred.")
   public static final String QUANTITY_VALIDATION_FOR_EACH_UNIT = "93";

   @RBEntry("检索号的值 \"{0}\" 在子装配中必须唯一")
   @RBComment("Error message")
   public static final String FIND_NUMBER_NOT_UNIQUE = "94";

   @RBEntry("行号的值 \"{0}\" 在子装配中必须唯一")
   @RBComment("Error message")
   public static final String LINE_NUMBER_NOT_UNIQUE = "95";

   @RBEntry("位号的值 \"{0}\" 在子装配中必须唯一")
   @RBComment("Error message")
   public static final String REF_DESIG_NOT_UNIQUE = "96";

   @RBEntry("位号的值 \"{0}\" 无效。字符 '{1}' 和 '{2}' 已被保留，不能用于位号的一部分。")
   @RBComment("Error message")
   public static final String REF_DESIG_INVALID = "97";

   @RBEntry("确认: 是否调整数量?\n\n位号的设定计数 {0} 小于当前数量 {1}，最大数量限制为 {2}。\n\n 是否要调整数量以使其与位号的计数相符?\n\n请单击“确定”可更改数量，或单击“取消”不对位号进行任何更改。")
   @RBComment("Message for reference designator validation.")
   public static final String REFDESIGNATOR_LESS_VALIDATION_MSG = "98";

   @RBEntry("新建部件")
   @RBComment("Name of the multi part attributes table")
   public static final String TABLE_NAME_MULTI_PART = "99";

   @RBEntry("*装配模式")
   @RBComment("This string is used for the assembly mode required label in the jsp page")
   public static final String TYPE_R_COLUMN_LABEL = "100";

   @RBEntry("*默认追踪代码")
   @RBComment("This string is used for the trace code required label in the jsp page")
   public static final String TRACE_CODE_R_COLUMN_LABEL = "101";

   @RBEntry("默认追踪代码")
   @RBComment("This string is used for the trace code label in jsp pages. Note that it does not have * in the label")
   public static final String TRACE_CODE_COLUMN_LABEL = "TRACE_CODE_COLUMN_LABEL";

   @RBEntry("*默认单位")
   @RBComment("This string is used for the default units required label in the jsp page")
   public static final String UNITS_R_COLUMN_LABEL = "102";

   @RBEntry("*收集部件")
   @RBComment("This string is used for the Gathering Part required label in the jsp page")
   public static final String GATHERING_PART = "GATHERING_PART";

   @RBEntry("*生命周期模板")
   @RBComment("This string is used for the lifecycle template required label in the jsp page")
   public static final String LIFECYCLE_TEMPLATE_R_COLUMN_LABEL = "103";

   @RBEntry("*位置")
   @RBComment("This string is used for the location required label in the jsp page")
   public static final String LOCATION_R_COLUMN_LABEL = "104";

   @RBEntry("*源")
   @RBComment("This string is used for the source required label in the jsp page")
   public static final String SOURCE_COLUMN_LABEL = "105";

   @RBEntry("请只选择部件和文档。")
   @RBComment("Error message displayed when invalid objects are selected for edit multi action")
   public static final String SELECT_VALID_TYPES = "106";

   @RBEntry("在结构中，无法将 \"{0}\" 添加到其自身")
   @RBComment("Error message")
   public static final String PART_REFLEXIVE_LINK = "107";

   @RBEntry("查找部件")
   @RBComment("This string is used for the Find Part title in Item Picker")
   public static final String FIND_PART = "FIND_PART";

   @RBEntry("工艺计划")
   @RBComment("Used in the 3rd level nav bar under 'Related items' and as the table title")
   public static final String PRIVATE_CONSTANT_3 = "part.relatedMPMProcessPlans.description";

   /**
    * Column headers for the related MPM Process Plans table (3rd level nav)
    **/
   @RBEntry("版本")
   public static final String PRIVATE_CONSTANT_4 = "part.relatedMPMProcessPlans.VERSION";

   @RBEntry("上下文")
   public static final String PRIVATE_CONSTANT_5 = "part.relatedMPMProcessPlans.CONTEXT";

   @RBEntry("状态")
   public static final String PRIVATE_CONSTANT_6 = "part.relatedMPMProcessPlans.STATE";

   @RBEntry("团队")
   public static final String PRIVATE_CONSTANT_7 = "part.relatedMPMProcessPlans.TEAM";

   @RBEntry("上次修改时间")
   public static final String PRIVATE_CONSTANT_8 = "part.relatedMPMProcessPlans.LAST_UPDATED";

   /**
    * Part Info, Actions dropdown, New View Version action
    **/
   @RBEntry("新建视图版本")
   @RBComment("Used as a label for new view version action popup")
   public static final String PRIVATE_CONSTANT_9 = "part.newViewVersion.title";

   @RBEntry("新建视图版本")
   @RBComment("Used in the action list for part details page")
   public static final String PRIVATE_CONSTANT_10 = "part.newViewVersion.description";

   @RBEntry("创建部件的新视图版本")
   @RBComment("Used as tooltip for new view version action")
   public static final String PRIVATE_CONSTANT_11 = "part.newViewVersion.tooltip";

   @RBEntry("height=240,width=600")
   @RBPseudo(false)
   @RBComment("DO NOT TRANSLATE")
   public static final String PRIVATE_CONSTANT_12 = "part.newViewVersion.moreurlinfo";

   @RBEntry("version_view_create.gif")
   @RBPseudo(false)
   @RBComment("Icon that appears in the page info page action drop down list")
   public static final String PRIVATE_CONSTANT_13 = "part.newViewVersion.icon";

   @RBEntry("发生意外故障。请参见日志，了解详细信息。")
   @RBComment("This text is used as a part of an exception (that should never occur).")
   public static final String PRIVATE_CONSTANT_14 = "part.newViewVersion.UNEXPECTED_FAILURE";

   @RBEntry("选择视图")
   @RBComment("Used as a label for new view version wizard step")
   public static final String PRIVATE_CONSTANT_15 = "part.newViewVersion_step.description";

   @RBEntry("选择必须分配给部件新版本的视图")
   @RBComment("Used for tooltip in the new view version wizard step")
   public static final String PRIVATE_CONSTANT_16 = "part.newViewVersion_step.tooltip";

   @RBEntry("选择视图")
   @RBComment("Text used in the niew view version wizard step")
   public static final String PRIVATE_CONSTANT_17 = "part.newViewVersion_step.SELECT_VIEW";

   /**
    * Part Info, Actions dropdown, One Off Version action
    **/
   @RBEntry("新的一次性版本")
   @RBComment("Used as a label for New One Off Version action popup")
   public static final String PRIVATE_CONSTANT_18 = "part.oneOffVersion.title";

   @RBEntry("新的一次性版本")
   @RBComment("Used in the action list for part details page")
   public static final String PRIVATE_CONSTANT_19 = "part.oneOffVersion.description";

   @RBEntry("创建部件的“新的一次性版本”")
   @RBComment("Used as tooltip for New One Off Version action")
   public static final String PRIVATE_CONSTANT_20 = "part.oneOffVersion.tooltip";

   @RBEntry("height=240,width=600")
   @RBPseudo(false)
   @RBComment("DO NOT TRANSLATE")
   public static final String PRIVATE_CONSTANT_21 = "part.oneOffVersion.moreurlinfo";

   @RBEntry("one_off_view.gif")
   @RBPseudo(false)
   @RBComment("Icon that appears in the page info page action drop down list")
   public static final String PRIVATE_CONSTANT_22 = "part.oneOffVersion.icon";

   @RBEntry("新的一次性版本")
   @RBComment("Used as a label for New One Off Version wizard step")
   public static final String PRIVATE_CONSTANT_23 = "part.oneOffVersion_step.description";

   @RBEntry("新的一次性版本")
   @RBComment("Used for tooltip in the New One Off Version wizard step")
   public static final String PRIVATE_CONSTANT_24 = "part.oneOffVersion_step.tooltip";

   /**
    * Create Part Wizard action
    **/
   @RBEntry("新建部件")
   @RBComment("Used as the label for the New Part Wizard")
   public static final String PRIVATE_CONSTANT_25 = "part.createPartWizard.description";

   @RBEntry("新建部件")
   @RBComment("Used as the label for the New Part Wizard")
   public static final String PRIVATE_CONSTANT_26 = "part.createPartWizard.title";

   @RBEntry("新建部件")
   @RBComment("Used as the tooltip for the New Part Wizard")
   public static final String PRIVATE_CONSTANT_27 = "part.createPartWizard.tooltip";

   @RBEntry("height=750,width=650")
   @RBPseudo(false)
   @RBComment("DO NOT TRANSLATE")
   public static final String PRIVATE_CONSTANT_28 = "part.createPartWizard.moreurlinfo";

   @RBEntry("newpart.gif")
   @RBPseudo(false)
   @RBComment("Icon that appears in toolbars for creating a new part")
   public static final String PRIVATE_CONSTANT_29 = "part.createPartWizard.icon";

   /**
    * Create Part Wizard labels
    **/
   @RBEntry("定义部件")
   @RBComment("Label for define item step in the Create Part Wizard")
   public static final String PRIVATE_CONSTANT_35 = "part.createPartWizard.DEFINE_ITEM_WIZ_STEP_LABEL";

   @RBEntry("设置标识属性")
   @RBComment(" Label for Set Identity Attributes step in Create Multiple Part wizard")
   public static final String PRIVATE_CONSTANT_36 = "part.createPartWizard.SET_IDENTITY_ATTRIBUTES_WIZ_STEP_LABEL";

   @RBEntry("设置属性")
   @RBComment("Label for set attributes step in the Create Part Wizard")
   public static final String PRIVATE_CONSTANT_37 = "part.createPartWizard.SET_ATTRIBUTES_WIZ_STEP_LABEL";

   @RBEntry("设置附加属性")
   @RBComment("Label for set attributes step in the Create Multiple Part Wizard")
   public static final String PRIVATE_CONSTANT_38 = "part.createPartWizard.SET_ATTRIBUTES_WIZ_STEP_MULTI_CREATE_LABEL";

   @RBEntry("访问控制")
   @RBComment("Label for access control step in the Create Part Wizard")
   public static final String PRIVATE_CONSTANT_40 = "part.createPartWizard.ACCESS_CONTROL_WIZ_STEP_LABEL";

   @RBEntry("创建为成品")
   @RBComment("Used as label for define item step in the Create Part Wizard")
   public static final String PRIVATE_CONSTANT_41 = "part.createPartDefineItemWizStep.END_ITEM";

   @RBEntry("新建 CAD 文档")
   @RBComment("Label for New CAD Document step in the Create Part Wizard")
   public static final String PRIVATE_CONSTANT_42 = "part.createPartWizard.NEW_CAD_DOC_WIZ_STEP_LABEL";

   @RBEntry("分配项选择")
   @RBComment("Used as label for assign item choices in the Create Part Wizard")
   public static final String PRIVATE_CONSTANT_43 = "part.createPartWizard.EDIT_ITEM_OPTION_RULES_WIZ_STEP_LABEL";

   /**
    * Create Part Wizard action resources when create part is invoked from a workspace
    * These labels should be the same as Create Part Wizard action above
    **/
   @RBEntry("新建部件")
   @RBComment("Should be same as part.createPartWizard")
   public static final String PRIVATE_CONSTANT_44 = "part.createPartFromWorkspace.description";

   @RBEntry("新建部件")
   @RBComment("Should be same as part.createPartWizard")
   public static final String PRIVATE_CONSTANT_45 = "part.createPartFromWorkspace.title";

   @RBEntry("新建部件")
   @RBComment("Should be same as part.createPartWizard")
   public static final String PRIVATE_CONSTANT_46 = "part.createPartFromWorkspace.tooltip";

   @RBEntry("height=750,width=650")
   @RBPseudo(false)
   @RBComment("Should be same as part.createPartWizard")
   public static final String PRIVATE_CONSTANT_47 = "part.createPartFromWorkspace.moreurlinfo";

   @RBEntry("newpart.gif")
   @RBPseudo(false)
   @RBComment("Icon that appears in toolbars for new part action")
   public static final String PRIVATE_CONSTANT_48 = "part.createPartFromWorkspace.icon";

   /**
    * Edit Part Wizard action
    **/
   @RBEntry("编辑")
   @RBComment("Used as the label for the edit action for a part")
   public static final String PRIVATE_CONSTANT_49 = "part.edit.description";

   @RBEntry("编辑部件")
   @RBComment("Used as the label for the edit action for a part")
   public static final String PRIVATE_CONSTANT_50 = "part.edit.title";

   @RBEntry("编辑部件")
   @RBComment("Used as the tooltip for the edit action for a part")
   public static final String PRIVATE_CONSTANT_51 = "part.edit.tooltip";

   @RBEntry("multi_update.gif")
   @RBPseudo(false)
   @RBComment("Icon for the edit part action (for toolbars)")
   public static final String PRIVATE_CONSTANT_52 = "part.edit.icon";

   @RBEntry("height=500,width=600")
   @RBPseudo(false)
   @RBComment("DO NOT TRANSLATE")
   public static final String PRIVATE_CONSTANT_53 = "part.edit.moreurlinfo";

   @RBEntry("编辑")
   @RBComment("Used as the label for the edit action for a part")
   public static final String PRIVATE_CONSTANT_54 = "part.editFromAttributesTable.description";

   @RBEntry("编辑部件")
   @RBComment("Used as the label for the edit action for a part")
   public static final String PRIVATE_CONSTANT_55 = "part.editFromAttributesTable.title";

   @RBEntry("编辑部件")
   @RBComment("Used as the tooltip for the edit action for a part")
   public static final String PRIVATE_CONSTANT_56 = "part.editFromAttributesTable.tooltip";

   @RBEntry("multi_update.gif")
   @RBPseudo(false)
   @RBComment("Icon for the edit part action (for toolbars)")
   public static final String PRIVATE_CONSTANT_57 = "part.editFromAttributesTable.icon";

   @RBEntry("height=500,width=600")
   @RBPseudo(false)
   @RBComment("DO NOT TRANSLATE")
   public static final String PRIVATE_CONSTANT_58 = "part.editFromAttributesTable.moreurlinfo";

   @RBEntry("检出并编辑")
   @RBComment("Used as the label for the Check Out and Edit action for a part")
   public static final String PRIVATE_CONSTANT_59 = "part.checkoutAndEditFromAttributesTable.description";

   @RBEntry("编辑部件")
   @RBComment("Used as the label for the Check Out and Edit action for a part")
   public static final String PRIVATE_CONSTANT_60 = "part.checkoutAndEditFromAttributesTable.title";

   @RBEntry("检出并编辑部件")
   @RBComment("Used as the tooltip for the Check Out and Edit action for a part")
   public static final String PRIVATE_CONSTANT_61 = "part.checkoutAndEditFromAttributesTable.tooltip";

   @RBEntry("multi_update.gif")
   @RBPseudo(false)
   @RBComment("Icon for the checkout and edit part action (for toolbars)")
   public static final String PRIVATE_CONSTANT_62 = "part.checkoutAndEditFromAttributesTable.icon";

   @RBEntry("height=500,width=600")
   @RBPseudo(false)
   @RBComment("DO NOT TRANSLATE")
   public static final String PRIVATE_CONSTANT_63 = "part.checkoutAndEditFromAttributesTable.moreurlinfo";

   @RBEntry("检出并编辑")
   @RBComment("Used as the label for the Check Out and Edit action for a part")
   public static final String PRIVATE_CONSTANT_64 = "part.checkoutAndEdit.description";

   @RBEntry("编辑部件")
   @RBComment("Used as the label for the Check Out and Edit action for a part")
   public static final String PRIVATE_CONSTANT_65 = "part.checkoutAndEdit.title";

   @RBEntry("检出并编辑部件")
   @RBComment("Used as the tooltip for the Check Out and Edit action for a part")
   public static final String PRIVATE_CONSTANT_66 = "part.checkoutAndEdit.tooltip";

   @RBEntry("multi_update.gif")
   @RBPseudo(false)
   @RBComment("Icon for the checkout and edit part action (for toolbars)")
   public static final String PRIVATE_CONSTANT_67 = "part.checkoutAndEdit.icon";

   @RBEntry("height=500,width=600")
   @RBPseudo(false)
   @RBComment("DO NOT TRANSLATE")
   public static final String PRIVATE_CONSTANT_68 = "part.checkoutAndEdit.moreurlinfo";

   @RBEntry("编辑")
   @RBComment("Used as the label for the edit action for a new part in workspace")
   public static final String PRIVATE_CONSTANT_69 = "part.editNewPartInWorkspace.description";

   @RBEntry("编辑部件")
   @RBComment("Used as the label for the edit action for a new part in workspace")
   public static final String PRIVATE_CONSTANT_70 = "part.editNewPartInWorkspace.title";

   @RBEntry("编辑部件")
   @RBComment("Used as the tooltip for the edit action for a new part in workspace")
   public static final String PRIVATE_CONSTANT_71 = "part.editNewPartInWorkspace.tooltip";

   @RBEntry("multi_update.gif")
   @RBPseudo(false)
   @RBComment("Icon for the edit part action (for toolbars)")
   public static final String PRIVATE_CONSTANT_72 = "part.editNewPartInWorkspace.icon";

   @RBEntry("height=500,width=600")
   @RBPseudo(false)
   @RBComment("DO NOT TRANSLATE")
   public static final String PRIVATE_CONSTANT_73 = "part.editNewPartInWorkspace.moreurlinfo";

   @RBEntry("编辑")
   @RBComment("Used as the label for the edit action for a part checked out in workspace")
   public static final String PRIVATE_CONSTANT_74 = "part.editCheckedOutPartInWorkspace.description";

   @RBEntry("编辑部件")
   @RBComment("Used as the label for the edit action for a new part in workspace")
   public static final String PRIVATE_CONSTANT_75 = "part.editCheckedOutPartInWorkspace.title";

   @RBEntry("编辑部件")
   @RBComment("Used as the tooltip for the edit action for a new part in workspace")
   public static final String PRIVATE_CONSTANT_76 = "part.editCheckedOutPartInWorkspace.tooltip";

   @RBEntry("multi_update.gif")
   @RBPseudo(false)
   @RBComment("Icon for the edit part action (for toolbars)")
   public static final String PRIVATE_CONSTANT_77 = "part.editCheckedOutPartInWorkspace.icon";

   @RBEntry("height=500,width=600")
   @RBPseudo(false)
   @RBComment("DO NOT TRANSLATE")
   public static final String PRIVATE_CONSTANT_78 = "part.editCheckedOutPartInWorkspace.moreurlinfo";

   /**
    * General Related Documents, CAD Docs from Part Details page
    **/
   @RBEntry("“说明方”文档")
   @RBComment("Related Items 3rd level nav Part Info page Documents")
   public static final String PRIVATE_CONSTANT_79 = "part.relatedPartsDocuments.description";

   @RBEntry("参考文档")
   @RBComment("Related Items 3rd level nav Part Info page Documents")
   public static final String PRIVATE_CONSTANT_80 = "part.relatedPartsReferences.description";

   @RBEntry("CAD/动态文档")
   @RBComment("Related Items 3rd level nav Part Info page CAD Documents")
   public static final String PRIVATE_CONSTANT_81 = "part.relatedPartsCADDocuments.description";

   @RBEntry("CAD 文档")
   @RBComment("Related Items 3rd level nav CAD Doc Info page CAD Documents")
   public static final String PRIVATE_CONSTANT_82 = "part.relatedCADDocsCADDocuments.description";

   @RBEntry("部件")
   @RBComment("Related Items 3rd level nav CAD Doc Info page parts")
   public static final String PRIVATE_CONSTANT_83 = "part.relatedCADDocsParts.description";

   /**
    * Related Documents from Part Details page
    **/
   @RBEntry("参考文档")
   @RBComment("used for the part info and doc info page references documents")
   public static final String REFERENCES_DOC_TABLE_HEADER = "REFERENCES_DOC_TABLE_HEADER";

   @RBEntry("“说明方”文档")
   @RBComment("used for the part info page described by documents")
   public static final String DESCRIBED_BY_DOC_TABLE_HEADER = "DESCRIBED_BY_DOC_TABLE_HEADER";

   @RBEntry("CAD/动态文档")
   @RBComment("used for the part info page described CAD")
   public static final String DESCRIBED_BY_CAD_DOC_TABLE_HEADER = "DESCRIBED_BY_CAD_DOC_TABLE_HEADER";

   @RBEntry("默认值")
   @RBComment("part info page default table view name")
   public static final String RELATED_DOC_TABLE_VIEW_DEFAULT_NAME = "RELATED_DOC_TABLE_VIEW_DEFAULT_NAME";

   @RBEntry("默认表格视图名称")
   @RBComment(" part info page description default table view name")
   public static final String RELATED_DOC_TABLE_VIEW_DEFAULT_DESCIP = "RELATED_DOC_TABLE_VIEW_DEFAULT_DESCIP";

   @RBEntry("确认: 部件将被检出。您要编辑的对象将检出供您编辑。")
   public static final String PART_AUTO_CHECKOUT_MSG = "PART_AUTO_CHECKOUT_MSG";

   @RBEntry("部件将被检出。您要编辑的对象将检出供您编辑。")
   public static final String PSB_PART_AUTO_CHECKOUT_MSG = "PSB_PART_AUTO_CHECKOUT_MSG";

   @RBEntry("分类")
   @RBComment("Classifications table header.")
   public static final String Classifications = "CLASSIFICATIONS";

   @RBEntry("分类属性")
   @RBComment("Classifications Attributes step table header.")
   public static final String CLASSIFICATION_ATTRS_TABLE_HEADER = "CLASSIFICATION_ATTRS_TABLE_HEADER";

   @RBEntry("移除")
   @RBComment("Used as the toolbar description for Remove References")
   public static final String PRIVATE_CONSTANT_84 = "part.related_delete_references.description";

   @RBEntry("移除选定的")
   @RBComment("Used for tooltip for the Remove References")
   public static final String PRIVATE_CONSTANT_85 = "part.related_delete_references.tooltip";

   @RBEntry("remove16x16.gif")
   @RBPseudo(false)
   @RBComment("Used for the icon for the Remove References")
   public static final String PRIVATE_CONSTANT_86 = "part.related_delete_references.icon";

   @RBEntry("移除")
   @RBComment("Used as the toolbar description for Remove Described By Link")
   public static final String PRIVATE_CONSTANT_87 = "part.related_delete_described.description";

   @RBEntry("移除选定的")
   @RBComment("Used as the tooltip for Remove Described By Link")
   public static final String PRIVATE_CONSTANT_88 = "part.related_delete_described.tooltip";

   @RBEntry("remove16x16.gif")
   @RBPseudo(false)
   @RBComment("Used as the icon for Remove Described By Link")
   public static final String PRIVATE_CONSTANT_89 = "part.related_delete_described.icon";

   @RBEntry("复制")
   @RBComment("Used as the toolbar description for copy to clipboard")
   public static final String PRIVATE_CONSTANT_90 = "part.related_copy_references.description";

   @RBEntry("复制选定对象")
   @RBComment("Used as the tooltip for copy to clipboard")
   public static final String PRIVATE_CONSTANT_91 = "part.related_copy_references.tooltip";

   @RBEntry("copy.gif")
   @RBPseudo(false)
   @RBComment("Used as the icon for copy to clipboard")
   public static final String PRIVATE_CONSTANT_92 = "part.related_copy_references.icon";

   @RBEntry("export_collapse.gif")
   @RBPseudo(false)
   @RBComment("Used as the disabled icon for copy to clipboard")
   public static final String PRIVATE_CONSTANT_93 = "part.related_copy_references.disabled_icon";

   @RBEntry("粘贴")
   @RBComment("Used as the toolbar description for paste from clipboard")
   public static final String PRIVATE_CONSTANT_94 = "part.related_paste_references.description";

   @RBEntry("粘贴")
   @RBComment("Used as the tooltip for paste from clipboard")
   public static final String PRIVATE_CONSTANT_95 = "part.related_paste_references.tooltip";

   @RBEntry("paste.gif")
   @RBPseudo(false)
   @RBComment("Used as the icon for paste from clipboard")
   public static final String PRIVATE_CONSTANT_96 = "part.related_paste_references.icon";

   @RBEntry("粘贴")
   @RBComment("Used as the toolbar description for paste from clipboard")
   public static final String PRIVATE_CONSTANT_97 = "part.related_paste_described.description";

   @RBEntry("粘贴")
   @RBComment("Used as the tooltip for paste from clipboard")
   public static final String PRIVATE_CONSTANT_98 = "part.related_paste_described.tooltip";

   @RBEntry("paste.gif")
   @RBPseudo(false)
   @RBComment("Used as the icon for paste from clipboard")
   public static final String PRIVATE_CONSTANT_99 = "part.related_paste_described.icon";

   @RBEntry("粘贴选择对象")
   @RBComment("Used as the toolbar description for paste Select from clipboard")
   public static final String PRIVATE_CONSTANT_100 = "part.related_paste_select_references.description";

   @RBEntry("粘贴选择对象 ")
   @RBComment("Used as the tooltip for paste Select from clipboard")
   public static final String PRIVATE_CONSTANT_101 = "part.related_paste_select_references.tooltip";

   @RBEntry("paste_select.gif")
   @RBPseudo(false)
   @RBComment("Used as the icon for paste Select from clipboard")
   public static final String PRIVATE_CONSTANT_102 = "part.related_paste_select_references.icon";

   @RBEntry("height=550,width=700")
   @RBPseudo(false)
   @RBComment("Used as the popup size for paste Select from clipboard")
   public static final String PRIVATE_CONSTANT_103 = "part.related_paste_select_references.moreurlinfo";

   @RBEntry("粘贴选择的参考文档")
   @RBComment("Used as the title for paste Select from clipboard")
   public static final String PRIVATE_CONSTANT_104 = "part.related_paste_select_references.title";

   @RBEntry("粘贴选择对象")
   @RBComment("Used as the toolbar description for paste Select from clipboard")
   public static final String PRIVATE_CONSTANT_105 = "part.related_paste_select_described.description";

   @RBEntry("粘贴选择对象 ")
   @RBComment("Used as the tooltip for paste Select from clipboard")
   public static final String PRIVATE_CONSTANT_106 = "part.related_paste_select_described.tooltip";

   @RBEntry("paste_select.gif")
   @RBPseudo(false)
   @RBComment("Used as the icon for paste Select from clipboard")
   public static final String PRIVATE_CONSTANT_107 = "part.related_paste_select_described.icon";

   @RBEntry("height=550,width=700")
   @RBPseudo(false)
   @RBComment("Used as the popup size for paste Select from clipboard")
   public static final String PRIVATE_CONSTANT_108 = "part.related_paste_select_described.moreurlinfo";

   @RBEntry("粘贴选择的“说明方”文档")
   @RBComment("Used as the title for paste Select from clipboard")
   public static final String PRIVATE_CONSTANT_109 = "part.related_paste_select_described.title";

   @RBEntry("添加")
   @RBComment("Used as the toolbar description for add documents link")
   public static final String PRIVATE_CONSTANT_110 = "part.related_add_references.description";

   @RBEntry("添加现有文档")
   @RBComment("Used as the tooltip for add documents link")
   public static final String PRIVATE_CONSTANT_111 = "part.related_add_references.tooltip";

   @RBEntry("add16x16.gif")
   @RBPseudo(false)
   @RBComment("Used as the icon for add documents link")
   public static final String PRIVATE_CONSTANT_112 = "part.related_add_references.icon";

   @RBEntry("height=950,width=1000")
   @RBPseudo(false)
   @RBComment("Used as the popup size for add documents link")
   public static final String PRIVATE_CONSTANT_113 = "part.related_add_references.moreurlinfo";

   @RBEntry("添加参考文档")
   @RBComment("Used as the title for add documents link")
   public static final String PRIVATE_CONSTANT_114 = "part.related_add_references.title";

   @RBEntry("添加")
   @RBComment("Used as the toolbar description for add documents link")
   public static final String PRIVATE_CONSTANT_115 = "part.related_add_described.description";

   @RBEntry("添加现有文档")
   @RBComment("Used as the tooltip for add documents link")
   public static final String PRIVATE_CONSTANT_116 = "part.related_add_described.tooltip";

   @RBEntry("add16x16.gif")
   @RBPseudo(false)
   @RBComment("Used as the icon for add documents link")
   public static final String PRIVATE_CONSTANT_117 = "part.related_add_described.icon";

   @RBEntry("height=950,width=1000")
   @RBPseudo(false)
   @RBComment("Used as the popup size for add documents link")
   public static final String PRIVATE_CONSTANT_118 = "part.related_add_described.moreurlinfo";

   @RBEntry("添加说明方文档")
   @RBComment("Used as the title for add documents link")
   public static final String PRIVATE_CONSTANT_119 = "part.related_add_described.title";

   /**
    * Related Parts from Document Details page
    **/
   @RBEntry("说明部件")
   @RBComment("This string is used for the document info page describes parts table header")
   public static final String DESCRIBES_PARTS_TABLE_HEADER = "DESCRIBES_PARTS_TABLE_HEADER";

   @RBEntry("参考方部件")
   @RBComment("This string is used for the document info page referenced by parts table header")
   public static final String REFERENCED_BY_PARTS_TABLE_HEADER = "REFERENCED_BY_PARTS_TABLE_HEADER";

   @RBEntry("添加")
   @RBComment("Used as the toolbar description for add parts link")
   public static final String PRIVATE_CONSTANT_120 = "part.related_add_references_docpart.description";

   @RBEntry("添加参考方部件")
   @RBComment("Used as the tooltip for add parts link")
   public static final String PRIVATE_CONSTANT_121 = "part.related_add_references_docpart.tooltip";

   @RBEntry("add16x16.gif")
   @RBPseudo(false)
   @RBComment("Used as the icon for add documents link")
   public static final String PRIVATE_CONSTANT_122 = "part.related_add_references_docpart.icon";

   @RBEntry("height=950,width=1000")
   @RBPseudo(false)
   @RBComment("Used as the popup size for add documents link")
   public static final String PRIVATE_CONSTANT_123 = "part.related_add_references_docpart.moreurlinfo";

   @RBEntry("添加参考方部件")
   @RBComment("Used as the title for add documents link")
   public static final String PRIVATE_CONSTANT_124 = "part.related_add_references_docpart.title";

   @RBEntry("添加")
   @RBComment("Used as the toolbar description for add parts link")
   public static final String PRIVATE_CONSTANT_125 = "part.related_add_described_docpart.description";

   @RBEntry("添加说明部件")
   @RBComment("Used as the tooltip for add parts link")
   public static final String PRIVATE_CONSTANT_126 = "part.related_add_described_docpart.tooltip";

   @RBEntry("add16x16.gif")
   @RBPseudo(false)
   @RBComment("Used as the icon for add documents link")
   public static final String PRIVATE_CONSTANT_127 = "part.related_add_described_docpart.icon";

   @RBEntry("height=950,width=1000")
   @RBPseudo(false)
   @RBComment("Used as the popup size for add documents link")
   public static final String PRIVATE_CONSTANT_128 = "part.related_add_described_docpart.moreurlinfo";

   @RBEntry("添加说明部件")
   @RBComment("Used as the title for add documents link")
   public static final String PRIVATE_CONSTANT_129 = "part.related_add_described_docpart.title";

   @RBEntry("关联新建内容")
   @RBComment("Used as the toolbar description for New on part info page")
   public static final String PRIVATE_CONSTANT_130 = "part.related_part_create_wizard_doc_described.description";

   @RBEntry("创建并关联新说明部件")
   @RBComment("Used as the tooltip for New on part info page")
   public static final String PRIVATE_CONSTANT_131 = "part.related_part_create_wizard_doc_described.tooltip";

   @RBEntry("newpart.gif")
   @RBPseudo(false)
   @RBComment("Used as the icon for New on part info page")
   public static final String PRIVATE_CONSTANT_132 = "part.related_part_create_wizard_doc_described.icon";

   @RBEntry("新建部件")
   @RBComment("Used as the title for Associate New on part info page")
   public static final String PRIVATE_CONSTANT_133 = "part.related_part_create_wizard_doc_described.title";

   @RBEntry("height=700,width=800")
   @RBPseudo(false)
   @RBComment("Used as the popup size for New on part info page")
   public static final String PRIVATE_CONSTANT_134 = "part.related_part_create_wizard_doc_described.moreurlinfo";

   @RBEntry("关联新建内容")
   @RBComment("Used as the toolbar description for New on part info page")
   public static final String PRIVATE_CONSTANT_135 = "part.related_part_create_wizard_doc_references.description";

   @RBEntry("创建并关联新参考方部件")
   @RBComment("Used as the tooltip for New on part info page")
   public static final String PRIVATE_CONSTANT_136 = "part.related_part_create_wizard_doc_references.tooltip";

   @RBEntry("newpart.gif")
   @RBPseudo(false)
   @RBComment("Used as the icon for New on part info page")
   public static final String PRIVATE_CONSTANT_137 = "part.related_part_create_wizard_doc_references.icon";

   @RBEntry("新建部件")
   @RBComment("Used as the title for Associate New on for part info page")
   public static final String PRIVATE_CONSTANT_138 = "part.related_part_create_wizard_doc_references.title";

   @RBEntry("height=700,width=800")
   @RBPseudo(false)
   @RBComment("Used as the popup size for New on part info page")
   public static final String PRIVATE_CONSTANT_139 = "part.related_part_create_wizard_doc_references.moreurlinfo";

   @RBEntry("粘贴选择对象")
   @RBComment("Used as the toolbar description for paste Select from clipboard")
   public static final String PRIVATE_CONSTANT_140 = "part.related_paste_select_references_part.description";

   @RBEntry("从剪贴板粘贴选择的“参考方”部件")
   @RBComment("Used as the tooltip for paste Select from clipboard")
   public static final String PRIVATE_CONSTANT_141 = "part.related_paste_select_references_part.tooltip";

   @RBEntry("paste_select.gif")
   @RBPseudo(false)
   @RBComment("Used as the icon for paste Select from clipboard")
   public static final String PRIVATE_CONSTANT_142 = "part.related_paste_select_references_part.icon";

   @RBEntry("height=550,width=700")
   @RBPseudo(false)
   @RBComment("Used as the popup size for paste Select from clipboard")
   public static final String PRIVATE_CONSTANT_143 = "part.related_paste_select_references_part.moreurlinfo";

   @RBEntry("粘贴选择的“参考方”部件")
   @RBComment("Used as the title for paste Select from clipboard")
   public static final String PRIVATE_CONSTANT_144 = "part.related_paste_select_references_part.title";

   @RBEntry("粘贴选择对象")
   @RBComment("Used as the toolbar description for paste Select from clipboard")
   public static final String PRIVATE_CONSTANT_145 = "part.related_paste_select_described_part.description";

   @RBEntry("从剪贴板粘贴选择的“说明部件”")
   @RBComment("Used as the tooltip for paste Select from clipboard")
   public static final String PRIVATE_CONSTANT_146 = "part.related_paste_select_described_part.tooltip";

   @RBEntry("paste_select.gif")
   @RBPseudo(false)
   @RBComment("Used as the icon for paste Select from clipboard")
   public static final String PRIVATE_CONSTANT_147 = "part.related_paste_select_described_part.icon";

   @RBEntry("height=550,width=700")
   @RBPseudo(false)
   @RBComment("Used as the popup size for paste Select from clipboard")
   public static final String PRIVATE_CONSTANT_148 = "part.related_paste_select_described_part.moreurlinfo";

   @RBEntry("粘贴选择的“说明部件”")
   @RBComment("Used as the title for paste Select from clipboard")
   public static final String PRIVATE_CONSTANT_149 = "part.related_paste_select_described_part.title";

   @RBEntry("粘贴")
   @RBComment("Used as the toolbar description for paste from clipboard")
   public static final String PRIVATE_CONSTANT_150 = "part.related_paste_references_part.description";

   @RBEntry("从剪贴板粘贴参考方部件")
   @RBComment("Used as the tooltip for paste from clipboard")
   public static final String PRIVATE_CONSTANT_151 = "part.related_paste_references_part.tooltip";

   @RBEntry("paste.gif")
   @RBPseudo(false)
   @RBComment("Used as the icon for paste from clipboard")
   public static final String PRIVATE_CONSTANT_152 = "part.related_paste_references_part.icon";

   @RBEntry("粘贴")
   @RBComment("Used as the toolbar description for paste from clipboard")
   public static final String PRIVATE_CONSTANT_153 = "part.related_paste_described_part.description";

   @RBEntry("从剪贴板粘贴说明部件")
   @RBComment("Used as the tooltip for paste from clipboard")
   public static final String PRIVATE_CONSTANT_154 = "part.related_paste_described_part.tooltip";

   @RBEntry("paste.gif")
   @RBPseudo(false)
   @RBComment("Used as the icon for paste from clipboard")
   public static final String PRIVATE_CONSTANT_155 = "part.related_paste_described_part.icon";

   @RBEntry("移除")
   @RBComment("Used as the toolbar description for Remove Referenced By Link")
   public static final String PRIVATE_CONSTANT_156 = "part.related_delete_references_part.description";

   @RBEntry("移除参考方部件")
   @RBComment("Used for tooltip for the Remove Referenced By Link")
   public static final String PRIVATE_CONSTANT_157 = "part.related_delete_references_part.tooltip";

   @RBEntry("remove16x16.gif")
   @RBPseudo(false)
   @RBComment("Used for the icon for the Remove Referenced By Link")
   public static final String PRIVATE_CONSTANT_158 = "part.related_delete_references_part.icon";

   @RBEntry("移除")
   @RBComment("Used as the toolbar description for Remove Describes Link")
   public static final String PRIVATE_CONSTANT_159 = "part.related_delete_described_part.description";

   @RBEntry("移除说明部件")
   @RBComment("Used as the tooltip for Remove Describes Link")
   public static final String PRIVATE_CONSTANT_160 = "part.related_delete_described_part.tooltip";

   @RBEntry("remove16x16.gif")
   @RBPseudo(false)
   @RBComment("Used as the icon for Remove Describes Link")
   public static final String PRIVATE_CONSTANT_161 = "part.related_delete_described_part.icon";

   @RBEntry("复制")
   @RBComment("Used as the toolbar description for copy to clipboard")
   public static final String PRIVATE_CONSTANT_162 = "part.related_copy_references_part.description";

   @RBEntry("复制到剪切板")
   @RBComment("Used as the tooltip for copy to clipboard")
   public static final String PRIVATE_CONSTANT_163 = "part.related_copy_references_part.tooltip";

   @RBEntry("copy.gif")
   @RBPseudo(false)
   @RBComment("Used as the icon for copy to clipboard")
   public static final String PRIVATE_CONSTANT_164 = "part.related_copy_references_part.icon";

   @RBEntry("export_collapse.gif")
   @RBPseudo(false)
   @RBComment("Used as the disabled icon for copy to clipboard")
   public static final String PRIVATE_CONSTANT_165 = "part.related_copy_references_part.disabled_icon";

   /**
    * This is where the association should be coming from but there is a build order issue
    * /wt_ma_services/modules/UwgmCadx/src/com\ptc/windchill/uwgm/cadx/associate/associateResource.rbInfo
    * ASSOCTYPE_ACTIVE = Owner
    * ASSOCTYPE_PASSIVE = Content
    * ASSOCTYPE_CALCULATED = Calculated
    **/
   @RBEntry("所有者")
   public static final String ASSOCTYPE_ACTIVE = "ASSOCTYPE_ACTIVE";

   @RBEntry("内容")
   public static final String ASSOCTYPE_PASSIVE = "ASSOCTYPE_PASSIVE";

   @RBEntry("计算的")
   public static final String ASSOCTYPE_CALCULATED = "ASSOCTYPE_CALCULATED";

   /**
    * End Related documents and parts
    **/
   @RBEntry("AML/AVL")
   @RBComment("Used as the label for the AML AVL action for a part")
   public static final String PRIVATE_CONSTANT_166 = "part.AXL.description";

   @RBEntry("AML/AVL")
   @RBComment("Used as the label for the AML AVL action for a part")
   public static final String PRIVATE_CONSTANT_167 = "part.AXL.title";

   @RBEntry("AML/AVL")
   @RBComment("Used as the label for the AML AVL action for a part")
   public static final String PRIVATE_CONSTANT_168 = "part.AXL.tooltip";

   @RBEntry("height=500,width=475")
   @RBPseudo(false)
   @RBComment("DO NOT TRANSLATE")
   public static final String PRIVATE_CONSTANT_169 = "part.AXL.moreurlinfo";

   @RBEntry("编辑公用属性")
   @RBComment("Used as the label for the Edit Common Attributes action for a part")
   public static final String PRIVATE_CONSTANT_170 = "part.editPartCommonAttrsWizard.description";

   @RBEntry("编辑公用属性")
   @RBComment("Used as the label for the Edit Common Attributes action for a part")
   public static final String PRIVATE_CONSTANT_171 = "part.editPartCommonAttrsWizard.title";

   @RBEntry(" 编辑公用属性")
   @RBComment("Used as the tooltip for the Edit Common Attributes action for a part")
   public static final String PRIVATE_CONSTANT_172 = "part.editPartCommonAttrsWizard.tooltip";

   @RBEntry("attribute_edit.gif")
   @RBPseudo(false)
   @RBComment("Icon for the edit part common attributes action (for toolbars)")
   public static final String PRIVATE_CONSTANT_173 = "part.editPartCommonAttrsWizard.icon";

   @RBEntry("height=550,width=750")
   @RBPseudo(false)
   @RBComment("DO NOT TRANSLATE")
   public static final String PRIVATE_CONSTANT_174 = "part.editPartCommonAttrsWizard.moreurlinfo";

   @RBEntry("设置属性")
   @RBComment("Used as the label for the Set Attributes step of Edit Common Attributes action")
   public static final String PRIVATE_CONSTANT_175 = "part.editPartCommonAttrsWizardStep.description";

   @RBEntry("设置属性")
   @RBComment("Used as the label for the Set Attributes step of Edit Common Attributes action")
   public static final String PRIVATE_CONSTANT_176 = "part.editPartCommonAttrsWizardStep.title";

   @RBEntry("设置属性")
   @RBComment("Used as the label for the Set Attributes step of Edit Common Attributes action")
   public static final String PRIVATE_CONSTANT_177 = "part.editPartCommonAttrsWizardStep.tooltip";

   @RBEntry("编辑公用属性")
   @RBComment("Used as the label for the Edit Common Attributes action for a part")
   public static final String PRIVATE_CONSTANT_178 = "part.editPartCommonAttrsAttrTableWizard.description";

   @RBEntry("编辑公用属性")
   @RBComment("Used as the label for the Edit Common Attributes action for a part")
   public static final String PRIVATE_CONSTANT_179 = "part.editPartCommonAttrsAttrTableWizard.title";

   @RBEntry(" 编辑公用属性")
   @RBComment("Used as the tooltip for the Edit Common Attributes action for a part")
   public static final String PRIVATE_CONSTANT_180 = "part.editPartCommonAttrsAttrTableWizard.tooltip";

   @RBEntry("attribute_edit.gif")
   @RBPseudo(false)
   @RBComment("Icon for the edit part common attributes action (for toolbars)")
   public static final String PRIVATE_CONSTANT_181 = "part.editPartCommonAttrsAttrTableWizard.icon";

   @RBEntry("height=500,width=750")
   @RBPseudo(false)
   @RBComment("DO NOT TRANSLATE")
   public static final String PRIVATE_CONSTANT_182 = "part.editPartCommonAttrsAttrTableWizard.moreurlinfo";

   @RBEntry("重新分配视图")
   @RBComment("Used as the label for Reassign View action")
   public static final String PRIVATE_CONSTANT_183 = "part.assignView.description";

   @RBEntry("重新分配视图")
   @RBComment("Used as the label for Reassign View action")
   public static final String PRIVATE_CONSTANT_184 = "part.assignView.title";

   @RBEntry("重新分配视图")
   @RBComment("Used as the label for Reassign View action")
   public static final String PRIVATE_CONSTANT_185 = "part.assignView.tooltip";

   @RBEntry("height=200,width=600")
   @RBPseudo(false)
   @RBComment("DO NOT TRANSLATE")
   public static final String PRIVATE_CONSTANT_186 = "part.assignView.moreurlinfo";

   @RBEntry("view_reassign.gif")
   @RBPseudo(false)
   @RBComment("Icon for Reassign View action")
   public static final String PRIVATE_CONSTANT_187 = "part.assignView.icon";

   @RBEntry("全局替换部件")
   public static final String PRIVATE_CONSTANT_188 = "part.alternates.description";

   @RBEntry("全局替换部件")
   public static final String PRIVATE_CONSTANT_189 = "part.alternates.title";

   @RBEntry("将全局替换")
   @RBComment("Label to use as the menu option for the \"Alternate For\" table in the \"My Tab\" menu which lets you choose which tables to display on the info page. Probably should match the ALTERNATESFOR_LABEL.")
   public static final String PRIVATE_CONSTANT_190 = "part.alternateFor.description";

   @RBEntry("特定替换部件")
   public static final String PRIVATE_CONSTANT_191 = "part.substitutes.description";

   @RBEntry("特定替换部件")
   public static final String PRIVATE_CONSTANT_192 = "part.substitutes.title";

   @RBEntry("用于特定替换")
   public static final String PRIVATE_CONSTANT_193 = "part.substitutesFor.description";

   @RBEntry("用于特定替换")
   public static final String PRIVATE_CONSTANT_194 = "part.substitutesFor.title";

   @RBEntry("管理替换部件")
   public static final String PRIVATE_CONSTANT_195 = "part.replacements.description";

   @RBEntry("管理替换部件")
   public static final String PRIVATE_CONSTANT_196 = "part.replacements.title";

   @RBEntry("height=500,width=1000")
   @RBPseudo(false)
   @RBComment("DO NOT TRANSLATE")
   public static final String PRIVATE_CONSTANT_197 = "part.replacements.moreurlinfo";

   @RBEntry("../../com/ptc/core/ui/images/replacement_manage.gif")
   @RBPseudo(false)
   @RBComment("Icon that appears in the see action drop down list")
   public static final String PRIVATE_CONSTANT_198 = "part.replacements.icon";

   @RBEntry("取消")
   @RBComment("Used as the label for the Cancel button")
   public static final String PRIVATE_CONSTANT_199 = "part.replacementsCancelButton.description";

   @RBEntry("取消")
   @RBComment("Used as the title for Cancel button")
   public static final String PRIVATE_CONSTANT_200 = "part.replacementsCancelButton.title";

   @RBEntry("取消")
   @RBComment("Used as the tooltip for Cancel button")
   public static final String PRIVATE_CONSTANT_201 = "part.replacementsCancelButton.tooltip";

   @RBEntry("确定")
   @RBComment("Used as the label for the Ok button")
   public static final String PRIVATE_CONSTANT_202 = "part.replacementsOkButton.description";

   @RBEntry("确定")
   @RBComment("Used as the title for Ok button")
   public static final String PRIVATE_CONSTANT_203 = "part.replacementsOkButton.title";

   @RBEntry("确定")
   @RBComment("Used as the tooltip for Ok button")
   public static final String PRIVATE_CONSTANT_204 = "part.replacementsOkButton.tooltip";

   @RBEntry("分配")
   public static final String PRIVATE_CONSTANT_205 = "part.allocations.description";

   @RBEntry("关联的更改")
   public static final String PRIVATE_CONSTANT_206 = "part.associatedChanges.description";

   @RBEntry("生命周期历史记录")
   public static final String PRIVATE_CONSTANT_207 = "object.showLifeCycleHistory.description";

   @RBEntry("双向")
   public static final String PRIVATE_CONSTANT_208 = "part.toggleTwoway.description";

   @RBEntry("表示双向关系")
   public static final String PRIVATE_CONSTANT_209 = "part.toggleTwoway.tooltip";

   @RBEntry("../../wtcore/images/worksp.gif")
   public static final String PRIVATE_CONSTANT_210 = "part.toggleTwoway.icon";

   @RBEntry("../../wtcore/images/replacement_add.gif")
   public static final String PRIVATE_CONSTANT_211 = "part.addAlternateLinks.icon";

   @RBEntry("添加全局替换部件")
   public static final String PRIVATE_CONSTANT_212 = "part.addAlternateLinks.description";

   @RBEntry("添加全局替换部件")
   public static final String PRIVATE_CONSTANT_213 = "part.addAlternateLinks.tooltip";

   @RBEntry("remove16x16.gif")
   @RBComment("DO NOT TRANSLATE")
   public static final String PART_REMOVE_ALTERNATELINK_ICON = "part.removeAlternateLinks.icon";

   @RBEntry("移除全局替换部件")
   @RBComment("Toolbar button to remove an alternate part")
   public static final String PART_REMOVE_ALTERNATELINK_DESCRIPTION = "part.removeAlternateLinks.description";

   @RBEntry("移除全局替换部件")
   @RBComment("Toolbar button to remove an alternate part")
   public static final String PART_REMOVE_ALTERNATELINK_TOOLTIP = "part.removeAlternateLinks.tooltip";

   @RBEntry("../../wtcore/images/substitute_add.gif")
   public static final String PRIVATE_CONSTANT_214 = "part.addSubstituteLinks.icon";

   @RBEntry("添加特定替换部件")
   public static final String PRIVATE_CONSTANT_215 = "part.addSubstituteLinks.description";

   @RBEntry("添加特定替换部件")
   public static final String PRIVATE_CONSTANT_216 = "part.addSubstituteLinks.tooltip";

   @RBEntry("remove16x16.gif")
   @RBComment("DO NOT TRANSLATE")
   public static final String PART_REMOVE_SUBSTITUTELINK_ICON = "part.removeSubstituteLinks.icon";

   @RBEntry("移除特定替换部件")
   @RBComment("Toolbar button to remove a substitute part")
   public static final String PART_REMOVE_SUBSTITUTELINK_DESCRIPTION = "part.removeSubstituteLinks.description";

   @RBEntry("移除特定替换部件")
   @RBComment("Toolbar button to remove a substitute part")
   public static final String PART_REMOVE_SUBSTITUTELINK_TOOLTIP = "part.removeSubstituteLinks.tooltip";

   @RBEntry("add16x16.gif")
   public static final String PRIVATE_CONSTANT_217 = "part.addAlternateLinksThirdNav.icon";

   @RBEntry("添加")
   public static final String PRIVATE_CONSTANT_218 = "part.addAlternateLinksThirdNav.description";

   @RBEntry("添加全局替换部件")
   public static final String PRIVATE_CONSTANT_219 = "part.addAlternateLinksThirdNav.tooltip";

   @RBEntry("../../wtcore/images/replacement_add.gif")
   public static final String PRIVATE_CONSTANT_220 = "part.addSubstituteLinksThirdNav.icon";

   @RBEntry("添加特定替换部件")
   public static final String PRIVATE_CONSTANT_221 = "part.addSubstituteLinksThirdNav.description";

   @RBEntry("添加特定替换部件")
   public static final String PRIVATE_CONSTANT_222 = "part.addSubstituteLinksThirdNav.tooltip";

   @RBEntry("分配部件版本")
   public static final String PRIVATE_CONSTANT_223 = "part.assignPartVersion.description";

   @RBEntry("分配部件版本")
   public static final String PRIVATE_CONSTANT_224 = "part.assignPartVersion.title";

   @RBEntry("覆盖部件版本")
   public static final String PRIVATE_CONSTANT_225 = "part.overridePartVersion.description";

   @RBEntry("覆盖部件版本")
   public static final String PRIVATE_CONSTANT_226 = "part.overridePartVersion.title";

   @RBEntry("添加说明方文档")
   public static final String PRIVATE_CONSTANT_227 = "part.associate_document_to_part_described.description";

   @RBEntry("添加说明方文档")
   public static final String PRIVATE_CONSTANT_228 = "part.associate_document_to_part_described.title";

   @RBEntry("iconAssociate.gif")
   @RBPseudo(false)
   @RBComment("Icon that appears in the see action drop down list")
   public static final String PRIVATE_CONSTANT_229 = "part.associate_document_to_part_described.icon";

   @RBEntry("替换")
   public static final String PRIVATE_CONSTANT_233 = "part.partInstance_replacePart.description";

   @RBEntry("替换部件")
   public static final String PRIVATE_CONSTANT_234 = "part.partInstance_replacePart.title";

   @RBEntry("height=500,width=500")
   @RBPseudo(false)
   @RBComment("DO NOT TRANSLATE")
   public static final String PRIVATE_CONSTANT_235 = "part.partInstance_replacePart.moreurlinfo";

   @RBEntry("撤消替换")
   public static final String PRIVATE_CONSTANT_236 = "part.partInstance_undo_replacement.description";

   @RBEntry("撤消替换部件")
   public static final String PRIVATE_CONSTANT_237 = "part.partInstance_undo_replacement.title";

   @RBEntry("替换")
   public static final String PRIVATE_CONSTANT_238 = "part.replacePart.description";

   @RBEntry("替换部件")
   public static final String PRIVATE_CONSTANT_239 = "part.replacePart.title";

   @RBEntry("../../com/ptc/core/ui/images/replace.gif")
   @RBPseudo(false)
   @RBComment("Icon that appears in the see action drop down list")
   public static final String PRIVATE_CONSTANT_240 = "part.replacePart.icon";

   @RBEntry("确定要撤消替换部件 {0} - {1}  - {2} 吗?")
   @RBComment("The confirmation message when a user tries to undo replacement from part instance.")
   @RBArgComment0("Part number")
   @RBArgComment1("Part manufacture id")
   @RBArgComment2("Part name")
   public static final String CONFIRM_PARTINSTANCE_UNDO_REPLACEMENT = "CONFIRM_PARTINSTANCE_UNDO_REPLACEMENT";

   @RBEntry("替换部件")
   public static final String REPLACEMENT_PART_HEADER = "REPLACEMENT_PART_HEADER";

   @RBEntry("关联文档")
   public static final String ASSOCIATE_DOCUMENT_SEARCH_HEADER = "ASSOCIATE_DOCUMENT_SEARCH_HEADER";

   @RBEntry("设置为主要成品")
   @RBComment("Used as the label for Set As Primary End Item action on End items tab in Product Details Page")
   public static final String PRIVATE_CONSTANT_241 = "part.makePrimaryEndItem.description";

   @RBEntry("设置为主要成品")
   @RBComment("Used as the tooltip for Set As Primary End Item action on End items tab in Product Details Page")
   public static final String PRIVATE_CONSTANT_242 = "part.makePrimaryEndItem.tooltip";

   @RBEntry("取消设置主要成品")
   @RBComment("Used as the label for Unset Primary End Item action on End items tab in Product Details Page")
   public static final String PRIVATE_CONSTANT_243 = "part.removePrimaryEndItem.description";

   @RBEntry("取消设置主要成品")
   @RBComment("Used as the tooltip for Unset Primary End Item action on End items tab in Product Details Page")
   public static final String PRIVATE_CONSTANT_244 = "part.removePrimaryEndItem.tooltip";

   @RBEntry("关联文档")
   @RBComment("Used as the label for Associate Documents action on PSB table")
   public static final String PRIVATE_CONSTANT_245 = "part.ASSOCIATEDOCUMENTSTOPART.description";

   @RBEntry("关联文档")
   @RBComment("Used as the tooltip for Associate Documents action on PSB table")
   public static final String PRIVATE_CONSTANT_246 = "part.ASSOCIATEDOCUMENTSTOPART.tooltip";

   @RBEntry("iconAssociate.gif")
   @RBPseudo(false)
   @RBComment("Used as the icon for Associate Documents action on PSB table")
   public static final String PRIVATE_CONSTANT_247 = "part.ASSOCIATEDOCUMENTSTOPART.icon";

   /**
    * Tabular-input specific entries
    **/
   @RBEntry("编辑物料清单")
   @RBComment("Title for edit structure pop up window")
   public static final String PRIVATE_CONSTANT_248 = "part.tabular_input.title";

   @RBEntry("编辑物料清单")
   @RBComment("Action name (description) that shows up in 'actions' link")
   public static final String PRIVATE_CONSTANT_249 = "part.tabular_input.description";

   @RBEntry("编辑物料清单")
   public static final String PRIVATE_CONSTANT_250 = "part.tabular_input.tooltip";

   @RBEntry("multi_update.gif")
   @RBComment("Icon for the Edit Bill of Materials action ")
   public static final String PRIVATE_CONSTANT_251 = "part.tabular_input.icon";

   @RBEntry("height=580,width=1000")
   @RBPseudo(false)
   @RBComment("DO NOT TRANSLATE")
   public static final String PRIVATE_CONSTANT_252 = "part.tabular_input.moreurlinfo";

   @RBEntry("编辑物料清单")
   @RBComment("Title for edit structure pop up window")
   public static final String PRIVATE_CONSTANT_253 = "part.psb_tabular_input.title";

   @RBEntry("编辑物料清单")
   @RBComment("Action name (description) that shows up in 'actions' link")
   public static final String PRIVATE_CONSTANT_254 = "part.psb_tabular_input.description";

   @RBEntry("编辑物料清单")
   public static final String PRIVATE_CONSTANT_255 = "part.psb_tabular_input.tooltip";

   @RBEntry("multi_update.gif")
   @RBComment("Icon for the Edit Structure action ")
   public static final String PRIVATE_CONSTANT_256 = "part.psb_tabular_input.icon";

   @RBEntry("height=580,width=1000")
   @RBPseudo(false)
   @RBComment("DO NOT TRANSLATE")
   public static final String PRIVATE_CONSTANT_257 = "part.psb_tabular_input.moreurlinfo";

   @RBEntry("粘贴部件")
   @RBComment("Tooltip for this action that shows up in the client action toolbar")
   public static final String PRIVATE_CONSTANT_258 = "part.ti_paste_part.tooltip";

   @RBEntry("粘贴部件")
   @RBComment("Localize the same way as tooltip")
   public static final String PRIVATE_CONSTANT_259 = "part.ti_paste_part.description";

   @RBEntry("height=375,width=485")
   @RBPseudo(false)
   public static final String PRIVATE_CONSTANT_260 = "part.ti_paste_part.moreurlinfo";

   @RBEntry("paste.gif")
   @RBPseudo(false)
   public static final String PRIVATE_CONSTANT_261 = "part.ti_paste_part.icon";

   @RBEntry("选择要粘贴的部件")
   @RBComment("Title of the Paste Select popup window")
   public static final String PRIVATE_CONSTANT_262 = "part.ti_paste_select_part.title";

   @RBEntry("选择要粘贴的部件")
   @RBComment("Tooltip for this action that shows up in the client action toolbar")
   public static final String PRIVATE_CONSTANT_263 = "part.ti_paste_select_part.tooltip";

   @RBEntry("粘贴选择对象")
   @RBComment("Localize the same way as tooltip")
   public static final String PRIVATE_CONSTANT_264 = "part.ti_paste_select_part.description";

   @RBEntry("paste_select.gif")
   @RBPseudo(false)
   public static final String PRIVATE_CONSTANT_265 = "part.ti_paste_select_part.icon";

   @RBEntry("height=550,width=700")
   @RBPseudo(false)
   public static final String PRIVATE_CONSTANT_266 = "part.ti_paste_select_part.moreurlinfo";

   @RBEntry("查找并添加部件")
   public static final String PRIVATE_CONSTANT_267 = "part.ti_add_part.title";

   @RBEntry("查找并添加部件")
   public static final String PRIVATE_CONSTANT_268 = "part.ti_add_part.tooltip";

   @RBEntry("查找并添加部件")
   public static final String PRIVATE_CONSTANT_269 = "part.ti_add_part.description";

   @RBEntry("add16x16.gif")
   @RBPseudo(false)
   public static final String PRIVATE_CONSTANT_270 = "part.ti_add_part.icon";

   @RBEntry("height=750,width=900")
   @RBPseudo(false)
   public static final String PRIVATE_CONSTANT_271 = "part.ti_add_part.moreurlinfo";

   @RBEntry("Find and add part")
   @RBComment("DO NOT TRANSLATE. This action is not visible to users.")
   public static final String PRIVATE_CONSTANT_272 = "part.ti_add_part_auto.title";

   @RBEntry("Find and add part")
   @RBComment("DO NOT TRANSLATE. This action is not visible to users.")
   public static final String PRIVATE_CONSTANT_273 = "part.ti_add_part_auto.tooltip";

   @RBEntry("Find and add part")
   @RBComment("DO NOT TRANSLATE. This action is not visible to users.")
   public static final String PRIVATE_CONSTANT_274 = "part.ti_add_part_auto.description";

   @RBEntry("add16x16.gif")
   @RBPseudo(false)
   public static final String PRIVATE_CONSTANT_275 = "part.ti_add_part_auto.icon";

   @RBEntry("height=750,width=900")
   @RBPseudo(false)
   public static final String PRIVATE_CONSTANT_276 = "part.ti_add_part_auto.moreurlinfo";

   @RBEntry("新建部件")
   @RBComment("Used as the label for the New Part action from Edit Structure table")
   public static final String PRIVATE_CONSTANT_277 = "part.ti_create_part.description";

   @RBEntry("新建部件")
   @RBComment("Used as the tooltip for the New Part action from Edit Structure table")
   public static final String PRIVATE_CONSTANT_278 = "part.ti_create_part.tooltip";

   @RBEntry("height=550,width=700")
   @RBPseudo(false)
   @RBComment("DO NOT TRANSLATE")
   public static final String PRIVATE_CONSTANT_279 = "part.ti_create_part.moreurlinfo";

   @RBEntry("newpart.gif")
   @RBPseudo(false)
   @RBComment("Used as the icon for the New Part action from Edit Structure table")
   public static final String PRIVATE_CONSTANT_280 = "part.ti_create_part.icon";

   @RBEntry("新建多个部件")
   @RBComment("Used as the label for the New Multiple Parts action from Edit Structure table")
   public static final String PRIVATE_CONSTANT_281 = "part.ti_create_multiple_part.description";

   @RBEntry("新建多个部件")
   @RBComment("Used as the tooltip for the New Multiple Parts action from Edit Structure table")
   public static final String PRIVATE_CONSTANT_282 = "part.ti_create_multiple_part.tooltip";

   @RBEntry("part_createmultiple.gif")
   @RBPseudo(false)
   @RBComment("Used as the icon for the New Multiple Parts action from Edit Structure table")
   public static final String PRIVATE_CONSTANT_283 = "part.ti_create_multiple_part.icon";

   @RBEntry("确定")
   @RBComment("Used as the label for the Ok button")
   public static final String PRIVATE_CONSTANT_284 = "part.tabularInputOkButton.description";

   @RBEntry("确定")
   @RBComment("Used as the title for Ok button")
   public static final String PRIVATE_CONSTANT_285 = "part.tabularInputOkButton.title";

   @RBEntry("确定")
   @RBComment("Used as the tooltip for Ok button")
   public static final String PRIVATE_CONSTANT_286 = "part.tabularInputOkButton.tooltip";

   @RBEntry("应用")
   @RBComment("Used as the label for the Apply button")
   public static final String PRIVATE_CONSTANT_287 = "part.tabularInputApplyButton.description";

   @RBEntry("应用")
   @RBComment("Used as the title for Apply button")
   public static final String PRIVATE_CONSTANT_288 = "part.tabularInputApplyButton.title";

   @RBEntry("应用")
   @RBComment("Used as the tooltip for Apply button")
   public static final String PRIVATE_CONSTANT_289 = "part.tabularInputApplyButton.tooltip";

   @RBEntry("取消")
   @RBComment("Used as the label for the Cancel button")
   public static final String PRIVATE_CONSTANT_290 = "part.tabularInputCancelButton.description";

   @RBEntry("取消")
   @RBComment("Used as the title for Cancel button")
   public static final String PRIVATE_CONSTANT_291 = "part.tabularInputCancelButton.title";

   @RBEntry("取消")
   @RBComment("Used as the tooltip for Cancel button")
   public static final String PRIVATE_CONSTANT_292 = "part.tabularInputCancelButton.tooltip";

   /**
    * multi part related entries
    **/
   @RBEntry("height=500,width=475")
   @RBPseudo(false)
   @RBComment("DO NOT TRANSLATE")
   public static final String PRIVATE_CONSTANT_293 = "part.setAttributesWizStepForCreateMultiPart.moreurlinfo";

   @RBEntry("height=500,width=475")
   @RBPseudo(false)
   @RBComment("DO NOT TRANSLATE")
   public static final String PRIVATE_CONSTANT_294 = "part.setAttributesWizStepForWTPart.moreurlinfo";

   @RBEntry("新建多个部件")
   @RBComment("Used as the label for the New Multiple Parts Wizard")
   public static final String PRIVATE_CONSTANT_295 = "part.createMultiPart.description";

   @RBEntry("新建多个部件")
   @RBComment("Used as the label for the New Multiple Parts Wizard")
   public static final String PRIVATE_CONSTANT_296 = "part.createMultiPart.title";

   @RBEntry("新建多个部件")
   @RBComment("Used as the tooltip for the New Multiple Parts Wizard")
   public static final String PRIVATE_CONSTANT_297 = "part.createMultiPart.tooltip";

   @RBEntry("height=550,width=1000")
   @RBPseudo(false)
   @RBComment("DO NOT TRANSLATE")
   public static final String PRIVATE_CONSTANT_298 = "part.createMultiPart.moreurlinfo";

   @RBEntry("part_createmultiple.gif")
   @RBPseudo(false)
   @RBComment("Icon that appears in toolbars for creating a New Multiple Parts")
   public static final String PRIVATE_CONSTANT_299 = "part.createMultiPart.icon";

   /**
    * adding one more entry because for some reason, title defined above is not picked up
    * using this title explicitly in CreatePartWizard.jsp
    **/
   @RBEntry("新建多个部件")
   @RBComment("Used as the label for the New Multiple Parts Wizard")
   public static final String PRIVATE_CONSTANT_300 = "part.createMultiPart.WIZARD_LABEL";

   @RBEntry("insert_multi_rows_below.gif")
   @RBPseudo(false)
   public static final String PRIVATE_CONSTANT_301 = "part.addMultipleObjects.icon";

   @RBEntry("添加 5 行")
   public static final String PRIVATE_CONSTANT_302 = "part.addMultipleObjects.description";

   @RBEntry("添加 5 行")
   public static final String PRIVATE_CONSTANT_303 = "part.addMultipleObjects.tooltip";

   @RBEntry("insert_row_below.gif")
   @RBPseudo(false)
   public static final String PRIVATE_CONSTANT_304 = "part.addObject.icon";

   @RBEntry("添加行")
   public static final String PRIVATE_CONSTANT_305 = "part.addObject.description";

   @RBEntry("添加行")
   public static final String PRIVATE_CONSTANT_306 = "part.addObject.tooltip";

   @RBEntry("row_select_remove.gif")
   @RBPseudo(false)
   public static final String PRIVATE_CONSTANT_307 = "part.remove.icon";

   @RBEntry("移除选定行")
   public static final String PRIVATE_CONSTANT_308 = "part.remove.description";

   @RBEntry("移除选定行")
   public static final String PRIVATE_CONSTANT_309 = "part.remove.tooltip";

   @RBEntry("remove16x16.gif")
   @RBPseudo(false)
   public static final String PRIVATE_CONSTANT_310 = "part.remove_part_usage.icon";

   @RBEntry("移除选定行")
   public static final String PRIVATE_CONSTANT_311 = "part.remove_part_usage.description";

   @RBEntry("移除选定行")
   public static final String PRIVATE_CONSTANT_312 = "part.remove_part_usage.tooltip";

   @RBEntry("创建部件失败")
   public static final String ERROR_CREATING_PART = "ERROR_CREATING_PART";

   @RBEntry("部件编号不是独有编号。请选取唯一的部件编号。")
   public static final String NUMBER_NOT_UNIQUE = "NUMBER_NOT_UNIQUE";

   @RBEntry("要向列表中添加子项部件，请输入数字然后按“搜索”按钮，或&lt;tab&gt; 或 &lt;enter&gt;键，或使用工具栏中的操作。必须先输入最低限量的字符，然后才能开始搜索。")
   @RBComment("This help message is displayed in the edit strcuture client. It is Not online help.")
   public static final String EDIT_STRUCTURE_HELP = "EDIT_STRUCTURE_HELP";

   @RBEntry("注意: 未找到结果\n请尝试其他搜索。")
   @RBComment("An error message displayed as pop up")
   public static final String EDIT_STRUCTURE_NO_RESULTS = "EDIT_STRUCTURE_NO_RESULTS";

   @RBEntry("确认: 子项已存在。是否再次添加?\n您所添加的部件已经是父项的一个子项。")
   @RBComment("A pop up error message used by edit structure client")
   public static final String EDIT_STRUCTURE_CHILD_EXISTS = "EDIT_STRUCTURE_CHILD_EXISTS";

   @RBEntry("部件主数据")
   @RBComment("Part Master")
   public static final String WTPARTMASTER_TABLEVIEW_LABEL = "WTPARTMASTER_TABLEVIEW_LABEL";

   @RBEntry("默认部件主数据视图")
   @RBComment("Default Part Master View")
   public static final String WTPARTMASTER_TABLEVIEW_NAME = "WTPARTMASTER_TABLEVIEW_NAME";

   @RBEntry("默认部件主数据搜索表格视图")
   @RBComment("Default Part Master Search Table View")
   public static final String WTPARTMASTER_TABLEVIEW_DESC = "WTPARTMASTER_TABLEVIEW_DESC";

   @RBEntry("未选择任何对象。")
   @RBComment("Error message displayed when the user doesn't select any objects before launching the multi select action.")
   public static final String NONE_SELECTED_ERROR = "NONE_SELECTED";

   @RBEntry("要求的最小值")
   @RBComment("Minimum Number of choices required for the option")
   public static final String MINIMUM_REQUIRED_LABEL = "MINIMUM_REQUIRED_LABEL";

   @RBEntry("允许的最大值")
   @RBComment("Maximum Number of choices allowed for the option")
   public static final String MAXIMUM_ALLOWED_LABEL = "MAXIMUM_ALLOWED_LABEL";

   /**
    * Create Shared Part Wizard action
    **/
   @RBEntry("新建共享部件")
   @RBComment("Used as the label for the New Shared Part Wizard")
   public static final String PRIVATE_CONSTANT_316 = "part.createSharedPart.description";

   @RBEntry("新建共享部件")
   @RBComment("Used as the label for the New Shared Part Wizard")
   public static final String PRIVATE_CONSTANT_317 = "part.createSharedPart.title";

   @RBEntry("新建共享部件")
   @RBComment("Used as the tooltip for the New Shared Part Wizard")
   public static final String PRIVATE_CONSTANT_318 = "part.createSharedPart.tooltip";

   @RBEntry("height=750,width=650")
   @RBPseudo(false)
   @RBComment("DO NOT TRANSLATE")
   public static final String PRIVATE_CONSTANT_319 = "part.createSharedPart.moreurlinfo";

   @RBEntry("确认: 将“单位”从“个”改为其他，将强制“位号”被删除!\n您是否要继续?")
   @RBComment("A pop up confirmation message used by edit structure client")
   public static final String CONFIRM_UNITS_CHANGE_WITH_REF_DES = "CONFIRM_UNITS_CHANGE_WITH_REF_DES";

   /**
    * Clicking the Apply button on a New Multi Parts wizard should reset the wizard to the initial step
    * Overridden the Apply button in order to redirect the user to the first step
    **/
   @RBEntry("应用(<u class='mnemonic'>A</u>)")
   @RBComment("Used for the text on the Apply wizard button.  The <U class=?mnemonic?> </U> tag should be put around the character that is the access key.")
   public static final String PRIVATE_CONSTANT_320 = "part.multiPartApplyButton.description";

   @RBEntry("a")
   @RBPseudo(false)
   @RBComment("Mnemonic for the Apply wizard button. This should be a character that matches the character surrounded by the <U class=?mnemonic?> </U> tag in the value line above.")
   public static final String PRIVATE_CONSTANT_321 = "part.multiPartApplyButton.hotkey";

   /**
    * Clicking the Back button on a New Multi Parts wizard should skip the nameNumberValidation
    * Overridden the Back button
    **/
   @RBEntry("上一页(<u class='mnemonic'>B</u>)")
   @RBComment("Used for the text on the Back wizard button.  The <U class=?mnemonic?> </U> tag should be put around the character that is the access key.")
   public static final String PRIVATE_CONSTANT_322 = "part.multiPartprevButton.description";

   @RBEntry("b")
   @RBPseudo(false)
   @RBComment("Mnemonic for the Back wizard button. This should be a character that matches the character surrounded by the <U class=?mnemonic?> </U> tag in the value line above.")
   public static final String PRIVATE_CONSTANT_323 = "part.multiPartprevButton.hotkey";

   /**
    * Edit Multiple Parts Wizard Buttons
    **/
   @RBEntry("检入(<u class='mnemonic'>I</u>)")
   @RBComment("Used for the text on the Check In wizard button.  The <U class=mnemonic> </U> tag should be put around the character that is the access key.")
   public static final String PRIVATE_CONSTANT_324 = "part.checkinButton.description";

   @RBEntry("i")
   @RBPseudo(false)
   @RBComment("Mnemonic for the Check In wizard button. This should be a character that matches the character surrounded by the <U class=mnemonic> </U> tag in the value line above.")
   public static final String PRIVATE_CONSTANT_325 = "part.checkinButton.hotkey";

   @RBEntry("检入")
   @RBComment("Used for the tooltip on the Check In wizard button.")
   public static final String PRIVATE_CONSTANT_326 = "part.checkinButton.tooltip";

   @RBEntry("保存(<U class='mnemonic'>S</U>)")
   @RBComment("Used for the text on the Save wizard button.  The <U class=mnemonic> </U> tag should be put around the character that is the access key.")
   public static final String PRIVATE_CONSTANT_327 = "part.saveButton.description";

   @RBEntry("s")
   @RBPseudo(false)
   @RBComment("Mnemonic for the Save wizard button. This should be a character that matches the character surrounded by the <U class=mnemonic> </U> tag in the value line above.")
   public static final String PRIVATE_CONSTANT_328 = "part.saveButton.hotkey";

   @RBEntry("保存")
   @RBComment("Used for the tooltip on the Save wizard button.")
   public static final String PRIVATE_CONSTANT_329 = "part.saveButton.tooltip";

   @RBEntry("取消(<u class='mnemonic'>C</u>)")
   @RBComment("Used for the text on the Cancel wizard button.  The <U class=mnemonic> </U> tag should be put around the character that is the access key.")
   public static final String PRIVATE_CONSTANT_330 = "part.editCancelButton.description";

   @RBEntry("c")
   @RBPseudo(false)
   @RBComment("Mnemonic for the Cancel wizard button. This should be a character that matches the character surrounded by the <U class=mnemonic> </U> tag in the value line above.")
   public static final String PRIVATE_CONSTANT_331 = "part.editCancelButton.hotkey";

   @RBEntry("取消")
   @RBComment("Used for the tooltip on the edit Cancel wizard button.")
   public static final String PRIVATE_CONSTANT_332 = "part.editCancelButton.tooltip";

   @RBEntry("粘贴操作失败")
   @RBComment(" Error message title unable to paste")
   public static final String PASTE_ACTION_FAILURE = "PASTE_ACTION_FAILURE";

   @RBEntry("粘贴操作部分失败")
   @RBComment(" Error message title unable to paste")
   public static final String PASTE_ACTION_PARTIAL_FAILURE = "PASTE_ACTION_PARTIAL_FAILURE";

   @RBEntry("剪贴板内没有可粘贴的对象")
   @RBComment(" Error message for no objects selected to be pasted")
   public static final String NO_OBJECTS_IN_CLIPBOARD = "NO_OBJECTS_IN_CLIPBOARD";

   @RBEntry("某些剪贴板中的对象没有定义与目标对象之间有效的关系。")
   @RBComment(" Error message for not all valid objects selected to be pasted")
   public static final String SOME_INVALID_OBJECTS_SELECTED_TO_PASTE = "SOME_INVALID_OBJECTS_SELECTED_TO_PASTE";

   @RBEntry("选项规则")
   @RBComment("option rules column heading")
   public static final String OPTION_RULES = "OPTION_RULES";

   @RBEntry("参与构建")
   @RBComment("Participate In Build column heading")
   public static final String PARTICIPATE_IN_BUILD = "PARTICIPATE_IN_BUILD";

   @RBEntry("*虚拟制造部件")
   @RBComment("This string is used for the Phantom required label in the jsp page.")
   public static final String PHANTOM = "PHANTOM";

   @RBEntry("展开条件")
   @RBComment("Used as a label for Expansion Criteria action popup")
   public static final String PRIVATE_CONSTANT_333 = "part.showExpansionCriteria.title";

   @RBEntry("展开条件")
   public static final String PRIVATE_CONSTANT_334 = "part.showExpansionCriteria.description";

   @RBEntry("显示展开条件")
   @RBComment("Used as tooltip for the action")
   public static final String PRIVATE_CONSTANT_335 = "part.showExpansionCriteria.tooltip";

   @RBEntry("height=400,width=600")
   @RBPseudo(false)
   @RBComment("DO NOT TRANSLATE")
   public static final String PRIVATE_CONSTANT_336 = "part.showExpansionCriteria.moreurlinfo";

   @RBEntry("{0}, {1}")
   @RBComment("Used to build a list of values separated by a separator")
   public static final String MULTIPLE_VALUES = "MULTIPLE_VALUES";

   /**
    * Edit Alternate Table from 3rd level Nav
    **/
   @RBEntry("编辑")
   @RBComment("Used as the label for the edit action for a part")
   public static final String PRIVATE_CONSTANT_337 = "object.editReplacement.description";

   @RBEntry("编辑全局替换部件")
   @RBComment("Used as the label for the edit action for a part")
   public static final String PRIVATE_CONSTANT_338 = "object.editReplacement.title";

   @RBEntry("编辑全局替换部件链接属性")
   @RBComment("Used as the tooltip for the edit action for a part")
   public static final String PRIVATE_CONSTANT_339 = "object.editReplacement.tooltip";

   @RBEntry("multi_update.gif")
   @RBPseudo(false)
   @RBComment("Icon for the edit part action (for toolbars)")
   public static final String PRIVATE_CONSTANT_340 = "object.editReplacement.icon";

   @RBEntry("height=500,width=600")
   @RBPseudo(false)
   @RBComment("DO NOT TRANSLATE")
   public static final String PRIVATE_CONSTANT_341 = "object.editReplacement.moreurlinfo";

   @RBEntry("注意: 缺少必需的信息。\n\n编号 \"{0}\" 的“名称”字段为空白。\n请填写所有标有星号 (*) 的字段。")
   @RBComment("Message should prompt to user when Name field is empty but Number field has a valid value.")
   public static final String CREATE_MULTI_PART_NAME_VALIDATION_ERROR = "108";

   @RBEntry("未能添加选定部件 \"{0}\" 到列表中，由于它是在工作区内新建的。")
   @RBComment("Message should promt when user tries to add newly created part in workspace.")
   public static final String ADD_PART_FAILED = "109";

   @RBEntry("注意: 缺少必需的信息。\n\n名称 \"{0}\" 的“编号”字段为空白。\n请填写所有标有星号 (*) 的字段。")
   @RBComment("Message should prompt to user when Number field is empty but Name field has a valid value.")
   public static final String CREATE_MULTI_PART_NUMBER_VALIDATION_ERROR = "110";

   @RBEntry("注意: 缺少必需的信息。\n\n所有行的“名称”和“编号”字段均为空白。\n请在至少一行中填写所有标有星号 (*) 的字段，包括“名称”、“编号”等。")
   @RBComment("Message should prompt user when Name field and Number field are empty on all rows.")
   public static final String CREATE_MULTI_PART_NAME_WITH_NUMBER_VALIDATION_ERROR = "111";
   
   @RBEntry("当前用户没有创建对象 \"{0}\" 之“新建视图版本”的权限。")
   @RBArgComment0("The object ID")
   public static final String NO_PERMISSION_TO_CREATE_VIEW_VERSION = "NO_PERMISSION_TO_CREATE_VIEW_VERSION";

   @RBEntry("事件")
   @RBComment("Event Type of Alternate History")
   public static final String EVENT = "112";

   @RBEntry("备用历史记录")
   @RBComment("Alternate History Table Title")
   public static final String ALTERNATE_HISTORY_TABLE = "113";

   @RBEntry("事件日期")
   @RBComment("Event Date of Alternate History")
   public static final String EVENT_DATE = "114";

   @RBEntry("用户")
   @RBComment("Alternate History User")
   public static final String ALTERNATE_HISTORY_USER = "115";

   @RBEntry("创建多部件视图")
   @RBComment("Heading Label for Create Multi Part View in customize table.")
   public static final String CREATE_MULTI_PART_VIEW = "116";

   @RBEntry("编辑多部件视图")
   @RBComment("Heading Label for Edit Multi Part View in customize table.")
   public static final String EDIT_MULTI_PART_VIEW = "117";

   @RBEntry("确认: 您尚未单击“应用”。更改不会保存。\n确定要继续吗?")
   @RBComment("A pop up confirmation message used by edit structure client for navigation in pagination.")
   public static final String CONFIRM_NAVIGATION = "CONFIRM_NAVIGATION";

   @RBEntry("*停止有效性传播")
   @RBComment("Stop Effectivity Propagation Column")
   public static final String STOP_EFFECTIVITY_R_PROPAGATION = "118";

   @RBEntry("备注")
   @RBComment("used for the editing of comments on the doc references table")
   public static final String PART_COMMENT_TEXTAREA_HEADER = "119";

   @RBEntry("注意: 数量值超出 {0} 限制。")
   @RBComment("Message should prompt to user when quantity field is greater.")
   public static final String QUANTITY_LIMIT_VALIDATION = "120";

   @RBEntry("注意: 指定的位号计数大于 {0} 的数量限制。  ")
   @RBComment("Message should prompt to user when quantity field is greater.")
   public static final String REFDESIG_EXCEEDS_QUANTITY_LIMIT = "121";

   @RBEntry("注意: 输入的数量值 {0} 大于位号 {1}。\n\n 请单击“确定”存储先前的数量 {2}，或单击“取消”继续使用相同的数量 {3}")
   @RBComment("Message should prompt user that quantity value entered is more than the Reference Designator so previous value is stored.")
   public static final String QUANTITY_MORE_VALIDATION = "122";

   @RBEntry("编辑全局替换部件")
   @RBComment("Edit Alternate")
   public static final String EDIT_ALTERNATE = "123";

   @RBEntry("组织 ID")
   @RBComment("Lable for Organization Column in edit Alternate")
   public static final String ORG_ID = "124";

   @RBEntry("装配")
   @RBComment("In Related Substitutes Table group of 4 columns in one column namely:- Assembly Name,Number,Organization and Context")
   public static final String ASSEMBLY = "125";

   @RBEntry("特定替换编号")
   @RBComment("In Related Substitutes Table display substitute Number instead of just number.")
   public static final String SUBSTITUTE_NUMBER = "126";

   @RBEntry("特定替换部件组织 ID")
   @RBComment("In Related Substitutes Table display substitute Number instead of just number.")
   public static final String SUBSTITUTE_ORGANIZATION_ID = "127";

   @RBEntry("特定替换部件名称")
   @RBComment("In Related Substitutes Table display substitute Number instead of just number.")
   public static final String SUBSTITUTE_NAME = "128";

   @RBEntry("被特定替换编号")
   @RBComment("In Related Substitutes Table display substitute Number instead of just number.")
   public static final String SUBSTITUTEFOR_NUMBER = "129";

   @RBEntry("被特定替换组织 ID")
   @RBComment("In Related Substitutes Table display substitute Number instead of just number.")
   public static final String SUBSTITUTEFOR_ORGANIZATION_ID = "130";

   @RBEntry("被特定替换名称")
   @RBComment("In Related Substitutes Table display substitute Number instead of just number.")
   public static final String SUBSTITUTEFOR_NAME = "131";

   @RBEntry("打开方式")
   public static final String PRIVATE_CONSTANT_342 = "object.more parts toolbar actions open.description";

   @RBEntry("打开方式")
   public static final String PRIVATE_CONSTANT_343 = "object.more parts toolbar actions open.tooltip";

   @RBEntry("添加至")
   public static final String PRIVATE_CONSTANT_344 = "object.more parts toolbar actions add.description";

   @RBEntry("添加至")
   public static final String PRIVATE_CONSTANT_345 = "object.more parts toolbar actions add.tooltip";

   @RBEntry("比较")
   public static final String PRIVATE_CONSTANT_346 = "object.more parts actions compare.description";

   @RBEntry("比较")
   public static final String PRIVATE_CONSTANT_347 = "object.more parts actions compare.tooltip";

   @RBEntry("新建")
   public static final String PRIVATE_CONSTANT_348 = "object.more parts toolbar actions new.description";

   @RBEntry("新建")
   public static final String PRIVATE_CONSTANT_349 = "object.more parts toolbar actions new.tooltip";

   @RBEntry("创建 CAD 文档")
   @RBComment("Label on check box in New Part wizard to choose whether to create a new CAD document also.")
   public static final String CREATE_CAD_DOC_FROM_NEW_PART_LABEL = "CREATE_CAD_DOC_FROM_NEW_PART_LABEL";

   @RBEntry("classify.gif")
   @RBPseudo(false)
   @RBComment("Icon for Classification Attributes in Create New Multiple Part Wizard")
   public static final String PRIVATE_CONSTANT_350 = "part.setClassificationAttributesForMultiPart.icon";

   @RBEntry("设置分类属性")
   @RBComment("Description for Classification Attributes in Create New Multiple Part Wizard")
   public static final String PRIVATE_CONSTANT_351 = "part.setClassificationAttributesForMultiPart.description";

   @RBEntry("设置分类属性")
   @RBComment("ToolTip for Classification Attributes in Create New Multiple Part Wizard")
   public static final String PRIVATE_CONSTANT_352 = "part.setClassificationAttributesForMultiPart.tooltip";

   @RBEntry("height=375,width=485")
   @RBPseudo(false)
   @RBComment("Classification Attributes")
   public static final String PRIVATE_CONSTANT_353 = "part.setClassificationAttributesForMultiPart.moreurlinfo";

   @RBEntry("设置分类属性")
   @RBComment("Used as the label for 2nd step of Set Classification Wizard in New Multiple Part Wizard")
   public static final String PRIVATE_CONSTANT_354="part.setClassification.WIZARD_STEP_LABEL";

   @RBEntry("选择分类")
   @RBComment("Select Classifications")
   public static final String PRIVATE_CONSTANT_355="part.selectClassificationattributesWizStepForMultiPart";

    @RBEntry("设置类别")
   @RBComment("Title of Wizard Set Classification in New Multi Part Wizard")
   public static final String PRIVATE_CONSTANT_356 = "part.setClassificationAttributes.title";

   @RBEntry("*可折叠")
   @RBComment("This string is used for the Collapsible required label in the jsp page.")
   public static final String COLLAPSIBLE_R_COLUMN_LABEL = "part.createMultiPart.Collapsible";

   @RBEntry("允许的最大值")
   @RBComment("This string is used for the maximumAllowed label in the jsp page.")
   public static final String MaximumAllowed_R_COLUMN_LABEL = "part.createMultiPart.maximumAllowed";

   @RBEntry("要求的最小值")
   @RBComment("This string is used for the minimumRequired label in the jsp page.")
   public static final String MinimumRequired_R_COLUMN_LABEL = "part.createMultiPart.minimumRequired";

   @RBEntry("部件列表")
   @RBComment("Used in the 3rd level nav bar under 'Related items' and as the table title")
   public static final String PRIVATE_CONSTANT_357 = "part.relatedPartsLists.description";

   /**
    * Column headers for the related Parts Lists table (3rd level nav)
    **/
   @RBEntry("版本")
   public static final String PRIVATE_CONSTANT_358 = "part.relatedPartsLists.VERSION";

   @RBEntry("上下文")
   public static final String PRIVATE_CONSTANT_359 = "part.relatedPartsLists.CONTEXT";

   @RBEntry("状态")
   public static final String PRIVATE_CONSTANT_360 = "part.relatedPartsLists.STATE";

   @RBEntry("团队")
   public static final String PRIVATE_CONSTANT_361 = "part.relatedPartsLists.TEAM";

   @RBEntry("上次修改时间")
   public static final String PRIVATE_CONSTANT_362 = "part.relatedPartsLists.LAST_UPDATED";

   @RBEntry("关联")
   public static final String PRIVATE_CONSTANT_363 = "part.relatedPartsLists.ASSOCIATION";

   @RBEntry("可配置模块")
   @RBComment("This is a label for Configurable Module column")
   public static final String GENERICTYPE = "364";

    /**
     * Build status glyph
     **/
    @RBEntry("部件构建状况")
    @RBComment("Table column header for data that indicates if the object is build")
    public static final String BUILD_STATUS_TITLE = "BUILD_STATUS_TITLE";
    /**
     * Attribute(Service Kit, Serviceable) For Part. B-88847
     */
    @RBEntry("服务工具包")
    @RBComment("This string is used for the servicekit label in the jsp page")
    public static final String SERVICE_KIT_COLUMN_LABEL = "SERVICE_KIT_COLUMN_LABEL";

    @RBEntry("可服务")
    @RBComment("This string is used for the serviceable label in the jsp page")
    public static final String SERVICEABLE_COLUMN_LABEL = "SERVICEABLE_COLUMN_LABEL";

    @RBEntry("发布到 CAD")
    @RBComment("The title of the action")
    public static final String PUBLISH_TO_CAD_TITLE = "part.publishToCAD.title";

    @RBEntry("发布到 CAD")
    @RBComment("The description of the action")
    public static final String PUBLISH_TO_CAD_DESC = "part.publishToCAD.description";

    @RBEntry("发布到 CAD")
    @RBComment("The tooltip for the action")
    public static final String PUBLISH_TO_CAD_TOOLTIP = "part.publishToCAD.tooltip";

    @RBEntry("一个或多个选定发布到 CAD 的对象无效。有效部件已被加入队列用于构建对应的 CAD 结构。有关详细信息，请查看事件管理器。")
    @RBComment("Message displayed if invalid objects are selected when invoking the Publish to CAD action")
    public static final String INVALID_SELECTED_OBJECTS_WARNING = "INVALID_SELECTED_OBJECTS_WARNING";
    
    @RBEntry("选定发布到 CAD 的对象无效。只有那些未检出，并且是最新版本的最新小版本的部件才是有效对象。")
    @RBComment("Message displayed when no valid parts are selected when invoking the Publish to CAD action")
    public static final String NO_VALID_OBJECTS_SELECTED_ERROR = "NO_VALID_OBJECTS_SELECTED_ERROR";
    
    @RBEntry("选定部件已加入队列用于构建相应的 CAD 结构。有关详细信息，请查看事件管理器。")
    @RBComment("Message on the yellow banner displayed on part info page when parts are selected to be published to CAD from Check in UI")
    public static final String MESSAGE_PUBLISH_TO_CAD = "MESSAGE_PUBLISH_TO_CAD";
    
    @RBEntry("已排队向 CAD 发布的部件")
    @RBComment("Title on the yellow banner displayed on part info page when parts are selected to be published to CAD from Check in UI")
    public static final String CONFIRM_PUBLISH_TO_CAD = "CONFIRM_PUBLISH_TO_CAD";
   
    @RBEntry("未选定有效部件发布到 CAD")
    @RBComment("Title on the yellow banner displayed on part info page when parts are selected to be published to CAD from Check in UI")
    public static final String ERROR_PUBLISH_TO_CAD = "ERROR_PUBLISH_TO_CAD";
    
    @RBEntry("错误: 无法将选定部件发布到 CAD")
    @RBComment("Title on the yellow banner displayed on part info page when parts are selected to be published to CAD from Check in UI")
    public static final String UNABLE_TO_PUBLISH_TO_CAD = "UNABLE_TO_PUBLISH_TO_CAD";
    
    @RBEntry("无法将子部件的数量减少到具体值以下。")
    @RBComment("Message for quantity validation.")
    public static final String QUANTITY_CAN_NOT_BE_LESS_THAN_OCCURRENCES = "QUANTITY_CAN_NOT_BE_LESS_THAN_OCCURRENCES";
    
    @RBEntry("需要分类。可以使用“设置分类属性”操作进行设置。")
    @RBComment("This string is used to show alert on multi part create wizard for validation message when classification attribute is required")
    public static final String CLASSIFICATION_REQUIRED = "CLASSIFICATION_REQUIRED";

}
