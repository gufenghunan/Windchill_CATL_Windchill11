package com.catl.integration.rdm;

import java.util.HashMap;
import java.util.Properties;

import wt.services.applicationcontext.implementation.ServiceProperties;
import wt.util.WTException;

import com.ptc.core.meta.type.mgmt.server.impl.TypeDomainHelper;

public class MPLDefConstant {
   
    public static String domain = "";

    public static HashMap<String, String> CLS_CATEGORY_MAP = new HashMap<String, String>();
    static {
  
        try {
            domain = TypeDomainHelper.getExchangeDomain();
            Properties props = ServiceProperties.getServiceProperties("WTServiceProviderFromProperties");
            String key = "";
            String value = "";
            for (String category : MPLDefConstant.Value.PROD_CATEGORY_LIST) {
                key = category + "_IN";
                value = props.getProperty(key);
                CLS_CATEGORY_MAP.put(key, value);
                key = category + "_OUT";
                value = props.getProperty(key);
                CLS_CATEGORY_MAP.put(key, value);
            }

        } catch (WTException e) {
            e.printStackTrace();
        }
    }

    /**
     * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
     * @@@@@@@@@@@@@@@@@@@@@@@ 类型定义 @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
     * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
     */
    //业务对象
    public static final String PRODUCT = domain + ".PRODUCT";//产品
    public static final String SALE_UNIT = domain + ".SALE_UNIT";//销售码

    //ECA
    public static final String PRD_ECA = domain + ".PRODUCT_ECA";

