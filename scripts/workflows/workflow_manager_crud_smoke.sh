#!/usr/bin/env bash
set -euo pipefail

BASE_URL="${BASE_URL:-http://localhost:8080}"
TS="$(date +%s)"

echo "[EMPLOYEE] Add -> Get -> Update -> Delete"
EMP_CODE=$(curl -sS -X POST "$BASE_URL/manager/employee" -H "Content-Type: application/json" -d "{
  \"firstName\": \"Test$TS\",
  \"lastName\": \"Waiter\",
  \"date\": \"01/01/2001\",
  \"role\": \"waiter\",
  \"phone\": \"9777700000\",
  \"employmentType\": \"part-time\",
  \"salary\": 17000
}" | tr -d '"')
echo "Created employee code: $EMP_CODE"

curl -sS "$BASE_URL/manager/employee/$EMP_CODE"
echo

curl -sS -X PUT "$BASE_URL/manager/employee/$EMP_CODE" -H "Content-Type: application/json" -d '{
  "firstName": "Test",
  "lastName": "Waiter Updated",
  "date": "01/01/2001",
  "role": "waiter",
  "phone": "9777700001",
  "employmentType": "full-time",
  "salary": 19000
}'
echo

curl -sS -X DELETE "$BASE_URL/manager/employee/$EMP_CODE"
echo

echo "[FOOD] Add -> Get -> Update -> Delete"
FOOD_JSON=$(curl -sS -X POST "$BASE_URL/manager/food" -H "Content-Type: application/json" -d "{
  \"foodName\": \"Test Dish $TS\",
  \"category\": \"south\",
  \"mealType\": [\"snacks\"],
  \"price\": 123,
  \"veg\": true
}")
FOOD_CODE=$(echo "$FOOD_JSON" | python3 -c 'import sys,json; print(json.load(sys.stdin).get("foodCode",""))')
echo "Created food code: $FOOD_CODE"

curl -sS "$BASE_URL/manager/food/$FOOD_CODE"
echo

curl -sS -X PUT "$BASE_URL/manager/food/$FOOD_CODE" -H "Content-Type: application/json" -d '{
  "foodName": "Test Dish Updated",
  "category": "south",
  "mealType": ["snacks"],
  "price": 150,
  "veg": true
}'
echo

curl -sS -X DELETE "$BASE_URL/manager/food/$FOOD_CODE"
echo

echo "[TABLE] Add -> Update -> Delete"
TABLE_JSON=$(curl -sS -X POST "$BASE_URL/manager/table" -H "Content-Type: application/json" -d '{"noOfSeats": 3}')
TABLE_NO=$(echo "$TABLE_JSON" | python3 -c 'import sys,json; print(json.load(sys.stdin).get("tableNo",""))')
echo "Created table no: $TABLE_NO"

curl -sS -X PUT "$BASE_URL/manager/table/$TABLE_NO" -H "Content-Type: application/json" -d '{"seats":5}'
echo

curl -sS -X DELETE "$BASE_URL/manager/table/$TABLE_NO"
echo

echo "workflow_manager_crud_smoke: SUCCESS"
