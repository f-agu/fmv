rem https://nexus.oodrive.net/nexus/index.html#welcome
@echo off

set VERSION=0.7.1

echo "mvn -X -e deploy:deploy-file -DpomFile=%USERPROFILE%\.m2\repository\org\fagu\fmv-soft\%VERSION%\fmv-soft-%VERSION%.pom -Dfile=%USERPROFILE%\.m2\repository\org\fagu\fmv-soft\%VERSION%\fmv-soft-%VERSION%.jar -DrepositoryId=Nexus -Durl=https://nexus.oodrive.net/nexus/content/groups/public"

rem mvn -X -e deploy:deploy-file -DpomFile=%USERPROFILE%\.m2\repository\org\fagu\fmv-soft\%VERSION%\fmv-soft-%VERSION%.pom -Dfile=%USERPROFILE%\.m2\repository\org\fagu\fmv-soft\%VERSION%\fmv-soft-%VERSION%.jar -DrepositoryId=Nexus -Durl=https://nexus.oodrive.net/nexus/content/groups/public > out


mvn -X -e deploy:deploy-file -DpomFile=%USERPROFILE%\.m2\repository\org\fagu\fmv-soft\%VERSION%\fmv-soft-%VERSION%.pom -Dfile=%USERPROFILE%\.m2\repository\org\fagu\fmv-soft\%VERSION%\fmv-soft-%VERSION%.jar -DrepositoryId=Nexus -Durl=file://%USERPROFILE%/.m2/repository/org/fagu/fmv-soft/%VERSION%/fmv-soft-%VERSION%.jar  > out

pause    