    //ECO
    public static final String BULK_PRODUCTION = domain + ".BULK_PRODUCTION";
    public static final String PRD_CHANGE = domain + ".PRD_CHANGE";
    public static final String PRD_CHANGE_MODEL = domain + ".PRD_CHANGE_MODEL"; //产品型号变更单
    public static final String PRD_CHANGE_MODELAPPLY_E = domain + ".PRD_CHANGE_MODELAPPLY_E";//产品型号申请单(首产)
    public static final String PRD_CHANGE_MODELAPPLY_M = domain + ".PRD_CHANGE_MODELAPPLY_M";//产品型号申请单(转产)
    public static final String PRD_CHANGE_RELATION = domain + ".PRD_CHANGE_RELATION";//配套关系变更单
    public static final String PRD_STST_CHANGE = domain + ".PRD_STAT_CHANGE";//产品通用状态变更单
    public static final String PRD_CHANGE_DELIST = domain + ".PRD_CHANGE_DELIST"; //退市申请单
    public static final String PRD_CHANGE_SALE_UNIT = domain + ".PRD_CHANGE_SALE_UNIT";//销售码替换单
    public static final String TYPE_PURCHASEIDENTITY = domain + ".PurchaseIdentity"; //采购标识
    public static final String PRD_CHANGE_DELIST_FULLTYPENAME = "wt.change2.WTChangeOrder2"+"|"+PRD_CHANGE+"|"+PRD_CHANGE_DELIST; //退市申请单
    /**
     * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
     * @@@@@@@@@@@@@@@@@@@@@@@ 属性定义 （产品） @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
     * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
     */
    //IBA
    public static final String PRODUCT_CER_TREQEST = "PRODUCT_CER_TREQEST";
    //MBA
    public static final String PRODUCT_MODEL = "PRODUCT_MODEL";//销售码也有此属性
    public static final String ENG_NAME = "ENG_NAME";
    public static final String MARKET_MODEL = "MARKET_MODEL";
    public static final String PRODUCT_DESC = "PRODUCT_DESC";
    public static final String ENG_DESC = "ENG_DESC";
    public static final String BASIC_UNIT = "PRO_UNIT";
    public static final String MID_PRDT_YPE = "MID_PRDT_YPE";
    public static final String PROC_INDICATOR = "PROC_INDICATOR";
    public static final String PRODUCT_TEMPLATE = "PRODUCT_TEMPLATE";
    public static final String PRODUCT_STAT = "PRODUCT_STAT";
    public static final String NUMBERING_TYPE = "NUMBERING_TYPE";
    public static final String CONTROL_MODE = "CONTROL_MODE";
    public static final String PRODUCT_TYPE = "PRODUCT_TYPE";
    public static final String SALES_TO = "SALES_TO";
    public static final String BRAND_TYPE = "BRAND_TYPE";
    public static final String BRAND = "BRAND";
    public static final String POWER_TYPE = "POWER_TYPE";
    public static final String VOLTAGE_TYPE = "VOLTAGE_TYPE";
    public static final String SUPPLY_FREQUENCY = "SUPPLY_FREQUENCY";
    public static final String ABILITY_NAME = "ABILITY_NAME";
    public static final String ABILITY_NUMBER = "ABILITY_NUMBER";
    public static final String ABILI_TYUNIT = "ABILI_TYUNIT";
    public static final String ENERGY_EFF_RATIING = "ENERGY_EFF_RATIING";
    public static final String PRODUCT_FORM = "PRODUCT_FORM";
    public static final String ORDER_TYPE = "ORDER_TYPE";
    public static final String PRODUCT_POSITIONING = "PRODUCT_POSITIONING";
    public static final String FREQUENCY_TYPE = "FREQUENCY_TYPE";
    public static final String PRODUCT_RD_CLS = "PRODUCT_RD_CLS";//产品研发分类
    public static final String PRODUCT_NUMBERING_CLS = "PRODUCT_NUMBERING_CLS";//编码分类
    public static final String PRODUCT_FINANCIAL_CLS = "PRODUCT_FINANCIAL_CLS";
    public static final String PRODUCT_SALE_CLS = "PRODUCT_SALE_CLS";
    public static final String PRODUCT_SHORT_NAME = "PRODUCT_SHORT_NAME";
    public static final String CLIENT_MODEL = "CLIENT_MODEL";
    public static final String BODY_LENGTH = "BODY_LENGTH";
    public static final String BODY_WIDTH = "BODY_WIDTH";
    public static final String BODY_HEIGHT = "BODY_HEIGHT";
    public static final String BODY_SIZE = "BODY_SIZE";
    public static final String GROSS_WEIGHT = "GROSS_WEIGHT";
    public static final String NET_WEIGHT = "NET_WEIGHT";
    public static final String PACKING_LENGTH = "PACKING_LENGTH";
    public static final String PACKING_WIDTH = "PACKING_WIDTH";
    public static final String PACKING_HEIGHT = "PACKING_HEIGHT";
    public static final String PACKING_SIZE = "PACKING_SIZE";
    public static final String PRODUCT_SERIES = "PRODUCT_SERIES";
    public static final String PRODUCT_MODEL_CODE = "PRODUCT_MODEL_CODE";
    public static final String COMMODITY_BAR_CODE = "COMMODITY_BAR_CODE";
    public static final String PACKING_TYPE = "PACKING_TYPE";
    public static final String PACKING_AMOUNT = "PACKING_AMOUNT";
    public static final String CLIMATE_TYPE = "CLIMATE_TYPE";
    public static final String DEV_PROJECT_ID = "DEV_PROJECT_ID";
    public static final String STRATEGY_PRODUCT_FLAG = "STRATEGY_PRODUCT_FLAG";
    public static final String REFERENCE_CODE = "REFERENCE_CODE";
    public static final String ENERGY_SAVING = "ENERGY_SAVING";
    public static final String VICE_BRAND = "VICE_BRAND";
    public static final String ELIMINATION_FLAG = "ELIMINATION_FLAG";
    public static final String CRYOGEN = "CRYOGEN";
    public static final String STACK_LAYERS = "STACK_LAYERS";
    public static final String LOADING_CAPACITY = "LOADING_CAPACITY";
    public static final String POLICY = "POLICY";
    public static final String EXEMPTION_FLAG = "EXEMPTION_FLAG";
    public static final String PRODUCT_PHASE = "PRODUCT_PHASE";
    public static final String DISTRIBUTION_CHANNEL = "DISTRIBUTION_CHANNEL";
    public static final String PLAN_TIME = "PLAN_TIME";
    public static final String PRODUCTION_TYPE = "PRODUCTION_TYPE";
    public static final String BODY_COLOR = "BODY_COLOR";
    public static final String DRAW_NUMBER = "DRAW_NUMBER";
    public static final String CURRENT_TYPE = "CURRENT_TYPE";
    public static final String PRODUCT_LIFECYCLE = "PRODUCT_LIFECYCLE";
    public static final String PROJECTS = "PROJECTS";
    public static final String PROJECTS_STATUS = "PROJECTS_STATUS";
    public static final String PRODUCT_MANAGER = "PRODUCT_MANAGER";
    public static final String PRODUCT_PLATFORM = "PRODUCT_PLATFORM";
    public static final String DERIVED_DESC = "DERIVED_DESC";
    public static final String PICTURE = "PICTURE";
    public static final String CORPORATION = "CORPORATION";
    public static final String IS_MODEL_APPROVED = "IS_MODEL_APPROVED";
    public static final String SPECIALDESCRIPTION = "specialDescription";
    public static final String DELIST_DATE = "DELIST_DATE";//退市日期
    public static final String PRE_DELIST_DATE = "PRE_DELIST_DATE";//预退市日期
    public static final String PRE_SALE_DATE = "PRE_SALE_DATE";//预销售日期
	public static final String PRO_UNIT="PRO_UNIT";
	public static final String DEV_TARGET="DEV_TARGET";
	public static final String TARGETRETAILPRICE="TARGETRETAILPRICE";
	public static final String TARGETMASSPRODUCEDATE="TARGETMASSPRODUCEDATE";
	public static final String TARGETFIRSTSALEQTY="TARGETFIRSTSALEQTY";
	public static final String PRD_PRIMARY_SPEC="PRD_PRIMARY_SPEC";
	public static final String TARGET_GROSS_MARGIN="TARGET_GROSS_MARGIN";
    //分类
    public static final String PRODUCT_CLS_NAME = "CLS_NAME";

