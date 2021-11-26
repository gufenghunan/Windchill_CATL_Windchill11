REM set WT_USER=orgadmin
REM set WT_PASSWORD=1

windchill com.catl.tools.acl.ACLHelper refresh_domain %WT_HOME%\loadFiles\com\catl\acl_update\electronic_folder_domain_map.csv [/wt.inf.container.OrgContainer=CATL/wt.inf.library.WTLibrary=电子电气件] %WT_USER% %WT_PASSWORD%
