@echo off

rem set PWD=%~dp0
rem set PWD=D:\tmp\fmv\fmv\
set VERSION=0.25.4
rem set VERSION=0.18.4

set REPO_URL=https://nexus.oodrive.net/nexus/
set REPO_LOCAL=%USERPROFILE%\.m2\repository\org\fagu\
set REPO_NAME=releases


if "%VERSION:~-8%" == "SNAPSHOT" (
  set REPO_NAME=snapshots
)


call:artefact fmv-dependencies
call:artefact fmv-ffmpeg
call:artefact fmv-image
call:artefact fmv-imagemagick
call:artefact fmv-media
call:artefact fmv-parent
call:artefact fmv-textprogressbar
call:artefact fmv-soft
call:artefact fmv-soft-auto
call:artefact fmv-soft-spring-boot-1.x
call:artefact fmv-soft-spring-boot-2.x
call:artefact fmv-soft-spring-boot-3.x
call:artefact fmv-utils
call:artefact fmv-version

goto end


rem ---------------------
:artefact
echo.%1...
call:push_artefact "%REPO_LOCAL%%1\%VERSION%\%1-%VERSION%.pom" %1
call:push_artefact "%REPO_LOCAL%%1\%VERSION%\%1-%VERSION%.jar" %1
call:push_artefact "%REPO_LOCAL%%1\%VERSION%\%1-%VERSION%-sources.jar" %1
call:push_artefact "%REPO_LOCAL%%1\%VERSION%\%1-%VERSION%-tests.jar" %1
goto:eof

rem ---------------------

:push_artefact
if exist %1 (
  echo "Send %1"
  curl --upload-file %1 %REPO_URL%content/repositories/%REPO_NAME%/org/fagu/%2/%VERSION%/%~nx1
) 
goto:eof

rem ---------------------

:end
pause 