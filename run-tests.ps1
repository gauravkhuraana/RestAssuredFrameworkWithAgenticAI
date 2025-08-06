# API Test Execution Script for PowerShell
# This script provides easy ways to run different test suites

param(
    [string]$Environment = "dev",
    [string]$Suite = "smoke", 
    [int]$Threads = 4,
    [switch]$Clean,
    [switch]$Reports,
    [switch]$Docker,
    [switch]$Help
)

function Show-Help {
    Write-Host "🚀 REST Assured API Automation Framework" -ForegroundColor Blue
    Write-Host "========================================" -ForegroundColor Blue
    Write-Host ""
    Write-Host "Usage: .\run-tests.ps1 [OPTIONS]" -ForegroundColor Green
    Write-Host ""
    Write-Host "Parameters:" -ForegroundColor Yellow
    Write-Host "  -Environment ENV     Environment to run tests against (dev, qa, prod)" -ForegroundColor White
    Write-Host "  -Suite SUITE         Test suite to run (smoke, regression, all)" -ForegroundColor White
    Write-Host "  -Threads NUM         Number of parallel threads (default: 4)" -ForegroundColor White
    Write-Host "  -Clean               Clean before running tests" -ForegroundColor White
    Write-Host "  -Reports             Generate and open reports after execution" -ForegroundColor White
    Write-Host "  -Docker              Run tests in Docker container" -ForegroundColor White
    Write-Host "  -Help                Show this help message" -ForegroundColor White
    Write-Host ""
    Write-Host "Examples:" -ForegroundColor Yellow
    Write-Host "  .\run-tests.ps1 -Environment qa -Suite smoke" -ForegroundColor Gray
    Write-Host "  .\run-tests.ps1 -Environment prod -Suite regression -Threads 8" -ForegroundColor Gray
    Write-Host "  .\run-tests.ps1 -Docker -Environment dev -Suite smoke" -ForegroundColor Gray
    Write-Host "  .\run-tests.ps1 -Clean -Reports -Environment qa -Suite all" -ForegroundColor Gray
}

if ($Help) {
    Show-Help
    exit 0
}

Write-Host "🚀 REST Assured API Automation Framework" -ForegroundColor Blue
Write-Host "========================================" -ForegroundColor Blue
Write-Host ""

# Validate environment
if ($Environment -notin @("dev", "qa", "prod")) {
    Write-Host "Error: Invalid environment '$Environment'. Must be dev, qa, or prod." -ForegroundColor Red
    exit 1
}

# Validate suite
if ($Suite -notin @("smoke", "regression", "all")) {
    Write-Host "Error: Invalid test suite '$Suite'. Must be smoke, regression, or all." -ForegroundColor Red
    exit 1
}

Write-Host "Configuration:" -ForegroundColor Blue
Write-Host "Environment: $Environment" -ForegroundColor White
Write-Host "Test Suite: $Suite" -ForegroundColor White
Write-Host "Parallel Threads: $Threads" -ForegroundColor White
Write-Host ""

function Invoke-MavenTests {
    $mavenCommand = "mvn"
    
    if ($Clean) {
        $mavenCommand += " clean"
    }
    
    $mavenCommand += " test -Denv=$Environment -Dparallel.threads=$Threads"
    
    switch ($Suite) {
        "smoke" { $mavenCommand += " -Psmoke" }
        "regression" { $mavenCommand += " -Pregression" }
        "all" { }  # No additional profile needed
    }
    
    Write-Host "Executing: $mavenCommand" -ForegroundColor Yellow
    
    try {
        Invoke-Expression $mavenCommand
        return $LASTEXITCODE
    }
    catch {
        Write-Host "Error executing Maven command: $_" -ForegroundColor Red
        return 1
    }
}

function Invoke-DockerTests {
    Write-Host "Building Docker image..." -ForegroundColor Yellow
    
    try {
        docker build -t api-automation-tests .
        if ($LASTEXITCODE -ne 0) {
            Write-Host "Failed to build Docker image" -ForegroundColor Red
            return 1
        }
        
        $dockerCommand = "docker run --rm -e env=$Environment"
        $dockerCommand += " -v ${PWD}/test-output:/app/test-output"
        $dockerCommand += " -v ${PWD}/logs:/app/logs"
        
        # Add environment variables if they exist
        if ($env:API_TOKEN) {
            $dockerCommand += " -e API_TOKEN=$env:API_TOKEN"
        }
        if ($env:API_USERNAME) {
            $dockerCommand += " -e API_USERNAME=$env:API_USERNAME"
        }
        if ($env:API_PASSWORD) {
            $dockerCommand += " -e API_PASSWORD=$env:API_PASSWORD"
        }
        if ($env:API_KEY) {
            $dockerCommand += " -e API_KEY=$env:API_KEY"
        }
        
        $dockerCommand += " api-automation-tests mvn test -Denv=$Environment"
        
        switch ($Suite) {
            "smoke" { $dockerCommand += " -Psmoke" }
            "regression" { $dockerCommand += " -Pregression" }
        }
        
        Write-Host "Executing: $dockerCommand" -ForegroundColor Yellow
        Invoke-Expression $dockerCommand
        return $LASTEXITCODE
    }
    catch {
        Write-Host "Error executing Docker command: $_" -ForegroundColor Red
        return 1
    }
}

# Main execution
Write-Host "Starting test execution..." -ForegroundColor Blue

$testResult = 0

if ($Docker) {
    $testResult = Invoke-DockerTests
} else {
    $testResult = Invoke-MavenTests
}

# Check test results
if ($testResult -eq 0) {
    Write-Host "✅ Tests completed successfully!" -ForegroundColor Green
} else {
    Write-Host "❌ Tests failed!" -ForegroundColor Red
}

# Generate and open reports if requested
if ($Reports) {
    Write-Host "Generating reports..." -ForegroundColor Yellow
    
    try {
        # Generate Allure report
        mvn allure:report
        
        # Open reports
        $extentReports = Get-ChildItem -Path "test-output/extent-reports" -Filter "*.html" -ErrorAction SilentlyContinue
        if ($extentReports) {
            Write-Host "Opening Extent Report..." -ForegroundColor Green
            Start-Process $extentReports[0].FullName
        }
        
        Write-Host "Starting Allure server..." -ForegroundColor Green
        Start-Process -NoNewWindow -FilePath "mvn" -ArgumentList "allure:serve"
    }
    catch {
        Write-Host "Error generating reports: $_" -ForegroundColor Red
    }
}

Write-Host "Execution completed." -ForegroundColor Blue
exit $testResult
