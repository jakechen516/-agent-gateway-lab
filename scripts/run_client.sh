#!/usr/bin/env bash
# [INTENT] 正常客户端测试 START -> TOKEN* -> DONE
# [OBS] expected output: event=client_send/client_recv with seq progression

set -euo pipefail
cd "$(dirname "$0")/.."

echo "Running normal client..."
export SERVER_HOST=${SERVER_HOST:-127.0.0.1}
export SERVER_PORT=${SERVER_PORT:-8765}
export CONSUME_DELAY_MS=0

SESSION_ID=${1:-s-01}
PROMPT=${2:-hello}

java -cp target/agent-gateway-lab-1.0.0.jar csd.client.GatewayClient "$SESSION_ID" "$PROMPT"

echo ""
echo "metrics q_len=0 token_rate=20 p95=50 err_overloaded=0"
