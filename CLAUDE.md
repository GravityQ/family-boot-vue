# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is 芋道 (yudao) - A comprehensive RuoYi-Vue-Pro enterprise-level rapid development platform:
- **Branch**: master-jdk17 (JDK 17 + Spring Boot 3.5.5)
- **Version**: 2025.10-SNAPSHOT
- **Type**: Full-featured version (not mini version)
- **Architecture**: Multi-module Maven project based on Spring Boot

**Key modules currently enabled** (see root pom.xml):
- `yudao-server` - Main server application
- `yudao-module-system` - System core functionalities (users, roles, menus, tenants, etc.)
- `yudao-module-infra` - Infrastructure (code generation, file service, API logs, etc.)

**Disabled modules** (commented out in pom.xml):
- `yudao-module-member`, `yudao-module-bpm`, `yudao-module-pay`, `yudao-module-mall`,
  `yudao-module-crm`, `yudao-module-erp`, `yudao-module-ai`, `yudao-module-report`, `yudao-module-iot`

## Development Commands

### Building the Project

```bash
# Full clean build
mvn clean compile

# Package without tests
mvn clean package -DskipTests

# Package with tests
mvn clean package

# Install to local repository
mvn clean install -DskipTests
```

### Running Tests

```bash
# Run all unit tests
mvn test

# Run tests for specific module
cd yudao-module-system && mvn test

# Run single test class
mvn test -Dtest=UserServiceTest

# Run single test method
mvn test -Dtest=UserServiceTest#testCreateUser

# Run tests matching pattern
mvn test -Dtest=*ServiceTest
```

### Running the Application

```bash
# Run from yudao-server directory
cd yudao-server

# With dev profile (see yudao-server/src/main/resources/application-dev.yaml)
java -jar target/yudao-server.jar --spring.profiles.active=dev

# Or using Maven Spring Boot plugin
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Deployment (Production)

```bash
# Using the provided deployment script
./script/shell/deploy.sh

# Manual deployment with JVM options
java -server -Xms512m -Xmx512m \
  -XX:+HeapDumpOnOutOfMemoryError \
  -jar yudao-server.jar \
  --spring.profiles.active=prod
```

### Code Generation

1. Access the admin UI (http://localhost:48080) after starting the application
2. Navigate to "基础设施" -> "代码生成"
3. Configure table, generate code (Java backend + Vue frontend + SQL + API docs + unit tests)
4. Download and integrate generated code

## Architecture & Module Structure

### Multi-Module Maven Structure

```
yudao/ (root pom.xml)
├── yudao-dependencies/          # Maven BOM - dependency version management
├── yudao-framework/             # Common framework extensions and utilities
├── yudao-server/                # Main Spring Boot application (entry point)
└── yudao-module-*/             # Business modules (9+ modules available)
```

### Core Technical Stack

- **Spring Boot**: 3.5.5 (JDK 17)
- **Database**: MySQL 8.0 + Druid 1.2.27 + MyBatis Plus 3.5.12
- **Multi-datasource**: Dynamic Datasource 4.3.1 (master-slave pattern)
- **Cache**: Redis 6.0/7.0 + Redisson 3.35.0
- **Security**: Spring Security 6.5.2 + JWT Token + Redis
- **API Docs**: Springdoc OpenAPI (Swagger) 2.8.9
- **Validation**: Hibernate Validator 8.0.2
- **Scheduling**: Quartz 2.5.0
- **Object Mapping**: MapStruct 1.6.3 + Lombok 1.18.38
- **Testing**: JUnit 5.12.2 + Mockito 5.17.0

### Key Architectural Patterns

1. **Multi-tenancy (SaaS)**: Tenant isolation at database level with transparent encapsulation
2. **Permission System**: Role-based access control (RBAC) with menu/button-level permissions
3. **API Design**: RESTful APIs with uniform response format
4. **Caching Strategy**: Multi-level caching (Redis + local cache)
5. **Distributed Lock**: Based on Redisson for high-concurrency scenarios
6. **Idempotency**: Built-in idempotent support for critical operations

### Configuration Management

- **Database Config**: `application-dev.yaml` uses MySQL (47.92.217.175:3306) with credentials
- **Redis Config**: Redis at 47.92.217.175:6379 (database 1)
- **Server Port**: 48080 (primary), varies for different services
- **Profiles**: dev, prod (local configuration available but gitignored)

### Development Environment Database

Current dev environment uses cloud databases:
- MySQL: `jdbc:mysql://47.92.217.175:3306/my_test`
- Username: `my_test`
- Redis: `47.92.217.175:6379` (DB 1, password protected)

**Important**: Database credentials are in plain text in application-dev.yaml. For security:
- Use environment variables or Spring Cloud Config for production
- Consider using local Docker containers for development

## Module Responsibilities

### yudao-server
Main entry point. Contains Spring Boot application class and minimal business logic.
Should stay thin - mainly imports other modules and configuration.

### yudao-module-system
Core system functionalities:
- User, role, menu, department, position management
- Tenant management and tenant packages (SaaS)
- Dict data, error codes, sensitive words
- SMS, mail, notification, app management
- Operation logs, login logs

### yudao-module-infra
Infrastructure capabilities:
- **Code generation** (most important): Single/Tree/Sub-table CRUD generation
- File service: S3/MinIO/Aliyun/Qiniu/local/FTP/database storage
- API logging: RESTful API access logs and exception logs
- Config management: Dynamic configuration support
- Schedule jobs: Quartz-based online job management
- Java monitoring with Spring Boot Admin
- Message queue (Redis-based), WebSocket, rate limiting
- SkyWalking integration (tracing & log center)

## Important Development Notes

### Before Development

1. Identify which module your feature belongs to:
   - System core features → `yudao-module-system`
   - Infrastructure/tooling → `yudao-module-infra`
   - Specific business domain → Enable corresponding module in root pom.xml

2. Module activation: Uncomment the module in root pom.xml and add dependencies

3. Database migrations: Flyway not currently used - manually manage SQL scripts in `sql/` directory

### Testing Strategy

- Unit tests required for all business logic (JUnit + Mockito)
- Test configuration: `application-unit-test.yaml` in each module's `src/test/resources/`
- Run tests before committing: `mvn test`

### API Development Pattern

- Controller → Service → Mapper (MyBatis Plus)
- DTO/VO for request/response objects
- MapStruct for object conversion (not BeanUtils)
- Parameter validation with `@Valid` annotations
- Use `@PreAuthorize("@ss.hasPermi('system:user:list')")` for permission control

### Code Conventions

- Follow Alibaba Java Development Manual
- Use Lombok to reduce boilerplate
- All public methods must have JavaDoc
- Business errors use ServiceException with error codes
- Use SLF4J logging, not System.out
