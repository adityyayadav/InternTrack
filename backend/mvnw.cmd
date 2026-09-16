@REM Maven Wrapper script for Windows
@REM Downloads and caches Maven if not present, then delegates to it.
@echo off
setlocal

set "MAVEN_PROJECTBASEDIR=%~dp0"
set "MAVEN_WRAPPER_PROPERTIES=%MAVEN_PROJECTBASEDIR%.mvn\wrapper\maven-wrapper.properties"
set "MAVEN_HOME=%USERPROFILE%\.m2\wrapper\dists\apache-maven-3.9.9"

if not exist "%MAVEN_HOME%\apache-maven-3.9.9\bin\mvn.cmd" (
    echo Downloading Maven 3.9.9...
    if not exist "%MAVEN_HOME%" mkdir "%MAVEN_HOME%"
    powershell -Command "Invoke-WebRequest -Uri 'https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/3.9.9/apache-maven-3.9.9-bin.zip' -OutFile '%MAVEN_HOME%\maven.zip'"
    powershell -Command "Expand-Archive -Path '%MAVEN_HOME%\maven.zip' -DestinationPath '%MAVEN_HOME%' -Force"
    del "%MAVEN_HOME%\maven.zip"
)

set "MAVEN_CMD=%MAVEN_HOME%\apache-maven-3.9.9\bin\mvn.cmd"
"%MAVEN_CMD%" %*

endlocal
