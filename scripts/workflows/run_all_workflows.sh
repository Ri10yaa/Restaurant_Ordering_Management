#!/usr/bin/env bash
set -euo pipefail

DIR="$(cd "$(dirname "$0")" && pwd)"

"$DIR/workflow_system_reset_and_initialize.sh"
"$DIR/workflow_auth_login_logout.sh"
"$DIR/workflow_waiter_table_assignment_and_fetch.sh"
"$DIR/workflow_order_to_kitchen_to_billing.sh"
"$DIR/workflow_waiting_list_auto_allocation.sh"
"$DIR/workflow_manager_crud_smoke.sh"

echo "All workflows executed."
