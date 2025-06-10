#!/bin/bash

# JDK 22-25 Feature Demo Runner Script
# This script builds and runs the JDK 22-25 Feature Demo

# Function to display usage information
show_usage() {
    echo "JDK 22-25 Feature Demo Runner"
    echo ""
    echo "Usage: ./run_demo.sh [OPTIONS]"
    echo ""
    echo "Options:"
    echo "  --build              Build the project before running (default: false)"
    echo "  --concurrent         Run structured concurrency demos"
    echo "  --gather             Run stream gatherers demo"
    echo "  --scoped             Run scoped values demo"
    echo "  --primitive          Run primitive pattern switch demo"
    echo "  --native             Run Foreign Function & Memory API demo"
    echo "  --sequenced          Run sequenced collections demo"
    echo "  --presuper           Run statements-before-super demo"
    echo "  --vector             Run Vector API demo"
    echo "  --generate PATH      Generate a HelloWorld class file at the specified path"
    echo "  --generate-complex PATH  Generate a MathUtil class with multiple methods"
    echo "  --model              Run model class demos"
    echo "  --help               Show this help message"
    echo ""
    echo "Examples:"
    echo "  ./run_demo.sh --build --concurrent    # Build and run concurrency demos"
    echo "  ./run_demo.sh --gather                # Run gatherers demo without building"
    echo "  ./run_demo.sh --sequenced            # Run sequenced collections demo"
    echo "  ./run_demo.sh --presuper             # Run statements-before-super demo"
    echo "  ./run_demo.sh --vector               # Run Vector API demo"
    echo ""
}

# Default values
BUILD=false
DEMO_ARGS=""

# Parse command line arguments
while [[ $# -gt 0 ]]; do
    case $1 in
        --build)
            BUILD=true
            shift
            ;;
        --help)
            show_usage
            exit 0
            ;;
        --concurrent|--gather|--scoped|--primitive|--native|--model|--sequenced|--presuper|--vector)
            DEMO_ARGS="$1"
            shift
            ;;
        --generate|--generate-complex)
            if [[ $# -lt 2 ]]; then
                echo "Error: $1 requires a path argument"
                show_usage
                exit 1
            fi
            DEMO_ARGS="$1 $2"
            shift 2
            ;;
        *)
            echo "Unknown option: $1"
            show_usage
            exit 1
            ;;
    esac
done

# Check if no demo arguments were provided
if [[ -z "$DEMO_ARGS" ]]; then
    echo "Error: No demo option specified"
    show_usage
    exit 1
fi

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "Error: Maven is not installed or not in PATH"
    exit 1
fi

# Check if Java is installed and is the correct version
if ! command -v java &> /dev/null; then
    echo "Error: Java is not installed or not in PATH"
    exit 1
fi

JAVA_VERSION=$(java -version 2>&1 | head -1 | cut -d'"' -f2 | cut -d'.' -f1)
if [[ "$JAVA_VERSION" -lt 25 ]]; then
    echo "Warning: This demo requires JDK 25 or higher. Current version: $JAVA_VERSION"
    echo "You may experience issues running the demos."
fi

# Build the project if requested
if [[ "$BUILD" = true ]]; then
    echo "Building project..."
    if ! mvn clean package; then
        echo "Error: Build failed"
        exit 1
    fi
    echo "Build successful"
fi

# Check if the JAR file exists
JAR_FILE="target/jdk22-25-demo-1.0-SNAPSHOT.jar"
if [[ ! -f "$JAR_FILE" ]]; then
    echo "Error: JAR file not found: $JAR_FILE"
    echo "Try running with --build option to build the project first"
    exit 1
fi

# Run the demo
echo "Running demo with arguments: $DEMO_ARGS"
java --enable-preview --enable-native-access=ALL-UNNAMED --add-modules jdk.incubator.vector -jar "$JAR_FILE" $DEMO_ARGS

# Exit with the status of the Java command
exit $?