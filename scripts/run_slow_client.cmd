@echo off
REM [INTENT] 启动慢客户端，触发背压
REM [OBS] expected output: backpressure or OVERLOADED error

cd /d "%~dp0.."

set HOST=%1
set PORT=%2
set DELAY=%3
set SESSION=%4

if "%HOST%"=="" set HOST=127.0.0.1
if "%PORT%"=="" set PORT=8765
if "%DELAY%"=="" set DELAY=200
if "%SESSION%"=="" set SESSION=s-slow

echo Running slow client (delay=%DELAY%ms) to %HOST%:%PORT%...
java -cp target\agent-gateway-lab-1.0.0.jar csd.client.GatewayClient %HOST% %PORT% %DELAY% %SESSION%
