# Noda API

This is a banking-style REST API I built to practice backend development with Spring Boot, mainly Java, Spring Security, JWT authentication, and testing. It's not a real product and it's not meant to be used by real users — it's a personal study project to get hands-on with things I'd actually use as a backend developer.

## Why I built this

I wanted something more realistic than a tutorial CRUD app, so I picked a banking API because it forces you to deal with real problems: authentication, security, data validation, error handling, and testing. Everything here was built (and is still being fixed/improved) by me, learning as I went.

## Stack

- Java 21
- Spring Boot 3.4 (Web, Data JPA, Security, Validation, Test)
- PostgreSQL
- JWT (jjwt)
- Lombok
- JUnit 5 + Mockito
- Maven

## What it does

- User registration with address lookup via ViaCEP
- Two-step login: password check, then a one-time code sent by email, then a JWT is issued
- JWT-protected routes using a custom filter (stateless, no server sessions)
- Account operations: deposit, withdraw, transfer, statement
- Centralized error handling for things like duplicate CPF/email, insufficient funds, account not found, etc.
- 26+ tests (unit, controller, integration)

## Current state

I'm actively going back through the security part of this project and fixing things I rushed while learning Spring Security — stuff like tightening validation, fixing tests that don't match real login behavior, and cleaning up edge cases. This README will get updated as that progresses.

## Running it locally

1. Clone the repo
2. Create `application-local.properties` (gitignored) with your DB credentials, `jwt.secret`, and mail settings
3. `mvnw spring-boot:run`
4. `mvnw test` to run the tests
