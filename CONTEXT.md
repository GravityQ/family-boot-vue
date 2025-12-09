# CLAUDE.md - Project Context

## Project Overview

**Name**: 芋道 (yudao-boot-vue)
**Description**: An enterprise-level rapid development platform based on RuoYi-Vue-Pro.
**Version**: 2025.10-SNAPSHOT
**Core Stack**: JDK 17 + Spring Boot 3.5.5 (Master-JDK17 Branch)

## Tech Stack

| Component | Version | Description |
|-----------|---------|-------------|
| **Java** | JDK 17 | Core Language |
| **Spring Boot** | 3.5.5 | Application Framework (pom.xml) |
| **Database** | MySQL 5.7/8.0+ | Relational Database |
| **ORM** | MyBatis Plus 3.5.12 | Persistence Layer |
| **Connection Pool** | Druid 1.2.27 | Database Connection Pool |
| **Cache** | Redis 6/7 | Caching & Messaging |
| **Redis Client** | Redisson 3.35.0 | Distributed Locks & Objects |
| **Security** | Spring Security 6.5.2 | Auth & Access Control |
| **API Docs** | Springdoc 2.8.9 | Swagger/OpenAPI |
| **Workflow** | Flowable 7.0.0 | BPM Engine |
| **Job Schedule** | Quartz 2.5.0 | Task Scheduling |
| **Utils** | Lombok 1.18.38 | Boilerplate Reduction |
| **Mapping** | MapStruct 1.6.3 | Bean Mapping |

## Project Structure

This is a multi-module Maven project:

```text
yudao/
├── yudao-dependencies/          # Dependency management (BOM)
├── yudao-framework/             # Common framework components & utils
├── yudao-server/                # Application entry point (Spring Boot Main)
└── yudao-module-*/              # Business Modules
    ├── yudao-module-system/     # Core system (User, Role, Menu, Dict, etc.)
    ├── yudao-module-infra/      # Infrastructure (Code Gen, File, Job, API Log)
    ├── yudao-module-member/     # Member Center (Users, Tags, Level)
    ├── yudao-module-bpm/        # Business Process Management (Flowable)
    ├── yudao-module-pay/        # Payment System (Alipay, WeChat)
    ├── yudao-module-mall/       # E-commerce System
    ├── yudao-module-crm/        # Customer Relationship Management
    ├── yudao-module-erp/        # Enterprise Resource Planning
    ├── yudao-module-ai/         # AI Large Model Integration
    ├── yudao-module-report/     # Reporting & Dashboard
    └── yudao-module-iot/        # IoT Module
```

## Key Features

### System Module (Core)
- **User/Role/Menu**: RBAC permission model.
- **Tenant Management**: SaaS multi-tenancy support.
- **Data Permission**: Dept/Position based data scope.
- **System Monitoring**: Online users, operation logs, login logs.

### Infrastructure Module
- **Code Generation**: Generate Java, Vue, SQL, and Tests from DB tables.
- **File Service**: S3, MinIO, Aliyun, Local storage support.
- **API Logging**: Request/Response logging and analysis.
- **Job Scheduling**: Distributed task scheduling via Quartz.
- **Monitor**: Spring Boot Admin, SkyWalking integration.

### BPM (Workflow)
- **Designers**: Simple (DingTalk-like) & BPMN 2.0.
- **Flow Control**: Countersign, OR-sign, sequential approval.
- **Task Mgmt**: Delegate, Transfer, Revoke, Rollback.

### Payment & Mall
- **Payment**: Alipay, WeChat Pay integration (Refunds, Orders).
- **Mall**: Product, Order, Promotion, Trade management.

## Development Commands

### Build & Run
```bash
# Build project
mvn clean install -DskipTests

# Run Unit Tests
mvn test

# Run Application (Dev)
cd yudao-server
mvn spring-boot:run -Dspring-boot.run.profiles=dev
# OR
java -jar target/yudao-server.jar --spring.profiles.active=dev
```

### Code Generation
1. Start application.
2. Go to `Infrastructure -> Code Gen`.
3. Import table -> Edit Config -> Generate.
4. Download and place files in respective directories.

## Conventions
- **Controller-Service-DAO**: Standard layered architecture.
- **DO/DTO/VO**: Strict separation of data objects.
- **MapStruct**: Use for object conversion (avoid BeanUtils).
- **Lombok**: Use `@Data`, `@Builder` etc.
- **Exceptions**: Use `ServiceException` with error codes.
- **API**: RESTful, use `@Operation` for Swagger docs.

## Documentation & Resources
- **Docs**: https://doc.iocoder.cn/
- **Issues**: https://github.com/YunaiV/ruoyi-vue-pro/issues

