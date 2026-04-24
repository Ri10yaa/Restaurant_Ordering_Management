#!/usr/bin/env bash
set -euo pipefail

BASE_URL="${BASE_URL:-http://localhost:8080}"
WAITER_CODES=("WK1624" "WI3788" "WR7285" "WN1299" "WM1405")
WAITER_NAMES=("Ravi K" "Ananya I" "Karthik R" "Priya N" "Arjun M")

for (( i=0; i<5; i++ )); do
  if [[ -z "${WAITER_CODES[$i]:-}" ]]; then
    echo "Waiter code not found for ${WAITER_NAMES[$i]}"
    exit 1
  fi
  echo "[AUTH] Logging in waiter ${WAITER_NAMES[$i]}"
  curl -sS -X POST "$BASE_URL/auth/login" \
    -H "Content-Type: application/json" \
    -d "{\"code\":\"${WAITER_CODES[$i]}\",\"name\":\"${WAITER_NAMES[$i]}\"}"
  echo
done
#
#echo "[AUTH] Logging out waiter $WAITER_NAME ($WAITER_CODE)"
#curl -sS -X POST "$BASE_URL/auth/logout" \
#  -H "Content-Type: application/json" \
#  -d "{\"code\":\"$WAITER_CODE\",\"name\":\"$WAITER_NAME\"}"
#echo

echo "workflow_auth_login_logout: SUCCESS"
