# Portfolio Backend (Spring Boot + H2/MySQL)

A full-stack Java backend that captures **Contact Form** submissions and **Meeting Bookings** from the portfolio frontend, persisting them to a database.

## 🚀 Getting Started

### Option A: Quick Start with H2 (No MySQL needed!)
```bash
cd portfolio-backend
mvn spring-boot:run
```
That's it! The backend starts with an **in-memory H2 database** on port `8080`.

### Option B: MySQL Setup
1. Install and start MySQL Server
2. Run [schema.sql](schema.sql) to create the database and tables
3. Edit [application.properties](src/main/resources/application.properties):
```properties
spring.profiles.active=mysql
```
4. Update credentials in [application-mysql.properties](src/main/resources/application-mysql.properties)
5. Run `mvn spring-boot:run`

---

## 🛠️ REST API Endpoints

### Contact Messages

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/contact` | Save a new contact message |
| `GET` | `/api/contact` | List ALL contact messages |
| `GET` | `/api/contact/{id}` | Get a specific message by ID |

**POST Payload:**
```json
{
  "name": "Alex Mercer",
  "email": "alex@example.com",
  "message": "Interested in hiring opportunities."
}
```

---

### Meeting Bookings

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/meetings` | Book a new meeting |
| `GET` | `/api/meetings` | List ALL meeting bookings |
| `GET` | `/api/meetings/{id}` | Get a specific booking by ID |

**POST Payload:**
```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "topic": "Job Opportunity",
  "preferredDate": "2026-06-25",
  "preferredTime": "14:00"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Meeting booked successfully!",
  "data": {
    "id": 1,
    "name": "John Doe",
    "email": "john@example.com",
    "topic": "Job Opportunity",
    "preferredDate": "2026-06-25",
    "preferredTime": "14:00",
    "status": "PENDING",
    "bookedAt": "2026-06-21T17:33:05"
  }
}
```

---

## 🔍 How to Check Database User Data

### Method 1: H2 Web Console (Recommended for development)
When running with H2 profile, open your browser:
```
http://localhost:8080/h2-console
```
**Login settings:**
| Field | Value |
|-------|-------|
| JDBC URL | `jdbc:h2:mem:portfolio_db` |
| Username | `sa` |
| Password | *(leave empty)* |

Then run SQL queries:
```sql
-- View all contact messages
SELECT * FROM contact_messages;

-- View all meeting bookings
SELECT * FROM meeting_bookings;

-- Count records
SELECT COUNT(*) FROM contact_messages;
SELECT COUNT(*) FROM meeting_bookings;
```

### Method 2: REST API (works with both H2 and MySQL)
```bash
# List all contact messages
curl http://localhost:8080/api/contact

# List all meeting bookings
curl http://localhost:8080/api/meetings

# Get a specific contact message (by ID)
curl http://localhost:8080/api/contact/1

# Get a specific meeting booking (by ID)
curl http://localhost:8080/api/meetings/1
```

**PowerShell examples:**
```powershell
# View all contact messages
(Invoke-WebRequest -Uri http://localhost:8080/api/contact -UseBasicParsing).Content | ConvertFrom-Json | ConvertTo-Json -Depth 5

# View all meeting bookings
(Invoke-WebRequest -Uri http://localhost:8080/api/meetings -UseBasicParsing).Content | ConvertFrom-Json | ConvertTo-Json -Depth 5
```

### Method 3: MySQL Workbench / DBeaver (MySQL profile only)
Connect to `localhost:3306` with your MySQL credentials and run:
```sql
USE portfolio_db;
SELECT * FROM contact_messages ORDER BY submitted_at DESC;
SELECT * FROM meeting_bookings ORDER BY booked_at DESC;
```

---

## 🔒 CORS Configuration
The backend has `@CrossOrigin(origins = "*")` on all controllers, enabling requests from any origin (localhost, GitHub Pages, etc.).

## 📁 Project Structure
```
portfolio-backend/
├── pom.xml
├── schema.sql                          # MySQL schema (tables auto-created by Hibernate for H2)
└── src/main/
    ├── java/com/naik/portfolio/
    │   ├── PortfolioBackendApplication.java
    │   ├── controller/
    │   │   ├── ContactController.java   # /api/contact endpoints
    │   │   └── MeetingController.java   # /api/meetings endpoints
    │   ├── model/
    │   │   ├── ContactMessage.java      # JPA entity
    │   │   └── MeetingBooking.java      # JPA entity
    │   └── repository/
    │       ├── ContactMessageRepository.java
    │       └── MeetingBookingRepository.java
    └── resources/
        ├── application.properties       # Profile selector (h2 or mysql)
        ├── application-h2.properties    # H2 in-memory DB config
        └── application-mysql.properties # MySQL config
```
