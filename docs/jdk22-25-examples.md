# JDK 22-25 Examples

This document highlights where to find sample code for features introduced between JDK 22 and JDK 25.

## JDK 22
- **Sequenced Collections** – `SequencedCollectionsDemo` in `src/main/java/ca/bazlur/`.
- **Statements Before super()** – `StatementsBeforeSuperDemo` in `src/main/java/ca/bazlur/`.
- **Vector API (Incubator)** – `VectorApiDemo` in `src/main/java/ca/bazlur/`.
- **Structured Concurrency** – progressive demos under `src/main/java/ca/bazlur/concurrency/`.
- **Scoped Values** – example in `src/main/java/ca/bazlur/scopevalue/ScopedValueDemo.java`.

## JDK 23
- **Stream Gatherers** – `GathererDemo` in `src/main/java/ca/bazlur/gatherers/`.
- **Record Patterns & Unnamed Patterns** – model classes and pattern demos in `src/main/java/ca/bazlur/model/`.

## JDK 24
- **Foreign Function & Memory API** – see native interop code in `native/` and `NativeLibraryDemo` in `src/main/java/ca/bazlur/`.

## JDK 25
- **Primitive Pattern Matching in Switch** – `PrimitivePatternDemo` in `src/main/java/ca/bazlur/`.
- **Dynamic Class Generation** – `GenerateHelloWorldClass` and `GenerateComplexClass` in `src/main/java/ca/bazlur/`.
- **Stable Values API** – `StableValuesDemo` in `src/main/java/ca/bazlur/stablevalue/`.

These examples can be run using the provided `run_demo.sh` script or by executing the packaged JAR with preview features enabled.
