@echo off
REM JDK 22-25 Feature Demo Runner Script for Windows
REM This script builds and runs the JDK 22-25 Feature Demo

setlocal enabledelayedexpansion

REM Function to display usage information
:show_usage
    echo JDK 22-25 Feature Demo Runner
    echo.
    echo Usage: run_demo.bat [OPTIONS]
    echo.
    echo Options:
    echo   --build              Build the project before running (default: false)
    echo   --concurrent         Run structured concurrency demos
    echo   --gather             Run stream gatherers demo
    echo   --scoped             Run scoped values demo
    echo   --primitive          Run primitive pattern switch demo
    echo   --native             Run Foreign Function & Memory API demo
    echo   --generate PATH      Generate a HelloWorld class file at the specified path
    echo   --generate-complex PATH  Generate a MathUtil class with multiple methods
    echo   --model              Run model class demos
    echo   --help               Show this help message
    echo.
    echo Examples:
    echo   run_demo.bat --build --concurrent    # Build and run concurrency demos
    echo   run_demo.bat --gather                # Run gatherers demo without building
    echo.
    exit /b 0

REM Default values
set BUILD=false
set DEMO_ARGS=

REM Parse command line arguments
:parse_args
    if "%~1"=="" goto check_args
    
    if "%~1"=="--build" (
        set BUILD=true
        shift
        goto parse_args
    )
    
    if "%~1"=="--help" (
        call :show_usage
        exit /b 0
    )
    
    if "%~1"=="--concurrent" (
        set DEMO_ARGS=%~1
        shift
        goto parse_args
    )
    
    if "%~1"=="--gather" (
        set DEMO_ARGS=%~1
        shift
        goto parse_args
    )
    
    if "%~1"=="--scoped" (
        set DEMO_ARGS=%~1
        shift
        goto parse_args
    )
    
    if "%~1"=="--primitive" (
        set DEMO_ARGS=%~1
        shift
        goto parse_args
    )
    
    if "%~1"=="--native" (
        set DEMO_ARGS=%~1
        shift
        goto parse_args
    )
    
    if "%~1"=="--model" (
        set DEMO_ARGS=%~1
        shift
        goto parse_args
    )
    
    if "%~1"=="--generate" (
        if "%~2"=="" (
            echo Error: --generate requires a path argument
            call :show_usage
            exit /b 1
        )
        set DEMO_ARGS=%~1 %~2
        shift
        shift
        goto parse_args
    )
    
    if "%~1"=="--generate-complex" (
        if "%~2"=="" (
            echo Error: --generate-complex requires a path argument
            call :show_usage
            exit /b 1
        )
        set DEMO_ARGS=%~1 %~2
        shift
        shift
        goto parse_args
    )
    
    echo Unknown option: %~1
    call :show_usage
    exit /b 1

:check_args
    REM Check if no demo arguments were provided
    if "%DEMO_ARGS%"=="" (
        echo Error: No demo option specified
        call :show_usage
        exit /b 1
    )

REM Check if Maven is installed
where mvn >nul 2>nul
if %ERRORLEVEL% neq 0 (
    echo Error: Maven is not installed or not in PATH
    exit /b 1
)

REM Check if Java is installed
where java >nul 2>nul
if %ERRORLEVEL% neq 0 (
    echo Error: Java is not installed or not in PATH
    exit /b 1
)

REM Check Java version (simplified for Windows)
for /f "tokens=3" %%g in ('java -version 2^>^&1 ^| findstr /i "version"') do (
    set JAVA_VERSION=%%g
)
set JAVA_VERSION=!JAVA_VERSION:"=!
set JAVA_VERSION=!JAVA_VERSION:.=!
set JAVA_VERSION=!JAVA_VERSION:~0,2!

if !JAVA_VERSION! LSS 25 (
    echo Warning: This demo requires JDK 25 or higher. Current version: !JAVA_VERSION!
    echo You may experience issues running the demos.
)

REM Build the project if requested
if "%BUILD%"=="true" (
    echo Building project...
    call mvn clean package
    if %ERRORLEVEL% neq 0 (
        echo Error: Build failed
        exit /b 1
    )
    echo Build successful
)

REM Check if the JAR file exists
set JAR_FILE=target\jdk22-25-demo-1.0-SNAPSHOT.jar
if not exist "%JAR_FILE%" (
    echo Error: JAR file not found: %JAR_FILE%
    echo Try running with --build option to build the project first
    exit /b 1
)

REM Run the demo
echo Running demo with arguments: %DEMO_ARGS%
java --enable-preview --enable-native-access=ALL-UNNAMED -jar "%JAR_FILE%" %DEMO_ARGS%

REM Exit with the status of the Java command
exit /b %ERRORLEVEL%