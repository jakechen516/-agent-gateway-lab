@echo off
REM [INTENT] 验证脚本 - 输出日志摘要
REM [OBS] Compare normal vs slow client results

cd /d "%~dp0.."

echo ========================================
echo Agent Gateway Lab - Verification Report
echo ========================================
echo.

if not exist logs\server.log (
    echo [ERROR] logs\server.log not found. Run server and client first.
    exit /b 1
)

echo [Server Log Summary]
echo --------------------
echo Total events:
find /c "event=" logs\server.log

echo.
echo Token events:
find /c "event=token" logs\server.log

echo.
echo Done events:
find /c "event=done" logs\server.log

echo.
echo Error events:
find /c "event=error" logs\server.log

echo.
echo Backpressure events:
find /c "event=backpressure" logs\server.log

echo.
echo ========================================
echo [Client Log Summary]
echo --------------------
if exist logs\client.log (
    echo Client recv events:
    find /c "event=client_recv" logs\client.log
    
    echo.
    echo Client close events:
    find /c "event=client_close" logs\client.log
) else (
    echo [WARN] logs\client.log not found.
)

echo.
echo ========================================
echo [Verification Complete]
echo ========================================
