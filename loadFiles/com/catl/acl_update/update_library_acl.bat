REM set WT_USER=orgadmin
REM set WT_PASSWORD=1

windchill com.catl.tools.acl.ACLHelper importcsv %WT_HOME%\loadFiles\com\catl\acl_update\library_add_access.csv /wt.inf.container.OrgContainer=CATL/wt.inf.library.WTLibrary=电芯  %WT_USER% %WT_PASSWORD%
windchill com.catl.tools.acl.ACLHelper importcsv %WT_HOME%\loadFiles\com\catl\acl_update\library_add_access.csv /wt.inf.container.OrgContainer=CATL/wt.inf.library.WTLibrary=电子电气件  %WT_USER% %WT_PASSWORD%
windchill com.catl.tools.acl.ACLHelper importcsv %WT_HOME%\loadFiles\com\catl\acl_update\library_add_access.csv /wt.inf.container.OrgContainer=CATL/wt.inf.library.WTLibrary=原材料  %WT_USER% %WT_PASSWORD%
windchill com.catl.tools.acl.ACLHelper importcsv %WT_HOME%\loadFiles\com\catl\acl_update\library_add_access.csv /wt.inf.container.OrgContainer=CATL/wt.inf.library.WTLibrary=紧固件  %WT_USER% %WT_PASSWORD%
windchill com.catl.tools.acl.ACLHelper refresh %WT_HOME%\loadFiles\com\catl\acl_update\datasheet_add_access.csv [/wt.inf.container.OrgContainer=CATL/wt.inf.library.WTLibrary=Datasheet库]  %WT_USER% %WT_PASSWORD%