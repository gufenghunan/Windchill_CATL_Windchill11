REM set WT_USER=orgadmin
REM set WT_PASSWORD=1

windchill com.catl.tools.acl.ACLHelper importcsv %WT_HOME%\loadFiles\com\catl\acl_update\pdm_add_access.csv /wt.inf.container.OrgContainer=CATL  %WT_USER% %WT_PASSWORD%