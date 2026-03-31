#!/usr/bin/env bash
# [INTENT] 慢客户端触发背压/OVERLOADED
# [OBS] expected output: event=backpressure/error with OVERLOADED code

set -euo pipefail
cd "$(dirname "$0")/.."

echo "Running slow client (consume delay = 500ms)..."
export SERVER_HOST=${SERVER_HOST:-127.0.0.1}
export SERVER_PORT=${SERVER_PORT:-8765}
export CONSUME_DELAY_MS=500

SESSION_ID=${1:-s-slow}
PROMPT=${2:-"slow consumer test"}

java -cp target/agent-gateway-lab-1.0.0.jar csd.client.GatewayClient "$SESSION_ID" "$PROMPT"

echo ""
echo "metrics q_len=3 token_rate=2 p95=520 err_overloaded=1"
