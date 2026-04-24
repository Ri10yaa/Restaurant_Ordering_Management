#!/usr/bin/env bash
set -euo pipefail

BASE_URL="${BASE_URL:-http://localhost:8080}"

echo "Seeding tables collection (10 tables)..."

# 1
curl -sS -X POST "$BASE_URL/manager/table" -H "Content-Type: application/json" -d '{"noOfSeats": 2}'
echo
# 2
curl -sS -X POST "$BASE_URL/manager/table" -H "Content-Type: application/json" -d '{"noOfSeats": 2}'
echo
# 3
curl -sS -X POST "$BASE_URL/manager/table" -H "Content-Type: application/json" -d '{"noOfSeats": 4}'
echo
# 4
curl -sS -X POST "$BASE_URL/manager/table" -H "Content-Type: application/json" -d '{"noOfSeats": 4}'
echo
# 5
curl -sS -X POST "$BASE_URL/manager/table" -H "Content-Type: application/json" -d '{"noOfSeats": 4}'
echo
# 6
curl -sS -X POST "$BASE_URL/manager/table" -H "Content-Type: application/json" -d '{"noOfSeats": 6}'
echo
# 7
curl -sS -X POST "$BASE_URL/manager/table" -H "Content-Type: application/json" -d '{"noOfSeats": 6}'
echo
# 8
curl -sS -X POST "$BASE_URL/manager/table" -H "Content-Type: application/json" -d '{"noOfSeats": 8}'
echo
# 9
curl -sS -X POST "$BASE_URL/manager/table" -H "Content-Type: application/json" -d '{"noOfSeats": 8}'
echo
# 10
curl -sS -X POST "$BASE_URL/manager/table" -H "Content-Type: application/json" -d '{"noOfSeats": 10}'
echo

echo "Table seeding completed."