    /**
     * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
     * @@@@@@@@@@@@@@@@@@@@@@@ 属性定义 （销售码） @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
     * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
     */
    //MBA
    public static final String COMMODITY_CODE = "COMMODITY_CODE";
    public static final String BARCODE = "BAR_CODE";
    public static final String DERIVED_FROM = "DERIVED_FROM";
    public static final String FEATURE_DESC = "FEATURE_DESC";
    public static final String REMARK = "REMARK";
    public static final String PROCUREMENT_INDICATOR = "PROC_INDICATOR";
    public static final String SaleUnit_NUMBER_CLAZZ="SaleUnit_NUMBER_CLAZZ";//编码种类
    public static final String SaleUnit_NUMBERING_CLS= "SaleUnit_NUMBERING_CLS";//编码分类
    public static final String SaleUnit_RD_CLS="SaleUnit_RD_CLS"; //研发分类
    public static final String GE_MPL_CORPORATION="GE_MPL_CORPORATION"; //制造商主体（全局枚举类型）
    public static final String SaleUnit_CORPORATION="SaleUnit_CORPORATION"; //制造商主体

    /**
     * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
     * @@@@@@@@@@@@@@@@@@@@@@@ 属性定义 （ECA） @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
     * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
     */

    /**
     * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
     * @@@@@@@@@@@@@@@@@@@@@@@ 属性定义 （ECO） @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
     * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
     */
    //通用状态变更单
    //MBA
    public static final String PRD_LC_BEFORE = "PRD_LCBEFORE";//更改前产品状态
    public static final String PRD_LC_AFTER = "PRD_LCAFTER";//更改后产品状态
    public static final String PRD_VIEW = "PRD_VIEW";//发起视图
    
    public static final String CHANGED_WTPART_MASTER_OID = "changedWTPartMasterOid";//发起视图
    
    

    //销售码变更单
    //MBA
    public static final String PRD_SC_BEFORE = "PRD_SC_BEFORE";//更改前销售码(销售码变更单)
    public static final String PRD_SC_AFTER = "PRD_SC_AFTER";//更改后销售码(销售码变更单)
    public static final String PRD_SC_AFTER_TEMP = "PRD_SC_AFTER_TEMP";//更改后销售码(销售码变更单)

    //产品型号变更单
    //MBA
    public static final String PRD_MODEL_BEFORE = "PRD_MODEL_BEFORE";//更改前产品型号

    public static final String PRD_MODEL_AFTER = "PRD_MODEL_AFTER";//更改后产品型号

    //  其他？？？？？？？？？？？？？？？？？
    //MBA属性
    public static final String MBA_NAME = "name"; //名称	

    public static final String MBA_NUMBER = "number"; //产品编号

    public static final String LISTING_DATE = "LISTING_DATE"; //上市时间

