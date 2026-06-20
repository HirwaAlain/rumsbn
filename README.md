# RUMS — Rwanda Utilities Regulatory Authority Backend API

RESTful JSON API for the RUMS (Regulatory Utility Management System) internal back-office platform.
Built with Spring Boot 3.3.x, PostgreSQL 16, Flyway, Spring Security 6 + JWT, and SpringDoc OpenAPI 3.

---

## Prerequisites

| Tool | Version |
|---|---|
| Java (JDK) | 17 or later |
| Apache Maven | 3.9+ (or use the included `./mvnw` wrapper) |
| PostgreSQL | 16 |

---

## Setup

### 1. Create the database

```sql
CREATE USER rums_user WITH PASSWORD 'your_password';
CREATE DATABASE rums_db OWNER rums_user;
```

### 2. Configure application.properties

Edit `src/main/resources/application.properties` and set the values marked `CHANGE_ME`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/rums_db
spring.datasource.username=rums_user
spring.datasource.password=CHANGE_ME

rums.jwt.secret=CHANGE_ME_TO_A_256_BIT_BASE64_SECRET

spring.mail.host=smtp.example.com
spring.mail.username=noreply@rura.rw
spring.mail.password=CHANGE_ME
```

Generate a secure JWT secret (256-bit, Base64-encoded):
```bash
openssl rand -base64 32
```

### 3. Run database migrations and start the server

Flyway migrations run automatically on startup. The migrations apply in order:

| Version | Description |
|---|---|
| V1 | PostgreSQL enum types |
| V2 | All table definitions |
| V3 | Indexes |
| V4 | Seed data (9 users, 12 licenses, and sample records across all modules) |

```bash
./mvnw spring-boot:run
```

The API starts on **http://localhost:8080**.

---

## Default Admin Credentials

| Field | Value |
|---|---|
| Email | `admin@rura.rw` |
| Password | `Admin@1234!` |
| Role | `admin` |

> All seed users share the same temporary password `Admin@1234!`.

---

## Swagger UI

Interactive API documentation (requires the server to be running):

```
http://localhost:8080/swagger-ui.html
```

To authenticate in Swagger UI:
1. Call `POST /api/auth/login` with the admin credentials above.
2. Copy the `accessToken` from the response.
3. Click **Authorize** (padlock icon) and enter `<your token>` in the BearerAuth field.

---

## Running Tests

Tests use H2 in-memory database — no running PostgreSQL instance is required.

```bash
# Run all tests
./mvnw test

# Run a specific test class
./mvnw test -Dtest=AuthControllerTest
./mvnw test -Dtest=LicenseServiceTest
```

### Test coverage

| Test class | Type | What it covers |
|---|---|---|
| `AuthControllerTest` | Integration (`@SpringBootTest`) | Login success/failure, token refresh, unauthenticated 401 |
| `LicenseServiceTest` | Unit (Mockito) | Paginated list, getById not found, create, invalid status transition |
| `RumsApplicationTests` | Smoke | Spring context loads successfully |

---

## Project Structure

```
src/main/java/rw/rura/rums/
├── RumsApplication.java
├── config/          # SecurityConfig, OpenApiConfig
├── security/        # JwtAuthFilter, JwtService, UserDetailsServiceImpl
├── audit/           # AuditService
├── alert/           # AlertService
├── exception/       # GlobalExceptionHandler, custom exceptions
├── enums/           # All project-wide enums
├── dto/             # ApiResponse wrapper
└── module/
    ├── auth/        # Login, refresh, logout, change-password
    ├── dashboard/   # KPIs, charts, activity feed
    ├── licenses/
    ├── complaints/
    ├── compliance/
    ├── fraud/
    ├── clms/        # Case & Licence Management System
    ├── workflows/
    ├── alerts/
    ├── audit/
    ├── reports/
    └── users/
```

---

## Key Configuration Reference

| Property | Default | Notes |
|---|---|---|
| `server.port` | `8080` | |
| `rums.jwt.access-expiry-seconds` | `28800` | 8 hours |
| `rums.jwt.refresh-expiry-seconds` | `604800` | 7 days |
| `rums.files.upload-dir` | `./uploads/` | File storage root |
| `rums.cors.allowed-origin` | `http://localhost:3000` | Frontend origin |
| `spring.servlet.multipart.max-file-size` | `20MB` | Max upload size |
