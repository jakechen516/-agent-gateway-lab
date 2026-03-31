@echo off
REM [INTENT] 编译项目
REM [OBS] expected output: BUILD SUCCESS

cd /d "%~dp0.."

echo Building Agent Gateway Lab...
call mvn clean package -DskipTests

if %ERRORLEVEL% neq 0 (
    echo [ERROR] Build failed!
    exit /b 1
)

echo.
echo [SUCCESS] Build complete. JAR: target\agent-gateway-lab-1.0.0.jar
