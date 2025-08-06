# REST Assured API Automation Framework

A modern, scalable, and modular API automation framework built with Java 17, REST Assured, and JUnit 5. This framework provides comprehensive API testing capabilities with support for multiple environments, authentication methods, reporting, and CI/CD integration.

![Java](https://img.shields.io/badge/Java-17-orange)
![REST Assured](https://img.shields.io/badge/REST%20Assured-5.3.2-green)
![JUnit](https://img.shields.io/badge/JUnit-5.10.0-blue)
![Maven](https://img.shields.io/badge/Maven-3.x-red)
![Docker](https://img.shields.io/badge/Docker-Supported-blue)

## 🚀 Features

### ✅ HTTP Methods Support
- **GET, POST, PUT, DELETE, PATCH** methods
- Comprehensive request/response handling
- Custom headers and parameters support

### ✅ Authentication Support
- **Bearer Token** authentication
- **Basic Authentication** (username/password)
- **API Key** authentication
- Dynamic header configuration
- Environment-specific auth configuration

### ✅ Serialization/Deserialization
- **Jackson** and **Gson** libraries support
- POJO-based request/response handling
- Automatic JSON conversion
- Custom serialization configurations

### ✅ Modular Framework Structure
```
src/
├── main/java/com/api/automation/
│   ├── auth/           # Authentication handlers
│   ├── client/         # Base API client
│   ├── config/         # Configuration management
│   ├── models/         # POJO models
│   ├── reporting/      # Reporting utilities
│   ├── retry/          # Retry mechanisms
│   ├── services/       # API service classes
│   └── utils/          # Utility classes
├── main/resources/
│   ├── config/         # Environment configurations
│   └── log4j2.xml      # Logging configuration
└── test/
    ├── java/com/api/automation/tests/
    │   ├── base/       # Base test classes
    │   ├── smoke/      # Smoke tests
    │   ├── regression/ # Regression tests
    │   └── suites/     # Test suites
    └── resources/testdata/ # Test data files
```

### ✅ Environment Configuration
- **Properties-based** configuration (dev, qa, prod)
- **Environment variables** support
- Dynamic configuration loading
- Secure credential management

### ✅ CI/CD Integration
- **GitHub Actions** workflow
- **Azure DevOps** pipeline
- Parallel test execution
- Automated reporting
- Security scanning

### ✅ Comprehensive Reporting
- **Extent Reports** with HTML output
- **Allure Reports** with detailed analytics
- Request/response logging
- Screenshot capture (when applicable)
- Step-by-step execution logs

### ✅ Logging & Debugging
- **Log4j2** integration
- Request/response logging
- Separate log files for different components
- Configurable log levels
- Console and file output

### ✅ Error Handling & Retry Logic
- Robust exception handling
- Configurable retry mechanisms
- Custom retry annotations
- Failure analysis and reporting

### ✅ Parallel Execution
- JUnit 5 parallel execution
- Maven Surefire configuration
- Thread-safe implementations
- Configurable thread counts

### ✅ Docker Support
- **Dockerfile** for containerized execution
- **Docker Compose** for service orchestration
- Volume mounting for reports
- Environment variable support

### ✅ Test Organization
- **Tag-based** test execution (@smoke, @regression)
- **Test suites** for different test types
- **Data-driven** testing support
- **JSON/CSV/Excel** test data support

## 📋 Prerequisites

- **Java 17** or higher
- **Maven 3.6+**
- **Docker** (optional, for containerized execution)
- **Git** for version control

## 🛠️ Setup Instructions

### 1. Clone the Repository
```bash
git clone <repository-url>
cd RestAssuredFrameworkWithAgenticAI
```

### 2. Install Dependencies
```bash
mvn clean install
```

### 3. Configure Environment
Create environment-specific configuration files in `src/main/resources/config/`:
- `dev.properties`
- `qa.properties`
- `prod.properties`

### 4. Set Environment Variables (Optional)
```bash
export API_TOKEN="your_bearer_token"
export API_USERNAME="your_username"
export API_PASSWORD="your_password"
export API_KEY="your_api_key"
```

## 🏃‍♂️ Running Tests

### Local Execution

#### Run All Tests
```bash
mvn clean test
```

#### Run Specific Environment
```bash
mvn clean test -Denv=qa
```

#### Run Smoke Tests
```bash
mvn clean test -Psmoke
```

#### Run Regression Tests
```bash
mvn clean test -Pregression
```

#### Run Specific Test Class
```bash
mvn clean test -Dtest=UserSmokeTest
```

#### Run Tests with Custom Parameters
```bash
mvn clean test -Denv=qa -Psuite=smoke -Dparallel.threads=6
```

### Docker Execution

#### Build Docker Image
```bash
docker build -t api-automation-tests .
```

#### Run Tests in Container
```bash
docker run --rm \
  -e env=dev \
  -e API_TOKEN=your_token \
  -v $(pwd)/test-output:/app/test-output \
  api-automation-tests
```

#### Using Docker Compose
```bash
# Run smoke tests
docker-compose up api-tests

# Run with custom environment
docker-compose run -e env=qa api-tests mvn clean test -Denv=qa -Pregression
```

## 📊 Reporting

### Extent Reports
- **Location**: `test-output/extent-reports/`
- **Format**: HTML with interactive features
- **Features**: Request/response details, screenshots, step logging

### Allure Reports
```bash
# Generate Allure report
mvn allure:report

# Serve Allure report
mvn allure:serve
```

### Logs
- **Application Logs**: `logs/api-tests.log`
- **Request/Response Logs**: `logs/request-response.log`

## 🧪 Adding New Tests

### 1. Create Test Data
Add test data in `src/test/resources/testdata/`:
```json
{
  "name": "Test User",
  "username": "testuser",
  "email": "test@example.com"
}
```

### 2. Create Test Class
```java
@Tag("smoke")
@Epic("User Management")
@Feature("User API")
public class NewUserTest extends BaseTest {
    
    private final UserService userService = new UserService();
    
    @Test
    @DisplayName("Test New Functionality")
    @Description("Test description")
    @Severity(SeverityLevel.CRITICAL)
    void testNewFunctionality() {
        logStep("Perform test step");
        
        Response response = userService.getAllUsers();
        
        logStep("Verify response");
        response.then()
                .statusCode(200)
                .body("size()", greaterThan(0));
        
        logVerification("Verification successful");
    }
}
```

### 3. Create Service Class (if needed)
```java
public class NewService extends BaseApiClient {
    
    public Response getResource() {
        return get("/resource");
    }
    
    public Response createResource(Object resource) {
        return withBody(resource).post("/resource");
    }
}
```

## 🔧 Configuration

### Environment Configuration
Update `src/main/resources/config/{env}.properties`:
```properties
base.url=https://api.example.com
api.timeout=30000
auth.type=bearer
auth.token=${API_TOKEN}
```

### Maven Profiles
```xml
<profile>
    <id>custom-env</id>
    <properties>
        <env>custom</env>
        <suite>custom</suite>
    </properties>
</profile>
```

### JUnit Configuration
Create `junit-platform.properties`:
```properties
junit.jupiter.execution.parallel.enabled=true
junit.jupiter.execution.parallel.mode.default=concurrent
junit.jupiter.execution.parallel.config.strategy=dynamic
```

## 🐳 Docker Configuration

### Environment Variables
```bash
ENV_NAME=dev
API_TOKEN=your_token
API_USERNAME=your_username
API_PASSWORD=your_password
MAVEN_OPTS=-Xmx1024m
```

### Volume Mounts
```bash
-v $(pwd)/test-output:/app/test-output
-v $(pwd)/logs:/app/logs
-v $(pwd)/allure-results:/app/allure-results
```

## 🔄 CI/CD Integration

### GitHub Actions
- Triggered on push/PR to main/develop
- Matrix strategy for multiple environments
- Artifact upload for reports
- Security scanning with Trivy

### Azure DevOps
- Multi-stage pipeline
- Parallel job execution
- Test result publishing
- Docker container testing

### Pipeline Configuration
```yaml
# Environment variables
API_TOKEN: ${{ secrets.API_TOKEN }}
API_USERNAME: ${{ secrets.API_USERNAME }}
API_PASSWORD: ${{ secrets.API_PASSWORD }}
API_KEY: ${{ secrets.API_KEY }}
```

## 📈 Best Practices

### Test Design
- Use Page Object Model concepts for API services
- Implement data-driven testing for comprehensive coverage
- Use meaningful test names and descriptions
- Add appropriate tags for test categorization

### Error Handling
- Implement retry mechanisms for flaky tests
- Use custom exceptions for better error reporting
- Log all API interactions for debugging

### Data Management
- Use external data files (JSON, CSV, Excel)
- Implement data builders for complex objects
- Use random data generation where appropriate

### Reporting
- Log each test step for better traceability
- Include request/response details in reports
- Use screenshots and attachments when relevant

## 🔍 Troubleshooting

### Common Issues

#### Authentication Failures
```bash
# Check environment variables
echo $API_TOKEN

# Verify configuration
cat src/main/resources/config/dev.properties
```

#### Test Failures
```bash
# Check logs
tail -f logs/api-tests.log

# Run with debug logging
mvn clean test -Dlog.level=DEBUG
```

#### Report Generation Issues
```bash
# Clear previous reports
rm -rf test-output/*

# Regenerate reports
mvn clean test allure:report
```

## 📚 Additional Resources

- [REST Assured Documentation](https://rest-assured.io/)
- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [Allure Framework](https://docs.qameta.io/allure/)
- [Extent Reports](https://www.extentreports.com/)
- [Docker Documentation](https://docs.docker.com/)

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Ensure all tests pass
6. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🆘 Support

For questions and support:
- Create an issue in the repository
- Contact the development team
- Check the troubleshooting section

---

**Happy Testing! 🚀**
