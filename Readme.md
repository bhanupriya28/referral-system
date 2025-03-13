# Referral System

This is a **Spring Boot**-based Referral System that uses **Redis** as its database. The application provides APIs for managing referral codes, including creation, retrieval, redemption, and manager-specific referral codes.

## Technologies Used

- **Spring Boot** - For building REST APIs  
- **Redis** - In-memory database for storing referral codes  
- **Docker & Docker Compose** - For containerizing the application and Redis  

---

## Build and Run

This application contains **docker-compose.yml** file at root location , open commandprompt / powershell at same location and run the following command:
**docker-compose up --build** 
This will run the application and bind it to **localhost:8080** of host machine.

## API Endpoints

### 1. Get All Referral Codes
**Endpoint:** `GET /api/referrals`

**Response Example:**
```json
[
    {
        "id": "11f0af5f-44e9-4964-afca-17fb1762b6db",
        "address": "ashish",
        "ref_code": "112345691",
        "is_manager_code": "0",
        "created_at": "2025-02-24T18:48:30.530703892",
        "updated_at": "2025-02-24T18:48:30.530751402"
    }
]

```

### 2. Create new code 
**Endpoint:** `POST /api/referrals`

**Request Example:**
```json
{"address":"ashish","ref_code":"123456"}

```

### 3. Create new code for manager
**Endpoint:** `POST /api/referrals/manager`

**Request Example:**
```json
{"address":"ashish","ref_code":"123456"}

```

### 4. Get codes  for a specific user/address
**Endpoint:** `GET /api/referrals/address/{address}`

**Response Example:**
```json
[
    {
        "id": "11f0af5f-44e9-4964-afca-17fb1762b6db",
        "address": "ashish",
        "ref_code": "112345691",
        "is_manager_code": "0",
        "created_at": "2025-02-24T18:48:30.530703892",
        "updated_at": "2025-02-24T18:48:30.530751402"
    }
]

```