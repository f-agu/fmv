@echo off

java -cp "%~dp0lib\*" org.fagu.fmv.mymedia.rip.dvd.Bootstrap

%SystemRoot%\explorer.exe "d:\tmp\dvd-rip\"

pause