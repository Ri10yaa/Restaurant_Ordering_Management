#!/usr/bin/env bash
set -euo pipefail

BASE_URL="${BASE_URL:-http://localhost:8080}"

echo "[1] Reset system state (food availability, bill number, table status)"
curl -sS "$BASE_URL/schedule/reset"
echo

echo "[2] Optional sanity: current table assignments"
curl -sS "$BASE_URL/schedule/assignTable"
echo

echo "workflow_system_reset_and_initialize: SUCCESS"
