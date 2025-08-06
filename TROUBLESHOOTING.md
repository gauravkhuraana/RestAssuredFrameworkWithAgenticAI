# Test Failure Troubleshooting Guide

## Common Issues and Solutions

### 1. NullPointerException in ConnectivityTest

**Symptoms:**
- `ConnectivityTest.testApiConnectivity:30 » NullPointer`
- `ConnectivityTest.testGetSingleUser:42 » NullPointer`

**Root Cause:**
The `ConnectivityTest` was not extending `BaseTest`, causing configuration initialization issues.

**Solution:**
✅ **Fixed**: The `ConnectivityTest` now extends `BaseTest` to inherit proper configuration setup.

### 2. "Operation failed after 3 attempts" Runtime Exceptions

**Symptoms:**
- `PostSmokeTest.testCreatePost:139 » Runtime Operation failed after 3 attempts`
- `UserSmokeTest.testGetAllUsers:38 » Runtime Operation failed after 3 attempts`

**Root Causes & Solutions:**

#### a) Network Connectivity Issues
- **Cause**: Intermittent network issues or API timeouts
- **Solution**: ✅ **Enhanced** retry mechanism with exponential backoff and better exception handling

#### b) Configuration Loading Issues
- **Cause**: Config files not found in CI/CD environments
- **Solution**: ✅ **Added** fallback mechanism to load `dev.properties` if environment-specific file not found

#### c) Resource Loading in GitHub Actions
- **Cause**: Different classpath in CI environment
- **Solution**: ✅ **Improved** resource loading with better error handling

### 3. Configuration Issues

**Enhanced ConfigManager:**
- Added fallback to `dev.properties` if environment-specific config not found
- Better null safety with default values
- Improved error logging

**Default Configuration Values:**
```properties
base.url=https://jsonplaceholder.typicode.com  # Fallback default
api.timeout=30000                              # 30 seconds
retry.attempts=3                               # 3 retry attempts
retry.delay=1000                               # 1 second base delay
```

### 4. GitHub Actions Specific Issues

**Environment Configuration:**
- Set `MAVEN_OPTS` for proper memory allocation
- Use `--batch-mode` and `--no-transfer-progress` for cleaner output
- Added timeout settings for forked processes
- Reduced parallel threads for CI stability

**Recommended CI Command:**
```bash
mvn test -Denv=dev -Psmoke -Dparallel.threads=2 --batch-mode --no-transfer-progress
```

## Monitoring and Debugging

### 1. Enable Debug Logging
Add to your test execution:
```bash
mvn test -Denv=dev -Psmoke -X  # Maven debug mode
```

### 2. Check Configuration Loading
Look for these log messages:
```
INFO  ConfigManager - Loaded configuration for environment: dev
INFO  RestAssuredConfig - Set base URI to: https://jsonplaceholder.typicode.com
```

### 3. Monitor Retry Attempts
Watch for retry logs:
```
WARN  RetryHandler - Operation failed on attempt 1/3: ConnectException - Connection timeout
DEBUG RetryHandler - Waiting 1000ms before retry
```

### 4. Verify Test Data
Ensure test data files are present:
- `src/main/resources/config/dev.properties`
- `src/test/resources/testdata/posts.json`
- `src/test/resources/testdata/users.json`

## Performance Tuning

### 1. Parallel Execution
- **Local Development**: Use `-Dparallel.threads=4`
- **CI/CD**: Use `-Dparallel.threads=2` for stability

### 2. Memory Settings
```bash
export MAVEN_OPTS="-Xmx2048m -Xms512m"
```

### 3. Timeout Configuration
- API timeout: 30 seconds (configurable via `api.timeout`)
- Process timeout: 300 seconds (5 minutes)
- Retry delay: 1 second with exponential backoff

## Best Practices

### 1. Test Design
- Always extend `BaseTest` for proper setup
- Use retry mechanisms for network calls
- Implement proper error handling

### 2. Configuration Management
- Use environment-specific properties files
- Provide sensible defaults
- Validate configuration on startup

### 3. CI/CD Pipeline
- Use matrix builds for different environments
- Upload test artifacts for debugging
- Set appropriate timeouts
- Monitor resource usage

## Quick Fixes

### If tests are still failing:

1. **Clean and rebuild:**
   ```bash
   mvn clean compile test-compile
   ```

2. **Run with single thread:**
   ```bash
   mvn test -Denv=dev -Psmoke -Dparallel.threads=1
   ```

3. **Check connectivity:**
   ```bash
   curl -I https://jsonplaceholder.typicode.com/posts
   ```

4. **Verify Java version:**
   ```bash
   java -version  # Should be Java 17
   ```

5. **Check Maven version:**
   ```bash
   mvn -version   # Should be 3.6+ 
   ```

## Contact Information

For additional support:
- Check logs in `logs/` directory
- Review test reports in `test-output/extent-reports/`
- Examine Allure reports in `target/allure-results/`