    //枚举类型  》》》请参考内部类中的定义
    //    public static final String PRODUCT_LIFECYCLE_DESIGN_RELEASED= "DESIGN_RELEASE";//设计发放
    //    public static final String PRODUCT_LIFECYCLE_Manufacturing_RELEASED= "MANUFACTURE_RELEASE";//制造发放

    /**
     * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
     * @@@@@@@@@@@@@@@@@@@@@@@ 全局枚举定义 @@@@@@@@@@@@@@@@@@@@@@@@@@@@@
     * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
     */
    public static final String GE_MPL_PRODUCT_LIFECYCLE = "GE_MPL_PRODUCT_LIFECYCLE";//GE_MPL_生命周期状态
    public static final String GE_MPL_BRAND = "GE_MPL_BRAND";//品牌
    public static final String GE_MPL_PRODUCT_TYPE = "GE_MPL_PRODUCT_TYPE";//产品类别
    public static final String GE_MPL_PROC_INDICATOR = "GE_MPL_PROC_INDICATOR";//采购标识
    public static final String GE_MPL_VICE_BRAND = "GE_MPL_VICE_BRAND"; //副品牌
    public static final String GE_MPL_DISTRIBUTION_CHANEL = "GE_MPL_DISTRIBUTION_CHANEL";//销售渠道
    public static final String GE_MPL_BODY_COLOR = "GE_MPL_BODY_COLOR"; //机身颜色
    public static final String GE_MPL_VOLTAGE_TYPE = "GE_MPL_VOLTAGE_TYPE"; //电压类型
    public static final String GE_MPL_SUPPLY_FREQUENCY = "GE_MPL_SUPPLY_FREQUENCY"; //电源频率
    public static final String GE_MPL_UNIT = "GE_MPL_UNIT"; 
    
    
    
    public static final String GE_MPL_PRODUCT_FORM = "GE_MPL_PRODUCT_FORM"; //产品形态
    public static final String GE_MPL_SALES_TO = "GE_MPL_SALES_TO";
    public static final String GE_MPL_PRODUCT_POSITIONING = "GE_MPL_PRODUCT_POSITIONING";

    //型谱
    public static final String GE_MPL_PRD_CATEGORY = "PRODUCT_CATEGORY";//产品大类
    public static final String SPECIAL_DESCRIPTION = "specialDescription";

    /**
     * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
     * @@@@@@@@@@@@@@@@@@@@@@@ 属性值常量定义 @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
     * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ 
     *                                                                                    此内部静态类保存MPL模块业务对象的属性值
     */
    public static final class Value {

        //产品形态-枚举值
        public static final String ENU_SET_PRODUCT = "SET_PRODUCT";//套机
        public static final String ENU_INDOOR_PRODUCT = "INDOOR_PRODUCT";//室内机
        public static final String ENU_OUTDOOR_PRODUCT = "OUTDOOR_PRODUCT";//室外机
        public static final String ENU_WHOLE_PRODUCT = "WHOLE_PRODUCT";//整体机
        public static final String ENU_SAMPLE_PRODUCT = "SAMPLE_PRODUCT";//样机(内销)
        public static final String ENU_PARTS_PRODUCT = "PARTS_PRODUCT";//散件
        public static final String ENU_ACCESSORIES_PRODUCT = "ACCESSORIES_PRODUCT";//独立销售模块(配件)

        //生命周期状态-枚举值
        public static final String ENU_NEW = "NEW";//新建
        public static final String ENU_STORE = "STORE";//储备
        public static final String ENU_RESEARCH = "RESEARCH";//研究
        public static final String ENU_DESIGN = "DESIGN";//设计
        public static final String ENU_DESIGN_RELEASE = "DESIGN_RELEASE";//设计发放
        public static final String ENU_MANUFACTURE_RELEASE = "MANUFACTURE_RELEASE";// 制造发放
        public static final String ENU_PRE_SALE = "PRE_SALE";//预销售
        public static final String ENU_SALE = "SALE";//在销售
        public static final String ENU_FROZEN_IN = "FROZEN_IN";//冻结
        public static final String ENU_PRE_DELIST = "PRE_DELIST";//预退市
        public static final String ENU_DELIST = "DELIST";//退市
        public static final String ENU_OBSOLETE = "OBSOLETE";//废弃

        //销售去向
        public static final String ENU_DOMESTIC_MARKET = "DOMESTIC_MARKET"; //内销

