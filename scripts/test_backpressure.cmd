@echo off
REM [INTENT] 背压测试 - 生成大量token触发队列满
REM [OBS] expected: backpressure or OVERLOADED events

cd /d "%~dp0.."

echo ========================================
echo Agent Gateway - Backpressure Test
echo ========================================
echo.
echo Config: TOKEN_COUNT=50, TOKEN_DELAY_MS=10, QUEUE_LIMIT=3
echo Slow client delay: 500ms
echo.

REM Clean old logs
del /q logs\*.log 2>nul

REM Set environment for many fast tokens
set TOKEN_COUNT=50
set TOKEN_DELAY_MS=10
set QUEUE_LIMIT=3

echo [Step 1] Starting server...
start "GatewayServer" cmd /c "java -jar target\agent-gateway-lab-1.0.0.jar 8765"

REM Wait for server to start
timeout /t 3 /nobreak >nul

echo [Step 2] Running slow client (500ms delay)...
java -cp target\agent-gateway-lab-1.0.0.jar csd.client.GatewayClient 127.0.0.1 8765 500 s-backpressure

echo.
echo [Step 3] Checking logs...
echo.
echo --- Server backpressure events ---
find "backpressure" logs\server.log
find "OVERLOADED" logs\server.log

echo.
echo --- Server error events ---
find "event=error" logs\server.log

echo.
echo ========================================
echo [Test Complete] Check logs\server.log for details
echo ========================================

REM Stop server
taskkill /FI "WINDOWTITLE eq GatewayServer" >nul 2>&1
