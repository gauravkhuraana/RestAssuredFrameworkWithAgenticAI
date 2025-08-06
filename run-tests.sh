#!/bin/bash

# API Test Execution Script
# This script provides easy ways to run different test suites

echo "🚀 REST Assured API Automation Framework"
echo "========================================"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Default values
ENVIRONMENT="dev"
SUITE="smoke"
THREADS="4"

# Function to display help
show_help() {
    echo "Usage: $0 [OPTIONS]"
    echo ""
    echo "Options:"
    echo "  -e, --env ENV          Environment to run tests against (dev, qa, prod)"
    echo "  -s, --suite SUITE      Test suite to run (smoke, regression, all)"
    echo "  -t, --threads NUM      Number of parallel threads (default: 4)"
    echo "  -c, --clean            Clean before running tests"
    echo "  -r, --reports          Generate and open reports after execution"
    echo "  -d, --docker           Run tests in Docker container"
    echo "  -h, --help             Show this help message"
    echo ""
    echo "Examples:"
    echo "  $0 -e qa -s smoke"
    echo "  $0 --env prod --suite regression --threads 8"
    echo "  $0 --docker --env dev --suite smoke"
    echo "  $0 --clean --reports --env qa --suite all"
}

# Parse command line arguments
while [[ $# -gt 0 ]]; do
    case $1 in
        -e|--env)
            ENVIRONMENT="$2"
            shift 2
            ;;
        -s|--suite)
            SUITE="$2"
            shift 2
            ;;
        -t|--threads)
            THREADS="$2"
            shift 2
            ;;
        -c|--clean)
            CLEAN=true
            shift
            ;;
        -r|--reports)
            GENERATE_REPORTS=true
            shift
            ;;
        -d|--docker)
            USE_DOCKER=true
            shift
            ;;
        -h|--help)
            show_help
            exit 0
            ;;
        *)
            echo "Unknown option: $1"
            show_help
            exit 1
            ;;
    esac
done

echo -e "${BLUE}Configuration:${NC}"
echo "Environment: $ENVIRONMENT"
echo "Test Suite: $SUITE"
echo "Parallel Threads: $THREADS"
echo ""

# Validate environment
if [[ ! "$ENVIRONMENT" =~ ^(dev|qa|prod)$ ]]; then
    echo -e "${RED}Error: Invalid environment '$ENVIRONMENT'. Must be dev, qa, or prod.${NC}"
    exit 1
fi

# Validate suite
if [[ ! "$SUITE" =~ ^(smoke|regression|all)$ ]]; then
    echo -e "${RED}Error: Invalid test suite '$SUITE'. Must be smoke, regression, or all.${NC}"
    exit 1
fi

# Function to run tests with Maven
run_maven_tests() {
    local maven_command="mvn"
    
    if [[ "$CLEAN" == "true" ]]; then
        maven_command="$maven_command clean"
    fi
    
    maven_command="$maven_command test -Denv=$ENVIRONMENT -Dparallel.threads=$THREADS"
    
    case $SUITE in
        "smoke")
            maven_command="$maven_command -Psmoke"
            ;;
        "regression")
            maven_command="$maven_command -Pregression"
            ;;
        "all")
            # No additional profile needed
            ;;
    esac
    
    echo -e "${YELLOW}Executing: $maven_command${NC}"
    eval $maven_command
    return $?
}

# Function to run tests in Docker
run_docker_tests() {
    echo -e "${YELLOW}Building Docker image...${NC}"
    docker build -t api-automation-tests . || {
        echo -e "${RED}Failed to build Docker image${NC}"
        exit 1
    }
    
    local docker_command="docker run --rm"
    docker_command="$docker_command -e env=$ENVIRONMENT"
    docker_command="$docker_command -v \$(pwd)/test-output:/app/test-output"
    docker_command="$docker_command -v \$(pwd)/logs:/app/logs"
    
    # Add environment variables if they exist
    if [[ -n "$API_TOKEN" ]]; then
        docker_command="$docker_command -e API_TOKEN=$API_TOKEN"
    fi
    if [[ -n "$API_USERNAME" ]]; then
        docker_command="$docker_command -e API_USERNAME=$API_USERNAME"
    fi
    if [[ -n "$API_PASSWORD" ]]; then
        docker_command="$docker_command -e API_PASSWORD=$API_PASSWORD"
    fi
    if [[ -n "$API_KEY" ]]; then
        docker_command="$docker_command -e API_KEY=$API_KEY"
    fi
    
    docker_command="$docker_command api-automation-tests mvn test -Denv=$ENVIRONMENT"
    
    case $SUITE in
        "smoke")
            docker_command="$docker_command -Psmoke"
            ;;
        "regression")
            docker_command="$docker_command -Pregression"
            ;;
    esac
    
    echo -e "${YELLOW}Executing: $docker_command${NC}"
    eval $docker_command
    return $?
}

# Main execution
echo -e "${BLUE}Starting test execution...${NC}"

if [[ "$USE_DOCKER" == "true" ]]; then
    run_docker_tests
    TEST_RESULT=$?
else
    run_maven_tests
    TEST_RESULT=$?
fi

# Check test results
if [[ $TEST_RESULT -eq 0 ]]; then
    echo -e "${GREEN}✅ Tests completed successfully!${NC}"
else
    echo -e "${RED}❌ Tests failed!${NC}"
fi

# Generate and open reports if requested
if [[ "$GENERATE_REPORTS" == "true" ]]; then
    echo -e "${YELLOW}Generating reports...${NC}"
    
    # Generate Allure report
    mvn allure:report
    
    # Open reports
    if command -v open &> /dev/null; then
        # macOS
        open test-output/extent-reports/*.html
        mvn allure:serve
    elif command -v xdg-open &> /dev/null; then
        # Linux
        xdg-open test-output/extent-reports/*.html
        mvn allure:serve
    elif command -v start &> /dev/null; then
        # Windows
        start test-output/extent-reports/*.html
        mvn allure:serve
    else
        echo -e "${YELLOW}Reports generated. Open manually:${NC}"
        echo "Extent Report: test-output/extent-reports/"
        echo "Allure Report: Run 'mvn allure:serve' to view"
    fi
fi

echo -e "${BLUE}Execution completed.${NC}"
exit $TEST_RESULT
