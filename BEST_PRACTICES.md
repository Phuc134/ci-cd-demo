# 📘 Project Best Practices

## 1. Project Purpose
This repository contains a multi-module Java project with two Spring Boot microservices (account-service and customer-service) communicating via gRPC. The domain focuses on customers and their accounts, exposing gRPC APIs for account retrieval and customer aggregation.

## 2. Project Structure
- Root
  - account-service
    - src/main/java: Spring Boot application, gRPC service (AccountServiceGrpcService), in-memory repository (AccountServiceRepository)
    - src/main/resources: application.yml for service configuration
    - src/test/java: JUnit 5 Spring Boot context tests
  - customer-service
    - src/main/java: Spring Boot application, gRPC server (CustomerGrpcService), gRPC client (AccountClient), in-memory repository (CustomerRepository), client config (GrpcClientConfig)
    - src/main/resources: application.yml for server and client channel config
    - src/test/java: JUnit 5 Spring Boot context tests
  - Shared build tooling: Maven wrapper, module-specific pom.xml files
- Key entry points
  - AccountServiceApplication and CustomerApplication (Spring Boot main classes)
- Protos
  - account-service/src/main/proto and customer-service/src/main/proto define gRPC contracts compiled to Java stubs

## 3. Test Strategy
- Framework: JUnit 5 with SpringBootTest for context loading.
- Organization: Tests reside under each service’s module in src/test/java mirroring package structure.
- Mocking:
  - Prefer constructor injection to enable easy mocking of collaborators (e.g., repositories or gRPC stubs) using Mockito in unit tests.
  - For integration tests, spin up Spring context with in-memory data and consider using @GrpcClient/@GrpcService where applicable.
- Unit vs Integration:
  - Unit tests for repository logic and simple service transformations.
  - Integration tests for gRPC endpoints (both server and client interaction), verifying serialization, error codes, and channel configuration.
- Coverage expectations: Aim for meaningful coverage on domain logic (repositories, service glue) and happy-path/error-paths on gRPC services.

## 4. Code Style
- Language: Java 17+ (records/var allowed where appropriate), prefer immutability where possible (final fields, unmodifiable views).
- Naming:
  - Classes: PascalCase (e.g., AccountServiceGrpcService, CustomerRepository)
  - Methods/variables: camelCase
  - Packages: lower.case.with.dots
- Commenting:
  - Keep comments focused on why, not what. Avoid restating obvious code.
  - Use Javadoc for public APIs and complex behaviors.
- Error handling:
  - gRPC: Use io.grpc.Status to return appropriate status codes instead of generic exceptions.
  - Client calls: Catch StatusRuntimeException; consider structured logging and retries/backoff where needed.
- Formatting:
  - Consistent indentation (4 spaces), newline at EOF, no trailing whitespace.
  - Keep imports minimal and remove unused imports.

## 5. Common Patterns
- Dependency injection via constructor injection for easier testing and immutability.
- In-memory repositories for demo/dev; isolate persistence concerns behind repository interfaces if moving to a database later.
- Stream processing: Use streams for filtering/mapping, keep operations side-effect free.
- DTO/proto builders: Use builder patterns from generated proto types to enrich/compose responses (e.g., addAllAccounts).

## 6. Do's and Don'ts
- ✅ Do return appropriate gRPC Status codes (e.g., NOT_FOUND) on errors.
- ✅ Do keep configuration consistent and environment-specific via profiles if needed.
- ✅ Do keep services small and focused; aggregate data in service layer, not in controllers.
- ✅ Do write tests for both success and failure cases of gRPC methods and clients.
- ✅ Do use final for fields that shouldn’t change and prefer immutable collections in public APIs.
- ❌ Don’t use System.out.println for logging; use SLF4J with Spring logging.
- ❌ Don’t leak internal exceptions directly over gRPC; map to StatusRuntimeException.
- ❌ Don’t duplicate configuration files (yaml vs yml); keep one canonical format.
- ❌ Don’t place unrelated artifacts at repo root; keep modules self-contained.

## 7. Tools & Dependencies
- Spring Boot: Application framework and DI.
- gRPC + Protobuf: RPC and schema contract.
- Spring gRPC integration: Server and client wiring.
- JUnit 5: Testing framework.
- Maven Wrapper: Build tooling for consistent builds.
- Setup:
  - Build: ./mvnw -q -DskipTests package
  - Test: ./mvnw -q test
  - Run account-service: java -jar account-service/target/account-service-*.jar
  - Run customer-service: java -jar customer-service/target/customer-service-*.jar

## 8. Other Notes
- Configuration keys: Ensure gRPC configuration matches the starter in use. If using yidongnan/grpc-spring-boot-starter, prefer top-level `grpc.server.port`. If using Spring’s experimental grpc module, follow its documented key path. Keep consistency across services.
- Protos: When changing proto files, regenerate stubs via the Maven protobuf plugin. Keep service and message names stable to avoid breaking clients.
- Observability: Add structured logging and consider metrics/tracing for gRPC calls in production.
