#!/usr/bin/env bash
set -euo pipefail

BASE_URL="${BASE_URL:-http://localhost:8080}"
WAITER_NAME="Karthik Raman"

get_waiter_code() {
  curl -sS "$BASE_URL/manager/employee/" | python3 -c '
import sys, json
name = "Karthik Raman"
for e in json.load(sys.stdin):
    if e.get("empName") == name and e.get("empCode", "").startswith("W"):
        print(e["empCode"])
        break
'
}

WAITER_CODE=$(get_waiter_code)
if [[ -z "${WAITER_CODE:-}" ]]; then
  echo "Waiter code not found for $WAITER_NAME"
  exit 1
fi

echo "[1] Login waiter"
curl -sS -X POST "$BASE_URL/auth/login" -H "Content-Type: application/json" \
  -d "{\"code\":\"$WAITER_CODE\",\"name\":\"$WAITER_NAME\"}" >/dev/null

echo "[2] Assign tables and pick first"
curl -sS "$BASE_URL/schedule/assignTable" >/dev/null
TABLE_NO=$(curl -sS "$BASE_URL/$WAITER_CODE/fetchTables" | python3 -c '
import sys, json
arr=json.load(sys.stdin)
print(arr[0]["tableNo"] if arr else "")
')

if [[ -z "${TABLE_NO:-}" ]]; then
  echo "No table assigned"
  exit 1
fi

echo "[3] Create bill to occupy table $TABLE_NO"
BILL_NO=$(curl -sS -X POST "$BASE_URL/$WAITER_CODE/bill/create" -H "Content-Type: application/json" \
  -d "{\"tableNo\":\"$TABLE_NO\",\"persons\":\"2\"}" | tr -d '"')
echo "Bill: $BILL_NO"

echo "[4] Add waiting list customer (2 seats)"
UNIQ="$(date +%s)"
CUST_NAME="Rahul Verma $UNIQ"
curl -sS -X POST "$BASE_URL/waiting-list/add" -H "Content-Type: application/json" \
  -d "{\"name\":\"$CUST_NAME\",\"phoneNumber\":\"9898989898\",\"seatsRequired\":2,\"okToSplit\":false}"
echo

COUNT_BEFORE=$(curl -sS "$BASE_URL/waiting-list" | python3 -c 'import sys,json; print(len(json.load(sys.stdin)))')
echo "Waiting list count before freeing table: $COUNT_BEFORE"

echo "[5] Delete bill to free table (triggers table-updates -> waiting-list allocation)"
curl -sS -X DELETE "$BASE_URL/$WAITER_CODE/bill/$BILL_NO"
echo

sleep 2
COUNT_AFTER=$(curl -sS "$BASE_URL/waiting-list" | python3 -c 'import sys,json; print(len(json.load(sys.stdin)))')
echo "Waiting list count after freeing table: $COUNT_AFTER"

if (( COUNT_AFTER < COUNT_BEFORE )); then
  echo "Auto-allocation observed (customer removed from waiting list)."
else
  echo "No decrease observed. Check table status/waiting-list conditions manually."
fi

echo "[6] Logout waiter"
curl -sS -X POST "$BASE_URL/auth/logout" -H "Content-Type: application/json" \
  -d "{\"code\":\"$WAITER_CODE\",\"name\":\"$WAITER_NAME\"}" >/dev/null

echo "workflow_waiting_list_auto_allocation: COMPLETED"
