lan: Update Framework for Bill Payment Practice API
Manually create comprehensive models and services for the Bill Payment API, remove legacy jsonplaceholder tests, and refactor to use Lombok best practices throughout.

Steps
Refactor existing models in models - update User.java, Post.java, and BaseModel.java to use @Data, @Builder, @NoArgsConstructor, @AllArgsConstructor Lombok annotations, remove manual getters/setters.

Create enum classes in src/main/java/com/api/automation/models/billpay/enums/ - implement BillerCategory, BillStatus, PaymentStatus, PaymentMethodType, KycStatus with @JsonValue for proper serialization.

Create POJO model classes in src/main/java/com/api/automation/models/billpay/ using Lombok:

Money.java (value, currency)
Address.java (line1, line2, city, state, postalCode, country)
Biller.java, BillerInput.java
Bill.java, BillInput.java
Payment.java, PaymentInput.java
PaymentMethod.java, PaymentMethodInput.java
BillPayUser.java, UserInput.java
UploadedFile.java
ApiResponse<T>.java (generic wrapper for {success, data, meta})
PaginationMeta.java, ErrorResponse.java
Create service classes in src/main/java/com/api/automation/services/billpay/ extending BaseApiClient:

HealthService.java - health check endpoints
AuthService.java - OAuth2 token endpoint, /auth/me
BillerService.java - full CRUD + categories
BillService.java - CRUD + summary, overdue, fetch
PaymentService.java - CRUD + stats, refund, cancel
PaymentMethodService.java - CRUD + types, set-default
BillPayUserService.java - CRUD + bills, payment-methods, transactions, verify-kyc
FileService.java - upload, upload-multiple, list, get, delete with multipart support
Update AuthHandler in AuthHandler.java - add Cookie session auth (session_id=demo-session-abc123) and OAuth2 client credentials flow (client_id/client_secret token acquisition).

Update configuration - modify config.properties or ConfigManager.java to use base URL https://billpay-api.gauravkhurana-practice-api.workers.dev with demo credentials.

Add multipart support in BaseApiClient.java - add withMultiPart(String name, File file) method for file upload endpoints.

Create test classes in src/test/java/com/api/automation/tests/billpay/:

HealthCheckTest.java - connectivity verification
AuthenticationTest.java - all 6 auth methods
BillerTests.java - CRUD operations
BillTests.java - CRUD + business flows
PaymentFlowTests.java - end-to-end payment scenarios
FileUploadTests.java - multipart upload tests
SerializationTests.java - validate serialization/deserialization patterns
Delete legacy files - remove PostService.java, UserService.java, PostSmokeTest.java, UserSmokeTest.java, and any jsonplaceholder-specific tests.

Further Considerations
TypeReference Pattern - For generic deserialization of ApiResponse<List<Biller>>, recommend creating helper methods in JsonUtils like parseApiResponse(String json, TypeReference<T> type). Confirm this approach?

Test Data Strategy - Should we use the demo credentials provided by the API (demo-api-key-123, demo:password123) directly in config, or set up a test data factory pattern for more flexibility?