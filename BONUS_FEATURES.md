# Bonus Features Guide

This document describes the additional bonus features that have been implemented in the REST Assured API Automation Framework.

## 🚀 Available Bonus Features

### ✅ **1. JSONPath for Complex JSON Response Validation**

Advanced JSON validation utilities for complex response analysis.

**Location:** `src/main/java/com/api/automation/utils/JsonPathUtils.java`

**Features:**
- Extract values using JSONPath expressions
- Validate JSON structure and field existence
- Count array elements
- Support for nested object validation

**Usage Example:**
```java
// Extract specific values
String username = JsonPathUtils.extractString(response, "$.data.user.name");
Integer userId = JsonPathUtils.extractInt(response, "$.data.user.id");

// Validate structure
boolean hasEmail = JsonPathUtils.pathExists(response, "$.data.user.email");

// Count elements
int totalUsers = JsonPathUtils.countElements(response, "$.data.users");
```

### ✅ **2. Data-Driven Testing with CSV/Excel Support**

Comprehensive data management utilities for test data handling.

**Location:** `src/main/java/com/api/automation/utils/TestDataUtils.java`

**Features:**
- Read CSV files with header mapping
- Read Excel files (.xlsx) with sheet selection
- JSON test data support
- Random test data generation

**Usage Example:**
```java
// Read CSV data
List<Map<String, String>> csvData = TestDataUtils.readCsvTestData("users.csv");

// Read Excel data
List<Map<String, Object>> excelData = TestDataUtils.readExcelTestData("testdata.xlsx", "Users");

// Generate random data
Map<String, Object> randomData = TestDataUtils.generateRandomTestData();
```

### ✅ **3. Sample Validations (Status Code, Headers, Response Body)**

Built-in validation examples throughout the framework.

**Features:**
- Status code validation patterns
- Content-type header validation
- Response body structure validation
- Custom assertion helpers

**Usage Example:**
```java
response.then()
    .statusCode(200)
    .contentType("application/json")
    .body("id", equalTo(1))
    .body("email", matchesPattern(".*@.*\\..*"));
```

### ❌ **4. Postman/Swagger Integration** ✨ **NEW**

Import existing API specifications and generate test templates.

**Location:** 
- `src/main/java/com/api/automation/utils/SwaggerImporter.java`
- `src/main/java/com/api/automation/utils/PostmanImporter.java`

#### **Swagger/OpenAPI Import**

**Features:**
- Import from Swagger/OpenAPI URLs
- Import from local specification files
- Generate model classes from schemas
- Generate service classes from paths
- Generate test templates
- Create API documentation

**Usage Example:**
```java
// Import from URL
SwaggerImporter.importSwaggerSpec("https://api.example.com/swagger.json", "com.api.automation");

// Import from file
SwaggerImporter.importSwaggerFile("src/test/resources/api-spec.json", "com.api.automation");
```

**Generated Files:**
- `src/test/java/com/api/automation/tests/generated/models/` - Model classes
- `src/test/java/com/api/automation/tests/generated/services/` - Service classes  
- `src/test/java/com/api/automation/tests/generated/smoke/` - Test templates
- `target/swagger-templates/API_Documentation.md` - API documentation

#### **Postman Collection Import**

**Features:**
- Import from Postman collection URLs
- Import from local collection files
- Generate test classes from requests
- Support for folders and nested requests
- Extract headers and request bodies
- Create collection documentation

**Usage Example:**
```java
// Import from Postman URL
PostmanImporter.importPostmanCollection("https://api.postman.com/collections/your-id", "com.api.automation");

// Import from file
PostmanImporter.importPostmanFile("src/test/resources/collection.json", "com.api.automation");
```

**Generated Files:**
- `src/test/java/com/api/automation/tests/generated/smoke/` - Test classes
- `target/postman-templates/Postman_Collection_Documentation.md` - Collection docs

### ❌ **5. cURL and HAR Generation for Debugging** ✨ **NEW**

