# Insurance Claim Processing System

Microservices project with an API Gateway, Eureka, Policy, Claim, and Settlement services.

This README explains **how to call every endpoint in Postman**, including **headers**, **request body**, and **links for records already in the database**.

---

## Base URL

Use the API Gateway for all Postman calls:

```text
http://localhost:8080
```

| Service | URL |
| --- | --- |
| API Gateway | `http://localhost:8080` |
| Policy service | `http://localhost:8081` |
| Claim service | `http://localhost:8082` |
| Settlement service | `http://localhost:8083` |
| Eureka dashboard | `http://localhost:8761` |

If you call `8081`, `8082`, or `8083` directly, you do **not** need a JWT.  
If you call `8080`, you **must** send a JWT on every request except login.

---

## Postman setup

1. Create a request.
2. Set method (`GET` or `POST`) and paste the URL.
3. For **POST**, go to **Body → raw → JSON**.
4. For every request except login, go to **Authorization → Bearer Token** and paste the token from login.

Headers you should have:

```text
Authorization: Bearer <paste_token_here>
Content-Type: application/json
```

Login users:

```text
username: admin
password: password123

username: user1
password: password123
```

Important: never keep `{policyId}` in the URL. Use a real number, for example `37`.

---

## Suggested order in Postman

1. Login
2. Get a policy (use an ID from the tables below)
3. Get claims for that policy
4. Get the settlement for a claim
5. Optional: create a new policy, then submit a new claim

---

## 1. Login

Get a token. No Authorization header.

**Method:** `POST`  
**URL:**

```text
http://localhost:8080/api/v1/auth/login
```

**Body:**

```json
{
  "username": "admin",
  "password": "password123"
}
```

**Example response:**

```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "tokenType": "Bearer",
  "username": "admin"
}
```

Copy `token`. Use it as the Bearer token for all endpoints below.

---

## 2. Policy endpoints

### 2.1 Create a policy

Adds a new row in `policy_db`.

**Method:** `POST`  
**URL:**

```text
http://localhost:8080/api/v1/policies
```

**Body:**

```json
{
  "userId": "alice",
  "coverageAmount": 100000
}
```

| Field | Type | Required | Meaning |
| --- | --- | --- | --- |
| `userId` | string | yes | Who owns the policy |
| `coverageAmount` | number | yes | Total coverage. Must be greater than 0 |

The service fills in:

- `remainingCoverage` = same as `coverageAmount`
- `policyStatus` = `ACTIVE`

Save `policyId` from the response. You need that ID for GET and for claims.

**Expected status:** `201 Created`

---

### 2.2 Get one policy by ID

No body.

**Method:** `GET`  
**URL pattern:**

```text
http://localhost:8080/api/v1/policies/{policyId}
```

**Ready URLs for records in the database:**

```text
http://localhost:8080/api/v1/policies/1
http://localhost:8080/api/v1/policies/34
http://localhost:8080/api/v1/policies/35
http://localhost:8080/api/v1/policies/36
http://localhost:8080/api/v1/policies/37
http://localhost:8080/api/v1/policies/38
http://localhost:8080/api/v1/policies/39
http://localhost:8080/api/v1/policies/40
http://localhost:8080/api/v1/policies/41
```

| policyId | userId | coverageAmount | remainingCoverage | policyStatus |
| --- | --- | --- | --- | --- |
| 1 | USER_POSTMAN_1 | 100000.00 | 100000.00 | ACTIVE |
| 34 | user1 | 100000.00 | 100000.00 | ACTIVE |
| 35 | user1 | 100000.00 | 100000.00 | ACTIVE |
| 36 | user1 | 100000.00 | 100000.00 | ACTIVE |
| 37 | alice | 100000.00 | 70000.00 | ACTIVE |
| 38 | bob | 50000.00 | 50000.00 | ACTIVE |
| 39 | carol | 25000.00 | 25000.00 | ACTIVE |
| 40 | dave | 80000.00 | 75000.00 | ACTIVE |
| 41 | alice | 100000.00 | 100000.00 | ACTIVE |

Best demo policy: **37** (alice, has claims and settlements).

---

### 2.3 Get all policies for a user

No body.

**Method:** `GET`  
**URL pattern:**

```text
http://localhost:8080/api/v1/policies/user/{userId}
```

**Ready URLs:**

