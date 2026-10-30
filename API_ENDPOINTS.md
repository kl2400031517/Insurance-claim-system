# API Endpoints README

This file is the Postman guide for the Insurance Claim Processing System.

The same content is also in [README.md](README.md). Open **README.md** for the full document.

Each endpoint below shows:

- method
- URL
- whether a body is needed
- the exact JSON body (for POST)

Use base URL:

```text
http://localhost:8080
```

Add Bearer token after login on every request except login.

---

## Login

**POST** `http://localhost:8080/api/v1/auth/login`

Body:

```json
{
  "username": "admin",
  "password": "password123"
}
```

Copy `token` from the response.

Also valid:

```json
{
  "username": "user1",
  "password": "password123"
}
```

---

## Policy

### Create policy

**POST** `http://localhost:8080/api/v1/policies`

Body:

```json
{
  "userId": "alice",
  "coverageAmount": 100000
}
```

### Get policy by ID

**GET** `http://localhost:8080/api/v1/policies/37`

No body.

Other IDs in the database: `1`, `34`, `35`, `36`, `38`, `39`, `40`, `41`.

### Get policies by user

**GET** `http://localhost:8080/api/v1/policies/user/alice`

No body.

Other users: `user1`, `bob`, `carol`, `dave`, `USER_POSTMAN_1`.

### Validate policy

**GET** `http://localhost:8080/api/v1/policies/37/validate?claimAmount=5000`

No body. The amount is in the URL.

### Deduct coverage

**POST** `http://localhost:8080/api/v1/policies/37/deduct`

Body:

```json
{
  "amount": 5000
}
```

---

## Claim

### Submit claim

**POST** `http://localhost:8080/api/v1/claims`

Body:

```json
{
  "policyId": 40,
  "claimAmount": 5000
}
```

Rejected example:

```json
{
  "policyId": 39,
  "claimAmount": 30000
}
```

Failed payment example:

```json
{
  "policyId": 40,
  "claimAmount": 5000,
  "simulatePaymentFailure": true
}
```

### Get claim by ID

**GET** `http://localhost:8080/api/v1/claims/34`

No body.

Other IDs: `35`, `36`, `37` (rejected), `38`.

### Get claims by policy

**GET** `http://localhost:8080/api/v1/claims/policy/37`

No body.

---

## Settlement

### Process settlement

**POST** `http://localhost:8080/api/v1/settlements`

Body:

```json
{
  "claimId": 34,
  "policyId": 37,
  "approvedAmount": 15000
}
```

### Get settlement by ID

**GET** `http://localhost:8080/api/v1/settlements/1`

No body.

Other IDs: `2`, `3` (FAILED), `4`.

### Get settlement by claim

**GET** `http://localhost:8080/api/v1/settlements/claim/34`

No body.

Do not use claim `37`. It was rejected, so it has no settlement.

---

## Records already in the database

```text
Policy 37 alice  -> Claim 34 APPROVED -> Settlement 1 SUCCESS
                 -> Claim 35 APPROVED -> Settlement 2 SUCCESS

Policy 38 bob    -> Claim 36 APPROVED -> Settlement 3 FAILED

Policy 39 carol  -> Claim 37 REJECTED -> no settlement

Policy 40 dave   -> Claim 38 APPROVED -> Settlement 4 SUCCESS
```

For tables, sample responses, and error notes, see **README.md**.