        public static final String ENU_EXPORT_MARKET = "EXPORT_MARKET"; //外销

        //编码种类
        public static final String ENU_MAKE_TO_ORDER_NUMBER = "MAKE_TO_ORDER_NUMBER";

        //产品大类
        public static final String ENU_PRD_CATE_DT = "DT";//多头炉
        public static final String ENU_PRD_CATE_RZ = "RZ";//燃气灶
        public static final String ENU_PRD_CATE_ZH = "ZH";//组合灶
        public static final String ENU_PRD_CATE_WB = "WB";//微波炉
        public static final String ENU_PRD_CATE_ZL = "ZL";//蒸汽炉
        public static final String ENU_PRD_CATE_BW = "BW";//保温抽屉
        public static final String ENU_PRD_CATE_XK = "XK";//桌面式烤箱（小烤箱）
        public static final String ENU_PRD_CATE_DK = "DK";//驻立式烤箱（大烤箱）
        public static final String ENU_PRD_CATE_SC = "SC";//商用厨房设备
        public static final String ENU_PRD_CATE_YJ = "YJ";//吸油烟机
        public static final String ENU_PRD_CATE_XD = "XD";//消毒柜
        public static final String ENU_PRD_CATE_ZC = "ZC";//整体橱柜
        public static final String ENU_PRD_CATE_ZD = "ZD";//整体吊顶
        public static final String ENU_PRD_CATE_DG = "DG";//电器展柜
        public static final String ENU_PRD_CATE_XC = "XC";//吸尘器
        public static final String ENU_PRD_CATE_ZJ = "ZJ";//蒸汽机
        public static final String ENU_PRD_CATE_KT = "KT";//家用空调
        public static final String ENU_PRD_CATE_CS = "CS";//除湿机
        public static final String ENU_PRD_CATE_JH = "JH";//加湿器
        public static final String ENU_PRD_CATE_JS = "JS";//净化器

        //产品形态》套机
        public static final String[] SET_PRODUCT = { ENU_SET_PRODUCT,//套机
        };
        
        //产品形态》整机
        public static final String[] WHOLE_PRODUCT = { ENU_WHOLE_PRODUCT,//整机
        };
        
        //产品形态》生产机
        public static final String[] PRODUCE_UNIT = { ENU_INDOOR_PRODUCT,//室内机
                ENU_OUTDOOR_PRODUCT,//室外机
                ENU_WHOLE_PRODUCT,//整体机
                ENU_SAMPLE_PRODUCT,//样机(内销)
                ENU_PARTS_PRODUCT,//散件
                ENU_ACCESSORIES_PRODUCT,//独立销售模块(配件)
        };

        //发放后的生命周期状态
        public static final String[] afterPublishProductLifecycle = { ENU_MANUFACTURE_RELEASE,//制造发放
                ENU_PRE_SALE,//预销售
                ENU_SALE,//在销售
                ENU_FROZEN_IN,//冻结
                ENU_PRE_DELIST,//预退市
                ENU_DELIST,//退市
                ENU_OBSOLETE //废弃
        };

        //发放前生命周期状态
        public static final String[] beforePublishProductLifecycle = { ENU_NEW,//新建
                ENU_STORE,//储备
                ENU_RESEARCH,//研究
                ENU_DESIGN,//设计
                ENU_DESIGN_RELEASE //设计发放
        };

        //public static final String designReleasedProductLifecycle = "DESIGN_RELEASE";//设计发放

        //创建套机时允许的产品形态值列表
        public static final String[] ALLOWED_PRODUCT_FORM_LIST_FOR_SET_PRODUCT = { ENU_SET_PRODUCT //套机
        };

        //创建生产码时允许的产品形态值列表
        public static final String[] ALLOWED_PRODUCT_FORM_LIST_FOR_PRODUCE_UNIT = { ENU_INDOOR_PRODUCT,//室内机
                ENU_OUTDOOR_PRODUCT,//室外机
                ENU_SAMPLE_PRODUCT,//样机(内销)
                ENU_PARTS_PRODUCT,//散件
                ENU_ACCESSORIES_PRODUCT,//独立销售模块(配件)
                ENU_WHOLE_PRODUCT //整体机
        };

