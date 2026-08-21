# Architecture Decisions

## 2026-08-21: Java 17 + Spring Boot 3.2

**Decision:** Java 17 LTS with Spring Boot 3.2.5 for the backend.
**Rationale:** Stable LTS release, modern features (records, sealed classes, pattern matching), Spring Boot 3.2 is latest stable with good ecosystem support.
**Trade-off:** Java 21 is available on the machine but 17 is the target for broader compatibility.

## 2026-08-21: Maven multi-module monorepo

**Decision:** Single Git repo with Maven multi-module backend and pnpm workspaces for frontend.
**Rationale:** Hackathon judges want to see everything in one repo. Maven multi-module keeps the SDK reusable and the API deployable independently.
**Trade-off:** Heavier repo, but acceptable for hackathon scope.

## 2026-08-21: H2 in-memory for dev

**Decision:** H2 in-memory database for development, PostgreSQL ready for prod via profile.
**Rationale:** Zero setup, instant `mvn spring-boot:run`, Flyway migrations ensure schema parity.
**Trade-off:** H2 has minor SQL dialect differences from PostgreSQL — mitigated by using standard SQL in Flyway scripts.

## 2026-08-21: Mock MoMo backend by default

**Decision:** MoMo SDK defaults to mock mode (`momo.env=mock`). Real API integration toggled via config.
**Rationale:** Hackathon demo needs to work reliably without real MoMo sandbox credentials. Mock responses look realistic (UUIDs, timestamps, latency simulation).
**Trade-off:** Extra code for mock layer, but critical for demo reliability.

## 2026-08-21: MapStruct for DTO mapping

**Decision:** MapStruct over ModelMapper or manual mapping.
**Rationale:** Compile-time code generation, type-safe, zero reflection overhead. Works well with Lombok.
**Trade-off:** Annotation processor setup complexity, but well worth it.

## 2026-08-21: Flyway for schema management

**Decision:** Flyway over Liquibase for database migrations.
**Rationale:** Simpler for hackathon scope, SQL-first approach, well-integrated with Spring Boot.
**Trade-off:** Liquibase is more powerful for complex migrations, but Flyway is sufficient here.

## 2026-08-21: Spring WebSocket + STOMP

**Decision:** Spring's built-in WebSocket support with STOMP protocol for real-time operator dashboard updates.
**Rationale:** No external dependencies needed, integrates with Spring Security, sufficient for the use case.
**Trade-off:** Socket.IO might be easier for the frontend, but STOMP is more standard for enterprise.

## 2026-08-21: JWT for authentication

**Decision:** Stateless JWT tokens with phone-based OTP login.
**Rationale:** Standard for mobile apps, no server-side session storage, works with MoMo's phone-centric model.
**Trade-off:** Token revocation is harder than session-based auth, but acceptable for hackathon scope.
