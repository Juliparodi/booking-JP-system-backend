# Booking JP System Backend – Requirements

## 1. Overview

**Project name:** booking-JP-system-backend

**Description:**  
A production-ready Booking / Scheduling System backend designed for freelance use cases
(e.g. salons, clinics, coworking spaces).  
The system is built using Java, Spring Boot, Hexagonal Architecture, Domain-Driven Design (DDD),
and CQRS, with a strong focus on clean architecture, security, and operational readiness.

**Primary stakeholders / owners:**
- Product: Self-managed (portfolio / freelance project)
- Engineering: Backend Engineer (author)
- Security / Compliance: Backend Engineer (author)

---

## 2. Goals and Success Metrics

### Business Goals
- Provide a reusable, production-ready booking backend suitable for real client deployments.
- Demonstrate senior-level backend architecture and engineering practices.
- Be cloud-agnostic while remaining AWS-friendly.

### Success Metrics
- Availability ≥ 99.9% (monthly)
- 95th percentile API response time < 200ms under baseline load
- Error rate < 0.1%
- Code coverage ≥ 80%

---

## 3. Scope

### In-scope
- Backend-only monolithic application
- Booking and scheduling domain
- REST API
- OAuth2 authentication with JWT
- CQRS (command/query separation)
- Hexagonal Architecture + DDD
- Production-ready CI and configuration

### Out-of-scope
- Frontend (React will be developed later)
- Payment processing
- Multi-tenancy (future evolution)
- Notifications beyond domain events (email/SMS optional later)

---

## 4. Stakeholders & Roles

- Product Owner: Julian Parodi
- Engineering Lead: Julian Parodi
- Release Manager: Julian Parodi

---

## 5. High-level Architecture

- Architecture style: Hexagonal Architecture (Ports & Adapters)
- Deployment model: Monolithic Spring Boot application
- Communication: REST over HTTPs
- Persistence: PostgreSQL
- Authentication: OAuth2 Resource Server (JWT)

**Key dependencies:**
- PostgreSQL
- OAuth2 Authorization Server (external)
- Docker runtime

---

## 6. Tech Stack & Constraints

### Backend
- Java 21
- Spring Boot 3.x
- Maven
- Spring Web
- Spring Security (OAuth2 Resource Server)
- Spring Data JPA (in infrastructure layer only)
- Flyway for DB migrations

### Testing
- JUnit 5
- Mockito
- Testcontainers (PostgreSQL)

### Observability
- Structured logging (JSON)
- Micrometer metrics
- OpenTelemetry-ready setup

### CI / Runtime
- GitHub Actions
- Docker / OCI images
- Cloud-agnostic (AWS-compatible)

---

## 7. Domain Model & Bounded Contexts

### Core Domain: Booking

#### Entities
- Booking
- Resource (room, consultant, service, etc.)
- TimeSlot

#### Value Objects
- BookingId
- ResourceId
- TimeRange

#### Domain Rules
- A resource cannot be booked twice for overlapping time slots
- Booking lifecycle: CREATED → CONFIRMED → CANCELLED
- Cancellation rules enforced in the domain layer

#### Domain Events
- BookingCreated
- BookingCancelled

---

## 8. CQRS Requirements

### Commands
- CreateBookingCommand
- CancelBookingCommand
- RegisterResourceCommand

### Queries
- GetBookingByIdQuery
- GetBookingsByUserQuery
- GetAvailableResourcesQuery

**Rules:**
- Commands mutate state
- Queries are read-only
- Command and query models may differ

---

## 9. Package & Architecture Structure

com.example.booking
├── domain
│   ├── model
│   ├── valueobject
│   ├── event
│   ├── repository
│   └── service
├── application
│   ├── command
│   ├── query
│   ├── usecase
│   └── dto
└── infrastructure
├── inbound
│   └── rest
├── outbound
│   └── persistence
├── security
├── config
└── observability

## 10. Functional Requirements

### FR-001 — Create Booking
- **Endpoint:** `POST /api/v1/bookings`
- **Description:** Creates a new booking for a resource.
- **Behavior:**
    - Validates time-slot availability.
    - Rejects overlapping bookings for the same resource.
- **Response:**
    - `201 Created` with the generated booking ID.

---

### FR-002 — Cancel Booking
- **Endpoint:** `POST /api/v1/bookings/{id}/cancel`
- **Description:** Cancels an existing booking.
- **Behavior:**
    - Enforces domain cancellation rules.
    - Prevents invalid or late cancellations.

---

### FR-003 — List Bookings by User
- **Endpoint:** `GET /api/v1/bookings?userId={id}`
- **Description:** Returns all bookings associated with a specific user.
- **Response:**
    - `200 OK` with a list of bookings.

---

### FR-004 — Register Resource
- **Endpoint:** `POST /api/v1/resources`
- **Description:** Registers a new bookable resource (e.g., room, consultant, service).
- **Response:**
    - `201 Created` with the resource ID.

---

## 11. Non-Functional Requirements

- **Availability:** ≥ 99.9% monthly
- **Performance:**
    - 95th percentile API latency < 200ms under baseline load
- **Security:**
    - OWASP Top 10 mitigations applied
- **Maintainability:**
    - ≥ 80% test coverage (unit + integration)
- **Observability:**
    - Structured logs
    - Metrics (latency, error rate, throughput)
    - Distributed tracing support
- **Configuration:**
    - Environment variables only
    - No environment-specific values hardcoded

---

## 12. Security Requirements

- OAuth2 authentication using JWT
- Role-based authorization:
    - `ADMIN`
    - `CLIENT`
- No secrets or credentials committed to source control
- Dependency vulnerability scanning enabled in CI
- Communication with SSL, over HTTPS

---

## 13. CI / CD Requirements

### Continuous Integration (CI)
CI must run on every Pull Request and include:
- Unit tests execution
- Integration tests using Testcontainers
- Code coverage verification
- Dependency vulnerability scanning

### Artifacts
- Docker image built as an immutable artifact
- Image tagged using semantic versioning

---

## 14. Documentation

The repository must include:

- **README.md** containing:
    - Architecture overview
    - Local development instructions
    - Testing instructions
    - Docker usage
- **API documentation:**
    - OpenAPI / Swagger
- **Architecture documentation:**
    - Architectural decisions and rationale

---

## 15. Constraints & Assumptions

- Single-region deployment
- Moderate traffic profile (freelance / SME scale)
- OAuth2 authorization provider managed externally
- Backend-only scope (frontend developed separately)