Advanced debugging utilities for failed test analysis.

**Location:**
- `src/main/java/com/api/automation/utils/CurlGenerator.java`
- `src/main/java/com/api/automation/utils/HarGenerator.java`
- `src/main/java/com/api/automation/utils/DebugUtils.java`

#### **cURL Command Generation**

**Features:**
- Automatic cURL generation for failed requests
- Manual cURL generation from request specs
- Executable shell scripts with debugging options
- Support for all HTTP methods and headers

**Usage Example:**
```java
// Enable for specific test
CurlGenerator curlGen = new CurlGenerator().enableForTest("myTest");
RestAssured.filters(curlGen);

// Manual generation
String curl = CurlGenerator.generateSimpleCurl("POST", "https://api.example.com/users", headers, body);
```

**Generated Files:**
- `target/curl-commands/curl_testName_statusCode_timestamp.sh`

#### **HAR File Generation**

**Features:**
- HTTP Archive format for browser dev tools
- Complete request/response capture
- Timing information
- Import into Chrome/Firefox dev tools

**Usage Example:**
```java
// Enable for test session
HarGenerator harGen = new HarGenerator().enableForTest("myTest");
RestAssured.filters(harGen);

// Manual generation
HarGenerator.saveAsHar("debug_session", requestSpec, response);
```

**Generated Files:**
- `target/har-files/har_testName_timestamp.har`

#### **Integrated Debug Utilities**

**Features:**
- Comprehensive debug reports
- Automatic artifact generation on test failure
- Troubleshooting suggestions based on status codes
- Session summaries

**Usage Example:**
```java
// Enable debug mode
DebugUtils.enableDebugForTest("myFailingTest");

// Debug artifacts generated automatically on failure
// Manual generation
DebugUtils.generateDebugArtifacts(requestSpec, response, "testName", exception);
```

**Generated Files:**
- `target/debug-output/curl/` - cURL commands
- `target/debug-output/har/` - HAR files  
- `target/debug-output/reports/` - Debug reports
- `target/debug-output/debug_session_summary.md` - Session summary

## 📖 How to Use the Bonus Features

### **1. Running the Example Test**

A comprehensive example test demonstrates all bonus features:

```bash
mvn test -Dtest=BonusFeaturesExampleTest
```

**Location:** `src/test/java/com/api/automation/tests/examples/BonusFeaturesExampleTest.java`

### **2. Importing API Specifications**

#### **From Swagger/OpenAPI:**
```java
@Test
void importSwaggerSpec() {
    SwaggerImporter.importSwaggerSpec("https://petstore.swagger.io/v2/swagger.json", "com.api.automation");
}
```

#### **From Postman Collection:**
```java
@Test  
void importPostmanCollection() {
    PostmanImporter.importPostmanFile("src/test/resources/my-collection.json", "com.api.automation");
}
```

### **3. Enabling Debug Features**

#### **For Individual Tests:**
```java
@BeforeEach
void enableDebug() {
    DebugUtils.enableDebugForTest(testInfo.getDisplayName());
    
    CurlGenerator curlGen = new CurlGenerator().enableForTest(testInfo.getDisplayName());
    HarGenerator harGen = new HarGenerator().enableForTest(testInfo.getDisplayName());
    
    RestAssured.filters(curlGen, harGen);
}
```

#### **For Failed Tests Only:**
```java
@AfterEach
void handleFailure(TestInfo testInfo) {
    if (/* test failed */) {
        DebugUtils.generateDebugArtifacts(requestSpec, response, testInfo.getDisplayName(), exception);
    }
}
```

### **4. Data-Driven Testing Setup**

#### **CSV Test Data:**
```csv
# src/test/resources/testdata/users.csv
username,email,role
john.doe,john@example.com,admin
jane.smith,jane@example.com,user
```

