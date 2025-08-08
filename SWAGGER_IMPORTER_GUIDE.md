# SwaggerImporter Usage Guide

## Overview
The SwaggerImporter utility allows you to import Swagger/OpenAPI specifications and automatically generate:
- Model classes (POJOs)
- Service classes for API calls
- Test templates with smoke tests
- API documentation

## How to Use SwaggerImporter

### 1. Import from URL
```java
import com.api.automation.utils.SwaggerImporter;

// Import from a public Swagger URL
SwaggerImporter.importSwaggerSpec("https://petstore3.swagger.io/api/v3/openapi.json", "com.api.automation");
```

### 2. Import from Local File
```java
import com.api.automation.utils.SwaggerImporter;

// Import from a local Swagger JSON file
SwaggerImporter.importSwaggerFile("path/to/swagger.json", "com.api.automation");
```

## What Gets Generated

### Generated Structure:
```
src/test/java/com/api/automation/tests/generated/
├── models/                    # Model classes (POJOs)
│   └── User.java
├── services/                  # Service classes for API calls
│   └── UsersService.java
└── smoke/                     # Generated smoke tests
    └── UsersSmokeTest.java

target/swagger-templates/
└── API_Documentation.md       # API documentation
```

### 1. Model Classes
Generated model classes include common properties and use Jackson annotations:

```java
@Data
public class User {
    @JsonProperty("id")
    private Long id;
    
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("description")
    private String description;
    
    @JsonProperty("active")
    private Boolean active;
    
    @JsonProperty("createdAt")
    private String createdAt;
    
    @JsonProperty("updatedAt")
    private String updatedAt;
}
```

### 2. Service Classes
Service classes extend BaseApiClient and provide methods for each API endpoint:

```java
public class UsersService extends BaseApiClient {
    public Response getAllUsers() {
        return getRequestSpec()
                .when()
                .get("/users");
    }
    
    public Response createUser() {
        return getRequestSpec()
                .when()
                .post("/users");
    }
    
    public Response getUserById() {
        return getRequestSpec()
                .when()
                .get("/users/{id}");
    }
}
```

### 3. Test Classes
Generated test classes extend BaseTest and include smoke tests:

```java
@Tag("smoke")
@Tag("generated")
public class UsersSmokeTest extends BaseTest {
    
    @Test
    @DisplayName("Get user by ID")
    void testGetUsersId() {
        logStep("Send GET request to /users/{id}");
        
        Response response = RestAssured.given()
                .when()
                .get("/users/{id}");
        
        logStep("Verify response");
        response.then()
                .statusCode(200);
        
        logVerification("Get user by ID test passed");
    }
}
```

## Example Usage

### Complete Example:
```java
public class SwaggerImportExample {
    
    public static void main(String[] args) {
        // Example 1: Import PetStore API
        SwaggerImporter.importSwaggerSpec(
            "https://petstore3.swagger.io/api/v3/openapi.json", 
            "com.api.automation"
        );
        
        // Example 2: Import local file
        SwaggerImporter.importSwaggerFile(
            "sample-swagger.json", 
            "com.api.automation"
        );
        
        // Example 3: Use generated service class
        UsersService usersService = new UsersService();
        Response response = usersService.getAllUsers();
        
        // Example 4: Run generated tests
        // The generated test classes can be run with JUnit 5
        // mvn test -Dtest=UsersSmokeTest
    }
}
```

## Advanced Features

### Custom Package Names
You can specify custom package names:

```java
SwaggerImporter.importSwaggerSpec(
    "https://api.example.com/swagger.json", 
    "com.mycompany.api"  // Custom package name
);
```

### Generated Files Locations
- **Models**: `src/test/java/{package}/tests/generated/models/`
- **Services**: `src/test/java/{package}/tests/generated/services/`
- **Tests**: `src/test/java/{package}/tests/generated/smoke/`
- **Documentation**: `target/swagger-templates/API_Documentation.md`

### Integration with Framework
The generated classes integrate seamlessly with the existing framework:

1. **Service classes** extend `BaseApiClient` for request configuration
2. **Test classes** extend `BaseTest` for logging and reporting
3. **Model classes** use Jackson for JSON serialization
4. **Generated tests** include proper tags for test categorization

## Tips for Usage

1. **Review Generated Code**: Always review and customize the generated code for your specific needs
2. **Add Validations**: The generated tests include basic status code checks - add more specific validations
3. **Customize Models**: Add specific properties based on your actual API schemas
4. **Use Service Classes**: Leverage the generated service classes in your custom tests
5. **Documentation**: Check the generated documentation for API endpoint details

## Running Generated Tests

```bash
# Run all generated tests
mvn test -Dtags=generated

# Run specific generated test class
mvn test -Dtest=UsersSmokeTest

# Run smoke tests including generated ones
mvn test -Dtags=smoke
```

This utility significantly speeds up API test development by providing a solid foundation of models, services, and test templates based on your API specification.
