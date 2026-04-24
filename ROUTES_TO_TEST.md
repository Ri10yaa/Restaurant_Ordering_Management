# Restaurant Ordering Management - Routes + cURL

Base URL: `http://localhost:8080`

> Replace sample IDs/codes (`WAB123`, `CXY101`, `1`, etc.) with actual values from your DB.

---

## 1) Auth Component

### POST `/auth/login`
```bash
curl -X POST "http://localhost:8080/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "code": "WAB123",
    "name": "Arun B"
  }'
```

### POST `/auth/logout`
```bash
curl -X POST "http://localhost:8080/auth/logout" \
  -H "Content-Type: application/json" \
  -d '{
    "code": "WAB123",
    "name": "Arun B"
  }'
```

---

## 2) Waiter Component (Base: `/{waiterCode}`)

### GET `/{waiterCode}/fetchTables`
```bash
curl -X GET "http://localhost:8080/WAB123/fetchTables"
```

### POST `/{waiterCode}/bill/create`
```bash
curl -X POST "http://localhost:8080/WAB123/bill/create" \
  -H "Content-Type: application/json" \
  -d '{
    "tableNo": "3",
    "persons": "2"
  }'
```

### GET `/{waiterCode}/bill/{billNo}`
```bash
curl -X GET "http://localhost:8080/WAB123/bill/1"
```

### POST `/{waiterCode}/bill/close`
```bash
curl -X POST "http://localhost:8080/WAB123/bill/close" \
  -H "Content-Type: application/json" \
  -d '{
    "billNo": "1"
  }'
```

### DELETE `/{waiterCode}/bill/{billNo}`
```bash
curl -X DELETE "http://localhost:8080/WAB123/bill/1"
```

### POST `/{waiterCode}/order/{billNo}`
```bash
curl -X POST "http://localhost:8080/WAB123/order/1" \
  -H "Content-Type: application/json" \
  -d '{
    "foodCode": "S101",
    "quantity": 2,
    "status": "Ordered"
  }'
```

### PUT `/{waiterCode}/order/{billNo}`
```bash
curl -X PUT "http://localhost:8080/WAB123/order/1" \
  -H "Content-Type: application/json" \
  -d '{
    "foodCode": "S101",
    "quantity": 3,
    "status": "Ordered"
  }'
```

### POST `/{waiterCode}/order/{billNo}/serve`
```bash
curl -X POST "http://localhost:8080/WAB123/order/1/serve" \
  -H "Content-Type: application/json" \
  -d '{
    "foodCode": "S101"
  }'
```

### GET `/{waiterCode}/order/{billNo}`
```bash
curl -X GET "http://localhost:8080/WAB123/order/1"
```

### DELETE `/{waiterCode}/order/{billNo}`
```bash
curl -X DELETE "http://localhost:8080/WAB123/order/1" \
  -H "Content-Type: application/json" \
  -d '{
    "foodCode": "S101"
  }'
```

### GET `/{waiterCode}/search?keyword=...`
```bash
curl -X GET "http://localhost:8080/WAB123/search?keyword=dosa"
```

---

## 3) Kitchen Component (Base: `/chef`)

### POST `/chef/{empCode}`
```bash
curl -X POST "http://localhost:8080/chef/CXY101" \
  -H "Content-Type: application/json" \
  -d '{
    "foodCode": "S101",
    "billno": "1"
  }'
```

### GET `/chef/{empCode}`
```bash
curl -X GET "http://localhost:8080/chef/CXY101"
```

---

## 4) Waiting List Component (Base: `/waiting-list`)

### POST `/waiting-list/add`
```bash
curl -X POST "http://localhost:8080/waiting-list/add" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Karthik",
    "phoneNumber": "9876543210",
    "seatsRequired": 4,
    "okToSplit": true
  }'
```

### GET `/waiting-list`
```bash
curl -X GET "http://localhost:8080/waiting-list"
```

---

## 5) Manager - Employee Component (Base: `/manager/employee`)

### GET `/manager/employee/`
```bash
curl -X GET "http://localhost:8080/manager/employee/"
```

### POST `/manager/employee`
```bash
curl -X POST "http://localhost:8080/manager/employee" \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Ravi",
    "lastName": "K",
    "date": "01/01/2000",
    "role": "waiter",
    "phone": "9999999999",
    "employmentType": "full-time",
    "salary": 18000
  }'
```

### GET `/manager/employee/{code}`
```bash
curl -X GET "http://localhost:8080/manager/employee/WRK1123"
```

### PUT `/manager/employee/{code}`
```bash
curl -X PUT "http://localhost:8080/manager/employee/WRK1123" \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Ravi",
    "lastName": "Kumar",
    "date": "01/01/2000",
    "role": "waiter",
    "phone": "9999999999",
    "employmentType": "part-time",
    "salary": 20000
  }'
```

### DELETE `/manager/employee/{code}`
```bash
curl -X DELETE "http://localhost:8080/manager/employee/WRK1123"
```

---

## 6) Manager - Food Component (Base: `/manager/food`)

### POST `/manager/food`
```bash
curl -X POST "http://localhost:8080/manager/food" \
  -H "Content-Type: application/json" \
  -d '{
    "foodName": "Masala Dosa",
    "category": "south",
    "mealType": ["breakfast"],
    "price": 90,
    "veg": true
  }'
```

### GET `/manager/food/{code}`
```bash
curl -X GET "http://localhost:8080/manager/food/S101"
```

### PUT `/manager/food/{code}`
```bash
curl -X PUT "http://localhost:8080/manager/food/S101" \
  -H "Content-Type: application/json" \
  -d '{
    "foodName": "Ghee Masala Dosa",
    "category": "south",
    "mealType": ["breakfast"],
    "price": 110,
    "veg": true
  }'
```

### DELETE `/manager/food/{code}`
```bash
curl -X DELETE "http://localhost:8080/manager/food/S101"
```

---

## 7) Manager - Table Component (Base: `/manager/table`)

### POST `/manager/table`
```bash
curl -X POST "http://localhost:8080/manager/table" \
  -H "Content-Type: application/json" \
  -d '{
    "noOfSeats": 4
  }'
```

### GET `/manager/table`
```bash
curl -X GET "http://localhost:8080/manager/table"
```

### PUT `/manager/table/{tableNo}`
```bash
curl -X PUT "http://localhost:8080/manager/table/3" \
  -H "Content-Type: application/json" \
  -d '{
    "seats": 6
  }'
```

### DELETE `/manager/table/{tableNo}`
```bash
curl -X DELETE "http://localhost:8080/manager/table/3"
```

---

## 8) Operational Component (Base: `/schedule`)

### GET `/schedule/assignTable`
```bash
curl -X GET "http://localhost:8080/schedule/assignTable"
```

### GET `/schedule/reset`
```bash
curl -X GET "http://localhost:8080/schedule/reset"
```

### GET `/schedule/flush`
```bash
curl -X GET "http://localhost:8080/schedule/flush"
```

---

## Common Error Validation cURLs

### 400 Bad Request (missing required field)
```bash
curl -X POST "http://localhost:8080/WAB123/order/1/serve" \
  -H "Content-Type: application/json" \
  -d '{}'
```

### 404 Not Found (invalid food code)
```bash
curl -X GET "http://localhost:8080/manager/food/INVALID_CODE"
```

### 409 Conflict (close bill with non-served items)
```bash
curl -X POST "http://localhost:8080/WAB123/bill/close" \
  -H "Content-Type: application/json" \
  -d '{"billNo":"1"}'
```