```text
http://localhost:8080/api/v1/policies/user/USER_POSTMAN_1
http://localhost:8080/api/v1/policies/user/user1
http://localhost:8080/api/v1/policies/user/alice
http://localhost:8080/api/v1/policies/user/bob
http://localhost:8080/api/v1/policies/user/carol
http://localhost:8080/api/v1/policies/user/dave
```

---

### 2.4 Validate a policy for a claim amount

No body. Amount goes in the query string.

**Method:** `GET`  
**URL pattern:**

```text
http://localhost:8080/api/v1/policies/{policyId}/validate?claimAmount=5000
```

**Ready URLs:**

```text
http://localhost:8080/api/v1/policies/37/validate?claimAmount=5000
http://localhost:8080/api/v1/policies/39/validate?claimAmount=30000
```

The second URL should fail validation because carol’s remaining coverage is only `25000`.

---

### 2.5 Deduct remaining coverage

Settlement calls this after a successful payout. You can also call it from Postman.

**Method:** `POST`  
**URL pattern:**

```text
http://localhost:8080/api/v1/policies/{policyId}/deduct
```

**Example URL:**

```text
http://localhost:8080/api/v1/policies/37/deduct
```

**Body:**

```json
{
  "amount": 5000
}
```

---

## 3. Claim endpoints

### 3.1 Submit a claim

Checks the policy. If valid, the claim is `APPROVED` and a settlement is created.

**Method:** `POST`  
**URL:**

```text
http://localhost:8080/api/v1/claims
```

**Body (normal claim):**

```json
{
  "policyId": 40,
  "claimAmount": 5000
}
```

**Body (simulate failed payment):**

```json
{
  "policyId": 40,
  "claimAmount": 5000,
  "simulatePaymentFailure": true
}
```

**Body (this should be rejected — amount bigger than remaining coverage on policy 39):**

```json
{
  "policyId": 39,
  "claimAmount": 30000
}
```

| Field | Type | Required | Meaning |
| --- | --- | --- | --- |
| `policyId` | number | yes | Must exist in `policy_db` |
| `claimAmount` | number | yes | Must be greater than 0 |
| `simulatePaymentFailure` | boolean | no | Claim can still be APPROVED, but settlement payment becomes FAILED |

**Expected status:**

- `201 Created` if APPROVED
- `200 OK` if REJECTED

Save `claim.claimId` from the response.

---

### 3.2 Get one claim by ID

No body.

**Method:** `GET`  
**URL pattern:**

```text
http://localhost:8080/api/v1/claims/{claimId}
```

**Ready URLs for records in the database:**

```text
http://localhost:8080/api/v1/claims/34
http://localhost:8080/api/v1/claims/35
http://localhost:8080/api/v1/claims/36
http://localhost:8080/api/v1/claims/37
http://localhost:8080/api/v1/claims/38
```

| claimId | policyId | claimAmount | status | remarks |
| --- | --- | --- | --- | --- |
| 34 | 37 | 15000.00 | APPROVED | Claim validated and approved |
| 35 | 37 | 10000.00 | APPROVED | Claim validated and approved |
| 36 | 38 | 5000.00 | APPROVED | Claim validated and approved |
| 37 | 39 | 30000.00 | REJECTED | Claim amount exceeds remaining coverage |
| 38 | 40 | 5000.00 | APPROVED | Claim validated and approved |

---

### 3.3 Get all claims for a policy

No body.

**Method:** `GET`  
**URL pattern:**

```text
http://localhost:8080/api/v1/claims/policy/{policyId}
```

**Ready URLs:**

```text
http://localhost:8080/api/v1/claims/policy/37
http://localhost:8080/api/v1/claims/policy/38
http://localhost:8080/api/v1/claims/policy/39
http://localhost:8080/api/v1/claims/policy/40
```

Policies `1`, `34`, `35`, `36`, and `41` currently have **no claims**.

---

## 4. Settlement endpoints

A settlement is usually created automatically when a claim is approved.  
If you POST again with the same `claimId`, the existing settlement is returned.

### 4.1 Process a settlement

**Method:** `POST`  
**URL:**

```text
http://localhost:8080/api/v1/settlements
```

**Body (success path):**

```json
{
  "claimId": 34,
  "policyId": 37,
  "approvedAmount": 15000
}
```