        //创建整体机时允许的产品形态值列表 
        public static final String[] ALLOWED_PRODUCT_FORM_LIST_FOR_WHOLE_PRODUCT = { ENU_WHOLE_PRODUCT //整体机
        };

        //采购标识(创建销售码时允许的值列表)procurementIndicator  >> 临时列表
        public static final String[] ALLOWED_PROCUREMENT_INDICATOR_LIST = { "SL" };

        public static String defaultProcIndicator = "SL";

        //启动“通用状态变更流程”Processor类中引用  BEGIN
        //检查E视图产品对象的产品生命周期状态是否属于“制造发放”，“在销售”，“冻结”，“预退市”，“退市”。如果不是，则提示错误消息。
        public static final String[] IN_CHANGEABLE_STATE_FOR_E_VIEW = { 
														ENU_RESEARCH,
														ENU_DESIGN,
														ENU_DESIGN_RELEASE,
														ENU_MANUFACTURE_RELEASE,
														ENU_SALE,
														ENU_FROZEN_IN,
														ENU_PRE_DELIST,
														ENU_DELIST};

        //M视图产品只能在研究、设计、设计发放状态的才能走变更流程
        public static final String[] IN_CHANGEABLE_STATE_FOR_M_VIEW = { ENU_SALE, ENU_FROZEN_IN,ENU_DELIST, ENU_MANUFACTURE_RELEASE,ENU_PRE_DELIST};
        
        public static final String[] CHECK_VIEW_STATE = { ENU_PRE_DELIST, ENU_SALE, ENU_PRE_SALE };

        public static final String[] CHECK_FROZEN_IN_AND_OBSOLETE = { ENU_FROZEN_IN, ENU_OBSOLETE };

        public static final String[] ALLOWED_TARGET_STATE = { ENU_PRE_SALE,//预销售
                ENU_SALE,//在销售
                ENU_FROZEN_IN,//冻结
                ENU_PRE_DELIST,//预退市
                ENU_DELIST,//退市
                ENU_OBSOLETE //废弃
        };

        //启动“通用状态变更流程”Processor类中引用  END

        //退市流程中允许的状态列表
        public static final String[] ALLOWED_STAT_DELIST = { ENU_PRE_SALE,//预销售
    		ENU_SALE,//在销售
    		ENU_FROZEN_IN,//冻结
    		ENU_PRE_DELIST,//预退市
            ENU_MANUFACTURE_RELEASE //制造发放
        };

        //产品大类列表
        public static final String[] PROD_CATEGORY_LIST = { ENU_PRD_CATE_DT,//多头炉
                ENU_PRD_CATE_RZ,//燃气灶
                ENU_PRD_CATE_ZH,//组合灶
                ENU_PRD_CATE_WB,//微波炉
                ENU_PRD_CATE_ZL,//蒸汽炉
                ENU_PRD_CATE_BW,//保温抽屉
                ENU_PRD_CATE_XK,//桌面式烤箱（小烤箱）
                ENU_PRD_CATE_DK,//驻立式烤箱（大烤箱）
                ENU_PRD_CATE_SC,//商用厨房设备
                ENU_PRD_CATE_YJ,//吸油烟机
                ENU_PRD_CATE_XD,//消毒柜
                ENU_PRD_CATE_ZC,//整体橱柜
                ENU_PRD_CATE_ZD,//整体吊顶
                ENU_PRD_CATE_DG,//电器展柜
                ENU_PRD_CATE_XC,//吸尘器
                ENU_PRD_CATE_ZJ,//蒸汽机
                ENU_PRD_CATE_KT,//家用空调
                ENU_PRD_CATE_CS,//除湿机
                ENU_PRD_CATE_JH,//加湿器
                ENU_PRD_CATE_JS //净化器
        };

        public static final String[] ALL_PRODUCT_LIFESTATE = { ENU_NEW,//新建
                ENU_STORE,//储备
                ENU_RESEARCH,//研究
                ENU_DESIGN,//设计
                ENU_DESIGN_RELEASE,//设计发放
                ENU_MANUFACTURE_RELEASE,// 制造发放
                ENU_PRE_SALE,//预销售
                ENU_SALE,//在销售
                ENU_FROZEN_IN,//冻结
                ENU_PRE_DELIST,//预退市
                ENU_DELIST,//退市
                ENU_OBSOLETE //废弃
        };

    }

}
