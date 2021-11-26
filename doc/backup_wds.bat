@echo off
set LDIF_EXPORT_PATH=D:\ldif_export
Set WDS_PASSWORD=ldapadmin
For /f "tokens=1-2 delims=/:" %%a in ('time /t') do (set mytime=%%a%%b)
set CURRENT_TIMESTAMP=%date:~0,4%%date:~5,2%%date:~8,2%_%mytime%
echo "Start export LDIF from WindchillDS at " %CURRENT_TIMESTAMP%
cmd /C D:\ptc\WindchillDS\server\bat\export-ldif -l D:\ldif_export\WDS_%CURRENT_TIMESTAMP%.ldif -n userRoot -h 127.0.0.1 -p 4444 -D cn=Manager -w %WDS_PASSWORD%  -X

echo "开始删除30天前的LDIF文件..."
forfiles /P %LDIF_EXPORT_PATH% /D -30 /C "cmd /c del /F /Q @file" 