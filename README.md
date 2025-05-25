# JDK 22-25 Feature Demo

This project demonstrates various features introduced in JDK 25 Early Access builds. It serves as a showcase and learning resource for developers interested in exploring the latest Java language and API enhancements.

## Features Demonstrated

The project includes examples of the following JDK features:

### Structured Concurrency
- Joiner.awaitAllSuccessfulOrThrow() scope (DataFetcher)
- Joiner.awaitAll() scope (HeatMapBuilder)
- Deadline-capped (Joiner.anySuccessfulResultOrThrow() with config) scope (DeadlineDemo)
- FirstWin scope (Joiner.<String>anySuccessfulResultOrThrow()) (FirstWinDemo)
- Nested Scopes (NestedScopesDemo)

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

Run the demo application with:

```
java --enable-preview --enable-native-access=ALL-UNNAMED -jar target/jdk22-25-demo-1.0-SNAPSHOT.jar [COMMAND]
```

Available commands:

- `--concurrent`: Run structured concurrency demos
- `--gather`: Run stream gatherers demo
- `--scoped`: Run scoped values demo
- `--primitive`: Run primitive pattern switch demo
- `--native`: Run Foreign Function & Memory API demo
- `--generate PATH`: Generate a HelloWorld class file at the specified path
- `--generate-complex PATH`: Generate a MathUtil class with multiple methods
- `--model`: Run model class demos (record patterns, unnamed patterns, flexible constructor bodies)
- `--help`: Show help message

## Project Structure

- `src/main/java/ca/bazlur/`: Main Java source files
  - `concurrent/`: Structured concurrency examples
  - `gatherers/`: Stream gatherers examples
  - `model/`: Model classes for pattern matching demos
- `native/`: Native C code for Foreign Function & Memory API demos

## License

This project is available under the MIT License.