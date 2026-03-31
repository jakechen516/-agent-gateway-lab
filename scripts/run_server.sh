#!/usr/bin/env bash
# [INTENT] 启动Gateway服务端
# [OBS] expected output: event=server_start with host/port/transport

set -euo pipefail
cd "$(dirname "$0")/.."

echo "Building project..."
mvn -q package -DskipTests

echo "Starting Gateway Server..."
export SERVER_PORT=${SERVER_PORT:-8765}
export QUEUE_LIMIT=${QUEUE_LIMIT:-3}

java -cp target/agent-gateway-lab-1.0.0.jar csd.gateway.GatewayServer
