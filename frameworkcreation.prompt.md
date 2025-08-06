Create a modern, scalable, and modular API automation framework in Java (Java 17) using Rest Assured and JUnit as the test runner.

The framework should include the following features:

✅ Supported HTTP Methods
Implement API request support for GET, POST, PUT, DELETE, and PATCH methods.

✅ Authentication Support
Support multiple authentication types:
• Bearer token
• API key
• Basic auth
• Support for setting headers dynamically from properties files or environment.

✅ Serialization / Deserialization
Use POJOs and Jackson/Gson libraries for request payload creation and response parsing.

✅ Framework Structure
Modular folder structure with separation of concerns:
• config, utils, testdata, models, tests, reporting, etc.
• Environment configuration management via .properties or .yaml files.
• Test data management via JSON.

✅ Execution & CI/CD
• Compatible with GitHub Actions and Azure DevOps Pipelines.
• Configurable test execution using Maven profiles or environment tags.

✅ Reporting
• Integrate Extent Reports or Allure Reports for detailed HTML reports.
• Include request/response logs, steps (for failure scenarios).

✅ Logging & Debugging
• Integrate log4j2 or SLF4J for detailed logs.
• Add logs for request/response, headers, and exceptions.
• Enable easy debugging and verbose logs when needed.

✅ Error Handling & Retry Logic
• Add a robust exception handling mechanism.
• Implement retry logic for flaky requests using a custom annotation or Rest Assured filters.

✅ Parallel Execution
Enable parallel execution of tests using JUnit parallel configuration or Maven Surefire plugin.

✅ Docker Support
• Provide a Dockerfile to run the test framework in containers.
• Use a docker-compose.yml file (optional) for service dependencies like mock servers or test APIs.

✅ Test Suites & Tagging
• Organize tests into suites (e.g., smoke, regression, auth tests).
• Use tags/annotations for running specific groups.

✅ Documentation
Provide a README.md with:
• Prerequisites
• Project structure explanation
• How to add a new test case
• How to run tests locally, in Docker, or via pipeline
• Sample command-line usages (e.g., mvn clean test -Denv=qa)

✅ Bonus (Optional Enhancements)
• Integrate with Postman or Swagger to import existing APIs if needed.
• Include utility to generate curl or HAR from failed tests for debugging.
• Add sample validations like status code, headers, response body match.
• Use JSONPath or similar libraries for complex JSON response validation.
• Add data-driven testing capabilities with CSV/Excel support.