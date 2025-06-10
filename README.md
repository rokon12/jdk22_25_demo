# JDK 22-25 Feature Demo

This project demonstrates features introduced across JDK versions 22 through 25. It serves as a showcase and learning resource for developers interested in exploring the evolving Java language and API enhancements.

## Features Demonstrated

The project includes examples of the following JDK features:

### Structured Concurrency
- Progressive examples from traditional concurrency to advanced structured concurrency patterns:
  - A_ConferenceDemo: Main entry point showcasing all concurrency examples
  - B_TraditionalChaosDemo: Problems with traditional concurrent programming
  - C_FirstStructuredScopeDemo: Basic structured concurrency
  - D_RacingToWinDemo: The "Racing to Win" pattern for fastest response
  - E_AllOrNothingDemo: The "All or Nothing" pattern for distributed transactions
  - F_CustomJoinerDemo: Custom intelligence with smart joiners
  - G_DeadlineAwareDemo: Deadline-aware processing
  - H_NestedScopeDemo: Hierarchical nested scope architecture
- Additional specialized examples:
  - Z1_DataFetcher: Joiner.awaitAllSuccessfulOrThrow() scope
  - Z2_HeatMapBuilder: Joiner.awaitAll() scope
  - Z3_DeadlineDemo: Deadline-capped (Joiner.anySuccessfulResultOrThrow() with config) scope
  - Z4_FirstWinDemo: FirstWin scope (Joiner.<String>anySuccessfulResultOrThrow())
  - Z5_NestedScopesDemo: Complex nested scopes

### Stream Gatherers
- Examples of the new Stream Gatherers API for more powerful stream processing

### Scoped Values
- Demonstration of the ScopedValue API for thread-local data with virtual threads

### Pattern Matching
- Record patterns and unnamed patterns
- Primitive pattern matching in switch expressions

### Foreign Function & Memory API
- Working with custom structs and memory layouts
- Callback mechanisms (upcalls from native to Java)
- Asynchronous operations with native memory
- Complex memory layouts with nested structures and arrays

### Code Generation
- Dynamic generation of Java class files
- Simple HelloWorld class generation
- Complex class generation with multiple methods

### Other Features
- Flexible constructor bodies
- Model class demonstrations
- Sequenced collections examples
- Statements before super() example
- Vector API demo

## Requirements

- JDK 25 Early Access build
- Maven 3.8+

## Setup Instructions

1. Download and install JDK 25 Early Access build from [jdk.java.net](https://jdk.java.net/)
2. Set JAVA_HOME to point to your JDK 25 installation
3. Alternatively, use SDKMan to install your Java
4Clone this repository
5Build the project with Maven:
   ```
   mvn clean package
   ```

### Native Library Setup

For the Foreign Function & Memory API demos:

1. Navigate to the `native` directory
2. Compile the native library:
   ```
   clang -shared -O2 -o libcomplex.dylib main_geometry.c
   ```
   (Use appropriate compiler options for your platform)

## Usage

### Using the Run Script

The easiest way to run the demo is using the provided scripts:

#### For Unix/Linux/macOS:
```
./run_demo.sh [OPTIONS]
```

#### For Windows:
```
run_demo.bat [OPTIONS]
```

Options:
- `--build`: Build the project before running (default: false)
- `--concurrent`: Run structured concurrency demos
- `--gather`: Run stream gatherers demo
- `--scoped`: Run scoped values demo
- `--primitive`: Run primitive pattern switch demo
- `--native`: Run Foreign Function & Memory API demo
- `--sequenced`: Run sequenced collections demo
- `--stable`: Run stable values demo
- `--presuper`: Run statements-before-super demo
- `--vector`: Run Vector API demo
- `--generate PATH`: Generate a HelloWorld class file at the specified path
- `--generate-complex PATH`: Generate a MathUtil class with multiple methods
- `--model`: Run model class demos
- `--help`: Show this help message

Examples:
```
./run_demo.sh --build --concurrent    # Build and run concurrency demos
./run_demo.sh --gather                # Run gatherers demo without building
```

### Manual Execution

Alternatively, you can run the demo application directly with:

```
java --enable-preview --enable-native-access=ALL-UNNAMED -jar target/jdk22-25-demo-1.0-SNAPSHOT.jar [COMMAND]
```

Available commands:

- `--concurrent`: Run structured concurrency demos (A_ConferenceDemo with progressive examples)
- `--gather`: Run stream gatherers demo
- `--scoped`: Run scoped values demo
- `--primitive`: Run primitive pattern switch demo
- `--native`: Run Foreign Function & Memory API demo
- `--sequenced`: Run sequenced collections demo
- `--stable`: Run stable values demo
- `--presuper`: Run statements-before-super demo
- `--vector`: Run Vector API demo
- `--generate PATH`: Generate a HelloWorld class file at the specified path
- `--generate-complex PATH`: Generate a MathUtil class with multiple methods
- `--model`: Run model class demos (record patterns, unnamed patterns, flexible constructor bodies)
- `--help`: Show help message

## Project Structure

- `src/main/java/ca/bazlur/`: Main Java source files
  - `concurrency/`: Structured concurrency examples with alphabetical prefixes for logical flow
    - `A_ConferenceDemo.java`: Main entry point for the concurrency demo
    - `B_TraditionalChaosDemo.java` through `H_NestedScopeDemo.java`: Progressive concurrency examples
    - `Z1_DataFetcher.java` through `Z5_NestedScopesDemo.java`: Additional specialized examples
  - `gatherers/`: Stream gatherers examples
  - `model/`: Model classes for pattern matching demos
- `native/`: Native C code for Foreign Function & Memory API demos

Additional examples categorized by JDK version are listed in [`docs/jdk22-25-examples.md`](docs/jdk22-25-examples.md).

## License

This project is available under the MIT License.
