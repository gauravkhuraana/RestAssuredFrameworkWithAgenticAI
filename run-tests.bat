@echo off
:: API Test Execution Script for Windows
:: This script provides easy ways to run different test suites

echo 🚀 REST Assured API Automation Framework
echo ========================================

:: Default values
set ENVIRONMENT=dev
set SUITE=smoke
set THREADS=4
set CLEAN=false
set GENERATE_REPORTS=false
set USE_DOCKER=false

:: Parse command line arguments
:parse_args
if "%1"=="" goto execute_tests
if "%1"=="-e" (
    set ENVIRONMENT=%2
    shift
    shift
    goto parse_args
)
if "%1"=="--env" (
    set ENVIRONMENT=%2
    shift
    shift
    goto parse_args
)
if "%1"=="-s" (
    set SUITE=%2
    shift
    shift
    goto parse_args
)
if "%1"=="--suite" (
    set SUITE=%2
    shift
    shift
    goto parse_args
)
if "%1"=="-t" (
    set THREADS=%2
    shift
    shift
    goto parse_args
)
if "%1"=="--threads" (
    set THREADS=%2
    shift
    shift
    goto parse_args
)
if "%1"=="-c" (
    set CLEAN=true
    shift
    goto parse_args
)
if "%1"=="--clean" (
    set CLEAN=true
    shift
    goto parse_args
)
if "%1"=="-r" (
    set GENERATE_REPORTS=true
    shift
    goto parse_args
)
if "%1"=="--reports" (
    set GENERATE_REPORTS=true
    shift
    goto parse_args
)
if "%1"=="-d" (
    set USE_DOCKER=true
    shift
    goto parse_args
)
if "%1"=="--docker" (
    set USE_DOCKER=true
    shift
    goto parse_args
)
if "%1"=="-h" goto show_help
if "%1"=="--help" goto show_help

echo Unknown option: %1
goto show_help

:show_help
echo Usage: %0 [OPTIONS]
echo.
echo Options:
echo   -e, --env ENV          Environment to run tests against (dev, qa, prod)
echo   -s, --suite SUITE      Test suite to run (smoke, regression, all)
echo   -t, --threads NUM      Number of parallel threads (default: 4)
echo   -c, --clean            Clean before running tests
echo   -r, --reports          Generate and open reports after execution
echo   -d, --docker           Run tests in Docker container
echo   -h, --help             Show this help message
echo.
echo Examples:
echo   %0 -e qa -s smoke
echo   %0 --env prod --suite regression --threads 8
echo   %0 --docker --env dev --suite smoke
echo   %0 --clean --reports --env qa --suite all
goto :eof

:execute_tests
echo Configuration:
echo Environment: %ENVIRONMENT%
echo Test Suite: %SUITE%
echo Parallel Threads: %THREADS%
echo.

:: Validate environment
if not "%ENVIRONMENT%"=="dev" if not "%ENVIRONMENT%"=="qa" if not "%ENVIRONMENT%"=="prod" (
    echo Error: Invalid environment '%ENVIRONMENT%'. Must be dev, qa, or prod.
    exit /b 1
)

:: Validate suite
if not "%SUITE%"=="smoke" if not "%SUITE%"=="regression" if not "%SUITE%"=="all" (
    echo Error: Invalid test suite '%SUITE%'. Must be smoke, regression, or all.
    exit /b 1
)

echo Starting test execution...

if "%USE_DOCKER%"=="true" (
    goto run_docker_tests
) else (
    goto run_maven_tests
)

:run_maven_tests
set MAVEN_COMMAND=mvn

if "%CLEAN%"=="true" (
    set MAVEN_COMMAND=%MAVEN_COMMAND% clean
)

set MAVEN_COMMAND=%MAVEN_COMMAND% test -Denv=%ENVIRONMENT% -Dparallel.threads=%THREADS%

if "%SUITE%"=="smoke" (
    set MAVEN_COMMAND=%MAVEN_COMMAND% -Psmoke
)
if "%SUITE%"=="regression" (
    set MAVEN_COMMAND=%MAVEN_COMMAND% -Pregression
)

echo Executing: %MAVEN_COMMAND%
%MAVEN_COMMAND%
set TEST_RESULT=%ERRORLEVEL%
goto check_results

:run_docker_tests
echo Building Docker image...
docker build -t api-automation-tests .
if %ERRORLEVEL% neq 0 (
    echo Failed to build Docker image
    exit /b 1
)

set DOCKER_COMMAND=docker run --rm -e env=%ENVIRONMENT%
set DOCKER_COMMAND=%DOCKER_COMMAND% -v %cd%/test-output:/app/test-output
set DOCKER_COMMAND=%DOCKER_COMMAND% -v %cd%/logs:/app/logs

if defined API_TOKEN (
    set DOCKER_COMMAND=%DOCKER_COMMAND% -e API_TOKEN=%API_TOKEN%
)
if defined API_USERNAME (
    set DOCKER_COMMAND=%DOCKER_COMMAND% -e API_USERNAME=%API_USERNAME%
)
if defined API_PASSWORD (
    set DOCKER_COMMAND=%DOCKER_COMMAND% -e API_PASSWORD=%API_PASSWORD%
)
if defined API_KEY (
    set DOCKER_COMMAND=%DOCKER_COMMAND% -e API_KEY=%API_KEY%
)

set DOCKER_COMMAND=%DOCKER_COMMAND% api-automation-tests mvn test -Denv=%ENVIRONMENT%

if "%SUITE%"=="smoke" (
    set DOCKER_COMMAND=%DOCKER_COMMAND% -Psmoke
)
if "%SUITE%"=="regression" (
    set DOCKER_COMMAND=%DOCKER_COMMAND% -Pregression
)

echo Executing: %DOCKER_COMMAND%
%DOCKER_COMMAND%
set TEST_RESULT=%ERRORLEVEL%
goto check_results

:check_results
if %TEST_RESULT% equ 0 (
    echo ✅ Tests completed successfully!
) else (
    echo ❌ Tests failed!
)

if "%GENERATE_REPORTS%"=="true" (
    echo Generating reports...
    mvn allure:report
    
    echo Reports generated. Opening...
    start test-output\extent-reports\*.html
    echo Run 'mvn allure:serve' to view Allure report
)

echo Execution completed.
exit /b %TEST_RESULT%
