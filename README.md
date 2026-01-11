# Pattern Project

A multi-module Spring Boot application demonstrating microservices architecture with authentication and system modules.

## Architecture

This project follows a modular microservices architecture with:

- **Auth Module**: Handles authentication, authorization, and user management
- **System Module**: Core business logic and system operations

## Tech Stack

- **Java 21**
- **Spring Boot 3.5.3**
- **Spring Security**
- **Spring Data JPA**
- **PostgreSQL**
- **Redis** (for caching)
- **JWT** for authentication
- **Maven** for dependency management
- **Testcontainers** for integration testing

## Prerequisites

- Java 21 or higher
- Maven 3.6+
- PostgreSQL 12+
- Redis 6+
- Docker (for running tests with Testcontainers)

## Getting Started

### 1. Clone the repository
```bash
git clone https://github.com/kiporenko/patternproject.git
cd patternproject
```

### 2. Set up the database
```sql
CREATE DATABASE Blog;
```

### 3. Configure environment variables
Create a `.env` file or set the following environment variables:

```bash
DATABASE_URL=jdbc:postgresql://localhost:5432/Blog
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=your_password
REDIS_HOST=localhost
REDIS_PORT=6379
JWT_SECRET=your_jwt_secret_key
```

### 4. Build the project
```bash
mvn clean install
```

### 5. Run the services

#### Auth Service
```bash
cd auth
mvn spring-boot:run
```
The auth service will be available at `http://localhost:8080/api/v1`

#### System Service
```bash
cd system
mvn spring-boot:run
```
The system service will be available at `http://localhost:8081/api/v1`

## Configuration Profiles

The application supports multiple profiles:

- **dev**: Development environment with debug logging
- **prod**: Production environment with optimized settings
- **test**: Test environment with in-memory database

Set the active profile using:
```bash
export SPRING_PROFILES_ACTIVE=dev
```

## API Documentation

### Health Checks
- Auth Service: `GET http://localhost:8080/api/v1/actuator/health`
- System Service: `GET http://localhost:8081/api/v1/actuator/health`

### Metrics
- Prometheus metrics: `/actuator/prometheus`
- Application metrics: `/actuator/metrics`

## Testing

### Run all tests
```bash
mvn test
```

### Run tests with coverage
```bash
mvn clean test jacoco:report
```

Coverage reports will be available in `target/site/jacoco/index.html`

## Docker Support

### Build Docker images
```bash
# Auth service
docker build -t auth-service ./auth

# System service
docker build -t system-service ./system
```

### Run with Docker Compose
```bash
docker-compose up -d
```

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.