```java
@ParameterizedTest
@MethodSource("getUserData")
void testWithCsvData(Map<String, String> userData) {
    // Test implementation
}

static Stream<Map<String, String>> getUserData() {
    return TestDataUtils.readCsvTestData("testdata/users.csv").stream();
}
```

#### **Excel Test Data:**
```java
@Test
void testWithExcelData() {
    List<Map<String, Object>> testData = TestDataUtils.readExcelTestData("testdata/users.xlsx", "TestData");
    
    for (Map<String, Object> data : testData) {
        // Use data in test
    }
}
```

## 🔧 Configuration

### **Maven Dependencies Added**

The following dependencies were added to support the bonus features:

```xml
<!-- Swagger/OpenAPI Support -->
<dependency>
    <groupId>io.swagger.parser.v3</groupId>
    <artifactId>swagger-parser</artifactId>
    <version>2.1.16</version>
</dependency>

<!-- HTTP Client for Postman Collection Import -->
<dependency>
    <groupId>org.apache.httpcomponents.client5</groupId>
    <artifactId>httpclient5</artifactId>
    <version>5.2.1</version>
</dependency>
```

### **Output Directories**

The bonus features create the following output directories:

```
target/
├── curl-commands/          # Generated cURL scripts
├── har-files/              # Generated HAR files
├── debug-output/           # Debug artifacts
│   ├── curl/
│   ├── har/
│   ├── reports/
│   └── debug_session_summary.md
├── swagger-templates/      # Swagger import outputs
└── postman-templates/      # Postman import outputs

src/test/java/com/api/automation/tests/generated/
├── models/                 # Generated model classes
├── services/               # Generated service classes
└── smoke/                  # Generated test templates
```

## 🎯 Best Practices

### **1. Debug Features**
- Enable debug mode only for failing tests to avoid clutter
- Review generated cURL commands to reproduce issues locally
- Import HAR files into browser dev tools for detailed analysis

### **2. API Import**
- Use imports during initial setup to bootstrap test suites
- Review and customize generated test templates
- Update imports when API specifications change

### **3. Data-Driven Testing**
- Organize test data files in `src/test/resources/testdata/`
- Use CSV for simple tabular data
- Use Excel for complex datasets with multiple sheets
- Use JSON for hierarchical test data

### **4. JSONPath Validation**
- Use specific JSONPath expressions for better error messages
- Combine with standard REST Assured validations
- Cache extracted values for reuse in multiple assertions

## 🚨 Troubleshooting

### **Common Issues**

1. **Swagger Import Fails**
   - Verify the Swagger URL is accessible
   - Check if the specification is valid JSON/YAML
   - Ensure network connectivity

2. **Postman Import Fails**
   - Verify collection format is valid JSON
   - Check if file paths are correct
   - Ensure collection contains request objects

3. **Debug Files Not Generated**
   - Verify debug mode is enabled before test execution
   - Check if target directories have write permissions
   - Ensure filters are added to RestAssured

4. **Data Files Not Found**
   - Verify file paths are relative to `src/test/resources/`
   - Check file format matches expected structure
   - Ensure files are included in test classpath

### **Getting Help**

- Check the example test: `BonusFeaturesExampleTest.java`
- Review generated documentation in `target/` directories
- Enable verbose logging for detailed error messages
- Check the main `TROUBLESHOOTING.md` for general issues

## 🎉 Summary

The bonus features significantly enhance the framework's capabilities:

✅ **JSONPath validation** - Advanced JSON response analysis  
✅ **Data-driven testing** - CSV/Excel test data support  
✅ **Sample validations** - Built-in validation patterns  
✅ **Swagger/OpenAPI import** - Generate tests from API specs  
✅ **Postman import** - Generate tests from collections  
✅ **cURL generation** - Reproduce requests for debugging  
✅ **HAR generation** - Browser-compatible request archives  
✅ **Debug utilities** - Comprehensive failure analysis  

These features make the framework more powerful for API testing, debugging, and maintenance scenarios.
