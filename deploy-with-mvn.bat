@echo off

rem mvn -q -Dexec.executable=echo -Dexec.args=${project.version} --non-recursive exec:exec

FOR /F "tokens=*" %a in ('mvn -q -Dexec.executable=echo -Dexec.args=${project.version} --non-recursive exec:exec') do SET OUTPUT=%a

rem echo %OUTPUT%

rem set REPO_NAME=releases

rem for /f %%i in ('mvn -q -Dexec.executable=echo -Dexec.args=${project.version} --non-recursive exec:exec') do set VERSION=%%i

rem mvn -q -Dexec.executable=echo -Dexec.args='${project.version}' --non-recursive exec:exec

rem echo %VERSION%

rem if "%VERSION:~-8%" == "SNAPSHOT" (
rem   set REPO_NAME=snapshots
rem )

rem echo %REPO_NAME%

rem mvn deploy -DskipTests -DaltDeploymentRepository=anid::default::https://nexus.oodrive.net/nexus/content/repositories/snapshots