**Body (failed payment):**

```json
{
  "claimId": 34,
  "policyId": 37,
  "approvedAmount": 15000,
  "simulateFailure": true
}
```

| Field | Type | Required | Meaning |
| --- | --- | --- | --- |
| `claimId` | number | yes | One settlement per claim |
| `policyId` | number | yes | Policy used for coverage deduction |
| `approvedAmount` | number | yes | Must be greater than 0 |
| `simulateFailure` | boolean | no | If `true`, `paymentStatus` is FAILED and coverage is not deducted |

**Expected status:** `201 Created`

Note: claim `34` already has settlement `1`. Posting the body above will return that existing settlement instead of creating a duplicate.

---

### 4.2 Get one settlement by ID

No body.

**Method:** `GET`  
**URL pattern:**

```text
http://localhost:8080/api/v1/settlements/{settlementId}
```

**Ready URLs for records in the database:**

```text
http://localhost:8080/api/v1/settlements/1
http://localhost:8080/api/v1/settlements/2
http://localhost:8080/api/v1/settlements/3
http://localhost:8080/api/v1/settlements/4
```

| settlementId | claimId | policyId | approvedAmount | paymentStatus |
| --- | --- | --- | --- | --- |
| 1 | 34 | 37 | 15000.00 | SUCCESS |
| 2 | 35 | 37 | 10000.00 | SUCCESS |
| 3 | 36 | 38 | 5000.00 | FAILED |
| 4 | 38 | 40 | 5000.00 | SUCCESS |

---

### 4.3 Get settlement by claim ID

No body.

**Method:** `GET`  
**URL pattern:**

```text
http://localhost:8080/api/v1/settlements/claim/{claimId}
```

**Ready URLs:**

```text
http://localhost:8080/api/v1/settlements/claim/34
http://localhost:8080/api/v1/settlements/claim/35
http://localhost:8080/api/v1/settlements/claim/36
http://localhost:8080/api/v1/settlements/claim/38
```

Do **not** use claim `37` here. That claim was **REJECTED**, so there is no settlement.

---

## 5. How the database records are linked

```text
Policy 37  alice   coverage 100000   remaining 70000
  -> Claim 34   15000  APPROVED  ->  Settlement 1  SUCCESS
  -> Claim 35   10000  APPROVED  ->  Settlement 2  SUCCESS

Policy 38  bob     coverage  50000   remaining 50000
  -> Claim 36    5000  APPROVED  ->  Settlement 3  FAILED
     (payment failed, so remaining coverage was not reduced)

Policy 39  carol   coverage  25000   remaining 25000
  -> Claim 37   30000  REJECTED  ->  no settlement

Policy 40  dave    coverage  80000   remaining 75000
  -> Claim 38    5000  APPROVED  ->  Settlement 4  SUCCESS

Policies 1, 34, 35, 36, 41 have no claims yet.
```

---

## 6. Full example: create new data yourself

### Step A — login

```text
POST http://localhost:8080/api/v1/auth/login
```

```json
{
  "username": "admin",
  "password": "password123"
}
```

### Step B — create policy

```text
POST http://localhost:8080/api/v1/policies
```

```json
{
  "userId": "alice",
  "coverageAmount": 100000
}
```

Copy `policyId` from the response. Example: `42`.

### Step C — submit claim using that policy ID

```text
POST http://localhost:8080/api/v1/claims
```

```json
{
  "policyId": 42,
  "claimAmount": 5000
}
```

Copy `claim.claimId` and `settlement.settlementId`.

### Step D — read the new records

Replace the numbers with the IDs you received:

```text
GET http://localhost:8080/api/v1/policies/42
GET http://localhost:8080/api/v1/claims/39
GET http://localhost:8080/api/v1/settlements/5
```

---

## 7. Common errors

| What you see | Why | What to do |
| --- | --- | --- |
| `401 Unauthorized` | No token, or wrong token | Login again and set Bearer Token |
| `Failed to convert ... "{policyId}"` | You left `{policyId}` in the URL | Use `37`, not `{policyId}` |
| Policy / claim not found | Wrong ID | Use an ID from the tables in this README |
| Settlement not found for claim | Claim was rejected | Use claim `34`, `35`, `36`, or `38` |

Gateway health (no token):

```text
http://localhost:8080/actuator/health
```
