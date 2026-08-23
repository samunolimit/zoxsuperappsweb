@echo off
setlocal
set "GRADLE_VERSION=9.3.1"
set "GRADLE_HOME=%USERPROFILE%\.gradle\bootstrap\gradle-%GRADLE_VERSION%"
set "GRADLE_ZIP=%TEMP%\gradle-%GRADLE_VERSION%-bin.zip"

if exist "%GRADLE_HOME%\bin\gradle.bat" goto runGradle

if not exist "%GRADLE_ZIP%" (
  echo Downloading Gradle %GRADLE_VERSION%...
  powershell -NoProfile -ExecutionPolicy Bypass -Command "Invoke-WebRequest -Uri 'https://services.gradle.org/distributions/gradle-%GRADLE_VERSION%-bin.zip' -OutFile '%GRADLE_ZIP%'"
  if errorlevel 1 exit /b 1
)

if not exist "%USERPROFILE%\.gradle\bootstrap" mkdir "%USERPROFILE%\.gradle\bootstrap"
powershell -NoProfile -ExecutionPolicy Bypass -Command "Expand-Archive -Path '%GRADLE_ZIP%' -DestinationPath '%USERPROFILE%\.gradle\bootstrap' -Force"
if errorlevel 1 exit /b 1

:runGradle
call "%GRADLE_HOME%\bin\gradle.bat" %*
exit /b %ERRORLEVEL%
