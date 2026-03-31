@echo off
REM [INTENT] 启动Gateway服务端
REM [OBS] expected output: event=server_start with host/port/transport

cd /d "%~dp0.."

echo Building project...
call mvn -q package -DskipTests
if %ERRORLEVEL% neq 0 (
    echo Build failed!
    exit /b 1
)

echo Starting Gateway Server on port 8765...
java -jar target\agent-gateway-lab-1.0.0.jar 8765
