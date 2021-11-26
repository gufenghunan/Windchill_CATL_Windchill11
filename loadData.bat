
REM ==================设置用户名密码==================

set WT_USER=orgadmin
set WT_PASSWORD=778899
set DB_USER=plm
set DB_PASSWORD=plm

set WT_USER=orgadmin
set WT_PASSWORD=1
set DB_USER=plmdb
set DB_PASSWORD=plmdb

cd %WT_HOME%

REM ==================部署客制化代码==================
ant -f bin/swmaint.xml installSiteChanges

REM ==================注册客制化配置文件==================
xconfmanager -i codebase\com\ptc\xworks\config\xworks-windchill.properties.xconf
xconfmanager -i \codebase\config\custom\custom.xconf -p
xconfmanager -p

REM ==================合并javascript文件==================
ant -f %WT_HOME%/bin/jsfrag_combine.xml

REM ==================重新编译客制化过的枚举类型==================
ResourceBuild wt.project.RoleRB
ResourceBuild wt.part.SourceRB
ResourceBuild wt.part.QuantityUnitRB
ResourceBuild wt.lifecycle.StateRB
ResourceBuild wt.access.AdHocAccessKeyRB
ResourceBuild com.catl.part.classification.AttributeForFAERB

REM ==================重新打包Applet Jar文件==================
ant -f codebase/MakeJar.xml custUpdate

REM ==================执行下面的命令创建所有的客制化表/索引/序列==================
windchill --javaargs="-Dwt.tools.sql.dbUser=%DB_USER% -Dwt.tools.sql.dbPassword=%DB_PASSWORD%" wt.tools.sql.SQLCommandTool create_catl_tables.sql %WT_HOME%\db\sql3
REM 检查Windchill目录下的errors.txt文件

REM ==================加载Phase1用户群组, 仅在空白系统初始化时需要执行==================
windchill wt.load.LoadFromFile -d %WT_HOME%\loadFiles\com\catl\dms\01users.xml -u %WT_USER% -p %WT_PASSWORD% -CONT_PATH /
windchill wt.load.LoadFromFile -d %WT_HOME%\loadFiles\com\catl\dms\02groupName.xml -u %WT_USER% -p %WT_PASSWORD% -CONT_PATH /wt.inf.container.OrgContainer=CATL
windchill wt.load.LoadFromFile -d %WT_HOME%\loadFiles\com\catl\dms\02groupUser.xml -u %WT_USER% -p %WT_PASSWORD% -CONT_PATH /wt.inf.container.OrgContainer=CATL
windchill wt.load.LoadFromFile -d %WT_HOME%\loadFiles\com\catl\group\group.xml -u %WT_USER% -p %WT_PASSWORD% -CONT_PATH /wt.inf.container.OrgContainer=CATL
windchill wt.load.LoadFromFile -d %WT_HOME%\loadFiles\com\catl\group\grouppeople.xml -u %WT_USER% -p %WT_PASSWORD% -CONT_PATH /wt.inf.container.OrgContainer=CATL

REM ==================加载Phase2用户群组, 仅在空白系统初始化时需要执行==================
windchill wt.load.LoadFromFile -d %WT_HOME%\loadFiles\com\catl\group\users_phase2.xml -u %WT_USER% -p %WT_PASSWORD% -CONT_PATH /
windchill wt.load.LoadFromFile -d %WT_HOME%\loadFiles\com\catl\group\group_phase2.xml -u %WT_USER% -p %WT_PASSWORD% -CONT_PATH /wt.inf.container.OrgContainer=CATL
windchill wt.load.LoadFromFile -d %WT_HOME%\loadFiles\com\catl\group\group_users_phase2.xml -u %WT_USER% -p %WT_PASSWORD% -CONT_PATH /wt.inf.container.OrgContainer=CATL

REM ==================加载对象类型==================
windchill wt.load.LoadFileSet -file %WT_HOME%\loadFiles\com\catl\loader\loadGlobalEnumeration.xml -NoServerStop -u %WT_USER% -p %WT_PASSWORD% -UAOps
windchill wt.load.LoadFileSet -file %WT_HOME%\loadFiles\com\catl\loader\loadPartType.xml -NoServerStop -u %WT_USER% -p %WT_PASSWORD% -UAOps
windchill wt.load.LoadFileSet -file %WT_HOME%\loadFiles\com\catl\loader\loadDocType.xml -NoServerStop -u %WT_USER% -p %WT_PASSWORD% -UAOps
windchill wt.load.LoadFileSet -file %WT_HOME%\loadFiles\com\catl\loader\loadChangeType.xml -NoServerStop -u %WT_USER% -p %WT_PASSWORD% -UAOps
windchill wt.load.LoadFileSet -file %WT_HOME%\loadFiles\com\catl\loader\loadPromotionNoticeType.xml -NoServerStop -u %WT_USER% -p %WT_PASSWORD% -UAOps

REM ==================重启一次Windchill服务==================

REM ==================加载工作流模板、生命周期摸吧、对象初始化规则==================
windchill wt.load.LoadFileSet -file %WT_HOME%\loadFiles\com\catl\loader\loadWorkflow.xml -NoServerStop -u %WT_USER% -p %WT_PASSWORD% -UAOps
windchill wt.load.LoadFileSet -file %WT_HOME%\loadFiles\com\catl\loader\loadLifeCycle.xml -NoServerStop -u %WT_USER% -p %WT_PASSWORD% -UAOps
windchill wt.load.LoadFileSet -file %WT_HOME%\loadFiles\com\catl\loader\loadOIR.xml -NoServerStop -u %WT_USER% -p %WT_PASSWORD% -UAOps

