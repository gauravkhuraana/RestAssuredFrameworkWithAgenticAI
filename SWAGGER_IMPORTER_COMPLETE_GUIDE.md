# 🚀 SwaggerImporter - Complete Usage Guide

## What is SwaggerImporter?

SwaggerImporter is a powerful utility that reads OpenAPI/Swagger specifications and automatically generates:
- 📦 **Model Classes** - Java POJOs for API data structures
- 🔧 **Service Classes** - Client methods for API operations
- 🧪 **Test Templates** - Smoke tests for API endpoints
- 📚 **Documentation** - Markdown API reference

## ✨ Key Benefits

1. **⚡ Speed**: Generate hundreds of lines of code instantly
2. **🎯 Accuracy**: Code matches API specification exactly  
3. **🔄 Consistency**: All generated code follows same patterns
4. **🛠️ Maintainability**: Easy to regenerate when API changes
5. **📚 Documentation**: Auto-generates API reference

## 🎯 How to Use SwaggerImporter

### Method 1: Import from URL
```java
SwaggerImporter.importSwaggerSpec("https://api.example.com/swagger.json", "com.api.automation");
```

### Method 2: Import from Local File
```java
SwaggerImporter.importSwaggerFile("swagger-spec.json", "com.api.automation");
```

## 📁 What Gets Generated

### Generated Directory Structure:
```
src/test/java/com/api/automation/tests/generated/
├── models/           # Data model classes
│   └── User.java
├── services/         # API client classes
│   └── UsersService.java
└── smoke/           # Test templates
    └── UsersSmokeTest.java

target/swagger-templates/
└── API_Documentation.md
```

## 🔧 Using Generated Service Classes

```java
// 1. Create service instance
UsersService usersService = new UsersService();

// 2. Call API methods
Response response = usersService.getAllUsers();

// 3. Verify response
response.then().statusCode(200);
```

### Service Class Features:
- ✅ Extends `BaseApiClient` for configuration
- ✅ Built-in logging for each API call
- ✅ Returns REST Assured `Response` objects
- ✅ Proper error handling

## 📦 Using Generated Model Classes

```java
// 1. Create model instance
User user = new User();

// 2. Set properties
user.setName("John Doe");
user.setActive(true);

// 3. Use in API calls
Response response = usersService.createUser(user);
```

### Model Class Features:
- ✅ Uses Lombok `@Data` for getters/setters
- ✅ Jackson `@JsonProperty` annotations
- ✅ Proper field types from schema
- ✅ Null-safe design

## 🧪 Using Generated Test Classes

```bash
# Run specific generated test
mvn test -Dtest=UsersSmokeTest

# Run all generated smoke tests
mvn test -Dtags=smoke,generated
```

### Test Class Features:
- ✅ Extends `BaseTest` for setup/teardown
- ✅ Proper JUnit 5 annotations
- ✅ Tagged for easy execution
- ✅ Basic validation templates

## 📝 Sample Swagger Specification

Here's what a basic Swagger spec looks like:

```json
{
  "openapi": "3.0.0",
  "info": {
    "title": "Sample API",
    "version": "1.0.0"
  },
  "paths": {
    "/users": {
      "get": {
        "summary": "Get all users",
        "responses": {
          "200": {
            "description": "Success"
          }
        }
      }
    }
  },
  "components": {
    "schemas": {
      "User": {
        "type": "object",
        "properties": {
          "id": {"type": "integer"},
          "name": {"type": "string"}
        }
      }
    }
  }
}
```

## 🛠️ Practical Examples

### Example 1: Basic Usage
```java
@Test
void testBasicSwaggerImport() {
    // Import from file
    SwaggerImporter.importSwaggerFile("api-spec.json", "com.api.automation");
    
    // Use generated service
    UsersService service = new UsersService();
    Response response = service.getAllUsers();
    
    // Verify
    response.then().statusCode(200);
}
```

### Example 2: Real API Testing
```java
@Test 
void testRealApiWithGeneratedCode() {
    // Generate from live API
    SwaggerImporter.importSwaggerSpec("https://petstore.swagger.io/v2/swagger.json", "com.petstore");
    
    // Use generated code
    PetService petService = new PetService();
    Response response = petService.findPetsByStatus("available");
    
    // Advanced validation
    response.then()
        .statusCode(200)
        .body("size()", greaterThan(0))
        .body("[0].status", equalTo("available"));
}
```

## 🔄 Best Practices

### 1. Organization
- Use meaningful package names: `com.api.automation.ecommerce`
- Group related APIs: `com.api.automation.users`, `com.api.automation.orders`

### 2. Regeneration
- Keep original swagger files for reference
- Regenerate when API specs change
- Version control generated files for tracking

### 3. Customization
- Extend generated services for complex operations
- Add custom validations to generated tests
- Use generated models as base classes

### 4. Integration
```java
// In your test classes
@Test
void testUserWorkflow() {
    UsersService users = new UsersService();
    OrdersService orders = new OrdersService();
    
    // Create user
    User user = new User();
    user.setName("Test User");
    Response userResponse = users.createUser(user);
    
    // Create order for user
    Order order = new Order();
    order.setUserId(userResponse.jsonPath().getLong("id"));
    Response orderResponse = orders.createOrder(order);
    
    // Verify workflow
    orderResponse.then().statusCode(201);
}
```

## 🚫 Common Pitfalls to Avoid

1. **Don't edit generated files directly** - They'll be overwritten
2. **Always specify package names** - Prevents naming conflicts
3. **Check swagger spec validity** - Invalid specs produce broken code
4. **Handle large specs carefully** - May generate many files

## 🎓 Next Steps

Now that you understand SwaggerImporter:

1. ✅ Try importing your own API specifications
2. ✅ Generate code for multiple APIs
3. ✅ Integrate generated code into existing tests
4. ✅ Create custom workflows using generated services

## 💡 Pro Tips

- **Tip 1**: Use SwaggerImporter early in development to establish patterns
- **Tip 2**: Combine multiple API imports for comprehensive test suites
- **Tip 3**: Use generated documentation for team onboarding
- **Tip 4**: Automate regeneration in CI/CD pipelines

---

🎉 **You're now fully equipped to use SwaggerImporter effectively!**

The generated code integrates seamlessly with your framework and follows all established patterns. Happy testing! 🚀
