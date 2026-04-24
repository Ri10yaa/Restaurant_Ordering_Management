#!/usr/bin/env bash
set -euo pipefail

BASE_URL="${BASE_URL:-http://localhost:8080}"

WAITERS=(
  "Ananya Iyer"
  "Karthik Raman"
  "Priya Nair"
  "Arjun Menon"
)

get_waiter_code() {
  local name="$1"
  curl -sS "$BASE_URL/manager/employee/" | python3 -c '
import sys, json
name = sys.argv[1]
for e in json.load(sys.stdin):
    if e.get("empName") == name and e.get("empCode", "").startswith("W"):
        print(e["empCode"])
        break
' "$name"
}

for name in "${WAITERS[@]}"; do
  code=$(get_waiter_code "$name")
  if [[ -z "${code:-}" ]]; then
    echo "Waiter not found: $name"
    exit 1
  fi
  echo "[LOGIN] $name ($code)"
  curl -sS -X POST "$BASE_URL/auth/login" -H "Content-Type: application/json" \
    -d "{\"code\":\"$code\",\"name\":\"$name\"}" >/dev/null
  declare "CODE_${name// /_}=$code"
done

echo "[SCHEDULE] Assigning tables"
curl -sS "$BASE_URL/schedule/assignTable"
echo

echo "[FETCH] Tables by waiter"
for name in "${WAITERS[@]}"; do
  var="CODE_${name// /_}"
  code="${!var}"
  echo "- $name ($code)"
  curl -sS "$BASE_URL/$code/fetchTables"
  echo
  echo

done

for name in "${WAITERS[@]}"; do
  var="CODE_${name// /_}"
  code="${!var}"
  echo "[LOGOUT] $name ($code)"
  curl -sS -X POST "$BASE_URL/auth/logout" -H "Content-Type: application/json" \
    -d "{\"code\":\"$code\",\"name\":\"$name\"}" >/dev/null

done

echo "workflow_waiter_table_assignment_and_fetch: SUCCESS"
