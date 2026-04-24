# Redis Keys Used in This Project

This file documents every Redis key/pattern currently used in the codebase, with datatype and structure.

---

## 1) Order Keys

### Key pattern
- `orders:bill:{billNo}:{foodCode}`

### Redis datatype
- **HASH**

### Fields
- `quantity` (stringified integer)
- `status` (string; e.g., `Ordered`, `Prepared`, `Served`, `Deleted`)
- `chefCode` (string, optional; set after chef assignment)

### Used by
- `service/OrderService.java`
- `service/ChefOrderService.java`
- `kitchen/kitchenService.java`
- `service/waiterService.java`

---

## 2) Bill Keys

### Key pattern
- `bill:{billNo}`

### Redis datatype
- **HASH**

### Fields
- `billNo` (stringified long)
- `waiterCode` (string)
- `tableNo` (stringified int)
- `NoOfPersons` (stringified int)

### Used by
- `service/billService.java`

---

## 3) Food Availability Key

### Key
- `foodAvailability`

### Redis datatype
- **HASH**

### Hash entry pattern
- field = `{foodCode}`
- value = available quantity (stringified integer)

### Used by
- `service/foodAvailabilityService.java`
- `service/foodService.java`

---

## 4) Table Status Key

### Key
- `tableStatus`

### Redis datatype
- **HASH**

### Hash entry pattern
- field = `{tableNo}`
- value = free seats (stringified integer)

### Used by
- `service/tableStatusService.java`
- `CustomerAssignment/CustomerAssignmentService.java`

---

## 5) Waiter Attendance Keys

### Key pattern
- `waiter:{waiterCode}`

### Redis datatype
- **HASH**

### Fields
- `present` (`true` / `false` as string)
- `LastLoginTime` (ISO datetime string)

### Used by
- `waiter/waiterAttendance.java`
- `Login/loginService.java`

---

## 6) Waiter Table Assignment Keys

### Key pattern
- `waiterAssigned:{waiterCode}`

### Redis datatype
- **LIST**

### Values
- table numbers (as list elements; stored via `rightPushAll`)

### Used by
- `waiter/tableAssignment.java`
- `service/waiterService.java`

---

## 7) Waiting List Key

### Key
- `waitingList`

### Redis datatype
- **LIST**

### Values
- JSON string for each waiting-list entry (`WaitingList` object serialized)

### Used by
- `waitingList/WaitingListService.java`
- `CustomerAssignment/CustomerAssignmentService.java`
- `waiter/Subscriber.java`

---

## 8) Bill Number Counter

### Key
- `billNo`

### Redis datatype
- **STRING** (numeric counter via INCR)

### Used by
- `helpers/BillNoIncrementingService.java`
- `service/billService.java`
- `admin/scheduledController.java` (reset)

---

## 9) Food Code Counter (currently not in active flow)

### Key
- `FoodCodeInc`

### Redis datatype
- **STRING** (intended numeric counter)

### Status
- Present in helper class but currently not used by active food creation flow.

### Used by
- `helpers/foodCodeIncrementingService.java`

---

## 10) Redis Pub/Sub Channels

> These are **channels**, not keys.

### Channels
- `order-updates`
- `order-deletes`
- `table-updates`

### Message payloads
- `order-updates` / `order-deletes`: order key string (e.g., `orders:bill:12:S101`)
- `table-updates`: table number as string

### Used by
- `configuration/RedisConfig.java`
- `waiter/OrderPublisher.java`
- `waiter/tablePublisher.java`
- `waiter/Subscriber.java`

---

## 11) Key scans used in code

These wildcard scans are currently used:
- `orders:bill:{billNo}:*`
- `orders:bill:*`
- `waiter:*`

They are functionally correct for your current size, but keep in mind `KEYS` can be expensive at scale.
