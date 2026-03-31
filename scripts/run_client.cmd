@echo off
REM [INTENT] 启动正常客户端
REM [OBS] expected output: TOKEN events followed by DONE

cd /d "%~dp0.."

set HOST=%1
set PORT=%2
set SESSION=%3

if "%HOST%"=="" set HOST=127.0.0.1
if "%PORT%"=="" set PORT=8765
if "%SESSION%"=="" set SESSION=s-normal

echo Running normal client to %HOST%:%PORT%...
java -cp target\agent-gateway-lab-1.0.0.jar csd.client.GatewayClient %HOST% %PORT% 0 %SESSION%
