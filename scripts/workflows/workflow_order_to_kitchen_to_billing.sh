#!/usr/bin/env bash
set -euo pipefail

BASE_URL="${BASE_URL:-http://localhost:8080}"
WAITER_NAME="Ananya I"
CHEF_NAME="Meenakshi S"

get_emp_code_by_name() {
  local name="$1"
  local prefix="$2"
  curl -sS "$BASE_URL/manager/employee/" | python3 -c '
import sys, json
name = sys.argv[1]
prefix = sys.argv[2]
for e in json.load(sys.stdin):
    code = e.get("empCode", "")
    if e.get("empName") == name and code.startswith(prefix):
        print(code)
        break
' "$name" "$prefix"
}

WAITER_CODE=$(get_emp_code_by_name "$WAITER_NAME" "W")
CHEF_CODE=$(get_emp_code_by_name "$CHEF_NAME" "C")

if [[ -z "${WAITER_CODE:-}" || -z "${CHEF_CODE:-}" ]]; then
  echo "Could not resolve waiter/chef codes"
  exit 1
fi

TABLE_NO=$(curl -sS "$BASE_URL/$WAITER_CODE/fetchTables" | python3 -c '
import sys, json
arr=json.load(sys.stdin)
print(arr[0]["tableNo"] if arr else "")
')

if [[ -z "${TABLE_NO:-}" ]]; then
  echo "No table assigned to waiter"
  exit 1
fi

echo "[3] Create bill on table $TABLE_NO"
BILL_NO=$(curl -sS -X POST "$BASE_URL/$WAITER_CODE/bill/create" -H "Content-Type: application/json" \
  -d "{\"tableNo\":\"$TABLE_NO\",\"persons\":\"2\"}" | tr -d '"')

echo "Bill created: $BILL_NO"

FOOD1=$(curl -sS "$BASE_URL/$WAITER_CODE/search?keyword=Masala%20Dosa" | python3 -c '
import sys, json
arr=json.load(sys.stdin)
print(arr[0]["foodCode"] if arr else "")
')

FOOD2=$(curl -sS "$BASE_URL/$WAITER_CODE/search?keyword=Idli" | python3 -c '
import sys, json
arr=json.load(sys.stdin)
print(arr[0]["foodCode"] if arr else "")
')

if [[ -z "${FOOD1:-}" || -z "${FOOD2:-}" ]]; then
  echo "Could not resolve food codes from search"
  exit 1
fi

echo "[4] Create orders ($FOOD1, $FOOD2)"
curl -sS -X POST "$BASE_URL/$WAITER_CODE/order/$BILL_NO" -H "Content-Type: application/json" \
  -d "{\"foodCode\":\"$FOOD1\",\"quantity\":1,\"status\":\"Ordered\"}" >/dev/null

curl -sS -X POST "$BASE_URL/$WAITER_CODE/order/$BILL_NO" -H "Content-Type: application/json" \
  -d "{\"foodCode\":\"$FOOD2\",\"quantity\":2,\"status\":\"Ordered\"}" >/dev/null

echo "[5] Kitchen prepares orders"
curl -sS -X POST "$BASE_URL/chef/$CHEF_CODE" -H "Content-Type: application/json" \
  -d "{\"foodCode\":\"$FOOD1\",\"billno\":\"$BILL_NO\"}" >/dev/null
curl -sS -X POST "$BASE_URL/chef/$CHEF_CODE" -H "Content-Type: application/json" \
  -d "{\"foodCode\":\"$FOOD2\",\"billno\":\"$BILL_NO\"}" >/dev/null

# async prepare delay is quantity * 2 sec; wait enough
echo "Waiting for kitchen prep..."
sleep 8

echo "[6] Waiter marks served"
curl -sS -X POST "$BASE_URL/$WAITER_CODE/order/$BILL_NO/serve" -H "Content-Type: application/json" \
  -d "{\"foodCode\":\"$FOOD1\"}"
echo
curl -sS -X POST "$BASE_URL/$WAITER_CODE/order/$BILL_NO/serve" -H "Content-Type: application/json" \
  -d "{\"foodCode\":\"$FOOD2\"}"
echo

echo "[7] Close bill"
curl -sS -X POST "$BASE_URL/$WAITER_CODE/bill/close" -H "Content-Type: application/json" \
  -d "{\"billNo\":\"$BILL_NO\"}"
echo


echo "workflow_order_to_kitchen_to_billing: SUCCESS"
