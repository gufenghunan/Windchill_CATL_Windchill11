REM set WT_USER=orgadmin
REM set WT_PASSWORD=1

windchill com.catl.tools.acl.ACLHelper importcsv %WT_HOME%\loadFiles\com\catl\acl_update\library_add_access.csv /wt.inf.container.OrgContainer=CATL/wt.inf.library.WTLibrary=��о  %WT_USER% %WT_PASSWORD%
windchill com.catl.tools.acl.ACLHelper importcsv %WT_HOME%\loadFiles\com\catl\acl_update\library_add_access.csv /wt.inf.container.OrgContainer=CATL/wt.inf.library.WTLibrary=���ӵ�����  %WT_USER% %WT_PASSWORD%
windchill com.catl.tools.acl.ACLHelper importcsv %WT_HOME%\loadFiles\com\catl\acl_update\library_add_access.csv /wt.inf.container.OrgContainer=CATL/wt.inf.library.WTLibrary=ԭ����  %WT_USER% %WT_PASSWORD%
windchill com.catl.tools.acl.ACLHelper importcsv %WT_HOME%\loadFiles\com\catl\acl_update\library_add_access.csv /wt.inf.container.OrgContainer=CATL/wt.inf.library.WTLibrary=���̼�  %WT_USER% %WT_PASSWORD%
windchill com.catl.tools.acl.ACLHelper refresh %WT_HOME%\loadFiles\com\catl\acl_update\datasheet_add_access.csv [/wt.inf.container.OrgContainer=CATL/wt.inf.library.WTLibrary=Datasheet��]  %WT_USER% %WT_PASSWORD%