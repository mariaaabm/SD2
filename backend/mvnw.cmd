@echo off
setlocal

set MAVEN_VERSION=3.9.9
set BASE_DIR=%~dp0
set MAVEN_HOME=%BASE_DIR%.mvn\apache-maven-%MAVEN_VERSION%
set MAVEN_ARCHIVE=%BASE_DIR%.mvn\apache-maven-%MAVEN_VERSION%-bin.zip
set MAVEN_URL=https://archive.apache.org/dist/maven/maven-3/%MAVEN_VERSION%/binaries/apache-maven-%MAVEN_VERSION%-bin.zip

if exist "%MAVEN_HOME%\bin\mvn.cmd" goto run

if not exist "%BASE_DIR%.mvn" mkdir "%BASE_DIR%.mvn"

if not exist "%MAVEN_ARCHIVE%" (
  powershell -NoProfile -ExecutionPolicy Bypass -Command "Invoke-WebRequest -Uri '%MAVEN_URL%' -OutFile '%MAVEN_ARCHIVE%'"
)

powershell -NoProfile -ExecutionPolicy Bypass -Command "Expand-Archive -Force '%MAVEN_ARCHIVE%' '%BASE_DIR%.mvn'"

:run
call "%MAVEN_HOME%\bin\mvn.cmd" %*

