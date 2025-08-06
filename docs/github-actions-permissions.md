# GitHub Actions Permissions and Test Reporting

## Issue: dorny/test-reporter Permission Error

### Problem
```
Error: HttpError: Resource not accessible by integration
```

This error occurs when the `dorny/test-reporter@v1` action doesn't have sufficient permissions to create check runs in the repository.

### Root Cause
The GitHub token used by actions needs specific permissions to create check runs and write to pull requests.

## Solutions

### Solution 1: Add Permissions to Workflow (Recommended)
Add these permissions to your workflow file:

```yaml
permissions:
  contents: read
  actions: read
  checks: write        # Required for check runs
  pull-requests: write # Required for PR comments
```

### Solution 2: Repository Settings
1. Go to **Settings** → **Actions** → **General**
2. Under **Workflow permissions**, select:
   - ✅ **Read and write permissions**
   - ✅ **Allow GitHub Actions to create and approve pull requests**

### Solution 3: Use Alternative Workflow
We've created `ci-simple.yml` that doesn't use external test reporters and relies on:
- GitHub's built-in step summaries
- Artifact uploads for test results
- Manual parsing of test results

## Current Workflow Status

### Primary Workflow (`ci.yml`)
- ✅ Updated with proper permissions
- ✅ Uses `dorny/test-reporter@v1.9.1` (latest version)
- ✅ Has fallback test summary
- ✅ Continues on test reporter errors

### Backup Workflow (`ci-simple.yml`)
- ✅ No external dependencies
- ✅ Built-in test result parsing
- ✅ Detailed step summaries
- ✅ Artifact uploads for manual review

## Usage Recommendations

1. **For repositories with admin access**: Use `ci.yml` after enabling repository-level permissions
2. **For repositories with limited permissions**: Use `ci-simple.yml`
3. **For maximum compatibility**: Both workflows can coexist

## Test Report Formats

Both workflows generate:
- **Surefire Reports**: `target/surefire-reports/*.xml`
- **Allure Reports**: `target/allure-results/`
- **Extent Reports**: `test-output/extent-reports/`
- **Log Files**: `logs/`

## Monitoring

Check the **Actions** tab in your repository to see:
- ✅ Test execution status
- 📊 Test result summaries in step outputs
- 📁 Downloadable artifacts with detailed reports
