@echo off

cd %~dp0

call mvn -U clean install
cd fmv-mymedia
call mvn dependency:copy-dependencies
cd ..

if not exist target\pkg-mymedia mkdir target\pkg-mymedia

copy fmv-mymedia\target\dependency\*.jar target\pkg-mymedia

for /f "tokens=*" %%a in ('dir fmv-mymedia\target\fmv-mymedia-*-SNAPSHOT.jar /B /OD') do set newest=%%a
copy "fmv-mymedia\target\%newest%" target\pkg-mymedia

echo.
echo Copied in %~dp0fmv-mymedia\target



%SystemRoot%\explorer.exe "%~dp0fmv-mymedia\target"

pause
