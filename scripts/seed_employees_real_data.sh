#!/usr/bin/env bash
set -euo pipefail

BASE_URL="${BASE_URL:-http://localhost:8080}"

echo "Seeding employee collection (4 waiters, 4 chefs, 2 managers)..."

# 1) Waiter - Ananya Iyer
curl -sS -X POST "$BASE_URL/manager/employee" -H "Content-Type: application/json" -d '{
  "firstName": "Ananya",
  "lastName": "I",
  "date": "14/03/1998",
  "role": "waiter",
  "phone": "9840012345",
  "employmentType": "full-time",
  "salary": 22000
}'
echo

# 2) Waiter - Karthik Raman
curl -sS -X POST "$BASE_URL/manager/employee" -H "Content-Type: application/json" -d '{
  "firstName": "Karthik",
  "lastName": "R",
  "date": "22/07/1997",
  "role": "waiter",
  "phone": "9840023456",
  "employmentType": "full-time",
  "salary": 23000
}'
echo

# 3) Waiter - Priya Nair
curl -sS -X POST "$BASE_URL/manager/employee" -H "Content-Type: application/json" -d '{
  "firstName": "Priya",
  "lastName": "N",
  "date": "05/11/1999",
  "role": "waiter",
  "phone": "9840034567",
  "employmentType": "part-time",
  "salary": 18000
}'
echo

# 4) Waiter - Arjun Menon
curl -sS -X POST "$BASE_URL/manager/employee" -H "Content-Type: application/json" -d '{
  "firstName": "Arjun",
  "lastName": "M",
  "date": "19/01/1996",
  "role": "waiter",
  "phone": "9840045678",
  "employmentType": "full-time",
  "salary": 24000
}'
echo

# 5) Chef - Raghav Sharma (North)
curl -sS -X POST "$BASE_URL/manager/employee" -H "Content-Type: application/json" -d '{
  "firstName": "Raghav",
  "lastName": "S",
  "date": "10/08/1992",
  "role": "chef",
  "spec": ["North"],
  "phone": "9840056789",
  "employmentType": "full-time",
  "salary": 42000
}'
echo

# 6) Chef - Meenakshi Subramaniam (South)
curl -sS -X POST "$BASE_URL/manager/employee" -H "Content-Type: application/json" -d '{
  "firstName": "Meenakshi",
  "lastName": "S",
  "date": "27/02/1990",
  "role": "chef",
  "spec": ["South"],
  "phone": "9840067890",
  "employmentType": "full-time",
  "salary": 44000
}'
echo

# 7) Chef - Li Wei (Chinese)
curl -sS -X POST "$BASE_URL/manager/employee" -H "Content-Type: application/json" -d '{
  "firstName": "Li",
  "lastName": "Wei",
  "date": "12/12/1991",
  "role": "chef",
  "spec": ["Chinese"],
  "phone": "9840078901",
  "employmentType": "full-time",
  "salary": 45000
}'
echo

# 8) Chef - Farhan Ali (North, South)
curl -sS -X POST "$BASE_URL/manager/employee" -H "Content-Type: application/json" -d '{
  "firstName": "Farhan",
  "lastName": "A",
  "date": "03/05/1989",
  "role": "chef",
  "spec": ["North", "South"],
  "phone": "9840089012",
  "employmentType": "full-time",
  "salary": 47000
}'
echo

# 9) Manager - Nandini Rao
curl -sS -X POST "$BASE_URL/manager/employee" -H "Content-Type: application/json" -d '{
  "firstName": "Nandini",
  "lastName": "R",
  "date": "16/04/1987",
  "role": "manager",
  "spec": ["Operations"],
  "dept": "Operations",
  "phone": "9840090123",
  "employmentType": "full-time",
  "salary": 65000
}'
echo

# 10) Manager - Vivek Khanna
curl -sS -X POST "$BASE_URL/manager/employee" -H "Content-Type: application/json" -d '{
  "firstName": "Vivek",
  "lastName": "K",
  "date": "29/09/1985",
  "role": "manager",
  "spec": ["Admin"],
  "dept": "Admin",
  "phone": "9840101234",
  "employmentType": "full-time",
  "salary": 70000
}'
echo

echo "Employee seeding completed."
