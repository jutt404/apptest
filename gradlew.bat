@echo off
where gradle >nul 2>nul
if %ERRORLEVEL% EQU 0 (
  gradle %*
  exit /b %ERRORLEVEL%
)
echo Gradle command not found. Open this project in Android Studio or build on Codemagic.
exit /b 1
