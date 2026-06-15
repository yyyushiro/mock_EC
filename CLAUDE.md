# CLAUDE.md

## Commands

```bash
# Build
./gradlew build

# Run
./gradlew bootRun

# Run all tests
./gradlew test

# Run a single test class
./gradlew test --tests "com.example.springboot.ApplicationTests"
```

## Architecture

Standard Spring Boot 3.5 layered architecture: Controller → Service → Repository → Entity.

**Role-based API split:** There are two separate controller/DTO paths depending on the caller's role:
- `/api/customer/products` — served by `CustomerProductController` + `CustomerProductService`, returns `ProductForCustomer` (exposes `id`)
- `/api/seller/products` — served by `SellerProductController`, returns `ProductForSeller` (exposes `uuidV4` instead of `id`)

This is intentional: internal numeric IDs are hidden from customers; sellers receive the UUID.

**Entity ID strategy:** All entities use a database sequence (`allocationSize = 50`) for the primary key `Long id`, plus a separate `UUID uuidV4` column for external-facing identification. The comment `// Replace with uuid v7 in the future.` appears in both `User` and `Product` — UUIDv4 is a placeholder.

**Database:** H2 in-memory with `ddl-auto=create-drop`. On startup, `DataInitializer` (a `CommandLineRunner`) bulk-inserts 1,000 test `Product` rows using `saveAll()` + `flush()` inside a single `@Transactional` method to exercise batch insert performance.

**Stubs in progress:** `UserController`, `CartController`, `OrderController`, `UserService`, `CartService`, `OrderService`, and `OrderRepository` are empty classes. `Order` entity is also empty.

**SQL logging:** p6spy is used (not Hibernate's `show-sql`). Its log level is set to `WARN` in `application.properties` to suppress noise; set `logging.level.p6spy=DEBUG` temporarily to see SQL.

**H2 console:** available at `/h2-console` (enabled in `application.properties`).
