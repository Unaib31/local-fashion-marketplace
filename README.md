# Local Fashion Marketplace — Backend

Production-oriented Spring Boot backend for a local fashion marketplace connecting local clothing and fashion boutiques with nearby customers.

## Tech Stack
* **Java 21**
* **Spring Boot 3.3.3**
* **Spring Web, Spring Data JPA, Spring Security**
* **PostgreSQL**
* **JSON Web Token (JJWT 0.12.6)**
* **Jakarta Bean Validation**
* **SpringDoc OpenAPI / Swagger UI**

## Configuration
The application reads PostgreSQL and JWT configuration from environment variables with safe development defaults:

| Variable | Description | Default |
|---|---|---|
| `DB_URL` | PostgreSQL JDBC Connection URL | `jdbc:postgresql://localhost:5432/local_fashion_db` |
| `DB_USERNAME` | PostgreSQL User | `postgres` |
| `DB_PASSWORD` | PostgreSQL Password | `postgres` |
| `JWT_SECRET` | 256-bit Secret Key for signing JWTs | *(dev default key)* |
| `JWT_EXPIRATION_MS` | JWT Expiry in milliseconds | `86400000` (24h) |

## How to Build & Run

### Build & Run Tests
```powershell
.\mvnw.cmd clean test
```

### Run Application
```powershell
.\mvnw.cmd spring-boot:run
```

### Interactive API Documentation
Once running, open:
`http://localhost:8080/swagger-ui.html`
