#!/usr/bin/env bash
set -euo pipefail

BASE_URL="${BASE_URL:-http://localhost:8080}"

echo "Seeding food collection (10 diverse items)..."

# 1) Idli Sambar (South, breakfast)
curl -sS -X POST "$BASE_URL/manager/food" -H "Content-Type: application/json" -d '{
  "foodName": "Idli",
  "category": "south",
  "mealType": ["breakfast", "dinner"],
  "price": 60,
  "veg": true
}'
echo

# 2) Masala Dosa (South, breakfast)
curl -sS -X POST "$BASE_URL/manager/food" -H "Content-Type: application/json" -d '{
  "foodName": "Masala Dosa",
  "category": "south",
  "mealType": ["breakfast", "dinner"],
  "price": 90,
  "veg": true
}'
echo

# 3) Veg Meals (South, lunch)
curl -sS -X POST "$BASE_URL/manager/food" -H "Content-Type: application/json" -d '{
  "foodName": "South Veg Meals",
  "category": "south",
  "mealType": ["lunch"],
  "price": 180,
  "veg": true
}'
echo

# 4) Butter Naan (North, dinner)
curl -sS -X POST "$BASE_URL/manager/food" -H "Content-Type: application/json" -d '{
  "foodName": "Butter Naan",
  "category": "north",
  "mealType": ["dinner"],
  "price": 45,
  "veg": true
}'
echo

# 5) Paneer Butter Masala (North, lunch+dinner)
curl -sS -X POST "$BASE_URL/manager/food" -H "Content-Type: application/json" -d '{
  "foodName": "Paneer Butter Masala",
  "category": "north",
  "mealType": ["lunch", "dinner"],
  "price": 260,
  "veg": true
}'
echo

# 6) Chicken Biryani (North, lunch+dinner)
curl -sS -X POST "$BASE_URL/manager/food" -H "Content-Type: application/json" -d '{
  "foodName": "Chicken Biryani",
  "category": "north",
  "mealType": ["lunch", "dinner"],
  "price": 320,
  "veg": false
}'
echo

# 7) Veg Hakka Noodles (Chinese, lunch+dinner)
curl -sS -X POST "$BASE_URL/manager/food" -H "Content-Type: application/json" -d '{
  "foodName": "Veg Noodles",
  "category": "chinese",
  "mealType": ["lunch", "dinner"],
  "price": 190,
  "veg": true
}'
echo

# 8) Chicken Manchurian (Chinese, dinner)
curl -sS -X POST "$BASE_URL/manager/food" -H "Content-Type: application/json" -d '{
  "foodName": "Chicken Manchurian",
  "category": "chinese",
  "mealType": ["dinner"],
  "price": 240,
  "veg": false
}'
echo

# 9) Filter Coffee (Other, snacks)
curl -sS -X POST "$BASE_URL/manager/food" -H "Content-Type: application/json" -d '{
  "foodName": "Filter Coffee",
  "category": "other",
  "mealType": ["snacks"],
  "price": 40,
  "veg": true
}'
echo

# 10) Brownie with Ice Cream (Other, snacks)
curl -sS -X POST "$BASE_URL/manager/food" -H "Content-Type: application/json" -d '{
  "foodName": "Brownie with Ice Cream",
  "category": "other",
  "mealType": ["snacks"],
  "price": 160,
  "veg": true
}'
echo

echo "Food seeding completed."
