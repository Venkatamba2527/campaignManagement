# Campaign Management Platform — Backend

A Spring Boot backend for managing campaigns, built with Java 21, jOOQ, Liquibase, and PostgreSQL.

## Tech Stack

| Layer         | Technology                        |
|---------------|-----------------------------------|
| Language      | Java 21                           |
| Framework     | Spring Boot 3.3.4                 |
| Database      | PostgreSQL 16                     |
| Query DSL     | jOOQ 3.19                         |
| Migrations    | Liquibase                         |
| Build Tool    | Gradle (Kotlin DSL)               |

## Prerequisites

- Java 21+
- Docker & Docker Compose
- Gradle 8+ (or use the included `./gradlew` wrapper)

## Getting Started

### 1. Start the database

```bash
docker-compose up -d
```

This starts:
- **PostgreSQL** on `localhost:5432` (db: `campaign_db`, user/password: `campaign`)
- **Adminer** (DB UI) on `http://localhost:8081`

### 2. Run the application

```bash
./gradlew bootRun
```

The API will be available at `http://localhost:8080`.

### 3. Build a JAR

```bash
./gradlew bootJar
```

Output: `build/libs/campaign-platform-0.0.1-SNAPSHOT.jar`

## Database Migrations

Liquibase runs automatically on startup. Migration changelogs are located at:

```
src/main/resources/db/changelog/
```

## Health Check

```
GET http://localhost:8080/actuator/health
```

## Project Structure

```
src/
├── main/
│   ├── java/com/campaignPlatform/backend/   # Application source
│   └── resources/
│       ├── application.yml                  # App configuration
│       └── db/changelog/                    # Liquibase migrations
└── test/
    └── java/com/campaignPlatform/backend/   # Tests
```
