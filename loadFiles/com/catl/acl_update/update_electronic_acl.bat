REM set WT_USER=orgadmin
REM set WT_PASSWORD=1

windchill com.catl.tools.acl.ACLHelper clean [/wt.inf.container.OrgContainer=CATL/wt.inf.library.WTLibrary=���ӵ�����]/Default/CATLDefault %WT_USER% %WT_PASSWORD%
windchill com.catl.tools.acl.ACLHelper importcsv %WT_HOME%\loadFiles\com\catl\acl_update\electronic_change_access.csv /wt.inf.container.OrgContainer=CATL/wt.inf.library.WTLibrary=���ӵ�����  %WT_USER% %WT_PASSWORD%