REM ==================创建存储库、产品容器==================
windchill wt.load.LoadFileSet -file %WT_HOME%\loadFiles\com\catl\loader\loadContextTemplate.xml -NoServerStop -u %WT_USER% -p %WT_PASSWORD% -UAOps

REM ==================加载搜选项==================
REM windchill wt.load.LoadFileSet -file %WT_HOME%\loadFiles\com\catl\loader\loadPreference.xml -NoServerStop -u %WT_USER% -p %WT_PASSWORD% -UAOps
REM windchill wt.load.LoadFromFile -d %WT_HOME%\loadFiles\com\catl\preference\preference.xml -u %WT_USER% -p %WT_PASSWORD%
REM windchill wt.load.LoadFromFile -d %WT_HOME%\loadFiles\com\catl\preference\changedPreference.xml -u %WT_USER% -p %WT_PASSWORD%
windchill wt.load.LoadFromFile -d %WT_HOME%\loadFiles\com\catl\preference\changedPreference2.xml -u %WT_USER% -p %WT_PASSWORD%

REM =================加载权限==================
windchill wt.load.LoadFromFile -d loadFiles\com\catl\access\accessRule.xml -u %WT_USER% -p %WT_PASSWORD%

REM ==================加载视图==================
windchill wt.load.LoadFromFile -d %WT_HOME%\loadFiles\com\catl\view\BomViewTree.xml -u %WT_USER% -p %WT_PASSWORD%
windchill wt.load.LoadFromFile -d %WT_HOME%\loadFiles\com\catl\view\BomViewUses.xml -u %WT_USER% -p %WT_PASSWORD%
windchill wt.load.LoadFromFile -d %WT_HOME%\loadFiles\com\catl\view\FolderViewAll.xml -u %WT_USER% -p %WT_PASSWORD%

windchill wt.load.LoadFromFile -d %WT_HOME%\loadFiles\com\catl\view\PSBTree.xml -u %WT_USER% -p %WT_PASSWORD%
windchill wt.load.LoadFromFile -d %WT_HOME%\loadFiles\com\catl\view\PSBUses.xml -u %WT_USER% -p %WT_PASSWORD%
windchill wt.load.LoadFromFile -d %WT_HOME%\loadFiles\com\catl\view\folderbrowser_PDM.xml -u %WT_USER% -p %WT_PASSWORD%

REM ====================================历史数据整理====================================

REM ==================设计禁用，刷新生命周期模板==================
windchill com.catl.common.toolbox.data.refreshLifeCycle com.CATLBattery.CATLPart LC_Part_Cycle true %WT_USER% %WT_PASSWORD%
windchill com.catl.common.toolbox.data.refreshLifeCycle com.CATLBattery.autocadDrawing LC_epmdoc_Cycle true %WT_USER% %WT_PASSWORD%
windchill com.catl.common.toolbox.data.refreshLifeCycle com.CATLBattery.DefaultEPMDocument LC_epmdoc_Cycle true %WT_USER% %WT_PASSWORD%
windchill com.catl.common.toolbox.data.refreshLifeCycle com.CATLBattery.gerberDoc LC_doc_cycle_phase2 true %WT_USER% %WT_PASSWORD%
windchill com.catl.common.toolbox.data.refreshLifeCycle com.CATLBattery.pcbaDrawing LC_doc_cycle_phase2 true %WT_USER% %WT_PASSWORD%

REM ==================初始化所有物料分类的配置信息==================
windchill com.catl.loadData.NodeConfigDataUtil %WT_USER% %WT_PASSWORD%

REM ==================从Excel导入物料分类的配置信息==================
windchill com.catl.loadData.NodeConfigDataUtil %WT_HOME%\loadFiles\com\catl\part\NodeConfigDataTemplate.xlsx %WT_USER% %WT_PASSWORD%

REM ==================刷新所有物料成熟度到1==================
windchill com.catl.common.toolbox.data.refreshWTPartMaturity %WT_USER% %WT_PASSWORD%

REM ==================从Excel导入物料成熟度到3==================
windchill com.catl.common.toolbox.data.UpdateWTPartMaturity %WT_HOME%\loadFiles\com\catl\part\update_maturity.xlsx %WT_USER% %WT_PASSWORD%

REM ==================刷新FAE状态==================
windchill com.catl.common.toolbox.data.refreshWTPartFAEStatus %WT_USER% %WT_PASSWORD%

REM ==================导入Datasheet==================
copy %WT_HOME%\loadXMLFiles\standardX24.dtd %WT_HOME%\bin\
windchill com.catl.common.toolbox.data.LoadDataSheetFromFile %WT_USER% %WT_PASSWORD%
%WT_HOME%\loadFiles\com\catl\dms\datasheet\loadDataSheet.bat

REM ==================添加新增的目录==================
%WT_HOME%\loadFiles\com\catl\acl_update\add_folders.bat
REM ==================修改电子电气件文件夹域==================
%WT_HOME%\loadFiles\com\catl\acl_update\add_folders_electronic.bat

REM ==================刷新产品的权限设置==================
%WT_HOME%\loadFiles\com\catl\acl_update\update_product_acl.bat
REM ==================刷新存储的权限设置==================
%WT_HOME%\loadFiles\com\catl\acl_update\update_library_acl.bat
REM ==================刷新PDM的权限设置==================
%WT_HOME%\loadFiles\com\catl\acl_update\update_pdm_acl.bat
REM ==================刷新电子电气件的权限设置==================
%WT_HOME%\loadFiles\com\catl\acl_update\update_electronic_acl.bat

REM ==================刷新变更对象的状态图标==================
windchill wt.change2.ChangeStatusCalculator

REM ==================权限设置==================
REM 根据/doc/遗漏的权限.txt来设置系统权限
